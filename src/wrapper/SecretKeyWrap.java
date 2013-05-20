package wrapper;

import java.io.Serializable;


public class SecretKeyWrap implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 3095841066035677548L;

	private byte[] data;
	private byte[] signatureHashCipherKeySession;
	
	public SecretKeyWrap(byte[] cryptedData, byte[] signatureHashCipherKeySession){
		this.data = cryptedData;
		this.signatureHashCipherKeySession = signatureHashCipherKeySession;
	}

	public byte[] getData() {
		return data;
	}

	public void setData(byte[] data) {
		this.data = data;
	}

	public byte[] getSignatureHashCipherKeySession() {
		return signatureHashCipherKeySession;
	}

	public void setSignatureHashCipherKeySession(byte[] hashCipherKeySession) {
		this.signatureHashCipherKeySession = hashCipherKeySession;
	}
}
