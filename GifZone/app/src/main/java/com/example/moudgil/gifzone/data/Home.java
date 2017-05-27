package com.example.moudgil.gifzone.data;

/**
 * Created by apple on 24/05/17.
 */

public class Home {
    private String title;
    private int drawableId;

    public Home(String title, int drawableId) {
        this.title = title;
        this.drawableId = drawableId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getDrawableId() {
        return drawableId;
    }

    public void setDrawableId(int drawableId) {
        this.drawableId = drawableId;
    }
}
