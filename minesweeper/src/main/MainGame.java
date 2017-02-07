package main;

import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import rank.RankUtils;
import rank.RankView;
import user.GameUser;
/**
 * 扫雷游戏的主类
 * */
public class MainGame extends JPanel{

	private static final long serialVersionUID = 1L;
	//主布局
	private CardLayout card=new CardLayout();
	//选择的是初级还是中级参数
	private int level=1;
	private int hlnum;
	private int zdnum;
	//扫雷格子的panel
	private JPanel p2=new JPanel();
	//标签和按钮
	private JLabel labts=new JLabel("时间：0",JLabel.CENTER);
	private JLabel labnu=new JLabel("红旗：10",JLabel.CENTER);
	private JButton buttop=new JButton();
	private int hongqinum=10;
	//扫雷的9X9格子
	private JPanel pan1=new JPanel();     	
	private SCell[][] cells;
	private NumTable nt=new NumTable();
	private int[][] tb;
	private int[] tanh;
	private int[] tanl;
	//排行榜
	private RankUtils rankUtils;
	private RankView rankView;
	//计时
	private boolean pause=true;
	private boolean stop;
	private Thread timeThread;
	private TimeTask timeRunnable;
	private int t;
	//用到的图片
	private final String str_back="/picture/back1.jpg";
	private final String str_hq1="/picture/hq1.jpg";
	private final String str_hq3="/picture/hq3.jpg";
	private final String str_ks1="/picture/ks1.jpg";
	private final String str_sb1="/picture/sb1.jpg";
	private final String str_sl1="/picture/sl1.jpg";
	private final String str_zt1="/picture/zt1.jpg";
	private final String str_zt2="/picture/zt2.jpg";
	
	public MainGame(){
		setPreferredSize(new Dimension(480,550));
		setBorder(BorderFactory.createLoweredBevelBorder());
		setLayout(card);
		
		initViewPanel1();
		initViewPanel2();
		
		rankUtils=new RankUtils();
		rankView=new RankView(rankUtils);
		
		timeRunnable=new TimeTask();
		timeThread=new Thread(timeRunnable);
		timeThread.start();
	}
	
	public static void main(String[] args){
		JFrame fra=new JFrame("扫雷大冒险");
		MainGame game=new MainGame();
		
		fra.setLocation(150, 50);
		fra.setResizable(false);
		fra.addWindowListener(new WindowAdapter(){
			public void windowClosing(WindowEvent e){
				game.destroyThread();
				fra.dispose();
			}
		});
				
		Container c=fra.getContentPane();
		c.setLayout(new FlowLayout());

		c.add(game);
		fra.pack();
		fra.setVisible(true);
	}
	//重新开始
	public void restartGame(int level){
		this.level=level;
		
		t=0;
		pause=true;
		timeRunnable.resetSecond();
		
		p2.remove(pan1);
		pan1=new JPanel();

		switch(level){
		case 1:
			hlnum=9;
			zdnum=10;
			pan1.setBounds(127,130,225,225);
			break;
		case 2:
			hlnum=16;
			zdnum=40;
			pan1.setBounds(40,80,400,400);
			break;
		default:
			hlnum=9;
			zdnum=10;
			pan1.setBounds(127,130,225,225);
			break;
		}
		hongqinum=zdnum;
		pan1.setLayout(new GridLayout(hlnum,hlnum));
		setButtop(str_ks1);
		labts.setText("时间：0");
		setLabNum(hongqinum);
		
		cells=new SCell[hlnum][hlnum];
		for(int i=0;i<hlnum;i++)
			for(int j=0;j<hlnum;j++)
			{			
				cells[i][j]=new SCell();
				pan1.add(cells[i][j]);
			}	
		
		nt.produceTable(hlnum, zdnum);
		tb=nt.getTable();
		tanh=nt.getZhadanH();
		tanl=nt.getZhadanL();
		
		for(int i=0;i<hlnum;i++)
			for(int j=0;j<hlnum;j++)
			{			
				cells[i][j].setNum(tb[i][j]);
				cells[i][j].setHBianHao(i);
				cells[i][j].setLBianHao(j);
			}
		
		p2.add(pan1,0);
		p2.updateUI();	
		
		System.out.println(nt.toString());
	}
	//判断是否胜利
	public boolean isVectory(){

		int nn=0;
		for(int i=0;i<getZDNum();i++){
			if(!cells[tanh[i]][tanl[i]].getHongQi())
				return false;
		}
		
		for(int i=0;i<getHLNum();i++)
			for(int j=0;j<getHLNum();j++){			
				if((!cells[i][j].getHongQi())&&(cells[i][j].getLow())){
					nn++;
				}
			}
		if(nn>(getHLNum()*getHLNum()-getZDNum()-1)){
			return true;
		}
		
		return false;
	}
	
