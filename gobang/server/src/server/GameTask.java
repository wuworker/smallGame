package server;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class GameTask implements Runnable,GoBangParams{

	private Socket p1;
	private Socket p2;
	private String p1Msg;
	private String p2Msg;
	
	public GameTask(Socket p1,Socket p2){
		this.p1=p1;
		this.p2=p2;
	}
	
	public void setP1P2Msg(String p1,String p2){
		p1Msg=p1;
		p2Msg=p2;
	}
	
	public void run(){
		try{
			MyInputStream in1 = new MyInputStream(p1.getInputStream());
			MyOutputStream out1 = new MyOutputStream(p1.getOutputStream());
			MyInputStream in2 = new MyInputStream(p2.getInputStream());
			MyOutputStream out2 = new MyOutputStream(p2.getOutputStream());

			out1.write(p2Msg);
			out2.write(p1Msg);
			
			CommutativeTask p1top2 = new CommutativeTask(in1,out2);
			CommutativeTask p2top1 = new CommutativeTask(in2,out1);
			
			new Thread(p1top2).start();
			new Thread(p2top1).start();

		}catch(IOException e){
			e.printStackTrace();
		}
	}
	/**
	 * 互相发消息
	 * */
	class CommutativeTask implements Runnable{
		
		private MyInputStream in;
		private MyOutputStream out;
		
		public CommutativeTask(MyInputStream in,MyOutputStream out){
			this.in=in;
			this.out=out;
		}
		public void run(){
			try{
				while(true){
					
					String data = in.read();
					if(data==null){
						out.close();
						break;
					} else {
						out.write(data);
					}
				}
			}catch(IOException e){
				e.printStackTrace();
			}finally{
				System.out.println("CommutativeTask结束");
			}
		}
	}
	
	
	/**
	 * 自定义的输出流
	 * */
	class MyOutputStream{
		private OutputStream out;
		
		public MyOutputStream(OutputStream out){
			this.out = out;
		}
		
		public void write(String cmd, String data)throws IOException{
			byte[] head=new byte[2];
			head[0]=(byte)0x81;
			byte[] content = (cmd+":"+data).getBytes("UTF-8");
			head[1] = (byte)content.length;
			out.write(head);
			out.write(content);
		}
		
		public void write(String data)throws IOException{
			byte[] head=new byte[2];
			head[0]=(byte)0x81;
			byte[] content = data.getBytes("UTF-8");
			head[1] = (byte)content.length;
			out.write(head);
			out.write(content);
		}
		
		public void close()throws IOException{
			if(out!=null){
				out.close();
				out=null;
			}
		}
	}
	/**
	 * 自定义的输入流
	 * */
	class MyInputStream{
		private InputStream in;
		
		public MyInputStream(InputStream in){
			this.in=in;
		}
		
		public String read()throws IOException{
			byte[] buff=new byte[100];
			int count=in.read(buff);
			if(count > 6){
				for(int i=0;i<count-6;i++){
					buff[i+6]= (byte)(buff[i % 4 +2] ^ buff[i+6]);
				}
				
				return new String(buff,6,count-6,"UTF-8");
			} else {
				return null;
			}
		}
	}

}








