package com.wxl.firsttest.metalslug;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.util.Log;

import com.wxl.firsttest.metalslug.comp.MonsterManager;
import com.wxl.firsttest.metalslug.comp.MyGraphics;

import java.io.InputStream;

/**
 * Created by Administrator on 2016/8/13.
 * 游戏管理类
 * 管理图片加载和图片绘制的工具类
 */
public class GameManager {

    private static final String TAG="GameManager:";
    //游戏图片缩放比
    public static float imgScale=1f;
    //屏幕长宽
    public static int screenWidth;
    public static int screenHeight;
    //游戏地图
    public static Bitmap gameMap;
    public static int mapWidth;
    public static int mapHeight;
    //左上角角色信息
    public static Bitmap headTitleImg;
    public static Bitmap[] hpImg;
    //所有角色图片
    public static Bitmap[] legStandImg;
    public static Bitmap[] headStandImg;
    public static Bitmap[] legRunImg;
    public static Bitmap[] headRunImg;
    public static Bitmap[] legJumpImg;
    public static Bitmap[] headJumpImg;
    public static Bitmap[] headShootImg;
    public static Bitmap[] playDieImg;
    //怪物人1图片
    public static Bitmap[] monsterMan1Img;
    public static Bitmap[] monsterMan1DieImg;
    //怪物飞机1图片
    public static Bitmap[] monsterPlane1Img;
    public static Bitmap[] monsterPlane1DieImg;
    //子弹图片
    public static Bitmap[] bulletImg;
    //爆炸图
    public static Bitmap[] boomImg;
    //
    public static Bitmap[] bossImg;
    //
    public static boolean isMeetBoss=false;

    public static final float[][] CAN_JUMP_LOC={
            {0.695f,0.7276f,0.49f}
    };

    //初始化屏幕宽高
    public static void initScreen(int width,int height){
        screenWidth=width;
        screenHeight=height;
    }

