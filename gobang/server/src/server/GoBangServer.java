package server;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import sun.misc.BASE64Encoder;
import server.GoBangParams;

public class GoBangServer implements GoBangParams{

	private JFrame frame;
	private JTextArea text;
	
	private ServerSocket server;
	private ArrayList<Socket> socketLists;

	public GoBangServer()throws Exception{
		initView();
		socketLists=new ArrayList<Socket>();
		server=new ServerSocket(30000);
		while(true){
			//等待玩家1接入
			Socket player1=server.accept();
			socketLists.add(player1);
			text.append("玩家1接入\n");
			doHandshake(player1);
			String p1msg=readData(player1.getInputStream());
			text.append(p1msg+"\n");
			sendData(player1.getOutputStream(),CMD_PLAYER_WHO+":"+PLAYER1);
			//等待玩家2接入
			Socket player2=server.accept();
			socketLists.add(player2);
			text.append("玩家2接入\n");
			doHandshake(player2);
			String p2msg=readData(player2.getInputStream());
			text.append(p2msg+"\n");
			sendData(player2.getOutputStream(),CMD_PLAYER_WHO+":"+PLAYER2);
			
			GameTask task=new GameTask(player1,player2);
			task.setP1P2Msg(p1msg, p2msg);
			
			new Thread(task).start();
			
			break;
		}
	}
	

	/**
	 * 发送数据
	 * */
	public void sendData(OutputStream out,String data)throws IOException{
		byte[] head=new byte[2];
		head[0]=(byte)0x81;
		byte[] content=data.getBytes("UTF-8");
		head[1] = (byte)content.length;
		out.write(head);
		out.write(content);
	}
	
	/**
	 * 读取数据
	 * */
	public String readData(InputStream in)throws IOException{
		byte[] buff=new byte[1000];
		int count=in.read(buff);
		if(count>6){
			for(int i=0;i<count-6;i++){
				buff[i+6]= (byte)(buff[i % 4 +2] ^ buff[i+6]);
			}
	
			return new String(buff,6,count-6,"UTF-8");
		} else {
			return "";
		}
	}
	
	
	/**
	 * 与webSocket进行握手连接
	 * */
	private void doHandshake(Socket socket)throws IOException{
		InputStream in=socket.getInputStream();
		OutputStream out=socket.getOutputStream();
		
		byte[] buff=new byte[1024];
		int count=-2;
		String req;
		//获取请求
		count=in.read(buff);
		req=new String(buff,0,count);
		System.out.println(req);
		//获取key
		String secKey=getSecWebSocketKey(req);
		System.out.println(secKey);
		text.append("secKey："+secKey+"\n");
		//获取accept
		String accept = getSecWebSocketAccept(secKey);
		System.out.println("accept:"+accept);
		text.append("accept:"+accept+"\n");
		//发送应答
		String response="HTTP/1.1 101 Switching Protocols\r\n"
				+ "Upgrade:websocket\r\n"
				+ "Connection:Upgrade\r\n"
				+ "Sec-WebSocket-Accept:"
				+ accept+"\r\n\r\n";
		out.write(response.getBytes());
	}
	/**
	 * 得到webSocket的请求SecKey
	 * */
	private String getSecWebSocketKey(String req){
		Pattern p=Pattern.compile("^(Sec-WebSocket-Key:).+",
				Pattern.CASE_INSENSITIVE |Pattern.MULTILINE);
		Matcher m=p.matcher(req);
		if(m.find()){
			String found=m.group();
			return found.split(":")[1].trim();
		} else {
			return null;
		}
	}
	/**
	 * 根据请求key，计算accept
	 * */
	private String getSecWebSocketAccept(String key){
		String guid="258EAFA5-E914-47DA-95CA-C5AB0DC85B11";
		key += guid;
		try{
			MessageDigest md=MessageDigest.getInstance("SHA-1");
			md.update(key.getBytes("ISO-8859-1"),0,key.length());
			byte[] sha1Hash=md.digest();
			BASE64Encoder encoder=new BASE64Encoder();
			return encoder.encode(sha1Hash);
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * 初始化界面
	 * */
	private void initView(){
		frame=new JFrame("WEB SOCKET SERVER");
		frame.setSize(400, 600);
		frame.setResizable(false);
		frame.setLocation(100, 30);
		frame.addWindowListener(new WindowAdapter(){
			public void windowClosing(WindowEvent e){
				try{
					for(Socket s:socketLists){
						s.close();
					}
					server.close();
				}catch(IOException e2){
					e2.printStackTrace();
				}
				frame.dispose();
				System.out.println("已关闭");
			}
		});
		
		Container c=frame.getContentPane();
		JPanel panel=new JPanel();
		panel.setLayout(new BorderLayout());

		text=new JTextArea();
		text.setEditable(false);
		
		JScrollPane scroll = new JScrollPane(text); 
		//分别设置水平和垂直滚动条自动出现 
		scroll.setHorizontalScrollBarPolicy( 
		JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED); 
		scroll.setVerticalScrollBarPolicy( 
		JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED); 
		
		panel.add(scroll,BorderLayout.CENTER);
		c.add(panel);
		
		frame.setVisible(true);
	}
	
	//main
	public static void main(String[] args) {
		try{
			new GoBangServer();
		}catch(Exception e){
			e.printStackTrace();
		}

	}

}
