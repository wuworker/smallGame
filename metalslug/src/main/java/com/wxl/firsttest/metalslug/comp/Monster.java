package com.wxl.firsttest.metalslug.comp;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;

import com.wxl.firsttest.metalslug.GameManager;
import com.wxl.firsttest.metalslug.GameView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/8/23 0023.
 * 怪物类
 */
public class Monster {

    private static final String TAG="Monster:";

    public static final int MOSTER_TYPE_MAN_1=1;
    public static final int MOSTER_TYPE_PLANE=2;
    public static final int MOSTER_TYPE_BOSS=3;

    //怪物类型
    private int type;
    //怪物初始位置(左下角)
    private int monsterX;
    private int monsterY;
    //怪物大小
    private int startX;
    private int startY;
    private int endX;
    private int endY;
    //怪物宽度
    private int monsterWidth;
    //是否死亡
    private boolean isDie;
    //死亡绘制帧数
    private int dieDrawCount=Integer.MAX_VALUE;
    //动画刷新速度
    private int drawCount;
    //当前正在绘制第几针
    private int drawIndex;
    //保存所有的子弹
    private List<Bullet> bulletList=new ArrayList<>();
    //怪物血量
    private int hp;

    public Monster(int x,int y,int type){
        this.type=type;
        monsterX=x;
        monsterY=y;
        switch (type) {
            case MOSTER_TYPE_MAN_1:
                int[] drawCounts = new int[]{0, 1, 4, 5};
                drawCount = drawCounts[(int) (Math.random() * 4)];
                hp=3;
                break;
            case MOSTER_TYPE_PLANE:
                drawCount=(int) (Math.random() * 3);
                hp=1;
                break;
            case MOSTER_TYPE_BOSS:
                hp=100;
                break;
            default:break;
        }
    }

    //画怪物
    public void drawMonster(Canvas canvas){

        if(monsterX>GameManager.screenWidth || monsterX+monsterWidth<0){
            drawBullet(canvas);
            return;
        }
        if(type==MOSTER_TYPE_PLANE && !isDie)
            monsterX-=(int)(GameManager.imgScale*8);
        if(canvas==null){
            Log.d(TAG,"drawMonster方法的canvas为null");
            return;
        }

        switch (type){
            case MOSTER_TYPE_MAN_1:
                drawAni(canvas,isDie? GameManager.monsterMan1DieImg : GameManager.monsterMan1Img);
                break;
            case MOSTER_TYPE_PLANE:
                drawAni(canvas,isDie ? GameManager.monsterPlane1DieImg : GameManager.monsterPlane1Img);
                break;
            case MOSTER_TYPE_BOSS:
                drawBoss(canvas,GameManager.bossImg[0],1);
                break;
            default:break;
        }
    }

    public void drawBoss(Canvas canvas,Bitmap bitmap,int action){

        int drawX=monsterX;
        int drawY=monsterY-bitmap.getHeight();

        startX=drawX;
        startY=drawY;
        endX=startX+bitmap.getWidth();
        endY=startY+bitmap.getHeight();

        MyGraphics.drawMatrixImage(canvas,bitmap,0,0,bitmap.getWidth(),bitmap.getHeight(),
                MyGraphics.TRANS_NONE,drawX,drawY,0,MyGraphics.TIMES_SCALE);
    }


    //画一帧
    public void drawAni(Canvas canvas, Bitmap[] bitmaps){

        if(canvas==null){
            Log.d(TAG,"drawAni方法的canvas为null");
            return;
        }

        if(bitmaps==null){
            Log.d(TAG,"drawAni方法的图片为null");
            return;
        }
        //如果刚刚死亡
        if(isDie && dieDrawCount==Integer.MAX_VALUE){
            dieDrawCount=bitmaps.length;
            drawIndex=0;
        }

        drawIndex=drawIndex % bitmaps.length;

        if(dieDrawCount==0)
            drawIndex=bitmaps.length-1;

        Bitmap bitmap=bitmaps[drawIndex];

        if(bitmap==null || bitmap.isRecycled()){
            Log.d(TAG,"drawAni方法的图片为null");
            return;
        }

        int drawX=monsterX;
        int drawY=monsterY-bitmap.getHeight();
        monsterWidth=bitmap.getWidth();
        MyGraphics.drawMatrixImage(canvas,bitmap,0,0,bitmap.getWidth(),bitmap.getHeight(),
                MyGraphics.TRANS_NONE,drawX,drawY,0,MyGraphics.TIMES_SCALE);

        startX=drawX;
        startY=drawY;
        endX=startX+bitmap.getWidth();
        endY=startY+bitmap.getHeight();
        drawCount++;

        switch (type) {
            case MOSTER_TYPE_MAN_1:
                if (!isDie) {
                    if (drawIndex == 1) {
                        if (drawCount >= 20) {
                            drawIndex++;
                            drawCount = 0;
                        }
                    } else if (drawCount >= 5) {
                        if (drawIndex == 2) {
                            addBullet();
                        }
                        drawIndex++;
                        drawCount = 0;
                    }
                } else {
                    if (drawCount >= 5) {
                        drawCount = 0;
                        drawIndex++;
                        if(dieDrawCount>0)
                            dieDrawCount--;
                    }

                }
                break;
            case MOSTER_TYPE_PLANE:
                if(drawCount>=5){
                    drawCount=0;
                    drawIndex++;
                    if(drawIndex==1 && !isDie)
                        addBullet();
                }
                if(isDie && dieDrawCount>0){
                        dieDrawCount--;
                }
                break;
            default:break;
        }

        drawBullet(canvas);
    }

