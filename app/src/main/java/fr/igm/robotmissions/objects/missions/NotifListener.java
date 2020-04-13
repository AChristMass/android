package fr.igm.robotmissions.objects.missions;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import org.json.JSONException;
import org.json.JSONObject;

import fr.igm.robotmissions.R;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;

class NotifListener extends WebSocketListener {
    private static final String CHANNEL_ID = "CHANNEL";
    private Context context;
    private Handler mainHandler;
    private MissionNotifHandler messageHandler;
    private static final int NOTIF_ID = 1;
    private NotificationManager notificationManager;
    private static final String JSON_ACTION_FIELD = "action";
    private static final String ACTION_MISSION_UPDATE = "update_mission";
    private static final String ACTION_ROBOT_CONNECTION = "robot_connection";

    NotifListener(Context context, Handler mainHandler) {
        this.context = context;
        this.mainHandler = mainHandler;
    }


    public void setMessageHandler(MissionNotifHandler messageHandler) {
        this.messageHandler = messageHandler;
    }

    public void onOpen(WebSocket webSocket, Response response) {
        Log.i("notif", "OPEN");
        createNotificationChannel();
    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = context.getString(R.string.channel_name);
            String description = context.getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }


    public void onMessage(WebSocket webSocket, String text) {
        Log.i("notif", "MESSAGE : " + text);
        try {
            final JSONObject jsonObject = new JSONObject(text);
            String action = jsonObject.getString(JSON_ACTION_FIELD);
            if (action.equals(ACTION_MISSION_UPDATE) && messageHandler != null) {
                Message m = new Message();
                Bundle data = new Bundle();
                data.putString(MissionNotifHandler.MISSION_MSG_KEY, text);
                m.setData(data);
                Log.i("notif", "handle message");
                mainHandler.post(() -> messageHandler.handleMessage(m));
            } else if (action.equals(ACTION_ROBOT_CONNECTION)) {
                final JSONObject robot = jsonObject.getJSONObject("robot");
                mainHandler.post(() -> {
                    try {
                        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                                .setSmallIcon(android.R.drawable.ic_dialog_info)
                                .setContentTitle("Robot online : "+ robot.getString("name"))
                                .setContentText("New robot online")
                                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
                        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
                        notificationManager.notify(NOTIF_ID, builder.build());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                });
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


    public void onMessage(WebSocket webSocket, ByteString bytes) {
        Log.i("notif", "MESSAGE : " + bytes.utf8());
    }


    public void onClosing(WebSocket webSocket, int code, String reason) {
        Log.i("notif", "CLOSE");
    }


    public void onFailure(WebSocket webSocket, Throwable t, Response response) {
        Log.i("notif", "FAILURE : " + t.getCause() );
        Log.i("notif", "FAILURE : " + t.getMessage() );
    }

}