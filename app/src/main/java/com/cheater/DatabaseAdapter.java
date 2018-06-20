package com.cheater;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import com.cheater.search.SearchResult;
import com.cheater.search.SearchResultFactory;

/** Класс, связанный с RecyclerView в DatabaseActivity, и отвечающий за его заполнение */
public class DatabaseAdapter extends RecyclerView.Adapter {

    /** Контент списка */
    private SearchResult content;
    private DatabaseActivity activity;

    /**
     * Класс элемента списка.
     * Это "контейнер", в который записывается текст соответствующего элемента списка.
     **/
    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView textView;
        public ViewHolder(View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.card_text);
        }
    }


    public DatabaseAdapter(DatabaseActivity activity, SearchResult content) {
        this.activity = activity;
        this.content = content;
    }

    /** Вызывается, когда владеющему адаптером RecyclerView нужен новый контейнер для списка */
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.database_card, parent, false);
        return new ViewHolder(v);
    }

    /** Вызывается, когда RecyclerView хочет заполнить новосозданный/не отображаемый элемент списка */
    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        ((ViewHolder)(holder)).textView.setText(content.getPreview(position));
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            /**
             * При клике на контейнер запускается EditActivity,
             * ему передается запись, которую нужно отредактировать
             **/
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activity, EditActivity.class);
                intent.putExtra("RECORD_TO_EDIT", content.getRecord(holder.getAdapterPosition()));
                activity.startActivity(intent);
            }
        });

        ((ViewHolder) holder).itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                new AlertDialog.Builder(activity)
                        .setMessage("Вы уверены, что хотите удалить запись?")
                        .setPositiveButton("Да", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                try {
                                    SearchResultFactory.deleteRecord(content.getRecord(holder.getAdapterPosition()));
                                } catch (InterruptedException e) {
                                    Toast.makeText(activity, "Не удалось удалить элемент!", Toast.LENGTH_SHORT).show();
                                }
                                activity.refresh();
                            }
                        })
                        .setNegativeButton("Нет", null)
                        .show();
                return true;
            }
        });
    }


    @Override
    public int getItemCount() {
        return content.getCount();
    }

    public void setRecords(SearchResult records) {
        content = records;
        notifyDataSetChanged();
    }
}
