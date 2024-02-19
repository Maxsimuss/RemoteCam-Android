package maxsimus.RemoteCam;

import java.util.concurrent.ThreadFactory;

public class ImageProcessingThreadFactory implements ThreadFactory {
    public static final ThreadLocal<NativeImageProcessor> threadLocal = new ThreadLocal<>();

    private final int address;

    public ImageProcessingThreadFactory(int address) {
        this.address = address;
    }

    @Override
    public Thread newThread(Runnable r) {
        return new Thread(r) {
            public void run() {
                setName("Image Processing Thread");
                threadLocal.set(new NativeImageProcessor(address));
                try {
                    super.run();
                } finally {
                    threadLocal.get().close();
                }
            }
        };
    }
}
