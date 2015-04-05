package ua.datalink.gstreamer;

import org.junit.Test;
import ua.datalink.gstreamer.server.Server;
import ua.datalink.gstreamer.utils.FLV.FLVUtil;

import static junit.framework.Assert.assertEquals;

/**
 * Created by dv on 03.04.15.
 */
public class StreamUtilsTest {

    @Test
    public void readBufferTest(){

    }

    @Test
    public void sizeBufferToIntTest(){
        byte[] buffer = new byte[3];
        buffer[0] = 0;
        buffer[1] = 9;
        buffer[2] = -44;
        assertEquals(2516, FLVUtil.sizeBufferToInt(buffer));
    }
}
