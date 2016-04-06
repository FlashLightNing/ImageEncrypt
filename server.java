package bishe;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FileDialog;
import java.awt.FlowLayout;
import java.awt.Image;
import java.awt.color.ColorSpace;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.awt.image.ColorConvertOp;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
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
import java.net.ServerSocket;
import java.net.Socket;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.plaf.DimensionUIResource;

public class server extends Thread {

	private OutputStream out = null;
	private InputStream in = null;
	private Socket socket;
	ServerSocket serverSocket = null;
	JFrame jframe;

	public static void main(String[] args) {
		// String path = "G:/matlab/bin/original_color_image_1.jpg";
		// String path2 = "E:/abc_gray.jpg";
		// new Main().arnoldChange(path2);

		server ma = new server();
		ma.systenUI();

		try {
			ma.setServer(9090);
		} catch (IOException e) {
			e.printStackTrace();
		}
		ma.start();

	}

	public void setServer(int port) throws IOException {
		serverSocket = new ServerSocket(port);
	}

	@Override
	public void run() {

		while (true) {
			try {
				System.out.println("开始监听");
				Socket socket = serverSocket.accept();
				System.out.println("有链接");
				receiveFile(socket);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public String receiveMessage(InputStream in) {
		String text = null;
		BufferedReader reader = new BufferedReader(new InputStreamReader(in));
		try {
			text = reader.readLine();

		} catch (IOException e) {
			e.printStackTrace();
		}

		return text;
	}

	public void receiveFile(Socket socket) {
//		DataInputStream dis = null;
//		FileOutputStream fos = null;
			try {
				/*
				 * 文件存储位置
				 */

				System.out.println("开始接收数据...");
				int N = 0;
				
				BufferedReader bfr = new BufferedReader(new InputStreamReader(
						socket.getInputStream()));
				String start = bfr.readLine();
				N=Integer.parseInt(start);
				System.out.println(N);
				String[][] aa = new String[N + 2][N + 2];
				int i = 0;
				int j = 0;
				double all=(double)(N*N);
				while (!start.equals("end")) {
					start = bfr.readLine();
					aa[i][j] = start;
					System.out.println("aa[" + i + "," + j + "]=" + start+",接收:"+((i*N+j)/all)*100+"%");
					j++;
					if (j == N) {
						j = 0;
						i++;
					}
				}

				JOptionPane.showConfirmDialog(jframe, "一张图片等待接收...", "图片",
						JOptionPane.YES_OPTION);
				String path2 = saveAs();

				changeArrayToImage(N,aa, path2);
				System.out.println("完成接收：" + path2 );
				JOptionPane.showConfirmDialog(jframe, "接收完成,保存地址:"+path2,"提示",JOptionPane.YES_OPTION);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void changeArrayToImage(int N,String[][] array, String path)
			throws IOException {
		
		double[][] end = new double[N][N];
		for (int i = 0; i < N; i++)
			for (int j = 0; j < N; j++) {
				end[i][j] = Double.parseDouble(array[i][j]);
			}

		File file = new File(path);
		String location = file.getParent().substring(0, 1) + ":/";

		// 混沌解密
		String after_De_ChaoticEncrypt_ImagePath = location
				+ "after_De_ChaoticEncrypt_ImagePath.jpg";

		ImageUtils.deChaoticEncrypt(end, after_De_ChaoticEncrypt_ImagePath);

		// 猫脸逆映射之后得到的图片地址
		// String afterInverseArnoldChangeImagePath = "d:/f.jpg";
		ImageUtils.inverseArnoldChange(after_De_ChaoticEncrypt_ImagePath, path,
				5, 15, 20);
		System.out.println("逆映射成功");
	}

	public String saveAs() {
		String path = null;
		System.out.println("打开");
		try {
			// jframe = new JFrame("测试");
			JTextField jtext = new JTextField(10);
			FileDialog fd = new FileDialog(jframe, "另存为", FileDialog.SAVE);// 框属性为"SAVE保存"，附于JFrame对象
			fd.setVisible(true);
			path = fd.getDirectory() + fd.getFile() + ".jpg";

			// FileOutputStream out = new FileOutputStream(fd.getDirectory()
			// + fd.getFile() + ".txt");// 存为.txt格式，文件名为你输入的字符串
			// String str = jtext.getText();
			// out.write(str.getBytes());
			// out.close();
		} catch (Exception ess) {
		}

		return path;
	}

	public void systenUI() {
		jframe = new JFrame("图片传输");
		jframe.setSize(new Dimension(300, 300));
		jframe.setLocationRelativeTo(null);
		jframe.setLayout(new FlowLayout());

		JTextField textField = new JTextField(10);
		JTextArea textArea = new JTextArea(10, 20);

		JButton openButton = new JButton("打开");
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
				JFileChooser fileChooser = new JFileChooser("文件选择器");
				fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
				fileChooser.showDialog(new JLabel(), "选择");
				File file = fileChooser.getSelectedFile();
				if (file != null) {
					System.out.println(file.getAbsolutePath());
				}
			}
		});

		sendButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String msg = "你好";
				// sendMsg(out, msg);
				System.out.println("发送成功");
			}
		});

	}

}
