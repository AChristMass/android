package fr.igm.robotmissions.ui.missions;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Map;

import fr.igm.robotmissions.Addable;
import fr.igm.robotmissions.MainActivity;
import fr.igm.robotmissions.R;
import fr.igm.robotmissions.Searchable;
import fr.igm.robotmissions.objects.utils.SimpleApiCallback;
import io.swagger.client.ApiCallback;
import io.swagger.client.ApiException;
import io.swagger.client.api.MissionApi;
import io.swagger.client.model.DeplacementMission;

public class MissionsFragment extends Fragment implements Addable, Searchable {


    static final int REQUEST_CODE = 10021;
    private MissionApi missionApi = new MissionApi();
    private Handler mainHandler = new Handler(Looper.getMainLooper());
    private MissionAdapter adapter;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_missions, container, false);
        // get main activity
        MainActivity mainActivity = (MainActivity) getActivity();
        if (mainActivity != null)
            mainActivity.changeFragment(this, this);

        // set recycler view
        adapter = new MissionAdapter(this);
        RecyclerView recyclerView = root.findViewById(R.id.missions_list);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        search("");
        return root;
    }

    @Override
    public void add() {
        startActivityForResult(new Intent(getContext(), MissionDetailsActivity.class), REQUEST_CODE);
    }

    @Override
    public void search(String searchText) {
        ApiCallback<List<DeplacementMission>> listApiCallback = new SimpleApiCallback<List<DeplacementMission>>() {
            @Override
            public void onSuccess(List<DeplacementMission> result, int statusCode, Map<String, List<String>> responseHeaders) {
                mainHandler.post(() -> adapter.setMissionList(result));
            }
        };
        try {
            if (searchText.isEmpty()) {
                missionApi.listMissionAsync(listApiCallback);
            } else {
                missionApi.searchMissionAsync(searchText, listApiCallback);
            }
        } catch (ApiException e) {
            // TODO : HANDLE EXCEPTION
            e.printStackTrace();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            search("");
        }
    }

}
