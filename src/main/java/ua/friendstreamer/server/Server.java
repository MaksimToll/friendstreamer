package ua.friendstreamer.server;

import org.apache.log4j.Logger;
import ua.friendstreamer.utils.FLV.FLVUtil;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;

/**
 * Created by dv on 31.03.15.
 */
public class Server {
    private ServerSocket serverSocket;
    private Socket streamSocket;
    private InputStream mediaStream;
    private MultiOutputStream outputStream = new MultiOutputStream();
    private byte[] header;

    Logger logger = Logger.getLogger(Server.class);

    public Server(){
        try {
            serverSocket = new ServerSocket(11111);
            connectDataSource("localhost", 4322);
            prepareHeader();
            new ConnectionsBinder(outputStream, serverSocket, header).start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void start(){
        try {
            logger.info("Start receiving stream data.");
            while (true) {
                byte[] tag = FLVUtil.readNexTag(mediaStream);
                outputStream.write(tag);
                Thread.yield();
            }
        } catch (IOException e) {
            logger.error(e.getMessage());
            logger.debug(e);
        }
    }

    /**
     * Connect to mediaStreamSource on specified hast and port
     * @param host
     * @param port
     */
    private void connectDataSource(String host, int port){
        try {
            logger.info("Try to connect to stream source");
            streamSocket = new Socket(host, port);
            mediaStream = streamSocket.getInputStream();
            sendRequest();
            readResponse();
        } catch (IOException e) {
            logger.error("Error occurred during connecting");
            logger.debug(e.getMessage(), e);
        }
    }

    /**
     * Send request to data source
     * todo make request more specific(many streams on one port)
     */
    private void sendRequest() throws IOException {
            PrintWriter writer = new PrintWriter(streamSocket.getOutputStream());
            writer.println("GET");
            writer.println("");
            writer.flush();
    }

    /**
     * Read response from data source
     * todo process diferent response
     */
    private void readResponse() throws IOException {
            BufferedReader reader = new BufferedReader(new InputStreamReader(streamSocket.getInputStream()));
            System.out.println(reader.readLine());
    }

    private void prepareHeader() throws IOException {
        logger.info("Preparing data header");
        logger.debug("Reading stream header..");
        ByteBuffer fullHeader = ByteBuffer.allocate(200240);
        fullHeader.mark();
        int count = 0;

        byte[] header = FLVUtil.readFLVStreamHeader(mediaStream);
        fullHeader.put(header);
        count += header.length;

        logger.debug("Reading script data...");
        byte[] tag = FLVUtil.readNexTag(mediaStream);
        fullHeader.put(tag);
        count += tag.length;

        logger.debug("Reading first(with metadata) video tag...");
        tag = FLVUtil.readNexTag(mediaStream);
        fullHeader.put(tag);
        count += tag.length;

        fullHeader.reset();
        this.header = new byte[count];
        fullHeader.get(this.header, 0, count);
        logger.debug("Full header was prepared.");
    }

}