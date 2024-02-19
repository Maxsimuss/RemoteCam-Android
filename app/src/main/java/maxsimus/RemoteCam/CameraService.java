package maxsimus.RemoteCam;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.camera2.CameraManager;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationManagerCompat;

import java.io.IOException;

import io.grpc.Grpc;
import io.grpc.InsecureServerCredentials;
import io.grpc.Server;
import io.grpc.netty.NettyServerBuilder;

public class CameraService extends Service implements ServerFoundCallback {
    private static final String CHANNEL_ID = "RemoteCamChannel";

    private final BroadcastListener broadcastListener = new BroadcastListener();
    private CameraController cameraController = null;

    @Override
    public void serverFound(int address) {
        cameraController.setAddress(address);
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public boolean stopService(Intent name) {
        cameraController.stop();
        server.shutdown();

        return super.stopService(name);
    }

    @Override
    public void onDestroy() {
        cameraController.stop();
        server.shutdown();

        super.onDestroy();
    }

    Server server;

    @SuppressLint("MissingPermission")
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "RemoteCameraServiceChannel", NotificationManager.IMPORTANCE_DEFAULT);
        channel.setDescription("RemoteCam channel for foreground service notification");

        NotificationManager notificationManager = getSystemService(NotificationManager.class);
        notificationManager.createNotificationChannel(channel);

        Notification notification = new Notification.Builder(this, CHANNEL_ID).setSmallIcon(R.drawable.ic_stat_onesignal_default).build();

        NotificationManagerCompat.from(this).notify(1, notification);

        startForeground(1, notification);

        broadcastListener.startListeningToBroadcast(this);
        cameraController = new CameraController((CameraManager) getSystemService(Context.CAMERA_SERVICE));
        RemoteControlService service = new RemoteControlService(cameraController);

        cameraController.openCamera();

        try {
            server = NettyServerBuilder.forPort(43924, InsecureServerCredentials.create())
                    .addService(service)
                    .build();
            server.start();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return super.onStartCommand(intent, flags, startId);
    }
}
