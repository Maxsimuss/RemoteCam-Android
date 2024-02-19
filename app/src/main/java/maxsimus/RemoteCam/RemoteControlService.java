package maxsimus.RemoteCam;

import java.util.List;
import java.util.stream.Collectors;

import io.grpc.stub.StreamObserver;
import maxsimus.remotecam.remotecontrol.CameraId;
import maxsimus.remotecam.remotecontrol.CameraList;
import maxsimus.remotecam.remotecontrol.Empty;
import maxsimus.remotecam.remotecontrol.RemoteControlGrpc;
import maxsimus.remotecam.remotecontrol.SettingValue;
import maxsimus.remotecam.remotecontrol.Size;

public class RemoteControlService extends RemoteControlGrpc.RemoteControlImplBase {
    private final CameraController cameraController;

    public RemoteControlService(CameraController cameraController) {
        this.cameraController = cameraController;
    }

    @Override
    public void getCameras(Empty request, StreamObserver<CameraList> responseObserver) {
        CameraList.Builder builder = CameraList.newBuilder();

        List<maxsimus.remotecam.remotecontrol.CameraInfo> ids = cameraController.getCameras().stream().map(input -> maxsimus.remotecam.remotecontrol.CameraInfo.newBuilder().setId(input.getId()).addAllResolutions(input.getResolutions().stream().map(size -> Size.newBuilder().setWidth(size.getWidth()).setHeight(size.getHeight()).build()).collect(Collectors.toList())).build()).collect(Collectors.toList());
        builder.addAllCameras(ids);

        responseObserver.onNext(builder.build());
        responseObserver.onCompleted();
    }

    @Override
    public void switchCamera(CameraId request, StreamObserver<Empty> responseObserver) {
        cameraController.switchCamera(request.getId());

        responseObserver.onNext(Empty.newBuilder().build());
        responseObserver.onCompleted();
    }

    @Override
    public void setZoom(SettingValue request, StreamObserver<Empty> responseObserver) {
        cameraController.setZoom(request.getValue());

        responseObserver.onNext(Empty.newBuilder().build());
        responseObserver.onCompleted();
    }

    @Override
    public void setResolution(Size request, StreamObserver<Empty> responseObserver) {
        cameraController.setResolution(request.getWidth(), request.getHeight());

        responseObserver.onNext(Empty.newBuilder().build());
        responseObserver.onCompleted();
    }
}
