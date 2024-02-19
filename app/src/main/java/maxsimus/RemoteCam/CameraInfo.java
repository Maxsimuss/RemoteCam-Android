package maxsimus.RemoteCam;

import android.graphics.ImageFormat;
import android.hardware.camera2.CameraCharacteristics;
import android.util.Range;
import android.util.Size;
import android.util.SizeF;

import androidx.annotation.NonNull;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

public class CameraInfo {
    private final String id;
    private final String friendlyName;
    private final List<Size> resolutions;
    private final Range<Integer> isoRange;
    private final Range<Long> exposureRange;
    private final Range<Float> zoomRange;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CameraInfo that = (CameraInfo) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @NonNull
    @Override
    public String toString() {
        return friendlyName;
    }

    public boolean isResolutionSupported(Size resolution) {
        return resolutions.contains(resolution);
    }

    public CameraInfo(String id, CameraCharacteristics chars) {
        this.id = id;

        isoRange = chars.get(CameraCharacteristics.SENSOR_INFO_SENSITIVITY_RANGE);
        exposureRange = chars.get(CameraCharacteristics.SENSOR_INFO_EXPOSURE_TIME_RANGE);

        resolutions = Arrays.asList(chars.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP).getOutputSizes(ImageFormat.JPEG));
        resolutions.sort(Comparator.comparingInt((Size a) -> -a.getWidth() * a.getHeight()));

        String facing = "";
        switch (chars.get(CameraCharacteristics.LENS_FACING)) {
            case CameraCharacteristics.LENS_FACING_BACK:
                facing = "Back";
                break;
            case CameraCharacteristics.LENS_FACING_FRONT:
                facing = "Front";
                break;
            case CameraCharacteristics.LENS_FACING_EXTERNAL:
                facing = "External";
                break;
        }

        SizeF sensorSize = chars.get(CameraCharacteristics.SENSOR_INFO_PHYSICAL_SIZE);

        zoomRange = chars.get(CameraCharacteristics.CONTROL_ZOOM_RATIO_RANGE);

        Size maxResolution = resolutions.get(0);

        friendlyName = facing + " " +
                maxResolution.getWidth() + "x" + maxResolution.getHeight() + " " +
                sensorSize.getWidth() + "x" + sensorSize.getHeight() + "mm" + " " + zoomRange.getLower() + " " + zoomRange.getUpper();
    }

    public List<Size> getResolutions() {
        return resolutions;
    }

    public Range<Integer> getIsoRange() {
        return isoRange;
    }


    public void setIso(double iso) {
//        int targetIso = (isoRange.getLower() + isoRange.getUpper()) / 8;
//        float factor = (float) iso / targetIso;
//        factor = MathUtils.clamp(factor, (float) ((double) exposureRange.getLower() / getBaseExposureTime()), 1);
//
//        iso /= factor;
//
//        float isoOvershoot = (float) iso / isoRange.getUpper();
//        isoOvershoot = MathUtils.clamp(isoOvershoot, 1, Math.min(16, (int) (exposureRange.getUpper() / getBaseExposureTime())));
//
//        this.iso = isoRange.clamp((int) iso);
//        exposureFactor = factor * isoOvershoot;
//
//        this.iso = 300;
//        exposureFactor = 1;
    }

    public String getId() {
        return id;
    }
}