    // 清除屏幕的方法
    public static void clearScreen(Canvas c, int color)
    {
        color = 0xff000000 | color;
        c.drawColor(color);
    }
    // 清除屏幕的方法
    public static void clearScreen(Canvas c)
    {
        c.drawColor(Color.BLACK);
    }
    //加载资源
    public static void loadResource(){

        //加载背景图片
        Bitmap temp=createBitmapById(MainActivity.res,R.drawable.map);
       // Bitmap temp=readBitMap(MainActivity.mainActivity,R.drawable.map);
        if(temp!=null && !temp.isRecycled()){
            //得到原始图片宽高和缩放比
            float width=temp.getWidth();
            float height=temp.getHeight();
            imgScale=(float)screenHeight/height;
            Log.d("GameManager:","原始图片:width="+width+",height="+height+",scale="+imgScale);
            //创建出符合屏幕尺寸的位图
            if(width!=0 && height!=0 && imgScale!=0){
                Matrix matrix=new Matrix();
                matrix.setScale(imgScale,imgScale);
                gameMap=Bitmap.createBitmap(temp,0,0,(int)width,(int)height,matrix,true);
                temp.recycle();
            }
            else{
                gameMap=temp;
            }
        }
        mapWidth=gameMap.getWidth();
        mapHeight=gameMap.getHeight();
        Log.d("GameManager:","背景图片:width="+mapWidth+",height="+mapHeight);

        //加载角色图片
        headTitleImg=createBitmapById(MainActivity.res,R.drawable.head,imgScale);
        hpImg=new Bitmap[3];
        hpImg[0]=createBitmapById(MainActivity.res,R.drawable.hp_img1,imgScale);
        hpImg[1]=createBitmapById(MainActivity.res,R.drawable.hp_img2,imgScale);
        hpImg[2]=createBitmapById(MainActivity.res,R.drawable.hp_img3,imgScale);
        //角色站立的图片
        legStandImg=new Bitmap[1];
        legStandImg[0]=createBitmapById(MainActivity.res,R.drawable.leg_stand,imgScale);
        headStandImg=new Bitmap[3];
        headStandImg[0]=createBitmapById(MainActivity.res,R.drawable.head_stand_1,imgScale);
        headStandImg[1]=createBitmapById(MainActivity.res,R.drawable.head_stand_2,imgScale);
        headStandImg[2]=createBitmapById(MainActivity.res,R.drawable.head_stand_3,imgScale);
        //角色跑动的图片
        legRunImg=new Bitmap[3];
        legRunImg[0]=createBitmapById(MainActivity.res,R.drawable.leg_run_1,imgScale);
        legRunImg[1]=createBitmapById(MainActivity.res,R.drawable.leg_run_2,imgScale);
        legRunImg[2]=createBitmapById(MainActivity.res,R.drawable.leg_run_3,imgScale);
        headRunImg=new Bitmap[3];
        headRunImg[0]=createBitmapById(MainActivity.res,R.drawable.head_run_1,imgScale);
        headRunImg[1]=createBitmapById(MainActivity.res,R.drawable.head_run_2,imgScale);
        headRunImg[2]=createBitmapById(MainActivity.res,R.drawable.head_run_3,imgScale);
        //角色跳动的图片
        legJumpImg=new Bitmap[5];
        legJumpImg[0]=createBitmapById(MainActivity.res,R.drawable.leg_jum_1,imgScale);
        legJumpImg[1]=createBitmapById(MainActivity.res,R.drawable.leg_jum_2,imgScale);
        legJumpImg[2]=createBitmapById(MainActivity.res,R.drawable.leg_jum_3,imgScale);
        legJumpImg[3]=createBitmapById(MainActivity.res,R.drawable.leg_jum_4,imgScale);
        legJumpImg[4]=createBitmapById(MainActivity.res,R.drawable.leg_jum_5,imgScale);
        headJumpImg=new Bitmap[5];
        headJumpImg[0]=createBitmapById(MainActivity.res,R.drawable.head_jump_1,imgScale);
        headJumpImg[1]=createBitmapById(MainActivity.res,R.drawable.head_jump_2,imgScale);
        headJumpImg[2]=createBitmapById(MainActivity.res,R.drawable.head_jump_3,imgScale);
        headJumpImg[3]=createBitmapById(MainActivity.res,R.drawable.head_jump_4,imgScale);
        headJumpImg[4]=createBitmapById(MainActivity.res,R.drawable.head_jump_5,imgScale);
        //角色射击的头部图片
        headShootImg=new Bitmap[6];
        headShootImg[0]=createBitmapById(MainActivity.res,R.drawable.head_shoot_1,imgScale);
        headShootImg[1]=createBitmapById(MainActivity.res,R.drawable.head_shoot_2,imgScale);
        headShootImg[2]=createBitmapById(MainActivity.res,R.drawable.head_shoot_3,imgScale);
        headShootImg[3]=createBitmapById(MainActivity.res,R.drawable.head_shoot_4,imgScale);
        headShootImg[4]=createBitmapById(MainActivity.res,R.drawable.head_shoot_5,imgScale);
        headShootImg[5]=createBitmapById(MainActivity.res,R.drawable.head_shoot_6,imgScale);
        //角色死亡图
        playDieImg=new Bitmap[11];
        playDieImg[0]=createBitmapById(MainActivity.res,R.drawable.player_die1,imgScale);
        playDieImg[1]=createBitmapById(MainActivity.res,R.drawable.player_die2,imgScale);
        playDieImg[2]=createBitmapById(MainActivity.res,R.drawable.player_die3,imgScale);
        playDieImg[3]=createBitmapById(MainActivity.res,R.drawable.player_die4,imgScale);
        playDieImg[4]=createBitmapById(MainActivity.res,R.drawable.player_die5,imgScale);
        playDieImg[5]=createBitmapById(MainActivity.res,R.drawable.player_die6,imgScale);
        playDieImg[6]=createBitmapById(MainActivity.res,R.drawable.player_die7,imgScale);
        playDieImg[7]=createBitmapById(MainActivity.res,R.drawable.player_die8,imgScale);
        playDieImg[8]=createBitmapById(MainActivity.res,R.drawable.player_die9,imgScale);
        playDieImg[9]=createBitmapById(MainActivity.res,R.drawable.player_die10,imgScale);
        playDieImg[10]=createBitmapById(MainActivity.res,R.drawable.player_die11,imgScale);
        //怪物人1图
        monsterMan1Img=new Bitmap[6];
        monsterMan1Img[0]=createBitmapById(MainActivity.res,R.drawable.monster_man1_1,imgScale);
        monsterMan1Img[1]=createBitmapById(MainActivity.res,R.drawable.monster_man1_2,imgScale);
        monsterMan1Img[2]=createBitmapById(MainActivity.res,R.drawable.monster_man1_3,imgScale);
        monsterMan1Img[3]=createBitmapById(MainActivity.res,R.drawable.monster_man1_4,imgScale);
        monsterMan1Img[4]=createBitmapById(MainActivity.res,R.drawable.monster_man1_5,imgScale);
        monsterMan1Img[5]=createBitmapById(MainActivity.res,R.drawable.monster_man1_6,imgScale);
        //怪物人1死亡图
        monsterMan1DieImg=new Bitmap[5];
        monsterMan1DieImg[0]=createBitmapById(MainActivity.res,R.drawable.man1_die1,imgScale);
        monsterMan1DieImg[1]=createBitmapById(MainActivity.res,R.drawable.man1_die2,imgScale);
        monsterMan1DieImg[2]=createBitmapById(MainActivity.res,R.drawable.man1_die3,imgScale);
        monsterMan1DieImg[3]=createBitmapById(MainActivity.res,R.drawable.man1_die4,imgScale);
        monsterMan1DieImg[4]=createBitmapById(MainActivity.res,R.drawable.die_last,imgScale);
        //怪物飞机1图
        monsterPlane1Img=new Bitmap[3];
        monsterPlane1Img[0]=createBitmapById(MainActivity.res,R.drawable.monster_plane1_1,imgScale);
        monsterPlane1Img[1]=createBitmapById(MainActivity.res,R.drawable.monster_plane1_2,imgScale);
        monsterPlane1Img[2]=createBitmapById(MainActivity.res,R.drawable.monster_plane1_3,imgScale);
        //怪物飞机1死亡图
        monsterPlane1DieImg=new Bitmap[4];
        monsterPlane1DieImg[0]=createBitmapById(MainActivity.res,R.drawable.plane1_die1,imgScale);
        monsterPlane1DieImg[1]=createBitmapById(MainActivity.res,R.drawable.plane1_die2,imgScale);
        monsterPlane1DieImg[2]=createBitmapById(MainActivity.res,R.drawable.plane1_die3,imgScale);
        monsterPlane1DieImg[3]=createBitmapById(MainActivity.res,R.drawable.die_last,imgScale);
        //子弹图片
        bulletImg=new Bitmap[5];
        bulletImg[0]=createBitmapById(MainActivity.res,R.drawable.bullet_1,imgScale);
        bulletImg[1]=createBitmapById(MainActivity.res,R.drawable.bullet_2,imgScale);
        bulletImg[2]=createBitmapById(MainActivity.res,R.drawable.bullet_3,imgScale);
        bulletImg[3]=createBitmapById(MainActivity.res,R.drawable.bullet_4,imgScale);
        bulletImg[4]=createBitmapById(MainActivity.res,R.drawable.bullet_5,imgScale);
        //
        bossImg=new Bitmap[1];
        bossImg[0]=createBitmapById(MainActivity.res,R.drawable.boss_img,imgScale);
        //爆炸图
        boomImg=new Bitmap[7];
        boomImg[0]=createBitmapById(MainActivity.res,R.drawable.boom0,imgScale);
        boomImg[1]=createBitmapById(MainActivity.res,R.drawable.boom1,imgScale);
        boomImg[2]=createBitmapById(MainActivity.res,R.drawable.boom2,imgScale);
        boomImg[3]=createBitmapById(MainActivity.res,R.drawable.boom3,imgScale);
        boomImg[4]=createBitmapById(MainActivity.res,R.drawable.boom4,imgScale);
        boomImg[5]=createBitmapById(MainActivity.res,R.drawable.boom5,imgScale);
        boomImg[6]=createBitmapById(MainActivity.res,R.drawable.boom6,imgScale);
    }

