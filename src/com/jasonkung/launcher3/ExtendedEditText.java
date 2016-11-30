/*
 * Copyright (C) 2015 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.jasonkung.launcher3;

import android.content.Context;
import android.graphics.Rect;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ImageSpan;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;


/**
 * The edit text that reports back when the back key has been pressed.
 */
public class ExtendedEditText extends EditText {
    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        setHint(getSearchHint());
    }

    /**
     * Implemented by listeners of the back key.
     */
    public interface OnBackKeyListener {
        public boolean onBackKey();
    }

    private OnBackKeyListener mBackKeyListener;

    public ExtendedEditText(Context context) {
        super(context);
    }

    public ExtendedEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ExtendedEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setOnBackKeyListener(OnBackKeyListener listener) {
        mBackKeyListener = listener;
    }

    @Override
    public boolean onKeyPreIme(int keyCode, KeyEvent event) {
        // If this is a back key, propagate the key back to the listener
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP) {
            if (mBackKeyListener != null) {
                return mBackKeyListener.onBackKey();
            }
            return false;
        }
        return super.onKeyPreIme(keyCode, event);
    }

    @Override
    protected void onFocusChanged(boolean focused, int direction, Rect previouslyFocusedRect) {
        setHint(focused ? "" : getSearchHint());
        super.onFocusChanged(focused, direction, previouslyFocusedRect);
    }

    private SpannableStringBuilder getSearchHint(){
        ImageSpan imageHint = new ImageSpan(getContext(), R.drawable.ic_search_grey);
        SpannableStringBuilder spannableString =
                new SpannableStringBuilder(" "+getContext().getResources().getString(R.string.all_apps_search_bar_hint));
        spannableString.setSpan(imageHint, 0, 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return spannableString;
    }


}
