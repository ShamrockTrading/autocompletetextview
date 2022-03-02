package com.reactlibrary;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.util.TypedValue;
import android.widget.LinearLayout.LayoutParams;

import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.AppCompatAutoCompleteTextView;


@RequiresApi(21)
public final class RNAutoCompleteTextView extends AppCompatAutoCompleteTextView {
    int eventCount;
    int jsEventCount;
    public RNAutoCompleteTextView(Context context) {
        super(context);
        this.setInputType(524288);
        this.setPadding(0, 12, 0, 15);
        eventCount = 0;
        jsEventCount = 0;
    }

    public int getAndSetEventCount () {
        return eventCount++;
    }

    public int getEventCount () {
        return eventCount;
    }

    public int getJSEventCount () {
        return jsEventCount;
    }

    public void setJSEventCount (int count) {
        jsEventCount = count;
    }
}
