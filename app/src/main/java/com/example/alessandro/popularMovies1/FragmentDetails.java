package com.example.alessandro.popularMovies1;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.alessandro.popularMovies1.Model.Movie;


// Fragment that displays detailed info about selected movie
public class FragmentDetails extends Fragment {

    private static final String ARG_MOVIE = "arg_movie";

    private Movie mMovie;

    public FragmentDetails() {
        // Required empty public constructor
    }

    // Create new Fragment instance
    public static FragmentDetails newInstance(Movie movieSelected) {
        FragmentDetails fragment = new FragmentDetails();
        Bundle args = new Bundle();
        args.putParcelable(ARG_MOVIE, movieSelected);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mMovie = getArguments().getParcelable(ARG_MOVIE);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.details_fragment, container, false);

        if (mMovie != null) {

            ImageView posterView = view.findViewById(R.id.poster);
            Glide.with(getActivity()).load(mMovie.getPosterUri()).into(posterView);

            TextView titleView = view.findViewById(R.id.title_content);
            titleView.setText(mMovie.getTitle());

            TextView releaseDateView = view.findViewById(R.id.release_date_content);
            releaseDateView.setText(mMovie.getReleaseDate());

            TextView averageView = view.findViewById(R.id.vote_average_content);
            averageView.setText(mMovie.getVoteAverage());

            TextView overviewView = view.findViewById(R.id.overview_content);

            // In portuguese, some movies does not contain overview data. In that case, displays
            // default text: @string/overview_not_available
            if (!TextUtils.isEmpty(mMovie.getOverview())) {
                overviewView.setText(mMovie.getOverview());
            }
        }

        return view;
    }
}
