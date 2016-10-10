package com.basestructure.util;

import android.content.Context;
import android.text.InputType;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import java.util.ArrayList;
import java.util.List;

public class AppAutoCompleteEditText extends AutoCompleteTextView {
    ArrayAdapter<String> mSimpleAdaptorObject = null;
    List<String> mListItems = new ArrayList<>();
    AutoCompleteTextView autoCompleteTextView;

    public void setItems(Context context, List<String> listItems) {
        setThreshold(1);
        mListItems = new ArrayList<>();
        for (String item : listItems)
            mListItems.add(item);
        mSimpleAdaptorObject = new ArrayAdapter<>(context,
                android.R.layout.simple_dropdown_item_1line, mListItems);
        setAdapter(mSimpleAdaptorObject);

        setOnKeyListener(new OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_DEL) {
                    for(String item: mListItems){
                        if(TextUtils.equals(getText().toString(), item)) {
                            setText("");
                            break;
                        }
                    }
                }
                return false;
            }
        });
    }

    public AppAutoCompleteEditText(Context context) {
        super(context);
    }

    public AppAutoCompleteEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        int type = getInputType();
        if(((type & InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS) > 0) && !isInEditMode() ){
            List<String> emailIds = AppAndroidUtils.getEmailIds(context);
            setItems(context,emailIds);
        }
    }

    public AppAutoCompleteEditText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void performFiltering(final CharSequence text, final int keyCode) {
        String filterText = text.toString();
        if (text.length()<1) {
            filterText="";
        }
        super.performFiltering(filterText, keyCode);

    }
}
