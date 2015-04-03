package ua.datalink.gstreamer;


import org.gstreamer.Element;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MediaSocketListener implements Runnable{
    private ServerSocket serverSocket = null;
    private ConnectionInterface connectionInterface;
    private MultiOutputStream outputStream;
    private ConnectionProcessor connectionProcessor;

    public MediaSocketListener(int port, ConnectionInterface connectionInterface) throws IOException {
        if(port < 0 || port > 65535){
            throw new IllegalArgumentException("Port can be in range 0..65535");
        }
        this.connectionInterface = connectionInterface;
        outputStream = new MultiOutputStream();
        serverSocket = new ServerSocket(port);

    }

    @Override
    public void run() {
        while (true){
            Socket client = null;
            try {
                client = serverSocket.accept();
                new Thread(new ConnectionProcessor(connectionInterface, client)).start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    private class ConnectionProcessor implements Runnable{
        private BufferedReader in = null;
        private PrintWriter out = null;
        ConnectionInterface connectCallback;
        Socket client;

        public ConnectionProcessor(ConnectionInterface callback, Socket client) {
            this.connectCallback = callback;
            this.client = client;
        }

        public void newConnection(ConnectionInterface callback, Socket client) throws IOException {
            connectCallback = callback;
            init(client);
            readHeader();
            sendResponce();
            outputStream.addStream(client.getOutputStream());
            connectCallback.onConnect(outputStream);
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
        }

        private void sendResponce(){
            out.print("HTTP/1.1 200 OK\r\n\r\n");
            out.flush();
        }


        @Override
        public void run() {
            try {
                init(client);
                readHeader();
                sendResponce();
                outputStream.addStream(client.getOutputStream());
                connectCallback.onConnect(outputStream);
            }catch (Exception e){
                e.printStackTrace();
            }

        }
    }
}
