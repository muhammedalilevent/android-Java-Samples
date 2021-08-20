package com.levent.landmarkbook;

import java.io.Serializable;

public class Landmark implements Serializable {

    String name,country;
    int image;

    //Constructor

    public Landmark(String name,String country,int image){
        this.name = name;
        this.country = country;
        this.image = image;
    }

}
