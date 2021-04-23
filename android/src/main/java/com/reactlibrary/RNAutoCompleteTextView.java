package com.reactlibrary;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.util.TypedValue;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListAdapter;

import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.AppCompatAutoCompleteTextView;


@RequiresApi(21)
public final class RNAutoCompleteTextView extends AppCompatAutoCompleteTextView {
    public RNAutoCompleteTextView(Context context) {
        super(context);
        this.setInputType(524288);
        this.setTextColor(Color.parseColor("#333333"));
        this.setTextSize(14.0F);
        int value = 8;
        float var10001 = (float)value;
        Resources var10002 = context.getResources();
        int dpValue = (int)TypedValue.applyDimension(1, var10001, var10002.getDisplayMetrics());
        this.setPadding(0, this.getPaddingTop(), 0, dpValue);
        LayoutParams layoutParams = new LayoutParams(-1, -2);
        layoutParams.setMargins(0, 30, 0, 30);
        this.setLayoutParams((android.view.ViewGroup.LayoutParams)layoutParams);
        this.getBackground().setTint(Color.parseColor("#d3d3d3"));
    }

    public void setAdapter(ListAdapter var25) {
    }
}
