package com.reactlibrary;

import android.content.Context;
import android.util.TypedValue;
import android.widget.LinearLayout.LayoutParams;

import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.AppCompatAutoCompleteTextView;


@RequiresApi(21)
public final class RNAutoCompleteTextView extends AppCompatAutoCompleteTextView {
    int eventCount;
    int jsEventCount;
    String autoCompleteType;

    public RNAutoCompleteTextView(Context context) {
        super(context);
        this.setInputType(524288);
        this.setPadding(0, 0, 0, 0);
        LayoutParams layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(0, 0, 0, 0);
        this.setLayoutParams(layoutParams);
        this.setLineSpacing(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5.0f, getResources().getDisplayMetrics()), 1.25f);
        eventCount = 0;
        jsEventCount = 0;
    }

    public int getAndSetEventCount() {
        return eventCount++;
    }

    public int getEventCount() {
        return eventCount;
    }

    public int getJSEventCount() {
        return jsEventCount;
    }

    public void setJSEventCount(int count) {
        jsEventCount = count;
    }

    public String getAutoCompleteType() {
        return autoCompleteType;
    }

    public void setAutoCompleteType(String type) {
        autoCompleteType = type;
    }
}
