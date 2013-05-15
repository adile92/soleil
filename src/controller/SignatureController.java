package controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.URL;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.Signature;
import java.util.ResourceBundle;

import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TextArea;
import javafx.stage.FileChooser;
import main.ProviderService;

import org.apache.log4j.Logger;

import appender.GuiAppender;

public class SignatureController implements Initializable {
	
	private static Logger logger = Logger.getLogger(SignatureController.class);


    @FXML
    SplitPane splitPane;

    @FXML
    Button fileChooserKey;
    @FXML
    Button fileChooserDatas;
    
    @FXML
    TextArea empreinteArea;
    
    @FXML 
    Label fichierSortie;

    @FXML 
    Label keyPath;
    
    @FXML 
    Label datasPath;
    
    @FXML
    ChoiceBox<String> algoList;
    
    @FXML
    ProgressBar progress;
    
    File chooserKeyFile;
    File chooserDatasFile;
    
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rsrcs) {
    	logger.addAppender(new GuiAppender(MainController.console));
        
    	logger.info(this.getClass().getSimpleName() + ".initialize");
        
        
        algoList.setItems(FXCollections.observableArrayList(ProviderService.algoMD()));
        // on set le 1er de la list comme valeur par defaut
        algoList.getSelectionModel().select(0);
        
    }
    

	public void newMdFileChooser(ActionEvent event) throws IOException, NoSuchAlgorithmException {

		FileChooser fileChooser = new FileChooser();
		File file;
		

		file = fileChooser.showOpenDialog(null);
		
		if (file != null) {
			Node node=(Node) event.getSource();
			
			if(node.getId().equals("fileChooserKey")){
				chooserKeyFile = file;
				keyPath.setText(chooserKeyFile.getPath());
			}else if (node.getId().equals("fileChooserDatas")){
				chooserDatasFile = file;
				datasPath.setText(chooserKeyFile.getPath());
			}
			
//			final String cheminEmpreinte = file.getParent() + "/" + "empreinte.txt";
//			
//			final Task performMessageDigest  = ProviderService.getSignature(datas, privateKey, signature);
//			
//			progress.progressProperty().bind(performMessageDigest.progressProperty());
//			
//			new Thread(performMessageDigest).start();
//			fichierSortie.setText("En cours ...");
//			
//			performMessageDigest.setOnSucceeded(new EventHandler<Event>() {
//
//				@Override
//				public void handle(Event event) {
//					// TODO Auto-generated method stub
//					
//					FileInputStream empreinteStream;
//					try {
//						empreinteStream = new FileInputStream(cheminEmpreinte);
//						byte[] b = new byte[empreinteStream.available()];
//						empreinteStream.read(b, 0, empreinteStream.available());
//						
//						String empreinte = new String(b);
//						
//						fichierSortie.setText(cheminEmpreinte);
//						empreinteArea.setText(empreinte);
//						
//					} catch (IOException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					}
//					
//				}
//			});
//			
			
			
		}
	}
	

    public void btnValidate() throws NoSuchAlgorithmException, IOException, ClassNotFoundException, NoSuchProviderException{
    	String algo = algoList.getSelectionModel().getSelectedItem();
		Signature signature  = Signature.getInstance(algo);

		byte[] datas = (byte[]) getContent(chooserDatasFile);
		PrivateKey pvKey = (PrivateKey) getContent(chooserKeyFile);
		
		
		
		final Task performSignature  = ProviderService.getSignature(datas, pvKey, signature);
		
		progress.progressProperty().bind(performSignature.progressProperty());
		
		new Thread(performSignature).start();
		fichierSortie.setText("En cours ...");
		
//		performSignature.setOnSucceeded(new EventHandler<Event>() {
//
//			@Override
//			public void handle(Event event) {
//				// TODO Auto-generated method stub
//				
//				FileInputStream empreinteStream;
//				try {
//					empreinteStream = new FileInputStream(cheminEmpreinte);
//					byte[] b = new byte[empreinteStream.available()];
//					empreinteStream.read(b, 0, empreinteStream.available());
//					
//					String empreinte = new String(b);
//					
//					fichierSortie.setText(cheminEmpreinte);
//					empreinteArea.setText(empreinte);
//					
//				} catch (IOException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//				
//			}
//		});
//		
    }


	private Object getContent(File file) throws FileNotFoundException, IOException, ClassNotFoundException {
		FileInputStream stream = new FileInputStream(file);
		ObjectInputStream ois = new ObjectInputStream(stream);
		return ois.readObject();
	}
}
