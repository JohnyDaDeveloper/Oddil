package cz.johnyapps.oddil;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import cz.johnyapps.oddil.ui.profile.ProfileViewModel;
import cz.johnyapps.oddil.user.User;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private static final String TAG = "MainActivity";

    private FirebaseAuth firebaseAuth;
    private ProfileViewModel profileViewModel;


    private AppBarConfiguration appBarConfiguration;
    private NavigationView navigationView;
    private NavController navController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setupToolbar();
        setupNavigation();
        setupViewModel();

        firebaseAuth = FirebaseAuth.getInstance();
    }

    @Override
    protected void onStart() {
        super.onStart();

        checkIfLoggedIn();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        return false;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.fragment);

        return NavigationUI.navigateUp(navController, appBarConfiguration) || super.onSupportNavigateUp();
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.MainActivityToolbar);
        setSupportActionBar(toolbar);
    }

    private void setupNavigation() {
        DrawerLayout drawerLayout = findViewById(R.id.MainActivityDrawerLayout);
        navigationView = findViewById(R.id.MainActivityNavigationView);
        navigationView.setNavigationItemSelectedListener(this);

        appBarConfiguration = new AppBarConfiguration.Builder(R.id.nav_newsfeed, R.id.nav_calendar, R.id.nav_profile)
                .setDrawerLayout(drawerLayout)
                .build();

        navController = Navigation.findNavController(this, R.id.fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
    }

    private void setupViewModel() {
        ViewModelProvider provider = new ViewModelProvider(this);
        profileViewModel = provider.get(ProfileViewModel.class);

        profileViewModel.getUser().observe(this, new Observer<User>() {
            @Override
            public void onChanged(User user) {
                if (user != null) {
                    nameChanged(user.getName());
                } else {
                    nameChanged(null);
                }
            }
        });

        profileViewModel.getFirebaseUser().observe(this, new Observer<FirebaseUser>() {
            @Override
            public void onChanged(FirebaseUser firebaseUser) {
                userChanged(firebaseUser);
            }
        });
    }

    private void nameChanged(String name) {
        TextView email = navigationView.getHeaderView(0).findViewById(R.id.MainNavHeaderEmail);

        if (name != null) {
            email.setText(name);
        } else {
            email.setText("");
        }
    }

    private void userChanged(FirebaseUser firebaseUser) {
        /*if (firebaseUser == null) {
            navController.navigate(R.id.nav_signin);
        } else {
            navController.navigate(R.id.nav_newsfeed);
        }*/

        updateMenu(firebaseUser != null);
    }

    private void updateMenu(boolean signedIn) {
        Menu menu = navigationView.getMenu();

        if (signedIn) {
            menu.findItem(R.id.nav_signin).setVisible(false);
            menu.findItem(R.id.nav_signup).setVisible(false);
            menu.findItem(R.id.nav_profile).setVisible(true);
        } else {
            menu.findItem(R.id.nav_signin).setVisible(true);
            menu.findItem(R.id.nav_signup).setVisible(true);
            menu.findItem(R.id.nav_profile).setVisible(false);
        }
    }

    private void checkIfLoggedIn() {
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();

        if (currentUser != null) {
            profileViewModel.setFirebaseUser(currentUser);
            profileViewModel.fetchUserData();
        }

        updateMenu(currentUser != null);
    }
}
