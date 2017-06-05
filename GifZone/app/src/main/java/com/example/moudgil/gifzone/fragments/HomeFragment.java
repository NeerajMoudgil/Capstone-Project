package com.example.moudgil.gifzone.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.moudgil.gifzone.CategoryActivity;
import com.example.moudgil.gifzone.FavoritesActivity;
import com.example.moudgil.gifzone.R;
import com.example.moudgil.gifzone.TopGifActivity;
import com.example.moudgil.gifzone.adapter.HomeAdapter;
import com.example.moudgil.gifzone.app.Config;
import com.example.moudgil.gifzone.data.Home;
import com.example.moudgil.gifzone.ui.GridSpacingItemDecoration;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.firebase.analytics.FirebaseAnalytics;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link HomeFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment implements HomeAdapter.homeClickListener {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;
    @BindView(R.id.adView)
    AdView mAdView;
    private FirebaseAnalytics firebaseAnalytics;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private OnFragmentInteractionListener mListener;
    private Unbinder unbinder;
    private HomeAdapter adapter;
    private List<Home> homeList;
    public static final String TAG_TOP="topFragment";
    public static final String TAG_FAVORITES="favFragment";
    public static final String TAG_CATEGORIES="categoriesFragment";

    public static String CURRENT_TAG=TAG_TOP;

    public HomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeFragment.
     */
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
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
        View v = inflater.inflate(R.layout.fragment_home, container, false);
        unbinder = ButterKnife.bind(this, v);
        firebaseAnalytics = FirebaseAnalytics.getInstance(getContext());
        homeList = new ArrayList<>();
        adapter = new HomeAdapter(this, homeList);

        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getContext(), 2);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.addItemDecoration(new GridSpacingItemDecoration(2, dpToPx(10), true));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);
        showAdd();
        return v;
    }

    private void showAdd() {

        // Create an ad request. Check logcat output for the hashed device ID to
        // get test ads on a physical device. e.g.
        // "Use AdRequest.Builder.addTestDevice("ABCDEF012345") to get test ads on this device."
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice("ABCDEF012345")
                .build();
        mAdView.loadAd(adRequest);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        prepareHome();
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

    @Override
    public void homeClick(Home home) {
        String navName = home.getTitle();
        Intent intent;
        Fragment fragment=null;

            switch (navName) {
            case "Top": {
                if (!getResources().getBoolean(R.bool.two_pane)) {

                    intent = new Intent(getActivity(), TopGifActivity.class);
                    intent.putExtra(Config.NAV_TYPE, Config.NAV_TRENDING);
                    Bundle bundle = new Bundle();
                    bundle.putInt(FirebaseAnalytics.Param.ITEM_ID, 100);
                    bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, Config.NAV_TRENDING);
                    bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "HOME Navigation");
                    firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
                    startActivity(intent);
                } else {
                    CURRENT_TAG=TAG_TOP;
                    Bundle bundle = new Bundle();
                    bundle.putString(Config.URL_TYPE, Config.TRENDING);
                     fragment = new TopGifsFragment();
                    fragment.setArguments(bundle);

                }
                break;
            }
            case "Categories":
                if (!getResources().getBoolean(R.bool.two_pane)) {

                    intent = new Intent(getActivity(), CategoryActivity.class);
                    startActivity(intent);
                } else {
                    CURRENT_TAG=TAG_CATEGORIES;

                     fragment = new CategoriesFragment();



                }
                break;
            case "Favorites":
                if (!getResources().getBoolean(R.bool.two_pane)) {

                    intent = new Intent(getActivity(), FavoritesActivity.class);
                    startActivity(intent);
                } else {
                    CURRENT_TAG=TAG_FAVORITES;
                     fragment = new FavoritesFragment();



                }
                break;
        }
        //check if clicked on same home menu again

        if (!getResources().getBoolean(R.bool.two_pane)) {
        return;
        }

        if (getActivity().getSupportFragmentManager().findFragmentByTag(CURRENT_TAG) != null) {
            return;

        }

        for (int i = 0; i < getActivity().getSupportFragmentManager().getBackStackEntryCount(); ++i) {
            getActivity().getSupportFragmentManager().popBackStack();
        }

        if(fragment!=null) {

            getActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.container, fragment, CURRENT_TAG)
                    .commit();
        }

    }

    /**
     * Converting dp to pixel
     */
    private int dpToPx(int dp) {
        Resources r = getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
    }

    /**
     * Adding few albums for testing
     */
    private void prepareHome() {

        String homeArr[] = getResources().getStringArray(R.array.homeArr);
        int len = homeArr.length;

        for (int iter = 0; iter < len; iter++) {
            Home home = new Home(homeArr[iter], R.mipmap.ic_launcher);
            homeList.add(home);

        }

        adapter.notifyDataSetChanged();
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
