package com.reactlibrary;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;

import androidx.annotation.Nullable;

import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AdapterView.OnItemClickListener;

import androidx.annotation.RequiresApi;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.common.MapBuilder;
import com.facebook.react.modules.core.DeviceEventManagerModule.RCTDeviceEventEmitter;
import com.facebook.react.uimanager.SimpleViewManager;
import com.facebook.react.uimanager.ThemedReactContext;
import com.facebook.react.uimanager.annotations.ReactProp;
import com.facebook.react.uimanager.events.RCTEventEmitter;
import com.reactlibrary.R.layout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;

import org.apache.commons.lang3.text.StrSubstitutor;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public final class RNAutoCompleteTextViewManager extends SimpleViewManager<RNAutoCompleteTextView> {
    @Nullable
    private Context mContext;
    @Nullable
    private String lastInputText;
    @Nullable
    private ArrayList optionList;
    private HashMap optionsMap;
    @Nullable
    private RNAutoCompleteTextViewManager.NativeTextWatcher textWatcher;
    private final int COMMAND_FOCUS;
    private final int COMMAND_BLUR;

    @Nullable
    public final String getLastInputText() {
        return this.lastInputText;
    }

    public final void setLastInputText(@Nullable String var1) {
        this.lastInputText = var1;
    }

    public String getName() {
        return "RNAutoCompleteTextView";
    }

    public String formatPhoneNumber(String unformattedNumber) {
        String number = unformattedNumber.replaceAll("[^\\d.]", "");
        if (number.length() > 10) {
            number = number.substring(0, 10);
        }
        int length = number.length();
        String formattedNumber = null;
        String areaCode;
        String three;
        String four;

        if (length > 6) {
            areaCode = number.substring(0, 3);
            three = number.substring(3, 6);
            four = number.substring(6, length);
            formattedNumber = String.format("(%s) %s - %s", areaCode, three, four);
        } else if (length > 3) {
            areaCode = number.substring(0, 3);
            three = number.substring(3, length);
            formattedNumber = String.format("(%s) %s", areaCode, three);
        } else if (length > 0) {
            formattedNumber = String.format("(%s", number);
        }
        return formattedNumber;
    }

    @RequiresApi(21)
    protected RNAutoCompleteTextView createViewInstance(ThemedReactContext reactContext) {
        this.mContext = (Context) reactContext;
        RNAutoCompleteTextView autocomplete = new RNAutoCompleteTextView((Context) reactContext);
        autocomplete.setOnFocusChangeListener((OnFocusChangeListener) (new OnFocusChangeListener() {
            public final void onFocusChange(View view, boolean hasFocus) {
                RNAutoCompleteTextViewManager.this.onChangeFocus(hasFocus, view);
            }
        }));
        this.textWatcher = new RNAutoCompleteTextViewManager.NativeTextWatcher(reactContext, autocomplete);
        autocomplete.addTextChangedListener((TextWatcher) this.textWatcher);
        autocomplete.setThreshold(1);
        return autocomplete;
    }

    public final void onChangeFocus(boolean focused, View view) {
        WritableMap event = Arguments.createMap();
        ReactContext reactContext = (ReactContext) view.getContext();
        if (focused) {
            reactContext.getJSModule(RCTEventEmitter.class).receiveEvent(
                    view.getId(),
                    "topFocus",
                    event);
        } else {
            reactContext.getJSModule(RCTEventEmitter.class).receiveEvent(
                    view.getId(),
                    "topBlur",
                    event);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @ReactProp(
            name = "dataSource"
    )

    public final void setDataSource(RNAutoCompleteTextView view, ReadableMap data) throws JSONException {
        String template = data.getString("itemFormat");
        String listData = data.getString("dataSource");
        JSONArray jsonArray = new JSONArray(listData);
        this.optionList = new ArrayList();
        int i = 0;

        for (int var7 = jsonArray.length(); i < var7; ++i) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            Iterator keys = jsonObject.keys();
            HashMap valuesMap = new HashMap();
            Iterator var13 = keys;

            while (var13.hasNext()) {
                Object element$iv = var13.next();
                String it = (String) element$iv;
                String value = jsonObject.optString(it);
                if (value instanceof String) {
                    valuesMap.put(it, value);
                }
            }

            StrSubstitutor sub = new StrSubstitutor((Map) valuesMap);
            String resolvedString = sub.replace(template);
            this.optionList.add(resolvedString);
            this.optionsMap.put(resolvedString, i);
        }

        if (view.getAutoCompleteType() != null && view.getAutoCompleteType().equals("tel")) {
            ArrayAdapter arrayAdapter = new PhoneAdapter(this.mContext, layout.simple_spinner_dropdown_item, this.optionList);
            view.setAdapter(arrayAdapter);
        } else {
            ArrayAdapter arrayAdapter = new ArrayAdapter(this.mContext, layout.simple_spinner_dropdown_item, this.optionList);
            view.setAdapter(arrayAdapter);
        }

        view.setOnItemClickListener((OnItemClickListener) (new OnItemClickListener() {
            public final void onItemClick(@Nullable AdapterView parent, View view, int position, long id) {
                RNAutoCompleteTextViewManager.this.onItemClick(parent, view, position, id);
            }
        }));
    }

    public final void onItemClick(@Nullable AdapterView parent, View view, int position, long id) {
        ReactContext reactContext = (ReactContext) view.getContext();
        Object item = parent.getItemAtPosition(position);
        Integer originalId = (Integer) this.optionsMap.get(item);
        ((RCTDeviceEventEmitter) reactContext.getJSModule(RCTDeviceEventEmitter.class)).emit("onItemClick", originalId);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @ReactProp(
            name = "value"
    )
    public final void setValue(RNAutoCompleteTextView view, @Nullable String value) {
        if (view.getJSEventCount() == view.getEventCount()) {
            view.removeTextChangedListener((TextWatcher) this.textWatcher);
            view.setText((CharSequence) value);
            if (value != null) {
                view.setSelection(value.length());
            }
            view.addTextChangedListener((TextWatcher) this.textWatcher);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @ReactProp(
            name = "jsEventCount"
    )
    public final void setJSEventCount(RNAutoCompleteTextView view, @Nullable int value) {
        view.setJSEventCount(value);
    }

    @ReactProp(
            name = "hint"
    )
    public final void setHint(RNAutoCompleteTextView view, @Nullable String value) {
        view.setHint((CharSequence) value);
    }

    @ReactProp(
            name = "showDropDown"
    )
    public final void showDropDown(RNAutoCompleteTextView view, boolean show) {
        if (show) {
            if (this.optionList.size() > 0) {
                view.showDropDown();
            }
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @ReactProp(
            name = "autoCompleteType"
    )
    public void setAutoCompleteType(RNAutoCompleteTextView view, @Nullable String type) {
        view.setAutoCompleteType(type);
        if (Objects.equals(type, "tel")) {
            view.setInputType(InputType.TYPE_CLASS_NUMBER);
        }
    }

    @ReactProp(
            name = "fontSize"
    )
    public void setTextSize(RNAutoCompleteTextView view, @Nullable int value) {
        view.setTextSize(TypedValue.COMPLEX_UNIT_SP, value);
    }

    @ReactProp(
            name = "fontColor"
    )
    public void setFontColor(RNAutoCompleteTextView view, @Nullable String value) {
        view.setTextColor(Color.parseColor(value));
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @ReactProp(
            name = "borderColor"
    )
    public void setBorderColor(RNAutoCompleteTextView view, @Nullable String value) {
        view.getBackground().setTint(Color.parseColor(value));
    }

    @Nullable
    @Override
    public Map<String, Integer> getCommandsMap() {
        return MapBuilder.of(
                "focus",
                COMMAND_FOCUS,
                "blur",
                COMMAND_BLUR
        );
    }

    public void receiveCommand(RNAutoCompleteTextView view, int commandId, @Nullable ReadableArray args) {
        if (commandId == this.COMMAND_FOCUS) {
            view.requestFocus();
        } else if (commandId == this.COMMAND_BLUR) {
            ReactContext reactContext = (ReactContext) view.getContext();
            InputMethodManager inputMethodManager = (InputMethodManager) reactContext.getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            view.clearFocus();
        }

    }

    public RNAutoCompleteTextViewManager() {
        this.optionsMap = new HashMap();
        this.COMMAND_FOCUS = 1;
        this.COMMAND_BLUR = 2;
    }

    public final class NativeTextWatcher implements TextWatcher {
        private ThemedReactContext reactContext;
        private RNAutoCompleteTextView view;

        public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {
        }

        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
            String inputText = charSequence.toString();
            int eventCount = view.getAndSetEventCount();
            if (!inputText.equals(RNAutoCompleteTextViewManager.this.getLastInputText()) && !this.view.isPerformingCompletion()) {
                RNAutoCompleteTextViewManager.this.setLastInputText(inputText);
                WritableMap event = Arguments.createMap();
                if (view.getAutoCompleteType() != null && view.getAutoCompleteType().equals("tel")) {
                    view.removeTextChangedListener(this);
                    String text = formatPhoneNumber(inputText);
                    view.setText(text);
                    if (text != null) view.setSelection(text.length());
                    view.addTextChangedListener(this);
                    String cleanPhone = text.replaceAll("[^\\d.]", "");
                    event.putString("text", cleanPhone);
                } else {
                    event.putString("text", inputText);
                }
                event.putInt("eventCount", eventCount);
                ((RCTEventEmitter) this.reactContext.getJSModule(RCTEventEmitter.class)).receiveEvent(this.view.getId(), "topChange", event);
            }
        }

        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        public void afterTextChanged(Editable editable) {
            if (view.getAutoCompleteType() != null && view.getAutoCompleteType().equals("tel")) {
                view.removeTextChangedListener(this);
                String inputText = editable.toString();
                String text = formatPhoneNumber(inputText);
                view.setText(text);
                if (text != null) view.setSelection(text.length());
                view.addTextChangedListener(this);
            }
        }

        public NativeTextWatcher(ThemedReactContext reactContext, RNAutoCompleteTextView view) {
            super();
            this.reactContext = reactContext;
            this.view = view;
        }
    }
}
