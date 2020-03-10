package fr.igm.robotmissions.ui.robots;

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

import fr.igm.robotmissions.MainActivity;
import fr.igm.robotmissions.R;
import fr.igm.robotmissions.Searchable;
import fr.igm.robotmissions.objects.utils.SimpleApiCallback;
import io.swagger.client.ApiCallback;
import io.swagger.client.ApiException;
import io.swagger.client.api.RobotApi;
import io.swagger.client.model.Robot;

public class RobotsFragment extends Fragment implements Searchable {


    private RobotAdapter adapter = new RobotAdapter();
    private RobotApi robotApi = new RobotApi();
    private Handler mainHandler = new Handler(Looper.getMainLooper());
    private RecyclerView recyclerView;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_robots, container, false);
        // get main activity
        MainActivity mainActivity = (MainActivity) getActivity();
        if (mainActivity != null) {
            mainActivity.changeFragment(null, this);
        }

        // set recycler view
        recyclerView = root.findViewById(R.id.robots_list);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        search("");
        return root;
    }

    @Override
    public void search(String searchText) {
        ApiCallback<List<Robot>> listApiCallback = new SimpleApiCallback<List<Robot>>() {
            @Override
            public void onSuccess(List<Robot> result, int statusCode, Map<String, List<String>> responseHeaders) {
                mainHandler.post(()-> adapter.setRobotList(result));
            }
        };
        try {
            if (searchText.isEmpty()){
                robotApi.listRobotAsync(listApiCallback);
            } else {
                robotApi.searchRobotAsync(searchText, listApiCallback);
            }
        } catch (ApiException e) {
            // TODO : HANDLE EXCEPTION
            e.printStackTrace();
        }
    }
}
