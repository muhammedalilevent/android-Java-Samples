package com.levent.seyahatkitabm.roomdb;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.levent.seyahatkitabm.model.Place;

@Database(entities = {Place.class},version = 1)
public abstract   class PlaceDatabase extends RoomDatabase {

    public abstract IPlaceDao placeDao();

}
