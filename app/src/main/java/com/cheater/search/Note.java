package com.cheater.search;

import android.os.Parcel;
import android.os.Parcelable;
import com.cheater.search.Database.FileManager;
import com.cheater.search.Database.Record;

public class Note implements SearchRecord {

    private String content;
    private String[] tags;
    private int length;
    private String type;
    private String title;
    private long id;

    Note(Record record) {
        content = FileManager.readFile(record.path);
        tags = ((String[]) record.tags.toArray());
        length = tags.length;
        type = record.type;
        title = record.header;
        id = record.id;
    }

    Note(String content, String[] tags, String title, String type) {
        this.content = content == null ? "" : content;
        this.tags = tags == null ? new String[0] : tags;
        length = this.tags.length;
        this.title = title == null ? "" : title;
        this.type = type;
        this.id = -1;
    }

    @Override
    public String getContent() {
        return content;
    }

    @Override
    public String[] getTags() {
        return tags;
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public String getType() {
        return type;
    }

    @Override
    public long getID() {
        return id;
    }

    @Override
    public void setTitle(String text) {
        this.title = text;
    }

    @Override
    public void setTags(String[] tags) {
        this.tags = tags;
        this.length = tags.length;
    }

    @Override
    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public void setType(String type) {
        this.type = type;
    }

    @Override
    public void setID(long id) {
        this.id = id;
    }

    @Override
    public int describeContents() {
        return 0;
    }
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(content);
        dest.writeInt(length);
        dest.writeStringArray(tags);
        dest.writeString(type);
        dest.writeLong(id);
    }

    private Note(Parcel in) {
        title = in.readString();
        content = in.readString();
        length = in.readInt();
        tags = new String[length];
        in.readStringArray(tags);
        type = in.readString();
        id = in.readLong();
    }

    public static final Parcelable.Creator<Note> CREATOR = new Parcelable.Creator<Note>() {
        public Note createFromParcel(Parcel in) {
            return new Note(in);
        }

        public Note[] newArray(int size) {
            return new Note[size];
        }
    };

}
