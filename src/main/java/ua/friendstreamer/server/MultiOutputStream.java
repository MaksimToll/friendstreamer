package ua.friendstreamer.server;

import org.apache.log4j.Logger;

import java.io.IOException;
import java.io.OutputStream;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by dv on 30.03.15.
 */
public class MultiOutputStream extends OutputStream {
    private CopyOnWriteArrayList<OutputStream> streams;
    public volatile AtomicInteger count = new AtomicInteger(0);
    private static final Logger logger = Logger.getLogger(MultiOutputStream.class);

    public MultiOutputStream(CopyOnWriteArrayList<OutputStream> streams) {
        this.streams = streams;
    }

    public MultiOutputStream() {
        streams = new CopyOnWriteArrayList<OutputStream>();
    }

    @Override
    public void write(int b) throws IOException {
        for(int i = 0; i < streams.size(); i++){
            try {
                streams.get(i).write(b);
            }catch (IOException ioe){
                streams.remove(i);
                count.decrementAndGet();
                logger.info("Client disconnected! Actual clients count " + count.get());
            }

        }
    }

    @Override
    public void write(byte b[]) throws IOException {
        for(int i = 0; i < streams.size(); i++){
            try {
                streams.get(i).write(b);
            } catch (IOException ioe) {
                streams.remove(i);
                count.decrementAndGet();
                logger.info("Client disconnected!");
            }
        }
    }

    @Override
    public void write(byte b[], int off, int len) throws IOException {
        for(int i = 0; i < streams.size(); i++){
            try {
                streams.get(i).write(b, off, len);
            } catch (IOException ioe) {
                streams.remove(i);
                count.decrementAndGet();
                logger.info("Client disconnected!");
            }
        }
    }

    @Override
    public void flush() throws IOException {
        for(int i = 0; i < streams.size(); i++){
            try {
                streams.get(i).flush();
            } catch (IOException ioe) {
                streams.remove(i);
                count.decrementAndGet();
                logger.info("Client disconnected!");
            }

        }
    }

    @Override
    public void close() throws IOException {
        for(int i = 0; i < streams.size(); i++){
            streams.get(i).close();
        }
    }

    public void addStream(OutputStream stream) {
        streams.add(stream);
        count.incrementAndGet();
    }
}
