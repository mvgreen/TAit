package com.cheater;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import com.cheater.search.SearchRecord;
import com.cheater.search.SearchResultFactory;

/** Активити редактирования записи*/
public class EditActivity extends AppCompatActivity {

    private boolean exists;
    private boolean saved;
    private SearchRecord record;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        saved = false;
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (((TextView) findViewById(R.id.record_title)).getText() == null ||
                        ((TextView) findViewById(R.id.record_title)).getText().toString().contentEquals("") ||
                        ((TextView) findViewById(R.id.record_content)).getText() == null ||
                        ((TextView) findViewById(R.id.record_content)).getText().toString().contentEquals("")) {
                    Snackbar.make(view, "Заполните поля заголовка и содержания!", Snackbar.LENGTH_SHORT).show();
                    return;
                }
                record.setTitle(((TextView) findViewById(R.id.record_title)).getText().toString());
                record.setContent(((TextView) findViewById(R.id.record_content)).getText().toString());
                record.setTags(SearchResultFactory.parseTags(((TextView) findViewById(R.id.tags)).getText().toString()));
                record.setType("text");
                saved = true;
                onBackPressed();
            }
        });

        // Заполнение полей
        Intent intent = getIntent();
        if (Intent.ACTION_SEND.equals(intent.getAction()) && intent.getType() != null) {
            if ("text/plain".equals(intent.getType())) {
                String sharedText = intent.getStringExtra(Intent.EXTRA_TEXT);
                record = SearchResultFactory.createRecord(sharedText, null, "", "text");
                exists = false;
            }
        } else if (intent.hasExtra("RECORD_TO_EDIT")) {
            record = intent.getParcelableExtra("RECORD_TO_EDIT");
            exists = true;
        }
        else if (Intent.ACTION_SEND.equals(intent.getAction())
                && intent.getType() != null && intent.getType().equals("text/plain")) {
            record = SearchResultFactory.createRecord(intent.getStringExtra(Intent.EXTRA_TEXT), null, "", "text");
            exists = false;
        }
        else {
            record = SearchResultFactory.createRecord("", null, "", "text");
            exists = false;
        }

        ((TextView) findViewById(R.id.record_title)).setText(record.getTitle());
        ((TextView) findViewById(R.id.tags)).setText(SearchResultFactory.tagsToString(record.getTags()));
        ((TextView) findViewById(R.id.record_content)).setText(record.getContent());
    }

    void handleSendText(Intent intent) {
        String sharedText = intent.getStringExtra(Intent.EXTRA_TEXT);
        if (sharedText != null) {
            // Update UI to reflect text being shared
        }
    }


    @Override
    public void onBackPressed() {
        if (saved) {
            if (!exists) {
                try {
                    SearchResultFactory.addRecord(record);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    Toast.makeText(this, "При добавлении записи произошла ошибка!", Toast.LENGTH_LONG).show();
                }
            }
            else {
                try {
                    SearchResultFactory.updateRecord(record);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    Toast.makeText(this, "При сохранении изменений произошла ошибка!", Toast.LENGTH_SHORT).show();
                }
            }
            super.onBackPressed();
        }
        else
            new AlertDialog.Builder(this)
                    .setMessage("Вы уверены, что хотите отменить изменения и вернуться?")
                    .setTitle("Вы покидаете редактор без сохранения!")
                    .setPositiveButton("Да", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            EditActivity.super.onBackPressed();
                        }
                    })
                    .setNegativeButton("Нет", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    })
                    .show();
    }
}
