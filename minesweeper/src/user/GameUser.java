package user;

import java.io.Serializable;
/**
 * 用户类
 * 包含用户姓名，时间，等级
 * */
public class GameUser implements Serializable,Comparable<GameUser>{
	
	private static final long serialVersionUID = 1L;
	private String name;
	private String rank;
	private int time;
	
	public GameUser(){};
	
	public GameUser(String name,String rank,int time){
		this.name=name;
		this.rank=rank;
		this.time=time;
	}
	//读写用户名
	public void setName(String name){
		this.name=name;
	}
	public String getName(){
		return name;
	}
	//读写等级
	public void setRank(String rank){
		this.rank=rank;
	}
	public String getRank(){
		return rank;
	}
	//读写时间
	public void setTime(int time){
		this.time=time;
	}
	public int getTime(){
		return time;
	}
	//用户重置
	public void initUser(){
		name="--";
		time=999;
	}
	//输出用户信息
	public String toString(){
		return name+","+rank+","+time;
	}
	//基于时间的比较
	public int compareTo(GameUser user){
		if(getTime()==user.getTime())
			return 0;
		else
			return getTime()>user.getTime()? 1 : -1;
	}
}







