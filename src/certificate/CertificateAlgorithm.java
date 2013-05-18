package certificate;

public enum CertificateAlgorithm {

	DSA, DSA_OIW, EC, MD5,
	// Algorithm ID for the MD5 Message Digest Algorthm, from RFC 1321.
	MD5WithRSAEncryption,
	// protected DerValue params
	// Parameters for this algorithm.
	PBEWithMD5AndDES,
	// Algorithm ID for the PBE encryption algorithms from PKCS#5 and PKCS#12.
	PBEWithMD5AndRC2, PBEWithSHA1AndDES, PBEWithSHA1AndDESede, PBEWithSHA1AndRC2_40, PBEWithSHA1AndRC2, RSA, SHA,
	// Algorithm ID for the SHA1 Message Digest Algorithm, from FIPS 180-1.
	SHA1WithDSA, SHA1WithECDSA, SHA1WithRSAEncryption, SHA224WithECDSA, SHA256, SHA256WithECDSA, SHA256WithRSAEncryption, SHA384, SHA384WithECDSA, SHA384WithRSAEncryption, SHA512, SHA512WithECDSA, SHA512WithRSAEncryption,
}
