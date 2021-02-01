package com.outofbound.rhinoenginelib.util.bitmap;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.IOException;

public class BitmapUtil {

    public static Bitmap getBitmapFromAsset(Context context, String fileName){
        AssetManager assetManager = context.getAssets();
        try {
            return BitmapFactory.decodeStream(assetManager.open(fileName));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
