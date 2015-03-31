package ua.datalink.gstreamer;


import java.io.OutputStream;

public interface ConnectionInterface {
    void onConnect(OutputStream out);
    void onDisconnect();
}
