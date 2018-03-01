package com.example.alessandro.popularMovies1;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.example.alessandro.popularMovies1.Model.Movie;


// Activity that hosts DetailsFragment
public class DetailsActivity extends AppCompatActivity {

    private static final String EXTRA_MOVIE = "intent_extra_movie";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        if (savedInstanceState == null) {
            Intent intent = getIntent();
            if (intent != null && intent.hasExtra(EXTRA_MOVIE)) {
                FragmentDetails detailsFragment = FragmentDetails.newInstance((Movie) intent
                        .getParcelableExtra(EXTRA_MOVIE));
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.details_fragment_container, detailsFragment)
                        .commit();
            }
        }
    }
}