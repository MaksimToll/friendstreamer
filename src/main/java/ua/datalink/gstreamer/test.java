package ua.datalink.gstreamer;

import org.gstreamer.io.OutputStreamSink;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Created by dv on 03.04.15.
 */
public class test {
    public static void main(String[] args) throws IOException {
        Socket socket = new Socket("localhost", 6666);
        InputStream stream = socket.getInputStream();
        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
        out.write("GET\r\n\r\n");
        out.flush();
        byte[] buf = new byte[1024];
        stream.read(buf, 0, 1024);
        System.out.println(buf);
    }
}
