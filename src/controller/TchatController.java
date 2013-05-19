package controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.net.URL;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.KeySpec;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.FileChooser;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.SecretKeySpec;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.ObjectMessage;
import javax.jms.TextMessage;

import jms.BrokerLauncher;

import org.apache.log4j.Logger;
import org.esiag.isidis.bdf.commons.initializer.springconf.BdfApplicationContext;
import org.esiag.isidis.bdf.commons.jms.AbstractReader;
import org.esiag.isidis.bdf.commons.jms.AbstractWriter;
import org.esiag.isidis.bdf.commons.jms.ConnexionMode;
import org.esiag.isidis.bdf.commons.jms.MessageConfiguration;
import org.esiag.isidis.bdf.commons.jms.MessageFilter;

import edu.esiag.isidis.security.provider.MyProvider;

import Wrapper.SecretKeyWrap;
import appender.GuiAppender;

public class TchatController implements Initializable {
	
	private static Logger logger = Logger.getLogger(TchatController.class);
	private static boolean init = false;
	private Reader reader;
	private Writer writer;
	private MessageConfiguration confReader;
	private MessageConfiguration confWriter;
	private static BdfApplicationContext context = BdfApplicationContext.getInstance();
	private MyProvider provider = new MyProvider();
	private Cipher matrix;
	private Key myPublicKey;
	private Key myPrivateKey;
	
	private Key otherPublicKey;
	
	private SecretKey sessionKey;
	
