package com.example.moudgil.gifzone.utils;

import android.net.Uri;
import android.util.Log;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by apple on 23/05/17.
 */

public class FetchData {

    private final String TAG = FetchData.class.getSimpleName();


        NetworkCalls networkCalls;


        OnResponse onResponseHandler;

        public FetchData(FetchData.OnResponse res) {
            onResponseHandler = res;
            networkCalls = NetworkCalls.getInstance();
        }

        public String createGetURL(String url, String tag,HashMap<String,String> params)
        {
            Uri uri= Uri.parse(url);
            uri=uri.withAppendedPath(uri,tag);
            Uri.Builder uriBuilder=uri.buildUpon();

            for(String key:params.keySet())
            {
                uriBuilder.appendQueryParameter(key,params.get(key));
            }

            uri=uriBuilder.build();
            Log.d(TAG,"url :"+url);
            return uri.toString();
        }

        public void getCall(String url)

        {
            if (networkCalls.isConnected()) {
                String tag_json_obj = "json_obj_req";

                StringRequest stringReq = new StringRequest(Request.Method.GET,
                        url,
                        new Response.Listener<String>() {

                            @Override
                            public void onResponse(String response) {
                                Log.d(TAG, "responseeeee" + response.toString());
                                if (response != null) {

                                    try {
                                        JSONObject jsonObject = new JSONObject(response);
                                        JSONObject meta = jsonObject.getJSONObject("meta");
                                        int status = meta.getInt("status");
                                        if (status ==200) {
                                            onResponseHandler.onReponse(response,"okk");
                                        } else {
                                            onResponseHandler.onReponse("error", "error");
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }


                                }


                            }
                        }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {

                        VolleyLog.d(TAG, "Error: " + error.getMessage());
                        onResponseHandler.onReponse("error", "error");


                    }
                });

                stringReq.setRetryPolicy(new DefaultRetryPolicy(
                        0,
                        DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

                // Adding request to request queue
                networkCalls.addToRequestQueue(stringReq, tag_json_obj);
                //mcontext.onReponse(response);

            } else {
                onResponseHandler.onReponse("network_error", "error");


            }
        }

public interface OnResponse {
    void onReponse(String response, String purpose);
}
}