	//破纪录
	public void PoJiLu()
	{
		switch(level){
		case 1:
			if( t < rankUtils.getRankList1().get(9).getTime()){
				InputYourName();
			}
			break;
		case 2:
			if( t < rankUtils.getRankList2().get(9).getTime()){
				InputYourName();
			}
			break;
		}
	}	
	
	//销毁线程
	public void destroyThread(){
		stop=true;
	}
	//得到行列数
	public int getHLNum(){
		switch(level){
		case 1:
			hlnum=9;break;
		case 2:
			hlnum=16;break;
		default:hlnum=9;break;
		}
		
		return hlnum;
	}
	//得到炸弹数
	public int getZDNum(){
		switch(level){
		case 1:
			zdnum=10;break;
		case 2:
			zdnum=40;break;
		default:zdnum=10;break;
		}
		
		return zdnum;
	}
	/**
	 * 扫雷游戏的一个格子
	 * */
 	private class SCell extends JButton{

		private static final long serialVersionUID = 1L;
		private Color[] colors={
				new Color(0,128,255),new Color(40,225,31),new Color(255,0,0),
				new Color(0,0,160),new Color(128,0,0),new Color(35,210,220),
				new Color(0,0,0),new Color(128,128,128)
	    };
		private int bianhao_h=0;
		private int bianhao_l=0;
		private int num=0;
		private boolean hongqi=false;
		private boolean low=false;
		MyCellListener mylistener=new MyCellListener();
		
		public SCell(){
			addMouseListener(mylistener);
		}	

		//打开格子
		public void OpenCell(){
			setBackground(new Color(230,230,230));
			setBorder(BorderFactory.createLoweredSoftBevelBorder());
			switch(num){
			case 0:
				setText("");
				break;
			case 9:
				setBooM(str_zt1);
				break;	
			default:
				setText(String.valueOf(num));
				setFont(new Font("宋体",Font.BOLD,15));
				setForeground(colors[num-1]);
				break;
			}
		}
		
