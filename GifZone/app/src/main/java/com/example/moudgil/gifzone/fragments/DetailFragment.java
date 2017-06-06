package com.example.moudgil.gifzone.fragments;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.gif.GifDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.moudgil.gifzone.R;
import com.example.moudgil.gifzone.app.Config;
import com.example.moudgil.gifzone.data.GifContract;
import com.example.moudgil.gifzone.utils.FileUtils;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.firebase.analytics.FirebaseAnalytics;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link DetailFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link DetailFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DetailFragment extends Fragment implements View.OnClickListener {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String SHARE_WHATSAPP = "shareWhatsapp";
    private static final String SHARE_FB = "shareFB";
    private static final String GALLERY = "saveGallery";
    private static final String SHARE_ALL = "shareALL";
    private static final int REQUEST_CODE_ASK_PERMISSIONS = 100;
    public static boolean favoriteChanged = false;
    @BindView(R.id.detail_img)
    ImageView detail_img;
    @BindView(R.id.whatsapp_share)
    ImageView whatsappImg;
    @BindView(R.id.favorite)
    ImageView favoriteImg;
    @BindView(R.id.fb_share)
    ImageView fbShareImg;
    @BindView(R.id.share)
    ImageView shareImg;
    @BindView(R.id.gallery)
    ImageView gallerySaveImg;
    @BindView(R.id.adView)
    AdView mAdView;
    private String shareType;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private OnFragmentInteractionListener mListener;
    private String gifID;
    private String gifURL;
    private ProgressDialog mProgressDialog;
    private Unbinder unbinder;
    private FirebaseAnalytics firebaseAnalytics;

    public DetailFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment DetailFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static DetailFragment newInstance(String param1, String param2) {
        DetailFragment fragment = new DetailFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        favoriteChanged = false;
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

        View v = inflater.inflate(R.layout.fragment_detail, container, false);
        unbinder = ButterKnife.bind(this, v);
        firebaseAnalytics=FirebaseAnalytics.getInstance(getContext());
        mProgressDialog = new ProgressDialog(getContext());
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage(getString(R.string.wait_msg));

        return v;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        whatsappImg.setOnClickListener(this);
        favoriteImg.setOnClickListener(this);
        fbShareImg.setOnClickListener(this);
        gallerySaveImg.setOnClickListener(this);
        shareImg.setOnClickListener(this);
        Bundle bundle = getArguments();

        if (bundle != null) {
            ActivityCompat.postponeEnterTransition(getActivity());
            gifURL = bundle.getString(Config.IMG_URL);
            gifID = getArguments().getString(Config.GIF_ID);
            new checkGifFavorite().execute(gifID);
            Glide.with(getContext()).load(Uri.parse(gifURL)).asGif().dontAnimate()
                    .listener(new RequestListener<Uri, GifDrawable>() {
                        @Override
                        public boolean onException(Exception e, Uri model, Target<GifDrawable> target, boolean isFirstResource) {
                            if (isAdded()) {
                                getActivity().supportStartPostponedEnterTransition();
                            }
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(GifDrawable resource, Uri model, Target<GifDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                            if (isAdded()) {
                                getActivity().supportStartPostponedEnterTransition();
                            }


                            return false;
                        }
                    }).into(detail_img);


        }
        showAdd();
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
        unbinder.unbind();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.whatsapp_share:
                shareType = SHARE_WHATSAPP;
                checkPermissions();
                break;
            case R.id.fb_share:
                shareType = SHARE_FB;
                checkPermissions();
                break;
            case R.id.share:
                shareType = SHARE_ALL;
                checkPermissions();
                break;
            case R.id.gallery:
                shareType = GALLERY;

                checkPermissions();
                break;
            case R.id.favorite:
                insertIntoDB();
                break;

        }
    }

    /**
     * inserting record for favorite item
     */

    private void insertIntoDB() {
        favoriteChanged = true;
        if (gifURL != null) {

            int currentImage = (Integer) favoriteImg.getTag(R.id.favoriteGifID);
            if(currentImage==R.mipmap.ic_launcher_round)
            {


                Uri deleturi = GifContract.GifEntry.buildGifUriWithId(gifID);
                getActivity().getContentResolver().delete(deleturi, null, null);
                favoriteImg.setImageResource(R.mipmap.ic_launcher);
                favoriteImg.setTag(R.id.favoriteGifID, R.mipmap.ic_launcher);


            }else if(currentImage==R.mipmap.ic_launcher)
            {
                ContentValues contentValues = new ContentValues();

                contentValues.put(GifContract.GifEntry.COLUMN_GIFID, gifID);
                contentValues.put(GifContract.GifEntry.COLUMN_GIF_URL, gifURL);

                Uri uri = getContext().getContentResolver().insert(GifContract.GifEntry.CONTENT_URI, contentValues);
                favoriteImg.setImageResource(R.mipmap.ic_launcher_round);
                favoriteImg.setTag(R.id.favoriteGifID, R.mipmap.ic_launcher_round);

            }

        }

    }

    public void download() {
        new Downloadmg().execute(getArguments().getString(Config.IMG_URL), getArguments().getString(Config.GIF_ID));

    }

    /**
     * Checking permissions for gallery access
     */

    private void checkPermissions() {
        int hasGalleryPermisiions = ContextCompat.checkSelfPermission(getContext(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (hasGalleryPermisiions != PackageManager.PERMISSION_GRANTED) {
            if (!this.shouldShowRequestPermissionRationale(
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                showMessageOKCancel(getString(R.string.allow_access),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                DetailFragment.this.requestPermissions(
                                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                        REQUEST_CODE_ASK_PERMISSIONS);
                            }
                        });
                return;
            }

            this.requestPermissions(
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    REQUEST_CODE_ASK_PERMISSIONS);
            return;
        }


        checkFileExists();

    }

    /**
     * Check if file already exists
     */
    private void checkFileExists() {
        FileUtils fileUtils = FileUtils.getInstance();
        if (fileUtils.checkFileExists(gifID)) {
            File sdcard = Environment.getExternalStorageDirectory();

            File folder = new File(sdcard.getAbsoluteFile(), Config.FOLDER_NAME);//the dot makes this directory hidden to the user
            folder.mkdir();
            File file = new File(folder.getAbsoluteFile(), gifID + ".gif");
            shareOrSave(file.getAbsolutePath());
        } else {
            download();
        }
    }

    private void shareOrSave(String path) {

        switch (shareType) {
            case SHARE_WHATSAPP:
                shareWhatsapp(path);
                break;
            case SHARE_FB:
                shareFB(path);
                break;
            case SHARE_ALL:
                shareAll(path);
                break;
            case GALLERY:
                saveToGallery(path);
                break;

        }
    }

    private void shareAll(String path) {

        Uri imageUri = Uri.parse(path);
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);

        shareIntent.putExtra(Intent.EXTRA_STREAM, imageUri);
        shareIntent.setType("image/*");
        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivity(Intent.createChooser(shareIntent, "Share Image"));

    }

    private void shareFB(String path) {


        Uri imageUri = Uri.parse(path);
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        //Target FB:
        shareIntent.setPackage("com.facebook.katana");
        //Add text and then Image URI
        shareIntent.putExtra(Intent.EXTRA_STREAM, imageUri);
        shareIntent.setType("image/gif");
        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        try {
            startActivity(shareIntent);
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(getContext(), getString(R.string.fb_error), Toast.LENGTH_SHORT).show();
        }
    }

    private void shareWhatsapp(String path) {

        Uri imageUri = Uri.parse(path);
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.setPackage("com.whatsapp");

        shareIntent.putExtra(Intent.EXTRA_STREAM, imageUri);
        shareIntent.setType("image/gif");
        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        try {
            startActivity(shareIntent);
            Bundle bundle = new Bundle();
            bundle.putInt(FirebaseAnalytics.Param.ITEM_ID, 101);
            bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, Config.NAV_TRENDING);
            bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "Whatsapp Share");
            firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(getContext(), getString(R.string.whatsapp_error), Toast.LENGTH_SHORT).show();
        }
    }

    private void saveToGallery(String path) {
        ContentValues values = new ContentValues();

        values.put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis());
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/gif");
        values.put(MediaStore.MediaColumns.DATA, path);

        getContext().getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        Toast.makeText(getContext(), getString(R.string.success_gallery), Toast.LENGTH_SHORT).show();


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_ASK_PERMISSIONS:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission Granted

                    checkFileExists();
                } else {
                    // Permission Denied
                    Toast.makeText(getContext(), getString(R.string.access_denied), Toast.LENGTH_SHORT)
                            .show();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new android.support.v7.app.AlertDialog.Builder(getContext())
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
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

    /**
     * AsyncTask for downloading image
     */

    private class Downloadmg extends AsyncTask<String, Integer, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mProgressDialog.show();

        }

        @Override
        protected String doInBackground(String... params) {
            String urlz = params[0];
            String gifId = params[1];
            try {
                URL url = new URL(urlz);

                URLConnection ucon = null;

                ucon = url.openConnection();

                InputStream inputStream = null;
                HttpURLConnection httpConn = (HttpURLConnection) ucon;
                httpConn.setRequestMethod("GET");
                httpConn.connect();
                inputStream = httpConn.getInputStream();


                File sdcard = Environment.getExternalStorageDirectory();

                File folder = new File(sdcard.getAbsoluteFile(), Config.FOLDER_NAME);
                folder.mkdir();
                File file = new File(folder.getAbsoluteFile(), gifId + ".gif");
                FileOutputStream fos = null;
                fos = new FileOutputStream(file);
                int totalSize = httpConn.getContentLength();
                int downloadedSize = 0;
                byte[] buffer = new byte[1024];
                int bufferLength = 0;
                while ((bufferLength = inputStream.read(buffer)) > 0) {
                    fos.write(buffer, 0, bufferLength);
                    downloadedSize += bufferLength;
                    Log.i("Progress:", "downloadedSize:" + downloadedSize + "totalSize:" + totalSize);
                }

                fos.flush();
                fos.close();
                httpConn.disconnect();
                Log.d("frag", file.getAbsolutePath());


                return file.getAbsolutePath();
            } catch (IOException io) {
                io.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;

        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            mProgressDialog.dismiss();
            shareOrSave(s);

        }
    }

    /**
     *Check weather Gif is added as favorite/not and set image on the basis of same
     */
    public class checkGifFavorite extends AsyncTask<String, Void, Cursor> {

        @Override
        protected Cursor doInBackground(String... params) {
            String gifID = params[0];
            Uri movieUri = GifContract.GifEntry.buildGifUriWithId(gifID);
            return getActivity().getContentResolver().query(movieUri, null, GifContract.GifEntry.COLUMN_GIFID, null, null);
        }

        @Override
        protected void onPostExecute(Cursor cursor) {
            int cursorCount = cursor.getCount();
            setFavriteImage(cursorCount);
        }
    }

    private void setFavriteImage(int cursorCount) {
        if (cursorCount > 0) {
            favoriteImg.setImageResource(R.mipmap.ic_launcher_round);
            favoriteImg.setTag(R.id.favoriteGifID, R.mipmap.ic_launcher_round);
        } else {
            favoriteImg.setImageResource(R.mipmap.ic_launcher);
            favoriteImg.setTag(R.id.favoriteGifID, R.mipmap.ic_launcher);
        }
    }

}