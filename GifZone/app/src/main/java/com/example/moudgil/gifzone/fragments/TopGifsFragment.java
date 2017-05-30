package com.example.moudgil.gifzone.fragments;

import android.content.Context;
import android.content.Intent;
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

import com.bumptech.glide.Glide;
import com.example.moudgil.gifzone.DetailActivity;
import com.example.moudgil.gifzone.R;
import com.example.moudgil.gifzone.adapter.GifImageAdapter;
import com.example.moudgil.gifzone.app.Config;
import com.example.moudgil.gifzone.data.GifImage;
import com.example.moudgil.gifzone.utils.FetchData;
import com.pkmmte.view.CircularImageView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link TopGifsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link TopGifsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TopGifsFragment extends Fragment implements FetchData.OnResponse, GifImageAdapter.ImageClickedListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    private List<GifImage> gifList;

    @BindView(R.id.gif_image_recycler)
    RecyclerView gifRecycelerView;

    private GifImageAdapter gifImageAdapter;

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
        if(gifList!=null) {
            if (gifList.size() ==0) {
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
        View view=inflater.inflate(R.layout.fragment_top_gifs, container, false);
        ButterKnife.setDebug(true);
        ButterKnife.bind(this,view);

        gifList= new ArrayList<>();
        gifImageAdapter=new GifImageAdapter(this);

        gifRecycelerView.setLayoutManager(new GridLayoutManager(getContext(),2));
        gifRecycelerView.setAdapter(gifImageAdapter);
       // dummy_test=(ImageView) view.findViewById(R.id.dummy_test);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if(gifList.size()==0) {
            getTopGifs();
        }
    }

    private void getTopGifs() {
        FetchData fetchData= new FetchData(this);
        String url= Config.BASE_URL;
        HashMap<String,String> params= new HashMap<>();
        Bundle bundle=getArguments();
        if(bundle!=null) {

            String urlType=bundle.getString(Config.URL_TYPE);
            String queryParam=bundle.getString(Config.CATEGORY_TYPE,null);
            if(queryParam!=null)
            {
                params.put(Config.QUERY_PARAM,queryParam);

            }

            params.put(Config.API_KEY, Config.API_KEY_VALUE);
            String getUrl = fetchData.createGetURL(url, urlType, params);
            Log.d("frag", getUrl);
            fetchData.getCall(getUrl);
        }
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
//        if (context instanceof OnFragmentInteractionListener) {
//            mListener = (OnFragmentInteractionListener) context;
//        } else {
//            throw new RuntimeException(context.toString()
//                    + " must implement OnFragmentInteractionListener");
//        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onReponse(String response, String purpose) {

        if(!response.equals("error"))
        {
            try {
                JSONObject jsonObj= new JSONObject(response);
                JSONArray dataArr= jsonObj.getJSONArray("data");
                int len= dataArr.length();
                if(len>0)
                {
                    for(int iter=0;iter<len;iter++) {
                        JSONObject dataobj = dataArr.getJSONObject(iter);
                        JSONObject imgObj = dataobj.getJSONObject("images");
                        String gifID = dataobj.getString("id");
                        JSONObject originalObj = imgObj.getJSONObject("fixed_width_downsampled");
                        String url = originalObj.getString("url");
                        GifImage gifImage=new GifImage(url,gifID,null);
                        gifList.add(gifImage);
                        Log.d("toppp", url);
                    }
                    gifImageAdapter.setGifImageList(gifList);
                }
            } catch (JSONException e) {
                e.printStackTrace();
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

            Fragment detailFragment=  DetailFragment.newInstance("ok","ok");
            Bundle bundle= new Bundle();
            bundle.putString(Config.IMG_URL,gifImage.getUrl());
            bundle.putString(Config.GIF_ID,gifImage.getId());

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
