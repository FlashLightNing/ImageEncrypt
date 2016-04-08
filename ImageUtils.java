package bishe;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.color.ColorSpace;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.awt.image.ColorConvertOp;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.imageio.ImageIO;
import javax.media.jai.JAI;
import javax.media.jai.RenderedOp;

import com.sun.media.jai.codec.ImageCodec;
import com.sun.media.jai.codec.ImageEncoder;
import com.sun.media.jai.codec.JPEGEncodeParam;

/**
 * 图片处理工具类：<br>
 * 功能：缩放图像、切割图像、图像类型转换、彩色转黑白、文字水印、图片水印等
 * 
 * @author Administrator
 */
public class ImageUtils {

	public boolean is_send = false;
	private static String imgType="JPEG";

	/**
	 * 程序入口：用于测试
	 * 
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {

		String outputPath = "E:/te.jpg";

		// // 2-切割图像：
		// // 方法一：按指定起点坐标和宽高切割
		ImageUtils.scale2(outputPath, "E:/abc_sc.jpg", true);// 测试OK

		// 4-彩色转黑白：
		// String path = "E:/original_color_image_1.jpeg";
		// String path2 = "E:/abc_gray.jpg";
		String grayImage = "E:/abc_sc.jpg";
		// String grayImage = "E://lena.tiff";

		boolean is_Jpeg = getImageType(grayImage);

		if (!is_Jpeg) {
			System.out.println("非JPEG转JPEG");
			convertPicture(grayImage, outputPath, imgType);
		} else {
			outputPath = grayImage;
		}
		System.out.println("已为jpeg");
		// ImageUtils.getGrayPicture(path,path3);

		String grayImagePath = "E:/b.jpg";
		// ImageUtils.gray(path, grayImagePath);

		// 猫脸映射之后得到的图片路径
		String after_ArnoldChange_ImagePath = "E:/d.jpg";

		double[][] a = ImageUtils.arnoldChange(outputPath,
				after_ArnoldChange_ImagePath, 5, 15, 20);

		// 混沌加密得到的图片路径
		String after_ChaoticEncrypt_ImagePath = "E:/w.jpg";

		double[][] ar = ImageUtils.chaoticEncrypt(a,
				after_ArnoldChange_ImagePath, after_ChaoticEncrypt_ImagePath,
				1.5, 1.2, 0.3);

		// double[][] br = ImageUtils.chaoticEncrypt(a,
		// afterArnoldChangeImagePath, afterChaoticEncryptImagePath);

		// 归一化
		// Object[] params = ImageUtils.guiyihua(ar,
		// after_ChaoticEncrypt_ImagePath);
		//
		// double[][] ar2 = ImageUtils.deGuiyihua((String) params[0],
		// (double) params[1], (double) params[2], (double[][]) params[3]);
		//
		// System.out.println(ImageUtils.juede(ar, ar2));

		// // 混沌解密得到的图片途径
		String after_De_ChaoticEncrypt_ImagePath = "E:/h.jpg";

		ImageUtils.deChaoticEncrypt(ar, after_De_ChaoticEncrypt_ImagePath, 1.5,
				1.2, 0.3);

		// 猫脸逆映射之后得到的图片地址
		String afterInverseArnoldChangeImagePath = "E:/f.jpg";
		ImageUtils.inverseArnoldChange(after_De_ChaoticEncrypt_ImagePath,
				afterInverseArnoldChangeImagePath, 5, 15, 20);

	}

	public static int getMaxLenth(int width, int height) {
		int N = 0;
		// 取最大的值
		N = width < height ? width : height;
		N = N - N % 4;
		return N;
	}

	/**
	 * @param srcImageFile
	 *            原地址
	 * @param result
	 *            切割之后的图片地址，若原图像的大小已经符合要求,则: result=scrImageFile
	 * @param bb
	 * @return result
	 */
	public final static String scale2(String srcImageFile, String result,
			boolean bb) {
		int height = 0;
		int width = 0;
		try {
			double ratio = 0.0; // 缩放比例
			File f = new File(srcImageFile);
			BufferedImage bi = ImageIO.read(f);
			int w = bi.getWidth();
			int h = bi.getHeight();
			System.out.println(w);
			if ((w == h) && (w % 4 == 0)) {
				System.out.println("符合要求.不用切割");
				return srcImageFile;
			} else {
				int N = getMaxLenth(w, h);
				System.out.println(N);
				height = N;
				width = N;
			}

			Image itemp = bi.getScaledInstance(width, height, bi.SCALE_SMOOTH);
			// 计算比例
			if ((bi.getHeight() > height) || (bi.getWidth() > width)) {
				if (bi.getHeight() > bi.getWidth()) {
					ratio = (new Integer(height)).doubleValue()
							/ bi.getHeight();
				} else {
					ratio = (new Integer(width)).doubleValue() / bi.getWidth();
				}
				AffineTransformOp op = new AffineTransformOp(
						AffineTransform.getScaleInstance(ratio, ratio), null);
				itemp = op.filter(bi, null);
			}
			if (bb) {// 补白
				BufferedImage image = new BufferedImage(width, height,
						BufferedImage.TYPE_INT_RGB);
				Graphics2D g = image.createGraphics();
				g.setColor(Color.white);
				g.fillRect(0, 0, width, height);
				if (width == itemp.getWidth(null))
					g.drawImage(itemp, 0, (height - itemp.getHeight(null)) / 2,
							itemp.getWidth(null), itemp.getHeight(null),
							Color.white, null);
				else
					g.drawImage(itemp, (width - itemp.getWidth(null)) / 2, 0,
							itemp.getWidth(null), itemp.getHeight(null),
							Color.white, null);
				g.dispose();
				itemp = image;
			}
			ImageIO.write((BufferedImage) itemp, "JPEG", new File(result));
			System.out.println("切割成功");
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * 判断图片是否为JPEG的图片，若是，返回true;
	 * 
	 * @param imgPath
	 * @return
	 * @throws IOException
	 */
	public static boolean getImageType(String imgPath) throws IOException {
		// http://blog.csdn.net/fenglibing/article/details/7733496
		// 文件的最开头的几个用于唯一区别其它文件类型的字节，有了这些魔术数字，我们就可以很方便的区别不同的文件，
		// 这也使得编程变得更加容易，因为我减少了我们用于区别一个文件的文件类型所要花费的时间。
		// 比如，一个JPEG文件，它开头的一些字节可能是类似这样的”ffd8 ffe0 0010 4a46 4946 0001 0101 0047
		// ……JFIF…..G“，
		// 这里”ffd8“就表示了这个文件是一个JPEG类型的文件，”ffe0“表示这是JFIF类型结构。
		File image = new File(imgPath);
		InputStream is = new FileInputStream(image);
		byte[] bt = new byte[2];
		is.read(bt);
		StringBuilder stringBuilder = new StringBuilder();
		if (bt == null || bt.length <= 0) {
			return false;
		}
		for (int i = 0; i < bt.length; i++) {
			int v = bt[i] & 0xFF;
			String hv = Integer.toHexString(v);
			if (hv.length() < 2) {
				stringBuilder.append(0);
			}
			stringBuilder.append(hv);
		}
		String number = stringBuilder.toString();
		System.out.println(number);
		if (number.equals("ffd8")) {
			return true;
		}
		return false;
	}

	/**
	 * 将任意类型的转为JPEG类型的图片
	 * 若已经为JPEG图片,则直接返回原地址;否则返回处理后的地址
	 * @param inputPath
	 * @param outputPath
	 * @param type
	 * @throws IOException
	 */
	public static String convertPicture(String inputPath, String outputPath,
			String type) throws IOException {

		boolean is_Jpeg = getImageType(inputPath);
		if (is_Jpeg) {//若已经为JPEG图片,则不用处理,直接返回
			return inputPath;
		}

		RenderedOp src2 = JAI.create("fileload", inputPath);
		OutputStream os2 = new FileOutputStream(outputPath);
		JPEGEncodeParam param2 = new JPEGEncodeParam();

		// 指定格式类型，jpg 属于 JPEG 类型
		ImageEncoder enc2 = ImageCodec.createImageEncoder("JPEG", os2, param2);

		try {
			enc2.encode(src2);
			os2.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		System.out.println("转换成功");
		return outputPath;
	}

	/**
	 * 彩色转为黑白
	 * 
	 * @param srcImageFile
	 *            源图像地址
	 * @param destImageFile
	 *            目标图像地址
	 */
	public final static String gray(String srcImageFile, String destImageFile) {
		try {
			BufferedImage src = ImageIO.read(new File(srcImageFile));
			ColorSpace cs = ColorSpace.getInstance(ColorSpace.CS_GRAY);
			ColorConvertOp op = new ColorConvertOp(cs, null);
			src = op.filter(src, null);
			ImageIO.write(src, "JPEG", new File(destImageFile));

		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("得到灰度图片:" + destImageFile);
		return destImageFile;
	}

	public static void getGrayPicture(String srcImageFile, String destImageFile) {
		// http://bbs.51cto.com/thread-1091799-1.html
		BufferedImage originalImage = null;
		try {
			originalImage = ImageIO.read(new File(srcImageFile));

			int green = 0, red = 0, blue = 0, rgb;
			int imageWidth = originalImage.getWidth();
			int imageHeight = originalImage.getHeight();
			for (int i = originalImage.getMinX(); i < imageWidth; i++) {
				for (int j = originalImage.getMinY(); j < imageHeight; j++) {
					// 图片的像素点其实是个矩阵，这里利用两个for循环来对每个像素进行操作
					// 图片变灰的通用算法：取出某个像素的r、g、b值，然后重新计算r、g、b值，计算公式为r=r*0.3+g*0.59+b*0.11，g=r,b=g，最后将该rgb值重新写回像素。

					Object data = originalImage.getRaster().getDataElements(i,
							j, null);// 获取该点像素，并以object类型表示
					red = originalImage.getColorModel().getRed(data);
					blue = originalImage.getColorModel().getBlue(data);
					green = originalImage.getColorModel().getGreen(data);
					red = (red * 3 + green * 6 + blue * 1) / 10;
					green = red;
					blue = green;
					/*
					 * 这里将r、g、b再转化为rgb值，因为bufferedImage没有提供设置单个颜色的方法，只能设置rgb。
					 * rgb最大为8388608，当大于这个值时，应减去255*255*255即16777216
					 */
					rgb = (red * 256 + green) * 256 + blue;
					if (rgb > 8388608) {
						rgb = rgb - 16777216;
					}
					// 将rgb值写回图片
					originalImage.setRGB(i, j, rgb);
				}
			}

			ImageIO.write(originalImage, "jpg", new File("E:/b.jpg"));

		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("读取图片出错");
		}

		// return originalImage;
	}

	/**
	 * 进行猫脸映射
	 * 
	 * @param grayPicPath灰度图像的地址
	 * @param desPicPath置乱之后的图像地址
	 * @param arnoldChangeTimes
	 *            置乱次数
	 * @return
	 * @throws IOException
	 */
	public static double[][] arnoldChange(String grayPicPath,
			String desPicPath, int arnoldChangeTimes, int aValue, int bValue) {

		int a = aValue;
		int b = bValue;

		// 置乱矩阵系数

		int A11 = 1;
		int A12 = b;
		int A21 = a;
		int A22 = a * b + 1;

		Object[] params = changeImageToArray(grayPicPath);
		double[][] beforeArnold = (double[][]) params[0];
		int N = (int) params[1];

		// 置乱次数
		for (int k = 1; k <= arnoldChangeTimes; k++) {

			double[][] afterArnold = new double[N][N];

			for (int i = 0; i < N; i++) {
				for (int j = 0; j < N; j++) {

					int X = (A11 * i + A12 * j) % N;
					int Y = (A21 * i + A22 * j) % N;
					afterArnold[X][Y] = beforeArnold[i][j];
				}
			}
			// 每轮置换
			beforeArnold = afterArnold;
		}

		// 生成猫脸映射之后得到的置乱图片

		changeArrayToImage(beforeArnold, desPicPath, "猫脸映射");

		return beforeArnold;

	}

	/**
	 * 猫脸逆变换
	 * 
	 * @param srcPath
	 *            源地址
	 * @param desPath目标地址
	 * @param aValue
	 *            初始值a
	 * @param bValue
	 *            初始值b
	 * @return
	 * @throws IOException
	 */
	public static String inverseArnoldChange(String srcPath, String desPath,
			int inverseTimes, int aValue, int bValue) throws IOException {

		Object[] params = changeImageToArray(srcPath);
		double[][] beforeInverseArnold = (double[][]) params[0];
		int N = (int) params[1];

		int a = aValue;
		int b = bValue;

		// int A11 = a * b + 1;
		// int A12 = a;
		// int A21 = b;
		// int A22 = 1;

		// int A11 = a * b + 1;
		// int A12 = -b;
		// int A21 = -a;
		// int A22 = 1;

		int A11 = 1;
		int A12 = b;
		int A21 = a;
		int A22 = a * b + 1;

		for (int k = 1; k <= inverseTimes; k++) {
			// 保证每次的数组都是新的，修改 afterInverseArnold 数组的值不会影响
			// beforeInverseArnold数组的值
			double[][] afterInverseArnold = new double[N][N];
			for (int i = 0; i < N; i++) {
				for (int j = 0; j < N; j++) {

					int X = (Math.abs(A11 * i + A12 * j)) % N;
					int Y = (Math.abs(A21 * i + A22 * j)) % N;

					afterInverseArnold[i][j] = beforeInverseArnold[X][Y];

				}
			}
			// 注意这里是引用传递,可能会导致修改 afterInverseArnold数组的值影响 beforeInverseArnold的值
			beforeInverseArnold = afterInverseArnold;
		}

		changeArrayToImage(beforeInverseArnold, desPath, "猫脸逆映射");

		return null;
	}

	/**
	 * 将一个矩阵数组转为图像
	 * 
	 * @param array 数组
	 * @param path 要存放的图片地址
	 * @throws IOException
	 */
	public static void changeArrayToImage(double[][] array, String path,
			String process) {
		int N = array.length;
		System.out.println(process + ":" + N);
		BufferedImage image = new BufferedImage(N, N,
				BufferedImage.TYPE_BYTE_GRAY);

		for (int i = 0; i < N; i++)
			for (int j = 0; j < N; j++) {
				int gray = (int) (array[i][j] * 255);
				
				int rgb = new Color(gray, gray, gray).getRGB();
				image.setRGB(i, j, rgb);
			}

		try {
			ImageIO.write(image, "jpg", new File(path));
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println(process + ":数组转换图片成功,路径:" + path);
	}

	public static Object[] changeImageToArray(String imgPath) {

		int N = 0;
		double[][] array = null;
		int[] rgbArray = null;
		try {
			BufferedImage image = ImageIO.read(new File(imgPath));
			
			N = image.getWidth();
			System.out.println("第一个:" + N + ",path=" + imgPath);
			array = new double[N][N];
			rgbArray = new int[N * N + 2];

			rgbArray = image.getRGB(0, 0, N, N, rgbArray, 0, N);
			for (int i = 0; i < N; i++)
				for (int j = 0; j < N; j++) {

					Color color = new Color(rgbArray[j * N + i]);
					array[i][j] = color.getRed() / 255.0;
				}

		} catch (IOException e) {
			e.printStackTrace();
		}
		Object[] object = new Object[] { array, N };
		return object;
	}

	public static double[][] chaoticEncrypt(double[][] arr, String srcPath,
			String desPath, double x0, double y0, double z0) {

		int N = 0;

		// 加密参数
		// double x0 = 0.5;
		// double y0 = 1.2;
		// double z0 = 0.3;

		double a = 0.0;
		double b = 0.1;
		double c = 0.2; // a,b+ci,b-ci
		double A11 = -a + 2 * b;
		double A12 = a - b - c;
		double A13 = a - b + c;
		double A21 = -a + b + c;
		double A22 = a - c;
		double A23 = a - b;
		double A31 = -a + b - c;
		double A32 = a - b;
		double A33 = a + c;
		double B = 20.0;

		int E = 100000;

		double[][] beforeChaoticEncrypt = null;
		double[][] afterChaoticEncrypt = null;
		double[][] returnArray = null;

		double maxValue = 10;
		double minValue = 10;
		double left0 = 100000;

		Object[] params = changeImageToArray(srcPath);

		// beforeChaoticEncrypt = (double[][]) params[0];
		beforeChaoticEncrypt = arr;
		N = (int) params[1];

		afterChaoticEncrypt = new double[N][N];

		for (int i = 0; i < N; i++)
			for (int j = 0; j < N; j++) {
				afterChaoticEncrypt[i][j] = beforeChaoticEncrypt[i][j] + y0;

				double value = afterChaoticEncrypt[i][j];

				if (value > maxValue)
					maxValue = value;
				if (value < minValue)
					minValue = value;
				if (Math.abs(value) < left0)
					left0 = Math.abs(value);

				double X = A11 * x0 + A12 * value + A13 * z0 + (B * value) % E;
				double Y = A21 * x0 + A22 * y0 + A23 * z0;
				double Z = A31 * x0 + A32 * value + A33 * z0;
				x0 = X;
				y0 = Y;
				z0 = Z;

			}

		System.out.println("max=" + maxValue + ",min=" + minValue + ",lef0="
				+ left0);
		returnArray = afterChaoticEncrypt;

		// changeArrayToImage(afterChaoticEncrypt, desPath,"混沌加密");

		System.out.println("混沌加密成功");

		return returnArray;
	}

	/**
	 * @param array
	 *            混沌加密后的数组，因为其值大于1，所以不能直接生成图片，要先归一化
	 * @param imgPath
	 *            归一化生成的图片路径
	 * @return obj[0]: 图片路径 ;obj[1]:最大值 obj[2]:最小值;obj[3]: 小数数组
	 */
	public static Object[] guiyihua(double[][] array, String imgPath) {

		double maxValue = 10;
		double minValue = 10;

		int N = array.length;

		for (int i = 0; i < N; i++)
			for (int j = 0; j < N; j++) {
				double value = array[i][j];
				if (value > maxValue)
					maxValue = value;
				else if (value < minValue)
					minValue = value;
			}

		double cha = maxValue - minValue;
		System.out.println("guiyi:差=" + cha + ",min=" + minValue + ",max="
				+ maxValue);

		double[][] xiaoshu = new double[N][N];
		System.out.println("一开始:" + array[1][1]);

		for (int i = 0; i < N; i++)
			for (int j = 0; j < N; j++) {
				double value = array[i][j];
				double afterValue = (value - minValue) / cha;
				array[i][j] = afterValue;

				double v = array[i][j] * 255;
				double end = (v - (int) v);
				xiaoshu[i][j] = end;
			}
		System.out.println("结束,");
		System.out.println(array[1][1] + "," + xiaoshu[1][1]);
		changeArrayToImage(array, imgPath, "归一化");

		Object[] objects = new Object[] { imgPath, maxValue, minValue, xiaoshu };

		return objects;

	}

	public static double[][] deGuiyihua(String imgPath, double maxValue,
			double minValue, double[][] xiaoshu) {

		Object[] params = changeImageToArray(imgPath);
		double[][] array = (double[][]) params[0];
		int N = (int) params[1];

		double cha = maxValue - minValue;
		System.out.println("逆归一化之前:" + array[1][1]);
		System.out.println();
		System.out.println("guiyi:差=" + cha);
		for (int i = 0; i < N; i++)
			for (int j = 0; j < N; j++) {
				array[i][j] = ((array[i][j] + xiaoshu[i][j]) * cha) + minValue;
			}
		System.out.println("逆归一化成功," + array[1][1]);
		return array;
	}

	public static void deChaoticEncrypt(double[][] array, String desPath,
			double x0, double y0, double z0) {

		int N = 0;

		double[][] beforeDeencrypt = null;
		double[][] afterDeencrypt = null;

		// double x0 = 0.5;
		// double y0 = 1.2;
		// double z0 = 0.3;
		// a=0.1;
		double a = 0.0;
		double b = 0.1;
		double c = 0.2; // a,b+ci,b-ci;
		double a11 = -a + 2 * b;
		double a12 = a - b - c;
		double a13 = a - b + c;
		double a21 = -a + b + c;
		double a22 = a - c;
		double a23 = a - b;
		double a31 = -a + b - c;
		double a32 = a - b;
		double a33 = a + c;
		b = 20.0;
		int E = 100000;

		N = array.length;

		beforeDeencrypt = array;
		afterDeencrypt = new double[N][N];
		for (int i = 0; i < N; i++)
			for (int j = 0; j < N; j++) {

				double value = beforeDeencrypt[i][j];
				afterDeencrypt[i][j] = value - y0;
				double x = a11 * x0 + a12 * value + a13 * z0 + (b * value) % E;
				double y = a21 * x0 + a22 * y0 + a23 * z0;
				double z = a31 * x0 + a32 * value + a33 * z0;
				x0 = x;
				y0 = y;
				z0 = z;
			}

		changeArrayToImage(afterDeencrypt, desPath, "混沌解密");

	}

}
