package com.main;
import java.awt.Image;
import javax.swing.JPanel;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
 
 
public class Main
{
	// GUI框架
	static private JFrame jframe;
	// 显示时间的控件
	static private JLabel displayArea;
	static private JLabel timeLabel;
	static private String time;
	static private String DEFAULT_TIME_FORMAT = "HH:mm:ss";
	static private int ONE_SECOND = 1000;
	/**
	 * 获得当前用户壁纸的路径
	 * @return
	 */
	private static String getPath()
	{
		File[] fileArr = new File("C:\\Users\\王发利\\AppData\\Roaming\\Microsoft\\Windows\\Themes\\CachedFiles")
				.listFiles();// win10下的用户设置的壁纸
		File maxSize = fileArr[0];
		for (File Temp : fileArr)
		{
			if (Temp.length() > maxSize.length())
				maxSize = Temp;
		}
		return maxSize.getPath();// 返回最大文件的路径，即最高清晰度
	}
	/**
	* 内部类
	* 获取当前系统动态时间
	* Copyright: Copyright (c) 2019 flwang
	* 
	* @ClassName: Main.java
	* @Description: 该类的功能描述
	*
	* @version: v1.0.0
	* @author: 王发利
	* @date: 2019年1月8日 下午1:45:00
	 */
	protected static class JLabelTimerTask extends TimerTask {
		SimpleDateFormat dateFormatter = new SimpleDateFormat(DEFAULT_TIME_FORMAT);

		@Override
		public void run() {
			time = dateFormatter.format(Calendar.getInstance().getTime());
			// 显示时间的控件显示时间
			displayArea.setText(time);
		}
	}
	
	/**
	 * 开启锁屏界面
	 */
	private static void startScreenSaver(){
		// 当前屏幕的大小
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		// 初始化GUI框架
		jframe = new JFrame();
		// 设置布局
		jframe.setLayout(null);
		// 无边框
		jframe.setUndecorated(true);
		
		Rectangle bounds = new Rectangle(screenSize);
		jframe.setBounds(bounds);
		Image image = new ImageIcon(getPath()).getImage();
		// 添加JPanel，在JPanel上添加图片
		JPanel jp = new BackgroundPanel(image);
		jp.setLayout(null);
		jp.setBounds(jframe.getBounds());
		// 框架上增加图片面板
		jframe.add(jp);
		
		// 显示时间
		Timer tmr = new Timer();
		timeLabel = new JLabel("CurrentTime: ");
		displayArea = new JLabel();
		tmr.scheduleAtFixedRate(new JLabelTimerTask(),new Date(), ONE_SECOND);
		timeLabel.setBounds(0,50,230,30);
		timeLabel.setForeground(Color.white);
		timeLabel.setFont(new java.awt.Font("Dialog", 1, 25));
		displayArea.setBounds(300,50,200,30);
		displayArea.setFont(new java.awt.Font("Dialog", 1, 35));
		displayArea.setForeground(Color.white);
		jp.add(timeLabel);
		jp.add(displayArea);
 
		// 增加退出按钮
		JButton exit = new JButton("退出");
		exit.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent arg0)
			{
//				System.exit(0);
				jframe.setVisible(false);
			}
		});
		exit.setBounds(0, 0, 100, 30);
		// 退出按钮默认可见
		exit.setVisible(true);
		jp.add(exit);

		// 密码输入
		JPasswordField jtf = new JPasswordField();
		jtf.setBounds((screenSize.width / 2) - 100, screenSize.height - 100, 200, 30);
		jtf.addKeyListener(new KeyListener()
		{
 
			@Override
			public void keyTyped(KeyEvent e){
			}
 
			@Override
			public void keyReleased(KeyEvent e){
			}
 
			@Override
			public void keyPressed(KeyEvent e)// 如按了指定的键，则真正的锁屏
			{
				if (17 == e.getKeyCode() || 18 == e.getKeyCode() || 110 == e.getKeyCode() || 115 == e.getKeyCode()
						|| 192 == e.getKeyCode())
				{
					try
					{
//						Runtime.getRuntime().exec("shutdown /s /t " + 0);
						Runtime.getRuntime().exec("RunDll32.exe user32.dll,LockWorkStation");
					} catch (IOException e1)
					{
						e1.printStackTrace();
					}
				}
			}
		});
		jp.add(jtf);
		// 锁定按钮
		JButton lockButton = new JButton("锁定");
		lockButton.addActionListener(new ActionListener()
		{
			boolean lockFlag = false;
			String password = null;
 
			public void actionPerformed(ActionEvent arg0)
			{
				if (true == lockFlag)
				{
					String input = new String(jtf.getPassword());
					
					if (input.equals(password))
					{
						lockFlag = false;
						lockButton.setText("锁定");
						jtf.setText("");
						exit.setVisible(true);
					} else
					{
						JOptionPane.showMessageDialog(jtf, "密码错误", "提示", 0);
						jtf.setText("");
					}
				} else {
					String input = new String(jtf.getPassword());
					if (false == input.equals(""))
					{
						password = input;
						lockFlag = true;
						jtf.setText("");
						lockButton.setText("解锁");
						exit.setVisible(false);
					} else
					{
						JOptionPane.showMessageDialog(jtf, "请输入密码", "提示", 1);
					}
				}
			}
		});
		lockButton.setBounds((screenSize.width / 2) - 50, screenSize.height - 50, 100, 30);
		jp.add(lockButton);
 
		jframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		jframe.setVisible(true);
	}
	
	/**
	 *  退出锁屏界面
	 */
	private static void endScreenerSaver(){
		jframe.setVisible(false);
	}

	/**
	 * 主方法
	 * @param args
	 */
	public static void main(String[] args){
		// 开启一个线程监听蓝牙的距离
		Thread t = new Thread(new Runnable(){
			@Override
			public void run() {
				// 接收蓝牙距离的socket
				ServerSocket ss = null;
				Socket s = null;
				try {
					ss = new ServerSocket(16666);
					System.out.println("等待连接......");
					// 接收socket连接
					s = ss.accept();
					System.out.println("a client connect!");
					String str = null;
					BufferedReader br = new BufferedReader(new InputStreamReader(s.getInputStream()));
					PrintWriter pw = new PrintWriter(new OutputStreamWriter(s.getOutputStream()));
					str = br.readLine();
					while (!"bye".equals(str)) {
						if("bye".equals(str)){break;};
						System.out.println("蓝牙距离：" + str);
						// m为单位
						double distance = Double.valueOf(str);
						// 如果小于10cm，则解除锁屏，否则进入锁屏
						if (distance < 0.1) {
							// 结束锁屏
							endScreenerSaver();
						} else {
							// 开始锁屏
							startScreenSaver();
						}
//							pw.println(str.toUpperCase());
//							pw.flush();
						s = ss.accept();
						System.out.println("a client connect!");
						br = new BufferedReader(new InputStreamReader(s.getInputStream()));
						pw = new PrintWriter(new OutputStreamWriter(s.getOutputStream()));
						str = br.readLine();
					}
					System.out.println("程序结束，bye!");
				} catch (IOException e) {
					e.printStackTrace();
				} finally {
					try {
						s.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
				
		});
		System.out.println("蓝牙线程监听开始运行...");
		t.start();
	}
}
