package com.wxl.firsttest.metalslug.comp;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.wxl.firsttest.metalslug.GameManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/8/13.
 * 合金弹头的玩家类
 */
public class Player {

    private static final String TAG="Player:";
    //角色最大生命
    public static final int HP_MAX=300;
    //角色的朝向
    public static final int DIR_RIGHT=1;
    public static final int DIR_LEFT=2;
    //角色动作
    public static final int ACTION_STAND_RIGHT=1;
    public static final int ACTION_STAND_LEFT=2;
    public static final int ACTION_RUN_RIGHT=3;
    public static final int ACTION_RUN_LEFT=4;
    public static final int ACTION_JUMP_RIGHT=5;
    public static final int ACTION_JUMP_LEFT=6;
    //设置角色移动
    public static final int MOVE_STAND=0;
    public static final int MOVE_RIGHT=1;
    public static final int MOVE_LEFT=2;
    //射击间隔
    public static final int MAX_SHOOT_TIME=10;
    //当前是否正在射击
    private int shootTime;
    private boolean isShoot;
    //角色的所有子弹
    private List<Bullet> bulletList=new ArrayList<>();
    //角色初始坐标
    public static int X_DEFAULT;
    public static int Y_DEFAULT;
    public static int JUMP_MAX;
    //名字，HP
    private String name;
    private int hp;
    //角色当前位移,左下角
    private int playerX;
    private int playerY;
    //当前动作
    private int action=ACTION_STAND_RIGHT;
    //设置当前移动
    private int move=MOVE_STAND;
    //跳动
    private boolean isJump;
    private int isJumpCount;
    private boolean isJumpMax;
    //当前绘制到第几帧
    private int indexLeg;
    private int indexHead;
    private int indexDie;
    //当前头部坐标,左上角
    private int cHeadX;
    private int cHeadY;
    //当前绘制的图片
    private Bitmap cLegBitmap;
    private Bitmap cHeadBitmap;
    //角色动画刷新速度
    private int drawCount;
    //游戏是否要结束
    private boolean isOver;

    public Player(String name,int hp){
        this.name=name;
        this.hp=hp;
        initPosition();
    }

    //初始化角色位置
    public void initPosition(){
        playerX=GameManager.screenWidth*15/100;
        playerY=GameManager.screenHeight*75/100;
        X_DEFAULT=playerX;
        Y_DEFAULT=playerY;
        JUMP_MAX=GameManager.screenHeight*45/100;
    }
    //得到角色朝向
    public int getDir(){
        if(action % 2==1)
            return DIR_RIGHT;
        else
            return DIR_LEFT;
    }
    //得到角色位移
    public int getShift(){
        if(!GameManager.isMeetBoss) {
            if (playerX < 0 || playerY < 0) {
                initPosition();
            }
            return X_DEFAULT - playerX;
        }
        return GameManager.screenWidth*15/100-playerX;
    }

    //设置角色移动
    public void move(){
        if(!GameManager.isMeetBoss) {
            switch (move) {
                case MOVE_RIGHT:
                    MonsterManager.updatePosistion((int) (GameManager.imgScale * 7));
                    setPlayerX(getPlayerX() + (int) (GameManager.imgScale * 7));
                    if (!isJump)
                        setAction(ACTION_RUN_RIGHT);
                    break;
                case MOVE_LEFT:
                    if (getPlayerX() > X_DEFAULT)
                        MonsterManager.updatePosistion(-(int) (GameManager.imgScale * 7));
                    setPlayerX(getPlayerX() - (int) (GameManager.imgScale * 7));
                    if (!isJump)
                        setAction(ACTION_RUN_LEFT);
                    break;
                case MOVE_STAND:
                    if (getAction() != ACTION_JUMP_LEFT && getAction() != ACTION_JUMP_RIGHT) {
                        if (!isJump)
                            setAction(ACTION_STAND_RIGHT);
                    }
                    break;
                default:
                    break;
            }
        }
        else{
            switch (move) {
                case MOVE_RIGHT:
                    if(X_DEFAULT+cHeadBitmap.getWidth()<=GameManager.screenWidth) {
                        X_DEFAULT += (int) (GameManager.imgScale * 7);
                        updateBulletShift((int) (GameManager.imgScale * 7));
                    }
                    if (!isJump)
                        setAction(ACTION_RUN_RIGHT);
                    break;
                case MOVE_LEFT:
                    if (X_DEFAULT>=0) {
                        X_DEFAULT -= (int) (GameManager.imgScale * 7);
                        updateBulletShift(-(int) (GameManager.imgScale * 7));
                    }
                    if (!isJump)
                        setAction(ACTION_RUN_LEFT);
                    break;
                case MOVE_STAND:
                    if (getAction() != ACTION_JUMP_LEFT && getAction() != ACTION_JUMP_RIGHT) {
                        if (!isJump)
                            setAction(ACTION_STAND_RIGHT);
                    }
                    break;
                default:
                    break;
            }
        }
    }
    //设置角色移动和跳跃
    public void moveAndJump(){

        if(!isJump){
            move();
            addBullet();
            return;
        }

        if(!isJumpMax){

            setAction(getDir()==DIR_LEFT ? ACTION_JUMP_LEFT:ACTION_JUMP_RIGHT);
            setPlayerY(getPlayerY()-(int)(GameManager.imgScale*11));
            if(getPlayerY()<=JUMP_MAX){
                isJumpMax=true;
            }
        }
        else{
            isJumpCount--;
            if(isJumpCount<=0){
                setPlayerY(getPlayerY()+(int)(GameManager.imgScale*11));
                //已经落到最低
                if(getPlayerY()>=Y_DEFAULT){
                    setPlayerY(Y_DEFAULT);
                    setAction(getDir() == DIR_LEFT ? ACTION_STAND_LEFT:ACTION_STAND_RIGHT);
                    isJump=false;
                    isJumpMax=false;
                }
                else{
                    setAction(getDir()==DIR_LEFT ? ACTION_JUMP_LEFT:ACTION_JUMP_RIGHT);
                }
            }
        }

        move();
        addBullet();
    }



