package fr.igm.robotmissions.objects.missions;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.WebSocket;

public class NotifService extends Service {
    public final static String START_WATCHING = "START_WATCHING";
    public final static String START_FOLLOWING_MISSION = "START_FOLLOWING";
    public static final String STOP_WATCHING = "STOP_WATCHING";
    public static final String EXTRA_MISSION_ID = "EXTRA_MISSION_ID";
    private final IBinder mIBinder = new LocalBinder();
    private final static String BASE_URL = "ws://35.210.237.250/usersocket/";

    private OkHttpClient client;
    private WebSocket webSocket;
    private NotifListener notifListener;
    private Handler handler = new Handler(Looper.getMainLooper());

    @Override
    public void onCreate() {
        super.onCreate();
        client = new OkHttpClient();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        String action = intent.getAction();
        if (action == null)
            return Service.START_NOT_STICKY;
        Log.i("notif service action", action);
        switch (action) {
            case START_WATCHING:
                closeSocket();
                webSocket = startWatching(BASE_URL);
                break;
            case START_FOLLOWING_MISSION:
                int id = intent.getIntExtra(EXTRA_MISSION_ID, -1);
                webSocket.send("{\"missionId\":" + id + "}");
                break;
            case STOP_WATCHING:
                closeSocket();
                stopSelf();
                break;
        }
        return START_NOT_STICKY;
    }

    @Override
    public boolean stopService(Intent name) {
        return super.stopService(name);
    }



    private void closeSocket(){
        if (webSocket != null) {
            webSocket.close(1000, null);
            webSocket = null;
        }
    }

    public WebSocket startWatching(String url) {
        Request request = new Request.Builder().url(url).build();
        Log.i("notif", "url:"+url);
        notifListener = new NotifListener(this, handler);
        return client.newWebSocket(request, notifListener);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mIBinder;
    }

    public class LocalBinder extends Binder
    {
        public NotifService getInstance()
        {
            return NotifService.this;
        }
    }

    public void setMissionHandler(MissionNotifHandler missionHandler) {
        Log.i("notif", "set mission handler");
        notifListener.setMessageHandler(missionHandler);
    }
}