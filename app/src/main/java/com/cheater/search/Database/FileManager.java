package com.cheater.search.Database;

import android.widget.Toast;
import com.cheater.App;
import com.cheater.search.SearchRecord;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class FileManager {

    public static String saveRecord(SearchRecord record) {
        String name = record.getID() + ".rcd";
        File f = new File(App.getInstance().getFilesDir(), name);
        if (!f.exists()) {
            try {
                if (!f.createNewFile())
                    Toast.makeText(App.getInstance().getApplicationContext(), "Не удалось сохранить файл!", Toast.LENGTH_LONG).show();
            } catch (IOException e) {
                Toast.makeText(App.getInstance().getApplicationContext(), "Ошибка ввода-вывода при создании файла!", Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }
        }

        try(FileWriter writer = new FileWriter(f)) {
            writer.write(record.getContent());
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(App.getInstance().getApplicationContext(), "Не удалось записать файл!", Toast.LENGTH_LONG).show();
        }

        return name;
    }

    public static String readFile(String path) {
        File rec = new File(App.getInstance().getFilesDir(), path);
        StringBuilder sb = new StringBuilder();
        try (FileReader in = new FileReader(rec)) {
            int c;
            while ((c = in.read()) != -1)
                sb.append((char)c);
        } catch(IOException e) {
            e.printStackTrace();
        }
        return sb.toString();
    }

    public static void deleteFile(String path) {
        File f = new File(App.getInstance().getFilesDir(), path);
        if (!f.delete())
            Toast.makeText(App.getInstance(), "Не удалось удалить файл " + path + "!", Toast.LENGTH_SHORT).show();
    }
}