	@FXML
	Button privateKey;
	@FXML
	Button publicKey;
	@FXML
	Button submit;
	@FXML
	TextField inputMessage;
	@FXML
	TextArea tchat;
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rsrcs) {
    	logger.addAppender(new GuiAppender(MainController.console));
    	logger.info(this.getClass().getSimpleName() + ".initialize");
    	
    	if(!init)
    		initJMS();
        
    }

	private void initJMS() {
		init = true;
		logger.info("initialisation de JMS");
		String boiteAlire;
    	String boiteAecrire;
    	
    	if(BrokerLauncher.getBokerLauncher().isFirst()){
    		boiteAlire = "boite1";
    		boiteAecrire = "boite2";
    	}
    	else
    	{
    		boiteAlire = "boite2";
    		boiteAecrire = "boite1";
    	}
    	
    	confReader = new MessageConfiguration(boiteAlire);
		confReader.setMode(ConnexionMode.QUEUE);
		
		try {
			reader = new Reader(context.getProperty("jmsBrokerConfig.ip"),confReader, null);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		confWriter = new MessageConfiguration(boiteAecrire);
		confWriter.setMode(ConnexionMode.QUEUE);

		writer = new Writer(context.getProperty("jmsBrokerConfig.ip"),confWriter);
	}
    
    public void initEchange(ActionEvent event) throws JMSException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException{
    	
    	logger.info("init !!");
    	
    	
    	
    	Cipher cipher = provider.getCipher("RSA");
    	
    	writer.send(writer.createObjectMessage(myPublicKey));
    	
    	
    	if(BrokerLauncher.getBokerLauncher().isFirst()){
    		logger.info("Je suis le 1er, je crée la clé secrete");
    		cipher.init(Cipher.ENCRYPT_MODE, otherPublicKey);
    		
    		sessionKey = provider.getKeyGenerator("AES").generateKey();
    		
    		cipher.update(sessionKey.getEncoded());
    		
    		SecretKeyWrap skw = new SecretKeyWrap(cipher.doFinal());
    		
    		writer.send(writer.createObjectMessage(skw));
    		
    		
    	}
    	
    }
    
    
    public void getPrivateKey(ActionEvent event) throws IOException, ClassNotFoundException{
    	
    	logger.info("get private key !!");
    	FileChooser fileChooser = new FileChooser();

		File privateKeyFile = fileChooser.showOpenDialog(null);
		
		FileInputStream fis = new FileInputStream(privateKeyFile);
		
		
		ObjectInputStream ois = new ObjectInputStream(fis);
		Object obj = ois.readObject();
		
		if (obj instanceof PrivateKey){
			myPrivateKey = (Key) obj;
			privateKey.setStyle("-fx-background-color: green;");
		}
		else
		{
			logger.error("Erreur de clé");
			privateKey.setStyle("-fx-background-color: red;");
		}
			
		
		fis.close();
		ois.close();
    	
    }
    
    public void getPublicKey(ActionEvent event) throws IOException, ClassNotFoundException{
    	
    	logger.info("get public key !!");
    	FileChooser fileChooser = new FileChooser();

		File publicKeyFile = fileChooser.showOpenDialog(null);
		
		FileInputStream fis = new FileInputStream(publicKeyFile);
		
		
		ObjectInputStream ois = new ObjectInputStream(fis);
		Object obj = ois.readObject();
		
		if (obj instanceof PublicKey){
			myPublicKey = (Key) obj;
			publicKey.setStyle("-fx-background-color: green;");
		}
		else
		{
			logger.error("Erreur de clé");
			publicKey.setStyle("-fx-background-color: red;");
		}
			
		
		fis.close();
		ois.close();
    	
    }
    
    public String decryptOrEncrypt(String data,int mode) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException{
    	
    	if(sessionKey != null){
    		
    		if(matrix == null)
    			matrix = provider.getCipher("AES");
    		
    		matrix.init(mode, sessionKey);
    		
    		return new String(matrix.doFinal(data.getBytes()));
    		
    	}
    		
		return "";
    	
    }
    
	public void submitMessage(ActionEvent event) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException{
	    	
			String clearMessage = inputMessage.getText();
			String cryptedMessage = decryptOrEncrypt(clearMessage, Cipher.ENCRYPT_MODE);
			
	    	
	    	tchat.appendText("Me : "+clearMessage);
	    	tchat.appendText("["+cryptedMessage+"]\n");
	    	logger.info("Envoi : "+inputMessage.getText());
	    	
	    	try {
				writer.send(writer.createTextMessage(inputMessage.getText()));
			} catch (JMSException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    	inputMessage.setText("");
	    }
	
    
    private class Reader extends AbstractReader {

		public Reader(String failover, MessageConfiguration configuration,MessageFilter filter) throws Exception {
			super(failover, configuration, filter);
		}

		@Override
		public void onException(JMSException arg0) {

		}

		@Override
		public void handleMessage(Message message) {
			try {
				if (message instanceof TextMessage) {
					
					String mess = ((TextMessage) message).getText();
					String clearMessage = decryptOrEncrypt(mess, Cipher.DECRYPT_MODE);
					
					tchat.appendText("Other : "+clearMessage+"");
					tchat.appendText("["+mess+"]\n");
				}
				else if(message instanceof ObjectMessage){
					
					Serializable object = ((ObjectMessage) message).getObject();
					
					if(object instanceof PublicKey){
						otherPublicKey = (Key) object;
						logger.info("Clé public du correspondant reçu !");
					}
					else if (object instanceof SecretKeyWrap){
						SecretKeyWrap skw  = (SecretKeyWrap) object;
						
						Cipher cipher = provider.getCipher("RSA");
						cipher.init(Cipher.DECRYPT_MODE, myPrivateKey);
						
						cipher.update(skw.getData());
						
						SecretKeyWrap data = new SecretKeyWrap(cipher.doFinal());
						
						sessionKey = new SecretKeySpec(data.getData(), 0, data.getData().length, "AES");
						
						logger.info("J'ai reçu la clé de session !");
					}
				}

			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}


	}

	private class Writer extends AbstractWriter {

		public Writer(String failover, MessageConfiguration configuration) {
			super(failover, configuration);
			// TODO Auto-generated constructor stub
		}

		@Override
		public void onException(JMSException arg0) {
			// TODO Auto-generated method stub

		}

	}

	

    
}
