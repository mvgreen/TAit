package com.cheater.search.Database;

import android.arch.persistence.room.*;

@Dao
public interface RecordDao {
    @Query("SELECT * FROM record")
    Record[] getAll();

    @Query("SELECT * FROM record WHERE id = :id")
    Record getById(long id);

    @Query("SELECT * FROM record WHERE header LIKE :s")
    Record[] getByHeader(String s);
    //s = "%"+s+"%";

    @Query("SELECT * FROM record WHERE header LIKE :s OR tags LIKE :s")
    Record[] getByTag(String s);
    //s = "%"+s+"%";

    @Insert
    long insert(Record record);

    @Update
    void update(Record record);

    @Delete
    void delete(Record record);

    @Query("SELECT last_insert_rowid()")
    int getLastID();
}