		//递归，把num=0的格子自动打开
		public void OpenKong(SCell cell){	
			if(cell.getLow())
				return;
			cell.OpenCell();
			cell.setLow(true);
			cell.removeMyListener();
			if(cell.getNum()==0){
				int h=cell.getHBianHao();
				int l=cell.getLBianHao();
				if((h>0)&&(h<(getHLNum()-1))&&(l>0)&&(l<(getHLNum()-1))){
					if(cells[h-1][l].getNum()!=9)
						OpenKong(cells[h-1][l]);
					if(cells[h+1][l].getNum()!=9)
						OpenKong(cells[h+1][l]);
					if(cells[h][l-1].getNum()!=9)
						OpenKong(cells[h][l-1]);
					if(cells[h][l+1].getNum()!=9)
						OpenKong(cells[h][l+1]);
					if(cells[h+1][l-1].getNum()!=9)
						OpenKong(cells[h+1][l-1]);
					if(cells[h+1][l+1].getNum()!=9)
						OpenKong(cells[h+1][l+1]);
					if(cells[h-1][l-1].getNum()!=9)
						OpenKong(cells[h-1][l-1]);
					if(cells[h-1][l+1].getNum()!=9)
						OpenKong(cells[h-1][l+1]);	
				}else if((h>0)&&(h<(getHLNum()-1))&&(l==0)){
					if(cells[h-1][l].getNum()!=9)
						OpenKong(cells[h-1][l]);
					if(cells[h+1][l].getNum()!=9)
						OpenKong(cells[h+1][l]);
					if(cells[h][l+1].getNum()!=9)
						OpenKong(cells[h][l+1]);
					if(cells[h+1][l+1].getNum()!=9)
						OpenKong(cells[h+1][l+1]);
					if(cells[h-1][l+1].getNum()!=9)
						OpenKong(cells[h-1][l+1]);	
				}else if((h>0)&&(h<(getHLNum()-1))&&(l==(getHLNum()-1))){
					if(cells[h-1][l].getNum()!=9)
						OpenKong(cells[h-1][l]);
					if(cells[h+1][l].getNum()!=9)
						OpenKong(cells[h+1][l]);
					if(cells[h][l-1].getNum()!=9)
						OpenKong(cells[h][l-1]);
					if(cells[h+1][l-1].getNum()!=9)
						OpenKong(cells[h+1][l-1]);
					if(cells[h-1][l-1].getNum()!=9)
						OpenKong(cells[h-1][l-1]);	
				}else if((h==0)&&(l>0)&&(l<(getHLNum()-1))){
					if(cells[h+1][l].getNum()!=9)
						OpenKong(cells[h+1][l]);
					if(cells[h][l-1].getNum()!=9)
						OpenKong(cells[h][l-1]);
					if(cells[h][l+1].getNum()!=9)
						OpenKong(cells[h][l+1]);
					if(cells[h+1][l-1].getNum()!=9)
						OpenKong(cells[h+1][l-1]);
					if(cells[h+1][l+1].getNum()!=9)
						OpenKong(cells[h+1][l+1]);
				}else if((h==(getHLNum()-1))&&(l>0)&&(l<(getHLNum()-1))){
					if(cells[h-1][l].getNum()!=9)
						OpenKong(cells[h-1][l]);
					if(cells[h][l-1].getNum()!=9)
						OpenKong(cells[h][l-1]);
					if(cells[h][l+1].getNum()!=9)
						OpenKong(cells[h][l+1]);
					if(cells[h-1][l-1].getNum()!=9)
						OpenKong(cells[h-1][l-1]);
					if(cells[h-1][l+1].getNum()!=9)
						OpenKong(cells[h-1][l+1]);
				}else if((h==0)&&(l==0)){
					if(cells[0][1].getNum()!=9)
						OpenKong(cells[0][1]);
					if(cells[1][1].getNum()!=9)
						OpenKong(cells[1][1]);
					if(cells[1][0].getNum()!=9)
						OpenKong(cells[1][0]);
				}else if((h==0)&&(l==(getHLNum()-1))){
					if(cells[0][getHLNum()-2].getNum()!=9)
						OpenKong(cells[0][getHLNum()-2]);
					if(cells[1][getHLNum()-2].getNum()!=9)
						OpenKong(cells[1][getHLNum()-2]);
					if(cells[1][getHLNum()-1].getNum()!=9)
						OpenKong(cells[1][getHLNum()-1]);
				}else if((h==(getHLNum()-1))&&(l==0)){
					if(cells[getHLNum()-2][0].getNum()!=9)
						OpenKong(cells[getHLNum()-2][0]);
					if(cells[getHLNum()-2][1].getNum()!=9)
						OpenKong(cells[getHLNum()-2][1]);
					if(cells[getHLNum()-1][1].getNum()!=9)
						OpenKong(cells[getHLNum()-1][1]);
				}else if((h==(getHLNum()-1))&&(l==(getHLNum()-1))){
					if(cells[getHLNum()-2][getHLNum()-1].getNum()!=9)
						OpenKong(cells[getHLNum()-2][getHLNum()-1]);
					if(cells[getHLNum()-2][getHLNum()-2].getNum()!=9)
						OpenKong(cells[getHLNum()-2][getHLNum()-2]);
					if(cells[getHLNum()-1][getHLNum()-2].getNum()!=9)
						OpenKong(cells[getHLNum()-1][getHLNum()-2]);
				}
			}
		}
		//插错红旗图
		public void setBlackHongqi(){
			setIcon(new ImageIcon(
					MainGame.class.getResource(str_hq3)));
		}
		//引爆的炸弹
		public void setBooM(String str){
			setIcon(
					new ImageIcon(MainGame.class.getResource(str)));
		}
		//这个格子是否被点过
		public void setLow(boolean a){
			low=a;
		}
		public boolean getLow(){
			return low;
		}
		//这个格子是否插上红旗
		public boolean getHongQi(){
			return hongqi;
		}
		//是否被点出炸弹
		public boolean isFail(){
			return (this.getNum()==9)&&low;
		}
		//设置这个格子的行编号
		public void setHBianHao(int i){
			bianhao_h=i;
		}
		//得到这个格子的行编号
		public int getHBianHao(){
			return bianhao_h;
		}
		//设置这个格子的列编号
		public void setLBianHao(int i){
			bianhao_l=i;
		}
		//得到这个格子的列编号
		public int getLBianHao(){
			return bianhao_l;
		}
		//得到这个格子的数字
		public int getNum(){
			return num;
		}
		//设置这个格子的数字
		public void setNum(int i){
			num=i;
		}
		//移除鼠标监听
		public void removeMyListener(){
			removeMouseListener(mylistener);
		}
		//鼠标监听
		class MyCellListener extends MouseAdapter{
			public void mouseClicked(MouseEvent e){
				pause=false;
				if((e.getButton()==MouseEvent.BUTTON1)&&!hongqi){
					OpenKong(cells[getHBianHao()][getLBianHao()]);
			
					if(isFail()){
						pause=true;
						setButtop(str_sb1);
						for(int i=0;i<getHLNum();i++)
							for(int j=0;j<getHLNum();j++){
								if(!cells[i][j].getHongQi())
									cells[i][j].OpenCell();
								else if(cells[i][j].getHongQi()&&(
										cells[i][j].getNum()!=9)){
									cells[i][j].setBlackHongqi();
								}
								cells[i][j].removeMyListener();
							}
						cells[getHBianHao()][getLBianHao()].setBooM(str_zt2);
					}else if(isVectory()){
						pause=true;
						setButtop(str_sl1);
						for(int i=0;i<getZDNum();i++){
							cells[tanh[i]][tanl[i]].removeMyListener();
						}
						PoJiLu();
					}
					removeMyListener();
					
				}else if(e.getButton()==MouseEvent.BUTTON3){
					
					if(!hongqi){
					hongqinum--;
					setIcon(new ImageIcon(MainGame.class.getResource(str_hq1)));
					}else{
						hongqinum++;
						setIcon(null);
					}
					
					hongqi=!hongqi;
					setLabNum(hongqinum);
					
					if(isVectory()){
						pause=true;
						setButtop(str_sl1);
						for(int i=0;i<getZDNum();i++){
							cells[tanh[i]][tanl[i]].removeMyListener();
						}
						PoJiLu();
					}
				}
			}
		}	
		
	}
 	
