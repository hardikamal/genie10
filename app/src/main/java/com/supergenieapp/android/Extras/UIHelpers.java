package com.supergenieapp.android.Extras;

import android.app.Activity;
import android.content.Context;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.TypedValue;
import android.view.Display;
import android.widget.TextView;

public class UIHelpers {
    public Point getXYPixels(Activity activity) {
        Display display = activity.getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        return size;
    }

    public void correctWidth(TextView textView, Activity activity) {
        Paint paint = new Paint();
        Rect bounds = new Rect();

        paint.setTypeface(textView.getTypeface());
        float textSize = textView.getTextSize();
        paint.setTextSize(textSize);
        String text = textView.getText().toString();
        paint.getTextBounds(text, 0, text.length(), bounds);

        while (bounds.width() > 1080) {
            textSize--;
            paint.setTextSize(textSize);
            paint.getTextBounds(text, 0, text.length(), bounds);
        }
        textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
    }

    public int determineMaxTextSize(String str, float maxWidth)
    {
        int size = 0;
        Paint paint = new Paint();

        do {
            paint.setTextSize(++ size);
        } while(paint.measureText(str) < maxWidth);

        return size;
    }
}
