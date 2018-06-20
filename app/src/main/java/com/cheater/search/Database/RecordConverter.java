package com.cheater.search.Database;

import android.arch.persistence.room.TypeConverter;

import java.util.Arrays;
import java.util.List;

public class RecordConverter {

    @TypeConverter
    public String fromtags(List<String> tags) {

        StringBuilder b = new StringBuilder();
        for (String s :
                tags) {
            b.append(s);
            b.append(",");
        }
        b.deleteCharAt(b.length() - 1);
        return b.toString();
    }

    @TypeConverter
    public List<String> totags(String data) {
        return Arrays.asList(data.split(","));
    }

}