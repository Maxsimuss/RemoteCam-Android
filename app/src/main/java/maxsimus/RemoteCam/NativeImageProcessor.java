package maxsimus.RemoteCam;

import java.nio.MappedByteBuffer;

public class NativeImageProcessor implements AutoCloseable {
    static {
        System.loadLibrary("RemoteCam");
    }

    private final long ptr;

    public NativeImageProcessor(int address) {
        ptr = nativeInit(address);
    }

    public void process(MappedByteBuffer buffer, int frameNumber) {
        nativeProcess(ptr, buffer, frameNumber);
    }

    @Override
    public void close() {
        nativeClose(ptr);
    }

    private static native long nativeInit(int address);

    private static native void nativeProcess(long ptr, MappedByteBuffer buffer, int frameNumber);

    private static native void nativeClose(long ptr);
}