    //画角色
    public void drawPlayer(Canvas canvas){
        if(canvas==null){
            Log.d(TAG, "drawPlayer方法的Canvas为null");
            return;
        }
        if(isDie()){
            drawDie(canvas,GameManager.playDieImg,getDir());
        }
        else {
            switch (action) {
                case ACTION_STAND_RIGHT:
                    drawAni(canvas, GameManager.legStandImg, GameManager.headStandImg, DIR_RIGHT);
                    break;
                case ACTION_STAND_LEFT:
                    drawAni(canvas, GameManager.legStandImg, GameManager.headStandImg, DIR_LEFT);
                    break;
                case ACTION_RUN_RIGHT:
                    drawAni(canvas, GameManager.legRunImg, GameManager.headRunImg, DIR_RIGHT);
                    break;
                case ACTION_RUN_LEFT:
                    drawAni(canvas, GameManager.legRunImg, GameManager.headRunImg, DIR_LEFT);
                    break;
                case ACTION_JUMP_RIGHT:
                    drawAni(canvas, GameManager.legJumpImg, GameManager.headJumpImg, DIR_RIGHT);
                    break;
                case ACTION_JUMP_LEFT:
                    drawAni(canvas, GameManager.legJumpImg, GameManager.headJumpImg, DIR_LEFT);
                    break;
                default:
                    break;
            }
        }
    }

    //绘制角色动画帧
    public void drawAni(Canvas canvas, Bitmap[] leg,Bitmap head[],int dir){

        if(canvas==null){
            Log.d(TAG,"drawAni方法的Canvas为null");
            return;
        }
        if(leg==null){
            Log.d(TAG,"drawAni方法的腿部图为null");
            return;
        }

        Bitmap[] headArr=head;
        if(shootTime>0){
            headArr=GameManager.headShootImg;
            shootTime--;
        }

        if(headArr==null){
            Log.d(TAG,"drawAni方法的头部图为null");
            return;
        }
        //如果绘制到底，重新开始绘制
        indexLeg=indexLeg % leg.length;
        indexHead=indexHead % headArr.length;
        //是否要翻转
        int trans= dir==DIR_LEFT ? MyGraphics.TRANS_NONE : MyGraphics.TRANS_MIRROR;

        Bitmap bitmap1=leg[indexLeg];
        if(bitmap1==null || bitmap1.isRecycled()){
            Log.d(TAG,"drawAni方法的腿部图不存在");
            return;
        }
        Bitmap bitmap2=headArr[indexHead];
        if(bitmap2==null || bitmap2.isRecycled()){
            Log.d(TAG,"drawAni方法的头部图不存在");
            return;
        }
        //先画脚

        int x=X_DEFAULT;
        int y=playerY-bitmap1.getHeight();

        MyGraphics.drawMatrixImage(canvas,bitmap1,0,0,bitmap1.getWidth(),bitmap1.getHeight(),
                trans,x,y,0,MyGraphics.TIMES_SCALE);
        //再画头
        //图片居中
        switch (action){
            case ACTION_STAND_RIGHT:
                x+=0;
                y=y-bitmap2.getHeight()+(int)(GameManager.imgScale*14);
                break;
            case ACTION_STAND_LEFT:
                x-=bitmap2.getWidth()-bitmap1.getWidth();
                y=y-bitmap2.getHeight()+(int)(GameManager.imgScale*14);
                break;
            case ACTION_RUN_RIGHT:
                x+=0;
                y=y-bitmap2.getHeight()+(int)(GameManager.imgScale*15);
                break;
            case ACTION_RUN_LEFT:
                x-=bitmap2.getWidth()-bitmap1.getWidth();
                y=y-bitmap2.getHeight()+(int)(GameManager.imgScale*15);
                break;
            case ACTION_JUMP_RIGHT:
                x=x-((bitmap2.getWidth()-bitmap1.getWidth())>>1)-(int)(GameManager.imgScale*5);
                y=y-bitmap2.getHeight()+(int)(GameManager.imgScale*7.5);
                break;
            case ACTION_JUMP_LEFT:
                x=x-((bitmap2.getWidth()-bitmap1.getWidth())>>1)+(int)(GameManager.imgScale*5);
                y=y-bitmap2.getHeight()+(int)(GameManager.imgScale*7.5);
                break;
            default:
                break;
        }

        MyGraphics.drawMatrixImage(canvas,bitmap2,0,0,bitmap2.getWidth(),bitmap2.getHeight(),
                trans,x,y,0,MyGraphics.TIMES_SCALE);

        cHeadX=x;
        cHeadY=y;
        cLegBitmap=bitmap1;
        cHeadBitmap=bitmap2;

        drawCount++;
        if(drawCount>=3){
            drawCount=0;
            indexLeg++;
            indexHead++;
        }
        //画子弹
        drawBullet(canvas);
    }

