package com.threezj.fuli.model;

/**
 * Created by Zj on 2015/12/28.
 */
public class ImageFuli {
    public ImageFuli(String url) {
        this.url = url;
    }

    private String url;
    private int width;
    private int height;



    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }
}
