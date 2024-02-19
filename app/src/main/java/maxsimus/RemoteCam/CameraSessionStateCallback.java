package maxsimus.RemoteCam;

import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.params.TonemapCurve;
import android.util.Range;
import android.view.Surface;

import androidx.annotation.NonNull;

public class CameraSessionStateCallback extends CameraCaptureSession.StateCallback {
    private CameraCaptureSession currentSession = null;
    private TonemapCurve curve;
    public float zoom = 0;

    private final Surface target;

    public CameraSessionStateCallback(Surface target) {
        this.target = target;

        curve = createTonemapCurve(2.2f);
    }

    private TonemapCurve createTonemapCurve(float gamma) {
        float[][] values = new float[3][];
        for (int channel = 0; channel < 3; channel++) {
            values[channel] = new float[128];
            for (int i = 0; i < values[channel].length; i += 2) {
                float input = (float) i / (values[channel].length - 2);
                float output = (float) Math.pow(input, 1.0 / gamma);

                values[channel][i] = Math.max(0, Math.min(1, input));
                values[channel][i + 1] = Math.max(0, Math.min(1, output));
            }
        }

        return new TonemapCurve(values[0], values[1], values[2]);
    }

    public void updateSettingsAndStart() {
        try {
            currentSession.setRepeatingRequest(createCaptureRequest(currentSession), null, null);
        } catch (CameraAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private CaptureRequest createCaptureRequest(CameraCaptureSession session) {
        CaptureRequest.Builder request;
        try {
            request = session.getDevice().createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
        } catch (CameraAccessException e) {
            throw new RuntimeException(e);
        }

//        System.out.printf("frame time: %dms, iso: %d, exposure: %dms\n", (int)imageProcessor.getAvgFrameTime(), cameraInfo.getIso(), (long)(cameraInfo.getBaseExposureTime() * cameraInfo.getExposureFactor()) / 1000000L);

        request.set(CaptureRequest.JPEG_QUALITY, (byte) 70);
        request.set(CaptureRequest.CONTROL_MODE, CaptureRequest.CONTROL_MODE_AUTO);
        request.set(CaptureRequest.CONTROL_SCENE_MODE, CaptureRequest.CONTROL_SCENE_MODE_DISABLED);
        request.set(CaptureRequest.CONTROL_ZOOM_RATIO, zoom);

        request.set(CaptureRequest.CONTROL_AF_MODE, CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE);
        request.set(CaptureRequest.CONTROL_AWB_MODE, CaptureRequest.CONTROL_AWB_MODE_AUTO);
        request.set(CaptureRequest.CONTROL_AE_MODE, CaptureRequest.CONTROL_AE_MODE_ON);
        request.set(CaptureRequest.CONTROL_AE_ANTIBANDING_MODE, CaptureRequest.CONTROL_AE_ANTIBANDING_MODE_AUTO);
        request.set(CaptureRequest.CONTROL_AE_TARGET_FPS_RANGE, new Range<>(30, 30));
//        request.set(CaptureRequest.CONTROL_AE_LOCK, true);

        request.set(CaptureRequest.TONEMAP_MODE, CaptureRequest.TONEMAP_MODE_CONTRAST_CURVE);
        request.set(CaptureRequest.TONEMAP_CURVE, curve);

        request.set(CaptureRequest.NOISE_REDUCTION_MODE, CaptureRequest.NOISE_REDUCTION_MODE_MINIMAL);
        request.set(CaptureRequest.EDGE_MODE, CaptureRequest.EDGE_MODE_FAST);
        request.addTarget(target);

        return request.build();
    }

    @Override
    public void onConfigured(@NonNull CameraCaptureSession session) {
        currentSession = session;

        try {
            updateSettingsAndStart();

        } catch (Exception ignored) {
        }
    }

    @Override
    public void onConfigureFailed(@NonNull CameraCaptureSession session) {
        currentSession = null;
    }

    public void stop() {
        try {
            currentSession.stopRepeating();
            currentSession = null;
        } catch (CameraAccessException e) {
        }
    }
}
