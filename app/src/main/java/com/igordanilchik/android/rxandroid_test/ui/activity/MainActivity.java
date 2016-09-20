package com.igordanilchik.android.rxandroid_test.ui.activity;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.igordanilchik.android.rxandroid_test.R;
import com.igordanilchik.android.rxandroid_test.ui.fragment.CategoriesFragment;
import com.igordanilchik.android.rxandroid_test.ui.fragment.LocationFragment;
import com.igordanilchik.android.rxandroid_test.utils.FragmentUtils;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    private static final String LOG_TAG = MainActivity.class.getSimpleName();
    public static final String ARG_DATA = "ARG_DATA";
    private static final String KEY_ONLY_MAP = "KEY_ONLY_MAP";

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.fab)
    FloatingActionButton fab;
    @BindView(R.id.drawer_layout)
    DrawerLayout drawer;
    @BindView(R.id.nav_view)
    NavigationView navigationView;

    private boolean onlyMapIsDisplayed;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);

        fab.setOnClickListener(view -> Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show());

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(item -> {
            selectDrawerItem(item);
            return true;
        });

        if (savedInstanceState == null) {
            MenuItem item = navigationView.getMenu().findItem(R.id.nav_catalogue);
            selectDrawerItem(item);
        } else {
            onlyMapIsDisplayed = savedInstanceState.getBoolean(KEY_ONLY_MAP);
        }

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(KEY_ONLY_MAP, onlyMapIsDisplayed);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if (onlyMapIsDisplayed) {
                finish();
            }
            super.onBackPressed();
        }
    }

    public void selectDrawerItem(MenuItem menuItem) {
       Fragment fragment = null;
        Class fragmentClass;

        switch(menuItem.getItemId()) {
            case R.id.nav_catalogue:
                fragmentClass = CategoriesFragment.class;
                onlyMapIsDisplayed = false;
                break;
            case R.id.nav_location:
                fragmentClass = LocationFragment.class;
                onlyMapIsDisplayed = true;
                break;
            default:
                fragmentClass = CategoriesFragment.class;
                onlyMapIsDisplayed = false;
        }

        try {
            fragment = (Fragment) fragmentClass.newInstance();
        } catch (Exception e) {
            Log.e(LOG_TAG, "Error fragment load", e);
        }
        getSupportFragmentManager().popBackStackImmediate();
        FragmentUtils.replaceFragment(this, R.id.frame_content, fragment, false);

        menuItem.setChecked(true);
        setTitle(menuItem.getTitle());

        //drawer.closeDrawers();
        drawer.closeDrawer(GravityCompat.START);
    }
}
