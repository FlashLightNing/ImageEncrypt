package bishe;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.security.Key;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import javax.crypto.Cipher;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

public abstract class EncryptKey {

	private static String PUBLIC_KEY_FILE = "publicKey";
	private static String PRIVATE_KEY_FILE = "privateKey";

	public static void main(String[] args) throws FileNotFoundException,
			IOException {
		RSAgetKey();
		
		String encrypted;
		try {
			encrypted = encrypt("5");
			System.out.println(encrypted);
			String de =decrypt(encrypted);
			System.out.println(de);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}

	public static void RSAgetKey() throws FileNotFoundException, IOException {
		SecureRandom random = new SecureRandom();
		KeyPairGenerator keys = null;
		try {
			keys = KeyPairGenerator.getInstance("RSA");
			keys.initialize(1024, random);
			KeyPair pair = keys.generateKeyPair();
			Key publicKey = pair.getPublic();
			Key privateKey = pair.getPrivate();

			// String private_key=String.valueOf(pair.getPrivate());
			// String public_key =String.valueOf(pair.getPublic());
			System.out.println(publicKey);
			System.out.println(privateKey);

			ObjectOutputStream oos1 = new ObjectOutputStream(
					new FileOutputStream(PUBLIC_KEY_FILE));
			ObjectOutputStream oos2 = new ObjectOutputStream(
					new FileOutputStream(PRIVATE_KEY_FILE));
			oos1.writeObject(publicKey);
			oos2.writeObject(privateKey);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}

	}

	public static String encrypt(String source) throws  Exception {
		ObjectInputStream ois = new ObjectInputStream(new FileInputStream(
				PUBLIC_KEY_FILE));
		Key key = (Key) ois.readObject();
		ois.close();
		
		/** 得到Cipher对象来实现对源数据的RSA加密 */
		Cipher cipher = Cipher.getInstance("RSA");
		cipher.init(Cipher.ENCRYPT_MODE, key);
		byte[] b = source.getBytes();
		/** 执行加密操作 */
		byte[] b1 = cipher.doFinal(b);
		BASE64Encoder encoder = new BASE64Encoder();
		return encoder.encode(b1);
	}

	public static String decrypt(String cryptograph) throws Exception{
		   /** 将文件中的私钥对象读出 */
		   ObjectInputStream ois = new ObjectInputStream(new FileInputStream(PRIVATE_KEY_FILE));
		   Key key = (Key) ois.readObject();
		   /** 得到Cipher对象对已用公钥加密的数据进行RSA解密 */
		   Cipher cipher = Cipher.getInstance("RSA");
		   cipher.init(Cipher.DECRYPT_MODE, key);
		   BASE64Decoder decoder = new BASE64Decoder();
		   byte[] b1 = decoder.decodeBuffer(cryptograph);
		   /** 执行解密操作 */
		   byte[] b = cipher.doFinal(b1);
		   return new String(b);
		}
	
}
