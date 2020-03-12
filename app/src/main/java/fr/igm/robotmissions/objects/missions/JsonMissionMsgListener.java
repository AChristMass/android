package fr.igm.robotmissions.objects.missions;

import org.json.JSONException;
import org.json.JSONObject;

public interface JsonMissionMsgListener {
    void onReceiveMissionMsg(JSONObject msg) throws JSONException;
}
