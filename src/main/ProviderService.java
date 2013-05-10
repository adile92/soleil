package main;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.SecureRandom;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.ArrayList;
import java.util.Date;
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
import sun.security.x509.AlgorithmId;
import sun.security.x509.CertificateAlgorithmId;
import sun.security.x509.CertificateIssuerName;
import sun.security.x509.CertificateSerialNumber;
import sun.security.x509.CertificateSubjectName;
import sun.security.x509.CertificateValidity;
import sun.security.x509.CertificateVersion;
import sun.security.x509.CertificateX509Key;
import sun.security.x509.X500Name;
import sun.security.x509.X509CertImpl;
import sun.security.x509.X509CertInfo;
import edu.esiag.isidis.security.provider.MyProvider;

public class ProviderService {

	private static final MyProvider provider = new MyProvider();

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
		list.add("SHA-256");
		list.add("SHA-384");
		list.add("SHA-512");

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

	public static Task<X509Certificate> generateCertificate(final String dn,
			final KeyPair pair, final int days, final String algorithm)
			throws GeneralSecurityException, IOException {

		return new Task<X509Certificate>() {

			@Override
			protected X509Certificate call() throws Exception {

				PrivateKey privkey = pair.getPrivate();
				X509CertInfo info = new X509CertInfo();
				Date from = new Date();
				Date to = new Date(from.getTime() + days * 86400000l);
				CertificateValidity interval = new CertificateValidity(from, to);
				BigInteger sn = new BigInteger(64, new SecureRandom());
				X500Name owner = new X500Name(dn);

				info.set(X509CertInfo.VALIDITY, interval);
				info.set(X509CertInfo.SERIAL_NUMBER,
						new CertificateSerialNumber(sn));
				info.set(X509CertInfo.SUBJECT,
						new CertificateSubjectName(owner));
				info.set(X509CertInfo.ISSUER, new CertificateIssuerName(owner));
				info.set(X509CertInfo.KEY,
						new CertificateX509Key(pair.getPublic()));
				info.set(X509CertInfo.VERSION, new CertificateVersion(
						CertificateVersion.V3));
				AlgorithmId algo = new AlgorithmId(
						AlgorithmId.md5WithRSAEncryption_oid);
				info.set(X509CertInfo.ALGORITHM_ID, new CertificateAlgorithmId(
						algo));

				// Sign the cert to identify the algorithm that's used.
				X509CertImpl cert = new X509CertImpl(info);

				cert.sign(privkey, algorithm);

				// Update the algorith, and resign.
				algo = (AlgorithmId) cert.get(X509CertImpl.SIG_ALG);
				info.set(CertificateAlgorithmId.NAME + "."
						+ CertificateAlgorithmId.ALGORITHM, algo);
				cert = new X509CertImpl(info);
				cert.sign(privkey, algorithm);
				return cert;
				
				
				/*	DH 
					DH_PKIX 
					DSA 
					DSA_OIW 
					EC 
					MD2
				Algorithm ID for the MD2 Message Digest Algorthm, from RFC 1319.
					md2WithRSAEncryption 
					MD5
				Algorithm ID for the MD5 Message Digest Algorthm, from RFC 1321.
					md5WithRSAEncryption 
				protected DerValue	params
				Parameters for this algorithm.
					pbeWithMD5AndDES
				Algorithm ID for the PBE encryption algorithms from PKCS#5 and PKCS#12.
					pbeWithMD5AndRC2 
					pbeWithSHA1AndDES 
					pbeWithSHA1AndDESede 
					pbeWithSHA1AndRC2_40 
					pbeWithSHA1AndRC2 
					RSA 
					RSAEncryption 
					SHA
				Algorithm ID for the SHA1 Message Digest Algorithm, from FIPS 180-1.
					sha1WithDSA 
					sha1WithDSA_OIW 
					sha1WithECDSA 
					sha1WithRSAEncryption 
					sha1WithRSAEncryption_OIW 
					sha224WithECDSA 
					SHA256 
					sha256WithECDSA 
					sha256WithRSAEncryption 
					SHA384 
					sha384WithECDSA 
					sha384WithRSAEncryption 
					SHA512 
					sha512WithECDSA 
					sha512WithRSAEncryption 
					shaWithDSA_OIW 
					specifiedWithECDSA 
				 */
			}
		};
	}

	public static void generate(){
	
	}
	
	public static Task<SecretKey> getSecretKey(final String algoKeyGenerator,
			final KeyFactory factoryAlgo, final Integer keyLenght,
			final String password, final String paddingValue,
			final Integer iterationCount) throws InvalidKeySpecException,
			NoSuchAlgorithmException, InvalidKeyException {

		return new Task<SecretKey>() {

			@Override
			protected SecretKey call() throws Exception {

				KeyGenerator kg = null;
				KeySpec keySpec = null;
				SecretKey key;
				SecretKeyFactory skf = null;

				byte[] padding = null;
				if (paddingValue != null && !paddingValue.isEmpty()) {
					padding = paddingValue.getBytes();
				} else {
					SecureRandom saltRand = new SecureRandom(new byte[] { 1, 2,
							3, 4 });
					byte[] salt = new byte[16];
					saltRand.nextBytes(salt);
					padding = salt;

				}

				switch (factoryAlgo) {
				// autre

				case DES:
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
					keySpec = (DESedeKeySpec) skf.getKeySpec(key,
							DESedeKeySpec.class);

					break;
				case PBEWithMD5AndDES:
				case PBEWithMD5AndTripleDES:
				case PBEWithSHA1AndDESede:
				case PBEWithSHA1AndRC2_40:
				case PBKDF2WithHmacSHA1:

					skf = SecretKeyFactory.getInstance(factoryAlgo.name());
					if (keyLenght != null && keyLenght > 0) {
						keySpec = new PBEKeySpec(password.toCharArray(),
								padding, iterationCount, keyLenght);
					} else {
						keySpec = new PBEKeySpec(password.toCharArray(),
								padding, iterationCount);
					}
					break;
				default:
					// skf = SecretKeyFactory.getInstance("DES");
					// return keySpec = new
					// SecretKeySpec(password.getBytes("UTF-8"),
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
		};
	}

	public static Task<KeyPair> getKeyPair(final String keyAlgorithm,
			final int numBits) throws NoSuchAlgorithmException,
			NoSuchProviderException {
		// Get the public/private key pair
		return new Task<KeyPair>() {

			@Override
			protected KeyPair call() throws Exception {
				KeyPairGenerator keyGen = KeyPairGenerator
						.getInstance(keyAlgorithm);
				SecureRandom random = SecureRandom.getInstance("SHA1PRNG",
						"SUN");
				keyGen.initialize(numBits, random);
				KeyPair keyPair = keyGen.generateKeyPair();
				return keyPair;
			}
		};
	}
}
