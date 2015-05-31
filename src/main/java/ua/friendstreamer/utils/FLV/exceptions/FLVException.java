package ua.friendstreamer.utils.FLV.exceptions;

/**
 * Represent bad FLV data
 */
public class FLVException extends RuntimeException {
    public FLVException() {
        super("Exception while processing FLV stream");
    }

    public FLVException(String message) {
        super(message);
    }
}
