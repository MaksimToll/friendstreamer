package ua.datalink.gstreamer.streamer;


import java.io.OutputStream;

public interface ConnectionInterface {
    void onConnect(OutputStream out);
    void onDisconnect();
}
