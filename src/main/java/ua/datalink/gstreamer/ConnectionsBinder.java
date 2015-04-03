package ua.datalink.gstreamer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by dv on 31.03.15.
 */
public class ConnectionsBinder extends Thread {
    private MultiOutputStream outputStream;
    private ServerSocket serverSocket;
    private BufferedReader in = null;
    private PrintWriter out = null;

    public ConnectionsBinder(MultiOutputStream outputStream, ServerSocket serverSocket) {
        super();
        this.outputStream = outputStream;
        this.serverSocket = serverSocket;
    }

    @Override
    public void run(){
        while (true){
            try {
                Socket client = serverSocket.accept();
                init(client);
                readHeader();
                sendResponce();
                outputStream.addStream(client.getOutputStream());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void init(Socket client) throws IOException {
        in = new BufferedReader(new InputStreamReader(client.getInputStream()));
        out = new PrintWriter(client.getOutputStream(), true);
    }

    private void readHeader() throws IOException {
        StringBuilder builder = new StringBuilder();
        String line = " ";
        while (! line.equals("")){
            line = in.readLine();
            builder.append(line);
        }
        System.out.println(builder.toString());
    }

    private void sendResponce(){
        out.print("HTTP/1.1 200 OK\r\n\r\n");
        out.flush();
    }
}
