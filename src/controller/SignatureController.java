package controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.URL;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Signature;
import java.util.ResourceBundle;

import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.stage.FileChooser;

import org.apache.log4j.Logger;

import service.ProviderService;
import appender.GuiAppender;
import certificate.CertificateAlgorithm;

public class SignatureController implements Initializable {

	private static Logger logger = Logger.getLogger(SignatureController.class);
	final ToggleGroup group = new ToggleGroup();

	@FXML
	ToggleButton choixActionSigner;

	@FXML
	ToggleButton choixActionVerifier;

	@FXML
	SplitPane splitPane;

	@FXML
	Button fileChooserKey;

	@FXML
	Button fileChooserDatas;

	@FXML
	Button fileChooserDataSign;

	@FXML
	TextArea empreinteArea;

	@FXML
	Label fichierSortie;

	@FXML
	Label keyPath;

	@FXML
	Label datasPath;

	@FXML
	Label signPath;

	@FXML
	ChoiceBox<CertificateAlgorithm> algoList;

	@FXML
	ProgressBar progress;

	File chooserKeyFile = null;
	File chooserDatasFile = null;
	File chooserDataSignFile = null;

	private boolean isSigner;

	/**
	 * Initializes the controller class.
	 */
	@Override
	public void initialize(URL url, ResourceBundle rsrcs) {
		logger.addAppender(new GuiAppender(MainController.console));

		logger.info(this.getClass().getSimpleName() + ".initialize");

		algoList.setItems(FXCollections
				.observableArrayList(CertificateAlgorithm.values()));
		// on set le 1er de la list comme valeur par defaut
		algoList.getSelectionModel().select(0);

		choixActionVerifier.setToggleGroup(group);
		choixActionSigner.setToggleGroup(group);

		fileChooserDataSign.setVisible(false);
		signPath.setVisible(false);
	}

	public void newMdFileChooser(ActionEvent event) throws IOException,
			NoSuchAlgorithmException {

		FileChooser fileChooser = new FileChooser();
		File file;

		file = fileChooser.showOpenDialog(null);

		if (file != null) {
			Node node = (Node) event.getSource();

			if (node.getId().equals("fileChooserKey")) {
				chooserKeyFile = file;
				keyPath.setText(chooserKeyFile.getPath());
			} else if (node.getId().equals("fileChooserDatas")) {
				chooserDatasFile = file;
				datasPath.setText(chooserDatasFile.getPath());
			} else if (node.getId().equals("fileChooserDataSign")) {
				chooserDataSignFile = file;
				signPath.setText(chooserDataSignFile.getPath());
			}
		}
	}

	public void btnValidate() throws NoSuchAlgorithmException, IOException,
			ClassNotFoundException, NoSuchProviderException {
		CertificateAlgorithm algo = algoList.getSelectionModel()
				.getSelectedItem();
		Signature signature = Signature.getInstance(algo.name());

		if (chooserDatasFile == null || chooserKeyFile == null) {
			logger.error("Datas file or key file is empty!");
			return;
		}

		byte[] datas = (byte[]) getContent(chooserDatasFile);
		Key key = (Key) getObjectContent(chooserKeyFile);
		byte[] dataSign = (byte[]) getContent(chooserDataSignFile);

		final Task<?> performSignature = ProviderService.getSignature(datas,
				dataSign, key, signature, isSigner);

		progress.progressProperty().bind(performSignature.progressProperty());

		new Thread(performSignature).start();
		fichierSortie.setText("En cours ...");

		Object object = null;
		try {
			object = performSignature.get();
		} catch (Exception e) {
			logger.error("Signe or Verified datas.");
		}

		if (object != null) {
			if (isSigner) {
				byte[] signedDatas = (byte[]) object;
				File file = new File(signature.getAlgorithm()
						+ "_Signature.sgt");
				FileOutputStream sigfos = new FileOutputStream(file);
	            sigfos.write(signedDatas);
	            sigfos.close();
				this.fichierSortie.setText(file.getCanonicalPath());

				this.empreinteArea.setText("Signature : "
						+ new String(signedDatas));
			} else {
				boolean verifiedDatas = (boolean) object;
				this.fichierSortie.setText(null);

				this.empreinteArea.setText("Signature : " + verifiedDatas);
			}

		} else {
			if (isSigner) {
				logger.info("Error to Signed datas!");
			} else {
				logger.info("Error to verified signed data!");
			}
		}
	}

	private Object getContent(File file) throws FileNotFoundException,
			IOException, ClassNotFoundException {
		if (file == null) {
			return null;
		}

		 FileInputStream fis = new FileInputStream(file);
			byte[] byteCode = new byte[fis.available()]; 
			fis.read(byteCode);

         fis.close();
         return byteCode;
	}

	private Object getObjectContent(File file) throws FileNotFoundException,
			IOException, ClassNotFoundException {

		FileInputStream fis = new FileInputStream(file);
		ObjectInputStream ois = new ObjectInputStream(fis);
		Object obj = ois.readObject();
		ois.close();
		fis.close();
		return obj;
	}

	public void newChoixAction(ActionEvent event) {

		logger.info("Changement de mode");
		Node node = (Node) event.getSource();
		if (node.getId().equals("choixActionSigner")) {
			isSigner = true;
			fileChooserDataSign.setVisible(false);
			signPath.setVisible(false);
			fileChooserKey.setText("Choisir une clé privée");
			keyPath.setText(null);
			signPath.setText(null);

		} else if (node.getId().equals("choixActionVerifier")) {
			isSigner = false;
			fileChooserDataSign.setVisible(true);
			signPath.setVisible(true);
			fileChooserKey.setText("Choisir une clé public");
			keyPath.setText(null);
			signPath.setText(null);
		}
	}
}