    // 绘制游戏界面的方法，该方法先绘制游戏背景地图，再绘制游戏角色，最后绘制所有怪物
    public static void drawGame(Canvas canvas){
        if(canvas==null){
            Log.d(TAG,"drawGame方法的Canvas为null");
            return;
        }
        if(gameMap!=null && !gameMap.isRecycled()){

            int width=mapWidth+GameView.player.getShift();
            if(width>=screenWidth) {
                isMeetBoss=false;
                MyGraphics.drawImage(canvas, gameMap, 0, 0, -GameView.player.getShift(), 0,
                        screenWidth, mapHeight);
            }
            else{
                isMeetBoss=true;
                MyGraphics.drawImage(canvas, gameMap, 0, 0, -GameView.player.getShift(), 0,
                        width, mapHeight);
                MyGraphics.drawImage(canvas, gameMap, width, 0, 0, 0,
                        screenWidth-width, mapHeight);
            }
        }

        MonsterManager.drawMonster(canvas);
        GameView.player.drawPlayer(canvas);
       // Log.d(TAG,"位移:"+GameView.player.getPlayerX());
    }

    //用这种方式读取大图防止内存溢出
    public static Bitmap readBitMap(Context context, int resId){
        BitmapFactory.Options opt = new BitmapFactory.Options();
        opt.inJustDecodeBounds = false;
        opt.inSampleSize=1;
        //获取资源图片
        InputStream is = context.getResources().openRawResource(resId);
        return BitmapFactory.decodeStream(is,null,opt);
    }

