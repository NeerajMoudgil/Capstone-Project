package com.example.moudgil.gifzone.data;

/**
 * Created by apple on 24/05/17.
 */

public class GifImage {

    private String url;
    private String id;

    public GifImage(String url, String id) {
        this.url = url;
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
