package rank;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collections;

import user.GameUser;
/**
 * 这个类包含了对用户数据的排序，输出
 * */
public class RankUtils {
	
	private static final String FILE_NAME="rank list.txt";
	
	private GameUser[] user=new GameUser[20];
	private ArrayList<GameUser> list1=new ArrayList<GameUser>();
	private ArrayList<GameUser> list2=new ArrayList<GameUser>();
	
	public RankUtils(){		
		readRank();
	}
	
	//得到排名列表1
	public ArrayList<GameUser> getRankList1(){
		return list1;
	}
	
	//得到排名列表2
	public ArrayList<GameUser> getRankList2(){
		return list2;
	}
	
	//更新排名
	public void updataRank(GameUser user){
		if(user.getRank().equals("初级")){
			if(user.getTime()<list1.get(9).getTime()){
				list1.set(9,user);
				Collections.sort(list1);
				writeRank();
			}
		}else if(user.getRank()=="中级"){
			if(user.getTime()<list2.get(9).getTime()){
				list2.set(9,user);
				Collections.sort(list2);
				writeRank();
			}
		}
	}
	
	//重置排名
	public void resetRank(){
		for(int i=0;i<10;i++){
			user[i].initUser();
			user[i+10].initUser();
			list1.set(i, user[i]);
			list2.set(i, user[i+10]);
		}
		writeRank();
	}
	
	public String toString(){
		return list1.toString()+"\n"+list2.toString();
	}
	
	//序列化输出
	private void writeRank(){
		try(ObjectOutputStream out=new ObjectOutputStream(
				new FileOutputStream(FILE_NAME))){
			for(int i=0;i<10;i++){
				out.writeObject(list1.get(i));
			}
			for(int i=0;i<10;i++){
				out.writeObject(list2.get(i));
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	//序列化输入
	private void readRank(){
		File file=new File(FILE_NAME);
		if(file.exists()){
			try(ObjectInputStream in=new ObjectInputStream(
					new FileInputStream(file))){
				for(int i=0;i<10;i++){
					user[i]=(GameUser)(in.readObject());
					list1.add(i,user[i]);
				}
				for(int i=0;i<10;i++){
					user[i+10]=(GameUser)(in.readObject());
					list2.add(i,user[i+10]);
				}
			}catch(Exception e){
				e.printStackTrace();
				sloveReadFail();
			}
		} else {
			System.out.println("文件不存在");
			sloveReadFail();
			writeRank();
		}
	}
	
	//解决读取失败,读取失败，重新初始化
	private void sloveReadFail(){
		for(int i=0;i<10;i++){
			user[i]=new GameUser("--","初级",999);
			user[i+10]=new GameUser("--","中级",999);
			list1.add(user[i]);
			list2.add(user[i+10]);
		}
	}
	
}