	//线程计时
 	class TimeTask implements Runnable
 	{
 		private int second=0;
 		public void resetSecond(){
 			second=0;
 		}
 		public void run(){
 			try{
 				System.out.println("线程开始");
 				while(!stop){
	 				while(!pause){
		 				second++;
		 				if(second>=1000){
		 					second=0;
		 					t++;
			 				labts.setText("时间:"+t);
		 				}
		 				Thread.sleep(1);
	 				}
	 				Thread.sleep(1);
 				}
 				System.out.println("线程结束");
 			}catch(Exception e){
 				System.out.println(e.getMessage());
 			}
 		}
 	}
	
 	//初始化界面1
	private void initViewPanel1(){	
		JPanel p1=new JPanel();
		p1.setPreferredSize(new Dimension(480,550));
		p1.setLayout(null);
		
		JLabel p1_laback=new JLabel();
		p1_laback.setBounds(2, 2, 476, 546);	
		p1_laback.setIcon(new ImageIcon(MainGame.class.getResource(str_back)));
		
		JLabel lab_title1=new JLabel("J A V A",JLabel.CENTER);
		lab_title1.setBounds(20, 20, 440, 70);
		lab_title1.setFont(new Font("宋体",Font.BOLD,40));
		JLabel lab_title2=new JLabel("扫  雷 大 冒 险",JLabel.CENTER);
		lab_title2.setBounds(20, 100, 440, 70);
		lab_title2.setFont(new Font("宋体",Font.BOLD,40));
		
		JLabel but_cj=new JLabel("初级",JLabel.CENTER);
		JLabel but_zj=new JLabel("中级",JLabel.CENTER);
		JLabel but_ph=new JLabel("排行榜",JLabel.CENTER);
		JLabel lab_tc=new JLabel("退出",JLabel.CENTER);
		JLabel lab_down=new JLabel("制   作  人：    吴  醒  乐",JLabel.CENTER);
		
		lab_down.setBounds(130, 500, 220, 30);
		lab_down.setFont(new Font("宋体",Font.BOLD,12));
		lab_down.setForeground(new Color(105,169,86));

		but_cj.setBounds(360,250,80,30);
		but_cj.setBorder(BorderFactory.createRaisedBevelBorder());
		but_cj.setFont(new Font("宋体",Font.BOLD,14));
		but_cj.setForeground(Color.YELLOW);
		but_zj.setBounds(360,290,80,30);
		but_zj.setBorder(BorderFactory.createRaisedBevelBorder());
		but_zj.setFont(new Font("宋体",Font.BOLD,14));
		but_zj.setForeground(Color.YELLOW);
		but_ph.setBounds(360,340,80,30);
		but_ph.setBorder(BorderFactory.createRaisedBevelBorder());
		but_ph.setFont(new Font("宋体",Font.BOLD,14));
		but_ph.setForeground(Color.YELLOW);
		lab_tc.setBounds(360,390,80,30);
		lab_tc.setBorder(BorderFactory.createRaisedBevelBorder());
		lab_tc.setFont(new Font("宋体",Font.BOLD,14));
		lab_tc.setForeground(Color.YELLOW);
		
		//把组件放入
		p1.add(p1_laback,-1);
		p1.add(lab_title1,0);
		p1.add(lab_title2,0);
		p1.add(lab_down,0);
		p1.add(but_cj,0);
		p1.add(but_zj,0);
		p1.add(but_ph,0);
		p1.add(lab_tc,0);
		
		add(p1);
		//-------第一张JPanel的鼠标监听-----------------
		class MyMouseListenPan1 extends MouseAdapter{
			
			public void mouseClicked(MouseEvent e){
				if(e.getSource()==but_cj){
					card.next(MainGame.this);
					restartGame(1);
				}else if(e.getSource()==but_zj){
					card.next(MainGame.this);
					restartGame(2);
				}else if(e.getSource()==but_ph){
					rankView.show();
				}else if(e.getSource()==lab_tc){
					System.exit(0);
				}
			}
			
			public void mouseEntered(MouseEvent e){
				if(e.getSource()==but_cj)
					but_cj.setForeground(Color.RED);
				else if(e.getSource()==but_zj)
					but_zj.setForeground(Color.RED);
				else if(e.getSource()==but_ph)
					but_ph.setForeground(Color.RED);
				else if(e.getSource()==lab_down)
					lab_down.setForeground(Color.YELLOW);
				else if(e.getSource()==lab_tc)
					lab_tc.setForeground(Color.RED);
			}
			
			public void mouseExited(MouseEvent e){
				if(e.getSource()==but_cj)
					but_cj.setForeground(Color.YELLOW);
				else if(e.getSource()==but_zj)
					but_zj.setForeground(Color.YELLOW);
				else if(e.getSource()==but_ph)
					but_ph.setForeground(Color.YELLOW);
				else if(e.getSource()==lab_down)
					lab_down.setForeground(new Color(105,169,86));
				else if(e.getSource()==lab_tc)
					lab_tc.setForeground(Color.YELLOW);
			}
		}
		
		MyMouseListenPan1  mylistenpan1=new MyMouseListenPan1();
		lab_down.addMouseListener(mylistenpan1);
		but_cj.addMouseListener(mylistenpan1);
		but_zj.addMouseListener(mylistenpan1);
		but_ph.addMouseListener(mylistenpan1);
		lab_tc.addMouseListener(mylistenpan1);
	}
	
