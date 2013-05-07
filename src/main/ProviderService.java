package main;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.ArrayList;
import java.util.List;

import javafx.concurrent.Task;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.DESedeKeySpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

import key.factory.KeyFactory;
import key.generator.AssymetricKey;
import key.generator.SymetricKey;
import edu.esiag.isidis.security.provider.MyProvider;

public class ProviderService {

	private static final MyProvider provider = new MyProvider();
	private static final int ITERATION_COUNT = 8192;

	/**
	 * retourne la liste des algo dispo
	 * 
	 * @return
	 */
	public static String[] algo() {
		List<String> list = new ArrayList<>();
		list.add("MD5");
		list.add("SHA");
		list.add("SHA-1");

		return list.toArray(new String[list.size()]);
	}

	public static String[] cleAlgoGenerationSymetrique() {

		List<String> list = new ArrayList<>();
		for (SymetricKey cle : SymetricKey.values()) {
			list.add(cle.name());
		}

		return list.toArray(new String[list.size()]);

	}

	public static String[] cleAlgoGenerationAssymetrique() {

		List<String> list = new ArrayList<>();
		for (AssymetricKey cle : AssymetricKey.values()) {
			list.add(cle.name());
		}

		return list.toArray(new String[list.size()]);

	}

	public static Task<?> performMessageDigest(final String algo,
			final File file, final String cheminEmpreinte) {
		return new Task<Object>() {
			@Override
			protected Object call() throws Exception {
				MessageDigest md = null;
				StringBuffer buff = new StringBuffer();
				try {
					FileInputStream fis = new FileInputStream(file);

					int tailleContent = fis.available();

					md = provider.getMessageDigest(algo);

					int content;

					while ((content = fis.read()) != -1) {
						buff.append((char) content);
						updateProgress(buff.length(), tailleContent);
					}

					String empreinte = new String(md.digest(buff.toString()
							.getBytes()));
					FileOutputStream fos = new FileOutputStream(new File(
							cheminEmpreinte));
					fos.write(empreinte.getBytes());

				} catch (NoSuchAlgorithmException e) {
					e.printStackTrace();
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}

				return true;
			}
		};
	}

	public static Task<?> performKeyGenerator(final String algo,
			final File file, final String cheminEmpreinte) {
		return new Task<Object>() {
			@Override
			protected Object call() throws Exception {
				KeyGenerator kg = null;
				StringBuffer buff = new StringBuffer();

				FileInputStream fis = new FileInputStream(file);

				int tailleContent = fis.available();

				kg = provider.getKeyGenerator(algo);

				int content;

				while ((content = fis.read()) != -1) {
					buff.append((char) content);
					updateProgress(buff.length(), tailleContent);
				}
				// kg.
				// String empreinte = new String(md.digest(buff.toString()
				// .getBytes()));
				// FileOutputStream fos = new FileOutputStream(new File(
				// cheminEmpreinte));
				// fos.write(empreinte.getBytes());

				return true;
			}
		};
	}

	public static SecretKey getSecretKey(String algoKeyGenerator,
			KeyFactory factoryAlgo, Integer keyLenght, String password,
			String paddingValue, Integer iterationCount)
			throws InvalidKeySpecException, NoSuchAlgorithmException,
			InvalidKeyException {

		KeyGenerator kg = null;
		KeySpec keySpec = null;
		SecretKey key;
		SecretKeyFactory skf = null;

		byte[] padding = null;
		if (paddingValue != null && !paddingValue.isEmpty()) {
			padding = paddingValue.getBytes();
		} else {
			SecureRandom saltRand = new SecureRandom(new byte[] { 1, 2, 3, 4 });
			byte[] salt = new byte[16];
			saltRand.nextBytes(salt);
			padding = salt;

		}
		if (iterationCount == null) {
			iterationCount = ITERATION_COUNT;
		}

		switch (factoryAlgo) {
		// autre

		case DES:
		case TripleDES:
			kg = KeyGenerator.getInstance(factoryAlgo.name());
			kg.init(new SecureRandom());

			key = kg.generateKey();
			skf = SecretKeyFactory.getInstance(factoryAlgo.name());
			keySpec = new DESKeySpec(key.getEncoded());
			break;
		case DESede:
			System.out.println(keyLenght);
			kg = KeyGenerator.getInstance(factoryAlgo.name());
			kg.init(keyLenght, new SecureRandom());

			key = kg.generateKey();
			skf = SecretKeyFactory.getInstance(factoryAlgo.name());
			keySpec = (DESedeKeySpec) skf.getKeySpec(key, DESedeKeySpec.class);

			break;
		case PBEWithMD5AndDES:
		case PBEWithMD5AndTripleDES:
		case PBEWithSHA1AndDESede:
		case PBEWithSHA1AndRC2_40:
		case PBKDF2WithHmacSHA1:

			skf = SecretKeyFactory.getInstance(factoryAlgo.name());
			if (keyLenght != null && keyLenght > 0) {
				keySpec = new PBEKeySpec(password.toCharArray(), padding,
						iterationCount, keyLenght);
			} else {
				keySpec = new PBEKeySpec(password.toCharArray(), padding,
						iterationCount);
			}
			break;
		default:
			// skf = SecretKeyFactory.getInstance("DES");
			// return keySpec = new SecretKeySpec(password.getBytes("UTF-8"),
			// factoryAlgo.name());
			break;
		}
		if (skf == null) {
			return null;
		}
		SecretKey keyTemp = skf.generateSecret(keySpec);
		SecretKey secret = new SecretKeySpec(keyTemp.getEncoded(),
				algoKeyGenerator);
		return secret;
	}

	public static KeyPair getKeyPair(String keyAlgorithm, int numBits)
			throws NoSuchAlgorithmException, NoSuchProviderException {
		// Get the public/private key pair

		KeyPairGenerator keyGen = KeyPairGenerator.getInstance(keyAlgorithm);
		SecureRandom random = SecureRandom.getInstance("SHA1PRNG", "SUN");
		keyGen.initialize(numBits, random);
		KeyPair keyPair = keyGen.generateKeyPair();
		return keyPair;
	}
}
