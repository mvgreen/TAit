package com.cheater.search;

import android.os.Parcelable;
import com.cheater.SearchActivity;

import java.io.Serializable;

/** Интерфейс списка результатов запроса */
public interface SearchResult extends Parcelable, Serializable {
    String getPreview(int position);

    int getCount();

    void saveScroll();

    void restoreScroll();

    void setTab(SearchActivity.TabFragment fragment);

    String getQuery();

    boolean queryEquals(String query);

    String getFull(int position);

    SearchRecord getRecord(int position);
}
