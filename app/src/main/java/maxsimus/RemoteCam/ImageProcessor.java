package maxsimus.RemoteCam;

import android.media.Image;
import android.media.ImageReader;

import java.nio.MappedByteBuffer;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ImageProcessor {
    public static int ThreadCount = Runtime.getRuntime().availableProcessors() / 2;

    private final ThreadPoolExecutor executor;
    private int frameCounter = 0;
    private int usedImages = 0;
    public ImageProcessor(int address) {
        executor = new ThreadPoolExecutor(ThreadCount, ThreadCount, 1, TimeUnit.SECONDS, new ArrayBlockingQueue<>(2), new ImageProcessingThreadFactory(address));
    }

    public void submitWork(ImageReader imageReader) {
        if (usedImages + 1 >= imageReader.getMaxImages()) {
            System.out.println("Out of images!!!");
            return;
        }

        if (executor.getQueue().remainingCapacity() < 1) {
            System.out.println("Out of compute!");
            return;
        }

        Image image = imageReader.acquireLatestImage();
        if (image == null) {
            System.out.println("Could not acquire an image!");
            return;
        }
        acquireImage();
        frameCounter++;

        final int frameNumber = frameCounter;
        executor.submit(() -> {
            try {
                MappedByteBuffer buffer = (MappedByteBuffer) image.getPlanes()[0].getBuffer();

                NativeImageProcessor imageProcessor = ImageProcessingThreadFactory.threadLocal.get();

                imageProcessor.process(buffer, frameNumber);
            } catch (Exception ex) {
                ex.printStackTrace();
            } finally {
                releaseImage(image);
            }
        });
    }

    private synchronized void acquireImage() {
        usedImages++;
    }

    private synchronized void releaseImage(Image image) {
        image.close();
        usedImages--;
    }
}