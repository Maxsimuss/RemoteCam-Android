package maxsimus.RemoteCam;

import android.annotation.SuppressLint;
import android.graphics.ImageFormat;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.params.OutputConfiguration;
import android.hardware.camera2.params.SessionConfiguration;
import android.media.ImageReader;
import android.os.Handler;
import android.os.Looper;
import android.util.Size;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class CameraController {
    private Size targetResolution = new Size(1920, 1080);
    private CameraInfo targetCamera;
    private CameraManager cameraManager;
    ImageReader imageReader = null;
    ImageProcessor imageProcessor;
    CameraSessionStateCallback callback = null;

    Executor exec = Executors.newSingleThreadExecutor();
    Handler handler = Handler.createAsync(Looper.getMainLooper());

    public CameraController(CameraManager cameraManager) {
        this.cameraManager = cameraManager;

        targetCamera = getCameras().get(0);
    }

    public void setAddress(int address) {
        if (imageProcessor == null) {
            imageProcessor = new ImageProcessor(address);
        }
    }

    public void switchCamera(String id) {
        Optional<CameraInfo> cameraInfo = getCameras().stream().filter(c -> c.getId().equals(id)).findFirst();
        if (cameraInfo.isPresent()) {
            targetCamera = cameraInfo.get();
            openCamera();
        }
    }

    @SuppressLint("MissingPermission")
    public void openCamera() {
        if (!targetCamera.isResolutionSupported(targetResolution)) {
            targetResolution = targetCamera.getResolutions().get(0);
        }

        try {
            cameraManager.openCamera(targetCamera.getId(), new CameraDevice.StateCallback() {
                @Override
                public void onOpened(@NonNull CameraDevice camera) {
                    if (imageReader != null) {
                        imageReader.close();
                    }

                    imageReader = ImageReader.newInstance(targetResolution.getWidth(), targetResolution.getHeight(), ImageFormat.JPEG, 8);
                    imageReader.setOnImageAvailableListener(reader -> {
                        if (imageProcessor != null) {
                            imageProcessor.submitWork(reader);
                        } else {
                            reader.acquireNextImage().close();
                        }
                    }, null);

                    List<OutputConfiguration> outputs = new ArrayList<>();

                    OutputConfiguration outputConfig = new OutputConfiguration(imageReader.getSurface());
                    outputs.add(outputConfig);

                    if (callback != null) {
                        callback.stop();
                    }

                    callback = new CameraSessionStateCallback(imageReader.getSurface());

                    SessionConfiguration config = new SessionConfiguration(SessionConfiguration.SESSION_REGULAR, outputs, exec, callback);
                    try {
                        camera.createCaptureSession(config);
                    } catch (CameraAccessException e) {
                        tryReopen();
//                        throw new RuntimeException(e);
                    }
                }

                @Override
                public void onDisconnected(@NonNull CameraDevice camera) {
                    callback = null;
                }

                @Override
                public void onError(@NonNull CameraDevice camera, int error) {
                    callback = null;
                    tryReopen();
                }
            }, handler);
        } catch (CameraAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private void tryReopen() {
        handler.postDelayed(() -> {
            try {
                openCamera();
            } catch (Exception ignored) {
                tryReopen();
            }
        }, 500);
    }

    public List<CameraInfo> getCameras() {
        ArrayList<CameraInfo> cameras = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            try {
                CameraInfo ci = new CameraInfo(String.valueOf(i), cameraManager.getCameraCharacteristics(String.valueOf(i)));
                cameras.add(ci);
            } catch (Exception ignored) {
            }
        }

        try {
            for (String id : cameraManager.getCameraIdList()) {
                CameraInfo ci = new CameraInfo(id, cameraManager.getCameraCharacteristics(id));
                if (!cameras.contains(ci)) {
                    cameras.add(ci);
                }
            }
        } catch (CameraAccessException e) {
            throw new RuntimeException(e);
        }

        return cameras;
    }

    public void setZoom(float zoom) {
        callback.zoom = zoom;
        callback.updateSettingsAndStart();
    }

    public void stop() {
        if (callback != null) {
            callback.stop();
        }
    }

    public void setResolution(int width, int height) {
        targetResolution = new Size(width, height);

        openCamera();
    }
}
