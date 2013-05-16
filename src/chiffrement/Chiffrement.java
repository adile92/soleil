package chiffrement;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.Key;
import java.security.cert.Certificate;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.spec.SecretKeySpec;

import main.ProviderService;

import edu.esiag.isidis.security.provider.MyProvider;

public class Chiffrement {
	
	public static void encrypt(Key key, InputStream is, OutputStream os,String algo) throws Throwable {
		encryptOrDecrypt(key, Cipher.ENCRYPT_MODE, is, os,algo);
	}

	public static void decrypt(Key key, InputStream is, OutputStream os,String algo) throws Throwable {
		encryptOrDecrypt(key, Cipher.DECRYPT_MODE, is, os, algo);
	}

	public static void encryptOrDecrypt(Key key, int mode, InputStream is, OutputStream os,String algo) throws Throwable {

		
		MyProvider provider = new MyProvider();
		
		Cipher cipher = provider.getCipher(algo); // DES/ECB/PKCS5Padding for SunJCE
		
		
		if (mode == Cipher.ENCRYPT_MODE) {
			cipher.init(Cipher.ENCRYPT_MODE, key);
			CipherInputStream cis = new CipherInputStream(is, cipher);
			doCopy(cis, os);
		} else if (mode == Cipher.DECRYPT_MODE) {
			cipher.init(Cipher.DECRYPT_MODE, key);
			CipherOutputStream cos = new CipherOutputStream(os, cipher);
			doCopy(is, cos);
		}
	}
	
	public static void encrypt(Certificate cer, InputStream is, OutputStream os,String algo) throws Throwable {
		encryptOrDecrypt(cer, Cipher.ENCRYPT_MODE, is, os,algo);
	}

	public static void decrypt(Certificate cer, InputStream is, OutputStream os,String algo) throws Throwable {
		encryptOrDecrypt(cer, Cipher.DECRYPT_MODE, is, os, algo);
	}

	public static void encryptOrDecrypt(Certificate cer, int mode, InputStream is, OutputStream os,String algo) throws Throwable {

		
		MyProvider provider = new MyProvider();
		
		Cipher cipher = provider.getCipher(algo); // DES/ECB/PKCS5Padding for SunJCE
		
		
		if (mode == Cipher.ENCRYPT_MODE) {
			cipher.init(Cipher.ENCRYPT_MODE, cer);
			CipherInputStream cis = new CipherInputStream(is, cipher);
			doCopy(cis, os);
		} else if (mode == Cipher.DECRYPT_MODE) {
			cipher.init(Cipher.DECRYPT_MODE, cer);
			CipherOutputStream cos = new CipherOutputStream(os, cipher);
			doCopy(is, cos);
		}
	}

	public static void doCopy(InputStream is, OutputStream os) throws IOException {
		byte[] bytes = new byte[64];
		int numBytes;
		while ((numBytes = is.read(bytes)) != -1) {
			os.write(bytes, 0, numBytes);
		}
		os.flush();
		os.close();
		is.close();
	}
}
