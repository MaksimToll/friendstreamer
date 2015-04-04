package ua.datalink.gstreamer.streamer;


import org.apache.log4j.Logger;
import org.gstreamer.*;
import org.gstreamer.elements.good.RTSPSrc;
import org.gstreamer.io.OutputStreamSink;

import java.io.IOException;
import java.io.OutputStream;

public class RTSP_TO_HTTP {
    public static final String RTSP_SRC_NAME = "RTSP_SOURCE";

    public static final String DEFAULT_RTP_DEPAY_CLASS = "rtph264depay";
    public static final String RTP_DEPAY_NAME = "RTP_DEPAY";

    public static final String DEFAULT_DECODER_CLASS = "ffdec_h264";
    public static final String DECODER_NAME = "decoder";
    public static final String DEFAULT_ENCODER_CLASS = "x264enc";
    public static final String ENCODER_NAME = "encoder";
    public static final String DEFAULT_MUXER_CLASS = "flvmux";
    public static final String MUXER_NAME = "muxer";

    public static final int DEFAULT_PROTOCOL = 4;//TCP
    public static final int DEFAULT_PORT = 6666;

    private static final Logger logger = Logger.getLogger(RTSP_TO_HTTP.class);

    private String sourceLocation;
    private int protocol;
    private int port;

    private Pipeline pipeline;
    private Element rtspSource;
    private org.gstreamer.Element rtpDepay;
    private org.gstreamer.Element decoder;
    private Element encoder;
    private Element muxer;
    OutputStreamSink outputStreamSink;

    public RTSP_TO_HTTP() {
    }

    public RTSP_TO_HTTP(String sourceLocation) {
        this(sourceLocation, DEFAULT_PROTOCOL);
    }

    public RTSP_TO_HTTP(String sourceLocation, int protocol) {
        this(sourceLocation, protocol, DEFAULT_PORT);
    }

    public RTSP_TO_HTTP(String sourceLocation, int protocol, int port) {
        this.sourceLocation = sourceLocation;
        this.protocol = protocol;
        this.port = port;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public int getProtocol() {
        return protocol;
    }

    public void setProtocol(int protocol) {
        this.protocol = protocol;
    }

    public String getSourceLocation() {
        return sourceLocation;
    }

    public void setSourceLocation(String sourceLocation) {
        this.sourceLocation = sourceLocation;
    }

    public boolean init(){
        if(sourceLocation == null){
            throw new RuntimeException("RTSP resource must be specified");
        }
        logger.debug("Initialising GStreamer.");
        Gst.init();
        String gstVersion = Gst.getVersionString();
        logger.info(gstVersion + " was initialized");

        createElements();

        linkElements();


        return false;
    }


    /**
     * Creating base pipeline elements
     */
    private void createElements(){
        logger.debug("Creating pipeline elements");
       // mainBin = new Bin(MAIN_BIN_NAME);
        pipeline = new Pipeline();
        //Add GStreamer message to default logger;
        Bus bus = pipeline.getBus();
        bus.connect(new Bus.MESSAGE() {
            public void busMessage(Bus bus, Message message) {
                String formatedMessage = message.getSource().getName() + "----" + message.getStructure().toString();
                switch (message.getType().intValue()){
                    case 2: logger.error(formatedMessage);
                        break;
                    case 4: logger.warn(formatedMessage);
                        break;
                    case 8: logger.info(formatedMessage);
                        break;
                    default:logger.debug(formatedMessage);
                }
            }
        });

        try {
            rtspSource = new RTSPSrc(RTSP_SRC_NAME);
            rtpDepay = ElementFactory.make(DEFAULT_RTP_DEPAY_CLASS, RTP_DEPAY_NAME);
            decoder = ElementFactory.make(DEFAULT_DECODER_CLASS, DECODER_NAME);
            encoder = ElementFactory.make(DEFAULT_ENCODER_CLASS, ENCODER_NAME);
            muxer = ElementFactory.make(DEFAULT_MUXER_CLASS, MUXER_NAME);
            pipeline.addMany(rtspSource, rtpDepay, decoder, encoder, muxer);



            rtspSource.set("location", sourceLocation);
            rtspSource.set("protocols", protocol);
        }catch (IllegalArgumentException iae){
            logger.error("Can't create pipeline elements. " + iae.getMessage() + ". Maybe some GStreamer plugin not installed");
            throw iae;
        }
    }

    /**
     * Connecting elements into chain
     */
    private void linkElements(){
        logger.debug("Linking elements in pipeline");

        //Adding handler for onPAD_ADDED
        //RTSP source has no PADs on creating, its adding on open rtsp nd read metadata
        //so we can't connect source with nex pipeline element before
        rtspSource.connect(new Element.PAD_ADDED() {
            @Override
            public void padAdded(Element element, Pad pad) {
                if(! element.link(rtpDepay)){
                    logger.error("Can't linc RTSP source with RTP depay.");
                    throw new RuntimeException();
                }
            }
        });

        if(! rtpDepay.link(muxer)){
            logger.error("Can't link RTPDepay to decoder");
            throw new RuntimeException();
        }

     /*   if(! decoder.link(encoder)){
            logger.error("Can't link decoder to encoder");
            throw new RuntimeException();
        }

        if(! encoder.link(muxer)){
            logger.error("Can't link encoder to muxer");
            throw new RuntimeException();
        }*/

    }

    public void listenPort(){
        listenPort(DEFAULT_PORT);
    }

    public void listenPort(int port){
        logger.info("Start listening on port " + port);
        try {
            MediaSocketListener listener = new MediaSocketListener(port, new ConnectHandler());
            new Thread(listener).start();
        } catch (IOException e) {
            logger.error("Can't connect port " + port + ". Port maybe used.");
        }
    }

    private class ConnectHandler implements ConnectionInterface {

        private int connectionsCount = 0;

        @Override
        public void onConnect(OutputStream out) {
            if(! pipeline.getSinks().contains(outputStreamSink)){
                outputStreamSink = new OutputStreamSink(out, "OutputVideoStream");
                pipeline.add(outputStreamSink);

                if(! muxer.link(outputStreamSink)){
                    logger.error("Can't connect muxer to outputStream");
                    throw new RuntimeException();
                }
            }
            if(connectionsCount++ == 0){
                pipeline.play();
            }
        }

        @Override
        public void onDisconnect() {
            if(--connectionsCount == 0){
                pipeline.stop();
            }
        }
    }
    }