	//初始化界面2
	private void initViewPanel2(){
		
		JLabel lab_fh=new JLabel("<-BACK");
		JLabel laback=new JLabel();
		
		p2.setPreferredSize(new Dimension(480,550));
		p2.setLayout(null);
		
		laback.setBounds(2, 2, 476, 546);	
		laback.setIcon(new ImageIcon(MainGame.class.getResource(str_back)));	
		labts.setBounds(290, 20, 100, 40);
		labts.setFont(new Font("宋体",Font.BOLD,14));
		labts.setBorder(BorderFactory.createLoweredBevelBorder());
		labnu.setBounds(90, 20, 100, 40);
		labnu.setBorder(BorderFactory.createLoweredBevelBorder());
		labnu.setFont(new Font("宋体",Font.BOLD,14));
		buttop.setBounds(220, 20, 40, 40);
		buttop.setBorder(BorderFactory.createRaisedBevelBorder());
		setButtop(str_ks1);
		lab_fh.setBounds(390,500,50,20);
		lab_fh.setForeground(Color.YELLOW);
		pan1.setBounds(127,130,225,225);
		pan1.setLayout(new GridLayout(9,9));
		
		p2.add(laback,-1);
		p2.add(labnu,0);
		p2.add(buttop,0);
		p2.add(labts,0);
		p2.add(pan1,0);
		p2.add(lab_fh,0);
		add(p2);	
		
		class MyMouseListenPan2 extends MouseAdapter{
			public void mouseClicked(MouseEvent e){
				if(e.getSource()==buttop){
					restartGame(level);
				}
				else if(e.getSource()==lab_fh){
					pause=true;
					t=0;
					timeRunnable.resetSecond();
					card.previous(MainGame.this);
				}
			}
			public void mouseEntered(MouseEvent e){
				if(e.getSource()==lab_fh)
					lab_fh.setForeground(Color.RED);
			}
			public void mouseExited(MouseEvent e){
				if(e.getSource()==lab_fh)
					lab_fh.setForeground(Color.YELLOW);
			}
		}
		MyMouseListenPan2 mylistenpan2=new MyMouseListenPan2();
		buttop.addMouseListener(mylistenpan2);
		lab_fh.addMouseListener(mylistenpan2);
	}
	//设置数字标签
	private void setLabNum(int i){
		String str=String.valueOf(i);
		labnu.setText("红旗："+str);
	}
	//设置按钮图片
	private void setButtop(String str){
		buttop.setIcon(
				new ImageIcon(MainGame.class.getResource(str)));
	}

