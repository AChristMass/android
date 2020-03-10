package fr.igm.robotmissions;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

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
                R.id.navigation_ifc, R.id.navigation_missions, R.id.navigation_robots)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);

        searchText = findViewById(R.id.search_edit_text);
        searchButton = findViewById(R.id.search_button);
        addButton = findViewById(R.id.add_button);
        searchLayout = findViewById(R.id.search_bar);
    }

    public void changeFragment(Addable addable, Searchable searchable){
        if (addable != null){
            addButton.setVisibility(View.VISIBLE);
            addButton.setOnClickListener((_v) -> addable.add());
        } else {
            addButton.setVisibility(View.GONE);
        }

        if (searchText != null){
            searchLayout.setVisibility(View.VISIBLE);
            searchText.setText(null);
            searchButton.setOnClickListener((_v) -> searchable.search(searchText.getText().toString()));
        } else {
            searchLayout.setVisibility(View.GONE);
        }
    }

    public void hideAddButton(){
        addButton.setVisibility(View.INVISIBLE);
    }
}
