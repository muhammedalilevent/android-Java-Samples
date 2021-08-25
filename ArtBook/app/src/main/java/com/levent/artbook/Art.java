package com.levent.artbook;

import java.io.Serializable;

public class Art implements Serializable {

    String name;
    int id;

    public Art(String name, int id) {
        this.name = name;
        this.id = id;
    }
}
