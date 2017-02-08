package com.wxl.firsttest.metalslug;

import android.content.DialogInterface;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

public class MainActivity extends AppCompatActivity {

    private static final String TAG="MainActivity:";
    //实现资源管理
    public static MainActivity mainActivity;
    public static Resources res;

    public static int windowWidth;
    public static int windowHeight;

    public static FrameLayout mainLayout;
    private GameView gameView;

    private ProgressBar bar;
    private int count;

    private Handler myHandler=new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            if(msg.what==0x111){
                bar.setProgress(count);
            }
            return false;
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mainActivity=this;
        res=getResources();

        //显示全屏
       // requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //获取窗口大小
        DisplayMetrics metrics=new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        windowWidth=metrics.widthPixels;
        windowHeight=metrics.heightPixels;
        Log.d("窗口大小:", "宽：" + windowWidth + ",高:" + windowHeight);
        //设置软键盘
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        setContentView(R.layout.activity_main);
        mainLayout=(FrameLayout)findViewById(R.id.main_layout);
        bar=(ProgressBar)findViewById(R.id.main_bar);

        new Thread(){
            @Override
            public void run() {
                try{
                    while(count<100) {
                        count++;
                        myHandler.sendEmptyMessage(0x111);
                        sleep(30);
                    }
                }
                catch (InterruptedException e){
                    e.printStackTrace();
                }
            }
        }.start();

        new LoadTask().execute();

    }
   // public void onBackPressed(){

   // }

    class LoadTask extends AsyncTask<Void,Integer,Void>{
        @Override
        protected void onPreExecute() {

        }

        @Override
        protected Void doInBackground(Void... params) {

            GameManager.initScreen(windowWidth, windowHeight);
            GameManager.loadResource();
            while(count<100);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            gameView=new GameView(MainActivity.this.getApplicationContext(),GameView.INIT);
            FrameLayout.LayoutParams params=new FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            mainLayout.removeAllViews();
            mainLayout.addView(gameView,params);
        }

        @Override
        protected void onProgressUpdate(Integer... values) {

        }
    }


    public void onBackPressed(){
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setTitle("退出应用")
                .setMessage("确定退出应用")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        finish();
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        builder.show();
    }


    public void onPause(){
        super.onPause();
        Log.d(TAG, "onPause");
     //   GameManager.releaseResource();
        mainLayout.removeAllViews();
    }
    public void onDestroy(){
        super.onDestroy();
        Log.d(TAG, "onDestroy");
        android.os.Process.killProcess(android.os.Process.myPid());
    }

}