	//产生新窗口用于输入用户名
	private void InputYourName(){
		JFrame fra=new JFrame("恭喜！破纪录");
		fra.setSize(200,120);
		fra.setLocation(300, 200);
		fra.setResizable(false);
		fra.addWindowListener(new WindowAdapter(){
			public void windowClosing(WindowEvent e){
				fra.dispose();
			}
		});
		
		Container c=fra.getContentPane();
		((JPanel)c).setOpaque(false);
		c.setLayout(null);
		
		JTextField text=new JTextField();
		text.setBounds(20, 10, 160, 40);
		text.setBorder(BorderFactory.createTitledBorder("请输入用户名："));
		
		JButton but1=new JButton("确定");
		JButton but2=new JButton("取消");
		but1.setBounds(40,60,40,20);
		but1.setBorder(BorderFactory.createRaisedBevelBorder());
		but1.setFont(new Font("宋体",Font.BOLD,12));
		but1.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				GameUser user=new GameUser();
				user.setName(text.getText());
				user.setTime(t);
				switch(level){
				case 1:
					user.setRank("初级");break;
				case 2:
					user.setRank("中级");break;
				}
				rankUtils.updataRank(user);
				fra.dispose();
			}
		});
		but2.setBounds(120,60,40,20);
		but2.setBorder(BorderFactory.createRaisedBevelBorder());
		but2.setFont(new Font("宋体",Font.BOLD,12));
		but2.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e)
			{
				fra.dispose();
			}
		});
		
		c.add(text);
		c.add(but1);
		c.add(but2);
		
		fra.setVisible(true);
	}
	
	
}







