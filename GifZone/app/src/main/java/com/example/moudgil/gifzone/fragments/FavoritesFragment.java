package com.example.moudgil.gifzone.fragments;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.moudgil.gifzone.R;
import com.example.moudgil.gifzone.adapter.GifImageAdapter;
import com.example.moudgil.gifzone.adapter.GifsCursorAdapter;
import com.example.moudgil.gifzone.app.Config;
import com.example.moudgil.gifzone.data.GifContract;
import com.example.moudgil.gifzone.data.GifImage;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link FavoritesFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link FavoritesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FavoritesFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>, GifsCursorAdapter.GifsCursorOnClickHandler {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final int LOADER_ID = 102;
    @BindView(R.id.favorites_recycler)
    RecyclerView favoriteRecyceler;
    @BindView(R.id.progress_bar)
    ProgressBar mProgressBar;
    @BindView(R.id.emptyView)
    TextView emptyView;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private OnFragmentInteractionListener mListener;
    private LoaderManager loaderManager;
    private GifsCursorAdapter gifCursorAdapter;

    private Unbinder unbinder;

    public FavoritesFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FavoritesFragment.
     */
    public static FavoritesFragment newInstance(String param1, String param2) {
        FavoritesFragment fragment = new FavoritesFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_favorites, container, false);
        unbinder = ButterKnife.bind(this, view);
        favoriteRecyceler.setHasFixedSize(true);
        gifCursorAdapter = new GifsCursorAdapter(this);
        if(!getResources().getBoolean(R.bool.two_pane)) {
            favoriteRecyceler.setLayoutManager(new GridLayoutManager(getContext(), 2));
        }else
        {
            favoriteRecyceler.setLayoutManager(new GridLayoutManager(getContext(), 4));

        }
        favoriteRecyceler.setAdapter(gifCursorAdapter);

        getActivity().getSupportLoaderManager().initLoader(LOADER_ID, null, this);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }



    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }



    /**
     * getting data from local DB for favourite gifs using contentProvider
     */
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new AsyncTaskLoader<Cursor>(getContext()) {
            Cursor mGifData = null;

            @Override
            protected void onStartLoading() {

                if (mGifData == null || DetailFragment.favoriteChanged) {
                    mProgressBar.setVisibility(View.VISIBLE);
                    forceLoad();
                    ;
                } else {
                    deliverResult(mGifData);
                }
            }

            @Override
            protected void onForceLoad() {
                super.onForceLoad();
            }

            @Override
            public void deliverResult(Cursor data) {
                mGifData = data;
                super.deliverResult(data);
            }

            @Override
            public Cursor loadInBackground() {

                try {
                    return getContext().getContentResolver().query(GifContract.GifEntry.CONTENT_URI,
                            null,
                            null,
                            null,
                            null);

                } catch (Exception e) {

                    e.printStackTrace();
                    return null;
                }
            }
        };
    }

    /**
     * @param loader
     * @param data   response data comes after querying the gifs DB
     */
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        int crsrCount = data.getCount();
        if (crsrCount > 0) {
            showGifsData(data);
        } else {
            String msg = getString(R.string.no_fav_error);
            showErrorView(msg);
        }
        mProgressBar.setVisibility(View.INVISIBLE);
        //  Movie.oldCursor = data;


    }

    private void showErrorView(String msg) {
        emptyView.setVisibility(View.VISIBLE);
        emptyView.setText(msg);
    }

    private void showGifsData(Cursor data) {
        gifCursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    @Override
    public void onClickCursor(GifsCursorAdapter.GifsAdapterViewHolder viewHolder, GifImage gifImage) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Transition changeTransform = TransitionInflater.from(getContext()).
                    inflateTransition(R.transition.change_image_transform);
            Transition explodeTransform = TransitionInflater.from(getContext()).
                    inflateTransition(android.R.transition.explode);
            this.setSharedElementReturnTransition(changeTransform);
            this.setExitTransition(explodeTransform);

            Fragment detailFragment = DetailFragment.newInstance("ok", "ok");
            Bundle bundle = new Bundle();
            bundle.putString(Config.IMG_URL, gifImage.getUrl());
            bundle.putString(Config.GIF_ID, gifImage.getId());

            // Setup enter transition on second fragment
            detailFragment.setSharedElementEnterTransition(changeTransform);
            detailFragment.setEnterTransition(explodeTransform);
            detailFragment.setArguments(bundle);
            getActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .addSharedElement(viewHolder.gifImage, "gif_transition")
                    .replace(R.id.container, detailFragment)
                    .addToBackStack(null)
                    .commit();
        }

    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
