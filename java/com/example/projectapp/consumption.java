package com.example.projectapp;

import android.media.Image;

public class consumption {
    String name;
    Image image;
    int consumption;
    String type;

    public consumption(String name, Image image, int consumption, String type)
    {
        this.name=name;
        this.image=image;
        this.consumption=consumption;
        this.type=type;
    }
    public void setName(String name)
    {
        this.name=name;
    }
}
