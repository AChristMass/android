package fr.igm.robotmissions.ui.ifc;

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
import io.swagger.client.api.IfcApi;
import io.swagger.client.model.Ifc;

public class IfcFragment extends Fragment implements Addable, Searchable {

    static final int REQUEST_CODE = 10245;
    private IfcApi ifcApi = new IfcApi();
    private IfcAdapter adapter;
    private Handler mainHandler = new Handler(Looper.getMainLooper());

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_ifc, container, false);
        // get main activity
        MainActivity mainActivity = (MainActivity) getActivity();
        if (mainActivity != null)
            mainActivity.changeFragment(this, this);

        adapter = new IfcAdapter(this);

        // set recycler view
        RecyclerView recyclerView = root.findViewById(R.id.ifc_list);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        search("");
        return root;
    }

    @Override
    public void add() {
        startActivity(new Intent(getContext(), IfcDetailsActivity.class));
    }

    @Override
    public void search(String searchText) {
        ApiCallback<List<Ifc>> listApiCallback = new SimpleApiCallback<List<Ifc>>() {
            @Override
            public void onSuccess(List<Ifc> result, int statusCode, Map<String, List<String>> responseHeaders) {
                mainHandler.post(()-> adapter.setIfcList(result));
            }

        };
        try {
            if (searchText.isEmpty()){
                ifcApi.listIfcAsync(listApiCallback);
            } else {
                ifcApi.searchIfcAsync(searchText, listApiCallback);
            }
        } catch (ApiException e) {
            // TODO : HANDLE EXCEPTION
            e.printStackTrace();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            restartFragment();
        }
    }

    private void restartFragment() {
        getParentFragmentManager()
                .beginTransaction()
                .detach(this)
                .attach(this)
                .commit();
    }
}
