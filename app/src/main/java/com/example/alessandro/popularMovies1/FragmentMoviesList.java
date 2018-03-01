package com.example.alessandro.popularMovies1;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.alessandro.popularMovies1.Model.Movie;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


// Class containing a list of movies
public class FragmentMoviesList extends Fragment {

    private static final String LOG_TAG = MainActivity.class.getSimpleName();
    private static final String ARG_COLUMN_COUNT = "column-count";
    private static final String SAVE_LAST_UPDATE_ORDER = "save_last_update_order";
    private static final int PORTRAIT_COLUMN_COUNT = 2;
    private static final int LANDSCAPE_COLUMN_COUNT = 3;
    private static final String API_POPULAR_MOVIES_BASE_URL = "https://api.themoviedb.org/3/movie/popular?";
    private static final String API_TOP_RATED_MOVIES_BASE_URL = "https://api.themoviedb.org/3/movie/top_rated?";
    private static final String API_POSTER_MOVIES_BASE_URL = "http://image.tmdb.org/t/p/";
    private static final String API_POSTER_SIZE = "w185/";
    private static final String API_KEY_PARAM = "api_key";
    private static final String JSON_LIST = "results";
    private static final String JSON_ID = "id";
    private static final String JSON_TITLE = "title";
    private static final String JSON_RELEASE_DATE = "release_date";
    private static final String JSON_VOTE_AVERAGE = "vote_average";
    private static final String JSON_OVERVIEW = "overview";
    private static final String JSON_POSTER_PATH = "poster_path";
    private static MoviesRecyclerViewAdapter mMoviesRecyclerViewAdapter;
    private static List<Movie> mMoviesList;
    private static String popularSortOrder;
    private static String topRatedSortOrder;
    private int mColumnCount = PORTRAIT_COLUMN_COUNT;
    private OnMoviesListInteractionListener mListener;
    private String mLastUpdateOrder;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public FragmentMoviesList() {
    }

    // Create new Fragment instance
    public static FragmentMoviesList newInstance(int columnCount) {
        FragmentMoviesList fragment = new FragmentMoviesList();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        popularSortOrder = getString(R.string.pref_popular_value);
        topRatedSortOrder = getString(R.string.pref_top_rated_value);

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }

