package com.example.alessandro.popularMovies1;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.alessandro.popularMovies1.Model.Movie;

// Class that can host MoviesListFragment or OfflineFragment
public class MainActivity extends AppCompatActivity implements FragmentMoviesList
        .OnMoviesListInteractionListener, OfflineFragment.OnRetryInteractionListener {

    private static final int PORTRAIT_COLUMN_COUNT = 2;
    private static final int LANDSCAPE_COLUMN_COUNT = 3;
    private static final String EXTRA_MOVIE = "intent_extra_movie";
    private FragmentManager fragmentManager;
    private Fragment currentFragment;
    private FragmentTransaction fragmentTransaction;
    private FragmentMoviesList fragmentMoviesList;
    private OfflineFragment offlineFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fragmentManager = getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();

        setupActionBar();

        if (savedInstanceState == null) {
            if (isInternetConnected()) {
                fragmentMoviesList = getMoviesFragment(getResources()
                        .getConfiguration());
                fragmentTransaction.add(R.id.movies_fragment_container, fragmentMoviesList)
                        .commit();
            } else {
                offlineFragment = OfflineFragment.newInstance();
                fragmentTransaction.add(R.id.movies_fragment_container, offlineFragment)
                        .commit();
            }
        } else {
            currentFragment = fragmentManager.findFragmentById(R.id
                    .movies_fragment_container);
            if (currentFragment instanceof FragmentMoviesList && !isInternetConnected()) {
                offlineFragment = OfflineFragment.newInstance();
                fragmentTransaction.replace(R.id.movies_fragment_container, offlineFragment)
                        .commit();
            } else if (currentFragment instanceof OfflineFragment && isInternetConnected()) {
                fragmentMoviesList = getMoviesFragment(getResources()
                        .getConfiguration());
                fragmentTransaction.replace(R.id.movies_fragment_container, fragmentMoviesList)
                        .commit();
            }
        }
    }

    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the App icon in the action bar.
            actionBar.setDisplayShowHomeEnabled(true);
            actionBar.setIcon(R.mipmap.ic_launcher);
        }
    }

    // Method to retrieve a new MovieFragment instance based on current phones's orientation
    private FragmentMoviesList getMoviesFragment(Configuration config) {
        if (config.orientation == Configuration.ORIENTATION_PORTRAIT) {
            return FragmentMoviesList.newInstance(PORTRAIT_COLUMN_COUNT);
        } else {
            return FragmentMoviesList.newInstance(LANDSCAPE_COLUMN_COUNT);
        }
    }

    @Override
    public void onMoviesListInteraction(Movie movieItem) {
        Intent intent = new Intent(this, DetailsActivity.class).putExtra(EXTRA_MOVIE,
                movieItem);
        startActivity(intent);
    }

    // Setting Menu

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_movies_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /*
    * Check if internet connection is available.
    * Method from http://stackoverflow.com/questions/16481334/check-network-connection-in-fragment
     */
    private boolean isInternetConnected() {

        ConnectivityManager connectivityManager = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo networkInfo = null;
        if (connectivityManager != null) {
            networkInfo = connectivityManager.getActiveNetworkInfo();
        }

        return networkInfo != null && networkInfo.isConnected();
    }

    @Override
    protected void onStart() {
        super.onStart();

        fragmentManager = getSupportFragmentManager();
        currentFragment = fragmentManager.findFragmentById(R.id.movies_fragment_container);
        if (currentFragment instanceof FragmentMoviesList && !isInternetConnected()) {
            fragmentTransaction = fragmentManager.beginTransaction();
            offlineFragment = OfflineFragment.newInstance();
            fragmentTransaction.replace(R.id.movies_fragment_container, offlineFragment)
                    .commit();
        }
    }

    // Method called after pressing RETRY button. It checks Internet connection again.
    @Override
    public void onRetryInteraction() {
        fragmentManager = getSupportFragmentManager();
        currentFragment = fragmentManager.findFragmentById(R.id.movies_fragment_container);
        if (currentFragment instanceof OfflineFragment && isInternetConnected()) {
            fragmentTransaction = fragmentManager.beginTransaction();
            fragmentMoviesList = getMoviesFragment(getResources()
                    .getConfiguration());
            fragmentTransaction.replace(R.id.movies_fragment_container, fragmentMoviesList)
                    .commit();
        } else if (!isInternetConnected()) {
            Toast.makeText(this, R.string.toast_no_connection, Toast.LENGTH_SHORT).show();
        }
    }

}
