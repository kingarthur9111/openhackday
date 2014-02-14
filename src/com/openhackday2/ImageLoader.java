package com.openhackday2;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.content.AsyncTaskLoader;

public class ImageLoader extends AsyncTaskLoader<Bitmap> {
    /** 対象のアイテム. */
    public ImageItem item;
 
    /**
     * コンストラクタ.
     * @param context {@link Context}
     * @param item {@link ImageItem}
     */
    public ImageLoader(Context context, ImageItem item) {
        super(context);
        this.item = item;
    }
 
    @Override
    public Bitmap loadInBackground() {
    	if (item.name == "item1")
    		return BitmapFactory.decodeResource(getContext().getResources(), R.drawable.image);
    	else {
    		return BitmapFactory.decodeResource(getContext().getResources(), R.drawable.image2);
    	}
    }
}
