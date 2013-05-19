package chiffrement;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
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
	
	private static MyProvider provider = new MyProvider();
	
	public static void encrypt(Key key,File is, File os) throws Throwable {
		Cipher cipher = provider.getCipher(key.getAlgorithm());
		cipher.init(Cipher.ENCRYPT_MODE, key);
		encryptOrDecrypt(is, os, cipher);
	}

	public static void decrypt(Key key,File is, File os) throws Throwable {
		Cipher cipher = provider.getCipher(key.getAlgorithm());
		cipher.init(Cipher.DECRYPT_MODE, key);
		encryptOrDecrypt(is, os,cipher);
	}

	public static void encryptOrDecrypt(File fileIn, File fileOut,Cipher cipher) throws Throwable {
		
		InputStream in 	= new FileInputStream(fileIn);
		OutputStream out = new FileOutputStream(fileOut);
		
		  int blockSize = (2048 / 8) - 11;
	      int outputSize = cipher.getOutputSize(blockSize);
	      byte[] inBytes = new byte[blockSize];
	      byte[] outBytes = new byte[outputSize];

	      int inLength = 0;
	      boolean more = true;
	      while (more)
	      {
	         inLength = in.read(inBytes);
	         if (inLength == blockSize)
	         {
	            int outLength = cipher.update(inBytes, 0, blockSize, outBytes);
	            out.write(outBytes, 0, outLength);
	         }
	         else more = false;
	      }
	      if (inLength > 0) outBytes = cipher.doFinal(inBytes, 0, inLength);
	      else outBytes = cipher.doFinal();
	      	out.write(outBytes);
	   
		in.close();
		out.close();
	}
	

	
}
