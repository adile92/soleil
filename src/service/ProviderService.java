package service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Signature;
import java.security.cert.X509Certificate;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javafx.concurrent.Task;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

import key.generator.AssymetricKey;
import key.generator.SymetricKey;
import model.Certificate;

import org.apache.log4j.Logger;

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
import chiffrement.Chiffrement;
import edu.esiag.isidis.security.provider.MyProvider;
import fr.cryptohash.Digest;

public class ProviderService {


	private static Logger logger = Logger
			.getLogger(ProviderService.class);
	private static final MyProvider provider = new MyProvider();
	final static int MAXREAD = 1048576 * 100;

	/**
	 * retourne la liste des algo dispo
	 * 
	 * @return
	 */
	public static String[] algoMD() {
		List<String> list = new ArrayList<>();
		list.add("MD5");
		list.add("SHA");
		list.add("SHA-1");
		list.add("SHA-256");
		list.add("SHA-384");
		list.add("SHA-512");
		list.add("Keccak-224");
		list.add("Keccak-256");
		list.add("Keccak-384");
		list.add("Keccak-512");

		return list.toArray(new String[list.size()]);
	}

	public static String[] algoChiffrement() {
		List<String> list = new ArrayList<>();

		for (SymetricKey cle : SymetricKey.values()) {
			list.add(cle.name());
		}

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
				Digest keccak = null;
				boolean keccakOrNot = false;

				StringBuffer buff = new StringBuffer();
				try {
					FileInputStream fis = new FileInputStream(file);

					int tailleContent = fis.available();

					if (algo.startsWith("Keccak")) {
						keccakOrNot = true;
						int version = Integer.valueOf(algo.substring(
								algo.length() - 3, algo.length()));

						keccak = provider.getKeccak(version);
					} else
						md = provider.getMessageDigest(algo);

					int content;

					while ((content = fis.read()) != -1) {
						buff.append((char) content);
						updateProgress(buff.length(), tailleContent);
					}

					String empreinte;

					if (!keccakOrNot)
						empreinte = new String(md.digest(buff.toString()
								.getBytes()));
					else
						empreinte = new String(keccak.digest(buff.toString()
								.getBytes()));

					FileOutputStream fos = new FileOutputStream(new File(
							cheminEmpreinte));

					fos.write(empreinte.getBytes());

				} catch (NoSuchAlgorithmException e) {
					logger.error("", e); 
				} catch (FileNotFoundException e) {
					logger.error("", e); 
				} catch (IOException e) {
					logger.error("", e); 
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

	public static Task<X509Certificate> generateCertificate(
			final Certificate certificate, final KeyPair pair,
			final Integer days, final String algorithm)
			throws GeneralSecurityException, IOException {

		return new Task<X509Certificate>() {

			@Override
			protected X509Certificate call() throws Exception {

				PrivateKey privkey = pair.getPrivate();
				X509CertInfo info = new X509CertInfo();
				Date from = new Date();
				Date to;
				if (days == null) {
					to = new Date(from.getTime() + 30 * 86400000l);
				} else {
					to = new Date(from.getTime() + days * 86400000l);
				}
				CertificateValidity interval = new CertificateValidity(from, to);
				BigInteger sn = new BigInteger(64, new SecureRandom());
				// common, orgUnit, org, locality, state, country
				X500Name owner = new X500Name(certificate.getCN(),
						certificate.getOU(), certificate.getO(),
						certificate.getL(), certificate.getS(),
						certificate.getC());
			
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
						AlgorithmId.RSA_oid);
				info.set(X509CertInfo.ALGORITHM_ID, new CertificateAlgorithmId(
						algo));

				// Sign the cert to identify the algorithm that's used.
				X509CertImpl cert = new X509CertImpl(info);

				cert.sign(privkey, algorithm);

				return cert;

				/*
				 * DH DH_PKIX DSA DSA_OIW EC MD2 Algorithm ID for the MD2
				 * Message Digest Algorthm, from RFC 1319. md2WithRSAEncryption
				 * MD5 Algorithm ID for the MD5 Message Digest Algorthm, from
				 * RFC 1321. md5WithRSAEncryption protected DerValue params
				 * Parameters for this algorithm. pbeWithMD5AndDES Algorithm ID
				 * for the PBE encryption algorithms from PKCS#5 and PKCS#12.
				 * pbeWithMD5AndRC2 pbeWithSHA1AndDES pbeWithSHA1AndDESede
				 * pbeWithSHA1AndRC2_40 pbeWithSHA1AndRC2 RSA RSAEncryption SHA
				 * Algorithm ID for the SHA1 Message Digest Algorithm, from FIPS
				 * 180-1. sha1WithDSA sha1WithDSA_OIW sha1WithECDSA
				 * sha1WithRSAEncryption sha1WithRSAEncryption_OIW
				 * sha224WithECDSA SHA256 sha256WithECDSA
				 * sha256WithRSAEncryption SHA384 sha384WithECDSA
				 * sha384WithRSAEncryption SHA512 sha512WithECDSA
				 * sha512WithRSAEncryption shaWithDSA_OIW specifiedWithECDSA
				 */
			}
		};
	}

	public static void generate() {

	}

	public static Task<SecretKey> getSecretKey(final String algoKeyGenerator,
			final Integer keyLenght)
			throws InvalidKeySpecException, NoSuchAlgorithmException,
			InvalidKeyException {

		return new Task<SecretKey>() {

			@Override
			protected SecretKey call() throws Exception {

				KeyGenerator kg = KeyGenerator.getInstance(algoKeyGenerator);
				if (keyLenght != null) {
					kg.init(keyLenght, new SecureRandom());
				} else {
					kg.init(new SecureRandom());
				}
				return kg.generateKey();
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

	public static Task<Object> getSignature(final byte[] datas, final byte[] dataSign,
			final Key key, final Signature signature, final boolean isSigner)
			throws NoSuchAlgorithmException, NoSuchProviderException {
		// Get the public/private key pair
		return new Task<Object>() {

			@Override
			protected Object call() throws Exception {
				System.out.println(isSigner);
				if(isSigner){
				signature.initSign((PrivateKey)key);
				signature.update(datas);
				byte[] sig = signature.sign();
				updateProgress(100, 100);
				/* Update and sign the data */
				return sig;
				}else{
					signature.initVerify((PublicKey)key);
					signature.update(datas);
					boolean value = signature.verify(dataSign);
					updateProgress(100, 100);
					return value;
				}

			}
		};
	}

	public static Task<?> performChiffrement(final File file, final File destination, final Key key) {
		return new Task<Object>() {
			@Override
			protected Object call() throws Exception {

				try {


					Chiffrement.encrypt(key, file,destination);

					updateProgress(100, 100);

				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				} catch (Throwable e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				return true;
			}
		};
	}
	
	public static Task<?> performDechiffrement(final File source, final File destination, final Key key) {
		return new Task<Object>() {
			@Override
			protected Object call() throws Exception {

				try {

					
					Chiffrement.decrypt(key, source, destination);

					updateProgress(100, 100);

				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				} catch (Throwable e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				return true;
			}
		};
	}
	
	
	
	
	
	

}
