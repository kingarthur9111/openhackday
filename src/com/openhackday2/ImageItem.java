package com.openhackday2;

import java.io.Serializable;

import android.graphics.Bitmap;

public class ImageItem implements Serializable {
    /** シリアルバージョン. */
    private static final long serialVersionUID = 1L;
    /** {@link Bitmap}. */
    public Bitmap bitmap;
    /** キー. */
    public String name;
    /** URL. */
    public String url;
}
