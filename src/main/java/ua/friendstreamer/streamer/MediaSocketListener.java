package ua.friendstreamer.streamer;


import org.apache.log4j.Logger;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class MediaSocketListener implements Runnable{
    private ServerSocket serverSocket = null;
    private ConnectionInterface connectionInterface;
    private OutputStream outputStream;
    private static final Logger logger = Logger.getLogger(MediaSocketListener.class);

    public MediaSocketListener(int port, ConnectionInterface connectionInterface) throws IOException {
        if(port < 0 || port > 65535){
            throw new IllegalArgumentException("Port can be in range 0..65535");
        }
        this.connectionInterface = connectionInterface;
        serverSocket = new ServerSocket(port);

    }

    @Override
    public void run() {
        Socket client = null;
        try {
            client = serverSocket.accept();
            new Thread(new ConnectionProcessor(connectionInterface, client)).start();
        } catch (IOException e) {
            logger.error(e.getMessage());
            logger.debug(e.getMessage(), e);
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

        private void init(Socket client) throws IOException {
            in = new BufferedReader(new InputStreamReader(client.getInputStream()));
            out = new PrintWriter(client.getOutputStream(), true);
        }

        //todo process headers
        private void readHeader() throws IOException {
            StringBuilder builder = new StringBuilder();
            String line = " ";
            while (! line.equals("")){
                line = in.readLine();
                builder.append(line);
            }
        }

        private void sendResponse(){
            out.print("HTTP/1.1 200 OK\r\n\r\n");
            out.flush();
        }

        @Override
        public void run() {
            try {
                logger.info("Client " + client.getInetAddress() + " connected");
                init(client);
                readHeader();
                sendResponse();
                outputStream = client.getOutputStream();
                connectCallback.onConnect(outputStream);
            }catch (Exception e){
                logger.error(e.getMessage());
                logger.debug(e.getMessage(), e);
            }

        }
    }
}
