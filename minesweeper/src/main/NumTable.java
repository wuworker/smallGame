package main;

/**
 * 产生用于扫雷的数表类
 * 数字最大为8,
 * 9代表炸弹
 * */
public class NumTable {

	private int[] zhadan;
	private int[][] table;
	private int[] h;
	private int[] l;
	private int hlNum;
	private int zhadanNum;
	
	public NumTable(){}
	
	/**
	 * 构造方法
	 * @param  hlNum        数表的行列数
	 * @param  zhadanNum    炸弹的数目
	 * */
	public NumTable(int hlNum, int zhadanNum){			
		produceTable(hlNum,zhadanNum);
	}
	
	//重新产生数表
	public void produceTable(int hlNum, int zhadanNum){
		this.hlNum=hlNum;
		this.zhadanNum=zhadanNum;
		
		table=new int[hlNum][hlNum];
		h=new int[zhadanNum];
		l=new int[zhadanNum];
		zhadan=new int[zhadanNum];
		
		for(int i=0;i<zhadanNum;i++)
			zhadan[i]=100;
		
		setZhaDan();
		setTable();
	}
	
	//得到数表
	public int[][] getTable(){
		return table;
	}
	
	//得到炸弹的行
	public int[] getZhadanH(){
		return h;
	}
	
	//得到炸弹的列
	public int[] getZhadanL(){
		return l;
	}
	
	/**
	 * 重写toString方法，
	 * 输出数表
	 * 方便查看
	 * */
	public String toString(){
		if(table==null)
			return "null";
		StringBuilder str=new StringBuilder();
		for(int i=0;i<table.length;i++){
			for(int j=0;j<table[i].length;j++){
				str.append(table[i][j]+" ");
			}
			str.append("\n");
		}
		
		return str.toString();
	}
	
	//设置炸弹
	private void setZhaDan(){			
		int n=0;
		Label1:
			for(int i=0;i<zhadanNum;i++){
				n=(int)(Math.random()*hlNum*hlNum)+1;

				for(int j=0;j<i;j++){
					if(zhadan[j]==n){
						i--;
						continue Label1;
					}
				}
				zhadan[i]=n;
			}
	}
	
	//设置表格
	private void setTable(){
		for(int i=0;i<zhadanNum;i++){
			if(zhadan[i]==0){
				l[i]=0;
				h[i]=0;
			}else{
				if(zhadan[i]%hlNum==0){
					l[i]=hlNum-1;
					h[i]=zhadan[i]/hlNum-1;
				}else{
					l[i]=zhadan[i]%hlNum-1;
					h[i]=zhadan[i]/hlNum;
				}
			}			
			table[h[i]][l[i]]=9;
		}
		//设置炸弹周围的数
		for(int i=0;i<zhadanNum;i++){				
			if((h[i]>0)&&(h[i]<(hlNum-1))&&
					(l[i]>0)&&(l[i]<(hlNum-1))){
				table[h[i]][l[i]-1]=table[h[i]][l[i]-1]+1;
				table[h[i]][l[i]+1]=table[h[i]][l[i]+1]+1;
				table[h[i]+1][l[i]]=table[h[i]+1][l[i]]+1;
				table[h[i]-1][l[i]]=table[h[i]-1][l[i]]+1;
				table[h[i]+1][l[i]-1]=table[h[i]+1][l[i]-1]+1;
				table[h[i]+1][l[i]+1]=table[h[i]+1][l[i]+1]+1;
				table[h[i]-1][l[i]-1]=table[h[i]-1][l[i]-1]+1;
				table[h[i]-1][l[i]+1]=table[h[i]-1][l[i]+1]+1;				
			} else if((h[i]<1)&&(l[i]>0)&&
					(l[i]<(hlNum-1))){
				table[h[i]][l[i]-1]=table[h[i]][l[i]-1]+1;
				table[h[i]][l[i]+1]=table[h[i]][l[i]+1]+1;
				table[h[i]+1][l[i]]=table[h[i]+1][l[i]]+1;
				table[h[i]+1][l[i]-1]=table[h[i]+1][l[i]-1]+1;
				table[h[i]+1][l[i]+1]=table[h[i]+1][l[i]+1]+1;
			} else if((h[i]>(hlNum-2))&&(l[i]>0)&&
					(l[i]<(hlNum-1))){
				table[h[i]][l[i]-1]=table[h[i]][l[i]-1]+1;
				table[h[i]][l[i]+1]=table[h[i]][l[i]+1]+1;
				table[h[i]-1][l[i]]=table[h[i]-1][l[i]]+1;
				table[h[i]-1][l[i]-1]=table[h[i]-1][l[i]-1]+1;
				table[h[i]-1][l[i]+1]=table[h[i]-1][l[i]+1]+1;
			}else if((h[i]>0)&&(h[i]<(hlNum-1))&&
					(l[i]<1)){
				table[h[i]][l[i]+1]=table[h[i]][l[i]+1]+1;
				table[h[i]+1][l[i]]=table[h[i]+1][l[i]]+1;
				table[h[i]-1][l[i]]=table[h[i]-1][l[i]]+1;
				table[h[i]+1][l[i]+1]=table[h[i]+1][l[i]+1]+1;
				table[h[i]-1][l[i]+1]=table[h[i]-1][l[i]+1]+1;	
			}else if((h[i]>0)&&(h[i]<(hlNum-1))&&
					(l[i]>(hlNum-2))){
				table[h[i]][l[i]-1]=table[h[i]][l[i]-1]+1;
				table[h[i]+1][l[i]]=table[h[i]+1][l[i]]+1;
				table[h[i]-1][l[i]]=table[h[i]-1][l[i]]+1;
				table[h[i]+1][l[i]-1]=table[h[i]+1][l[i]-1]+1;
				table[h[i]-1][l[i]-1]=table[h[i]-1][l[i]-1]+1;
			}else if((h[i]==0)&&(l[i]==0)){
				table[1][0]=table[1][0]+1;
				table[0][1]=table[0][1]+1;
				table[1][1]=table[1][1]+1;
			}else if((h[i]==0)&&(l[i]==(hlNum-1))){
				table[0][hlNum-2]=table[0][hlNum-2]+1;
				table[1][hlNum-2]=table[1][hlNum-2]+1;
				table[1][hlNum-1]=table[1][hlNum-1]+1;
			}else if((h[i]==(table.length-1))&&(l[i]==0)){
				table[hlNum-2][0]=table[(hlNum-2)][0]+1;
				table[hlNum-2][1]=table[hlNum-2][1]+1;
				table[hlNum-1][1]=table[(hlNum-1)][1]+1;
			}else if((h[i]==(hlNum-1))&&(l[i]==(hlNum-1))){
				table[hlNum-2][hlNum-1]=
						table[hlNum-2][hlNum-1]+1;
				table[hlNum-2][hlNum-2]=
						table[hlNum-2][hlNum-2]+1;
				table[hlNum-1][hlNum-2]=
						table[hlNum-1][hlNum-2]+1;
			}
		}
		//数字大于9的全部为炸弹
		for(int i=0;i<hlNum;i++)
			for(int j=0;j<hlNum;j++)
				if(table[i][j]>9)
					table[i][j]=9;			
	}
	
	
}





