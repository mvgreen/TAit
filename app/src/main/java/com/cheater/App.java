package com.cheater;

import android.app.Application;
import android.arch.persistence.room.Room;
import com.cheater.search.Database.RecDatabase;

import java.io.File;

public class App extends Application {

    public static App instance;
    public File tessDir;

    private RecDatabase database;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        database = Room.databaseBuilder(this, RecDatabase.class, "database")
                .build();
    }

    public static App getInstance() {
        return instance;
    }

    public RecDatabase getDatabase() {
        return database;
    }
}