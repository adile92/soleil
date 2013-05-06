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
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.URL;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
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
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;

import javax.crypto.SecretKey;

import key.factory.KeyFactory;
import key.generator.TypeCle;
import main.ProviderService;

public class KeyGeneratorController implements Initializable {

	@FXML
	SplitPane splitPane;

	@FXML
	Button generateKey;

	@FXML
	TextArea empreinteArea;

	@FXML
	Label fichierSortie;

	@FXML
	ChoiceBox<TypeCle> typeCle;

	@FXML
	ChoiceBox<String> algoList;

	@FXML
	ProgressBar progress;

	@FXML
	ChoiceBox<KeyFactory> factoryList;

	@FXML
	TextField paddingSize;

	@FXML
	TextField keySize;

	@FXML
	TextField paddingValue;

	@FXML
	TextField password;

	/**
	 * Initializes the controller class.
	 */
	@Override
	public void initialize(URL url, ResourceBundle rsrcs) {
		// assert list != null :
		// "fx:id=\"list\" was not injected: check your FXML file 'IssueTrackingLite.fxml'.";

		System.out.println(this.getClass().getSimpleName() + ".initialize");

		typeCle.setItems(FXCollections.observableArrayList(TypeCle.values()));
		typeCle.getSelectionModel().select(0);

		algoList.setItems(FXCollections.observableArrayList(ProviderService
				.cleAlgoGenerationSymetrique()));
		// on set le 1er de la list comme valeur par defaut
		algoList.getSelectionModel().select(0);

		factoryList.setItems(FXCollections.observableArrayList(KeyFactory
				.values()));
		// on set le 1er de la list comme valeur par defaut
		factoryList.getSelectionModel().select(0);

		List<Integer> size = new ArrayList<Integer>();
		for (int i = 1; i <= 32; i++) {
			size.add(i);
		}

		typeCle.getSelectionModel().selectedItemProperty()
				.addListener(new ChangeListener<TypeCle>() {
					@Override
					public void changed(
							ObservableValue<? extends TypeCle> selected,
							TypeCle oldFruit, TypeCle newFruit) {
						if (selected.getValue().equals(TypeCle.Symetrique)) {
							algoList.setItems(FXCollections
									.observableArrayList(ProviderService
											.cleAlgoGenerationSymetrique()));
							algoList.getSelectionModel().select(0);
						} else if (selected.getValue().equals(
								TypeCle.Assymetrique)) {
							algoList.setItems(FXCollections
									.observableArrayList(ProviderService
											.cleAlgoGenerationAssymetrique()));
							algoList.getSelectionModel().select(0);
						} else {
							try {
								throw new Exception("Type de clé inexistant.");
							} catch (Exception e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
						// if (newFruit != null && newFruit != oldFruit) {
						// switch() {selected.
						// case "Apple": appleImage.setVisible(false); break;
						// case "Orange": orangeImage.setVisible(false); break;
						// case "Pear": pearImage.setVisible(false); break;
						// }
						// }
						// if (newFruit != null) {
						// switch(newFruit) {
						// case "Apple": appleImage.setVisible(true); break;
						// case "Orange": orangeImage.setVisible(true); break;
						// case "Pear": pearImage.setVisible(true); break;
						// }
						// }
					}
				});
	}

	public void generateKey(ActionEvent event) throws IOException {
		System.out.println("toto");

		String algo = this.algoList.getSelectionModel().getSelectedItem();
		KeyFactory factory = this.factoryList.getSelectionModel()
				.getSelectedItem();
		Integer keySize = (this.keySize.getText() == null || this.keySize
				.getText().isEmpty()) ? null : Integer.parseInt(this.keySize
				.getText());
		String password = this.password.getText();
		String paddingValue = this.paddingValue.getText();
		Integer paddingSize = (this.paddingSize.getText() == null || this.paddingSize
				.getText().isEmpty()) ? null : Integer
				.parseInt(this.paddingSize.getText());

		SecretKey key = ProviderService.getSecretKey(algo, factory, keySize,
				password, paddingValue, paddingSize);
		if (key != null) {
			ObjectOutputStream oos = new ObjectOutputStream(
					new FileOutputStream(new File(key.getAlgorithm()
							+ "_SecretKey.txt")));
			oos.writeObject(key);
			oos.close();
			System.out.println(new String(key.getEncoded()));
//			 SecureRandom keyGenRand = null;
//			 try {
//			 keyGenRand = SecureRandom.getInstance("SHA1PRNG");
//			 } catch (NoSuchAlgorithmException e) {
//			 e.printStackTrace();
//			 }
//			 keyGenRand.setSeed(key.getEncoded());
//
//			System.out.println(new String(key.getEncoded()));
		} else {
			System.out.println("clé null");
		}
	}

	public void newMdFileChooser(ActionEvent event) throws IOException {

		FileChooser fileChooser = new FileChooser();
		File file;

		file = fileChooser.showOpenDialog(null);

		if (file != null) {

			String algo = algoList.getSelectionModel().getSelectedItem();
			final String cheminEmpreinte = file.getParent() + "/"
					+ "empreinte.txt";

			final Task performMessageDigest = ProviderService
					.performMessageDigest(algo, file, cheminEmpreinte);

			progress.progressProperty().bind(
					performMessageDigest.progressProperty());

			new Thread(performMessageDigest).start();
			fichierSortie.setText("En cours ...");

			performMessageDigest.setOnSucceeded(new EventHandler<Event>() {

				@Override
				public void handle(Event event) {

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
