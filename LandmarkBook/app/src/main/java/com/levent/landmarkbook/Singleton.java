package com.levent.landmarkbook;

public class Singleton {

    private Landmark sentLandmark;

    private static Singleton singleton;

    private Singleton(){

    }

    public  Landmark getChosenLandmark(){
        return sentLandmark;
    }

    public void setChosenLandmark(Landmark sentLandmark){
        this.sentLandmark = sentLandmark;
    }

    public static Singleton getInstance(){
        if (singleton == null){
            singleton = new Singleton();
        }
        return  singleton;
    }


}
