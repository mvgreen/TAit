package com.cheater.search.Database;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;

@Database(entities = {Record.class}, version = 1)
@TypeConverters({RecordConverter.class})
public abstract class RecDatabase extends RoomDatabase {
    public abstract RecordDao recordDao();
}
