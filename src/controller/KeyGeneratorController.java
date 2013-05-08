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
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutionException;

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

import org.apache.log4j.Logger;

import appender.GuiAppender;

import key.factory.KeyFactory;
import key.generator.TypeCle;
import main.ProviderService;

public class KeyGeneratorController implements Initializable {
	
	private static Logger logger = Logger.getLogger(KeyGeneratorController.class);
	
	private static final int ITERATION_COUNT = 8192;
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

	@FXML
	TextField nbBits;

	@FXML
	Label factory;

	@FXML
	Label passwordLb;

	@FXML
	Label padding;

	@FXML
	Label paddingSizeLb;

	@FXML
	Label nbBitsLb;

	@FXML
	Label keySizeLb;

	/**
	 * Initializes the controller class.
	 */
	@Override
	public void initialize(URL url, ResourceBundle rsrcs) {
		logger.addAppender(new GuiAppender(MainController.console));

		logger.info(this.getClass().getSimpleName() + ".initialize");

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

		nbBitsLb.setVisible(false);
		nbBits.setVisible(false);
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

							factoryList.setVisible(true);
							password.setVisible(true);
							paddingValue.setVisible(true);
							factory.setVisible(true);
							passwordLb.setVisible(true);
							padding.setVisible(true);
							paddingSizeLb.setVisible(true);
							paddingSize.setVisible(true);
							keySize.setVisible(true);
							keySizeLb.setVisible(true);

							nbBitsLb.setVisible(false);
							nbBits.setVisible(false);
						} else if (selected.getValue().equals(
								TypeCle.Assymetrique)) {
							algoList.setItems(FXCollections
									.observableArrayList(ProviderService
											.cleAlgoGenerationAssymetrique()));
							algoList.getSelectionModel().select(0);

							factoryList.setVisible(false);
							password.setVisible(false);
							paddingValue.setVisible(false);
							factory.setVisible(false);
							passwordLb.setVisible(false);
							padding.setVisible(false);
							paddingSizeLb.setVisible(false);
							paddingSize.setVisible(false);
							keySize.setVisible(false);
							keySizeLb.setVisible(false);
							nbBitsLb.setVisible(true);
							nbBits.setVisible(true);

						} else {
							try {
								throw new Exception("Type de clé inexistant.");
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					}
				});
	}

	public Object generateKey(ActionEvent event) throws IOException {

		this.empreinteArea.setText(null);
		this.fichierSortie.setText(null);
		switch (typeCle.getSelectionModel().getSelectedItem()) {
		case Symetrique:
			executeSecretKey();
			break;
		case Assymetrique:
			executeKeyPair();
			break;
		default:
			break;
		}

		return true;

	}

	private void executeKeyPair() {
		try {
			String algo = this.algoList.getSelectionModel().getSelectedItem();

			Integer nbBits = (this.nbBits.getText() == null || this.nbBits
					.getText().isEmpty()) ? null : Integer.parseInt(this.nbBits
					.getText());
			logger.info("En cours ...");
			Task task = ProviderService.getKeyPair(algo, nbBits);
		
			new Thread(task).start();

			task.setOnSucceeded(new EventHandler<Event>() {

				@Override
				public void handle(Event arg0) {
						logger.info("Execution complete.");

				}

			});
			KeyPair keyPair = (KeyPair) task.get();
			if (keyPair != null) {

				PrivateKey privKey = keyPair.getPrivate();
				PublicKey pubKey = keyPair.getPublic();

				// Store the keys
				byte[] pkey = pubKey.getEncoded();
				File file = new File(algo + "_publicKey.txt");
				FileOutputStream keyfos = new FileOutputStream(file);
				String pathPublicKey = file.getCanonicalPath();
				keyfos.write(pkey);
				keyfos.close();

				pkey = privKey.getEncoded();
				file = new File(algo + "_privateKey.txt");
				keyfos = new FileOutputStream(file);
				String pathPrivateKey = file.getCanonicalPath();
				keyfos.write(pkey);
				keyfos.close();

				this.empreinteArea.setText("Private key : "
						+ new String(privKey.getEncoded()) + "\n\n"
						+ "Public key : " + new String(pubKey.getEncoded()));
				this.fichierSortie.setText("Private key : " + pathPrivateKey
						+ "\n\n" + "Public key : " + pathPublicKey);
			} else {
				logger.info("Error to generate key pair : KeyPair instance is null");

			}
		} catch (Exception e) {
			logger.info(e.getMessage());
		}

	}

	private void executeSecretKey() throws IOException {
		try {
			String algo = this.algoList.getSelectionModel().getSelectedItem();
			KeyFactory factory = this.factoryList.getSelectionModel()
					.getSelectedItem();
			Integer keySize = (this.keySize.getText() == null || this.keySize
					.getText().isEmpty()) ? null : Integer
					.parseInt(this.keySize.getText());
			String password = this.password.getText();
			String paddingValue = this.paddingValue.getText();
			Integer paddingSize = (this.paddingSize.getText() == null || this.paddingSize
					.getText().isEmpty()) ? ITERATION_COUNT : Integer
					.parseInt(this.paddingSize.getText());

			logger.info("En cours ...");
			Task task = ProviderService.getSecretKey(algo, factory, keySize,
					password, paddingValue, paddingSize);
			new Thread(task).start();
			task.setOnSucceeded(new EventHandler<Event>() {

				@Override
				public void handle(Event arg0) {
						logger.info("Execution complete.");

				}

			});
			SecretKey key = (SecretKey) task.get();
			if (key != null) {
				File file = new File(key.getAlgorithm() + "_SecretKey.txt");
				ObjectOutputStream oos = new ObjectOutputStream(
						new FileOutputStream(file));
				oos.writeObject(key);
				oos.close();
				this.fichierSortie.setText(file.getCanonicalPath());

				this.empreinteArea.setText("Secret key : "
						+ new String(key.getEncoded()));

			} else {
				logger.info("Error to generate secret key : SecretKey instance is null");
			}
		} catch (Exception e) {

			logger.info(e.getMessage());
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
