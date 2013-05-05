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
import java.net.URL;
import java.util.ResourceBundle;

import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TextArea;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import main.ProviderService;

public class MessageDigestController implements Initializable {
	
    @FXML
    SplitPane splitPane;
    
    @FXML
    Button fileChooser;
    
    @FXML
    TextArea empreinteArea;
    
    @FXML 
    Label fichierSortie;
    
    @FXML
    ChoiceBox<String> algoList;
    
    @FXML
    ProgressBar progress;
    
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rsrcs) {
//        assert list != null : "fx:id=\"list\" was not injected: check your FXML file 'IssueTrackingLite.fxml'.";
        
        System.out.println(this.getClass().getSimpleName() + ".initialize");
        
        
        
        algoList.setItems(FXCollections.observableArrayList(ProviderService.algo()));
        // on set le 1er de la list comme valeur par defaut
        algoList.getSelectionModel().select(0);
        
    }
    

	@SuppressWarnings("unchecked")
	public void newMdFileChooser(ActionEvent event) throws IOException {

		FileChooser fileChooser = new FileChooser();
		File file;
		

		file = fileChooser.showOpenDialog(null);
		
		if (file != null) {
			
			new ProviderService();
						
			String algo = algoList.getSelectionModel().getSelectedItem();
			final String cheminEmpreinte = file.getParent() + "/" + "empreinte.txt";
			
			final Task performMessageDigest  = ProviderService.performMessageDigest(algo, file,cheminEmpreinte);
			
			progress.progressProperty().bind(performMessageDigest.progressProperty());
			
			new Thread(performMessageDigest).start();
			fichierSortie.setText("En cours ...");
			
			performMessageDigest.setOnSucceeded(new EventHandler<Event>() {

				@Override
				public void handle(Event event) {
					// TODO Auto-generated method stub
					
					FileInputStream empreinteStream;
					try {
						empreinteStream = new FileInputStream(cheminEmpreinte);
						byte[] b = new byte[empreinteStream.available()];
						empreinteStream.read(b, 0, empreinteStream.available());
						
						String empreinte = new String(b);
						
						fichierSortie.setText(cheminEmpreinte);
						empreinteArea.setText(empreinte);
						
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
				}
			});
			
			
			
		}
	}
	

    
}
