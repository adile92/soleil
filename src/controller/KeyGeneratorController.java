package controller;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.URL;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.X509Certificate;
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
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import javax.crypto.SecretKey;

import key.factory.KeyFactory;
import key.generator.TypeCle;
import main.App;
import main.ProviderService;
import model.Certificate;

import org.apache.log4j.Logger;

import appender.GuiAppender;
import certificate.CertificateAlgorithm;

public class KeyGeneratorController implements Initializable {

	private static Logger logger = Logger
			.getLogger(KeyGeneratorController.class);

	private static final int ITERATION_COUNT = 8192;

	private static final int NB_DAY_MAX = 99;
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
	ChoiceBox<Integer> certificateLonger;

	@FXML
	ChoiceBox<CertificateAlgorithm> certificateAlgo;

	@FXML
	CheckBox checkCertificate;

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
	Label lbCertifAlgo;

	@FXML
	Label lbDuree;

	@FXML
	Label nbBitsLb;

	@FXML
	Label keySizeLb;

	boolean okClicked;

	Certificate certificate;

	/**
	 * Initializes the controller class.
	 */
	@Override
	public void initialize(URL url, ResourceBundle rsrcs) {
		logger.addAppender(new GuiAppender(MainController.console));

		logger.info(this.getClass().getSimpleName() + ".initialize");

		typeCle.setItems(FXCollections.observableArrayList(TypeCle.values()));
		typeCle.getSelectionModel().select(0);

		List<Integer> days = new ArrayList<Integer>();
		for (int i = 0; i < NB_DAY_MAX; i++) {
			days.add(i);
		}
		certificateLonger.setItems(FXCollections.observableArrayList(days));
		certificateLonger.getSelectionModel().select(0);

		certificateAlgo.setItems(FXCollections
				.observableArrayList(CertificateAlgorithm.values()));
		certificateAlgo.getSelectionModel().select(0);

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
		certificateLonger.setVisible(false);
		certificateAlgo.setVisible(false);
		checkCertificate.setVisible(false);
		lbCertifAlgo.setVisible(false);
		lbDuree.setVisible(false);
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
							certificateLonger.setLayoutY(190);
							certificateAlgo.setLayoutY(190);
							checkCertificate.setLayoutY(190);
							lbCertifAlgo.setLayoutY(190);
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
							certificateLonger.setVisible(false);
							certificateAlgo.setVisible(false);
							lbCertifAlgo.setVisible(false);
							lbDuree.setVisible(false);
							lbDuree.setLayoutY(190);
							checkCertificate.setVisible(false);
							nbBitsLb.setVisible(false);
							nbBits.setVisible(false);
						} else if (selected.getValue().equals(
								TypeCle.Assymetrique)) {
							algoList.setItems(FXCollections
									.observableArrayList(ProviderService
											.cleAlgoGenerationAssymetrique()));
							algoList.getSelectionModel().select(0);
							lbCertifAlgo.setVisible(true);
							lbCertifAlgo.setLayoutY(85);
							lbDuree.setVisible(true);
							lbDuree.setLayoutY(85);
							certificateLonger.setVisible(true);
							certificateLonger.setLayoutY(85);
							certificateAlgo.setVisible(true);
							certificateAlgo.setLayoutY(85);
							checkCertificate.setVisible(true);
							checkCertificate.setLayoutY(85);
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

		checkCertificate.selectedProperty().addListener(
				new ChangeListener<Boolean>() {

					public void changed(ObservableValue<? extends Boolean> ov,
							Boolean old_val, Boolean new_val) {

						if (new_val) {
							certificate = new Certificate();
							okClicked = showPersonEditDialog(certificate);

						}
						if (!okClicked) {
							checkCertificate.setSelected(false);
						} else {
							checkCertificate.setSelected(true);
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
				File file = new File(algo + "_publicKey.pbk");
				FileOutputStream keyfos = new FileOutputStream(file);
				String pathPublicKey = file.getCanonicalPath();
				keyfos.write(pkey);
				keyfos.close();

				pkey = privKey.getEncoded();
				file = new File(algo + "_privateKey.pvk");
				keyfos = new FileOutputStream(file);
				String pathPrivateKey = file.getCanonicalPath();
				keyfos.write(pkey);
				keyfos.close();
				Task taskCertificate = null;
				String pathCertif = null;
				byte[] certif = null;
				if (okClicked && checkCertificate.selectedProperty().getValue()) {
					taskCertificate = ProviderService.generateCertificate(
							certificate, keyPair, certificateLonger
									.getSelectionModel().getSelectedItem(),
							certificateAlgo.getSelectionModel()
									.getSelectedItem().name());

					new Thread(taskCertificate).start();

					taskCertificate.setOnSucceeded(new EventHandler<Event>() {

						@Override
						public void handle(Event arg0) {
							logger.info("Execution complete.");

						}

					});

					certif = ((X509Certificate) taskCertificate.get())
							.getEncoded();
					File certifFile = new File(algo + "_certificate.cer");
					FileOutputStream certifFos = new FileOutputStream(file);
					pathCertif = certifFile.getCanonicalPath();
					certifFos.write(certif);
					certifFos.close();
				}

				String value = "Private key : "
						+ new String(privKey.getEncoded()) + "\n\n"
						+ "Public key : " + new String(pubKey.getEncoded());

				if (certif != null) {
					value += "\n\nCertificate : " + new String(certif);
				}
				this.empreinteArea.setText(value);

				String pathValue = "Private key : " + pathPrivateKey + "\n\n"
						+ "Public key : " + pathPublicKey;

				if (pathCertif != null) {
					pathValue += "\n\nCertificate : " + pathCertif;
				}

				this.fichierSortie.setText(pathValue);
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
				File file = new File(key.getAlgorithm() + "_SecretKey.sck");
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

	public boolean showPersonEditDialog(Certificate certificate) {
		try {
			// Load the fxml file and create a new stage for the popup
			FXMLLoader loader = new FXMLLoader(
					App.class.getResource("CertificateEditDialog.fxml"));
			AnchorPane page = (AnchorPane) loader.load();
			Stage dialogStage = new Stage();
			dialogStage.setTitle("Création d'un certificat");
			dialogStage.initModality(Modality.WINDOW_MODAL);
			// dialogStage.initOwner(primaryStage);
			Scene scene = new Scene(page);
			dialogStage.setScene(scene);

			// Set the person into the controller
			CertificateEditDialogController controller = loader.getController();
			controller.setDialogStage(dialogStage);
			controller.setCertificate(certificate);

			// Show the dialog and wait until the user closes it
			dialogStage.showAndWait();

			return controller.isOkClicked();

		} catch (IOException e) {
			// Exception gets thrown if the fxml file could not be loaded
			e.printStackTrace();
			return false;
		}
	}
}
