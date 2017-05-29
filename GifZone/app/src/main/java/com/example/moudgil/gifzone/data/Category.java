package com.example.moudgil.gifzone.data;

/**
 * Created by apple on 29/05/17.
 */

public class Category {

    private int imgDrawable;
    private String imgTag;

    public Category(int imgDrawable, String imgTag) {
        this.imgDrawable = imgDrawable;
        this.imgTag = imgTag;
    }

    public int getImgDrawable() {
        return imgDrawable;
    }

    public void setImgDrawable(int imgDrawable) {
        this.imgDrawable = imgDrawable;
    }

    public String getImgTag() {
        return imgTag;
    }

    public void setImgTag(String imgTag) {
        this.imgTag = imgTag;
    }
}
