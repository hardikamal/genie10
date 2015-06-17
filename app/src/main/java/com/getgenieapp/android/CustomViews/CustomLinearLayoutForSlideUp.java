package com.getgenieapp.android.CustomViews;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

/**
 * Created by Raviteja on 6/16/2015.
 */
public class CustomLinearLayoutForSlideUp extends LinearLayout {

    public CustomLinearLayoutForSlideUp(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomLinearLayoutForSlideUp(Context context) {
        super(context);
    }

    private OnSoftKeyboardListener onSoftKeyboardListener;

    @Override
    protected void onMeasure(final int widthMeasureSpec, final int heightMeasureSpec) {
        if (onSoftKeyboardListener != null) {
            final int newSpec = MeasureSpec.getSize(heightMeasureSpec);
            final int oldSpec = getMeasuredHeight();
            if (oldSpec > newSpec) {
                onSoftKeyboardListener.onShown();
            } else {
                onSoftKeyboardListener.onHidden();
            }
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    public final void setOnSoftKeyboardListener(final OnSoftKeyboardListener listener) {
        this.onSoftKeyboardListener = listener;
    }

    public interface OnSoftKeyboardListener {
        public void onShown();
        public void onHidden();
    }

}