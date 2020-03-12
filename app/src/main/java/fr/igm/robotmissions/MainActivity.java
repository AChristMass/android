package fr.igm.robotmissions;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import fr.igm.robotmissions.objects.missions.MissionNotifHandler;
import fr.igm.robotmissions.objects.missions.NotifService;

import static java.security.AccessController.getContext;

public class MainActivity extends AppCompatActivity {

    private EditText searchText;
    private ImageButton searchButton;
    private FloatingActionButton addButton;
    private View searchLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_ifc, R.id.navigation_missions, R.id.navigation_robots, R.id.navigation_inprog)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);

        searchText = findViewById(R.id.search_edit_text);
        searchButton = findViewById(R.id.search_button);
        addButton = findViewById(R.id.add_button);
        searchLayout = findViewById(R.id.search_bar);

        startNotifService();
    }

    private void startNotifService() {
        Intent intent = new Intent(this, NotifService.class);
        intent.setAction(NotifService.START_WATCHING);
        startService(intent);
    }

    public void changeFragment(Addable addable, Searchable searchable){
        if (addable != null){
            addButton.setVisibility(View.VISIBLE);
            addButton.setOnClickListener((_v) -> addable.add());
        } else {
            addButton.setVisibility(View.GONE);
        }

        if (searchable != null){
            searchLayout.setVisibility(View.VISIBLE);
            searchText.setText(null);
            searchButton.setOnClickListener((_v) -> searchable.search(searchText.getText().toString()));
        } else {
            searchLayout.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onDestroy() {
        Intent intent = new Intent(this, NotifService.class);
        intent.setAction(NotifService.STOP_WATCHING);
        startService(intent);
        super.onDestroy();
    }
}
