package com.cheater.search;

import android.os.Parcelable;

/** Интерфейс отдельного элемента списка/бд */
public interface SearchRecord extends Parcelable {

    String getContent();

    String[] getTags();

    String getTitle();

    String getType();

    long getID();

    void setTitle(String text);

    void setTags(String[] tags);

    void setContent(String content);

    void setType(String type);

    void setID(long id);
}
