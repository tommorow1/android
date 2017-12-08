package com.example.bloold.buildp.common;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.ByteArrayOutputStream;

/**
 * Created by Leonov Oleg, http://pandorika-it.com
 */

public class ImageUtils {

    private static final int MAX_IMG_SIZE=1600;
    /*** Изменяем размеры изображения (выполнять в отдельном потоке) */
    public static byte[] compressPhoto(final String imagePath)
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        Bitmap bm = BitmapFactory.decodeFile(imagePath, bmOptions);
        if(bm!=null) {
            bm=scaleDown(bm, MAX_IMG_SIZE, true);
            bm.compress(Bitmap.CompressFormat.PNG, 60, baos); //bm is the bitmap object
            final byte[] bytes = baos.toByteArray();
            bm.recycle();
            return bytes;
        }
        return null;
    }

    private static Bitmap scaleDown(Bitmap realImage, float maxImageSize, boolean filter) {
        float ratio = Math.min(
                maxImageSize / realImage.getWidth(),
                maxImageSize / realImage.getHeight());
        if(ratio>=1) return realImage;
        int width = Math.round(ratio * realImage.getWidth());
        int height = Math.round(ratio * realImage.getHeight());

        Bitmap newBitmap = Bitmap.createScaledBitmap(realImage, width, height, filter);
        if(newBitmap!=realImage) realImage.recycle();
        return newBitmap;
    }
}
