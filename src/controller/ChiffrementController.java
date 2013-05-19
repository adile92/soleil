/*
 * Copyright (c) 2012 Oracle and/or its affiliates.
 * All rights reserved. Use is subject to license terms.
 *
 * This file is available and licensed under the following license:
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *  - Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *  - Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the distribution.
 *  - Neither the name of Oracle nor the names of its
 *    contributors may be used to endorse or promote products derived
 *    from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 * OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.URL;
import java.security.Key;
import java.security.cert.Certificate;
import java.util.ResourceBundle;

import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.stage.FileChooser;

import org.apache.log4j.Logger;

import service.ProviderService;
import appender.GuiAppender;
import edu.esiag.isidis.security.provider.MyProvider;

public class ChiffrementController implements Initializable {
	
	private static Logger logger = Logger.getLogger(ChiffrementController.class);
	
	private File keyFile;
	private File clearFile;
	private boolean mode = true; // true : chiffrement - false : dechiffrement
	
    @FXML
    SplitPane splitPane;
    
    @FXML
    Button fileChooser;
    
    @FXML 
    TextField fichierSortie;
    
    @FXML
    ProgressBar progress;
    
    @FXML
    TextField cleChiffrement;
    
    @FXML
    TextField fileChiffrement;
    
    @FXML
    ToggleButton choixAction;
    
    @FXML
    Label labelStatus;
    
    @FXML
    Label labelFichier;
    
    @FXML
    Label labelMode;
    
    @FXML
    Button chiffrer;
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rsrcs) {
    	logger.addAppender(new GuiAppender(MainController.console));
        
    	logger.info(this.getClass().getSimpleName() + ".initialize");
        
        
        
    }
    

	public void newCipherFileChooser(ActionEvent event) throws IOException {

		FileChooser fileChooser = new FileChooser();
		

		clearFile = fileChooser.showOpenDialog(null);
		
		logger.info("Choix du fichier "+clearFile.getAbsolutePath());
		
		fileChiffrement.setText(clearFile.getAbsolutePath());
		
		
		
		
	}
	
	
	
	public void newCipherKeyFileChooser(ActionEvent event) throws IOException {

		FileChooser fileChooser = new FileChooser();

		keyFile = fileChooser.showOpenDialog(null);
		
		logger.info("Choix de la cl� "+keyFile.getAbsolutePath());
		
		cleChiffrement.setText(keyFile.getAbsolutePath());
		
		
	}
	
	@SuppressWarnings("unchecked")
	public void newCipherCrypt(ActionEvent event){
		
		
		if (clearFile != null) {
			
			
			
			MyProvider provider = new MyProvider();
			Key key = null;
			Certificate cer = null;
			try {
				
				
				
				FileInputStream fis = new FileInputStream(keyFile);
				
				
				ObjectInputStream ois = new ObjectInputStream(fis);
				Object obj = ois.readObject();
				
				if (obj instanceof Key)
					key = (Key) obj;
					
				
				fis.close();
				
//				KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
//		        keyGen.initialize(512);
//		        key = keyGen.generateKeyPair().getPublic();
				
				
				
				
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			if(this.mode){
					
					final String pathFile = clearFile.getParent() + "\\" +"crypted_file";
					File destination = new File(pathFile);
				
					final Task performChiffrement  = ProviderService.performChiffrement(clearFile,destination, key);
					
					progress.progressProperty().bind(performChiffrement.progressProperty());
					
					logger.info("Chiffrement Algo : "+key.getAlgorithm()+" en cours");
					
					new Thread(performChiffrement).start();
					
					
					fichierSortie.setText("En cours ...");
					
					
					
					performChiffrement.setOnSucceeded(new EventHandler<Event>() {
		
						@Override
						public void handle(Event event) {
							// TODO Auto-generated method stub
							
							fichierSortie.setText(pathFile);
							logger.info("Chiffrement fini");
							
						}
					});
			
			}
			else
			{
				logger.debug("dechiffrement mode !");
				final String pathFile = clearFile.getParent() + "\\" +"clear_file";
				
				File destination = new File(pathFile);
				
				final Task performDechiffrement  = ProviderService.performDechiffrement(clearFile,destination, key);
				
				progress.progressProperty().bind(performDechiffrement.progressProperty());
				
				logger.info("D�chiffrement Algo : "+key.getAlgorithm()+" en cours");
				
				new Thread(performDechiffrement).start();
				
				
				fichierSortie.setText("En cours ...");
				
				
				
				performDechiffrement.setOnSucceeded(new EventHandler<Event>() {
	
					@Override
					public void handle(Event event) {
						// TODO Auto-generated method stub
						
						fichierSortie.setText(pathFile);
						logger.info("D�chiffrement fini");
						
					}
				});
			}
			
			
			
		}
		
	}
	
	
	public void newChoixAction(ActionEvent event){
		
		logger.info("Changement de mode");
		
		if(choixAction.isSelected()){
			
			this.mode = false;
			labelFichier.setText("Fichier � d�chiffrer :");
			labelStatus.setText("D�chiffrement :");
			labelMode.setText("Mode actuel : d�chiffrement");
			chiffrer.setText("D�chiffrer");
			
			
		}
		else
		{
			this.mode = true;
			labelFichier.setText("Fichier � chiffrer :");
			labelStatus.setText("Chiffrement :");
			labelMode.setText("Mode actuel : chiffrement");
			chiffrer.setText("Chiffrer");
		}
	}

    
}
