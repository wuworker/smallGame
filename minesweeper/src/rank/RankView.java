package rank;

import java.awt.Color;
import java.awt.Container;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
/**
 * 显示排行榜的视图
 * */
public class RankView {
	
	private static final String BACK_PIC="/picture/back3.png";
	private JFrame fra;
	private JLabel[][] labname=new JLabel[11][2];
	
	private RankUtils rankUtils;
	
	public RankView(RankUtils rankUtils){
		this.rankUtils=rankUtils;
		initView();
	}
	//显示
	public void show(){
		updataView();
		fra.setVisible(true);
	}
	//销毁
	public void dispose(){
		fra.dispose();
	}
	//更新排行
	public void updataView(){
		for(int i=1;i<11;i++){
			labname[i][0].setText(rankUtils.getRankList1().get(i-1).getName());
			labname[i][1].setText(String.valueOf(
					rankUtils.getRankList1().get(i-1).getTime()));
		}
	}
	//初始化
	private void initView(){
		fra=new JFrame("扫雷排行榜");
		fra.setSize(480,550);
		fra.setLocation(330, 30);
		fra.setResizable(false);
		fra.addWindowListener(new WindowAdapter(){
			public void windowClosing(WindowEvent e)
			{
				fra.dispose();
			}
		});
		
		ImageIcon img=new ImageIcon(RankView.class.getResource(BACK_PIC));
		JLabel back=new JLabel(img);
		back.setBounds(0, 0, 480, 550);
		fra.getLayeredPane().add(back,new Integer(Integer.MIN_VALUE));
		
		Container c=fra.getContentPane();
		((JPanel)c).setOpaque(false);
		c.setLayout(null);
		
		JLabel labt=new JLabel("排  行  榜",JLabel.CENTER);
		labt.setForeground(Color.white);
		labt.setBounds(40, 0, 400, 40);
		labt.setFont(new Font("宋体",Font.BOLD,20));
		
		JRadioButton jrb1=new JRadioButton("初级",true);
		JRadioButton jrb2=new JRadioButton("中级");
		ButtonGroup bg=new ButtonGroup();
		jrb1.setBounds(130,40,100,30);
		jrb2.setBounds(300,40,100,30);
		jrb1.setContentAreaFilled(false);
		jrb2.setContentAreaFilled(false);
		bg.add(jrb1);
		bg.add(jrb2);
		
		JPanel pan=new JPanel();
		pan.setBounds(40, 70, 400, 400);
		pan.setOpaque(false);
		pan.setBorder(BorderFactory.createRaisedBevelBorder());
		pan.setLayout(new GridLayout(11,2));

		
		for(int i=0;i<labname.length;i++){
			for(int j=0;j<labname[i].length;j++){
				labname[i][j]=new JLabel("1",JLabel.CENTER);
				labname[i][j].setFont(new Font("宋体",Font.BOLD,15));
				pan.add(labname[i][j]);
			}
		}
		
		labname[0][0].setText("用户名");
		labname[0][1].setText("时间");
		
		jrb1.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				for(int i=1;i<11;i++){
					labname[i][0].setText(rankUtils.getRankList1().get(i-1).getName());
					labname[i][1].setText(String.valueOf(
							rankUtils.getRankList1().get(i-1).getTime()));
				}
			}
		});
		
		jrb2.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				for(int i=1;i<11;i++){
					labname[i][0].setText(rankUtils.getRankList2().get(i-1).getName());
					labname[i][1].setText(String.valueOf(
							rankUtils.getRankList2().get(i-1).getTime()));
				}
			}
		});
		
		
		JButton but=new JButton("确定");
		but.setBounds(220,480,40,20);
		but.setBorder(BorderFactory.createRaisedBevelBorder());
		but.setFont(new Font("宋体",Font.BOLD,12));
		but.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				fra.dispose();
			}
		});
		
		c.add(labt);
		c.add(jrb1);
		c.add(jrb2);
		c.add(pan);
		c.add(but);

		fra.dispose();
	}
	
	
}
