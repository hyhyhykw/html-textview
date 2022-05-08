package org.sufficientlysecure.htmltextview;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

/**
 * Created time : 2022/5/8 12:16.
 *
 * @author 10585
 */
public class UrlDrawable extends BitmapDrawable {
    public Drawable drawable;
    public UrlDrawable(){
        super(null,(Bitmap)null);
    }

    @Override
    public void draw(Canvas canvas) {
        // override the draw to facilitate refresh function later
        if (drawable != null) {
            drawable.draw(canvas);
        }
    }
}