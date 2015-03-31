package ua.datalink.gstreamer;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by dv on 30.03.15.
 */
public class MultiOutputStream extends OutputStream {
    private List<OutputStream> streams;

    public MultiOutputStream(List<OutputStream> streams) {
        this.streams = streams;
    }

    public MultiOutputStream() {
        streams = new ArrayList<OutputStream>(1);
    }

    @Override
    public synchronized void write(int b) throws IOException {
        for(OutputStream stream : streams){
            stream.write(b);
        }
    }

    @Override
    public synchronized void write(byte b[]) throws IOException {
        for (OutputStream stream : streams){
            stream.write(b);
        }
    }

    @Override
    public synchronized void write(byte b[], int off, int len) throws IOException{
        for (OutputStream stream : streams){
            stream.write(b, off, len);
        }
    }

    @Override
    public synchronized void flush() throws IOException{
        for (OutputStream stream : streams){
            stream.flush();
        }
    }

    @Override
    public synchronized void close() throws IOException{
        for (OutputStream stream : streams){
            stream.close();
        }
    }

    public synchronized void addStream(OutputStream stream){
        this.streams.add(stream);
    }
}
