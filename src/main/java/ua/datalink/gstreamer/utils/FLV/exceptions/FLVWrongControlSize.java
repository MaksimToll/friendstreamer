package ua.datalink.gstreamer.utils.FLV.exceptions;

/**
 * Created by dv on 04.04.15.
 */
public class FLVWrongControlSize extends FLVException {
    public FLVWrongControlSize() {
        super("Size of tag in header not equal control size of tag!");
    }
}
