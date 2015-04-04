package ua.datalink.gstreamer.utils.stream;

import java.io.IOException;
import java.io.InputStream;

/**
 *
 */
public class StreamUtil {

    /**
     * Read all specified data, no more no less
     *
     * @param stream from where read data
     * @param buffer to where write data
     * @param length of data that must be read
     * @throws IOException if can't read from stream
     */
    public static void readFullBuffer(InputStream stream, byte[] buffer, int length) throws IOException {
        readFullBuffer(stream, buffer, 0, length);
    }

    /**
     * Read all specified data, no more no less
     *
     * @param stream from where read data
     * @param buffer to where write data
     * @param offset start position in buffer
     * @param length of data that must be read
     * @throws IOException if can't read from stream
     */
    public static void readFullBuffer(InputStream stream, byte[] buffer,int offset, int length) throws IOException {
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
}
