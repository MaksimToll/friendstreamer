package ua.datalink.gstreamer.server;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;

/**
 * Created by dv on 31.03.15.
 */
public class Server {
    private ServerSocket serverSocket;
    private Socket inSoket;
    private InputStream stream;
    private MultiOutputStream outputStream = new MultiOutputStream();
    private ByteBuffer headerBuffer;
    private byte[] header;

    public Server(){
        try {
            serverSocket = new ServerSocket(6666);
            connectDataSource();
            sendRequest();
            readResponse();
            stream = inSoket.getInputStream();
            prepareHeader();
            new ConnectionsBinder(outputStream, serverSocket, header).start();
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
        try {
            while (true) {
                byte[] tag = readNexTag(stream);
               // System.out.println(tag[11] + " - " + tag[12] + " - " + tag[13]);
                tag[4] = 0;
                tag[5] = 0;
                tag[6] = 0;
                tag[7] = 0;
                if(outputStream.count.get() > 0 &&getTagType(tag) == TagType.VIDEO){
                    outputStream.write(tag);
                }

            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public byte[] getHeader() {
        return header;
    }

    /**
     * Read tag type for even tag
     * @param stream
     * @return
     * @throws IOException
     */
    private byte readTagType(InputStream stream) throws IOException {
        byte[] tagType = new byte[1];
        int count = 0;
        do{
            count = stream.read(tagType, 0, 1);
        }while (count != 1);
        return tagType[0];
    }

    private int readTagSize(InputStream stream, byte[] buffer) throws IOException {
        readAllBytes(stream, buffer, 3);
        return sizeBufferToInt(buffer);
    }

    public static int sizeBufferToInt(byte[] buffer){
        ByteBuffer byteBuffer = ByteBuffer.allocate(4);
        byteBuffer.put((byte)0);
        byteBuffer.put(buffer);
        return byteBuffer.getInt(0);
    }

    private byte[] readNexTag(InputStream stream) throws IOException {
        byte[] tagTypeBuffer = new byte[1];
        tagTypeBuffer[0] = readTagType(stream);
        byte[] tagSizeBuffer = new byte[3];
        int tagSize = readTagSize(stream, tagSizeBuffer);
        byte[] tagBuffer = new byte[tagSize + 4 + 11];
        tagBuffer[0] = tagTypeBuffer[0];
        tagBuffer[1] = tagSizeBuffer[0];
        tagBuffer[2] = tagSizeBuffer[1];
        tagBuffer[3] = tagSizeBuffer[2];
        readAllBytes(stream, tagBuffer, 4, tagSize+11);
        return tagBuffer;
    }
    /**
     *
     * @param stream
     * @return next tag type
     * @throws IOException
     */
    private byte[] readHeader(InputStream stream) throws IOException {
        byte[] header = new byte[13];
        readAllBytes(stream, header, 13);
        return header;
    }

    /**
     * @param stream from where read data
     * @param buffer to where write data
     * @param length of data that must be read
     * @throws IOException
     */
    public static void readAllBytes(InputStream stream, byte[] buffer, int length) throws IOException {
        readAllBytes(stream, buffer, 0, length);
    }

    public static void readAllBytes(InputStream stream, byte[] buffer,int offset, int length) throws IOException {
        int totalCount = 0;
        int lastLength = length;
        int pos = offset;
        do {
            int count = stream.read(buffer, pos, lastLength);
            pos += count;
            lastLength -= count;
            totalCount += count;
        }while (totalCount != length);
    }

    public static TagType getTagType(byte[] tagBuffer){
        switch (tagBuffer[0]){
            case 8 : return TagType.AUDIO;
            case 9 : return TagType.VIDEO;
            case 18: return TagType.SCRIPT_DATA;
            default: throw new RuntimeException("Unknown tag type.");
        }
    }

    private void prepareHeader() throws IOException {
        ByteBuffer fullHeader = ByteBuffer.allocate(10240);
        fullHeader.mark();
        int count = 0;
        byte[] header = readHeader(stream);
        fullHeader.put(header);
        count += header.length;
        byte[] tag = readNexTag(stream);
        fullHeader.put(tag);
        count += tag.length;
        tag = readNexTag(stream);
        fullHeader.put(tag);
        fullHeader.reset();
        count += tag.length;
        this.header = new byte[count];
        fullHeader.get(this.header, 0, count);
    }

    public static void main(String[] args) {
        Server server = new Server();
        server.readData();
    }
}