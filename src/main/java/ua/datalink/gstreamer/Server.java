package ua.datalink.gstreamer;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;

/**
 * Created by dv on 31.03.15.
 */
public class Server {
    private ServerSocket serverSocket;
    private Socket inSoket;
    private MultiOutputStream outputStream = new MultiOutputStream();

    public Server(){
        try {
            serverSocket = new ServerSocket(6666);
            new ConnectionsBinder(outputStream, serverSocket).start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void connectDataSource(){
        try {
            inSoket = new Socket("localhost", 4322);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sendRequest(){
        try {
            PrintWriter writer = new PrintWriter(inSoket.getOutputStream());
            writer.println("GET");
            writer.println("");
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void readResponse(){
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(inSoket.getInputStream()));
            System.out.println(reader.readLine());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void readData(){
        ByteBuffer byteBuffer = ByteBuffer.allocate(10240);
        byteBuffer.mark();
        byte[] tmpBuffer = new byte[1024];

        try {
            InputStream reader = inSoket.getInputStream();
            while (true) {
                for (int i = 0; i < 10; i++) {
                    int c = reader.read(tmpBuffer, 0, 1024);
                    if(outputStream.count.get() > 0) {
                        outputStream.write(tmpBuffer, 0, c);
                    }
                    //System.out.println(tmpBuffer.toString());
                    byteBuffer.put(tmpBuffer);
                }
                byteBuffer.reset();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        Server server = new Server();
        server.connectDataSource();
        server.sendRequest();
        server.readResponse();
        server.readData();
    }
}