    //绘制角色死亡图
    public void drawDie(Canvas canvas,Bitmap[] bitmaps,int dir){
        if(canvas==null){
            Log.d(TAG,"drawDie方法的Canvas为null");
            return;
        }
        if(bitmaps==null){
            Log.d(TAG,"drawDie方法的腿部图为null");
            return;
        }

        Bitmap bitmap=bitmaps[indexDie];
        int trans= dir==DIR_LEFT ? MyGraphics.TRANS_NONE : MyGraphics.TRANS_MIRROR;
        int x=X_DEFAULT;
        int y=playerY-bitmap.getHeight();

        MyGraphics.drawMatrixImage(canvas,bitmap,0,0,bitmap.getWidth(),bitmap.getHeight(),
                trans,x,y,0,MyGraphics.TIMES_SCALE);

        drawCount++;
        if(drawCount>=3){
            drawCount=0;
            indexDie++;
            if(indexDie>=GameManager.playDieImg.length){
                isOver=true;
                indexDie=GameManager.playDieImg.length-1;
            }
        }

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
                    bullet.getDir() == Player.DIR_RIGHT ? MyGraphics.TRANS_NONE : MyGraphics.TRANS_MIRROR
                    , bullet.getX(), bullet.getY(), 0, MyGraphics.TIMES_SCALE);
        }
    }
    //发射子弹
    public void addBullet(){
        if(shootTime<=0 && isShoot) {
            int x = getDir() == DIR_RIGHT ?
                    X_DEFAULT + (int) (GameManager.imgScale * 50) : X_DEFAULT - (int) (GameManager.imgScale * 50);
            int y = playerY - (int) (GameManager.imgScale * 60);
            Bullet bullet = new Bullet(x, y, Bullet.BULLET_TYPE1, getDir());
            bulletList.add(bullet);
            shootTime = MAX_SHOOT_TIME;
        }
    }

    //更新子弹位置
    public void updateBulletShift(int shift){
        for(Bullet bullet :bulletList){
            if(bullet==null)
                continue;
            bullet.setX(bullet.getX()+shift);
        }
    }

    //是否被子弹打中
    public boolean isHurt(int x,int y){
        return x>=cHeadX && x<=cHeadX+cHeadBitmap.getWidth() && y>=cHeadY && y<=playerY;
    }

    //角色左上角信息
    public class PlayerTitle{

        //主布局
        private RelativeLayout relativeLayout;
        //图片
        private ImageView headImg;
        //血量
        private ImageView hpImg[];
        //名字
        private TextView nameText;

        public PlayerTitle(Context context){

            relativeLayout=new RelativeLayout(context);
            relativeLayout.setLayoutParams(new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            //添加图片
            headImg=new ImageView(context);
            int headId= View.generateViewId();
            headImg.setId(headId);
            headImg.setScaleType(ImageView.ScaleType.FIT_CENTER);
            Matrix matrix=new Matrix();
            matrix.setScale(-1,1);
            Bitmap bitmap=Bitmap.createBitmap(GameManager.headTitleImg,0,0,
                    GameManager.headTitleImg.getWidth(),GameManager.headTitleImg.getHeight(),
                    matrix,true);
            headImg.setImageBitmap(bitmap);
            RelativeLayout.LayoutParams params=new RelativeLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.addRule(RelativeLayout.ALIGN_PARENT_START);
            params.addRule(RelativeLayout.ALIGN_PARENT_TOP);
            params.setMargins((int) (GameManager.imgScale * 10), (int) (GameManager.imgScale * 14), 0, 0);
            relativeLayout.addView(headImg,params);
            //添加名字
            nameText=new TextView(context);
            int nameId=View.generateViewId();
            nameText.setId(nameId);
            nameText.setText(Player.this.name);
            nameText.setTextSize(14);
            nameText.setTextColor(Color.YELLOW);
            params=new RelativeLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.addRule(RelativeLayout.END_OF,headId);
            params.addRule(RelativeLayout.ALIGN_PARENT_TOP);
            params.setMargins((int) (GameManager.imgScale * 10), (int) (GameManager.imgScale * 5), 0, 0);
            relativeLayout.addView(nameText,params);
            //添加血量
            //血量字
            hpImg=new ImageView[3];
            int[] hpid=new int[3];
            for(int i=0;i<hpImg.length;i++){
                hpImg[i]=new ImageView(context);
                hpImg[i].setScaleType(ImageView.ScaleType.FIT_CENTER);
                hpImg[i].setImageBitmap(GameManager.hpImg[0]);
                if(i==0) {
                    hpid[i] = headId;
                }else{
                    hpid[i]=View.generateViewId();
                    hpImg[i-1].setId(hpid[i]);
                }
                params=new RelativeLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                params.addRule(RelativeLayout.END_OF,hpid[i]);
                params.addRule(RelativeLayout.BELOW,nameId);
                params.setMargins((int) (GameManager.imgScale * 5), (int) (GameManager.imgScale * 5), 0, 0);
                relativeLayout.addView(hpImg[i],params);
            }
        }

        public RelativeLayout getPlayerTitleLayout(){
            return relativeLayout;
        }

        public void setHp(int hp){
            switch (hp){
                case 300:
                    hpImg[2].setImageBitmap(GameManager.hpImg[0]);
                    hpImg[1].setImageBitmap(GameManager.hpImg[0]);
                    hpImg[0].setImageBitmap(GameManager.hpImg[0]);
                    break;
                case 250:
                    hpImg[2].setImageBitmap(GameManager.hpImg[1]);
                    break;
                case 200:
                    hpImg[2].setImageBitmap(GameManager.hpImg[2]);
                    break;
                case 150:
                    hpImg[2].setImageBitmap(GameManager.hpImg[2]);
                    hpImg[1].setImageBitmap(GameManager.hpImg[1]);
                    break;
                case 100:
                    hpImg[2].setImageBitmap(GameManager.hpImg[2]);
                    hpImg[1].setImageBitmap(GameManager.hpImg[2]);
                    break;
                case 50:
                    hpImg[2].setImageBitmap(GameManager.hpImg[2]);
                    hpImg[1].setImageBitmap(GameManager.hpImg[2]);
                    hpImg[0].setImageBitmap(GameManager.hpImg[1]);
                    break;
                default:
                    hpImg[2].setImageBitmap(GameManager.hpImg[2]);
                    hpImg[1].setImageBitmap(GameManager.hpImg[2]);
                    hpImg[0].setImageBitmap(GameManager.hpImg[2]);
                    break;
            }
        }

    }


    //判断角色是否死亡
    public boolean isDie(){
        return hp<=0;
    }
    //
    public boolean getIsOver(){
        return isOver;
    }
    public void setIsOver(boolean isOver){
        this.isOver=isOver;
    }
    public void setAction(int action) {
        this.action=action;
    }
    public int getAction(){
        return action;
    }

    public void setMove(int move){
        this.move=move;
    }
    public int getMove(){
        return move;
    }
    public void setPlayerX(int playerX){
        this.playerX=playerX % (GameManager.gameMap.getWidth()+X_DEFAULT);
        if(this.playerX<X_DEFAULT){
            this.playerX=X_DEFAULT;
        }
    }
    public int getPlayerX(){
        return playerX;
    }

    public void setPlayerY(int playerY){
        this.playerY=playerY;
    }
    public int getPlayerY(){
        return playerY;
    }
    public boolean isJump(){
        return isJump;
    }
    public void setJump(){
        isJumpCount=5;
        isJump=true;
    }
    public int getShootTime(){
        return shootTime;
    }
    public void setShoot(boolean isShoot){
        this.isShoot=isShoot;
    }

    public void setHp(PlayerTitle playerTitle,int hp){
        this.hp=hp;
        playerTitle.setHp(hp);
    }
    public int getHp(){
       return hp;
    }

    public List<Bullet> getBulletList(){
        return bulletList;
    }
}











































