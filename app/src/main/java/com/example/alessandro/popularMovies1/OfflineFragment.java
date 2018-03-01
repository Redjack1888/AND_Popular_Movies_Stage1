package com.example.alessandro.popularMovies1;

// Fragment displayed when internet connection is not available. It contains a text and a retry
// button.

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class OfflineFragment extends Fragment {

    private OnRetryInteractionListener mListener;

    public OfflineFragment() {
        // Required empty public constructor
    }

    public static OfflineFragment newInstance() {
        return new OfflineFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.offline_fragment, container, false);
        Button retryButton = view.findViewById(R.id.retry_button);
        retryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onRetryButtonPressed();
            }
        });
        return view;
    }

    // Calls listener implemented by host Activity
    private void onRetryButtonPressed() {
        if (mListener != null) {
            mListener.onRetryInteraction();
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnRetryInteractionListener) {
            mListener = (OnRetryInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnRetryInteractionListener {
        void onRetryInteraction();
    }
}
