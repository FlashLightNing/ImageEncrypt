package bishe;

import java.awt.Dimension;
import java.awt.FileDialog;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.plaf.DimensionUIResource;

import com.sun.org.apache.bcel.internal.util.ByteSequence;
import com.sun.security.ntlm.Client;

public class client extends JFrame {

	private JTextArea testArea = new JTextArea();
	private JTextField textField = new JTextField();
	private JButton btSend = new JButton("发送");
	JFrame jframe;
	private Socket socket = null;
	InputStream in = null;
	OutputStream out = null;
	private String imgPath = null;

	

	public static void main(String[] args) {
		client c = new client();
		c.setClient();
	}

	public void setClient() {
		systenUI();
		try {
			socket = new Socket("localhost", 9090);
			System.out.println("连接上");
			in = socket.getInputStream();
			out = socket.getOutputStream();
			// sendImage(socket);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("启动");
		// new Thread(this).start();
	}

	
	// @Override
	// public void run() {
	// while (true) {
	// try {
	// sendImage(socket);(socket);
	// } catch (IOException e) {
	// e.printStackTrace();
	// }
	// }
	// }

	public void sendImage(Socket socket, String imgPath) {
		int length = 0;
		double sumL = 0;
		byte[] sendBytes = null;
		DataOutputStream dos = null;
		FileInputStream fis = null;
		boolean bool = false;
		try {
			File file = new File(imgPath); // 要传输的文件路径
			long l = file.length();

			dos = new DataOutputStream(socket.getOutputStream());
			fis = new FileInputStream(file);
			sendBytes = new byte[1024];
			while ((length = fis.read(sendBytes, 0, sendBytes.length)) > 0) {
				sumL += length;
				System.out.println("已传输：" + ((sumL / l) * 100) + "%");
				dos.write(sendBytes, 0, length);
				dos.flush();
			}
			// 虽然数据类型不同，但JAVA会自动转换成相同数据类型后在做比较
			if (sumL == l) {
				System.out.println("长度="+l);
				bool = true;
			}
		} catch (Exception e) {
			System.out.println("客户端文件传输异常");
			bool = false;
			e.printStackTrace();
		} finally {
			try {
				if (dos != null)
					dos.close();
				if (fis != null)
					fis.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		System.out.println(bool ? "成功" : "失败");
		
	}

	

	
	
	public String openAs() {
		String path = null;
		try {
			// JFrame jf =new JFrame("测试");
			JTextArea jtext = new JTextArea(10, 10);
			FileDialog fdopen = new FileDialog(jframe, "打开", FileDialog.LOAD);// 框属性为"LOAD加载"，附于JFrame对象
			fdopen.setVisible(true);
			path = fdopen.getDirectory() + fdopen.getFile();
			
			File file =new File("E:/exist.txt");
			if(!file.exists()){
				file.createNewFile();
			}
			
		} catch (Exception ess) {
		}
		System.out.println(path );
		return path;
	}


	public void sendMsg(OutputStream out, String msg) {
		msg += "\r\n";
		try {
			out.write(msg.getBytes());
			out.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public void systenUI() {
		jframe = new JFrame("图片传输");
		jframe.setSize(new Dimension(400, 420));
		jframe.setLocationRelativeTo(null);
		jframe.setLayout(new FlowLayout());

		JTextField textField = new JTextField(30);
		JTextArea textArea = new JTextArea(20, 30);

		JButton openButton = new JButton("浏览");
		openButton.setSize(new DimensionUIResource(20, 20));

		JButton sendButton = new JButton("发送");
		sendButton.setSize(new DimensionUIResource(20, 20));

		jframe.add(textArea);
		jframe.add(textField);
		jframe.add(openButton);
		jframe.add(sendButton);

		jframe.setVisible(true);
		openButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				imgPath = openAs();
			}
		});

		sendButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				System.out.println("准备发送");
				sendMsg(out, "start");
				sendImage(socket, imgPath);
				System.out.println("发送路径:" + imgPath);
			}
		});

	}

}
