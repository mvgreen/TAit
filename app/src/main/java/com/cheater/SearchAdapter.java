package com.cheater;

import android.app.Activity;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.cheater.search.SearchResult;

/** Класс, связанный с RecyclerView в SearchActivity, и отвечающий за его заполнение */
public class SearchAdapter extends RecyclerView.Adapter {

    /** Контент списка */
    private SearchResult content;
    private Activity activity;

    /**
     * Класс элемента списка.
     * Это "контейнер", в который записывается текст соответствующего элемента списка.
     **/
    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView textView;

        public ViewHolder(View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.card_tv);
        }
    }

    public SearchAdapter(Activity activity, SearchResult content) {
        this.activity = activity;
        this.content = content;
    }

    /** Вызывается, когда владеющему адаптером RecyclerView нужен новый контейнер для списка */
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_layout, parent, false);
        return new ViewHolder(v);
    }

    /** Вызывается, когда RecyclerView хочет заполнить новосозданный/не отображаемый элемент списка */
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        ((ViewHolder)(holder)).textView.setText(content.getPreview(position));
        // При касании элемента открывается режим просмотра
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //if (content.getRecord(position).isLink()) {
                //    Intent intent = new Intent(Intent.ACTION_VIEW);
                //    intent.setData(Uri.parse(content.getRecord(position).getLink()));
                //    try {
                //        activity.startActivity(intent);
                //    } catch (ActivityNotFoundException e) {
                //        Toast.makeText(activity, "Не удалось открыть ссылку в браузере,\nпроверьте корректность ссылки и наличие браузера", Toast.LENGTH_SHORT).show();
                //    }
                //    return;
                //}
                LayoutInflater inflater = activity.getLayoutInflater();
                View view = inflater.inflate(R.layout.read_dialog, null);
                ((TextView) view.findViewById(R.id.read_dialog_text)).setText(content.getFull(position));

                new AlertDialog.Builder(activity).setView(view).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return content.getCount();
    }
}
