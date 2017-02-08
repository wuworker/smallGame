package com.wxl.firsttest.metalslug.comp;

import android.graphics.Canvas;
import android.util.Log;

import com.wxl.firsttest.metalslug.GameManager;
import com.wxl.firsttest.metalslug.GameView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/8/24 0024.
 * 怪物管理类
 */
public class MonsterManager {

    private static final String TAG="MonsterManager:";
    //死亡的怪物
    public static final List<Monster> dieMonsterList=new ArrayList<>();
    //活着的怪物
    public static final List<Monster> monsterList=new ArrayList<>();

    private static final float[][] MONSTER_MAN1_LOCATION={
            {0.125f,0.74f}, {0.193f,0.74f}, {0.253f,0.72f},{0.395f,0.72f},{0.488f,0.72f},
            {0.580f,0.72f},{0.688f,0.49f},{0.740f,0.24f},{0.796f,0.76f},{0.951f,0.76f}
    };

    private static final float[][] MONSTER_PLANE_LOCATION={
            {0.185f,0.35f}, {0.233f,0.40f}, {0.313f,0.35f},{0.435f,0.38f},{0.558f,0.37f},
            {0.620f,0.40f},{0.836f,0.37f},{0.951f,0.38f}
    };

    //产生怪物
    public static void generateManager(){

        int mapWidth= GameManager.mapWidth;
        int mapHeight=GameManager.mapHeight;

        for(float[] location:MONSTER_MAN1_LOCATION){
            Monster monster=new Monster((int)(mapWidth*location[0]),
                    (int)(mapHeight*location[1]),Monster.MOSTER_TYPE_MAN_1);
            monsterList.add(monster);
        }

        for(float[] location:MONSTER_PLANE_LOCATION){
            Monster monster=new Monster((int)(mapWidth*location[0]),
                    (int)(mapHeight*location[1]),Monster.MOSTER_TYPE_PLANE);
            monsterList.add(monster);
        }

        Monster monster=new Monster((int)(mapWidth*0.90f),
                (int)(mapHeight*0.75f), Monster.MOSTER_TYPE_BOSS);
        monsterList.add(monster);
    }

    //更新位置
    public static void updatePosistion(int shift){
        Monster monster=null;
        List<Monster> delList=new ArrayList<>();
        //更新活着怪物的位置
        for(int i=0;i<monsterList.size();i++){
            monster=monsterList.get(i);
            if(monster==null) {
                Log.d(TAG,i+"怪物为null");
                continue;
            }
            monster.updataShift(shift);
        }
        monsterList.removeAll(delList);
        delList.clear();
        //更新死亡怪物的位置
        for(int i=0;i<dieMonsterList.size();i++){
            monster=dieMonsterList.get(i);
            if(monster==null)
                continue;

            monster.updataShift(shift);
            //怪物在屏幕外
            if(monster.getX()<0){
                delList.add(monster);
            }
        }
        dieMonsterList.removeAll(delList);
        //更新角色子弹的位置
      //  GameView.player.updateBulletShift(shift);
    }

    //检测怪物是否死亡,是否打中玩家
    public static void checkMonster(){
        List<Bullet> playBulletList=GameView.player.getBulletList();
        if(playBulletList==null){
            playBulletList=new ArrayList<>();
        }

        Monster monster;
        List<Monster> delMonster=new ArrayList<>();
        List<Bullet> delBullet=new ArrayList<>();

        for(int i=0;i<monsterList.size();i++) {
            monster = monsterList.get(i);
            if (monster == null)
                continue;
            //遍历角色发的子弹
            for (Bullet bullet : playBulletList) {
                if (bullet == null || !bullet.getEffect())
                    continue;
                if (monster.isHurt(bullet.getX(), bullet.getY())) {
                    bullet.setEffect(false);

                    if(monster.getDie())
                        delMonster.add(monster);

                    delBullet.add(bullet);
                }
            }
            //把打到怪物的子弹删除
            playBulletList.removeAll(delBullet);
            //检查怪物子弹是否打到角色
            monster.checkBulletToPlayer();
        }
        //有可能怪物死亡之前刚好发出子弹
        for(Monster monster1:dieMonsterList){
            if(monster1!=null)
                monster1.checkBulletToPlayer();
        }
        //把刚死亡的添加进死亡集合,活着的集合去除
        dieMonsterList.addAll(delMonster);
        monsterList.removeAll(delMonster);
    }
    //画所有的怪物
    public static void drawMonster(Canvas canvas){
        Monster monster=null;
        for(int i=0;i<monsterList.size();i++){
            monster=monsterList.get(i);
            if(monster==null)
                continue;
            monster.drawMonster(canvas);
        }
        List<Monster> delList=new ArrayList<>();
        for(int i=0;i<dieMonsterList.size();i++){
            monster=dieMonsterList.get(i);
            if(monster==null)
                continue;
            monster.drawMonster(canvas);
            if(monster.getDieDrawCount()<=0 && monster.getBulletList().size()==0){
                delList.add(monster);
            }
        }
        dieMonsterList.removeAll(delList);
    }

}






















