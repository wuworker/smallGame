package com.wxl.firsttest.metalslug.comp;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.Log;

/**
 * Created by Administrator on 2016/8/15.
 * 自定义画图工具类
 */
public class MyGraphics {

    private static final String TAG="MyGraphics:";

    //绘制包边字符串
    public static void drawBorderString(Canvas c,int borderColor,int textColor,
        String text,int x,int y,int borderWidth,Paint paint){

        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(borderWidth);
        paint.setColor(Color.rgb((borderColor & 0xff0000)>>16,(borderColor & 0x00ff00)>>8,
                (borderColor & 0x0000ff)));
        c.drawText(text,x,y,paint);
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.rgb((textColor & 0xff0000)>>16,(textColor & 0x00ff00)>>8,
                (textColor & 0x0000ff)));
        c.drawText(text,x,y,paint);
    }

    // 定义Android翻转参数的常量
    public static final int TRANS_NONE = 0;
    public static final int TRANS_ROT90 = 1;
    public static final int TRANS_ROT180 = 2;
    public static final int TRANS_ROT270 = 3;
    public static final int TRANS_MIRROR = 4;
    public static final int TRANS_MIRROR_ROT90 = 5;
    public static final int TRANS_MIRROR_ROT180 = 6;
    public static final int TRANS_MIRROR_ROT270 = 7;
    //每次缩放的梯度
    public static final float INTERVAL_SCALE=0.05f;
    //20表示不缩放
    public static final int TIMES_SCALE=20;

    //
    private static final Rect src=new Rect();
    private static final Rect dst=new Rect();
    private static final Path path=new Path();
    private static final RectF srcRect=new RectF();
    private static final float[] pts=new float[8];

    // 工具方法：绘制位图
    // 作用是将源位图image中左上角为srcX、srcY、宽width、高height的区域绘制到canvas上
    public synchronized static void drawImage(Canvas canvas, Bitmap image,
        int destX, int destY, int srcX, int srcY, int width, int height){

        if(canvas==null){
            Log.d(TAG,"drawImage方法的Canvas为null");
            return;
        }
        if(image==null || image.isRecycled()){
            Log.d(TAG,"drawImage方法的源位图不存在");
            return;
        }

        if(srcX==0 && srcY==0 && image.getWidth()<=width
                && image.getHeight()<=height){
            canvas.drawBitmap(image,destX,destY,null);
        }
        src.left=srcX;
        src.top=srcY;
        src.right=srcX+width;
        src.bottom=srcY+height;
        dst.left=destX;
        dst.top=destY;
        dst.right=destX+width;
        dst.bottom=destY+height;
        canvas.drawBitmap(image,src,dst,null);
    }

    // 用于从源位图src中的srcX、srcY点开始、挖取宽width、高height的区域，并对该图片进行trans变换、
    // 缩放scale（当scale为20时表示不缩放）、并旋转degree角度后绘制到Canvas的drawX、drawY处。
    public synchronized static void drawMatrixImage(Canvas canvas, Bitmap src,
        int srcX, int srcY, int width, int height, int trans, int drawX,
        int drawY, int degree, int scale){

        if(canvas==null){
            Log.d(TAG,"drawMatrixImage方法的Canvas为null");
            return;
        }
        if(src==null || src.isRecycled()){
            Log.d(TAG,"drawMatrixImage方法的源位图不存在");
            return;
        }
        //要截取的长宽不能超过源位图
        int srcWidth=src.getWidth();
        int srcHeight=src.getHeight();
        if(srcX+width > srcWidth){
            width=srcWidth-srcX;
        }
        if(srcY+height > srcHeight){
            height=srcHeight-srcY;
        }
        if(width<=0 || height<=0){
            Log.d(TAG,"drawMatrixImage方法中要截取的长宽不能小于0");
            return;
        }
        //设置缩放因子，进行变换
        int scaleX=scale;
        int scaleY=scale;
        int rotate=0;
        switch (trans){
            case TRANS_ROT90:
                rotate=90;
                break;
            case TRANS_ROT180:
                rotate=180;
                break;
            case TRANS_ROT270:
                rotate=270;
                break;
            case TRANS_MIRROR:
                scaleX=-scale;
                break;
            case TRANS_MIRROR_ROT90:
                scaleX=-scale;
                rotate=90;
                break;
            case TRANS_MIRROR_ROT180:
                scaleX=-scale;
                rotate=180;
                break;
            case TRANS_MIRROR_ROT270:
                scaleX=-scale;
                rotate=270;
                break;
            default:
                break;
        }
        //无缩放，无旋转
        if(rotate==0 && degree==0
                && scaleX==TIMES_SCALE){
            drawImage(canvas,src,drawX,drawY,srcX,srcY,width,height);
        }
        else{
            Matrix matrix=new Matrix();
            matrix.postScale(scaleX*INTERVAL_SCALE,scaleY*INTERVAL_SCALE);
            matrix.postRotate(rotate);
            matrix.postRotate(degree);
            srcRect.set(srcX, srcY, srcX + width, srcY + height);
            matrix.mapRect(srcRect);
            matrix.postTranslate(drawX - srcRect.left, drawY - srcRect.top);

            pts[0]=srcX;
            pts[1]=srcY;
            pts[2]=srcX+width;
            pts[3]=srcY;
            pts[4]=srcX+width;
            pts[5]=srcY+height;
            pts[6]=srcX;
            pts[7]=srcY+height;

            matrix.mapPoints(pts);

            canvas.save();
            path.reset();
            path.moveTo(pts[0], pts[1]);
            path.lineTo(pts[2], pts[3]);
            path.lineTo(pts[4], pts[5]);
            path.lineTo(pts[6], pts[7]);
            path.close();
            canvas.clipPath(path);
            canvas.drawBitmap(src, matrix, null);
            canvas.restore();
        }


    }
}












