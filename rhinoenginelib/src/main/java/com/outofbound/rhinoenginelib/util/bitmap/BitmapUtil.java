package com.outofbound.rhinoenginelib.util.bitmap;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;

import java.io.IOException;

public class BitmapUtil {

    public static Bitmap getBitmapFromAsset(Context context, String fileName){
        AssetManager assetManager = context.getAssets();
        try {
            return rotate(BitmapFactory.decodeStream(assetManager.open(fileName)));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static Bitmap rotate(Bitmap bitmap){
        Matrix matrix = new Matrix();
        matrix.postRotate(180);
        return Bitmap.createBitmap(bitmap,0,0,bitmap.getWidth(),bitmap.getHeight(),matrix,true);
    }
}
