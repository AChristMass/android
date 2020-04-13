package fr.igm.robotmissions.ui.inprog;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Map;

import fr.igm.robotmissions.Addable;
import fr.igm.robotmissions.MainActivity;
import fr.igm.robotmissions.R;
import fr.igm.robotmissions.objects.utils.SimpleApiCallback;
import io.swagger.client.ApiException;
import io.swagger.client.api.MissionApi;
import io.swagger.client.model.MissionInProg;

public class InProgFragment extends Fragment implements Addable {

    public static final int REQUEST_CODE = 1503;
    private InProgAdapter inProgAdapter;
    private MissionApi missionApi = new MissionApi();
    private Handler handler = new Handler(Looper.getMainLooper());
    private RecyclerView recyclerView;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_inprog, container, false);
        // get main activity
        MainActivity mainActivity = (MainActivity) getActivity();
        if (mainActivity != null) {
            mainActivity.changeFragment(this, null);
        }
        recyclerView = root.findViewById(R.id.inprog_list);
        inProgAdapter = new InProgAdapter(this);
        recyclerView.setAdapter(inProgAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        loadMissionsInProg();
        return root;
    }

    private void loadMissionsInProg() {
        try {
            missionApi.listMissionInProgAsync(new SimpleApiCallback<List<MissionInProg>>() {
                @Override
                public void onSuccess(List<MissionInProg> result, int statusCode, Map<String, List<String>> responseHeaders) {

                    handler.post(() -> inProgAdapter.setMissionInProgList(result));
                }
            });
        } catch (ApiException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void add() {
        Intent intent = new Intent(getContext(), InProgDetailsActivity.class);
        startActivityForResult(intent, REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            loadMissionsInProg();
        }
    }
}
