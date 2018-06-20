package com.cheater.search.Database;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

import java.util.List;

@Entity
public class Record {

    public String header;

    public List<String> tags;

    public String path;

    public String type;

    @PrimaryKey(autoGenerate = true)
    public long id;

}
