package com.cheater.search;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.v7.widget.LinearLayoutManager;
import com.cheater.SearchActivity;

public class Result implements SearchResult {

    private int count;
    private int scroll = 0;
    private SearchActivity.TabFragment fragment;
    private String query;
    private SearchRecord[] records;

    public Result(String query, SearchRecord[] records) {
        this.query = query == null ? "" : query;
        this.records = records == null ? new SearchRecord[0] : records;
        count = this.records.length;
    }

    @Override
    public String getPreview(int position) {
        return records[position].getContent();
    }

    @Override
    public int getCount() {
        return count;
    }

    @Override
    public void saveScroll() {
        scroll = ((LinearLayoutManager) fragment.getRecyclerView().getLayoutManager())
                .findFirstCompletelyVisibleItemPosition();
    }

    @Override
    public void restoreScroll() {
        fragment.getRecyclerView()
                .getLayoutManager()
                .scrollToPosition(scroll);
    }

    @Override
    public void setTab(SearchActivity.TabFragment fragment) {
        this.fragment = fragment;
    }

    @Override
    public String getQuery() {
        return query;
    }

    @Override
    public boolean queryEquals(String query) {
        return getQuery().contentEquals(query);
    }

    @Override
    public String getFull(int position) {
        return records[position].getContent();
    }

    @Override
    public SearchRecord getRecord(int position) {
        return records[position];
    }
    /* everything below here is for implementing Parcelable */

    // 99.9% of the time you can just ignore this
    @Override
    public int describeContents() {
        return 0;
    }

    // write your object's data to the passed-in Parcel
    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeString(query);
        out.writeTypedArray(records, 0);
        out.writeInt(count);
        out.writeInt(scroll);
    }

    // this is used to regenerate your object. All Parcelables must have a CREATOR that implements these two methods
    public static final Parcelable.Creator<SearchResult> CREATOR = new Parcelable.Creator<SearchResult>() {
        public Result createFromParcel(Parcel in) {
            return new Result(in);
        }

        public Result[] newArray(int size) {
            return new Result[size];
        }
    };

    // example constructor that takes a Parcel and gives you an object populated with it's values
    private Result(Parcel in) {
        query = in.readString();
        records = in.createTypedArray(Note.CREATOR);
        count = in.readInt();
        scroll = in.readInt();
    }
}