    //使用输入流节约内存，速度快
    public static Bitmap createBitmapById(Resources res,int id){

        InputStream is=null;
        try{
            is=res.openRawResource(id);
            Bitmap bitmap=BitmapFactory.decodeStream(is,null,null);
            return bitmap;
        } catch (Exception e){
            Log.d("GameManager:","执行createBitmapById错误");
            return null;
        }
        finally {
            if(is!=null) {
                try {
                    is.close();
                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
    }

    // 工具方法：根据图片id来获取实际的位图，并按scale进行缩放
    private static Bitmap createBitmapById(Resources res, int id, float scale){

        InputStream is=null;
        try{
            is=res.openRawResource(id);
            Bitmap bitmap=BitmapFactory.decodeStream(is,null,null);

            if(bitmap==null || bitmap.isRecycled()){
                Log.d("GameManager:","createBitmapById方法的Bitmap不存在");
                return null;
            }
            if(scale<=0 || scale ==1.0){
                return bitmap;
            }

            Matrix matrix=new Matrix();
            matrix.setScale(scale,scale);
            Bitmap newBitmap=Bitmap.createBitmap(bitmap,0,0,
                    bitmap.getWidth(),bitmap.getHeight(),matrix,true);

            if(!bitmap.isRecycled() && !bitmap.equals(newBitmap)){
                bitmap.recycle();
            }
            return newBitmap;

        } catch (Exception e){
            Log.d("GameManager:","执行createBitmapById错误");
            return null;
        }
        finally {
            if(is!=null){
                try {
                    is.close();
                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
    }


}

























