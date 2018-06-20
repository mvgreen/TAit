package com.cheater.search;

import com.cheater.App;
import com.cheater.search.Database.FileManager;
import com.cheater.search.Database.Record;
import com.cheater.search.Database.RecordDao;
import com.cheater.search.Database.ThreadOperation;

import java.util.Arrays;
import java.util.List;

/**
 * Класс-фабрика результатов поиска, создание экземпляров SearchResult должно происходить только здесь.
 **/
public class SearchResultFactory {

    /** Буффер для операций в отдельном потоке*/
    private static Record[] buffer;

    /** Функция должна обращаться к БД с запросом и возвращать его результат */
    public static SearchResult search(final String query) throws InterruptedException {
        final SearchResult[] result = new SearchResult[1];
        ThreadOperation.run(new Runnable() {
            @Override
            public void run() {
                RecordDao dao = App.getInstance().getDatabase().recordDao();
                buffer = dao.getByTag("%" + query + "%");
                result[0] = toSearchResult(query);
            }
        });

        return result[0];
    }

    private static SearchResult toSearchResult(String query) {
        Note[] notes = new Note[buffer.length];
        for (int i = 0; i < buffer.length; i++)
            notes[i] = new Note(buffer[i]);
        buffer = null;
        return new Result(query, notes);
    }

    /** Функция должна возвращать SearchResult, хранящий абсолютно все записи, хранящиеся в БД */
    public static SearchResult getAll() throws InterruptedException {
        final SearchResult[] result = new SearchResult[1];
        ThreadOperation.run(new Runnable() {
            @Override
            public void run() {
                RecordDao dao = App.getInstance().getDatabase().recordDao();
                List<Record> list = Arrays.asList(dao.getAll());
                buffer = list.toArray(new Record[0]);
                result[0] = toSearchResult("");
            }
        });

        return result[0];
    }

    /** Функция должна разбивать строку с тегами на отдельные теги (уже не нужна?) */
    public static String[] parseTags(String text) {
        List<String> list = Arrays.asList(text.split(","));
        return list.toArray(new String[0]);
    }

    /** Функция должна конкатенировать все теги в одну строку для отображения пользователю */
    public static String tagsToString(String[] tags) {
        if (tags.length == 0)
            return "";
        if (tags.length == 1)
            return tags[0];
        StringBuilder b = new StringBuilder();
        for (String s :
                tags) {
            b.append(s);
            b.append(",");
        }
        b.deleteCharAt(b.length() - 1);
        return b.toString();
    }

    /** Функция сохраняет переданную ему запись в БД и в файл и возвращает ссылку на нее же обратно */
    public static SearchRecord addRecord(final SearchRecord searchRecord) throws InterruptedException {
        ThreadOperation.run(new Runnable() {
            @Override
            public void run() {
                Record record = toRecord(searchRecord);
                RecordDao dao = App.getInstance().getDatabase().recordDao();
//                record.id = dao.getLastID() + 1;
                long id = dao.insert(record);
                searchRecord.setID(id);
                record.id = id;
                record.path = FileManager.saveRecord(searchRecord);
                dao.update(record);
                record = dao.getById(record.id);
                //Toast.makeText(App.getInstance().getApplicationContext(), "" + (record.path == null), Toast.LENGTH_SHORT).show();
            }
        });
        return searchRecord;
    }

    private static Record toRecord(SearchRecord searchRecord) {
        Record record = new Record();
        record.header = searchRecord.getTitle();
        record.type = searchRecord.getType();
        record.tags = Arrays.asList(searchRecord.getTags());

        return record;
    }

    /** Функция должна создавать новый экземпляр SearchRecord-а, но не сохранять его в БД */
    public static SearchRecord createRecord(String content, String[] tags, String title, String type) {
        return new Note(content, tags, title, type);
    }

    public static void updateRecord(final SearchRecord record) throws InterruptedException {
        ThreadOperation.run(new Runnable() {
            @Override
            public void run() {
                RecordDao dao = App.getInstance().getDatabase().recordDao();
                Record r = dao.getById(record.getID());
                r.tags = Arrays.asList(record.getTags());
                r.type = record.getType();
                r.header = record.getTitle();
                dao.update(r);
                FileManager.saveRecord(record);
            }
        });

    }

    public static void deleteRecord(final SearchRecord record) throws InterruptedException{
        if (record == null)
            return;
        ThreadOperation.run(new Runnable() {
            @Override
            public void run() {
                RecordDao dao = App.getInstance().getDatabase().recordDao();
                Record r = dao.getById(record.getID());
                FileManager.deleteFile(r.path);
                dao.delete(r);
            }
        });
    }
}
