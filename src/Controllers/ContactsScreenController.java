import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class ContactsScreenController {
	
	@FXML private VBox vbox;
	@FXML private AnchorPane pane;
	@FXML private Button backButton, addContactButton;
	
	public static ContactsScreenController csc;
	
	@FXML
	private void initialize() {
		
		// Set a public instance of this controller class that can be used by other classes
		csc = this;
		
		pane.requestFocus();
		getContacts();
		
	}
	
	// Get Contacts info from database and add them to VBox
	@FXML
	private void getContacts() {
		
		String planCode = LoginScreenController.getSessionPlanCode();
		String pullContacts = "SELECT ContactID, ForChild, FirstName, LastName, HomePhone, CellPhone, Email, Address, Notes FROM Contacts WHERE PlanCode = '" + planCode + "'";
		
		// Get Contact data from database
		try (Connection conn = SQLDatabaseConnection.getConnection();
			 Statement st = conn.createStatement();
			 ResultSet rs = st.executeQuery(pullContacts);) {
			
			while (rs.next()) {
				
				// Instantiate Strings first to deal with potential NULL values in ResultSet
				String contactID = "", childName = "", fName = "", lName = "", homePhone = "", cellPhone = "", email = "", address = "", notes = "";   
				contactID += rs.getString(1);
				childName += rs.getNString(2);
				fName += rs.getNString(3);
				
				/* 
				 * Since the below columns are optional in the database, check for null values. 
				 * ResultSet returns the string "null" if the value is NULL in the database.
				 */
				lName += rs.getNString(4);
				if (rs.wasNull()) {
					lName = "";
				}
				homePhone += rs.getString(5);
				if (rs.wasNull()) {
					homePhone = "";
				}
				cellPhone += rs.getString(6);
				if (rs.wasNull()) {
					cellPhone = "";
				}
				email += rs.getString(7);
				if (rs.wasNull()) {
					email = "";
				}
				address += rs.getNString(8);
				if (rs.wasNull()) {
					address = "";
				}
				notes += rs.getNString(9);
				if (rs.wasNull()) {
					notes = "";
				}
				
				// Create new "Contact Box" to add to VBox (which is in ScrollPane)
				ContactBox cb = new ContactBox(contactID, fName, lName, childName, homePhone, cellPhone, email, address, notes);
				HBox contactRow = cb.getHBox();
				
				// Add the "Contact Box" to the VBox
				vbox.getChildren().add(contactRow);
				
			}
			
		} catch (SQLException e) {
			System.out.println("Failed to retrieve contacts info.");
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("Failed to create a Contact Box.");
			e.printStackTrace();
		}
		
	}
	
	// Allows outside classes to re-layout the VBox
	@FXML
	public void updateVboxLayout() {
		
		vbox.getChildren().clear();
		getContacts();
		vbox.layout();
		
	}
	
	@FXML
	private void addContact() throws IOException {
		Stage stage;
		Parent addContactScreen;
		
		stage = (Stage) addContactButton.getScene().getWindow();
		addContactScreen = FXMLLoader.load(getClass().getResource("AddNewContactScreen.fxml"));
		
		stage.setScene(new Scene(addContactScreen));
		stage.setTitle("Add Contact");
	}
	
	// Go to the previous screen
	@FXML
	private void goBack() throws IOException {
		Stage stage;
		Parent mainMenuScreen;
		
		stage = (Stage) backButton.getScene().getWindow();
		mainMenuScreen = FXMLLoader.load(getClass().getResource("MainMenuScreen.fxml"));
		
		stage.setScene(new Scene(mainMenuScreen));
		stage.setTitle("Main Menu");
	}
	
}
