package com.geostat.census_2024.ui.custom;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import com.geostat.census_2024.R;
import com.geostat.census_2024.data.local.entities.TagEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class InterAdapter<T extends TagEntity> extends ArrayAdapter<T> {

    private final List<T> items;
    private final List<T> tmp;
    private final int ILayout;
    private final IfTagDeleteHandler<T> deleteHandler;


    public InterAdapter(@NonNull Context context, int resource, @NonNull List<T> items, IfTagDeleteHandler<T> deleteHandler) {
        super(context, resource, items);

        this.items = new ArrayList<T>(items);
        this.tmp = new ArrayList<T>(items);

        this.deleteHandler = deleteHandler;
        ILayout = resource;
    }

    @Override
    public int getCount() {
        return items != null ? items.size() : 0;
    }

    @Nullable
    @Override
    public T getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    static class ViewHolder {
        public TextView textView;
    }

    public interface IfTagDeleteHandler<T> {
        int remove(T item) throws ExecutionException, InterruptedException;
    }

    @NonNull
    @Override
    public Filter getFilter() {
        return filter;
    }

    @NonNull
    @Override
    @SuppressLint("ClickableViewAccessibility")
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        ViewHolder viewHolder;
        View rowView = convertView;

        if (rowView == null) {
            LayoutInflater layoutInflater = ((Activity) getContext()).getLayoutInflater();
            rowView = layoutInflater.inflate(ILayout, null, true);
            viewHolder = new ViewHolder();
            viewHolder.textView = rowView.findViewById(R.id.textView);
            rowView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) rowView.getTag();
        }

        T item = items.get(position);
        viewHolder.textView.setText(item.getTitle());
        viewHolder.textView.setOnTouchListener((view, event) -> {
            if(event.getAction() == 0) {
                if(event.getRawX() >= viewHolder.textView.getRight() - viewHolder.textView.getTotalPaddingRight()) {
                    alert(position, item);
                    return true;
                }
            }
            return false;
        });

        return rowView;
    }

    public void alert (int index, T item) {
        AlertDialog.Builder may = new AlertDialog.Builder(getContext());
        may.setTitle("შეტყობინება");
        may.setMessage("გსურთ წაშლა?");
        may.setPositiveButton("დიახ", (dialogInterface, i) -> {
            try {
                int rem = InterAdapter.this.deleteHandler.remove(item);
                if (rem > 0) {
                    tmp.remove(index);
                    items.clear();
                    items.addAll(tmp);
                    notifyDataSetChanged();
                }
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }
        });
        may.setNegativeButton("არა", (dialogInterface, i) -> dialogInterface.cancel());
        may.create(); may.show();
    }


    Filter filter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            if (charSequence != null) {
                ArrayList<T> suggestions = new ArrayList<>();
                for (T item : tmp){
                    if (item.getTitle().contains(charSequence.toString())){
                        suggestions.add(item);
                    }
                }
                FilterResults results = new FilterResults();
                results.values = suggestions;
                results.count = suggestions.size();
                return results;
            } else {
                return new FilterResults();
            }
        }

        @Override
        @SuppressWarnings("unchecked")
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            items.clear();
            ArrayList<T> filtered = (ArrayList<T>) filterResults.values;
            clear();
            if (filterResults.count > 0) {
                items.addAll(filtered);
            } else if (charSequence == null) {
                items.addAll(tmp);
            }
            notifyDataSetChanged();
        }
    };
}


//            if(event.getAction() == MotionEvent.ACTION_UP) {
//                if(event.getRawX() >= (viewHolder.textView.getRight() - viewHolder.textView.getCompoundDrawables()[2].getBounds().width())){
//                    Log.d("WHAT", "getView: 2");
//                }
//            }