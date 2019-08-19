package com.example.indoorlocalizationv2;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.MenuItem;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.widget.Toast;

import com.example.indoorlocalizationv2.logic.IndoorLocalizationDatabase;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    //region UI related logic

    /**
     * Main activity on initialize event
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.initializeUIElements(savedInstanceState);
    }

    /**
     * Closes menu
     */
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new LocalizationInfoFragment()).commit();
        } else if (id == R.id.nav_discover_devices) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new DiscoverDevicesFragment()).commit();
        }  else if (id == R.id.nav_clear_db_logs) {
            new AlertDialog.Builder(this)
                    .setTitle("Clear database logs")
                    .setMessage("Do you really want to clear data regarding logged info?")
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            IndoorLocalizationDatabase database = IndoorLocalizationDatabase.getAppDatabase(getApplicationContext());
                            database.deviceLogDao().clearAllData();
                            IndoorLocalizationDatabase.destroyInstance();
                            Toast.makeText(MainActivity.this, "Logs cleared", Toast.LENGTH_SHORT).show();
                        }})
                    .setNegativeButton(android.R.string.no, null).show();

        }
        else if (id == R.id.nav_clear_defined_devices) {
            new AlertDialog.Builder(this)
                    .setTitle("Clear defined devices")
                    .setMessage("Do you really want to clear data regarding defined devices?")
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            IndoorLocalizationDatabase database = IndoorLocalizationDatabase.getAppDatabase(getApplicationContext());
                            database.definedDeviceDao().clearAllData();
                            IndoorLocalizationDatabase.destroyInstance();
                            Toast.makeText(MainActivity.this, "Devices cleared", Toast.LENGTH_SHORT).show();
                        }})
                    .setNegativeButton(android.R.string.no, null).show();
        }
        else if (id == R.id.nav_about) {
            Toast.makeText(this, "TODO: implement about fragment", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.nav_exit) {
            Toast.makeText(this, "TODO: show confirmation message and close app", Toast.LENGTH_SHORT).show();
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    //endregion

    //region UI related private methods
    private void initializeUIElements(Bundle savedInstanceState) {
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);

        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new LocalizationInfoFragment()).commit();
            navigationView.setCheckedItem(R.id.nav_home);
        }
    }

    //endregion
}
