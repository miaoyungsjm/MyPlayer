package com.activitytest.model;

/**
 * Created by MY on 2017/8/16.
 *
 */

public class MyMain {

    private int imageId;
    private String title;
    private int count;

    public MyMain(int imageId, String title, int count){
        this.imageId = imageId;
        this.title = title;
        this.count = count;
    }

    public int getImageId(){
        return imageId;
    }
    public String getTitle(){
        return title;
    }
    public int getCount(){
        return count;
    }
}
