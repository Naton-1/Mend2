import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class AddNewContactScreenController {
	
	@FXML private AnchorPane pane;
	@FXML private Label errorMessage;
	@FXML private Button addContactButton, backButton;
	@FXML private ComboBox<String> childInput;
	@FXML private TextField fNameInput, lNameInput, homePhoneInput, cellPhoneInput, emailAddressInput, addressInput;
	@FXML private TextArea notesInput;
	
	
	@FXML
	private void initialize() {
		
		// Initialize all child names from database into the ComboBox
		String getChildren = "SELECT DISTINCT ForChild FROM Contacts WHERE PlanCode = '" + LoginScreenController.getSessionPlanCode() + "'";
		try (Connection conn = SQLDatabaseConnection.getConnection();
			 Statement st = conn.createStatement();
			 ResultSet rs = st.executeQuery(getChildren);) {
			while (rs.next()) {
				childInput.getItems().add(rs.getString(1));
			}
			childInput.getItems().add("Add new...");   // Allows user to add a new child
		} catch (SQLException e) {
			System.out.println("Failed to retrieve child names.");
			e.printStackTrace();
		}
		
	}
	
	// Manage the "Add new..." ComboBox functionality
	@FXML
	private void checkChildInput() {
		
		if (childInput.getValue().equals("Add new...")) {
			childInput.setEditable(true);
		} else {
			childInput.setEditable(false);
		}
		
	}
	
	// Check all inputs
	@FXML
	private void checkInputs() {
		
		errorMessage.setText("");
		
		// First name length check
		boolean fNameFits = false;
		if (!fNameInput.getText().isEmpty()) {
			if (fNameInput.getText().trim().length() <= 25) {
				fNameInput.setStyle("-fx-border-color: transparent;");
				fNameFits = true;
			} else {
				errorMessage.setText(errorMessage.getText() + "First name is too long.\n");
			}
		} else {
			fNameInput.setStyle("-fx-border-color: red;");
		}
		
		// Last name length check and "if exists" check
		boolean lNameFits = false;
		boolean lNameExists = false;
		if (!lNameInput.getText().isEmpty()) {
			if (lNameInput.getText().trim().length() <= 30) {
				lNameFits = true;
			} else {
				errorMessage.setText(errorMessage.getText() + "Last name is too long.\n");
			}
			lNameExists = true;
		}
		
		// Home phone # length check and "if exists" check
		boolean homePhoneFits = false;
		boolean homePhoneExists = false;
		if (!homePhoneInput.getText().isEmpty()) {
			if (homePhoneInput.getText().trim().length() <= 15) {
				homePhoneFits = true;
			} else {
				errorMessage.setText(errorMessage.getText() + "Home Phone is too long.\n");
			}
			homePhoneExists = true;
		}
		
		// Cell phone # length check and "if exists" check
		boolean cellPhoneFits = false;
		boolean cellPhoneExists = false;
		if (!cellPhoneInput.getText().isEmpty()) {
			if (cellPhoneInput.getText().trim().length() <= 15) {
				cellPhoneFits = true;
			} else {
				errorMessage.setText(errorMessage.getText() + "Cell Phone is too long.\n");
			}
			cellPhoneExists = true;
		}
				
		// Email length check and "if exists" check
		boolean emailFits = false;
		boolean emailExists = false;
		if (!emailAddressInput.getText().isEmpty()) {
			if (emailAddressInput.getText().trim().length() <= 254) {
				emailFits = true;
			} else {
				errorMessage.setText(errorMessage.getText() + "Email is too long.\n");
			}
			emailExists = true;
		}
		
		// Address length check and "if exists" check
		boolean addressFits = false;
		boolean addressExists = false;
		if (!addressInput.getText().isEmpty()) {
			if (addressInput.getText().trim().length() <= 100) {
				addressFits = true;
			} else {
				errorMessage.setText(errorMessage.getText() + "Address is too long.\n");
			}
			addressExists = true;
		}
		
		// Notes length check and "if exists" check
		boolean notesFits = false;
		boolean notesExists = false;
		if (!notesInput.getText().isEmpty()) {
			if (notesInput.getText().trim().length() <= 300) {
				notesFits = true;
			} else {
				errorMessage.setText(errorMessage.getText() + "Notes are too long.\n");
			}
			notesExists = true;
		}
		
		// Check if category has been selected
		boolean validChild = false;
		if (!childInput.isEditable()) {   // If the ComboBox is editable, that means the user entered a new category.
			if (childInput.getValue() == null) {
				errorMessage.setText(errorMessage.getText() + "Please select a name.\n");
			} else {
				validChild = true;
			}
		} else {
			if (!childInput.getEditor().getText().isEmpty()) {
				if (childInput.getEditor().getText().trim().length() <= 20) {   // Check if custom category entered is too long
					validChild = true;
				} else {
					errorMessage.setText(errorMessage.getText() + "Child name is too long.\n");
				}
			} else {
				errorMessage.setText(errorMessage.getText() + "Please enter a name.\n");
			}
		}
		
		/*
		 *  Check if either input "x" does not exist, or if it fits the length requirements (which means it exists)
		 *  
		 */
		if (fNameFits && (lNameFits || !lNameExists) && (homePhoneFits || !homePhoneExists) && (cellPhoneFits || !cellPhoneExists) &&
		   (emailFits || !emailExists) && (addressFits || !addressExists) && (notesFits || !notesExists) && validChild) {
			enterContact(lNameExists, homePhoneExists, cellPhoneExists, emailExists, addressExists, notesExists);
		}
		
	}
	
	private void enterContact(boolean lNameExists, boolean homePhoneExists, boolean cellPhoneExists, boolean emailExists, boolean addressExists, boolean notesExists) {
		
		String planCode = LoginScreenController.getSessionPlanCode();
		String newContactID = generateContactID(planCode);
		
		String createContact = "INSERT INTO Contacts (ContactID, PlanCode, ForChild, FirstName, LastName, HomePhone, CellPhone, Email, Address, Notes) "
							 + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
		try (Connection conn = SQLDatabaseConnection.getConnection();
			 PreparedStatement ps = conn.prepareStatement(createContact);) {
			
			ps.setString(1, newContactID);
			ps.setString(2, planCode);
			ps.setNString(4, fNameInput.getText().trim());
			
			// The following fields are optional, so we must check to see if we should insert NULL values
			if (lNameExists) {
				ps.setNString(5, lNameInput.getText().trim());
			} else {
				ps.setNull(5, Types.NVARCHAR);
			}
			if (homePhoneExists) {
				ps.setString(6, homePhoneInput.getText().trim());
			} else {
				ps.setNull(6, Types.VARCHAR);
			}
			if (cellPhoneExists) {
				ps.setString(7, cellPhoneInput.getText().trim());
			} else {
				ps.setNull(7, Types.VARCHAR);
			}
			if (emailExists) {
				ps.setString(8, emailAddressInput.getText().trim());
			} else {
				ps.setNull(8, Types.VARCHAR);
			}
			if (addressExists) {
				ps.setNString(9, addressInput.getText().trim());
			} else {
				ps.setNull(9, Types.NVARCHAR);
			}
			if (notesExists) {
				ps.setNString(10, notesInput.getText().trim());
			} else {
				ps.setNull(10, Types.NVARCHAR);
			}
			
			// If the ComboBox is editable, that means the user entered a new child
			if (childInput.getEditor() == null) {
				ps.setNString(3, childInput.getValue());
			} else {
				ps.setNString(3, childInput.getEditor().getText().trim());
			}
						
			ps.executeUpdate();
			
			// Clear all TextFields and inform user
			fNameInput.clear();
			lNameInput.clear();
			homePhoneInput.clear();
			cellPhoneInput.clear();
			emailAddressInput.clear();
			addressInput.clear();
			notesInput.clear();
			errorMessage.setTextFill(Color.web("#40da2b"));
			errorMessage.setText("Contact successfully created.");
			
		} catch (SQLException e) {
			System.out.println("Failed to insert the Contact into the database.");
			e.printStackTrace();
		}
		
	}
	
	// Generate a new ContactID
	private String generateContactID(String planCode) {
		
		String contactID = null;
		
		String getTopContactID = "SELECT ContactID FROM Contacts WHERE PlanCode = '" + planCode + "' ORDER BY ContactID DESC";
		try (Connection conn = SQLDatabaseConnection.getConnection();
			 Statement st = conn.createStatement();
			 ResultSet rs = st.executeQuery(getTopContactID);) {
			
			if (!rs.isBeforeFirst()) {
				contactID = "00001";
			} else if (rs.next()) {
				String oldID = rs.getString(1);
				contactID = String.format("%05d",   // Zero-padding with a length of 5
						(Integer.parseInt(oldID) + 1));   // Increment previous ExpenseID by 1 
			}
			
		} catch (SQLException e) {
			System.out.println("Failed to generate ContactID.");
			e.printStackTrace();
		}
		return contactID;
		
	}
	
	// Go to the previous screen
	@FXML
	private void goBack() throws IOException {
		Stage stage;
		Parent contactsScreen;
		
		stage = (Stage) backButton.getScene().getWindow();
		contactsScreen = FXMLLoader.load(getClass().getResource("ContactsScreen.fxml"));
		
		stage.setScene(new Scene(contactsScreen));
		stage.setTitle("Contacts");
	}
	
}
