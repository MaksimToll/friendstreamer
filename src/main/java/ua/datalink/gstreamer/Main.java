package ua.datalink.gstreamer;

import ua.datalink.gstreamer.server.Server;
import ua.datalink.gstreamer.streamer.RTSP_TO_HTTP;

/**
 * Created by dv on 31.03.15.
 */
public class Main {
    public static void main(String[] args) {
        RTSP_TO_HTTP media = new RTSP_TO_HTTP("rtsp://109.95.48.77:1218/user=admin&password=2117&channel=1&stream=1.sdp");
        media.init();
        media.listenPort(4322);
        Server server = new Server();
        server.start();
    }
}
