package com.example.moudgil.gifzone.fragments;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.moudgil.gifzone.DetailActivity;
import com.example.moudgil.gifzone.R;
import com.example.moudgil.gifzone.adapter.GifImageAdapter;
import com.example.moudgil.gifzone.app.Config;
import com.example.moudgil.gifzone.data.GifContract;
import com.example.moudgil.gifzone.data.GifImage;
import com.example.moudgil.gifzone.utils.FetchData;
import com.example.moudgil.gifzone.utils.NetworkCalls;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link TopGifsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link TopGifsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TopGifsFragment extends Fragment implements FetchData.OnResponse, GifImageAdapter.ImageClickedListener {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    @BindView(R.id.gif_image_recycler)
    RecyclerView gifRecycelerView;
    @BindView(R.id.progress_bar)
    ProgressBar mProgressBar;
    @BindView(R.id.emptyView)
    TextView emptyView;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private OnFragmentInteractionListener mListener;
    private List<GifImage> gifList;
    private GifImageAdapter gifImageAdapter;
    private boolean isTrending = false;
    private Unbinder unbind;

    public TopGifsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment TopGifsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static TopGifsFragment newInstance(String param1, String param2) {
        TopGifsFragment fragment = new TopGifsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setRetainInstance(true);
        if (gifList != null) {
            if (gifList.size() == 0) {
                super.onCreate(savedInstanceState);
                return;

            } else {
                super.onCreate(null);
                return;
            }
        }
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_top_gifs, container, false);
        ButterKnife.setDebug(true);
        unbind = ButterKnife.bind(this, view);

        gifList = new ArrayList<>();
        gifImageAdapter = new GifImageAdapter(this);
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            gifRecycelerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        } else {
            gifRecycelerView.setLayoutManager(new GridLayoutManager(getContext(), 3));
        }

        gifRecycelerView.setAdapter(gifImageAdapter);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (gifList.size() == 0) {
            getTopGifs();
        }
    }

    //calling Giphy API for getting the gifs and displaing using recyclerview

    private void getTopGifs() {
        FetchData fetchData = new FetchData(this);
        String url = Config.BASE_URL;
        HashMap<String, String> params = new HashMap<>();
        Bundle bundle = getArguments();
        if (bundle != null) {

            String urlType = bundle.getString(Config.URL_TYPE);
            String queryParam = bundle.getString(Config.CATEGORY_TYPE, null);
            if (urlType.equals(Config.TRENDING)) {
                isTrending = true;
            } else {
                isTrending = false;
            }
            if (queryParam != null) {
                params.put(Config.QUERY_PARAM, queryParam);

            }

            params.put(Config.API_KEY, Config.API_KEY_VALUE);

            String getUrl = fetchData.createGetURL(url, urlType, params);
            Log.d("frag", getUrl);
            if (NetworkCalls.getInstance().isConnected()) {
                mProgressBar.setVisibility(View.VISIBLE);
                fetchData.getCall(getUrl);
            } else {

                showErrorView(getString(R.string.network_error));

            }
        }
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

    @Override
    public void onDestroy() {
        super.onDestroy();
        unbind.unbind();
    }

    @Override
    public void onReponse(String response, String purpose) {

        if (!response.equals("error")) {
            try {
                JSONObject jsonObj = new JSONObject(response);
                JSONArray dataArr = jsonObj.getJSONArray("data");
                int len = dataArr.length();
                if (len > 0) {
                    ContentValues content[] = new ContentValues[len];
                    for (int iter = 0; iter < len; iter++) {
                        JSONObject dataobj = dataArr.getJSONObject(iter);
                        JSONObject imgObj = dataobj.getJSONObject("images");
                        String gifID = dataobj.getString("id");
                        JSONObject originalObj = imgObj.getJSONObject("fixed_width_downsampled");
                        String url = originalObj.getString("url");
                        ContentValues contentValues = new ContentValues();
                        contentValues.put(GifContract.GifEntry.COLUMN_GIFID, gifID);
                        contentValues.put(GifContract.GifEntry.COLUMN_GIF_URL, url);
                        content[iter] = contentValues;
                        GifImage gifImage = new GifImage(url, gifID, null);
                        gifList.add(gifImage);
                        Log.d("toppp", url);
                    }
                    if (isTrending) {
                        getActivity().getContentResolver().delete(GifContract.GifEntry.CONTENT_URI_TRENDING, null, null);
                        getActivity().getContentResolver().bulkInsert(GifContract.GifEntry.CONTENT_URI_TRENDING, content);
                    }
                    gifImageAdapter.setGifImageList(gifList);
                    if (isAdded()) {
                        mProgressBar.setVisibility(View.GONE);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
                showErrorView(getString(R.string.other_error));
            }
        }

    }

    //show error
    private void showErrorView(String err) {
        if (isAdded()) {
            mProgressBar.setVisibility(View.GONE);
            emptyView.setText(err);
            emptyView.setVisibility(View.VISIBLE);

        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        if (getResources().getBoolean(R.bool.two_pane)) {

            if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
                gifRecycelerView.setLayoutManager(new GridLayoutManager(getContext(), 3));
            } else {
                gifRecycelerView.setLayoutManager(new GridLayoutManager(getContext(), 4));

            }
        } else {
            if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
                gifRecycelerView.setLayoutManager(new GridLayoutManager(getContext(), 3));
            }

        }
    }

    @Override
    public void onImageClicekd(GifImageAdapter.MyViewHolder viewHolder, GifImage gifImage) {
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
