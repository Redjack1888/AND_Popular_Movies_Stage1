package com.example.alessandro.popularMovies1;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.alessandro.popularMovies1.Model.Movie;

import java.util.List;

// Class that manages the list of movies (RecyclerView)
public class MoviesRecyclerViewAdapter extends RecyclerView.Adapter<MoviesRecyclerViewAdapter
        .ViewHolder> {


    private final List<Movie> mMoviesList;
    private final FragmentMoviesList.OnMoviesListInteractionListener mListener;

    MoviesRecyclerViewAdapter(List<Movie> moviesList, FragmentMoviesList
            .OnMoviesListInteractionListener listener) {
        mMoviesList = moviesList;
        mListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.movie_item_fragment, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        holder.mItem = mMoviesList.get(position);

        Glide.with(holder.mPosterView.getContext()).load(holder.mItem.getPosterUri())
                .dontTransform().into(holder.mPosterView);

        holder.mTitle.setText(holder.mItem.getTitle());
        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onMoviesListInteraction(holder.mItem);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mMoviesList.size();
    }

    @Override
    public void onViewRecycled(@NonNull ViewHolder holder) {
        Glide.clear(holder.mPosterView);
    }

    // Method implementation based on "Remove all items from RecyclerView" by
    // StackOverflow - short url: https://goo.gl/qyv3Xq
    // It resets the list and notifies the adapter
    void clearRecyclerViewData() {
        int size = mMoviesList.size();
        if (size > 0) {
            for (int i = 0; i < size; i++) {
                mMoviesList.remove(0);
            }
            notifyItemRangeRemoved(0, size);
        }
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        final View mView;
        final ImageView mPosterView;
        final TextView mTitle;
        Movie mItem;

        ViewHolder(View view) {
            super(view);
            mView = view;
            mPosterView = view.findViewById(R.id.poster);
            mTitle = view.findViewById(R.id.title);
        }

    }
}