        fetchMoviesList();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(SAVE_LAST_UPDATE_ORDER, mLastUpdateOrder);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null) {
            mLastUpdateOrder = savedInstanceState.getString(SAVE_LAST_UPDATE_ORDER);
        }

    }

    @Override
    public void onStart() {
        super.onStart();
        if (needToUpdateMoviesList()) {
            fetchMoviesList();
        }
    }

    // Starts AsyncTask to fetch The Movie DB API
    private void fetchMoviesList() {
        FetchMoviesTask moviesTask = new FetchMoviesTask();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String sortOrder = prefs.getString(getString(R.string.pref_sort_order_key),
                getString(R.string.pref_popular_value));
        mLastUpdateOrder = sortOrder;

        moviesTask.execute(sortOrder);
    }

    // Method to decide if movie info should be updated based on sort order
    private boolean needToUpdateMoviesList() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        return !mLastUpdateOrder.equals(prefs.getString(getString(R.string.pref_sort_order_key),
                getString(R.string.pref_popular_value)));
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.movie_list_fragment, container, false);

        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;

            int orientation = getResources().getConfiguration().orientation;

            if (orientation == Configuration
                    .ORIENTATION_PORTRAIT) {
                mColumnCount = PORTRAIT_COLUMN_COUNT;
            } else {
                mColumnCount = LANDSCAPE_COLUMN_COUNT;
            }

            GridLayoutManager gridLayoutManager = new GridLayoutManager(context, mColumnCount);
            recyclerView.setLayoutManager(gridLayoutManager);

            mMoviesList = new ArrayList<>();

            mMoviesRecyclerViewAdapter = new MoviesRecyclerViewAdapter(mMoviesList, mListener);
            recyclerView.setAdapter(mMoviesRecyclerViewAdapter);
        }
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnMoviesListInteractionListener) {
            mListener = (OnMoviesListInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnMoviesListFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnMoviesListInteractionListener {
        void onMoviesListInteraction(Movie item);
    }

    // AsyncTask to fetch The Movie DB data
    public static class FetchMoviesTask extends AsyncTask<String, Void, Movie[]> {

        private Movie[] getMoviesDataFromJson(String moviesJsonStr)
                throws JSONException {

            JSONObject moviesJson = new JSONObject(moviesJsonStr);
            JSONArray jsonMoviesArray = moviesJson.getJSONArray(JSON_LIST);

            Movie[] moviesArray = new Movie[jsonMoviesArray.length()];

            for (int i = 0; i < jsonMoviesArray.length(); i++) {
                Long id = jsonMoviesArray.getJSONObject(i).getLong(JSON_ID);
                String title = jsonMoviesArray.getJSONObject(i).getString(JSON_TITLE);
                String releaseDate = jsonMoviesArray.getJSONObject(i).getString(JSON_RELEASE_DATE);
                String voteAverage = jsonMoviesArray.getJSONObject(i).getString(JSON_VOTE_AVERAGE);
                String overview = jsonMoviesArray.getJSONObject(i).getString(JSON_OVERVIEW);
                Uri posterUri = createPosterUri(jsonMoviesArray.getJSONObject(i).getString
                        (JSON_POSTER_PATH));

                moviesArray[i] = new Movie(id, title, releaseDate, voteAverage, overview,
                        posterUri);
            }
            return moviesArray;
        }

        // Creates Uri based on sort order, language, etc
        private Uri createMoviesUri(String sortOrder) {
            Uri builtUri;

            // Method based on the StackOverflow question
            // Error:(64, 72) error: non-static method getString(int)
            // https://goo.gl/zLSne6

            if (sortOrder.equals(popularSortOrder)) {
                builtUri = Uri.parse(API_POPULAR_MOVIES_BASE_URL);
            } else if (sortOrder.equals(topRatedSortOrder)) {
                builtUri = Uri.parse(API_TOP_RATED_MOVIES_BASE_URL);
            } else {
                builtUri = Uri.parse(API_POPULAR_MOVIES_BASE_URL);

            }

            return builtUri.buildUpon()
                    .appendQueryParameter(API_KEY_PARAM, BuildConfig
                            .THE_MOVIE_DB_API_KEY)
                    .build();
        }

        // Method to create poster thumbnail Uri
        private Uri createPosterUri(String posterPath) {
            return Uri.parse(API_POSTER_MOVIES_BASE_URL).buildUpon()
                    .appendEncodedPath(API_POSTER_SIZE).appendEncodedPath(posterPath)
                    .build();
        }


        @Override
        protected Movie[] doInBackground(String... params) {

            if (params.length == 0) {
                return null;
            }

            // These two need to be declared outside the try/catch
            // so that they can be closed in the finally block.
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // Will contain the raw JSON response as a string.
            String moviesJsonStr;

            try {
                Uri moviesUri = createMoviesUri(params[0]);
                URL url = new URL(moviesUri.toString());

                // Create the request to TheMoviesDB, and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuilder buffer = new StringBuilder();
                if (inputStream == null) {
                    // Nothing to do.
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line).append("\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return null;
                }
                moviesJsonStr = buffer.toString();
            } catch (IOException e) {
                Log.e(LOG_TAG, "Error ", e);
                // If the code didn't successfully get the movies data, there's no point in
                // attempting to parse it.
                return null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(LOG_TAG, "Error closing stream", e);
                    }
                }
            }

            try {
                return getMoviesDataFromJson(moviesJsonStr);
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }

            // This will only happen if there was an error getting or parsing the movies.
            return null;
        }

        @Override
        protected void onPostExecute(Movie[] result) {
            if (result != null) {
                mMoviesRecyclerViewAdapter.clearRecyclerViewData();
                mMoviesList.addAll(Arrays.asList(result));

                mMoviesRecyclerViewAdapter.notifyItemRangeInserted(0, result.length);

            }
        }
    }
}
