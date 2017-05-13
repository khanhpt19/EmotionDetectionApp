package com.example.khanh.emotiondetection;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import com.microsoft.projectoxford.emotion.contract.FaceRectangle;

/**
 * Created by khanh on 4/17/2017.
 */

public class ImageHelper {
    public static Bitmap drawRectOnBitmap(Bitmap mbitmap, FaceRectangle faceRectangle,String status){
        Bitmap bitmap=mbitmap.copy(Bitmap.Config.ARGB_8888,true);
        Canvas canvas=new Canvas(bitmap);

        Paint paint=new Paint();
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(5);
        paint.setColor(Color.GREEN);

        canvas.drawRect(faceRectangle.left,
                faceRectangle.top,
                faceRectangle.left+faceRectangle.width,
                faceRectangle.top+faceRectangle.height,
                paint);

        int cx=faceRectangle.left+faceRectangle.width;
        int cy=faceRectangle.top+faceRectangle.height;
        drawOnBitmap(canvas,30,cx/2+cx/5,cy+50,Color.GREEN,status);

        return bitmap;
    }

    private static void drawOnBitmap(Canvas canvas, int textsize, int cx, int cy, int color, String status) {
        Paint paint=new Paint();
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(color);
        paint.setTextSize(textsize);

        canvas.drawText(status,cx,cy,paint);
    }
}