    //得到子弹类型
    public int getBulletType(){
        switch (type){
            case MOSTER_TYPE_MAN_1:
                return Bullet.BULLET_TYPE2;
            case MOSTER_TYPE_PLANE:
                return Bullet.BULLET_TYPE5;
        }
        return Bullet.BULLET_TYPE2;
    }

    //发射子弹
    public void addBullet(){

        int bulletType=getBulletType();
        int drawX=0;
        int drawY=0;
        switch (type){
            case MOSTER_TYPE_MAN_1:
                drawX=monsterX;
                drawY=monsterY-(int)(GameManager.imgScale*40);
                break;
            case MOSTER_TYPE_PLANE:
                drawX=monsterX+(int)(GameManager.imgScale*20);
                drawY=monsterY;
                break;
            default:break;
        }

        Bullet bullet=new Bullet(drawX,drawY,bulletType,Player.DIR_LEFT);

        bulletList.add(bullet);
    }

    //画子弹
    public void drawBullet(Canvas canvas){
        List<Bullet> delList=new ArrayList<>();
        Bullet bullet;
        //得到越界的子弹
        for(int i=0;i<bulletList.size();i++){
            bullet=bulletList.get(i);
            if(bullet==null)
                continue;
            if(bullet.getX()<0 || bullet.getX()>GameManager.screenWidth){
                delList.add(bullet);
            }
            else if(bullet.getY()>GameManager.screenHeight*65/100
                    && bullet.getType()==Bullet.BULLET_TYPE5) {
                    bullet.setEffect(false);
            }
            if(bullet.getType()==Bullet.BULLET_TYPE5
                    && bullet.getDrawIndex()>=bullet.drawMaxIndex){
                delList.add(bullet);
            }
        }
        Bitmap bitmap;
        //清除越界的子弹
        bulletList.removeAll(delList);
        //画屏幕内的子弹
        for(int i=0;i<bulletList.size();i++){

            bullet=bulletList.get(i);
            if(bullet==null)
                continue;

            bitmap=bullet.getBulletBitmap();
            if(bitmap==null || bitmap.isRecycled()){
                continue;
            }
            bullet.move();
            MyGraphics.drawMatrixImage(canvas, bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(),
                    bullet.getDir() == Player.DIR_RIGHT ? MyGraphics.TRANS_MIRROR : MyGraphics.TRANS_NONE
                    , bullet.getX(), bullet.getY(), 0, MyGraphics.TIMES_SCALE);
        }
    }

    //更新位置
    public void updataShift(int shift){
        monsterX-=shift;
        for(Bullet bullet:bulletList){
            if(bullet==null)
                continue;
            bullet.setX(bullet.getX()-shift);
        }
    }

    //判断是否被打中
    public boolean isHurt(int x,int y){
        if( x>=startX && x<=endX && y>=startY && y<=endY){
            hp--;
            if(hp<=0)
                isDie=true;
            return true;
        }

        return false;
    }

    //判断是否打中玩家
    public void checkBulletToPlayer(){
        List<Bullet> delBulletList=new ArrayList<>();
        for(Bullet bullet:bulletList){
            if(bullet==null || !bullet.getEffect()){
                continue;
            }
            if(GameView.player.isHurt(bullet.getX(),bullet.getEndY())
                    ||GameView.player.isHurt(bullet.getEndX(),bullet.getEndY())){
                bullet.setEffect(false);

                Message meg=new Message();
                meg.what=GameView.MESSAGE_HP;
                Bundle bundle=new Bundle();
                if(bullet.getType()==Bullet.BULLET_TYPE5)
                    bundle.putInt("HP",100);
                else
                    bundle.putInt("HP",50);
                meg.setData(bundle);

                GameView.changeHandler.sendMessage(meg);

                if(!(bullet.getType()==Bullet.BULLET_TYPE5 && bullet.getDrawIndex()<bullet.drawMaxIndex))
                    delBulletList.add(bullet);
            }
        }
        bulletList.removeAll(delBulletList);
    }

    public int getX(){
        return monsterX;
    }

    public boolean getDie(){
        return isDie;
    }
    public int getDieDrawCount(){
        return dieDrawCount;
    }
    public int getEndX(){
        return endX;
    }
    public List<Bullet> getBulletList(){
        return bulletList;
    }

}





















