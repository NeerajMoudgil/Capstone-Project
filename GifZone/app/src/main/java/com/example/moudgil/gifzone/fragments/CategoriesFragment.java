package com.example.moudgil.gifzone.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.moudgil.gifzone.R;
import com.example.moudgil.gifzone.TopGifActivity;
import com.example.moudgil.gifzone.adapter.GifImageAdapter;
import com.example.moudgil.gifzone.app.Config;
import com.example.moudgil.gifzone.data.GifImage;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link CategoriesFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link CategoriesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CategoriesFragment extends Fragment implements GifImageAdapter.ImageClickedListener {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    @BindView(R.id.categories_recycler)
    RecyclerView categoriesRecycler;
    private String mParam1;
    private String mParam2;
    private Unbinder unbinder;
    private TopGifsFragment.OnFragmentInteractionListener mListener;

    private List<GifImage> gifList;

    private GifImageAdapter gifImageAdapter;

    public CategoriesFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CategoriesFragment.
     */
    public static CategoriesFragment newInstance(String param1, String param2) {
        CategoriesFragment fragment = new CategoriesFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
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
        View view = inflater.inflate(R.layout.fragment_categories, container, false);
        unbinder = ButterKnife.bind(this, view);
        gifList = new ArrayList<>();
        gifImageAdapter = new GifImageAdapter(this);
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            categoriesRecycler.setLayoutManager(new GridLayoutManager(getContext(), 2));
        } else {
            categoriesRecycler.setLayoutManager(new GridLayoutManager(getContext(), 3));

        }
        categoriesRecycler.setAdapter(gifImageAdapter);

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        String arr[] = getResources().getStringArray(R.array.categoriesArr);
        int len = arr.length;
        for (int iter = 0; iter < len; iter++) {
            GifImage gifImage = new GifImage("https://media1.giphy.com/media/3o6gDTrDKD4cTqOlTG/200w_d.gif", "0", arr[iter]);
            gifList.add(gifImage);

        }
        gifImageAdapter.setGifImageList(gifList);

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
     * Whwn Image is clicked taken to detail page, in case oftablet on same page using fragments
     *
     * @param viewHolder viewholder holding item forshared transitions
     * @param gifImage   image clicked
     */
    @Override
    public void onImageClicekd(GifImageAdapter.MyViewHolder viewHolder, GifImage gifImage) {

        if (!getResources().getBoolean(R.bool.two_pane)) {

            Intent intent = new Intent(getActivity(), TopGifActivity.class);
            intent.putExtra(Config.NAV_TYPE, Config.NAV_CATEGORIES);
            intent.putExtra(Config.CATEGORY_TYPE, gifImage.getHashTAg());
            startActivity(intent);
        } else {
            Bundle bundle = new Bundle();
            bundle.putString(Config.NAV_TYPE, Config.NAV_CATEGORIES);
            bundle.putString(Config.URL_TYPE, Config.SEARCH);
            bundle.putString(Config.CATEGORY_TYPE, gifImage.getHashTAg());
            TopGifsFragment topGifsFragment = new TopGifsFragment();
            topGifsFragment.setArguments(bundle);

            getActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.container, topGifsFragment).addToBackStack(null)
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
        void onFragmentInteraction(Uri uri);
    }
}
