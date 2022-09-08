import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.NoSuchElementException;
import java.util.Optional;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;


public class ContactBox {
	
	@FXML private HBox hbox;
	@FXML private Label contactName, childName, homePhone, cellPhone, emailAddress, address, notes;
	@FXML private Button deleteButton;
	
	private String fName, lName, contactID;
	
	// Default constructor
	public ContactBox() {
		
		try {
			
			// Get the layout plan from the .fxml file
			FXMLLoader loader = new FXMLLoader(getClass().getResource("ContactBox.fxml"));
			loader.load();
			
			// Apply extracted layouts to the new FXML elements
			hbox = (HBox) loader.getNamespace().get("hbox");
			contactName = (Label) loader.getNamespace().get("contactName");
			childName = (Label) loader.getNamespace().get("childName");
			homePhone = (Label) loader.getNamespace().get("homePhone");
			cellPhone = (Label) loader.getNamespace().get("cellPhone");
			emailAddress = (Label) loader.getNamespace().get("emailAddress");
			address = (Label) loader.getNamespace().get("address");
			notes = (Label) loader.getNamespace().get("notes");
			deleteButton = (Button) loader.getNamespace().get("deleteButton");
			
		} catch (IOException e) {
			System.out.println("Failed loading the ContactBox FXML resource.");
			e.printStackTrace();
		}
		
	}
	
	// Structured constructor
	public ContactBox(String contactIDInput, String contactFNameInput, String contactLNameInput, String childNameInput, String homePhoneInput, String cellPhoneInput, String emailAddressInput, 
					  String addressInput, String notesInput) throws IOException {
		
		// Calls default constructor
		this();
		
		fName = contactFNameInput;
		lName = contactLNameInput;
		contactID = contactIDInput;
		
		// Set labels to information passed from Contacts Screen's database info
		contactName.setText(contactFNameInput + " " + contactLNameInput);
		childName.setText(childName.getText() + childNameInput);
		homePhone.setText(homePhone.getText() + homePhoneInput);
		cellPhone.setText(cellPhone.getText() + cellPhoneInput);
		emailAddress.setText(emailAddress.getText() + emailAddressInput);
		address.setText(address.getText() + addressInput);
		notes.setText(notes.getText() + notesInput);
		
		// Set delete button's actions
		deleteButton.setOnAction((event) -> {
			
			// Create confirmation Alert
			Alert warning = new Alert(AlertType.WARNING);
			warning.setTitle("Confirm");
			warning.setHeaderText("Are you sure you want to delete this contact?");
			ButtonType yes = new ButtonType("Yes");
			ButtonType cancel = new ButtonType("Cancel", ButtonData.CANCEL_CLOSE);
			warning.getButtonTypes().setAll(yes, cancel);
			
			Optional<ButtonType> result = warning.showAndWait();
			// Get rid of errors showing
			try {
				if (result.get() == yes) {
					deleteContact(this);
				}
			} catch (NoSuchElementException e) {
				System.out.println("User exited popup using the red X!");
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		});
		
	}
	
	// Returns the HBox with all its children to the controller class
	public HBox getHBox() {
		return hbox;
	}
	
	// Deletes the Contact from database, given a ContactBox object
	private void deleteContact(ContactBox cb) throws IOException {
		
		String deleteContact = "DELETE FROM Contacts WHERE ContactID = '" + cb.contactID + "'";
		
		try (Connection conn = SQLDatabaseConnection.getConnection();
			 Statement st = conn.createStatement();) { 
			
			int count = st.executeUpdate(deleteContact);
			
			// If a contact was deleted, update the VBox's layout using the static instance of the ContactsScreenController
			if (count == 1) {
				ContactsScreenController.csc.updateVboxLayout();
			}
			
		} catch (SQLException e) {
			System.out.println("Failed to delete the contact.");
			e.printStackTrace();
		}
		
	}
	
}
