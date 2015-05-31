package ua.friendstreamer.utils.FLV;

import ua.friendstreamer.utils.FLV.exceptions.FLVUnknownTagTypeException;
import ua.friendstreamer.utils.FLV.exceptions.FLVWrongControlSize;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

import static ua.friendstreamer.utils.stream.StreamUtil.readFullBuffer;

/**
 * Created by dv on 04.04.15.
 */
public class FLVUtil {

    public static final int TAG_HEADER_SIZE = 4;
    public static final int TAG_METADATA_SIZE = 11;

    /**
     * Read header of FLV stream and put it to buffer
     *
     * @param stream from where read data
     * @return byte array with header data
     * @throws java.io.IOException
     */
    public static byte[] readFLVStreamHeader(InputStream stream) throws IOException {
        byte[] header = new byte[13];
        readFullBuffer(stream, header, 13);
        return header;
    }

    /**
     * Read nex FLV tag from stream
     * @param stream
     * @return byte array with full tag data
     * @throws IOException
     */
    public static byte[] readNexTag(InputStream stream) throws IOException {
        byte[] tagTypeBuffer = new byte[1];
        readTagType(stream, tagTypeBuffer);

        byte[] tagSizeBuffer = new byte[3];
        int tagSize = readTagSize(stream, tagSizeBuffer);

        byte[] tagBuffer = new byte[tagSize + TAG_HEADER_SIZE + TAG_METADATA_SIZE];
        tagBuffer[0] = tagTypeBuffer[0];
        tagBuffer[1] = tagSizeBuffer[0];
        tagBuffer[2] = tagSizeBuffer[1];
        tagBuffer[3] = tagSizeBuffer[2];
        readFullBuffer(stream, tagBuffer, TAG_HEADER_SIZE, tagSize+TAG_METADATA_SIZE);

        chekControlSize(tagBuffer, tagSize);
        return tagBuffer;
    }

    /**
     * Reset timestamp in given tag
     * Timestamp is presented in 4-bytes from 4 to 7
     * @param tagBuffer
     */
    public static void resetTimestamp(byte[] tagBuffer){
        tagBuffer[4] = 0;
        tagBuffer[5] = 0;
        tagBuffer[6] = 0;
        tagBuffer[7] = 0;
    }

    /**
     * @param tagBuffer
     * @return tag type of given tag buffer
     */
    public static TagType getTagType(byte[] tagBuffer){
        switch (tagBuffer[0]){
            case 8 : return TagType.AUDIO;
            case 9 : return TagType.VIDEO;
            case 18: return TagType.SCRIPT_DATA;
            default: throw new FLVUnknownTagTypeException();
        }
    }

    /**
     * @param tagBuffer
     * @param size
     * @throws FLVUnknownTagTypeException if tads is unequal
     */
    public static void chekControlSize(byte[] tagBuffer, int size) throws FLVUnknownTagTypeException{
        byte[] controlSizeBuffer =  new byte[3];
        for(int i = 0; i < 3; i++){
            controlSizeBuffer[i] = tagBuffer[tagBuffer.length-(3-i)];
        }
        int controlSize = sizeBufferToInt(controlSizeBuffer);
        if((controlSize-TAG_METADATA_SIZE) != size){
            throw new FLVWrongControlSize();
        }
    }

    /**
     * Read tag type for even tag
     * Tag type is presented by 1 byte
     *
     * @param stream
     * @return
     * @throws IOException
     */
    public static void readTagType(InputStream stream, byte[] buffer) throws IOException {
        readFullBuffer(stream, buffer, 1);
    }

    /**
     * Read tag size of even tag
     * Tag size is presented by 3-byte value
     *
     * @param stream
     * @param buffer
     * @return
     * @throws IOException
     */
    public static int readTagSize(InputStream stream, byte[] buffer) throws IOException {
        readFullBuffer(stream, buffer, 3);
        return sizeBufferToInt(buffer);
    }

    /**
     * Convert 3-byte number value to int
     *
     * @param buffer 3 byte array with size data
     * @return converted array value of size
     */
    public static int sizeBufferToInt(byte[] buffer){
        ByteBuffer byteBuffer = ByteBuffer.allocate(4);
        byteBuffer.put((byte)0);
        byteBuffer.put(buffer);
        return byteBuffer.getInt(0);
    }
}
