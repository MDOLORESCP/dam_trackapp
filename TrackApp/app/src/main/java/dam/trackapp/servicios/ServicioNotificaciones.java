package dam.trackapp.servicios;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.io.IOException;
import java.util.Random;

import dam.trackapp.R;

public class ServicioNotificaciones extends FirebaseMessagingService {
    private static final String TAG = "NOTIFICACIONES";
    private static final String CHANNEL_ID = "NOTIFICACIONES";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        crearCanalNotificaciones();

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_tasks)
                .setContentTitle(remoteMessage.getNotification().getTitle())
                .setContentText(remoteMessage.getNotification().getBody())
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

        notificationManager.notify(new Random().nextInt(100), builder.build());
    }

    @Override
    public void onNewToken(String token) {
        Log.d(TAG, "Refreshed token: " + token);
    }

    private void crearCanalNotificaciones() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "TRACK APP Notificaciones";
            String description = "TRACK APP Notificaciones";

            int importance = NotificationManager.IMPORTANCE_DEFAULT;

            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private String obtenerNombreTopicUsuario(String usuarioId) {
        return "_user_" + usuarioId;
    }

    private String obtenerNombreTopicGrupo(String grupoId) {
        return "_group_" + grupoId;
    }

    public void suscribirATopicUsuario(final String usuarioId) {
        FirebaseMessaging.getInstance().subscribeToTopic(obtenerNombreTopicUsuario(usuarioId))
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (!task.isSuccessful()) {
                            return;
                        }

                        Log.d(TAG, "Subscribed to user: " + usuarioId);
                    }
                });
    }

    public void suscribirATopicGrupo(final String grupoId) {
        FirebaseMessaging.getInstance().subscribeToTopic(obtenerNombreTopicGrupo(grupoId))
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (!task.isSuccessful()) {
                            return;
                        }

                        Log.d(TAG, "Subscribed to group: " + grupoId);
                    }
                });
    }

    public void borrarSuscripcionTopicGrupo(final String grupoId) {
        FirebaseMessaging.getInstance().unsubscribeFromTopic(obtenerNombreTopicGrupo(grupoId))
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (!task.isSuccessful()) {
                            return;
                        }

                        Log.d(TAG, "Unsubscribed to group: " + grupoId);
                    }
                });
    }

    public void borrarInstanciaNotificaciones() {
        try {
            FirebaseInstanceId.getInstance().deleteInstanceId();
        } catch (IOException e) {

        }
    }
}