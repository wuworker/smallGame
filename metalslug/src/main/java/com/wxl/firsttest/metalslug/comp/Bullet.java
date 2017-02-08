package com.wxl.firsttest.metalslug.comp;

import android.graphics.Bitmap;

import com.wxl.firsttest.metalslug.GameManager;

/**
 * Created by Administrator on 2016/8/17.
 * 子弹类
 */
public class Bullet {

    public static final int BULLET_TYPE1=1;
    public static final int BULLET_TYPE2=2;
    public static final int BULLET_TYPE3=3;
    public static final int BULLET_TYPE4=4;
    public static final int BULLET_TYPE5=5;
    //子弹类型，坐标，方向
    private int x;
    private int y;
    private int type;
    private int dir;
    //子弹是否有效
    private boolean isEffect=true;
    //
    private int drawIndex=0;
    public int drawMaxIndex=GameManager.boomImg.length-1;

    public Bullet(int x,int y,int type,int dir){
        this.x=x;
        this.y=y;
        this.type=type;
        this.dir=dir;

    }

    //获取子弹图片
    public Bitmap getBulletBitmap(){
        switch (type){
            case BULLET_TYPE1:
                return GameManager.bulletImg[0];
            case BULLET_TYPE2:
                return GameManager.bulletImg[1];
            case BULLET_TYPE3:
                return GameManager.bulletImg[2];
            case BULLET_TYPE4:
                return GameManager.bulletImg[3];
            case BULLET_TYPE5:
                if(!isEffect){
                    if(drawIndex<drawMaxIndex) {
                        drawIndex++;
                        return GameManager.boomImg[drawIndex];
                    }
                    return null;
                }
                else
                    return GameManager.bulletImg[4];
            default:
                return null;
        }
    }

    //子弹移动速度
    public int speedX(){
        int sign= dir==Player.DIR_RIGHT ? 1 : -1;
        switch (type){
            case BULLET_TYPE1:
                return (int)(GameManager.imgScale*13)*sign;
            case BULLET_TYPE2:
                return (int)(GameManager.imgScale*8)*sign;
            case BULLET_TYPE3:
                return (int)(GameManager.imgScale*8)*sign;
            case BULLET_TYPE4:
                return (int)(GameManager.imgScale*8)*sign;
            default:
                return (int)(GameManager.imgScale*8)*sign;
        }
    }

    public int speedY(){

        return !isEffect ? 0 : (int)(GameManager.imgScale*5);
    }

    //子弹移动
    public void move(){
        if(type==BULLET_TYPE5) {
            y += speedY();
        }
        else
            x+=speedX();
    }

    public void setX(int x){
        this.x=x;
    }
    public int getX(){
        return x;
    }
    public int getY(){
        if(type==BULLET_TYPE5 && !isEffect)
            return GameManager.screenHeight*42/100;

        return y;
    }
    public int getEndX(){
        return getBulletBitmap().getWidth()+x;
    }
    public int getEndY(){
        return getBulletBitmap().getHeight()+y;
    }


    public int getDir(){
        return dir;
    }
    public int getType(){
        return type;
    }
    public void setEffect(boolean isEffect){
        this.isEffect=isEffect;
    }
    public boolean getEffect(){
        return isEffect;
    }
    public int getDrawIndex(){
        return drawIndex;
    }

}















