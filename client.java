package bishe;

import java.awt.Dimension;
import java.awt.FileDialog;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
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

import javax.imageio.ImageIO;
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
	private int N = 0;

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
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("启动");
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

	public double[][] getImageArray(String srcPath) {

		// 判断图像类别,是否为JPEG图像
		boolean is_Jpeg = false;
		try {
			is_Jpeg = ImageUtils.getImageType(srcPath);
		} catch (IOException e) {
			e.printStackTrace();
		}

		String jpeg_image_path = srcPath;

		File file = new File(srcPath);
		String location = file.getParent().substring(0, 1) + ":/";
		String newName = file.getName().split("\\.")[0] + ".jpg";
		String newPath = location + newName;

		// 如果不是JPEG类别的图像,进行处理
		if (!is_Jpeg) {
			System.out.println("非");
			try {
				ImageUtils.convertPicture(srcPath, newPath, "JPEG");
				BufferedImage im = null;
				try {
					im = ImageIO.read(new File(newPath));
				} catch (IOException e) {
					e.printStackTrace();
				}
				if (N == 0) {
					N = im.getHeight();
				}
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		} else {
			newPath = srcPath;
		}
		System.out.println("是");

		// 灰度图像地址
		String gray_image_path = location + "gray.jpg";

		ImageUtils.gray(newPath, gray_image_path);

		String after_ArnoldChange_ImagePath = location
				+ "after_arnoldChange_image_path.jpg";

		double[][] a = ImageUtils.arnoldChange(gray_image_path,
				after_ArnoldChange_ImagePath, 5, 15, 20);

		// 混沌加密得到的图片路径
		String after_ChaoticEncrypt_ImagePath = location
				+ "after_ChaoticEncrypt_ImagePath.jpg";

		double[][] ar = ImageUtils.chaoticEncrypt(a,
				after_ArnoldChange_ImagePath, after_ChaoticEncrypt_ImagePath);

		return ar;
	}

	public void sendImage(Socket socket, String imgPath, double[][] array) {
		int length = 0;
		double sumL = 0;
		DataOutputStream dos = null;
		FileInputStream fis = null;
		boolean bool = false;
		try {

			double[][] arr = getImageArray(imgPath);
			System.out.println("得到发送的数组");
			double all = (double) (N * N);
			for (int i = 0; i < N; i++)
				for (int j = 0; j < N; j++) {
					sendMsg(out, String.valueOf(arr[i][j]));
					double process = (i * N + j) / all;
					System.out.println("发送:arr[" + i + "," + j + "]="
							+ arr[i][j] + "," + (process * 100) + "%");
				}
			sendMsg(out, "end");

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

	public Object[] openAs() {
		String newPath = null;
		try {
			JTextArea jtext = new JTextArea(10, 10);
			FileDialog fdopen = new FileDialog(jframe, "打开", FileDialog.LOAD);// 框属性为"LOAD加载"，附于JFrame对象
			fdopen.setVisible(true);
			String path = fdopen.getDirectory() + fdopen.getFile();
			
			File file = new File(path);
			String location = file.getParent().substring(0, 1) + ":/";
			String newName = file.getName().split("\\.")[0] + ".jpg";
			 newPath = location + newName;
			
			ImageUtils.convertPicture(path, newPath, "JPEG");
			BufferedImage image=ImageIO.read(new File(newPath));
			N=image.getHeight();

		} catch (Exception ess) {
		}
		System.out.println(newPath);
		return new Object[] { newPath, N };
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

		final double[][] ar = new double[10][10];
		openButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				Object[] params = openAs();
				imgPath = (String) params[0];
				N = (int) params[1];
				System.out.println(N);
			}
		});

		sendButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				System.out.println("准备发送");
				sendMsg(out, String.valueOf(N));
				sendImage(socket, imgPath, ar);
				System.out.println("发送路径:" + imgPath);
			}
		});

	}

}
