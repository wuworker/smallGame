package com.wxl.firsttest.metalslug;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.wxl.firsttest.metalslug.comp.MonsterManager;
import com.wxl.firsttest.metalslug.comp.Player;

/**
 * Created by Administrator on 2016/8/13.
 * 游戏实现类
 */
public class GameView extends SurfaceView implements SurfaceHolder.Callback{

    private static final String TAG="GameView:";
    public static final Player player=new Player("吴大王",Player.HP_MAX);
    public static Player.PlayerTitle titleView;
    private Context maincontext;
    private Canvas canvas;
    private SurfaceHolder surfaceHolder;
    public static GameThread gameThread;

    // 定义该游戏当前处于何种场景的变量
    private int cStep = 0;

    //游戏步骤
    public static final int INIT=1;
    public static final int LOGIC=2;
    public static final int LOSE=3;
    public static final int PAINT=4;

    //
    public static final int MESSAGE_HP=0x111;

    public GameView(Context context,int firstStage){
        super(context);

        maincontext=context;
        surfaceHolder=getHolder();
        surfaceHolder.addCallback(this);
        cStep=firstStage;
        // 设置该组件会保持屏幕常量，避免游戏过程中出现黑屏。
        setKeepScreenOn(true);
        // 设置焦点，相应事件处理
        setFocusable(true);
    }

    public GameView(Context context, AttributeSet set){
        super(context,set);

        maincontext=context;
        surfaceHolder=getHolder();
        surfaceHolder.addCallback(this);
        cStep=INIT;
        // 设置该组件会保持屏幕常量，避免游戏过程中出现黑屏。
        setKeepScreenOn(true);
        // 设置焦点，相应事件处理
        setFocusable(true);
    }

