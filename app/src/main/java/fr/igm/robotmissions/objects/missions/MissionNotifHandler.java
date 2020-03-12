package fr.igm.robotmissions.objects.missions;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import androidx.annotation.NonNull;


import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;

public class MissionNotifHandler extends Handler {
    public static final String MISSION_MSG_KEY = "MISSION_MSG";
    private JsonMissionMsgListener jsonMissionMsgListener;

    public MissionNotifHandler(JsonMissionMsgListener jsonMissionMsgListener) {
        super();
        this.jsonMissionMsgListener = jsonMissionMsgListener;
    }

    @Override
    public void handleMessage(@NonNull Message msg) {
        try {
            JSONObject jsonMsg = new JSONObject(msg.getData().getString(MISSION_MSG_KEY));
            Log.i("notif", "treat message");
            jsonMissionMsgListener.onReceiveMissionMsg(jsonMsg);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}
