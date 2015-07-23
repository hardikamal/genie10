package com.supergenieapp.android.CustomViews.Button;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.supergenieapp.android.Extras.Utils;
import com.supergenieapp.android.R;

public class ButtonRectangle extends Button {
    public ButtonRectangle(Context context, AttributeSet attrs) {
        super(context, attrs);
        setDefaultProperties();
    }

    @Override
    protected void setDefaultProperties() {
        super.minWidth = 80;
        super.minHeight = 36;
        super.background = R.drawable.background_button_rectangle;
        super.setDefaultProperties();
    }


    // Set atributtes of XML to View
    protected void setAttributes(AttributeSet attrs) {

        //Set background Color
        // Color by resource
        int bacgroundColor = attrs.getAttributeResourceValue(ANDROIDXML, "background", -1);
        if (bacgroundColor != -1) {
            setBackgroundColor(getResources().getColor(bacgroundColor));
        } else {
            background = attrs.getAttributeIntValue(ANDROIDXML, "background", -1);
            if (background != -1)
                setBackgroundColor(background);
        }

        // Set Padding
        String value = attrs.getAttributeValue(ANDROIDXML, "padding");

        // Set text button
        String text = null;
        int textResource = attrs.getAttributeResourceValue(ANDROIDXML, "text", -1);
        if (textResource != -1) {
            text = getResources().getString(textResource);
        } else {
            text = attrs.getAttributeValue(ANDROIDXML, "text");
        }
        if (text != null) {
            textButton = new TextView(getContext());
            textButton.setText(text);
            textButton.setTextColor(Color.parseColor("#999999"));
            textButton.setTypeface(null, Typeface.BOLD);
            textButton.setTextSize(20);
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
            params.addRule(CENTER_IN_PARENT, TRUE);
            params.setMargins(Utils.dpToPx(5, getResources()), Utils.dpToPx(5, getResources()), Utils.dpToPx(5, getResources()), Utils.dpToPx(5, getResources()));
            textButton.setLayoutParams(params);
            addView(textButton);
            int textColor = attrs.getAttributeResourceValue(ANDROIDXML, "textColor", -1);
            if (textColor != -1) {
                textButton.setTextColor(textColor);
            } else {
                textColor = attrs.getAttributeIntValue(ANDROIDXML, "textColor", -1);
                if (textColor != -1)
                    textButton.setTextColor(textColor);
            }
            int[] array = {android.R.attr.textSize};
            TypedArray values = getContext().obtainStyledAttributes(attrs, array);
            values.recycle();

        }

        rippleSpeed = attrs.getAttributeFloatValue(MATERIALDESIGNXML,
                "rippleSpeed", Utils.dpToPx(6, getResources()));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (x != -1) {
            Rect src = new Rect(0, 0, getWidth() - Utils.dpToPx(6, getResources()), getHeight() - Utils.dpToPx(7, getResources()));
            Rect dst = new Rect(Utils.dpToPx(6, getResources()), Utils.dpToPx(6, getResources()), getWidth() - Utils.dpToPx(6, getResources()), getHeight() - Utils.dpToPx(7, getResources()));
            canvas.drawBitmap(makeCircle(), src, dst, null);
            invalidate();
        }
    }

}