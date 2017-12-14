package mcgyvers.mobitrip;

import android.app.Fragment;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    public static final String ORIGIN = "origin";
    public static final String DESTINATION = "destination";
    public static final String TMP_PREFS = "mobi_trip_prefs_TEMP";
    public static final String AMOUNT = "amount";
    public static final String COMMONEXP = "commonExp";
    public static final String TRIPDATE = "tripdate";
    public static final String MEMBERS = "members";
    public static final String TRIPID = "tripId";
    public static final String TRIPNAME = "tripName";

    Toolbar mainToolbar;
    DrawerLayout drawerLayout;
    ActionBarDrawerToggle actionBarDrawerToggle;
    FragmentTransaction fragmentTransaction;
    NavigationView navView;
    TextView tool_txt;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mainToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mainToolbar);





        tool_txt = findViewById(R.id.toolbarTXT);
        navView = (NavigationView) findViewById(R.id.navigationView);

        Typeface amaranth = Typeface.createFromAsset(getAssets(), "fonts/Amaranth-Bold.ttf");
        tool_txt.setTypeface(amaranth);

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, mainToolbar, R.string.drawer_open, R.string.drawer_close);

        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.main_container, new Current_trip());
        fragmentTransaction.commit();
        tool_txt.setText("Current Trip");
        getSupportActionBar().setTitle("");

        Bundle b = getIntent().getExtras();
        if(b != null){ // if we are coming back from choosing a location in placePicker, open NewTrip and send bundle b with location params as argument
            switchFragment(b.getInt("fragToLoad"));

        }

        Notifications notifications = new Notifications();
        notifications.notify(getApplicationContext());


        navView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switchFragment(item.getItemId());

                return false;
            }
        });

    }


    private void switchFragment(int n) {


        switch (n) {
            case R.id.nav_new_trips:
                NewTrip ft = new NewTrip();
                fragmentTransaction = getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.main_container, ft);
                fragmentTransaction.commit();
                tool_txt.setText("New Trip");
                getSupportActionBar().setTitle("");
                //item.setChecked(true);
                drawerLayout.closeDrawers();
                break;

            case R.id.nav_your_trips:
                fragmentTransaction = getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.main_container, new YourTrips());
                fragmentTransaction.commit();
                tool_txt.setText("Your Trips");
                getSupportActionBar().setTitle("");
                //item.setChecked(true);
                drawerLayout.closeDrawers();
                break;

            case R.id.nav_profile:
                fragmentTransaction = getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.main_container, new Profile());
                fragmentTransaction.commit();
                tool_txt.setText("Profile");
                getSupportActionBar().setTitle("");

                //item.setChecked(true);
                drawerLayout.closeDrawers();
                break;

            case R.id.nav_settings:
                fragmentTransaction = getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.main_container, new Settings());
                fragmentTransaction.commit();
                tool_txt.setText("Settings");
                getSupportActionBar().setTitle("");

                //item.setChecked(true);
                drawerLayout.closeDrawers();
                break;

            case R.id.nav_current_trip:
                fragmentTransaction = getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.main_container, new Current_trip());
                fragmentTransaction.commit();
                tool_txt.setText("Current Trip");
                getSupportActionBar().setTitle("");

                //item.setChecked(true);
                drawerLayout.closeDrawers();
                break;

        }


    }


    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        actionBarDrawerToggle.syncState();
    }

    //Double back to exit app
    boolean doubleBackToExitPressedOnce = false;

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 2000);
    }


}
