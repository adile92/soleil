package wrapper;

import java.io.Serializable;


public class SecretKeyWrap implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 3095841066035677548L;

	private byte[] data;
	private byte[] hashCipherKeySession;
	
	public SecretKeyWrap(byte[] cryptedData, byte[] hashCipherKeySession){
		this.data = cryptedData;
		this.hashCipherKeySession = hashCipherKeySession;
	}

	public byte[] getData() {
		return data;
	}

	public void setData(byte[] data) {
		this.data = data;
	}

	public byte[] getHashCipherKeySession() {
		return hashCipherKeySession;
	}

	public void setHashCipherKeySession(byte[] hashCipherKeySession) {
		this.hashCipherKeySession = hashCipherKeySession;
	}
}
