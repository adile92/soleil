package controller;

import javafx.fxml.FXML;
import javafx.scene.control.Dialogs;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import model.Certificate;

/**
 * Dialog to edit details of a person.
 * 
 * @author Marco Jakob
 */
public class CertificateEditDialogController {
	
     
	@FXML
	private TextField commonName;
	@FXML
	private TextField organizationalUnit;
	@FXML
	private TextField organizationName;
	@FXML
	private TextField  locationF;
	@FXML
	private TextField  state;
	@FXML
	private TextField  country;
	
	
	private Stage dialogStage;
	private Certificate certificate;
	private boolean okClicked = false;
	
	/**
	 * Initializes the controller class. This method is automatically called
	 * after the fxml file has been loaded.
	 */
	@FXML
	private void initialize() {
		
	}
	
	/**
	 * Sets the stage of this dialog.
	 * @param dialogStage
	 */
	public void setDialogStage(Stage dialogStage) {
		this.dialogStage = dialogStage;
	}
	
	/**
	 * Sets the person to be edited in the dialog.
	 * 
	 * @param person
	 */
	public void setCertificate(Certificate certificate) {
		this.certificate = certificate;
	}
	
	/**
	 * Returns true if the user clicked OK, false otherwise.
	 * @return
	 */
	public boolean isOkClicked() {
		return okClicked;
	}
	
	public TextField getCommonName() {
		return commonName;
	}

	public void setCommonName(TextField commonName) {
		this.commonName = commonName;
	}

	public TextField getOrganizationalUnit() {
		return organizationalUnit;
	}

	public void setOrganizationalUnit(TextField organizationalUnit) {
		this.organizationalUnit = organizationalUnit;
	}

	public TextField getOrganizationName() {
		return organizationName;
	}

	public void setOrganizationName(TextField organizationName) {
		this.organizationName = organizationName;
	}

	public TextField getLocation() {
		return locationF;
	}

	public void setLocation(TextField location) {
		this.locationF = location;
	}

	public TextField getState() {
		return state;
	}

	public void setState(TextField state) {
		this.state = state;
	}

	public TextField getCountry() {
		return country;
	}

	public void setCountry(TextField country) {
		this.country = country;
	}

	public Stage getDialogStage() {
		return dialogStage;
	}

	public Certificate getCertificate() {
		return certificate;
	}

	/**
	 * Called when the user clicks cancel.
	 */
	@FXML
	private void handleCancel() {
		okClicked = false;
		certificate = null;
		dialogStage.close();
	}
	
	/**
	 * Called when the user clicks ok.
	 */
	@FXML
	private void handleOk() {
		if (isInputValid()) {
			certificate.setC(country.getText());
			certificate.setCN(commonName.getText());
			certificate.setL(locationF.getText());
			certificate.setO(organizationName.getText());
			certificate.setOU(organizationalUnit.getText());
			certificate.setS(state.getText());
			
			okClicked = true;
			dialogStage.close();
		}
	}
	
	/**
	 * Validates the user input in the text fields.
	 * 
	 * @return true if the input is valid
	 */
	private boolean isInputValid() {
		String errorMessage = "";

		if (state.getText() == null || state.getText().length() == 0) {
			errorMessage += "Etat non valide!\n"; 
		}
		if (country.getText() == null || country.getText().length() == 0) {
			errorMessage += "Pays non valide!\n"; 
		}
		if (locationF.getText() == null || locationF.getText().length() == 0) {
			errorMessage += "Emplacement non valide!\n"; 
		}
		
		if (commonName.getText() == null || commonName.getText().length() == 0) {
			errorMessage += "Nom commun non valide!\n"; 
		} 
		
		if (organizationalUnit.getText() == null || organizationalUnit.getText().length() == 0) {
			errorMessage += "Unité organisationnellet non valide!\n"; 
		}
		
		if (organizationName.getText() == null || organizationName.getText().length() == 0) {
			errorMessage += "Nom de l'organisation non valide!\n";
		} 
		if (errorMessage.length() == 0) {
			return true;
		} else {
			// Show the error message
			Dialogs.showErrorDialog(dialogStage, errorMessage,
					"S'il vous plaît corriger les champs invalides", "Champs Invalides");
			return false;
		}
	}
}
