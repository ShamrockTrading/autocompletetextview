package com.reactlibrary;

import android.content.Context;
import android.widget.ArrayAdapter;
import android.widget.Filter;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

public class PhoneAdapter extends ArrayAdapter<String> {
    private List<String> phoneNumbers;

    public PhoneAdapter(@NonNull Context context, int resource, @NonNull List<String> phoneList) {
        super(context, resource, 0, phoneList);
        phoneNumbers = new ArrayList<>(phoneList);
    }

    @NonNull
    @Override
    public Filter getFilter() {
        return phoneFilter;
    }

    private Filter phoneFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();
            List<String> suggestions = new ArrayList<>();

            if (constraint == null || constraint.length() == 0) {
                suggestions.addAll(phoneNumbers);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim().replaceAll("[^\\d.]", "");
                for (String item : phoneNumbers) {
                    if (item.toLowerCase().contains(filterPattern)) {
                        suggestions.add(item);
                    }
                }
            }

            results.values = suggestions;
            results.count = suggestions.size();
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            clear();
            addAll((List) results.values);
            notifyDataSetChanged();
        }

        @Override
        public CharSequence convertResultToString(Object resultValue) {
            return ((String) resultValue);
        }
    };
}
