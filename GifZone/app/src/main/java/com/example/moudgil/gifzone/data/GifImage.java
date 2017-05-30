package com.example.moudgil.gifzone.data;

/**
 * Created by apple on 24/05/17.
 */

public class GifImage {

    private String url;
    private String id;
    private String hashTAg;

    public GifImage(String url, String id,String hashTAg) {
        this.url = url;
        this.id = id;
        this.hashTAg=hashTAg;
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

    public String getHashTAg() {
        return hashTAg;
    }

    public void setHashTAg(String hashTAg) {
        this.hashTAg = hashTAg;
    }
}
