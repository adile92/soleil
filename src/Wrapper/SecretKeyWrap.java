package Wrapper;

import java.io.Serializable;


public class SecretKeyWrap implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 3095841066035677548L;
	
	private byte[] data;
	
	public SecretKeyWrap(byte[] cryptedData){
		
		setData(new byte[cryptedData.length]);
		setData(cryptedData);
	}

	public byte[] getData() {
		return data;
	}

	public void setData(byte[] data) {
		this.data = data;
	}
}