    private Handler setViewHandler=new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {

            if(msg.what==0) {
                RelativeLayout relativeLayout = (RelativeLayout) (msg.obj);
                if (relativeLayout != null) {
                    RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                    MainActivity.mainLayout.addView(relativeLayout, params);
                }
            }
            else if(msg.what==1){
                RelativeLayout relativeLayout = (RelativeLayout) (msg.obj);
                MainActivity.mainLayout.removeView(relativeLayout);
            }
            return false;
        }
    });

    public static Handler changeHandler=new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            if(msg.what==MESSAGE_HP){
                Bundle bundle=msg.getData();
                int hp=bundle.getInt("HP");
                player.setHp(titleView,player.getHp()-hp);

            }
            return false;
        }
    });

    //设置开始界面
    private RelativeLayout gameLayout;
    private RelativeLayout loseLayout;
    public void doGame(int step){

        switch (step){
            case INIT:
                gameLayout=new RelativeLayout(maincontext);

                //添加向左的按钮
                ImageButton btn=new ImageButton(maincontext);
                int leftId=generateViewId();
                btn.setId(leftId);
                btn.setBackground(getResources().getDrawable(R.drawable.left, null));
                btn.setScaleType(ImageView.ScaleType.FIT_CENTER);
                RelativeLayout.LayoutParams params=new RelativeLayout.LayoutParams(
                        (int) (GameManager.imgScale * 60),(int) (GameManager.imgScale * 60));
                params.addRule(RelativeLayout.ALIGN_PARENT_START);
                params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                params.setMargins((int) (GameManager.imgScale * 20), 0, 0, (int) (GameManager.imgScale * 20));
                btn.setOnTouchListener(new OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        switch (event.getAction()){
                            case MotionEvent.ACTION_DOWN:
                                player.setMove(Player.MOVE_LEFT);
                                break;
                            case MotionEvent.ACTION_UP:
                                player.setMove(Player.MOVE_STAND);
                                break;
                            default:break;
                        }
                        return true;
                    }
                });
                gameLayout.addView(btn,params);
                //添加向右的按钮
                btn=new ImageButton(maincontext);
                params=new RelativeLayout.LayoutParams(
                        (int) (GameManager.imgScale * 60),(int) (GameManager.imgScale * 60));
                btn.setScaleType(ImageView.ScaleType.FIT_CENTER);
                btn.setBackground(getResources().getDrawable(R.drawable.right, null));
                params.addRule(RelativeLayout.END_OF, leftId);
                params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                params.setMargins((int) (GameManager.imgScale * 20), 0, 0, (int) (GameManager.imgScale * 20));
                btn.setOnTouchListener(new OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        switch (event.getAction()) {
                            case MotionEvent.ACTION_DOWN:
                                player.setMove(Player.MOVE_RIGHT);
                                break;
                            case MotionEvent.ACTION_UP:
                                player.setMove(Player.MOVE_STAND);
                                break;
                            default:
                                break;
                        }
                        return true;
                    }
                });
                gameLayout.addView(btn,params);
                //添加跳动按钮
                btn=new ImageButton(maincontext);
                int jumpId=generateViewId();
                btn.setId(jumpId);
                params=new RelativeLayout.LayoutParams(
                        (int) (GameManager.imgScale * 60),(int) (GameManager.imgScale * 60));
                btn.setScaleType(ImageView.ScaleType.FIT_CENTER);
                btn.setBackground(getResources().getDrawable(R.drawable.jump, null));
                params.addRule(RelativeLayout.ALIGN_PARENT_END);
                params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                params.setMargins(0, 0, (int) (GameManager.imgScale * 20), (int) (GameManager.imgScale * 20));
                btn.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!player.isJump() && !player.getIsOver())
                            player.setJump();
                    }
                });
                gameLayout.addView(btn,params);
                //添加射击的按钮
                btn=new ImageButton(maincontext);
                params=new RelativeLayout.LayoutParams(
                        (int) (GameManager.imgScale * 60),(int) (GameManager.imgScale * 60));
                btn.setScaleType(ImageView.ScaleType.FIT_CENTER);
                btn.setBackground(getResources().getDrawable(R.drawable.fire, null));
                params.addRule(RelativeLayout.START_OF, jumpId);
                params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                params.setMargins(0, 0, (int) (GameManager.imgScale * 20), (int) (GameManager.imgScale * 20));
                btn.setOnTouchListener(new OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        switch (event.getAction()) {
                            case MotionEvent.ACTION_DOWN:
                                player.setShoot(true);
                                break;
                            case MotionEvent.ACTION_UP:
                                player.setShoot(false);
                                break;
                            default:
                                break;
                        }
                        return true;
                    }
                });
                gameLayout.addView(btn, params);
                //添加角色信息
                titleView=player.new PlayerTitle(maincontext);
                RelativeLayout relativeLayout=titleView.getPlayerTitleLayout();
                params=new RelativeLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                params.addRule(RelativeLayout.ALIGN_PARENT_TOP);
                params.addRule(RelativeLayout.ALIGN_PARENT_START);

                gameLayout.addView(relativeLayout,params);
                setViewHandler.sendMessage(setViewHandler.obtainMessage(0,gameLayout));
                //产生怪物
                MonsterManager.generateManager();
                cStep=LOGIC;
                break;
            case LOGIC:
                if(!player.getIsOver()) {
                    MonsterManager.checkMonster();
                    player.moveAndJump();
                }
                else if(player.isJump()){
                    MonsterManager.checkMonster();
                    player.moveAndJump();
                }
                else{
                    cStep=LOSE;
                }
                GameManager.clearScreen(canvas, Color.WHITE);
                GameManager.drawGame(canvas);
                break;
            case LOSE:
                loseLayout=new RelativeLayout(maincontext);

                ImageButton btn1=new ImageButton(maincontext);
                btn1.setBackgroundResource(R.drawable.restart_game);
                btn1.setScaleType(ImageView.ScaleType.FIT_CENTER);
                btn1.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        setViewHandler.sendMessage(setViewHandler.obtainMessage(1,loseLayout));
                        player.setHp(titleView,Player.HP_MAX);
                        player.setIsOver(false);
                        player.getBulletList().clear();

                        cStep=LOGIC;
                    }
                });
                RelativeLayout.LayoutParams params1=new RelativeLayout.LayoutParams(
                        (int) (GameManager.imgScale * 135),(int) (GameManager.imgScale * 45));
                params1.addRule(RelativeLayout.CENTER_IN_PARENT);
                loseLayout.addView(btn1,params1);

                setViewHandler.sendMessage(setViewHandler.obtainMessage(0,loseLayout));
                cStep=PAINT;
                break;
            case PAINT:
                GameManager.clearScreen(canvas, Color.WHITE);
                GameManager.drawGame(canvas);
                break;

            default:break;
        }
    }

    class GameThread extends  Thread{

        public SurfaceHolder holder;

        public boolean needStop;

        public GameThread(SurfaceHolder holder){
            this.holder=holder;
        }

        @Override
        public void run() {

           // synchronized (holder){
                while(!needStop) {
                    try {
                        long time1 = System.currentTimeMillis();
                        if (holder != null)
                            canvas = holder.lockCanvas();
                        if (canvas != null) {
                            doGame(cStep);
                        }
                        long time2 = System.currentTimeMillis();
                        if (time2 - time1 < 30) {
                            sleep(30 - (time2 - time1));
                        }
                        //Log.d(TAG,"game:"+(time2-time1));
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        try {
                            if (canvas != null && !needStop && holder != null)
                                holder.unlockCanvasAndPost(canvas);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            Log.d(TAG,"gameThread已结束");
        }
    }


    public boolean onTouchEvent(MotionEvent e){
        Log.d(TAG,"点击了:"+e.getAction());
        return true;
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder){
        Log.d(TAG, "执行surfaceCreated");

        if(gameThread!=null){
            gameThread.needStop=true;
        }
        gameThread=new GameThread(surfaceHolder);
        gameThread.start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width,
                               int height)
    {
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        if(gameThread!=null){
            gameThread.needStop=true;
        }
    }
}
