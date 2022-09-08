import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Optional;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class SettingsScreenController {
	
	@FXML private AnchorPane pane;
	@FXML private Button themeButton, changeCurrencyButton, changeNameButton, changeEmailButton, changePasswordButton, backButton;
	
	@FXML
	private void initialize() {
		pane.requestFocus();
	}
	
	@FXML
	private void goToThemePicker() throws IOException {
		Stage stage;
		Parent themeScreen;
		
		stage = (Stage) themeButton.getScene().getWindow();
		themeScreen = FXMLLoader.load(getClass().getResource("ThemesScreen.fxml"));
		
		stage.setScene(new Scene(themeScreen));
		stage.setTitle("Theme Picker");
	}
	
	@FXML
	private void changeName() {
		
		String sql = null;
		
		// Initial popup
		TextInputDialog change = new TextInputDialog();
		change.setTitle("Change First Name");
		change.setHeaderText("Enter your new first name.");
		
		// Create a generic error popup
		Alert genericErrorAlert = new Alert(AlertType.ERROR);
		genericErrorAlert.setTitle("Oops!");
		
		// Display the initial popup
		boolean fNameFits = false;
		do {
			Optional<String> changeResult = change.showAndWait();
			if (changeResult.isPresent()) {
				String newFName = changeResult.get();
				
				if (!changeResult.get().isEmpty()) {   // Check is user entered nothing
					if (!Character.isWhitespace(newFName.charAt(0))) {   // Check if user entered whitespace only
						
						// First name length check
						if (newFName.trim().length() <= 25) {
							fNameFits = true;
							String username = LoginScreenController.getSessionUsername();
							sql = "UPDATE Users SET FirstName = '" + newFName + "' WHERE Username = '" + username + "'";
							
							TextInputDialog change2 = new TextInputDialog();
							change2.setTitle("Change Last Name");
							change2.setHeaderText("Enter your new last name.");
							change2.setContentText("Enter nothing if you do not want to change it.");
							boolean lNameFits = false;
							
							do {
								Optional<String> change2Result = change2.showAndWait();
								if (change2Result.isPresent()) {
									String newLName = change2Result.get();
									
									if (!change2Result.get().isEmpty()) {
										if (!Character.isWhitespace(newLName.charAt(0))) {   // Check if user entered whitespace only
											
											// Last name length check
											if (newLName.trim().length() <= 30) {
												lNameFits = true;
												sql = "UPDATE Users SET FirstName = '" + newFName + "', LastName = '" + newLName + "' WHERE Username = '" + username + "'";
											} else {
												genericErrorAlert.setHeaderText("Last name is too long.");
												genericErrorAlert.showAndWait();
											}
										}
										// User entered just spaces
										else {
											genericErrorAlert.setHeaderText("Input something besides spaces!");
											genericErrorAlert.showAndWait();
										}
									}
									// User entered nothing
									else {
										lNameFits = true;
									}
								} 
							} while (!lNameFits);
							
						} else {
							genericErrorAlert.setHeaderText("First name is too long.");
							genericErrorAlert.showAndWait();
						}
					} 
					// User entered whitespace
					else {
						genericErrorAlert.setHeaderText("Input something besides spaces.");
						genericErrorAlert.showAndWait();
					}
				} 
				// User entered nothing
				else {
					genericErrorAlert.setHeaderText("Input *something*!");
					genericErrorAlert.showAndWait();
				}
			} else {
				fNameFits = true;
			}
		} while (!fNameFits);
		
		if (sql != null) {
			// Execute the SQL statement, which depends on what the user chose
			try (Connection conn = SQLDatabaseConnection.getConnection();
				 Statement st = conn.createStatement();) {
				int rowsAffected = st.executeUpdate(sql);
				System.out.println(rowsAffected + " rows affected.");
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
	}
	
	@FXML
	private void changeEmail() {
		
		String sql = null;
		
		// Initial popup
		TextInputDialog change = new TextInputDialog();
		change.setTitle("Change Email");
		change.setHeaderText("Enter your new email.");
		
		// Create a generic error popup
		Alert genericErrorAlert = new Alert(AlertType.ERROR);
		genericErrorAlert.setTitle("Oops!");
		
		// Display the initial popup
		boolean validEmail = false;
		do {
			Optional<String> changeResult = change.showAndWait();
			if (changeResult.isPresent()) {
				String newEmail = changeResult.get();
				
				if (!changeResult.get().isEmpty()) {   // Check is user entered nothing
					if (!Character.isWhitespace(newEmail.charAt(0))) {   // Check if user entered whitespace only
						// Email length check
						if (newEmail.trim().length() <= 254) {
							try {
								InternetAddress testEmail = new InternetAddress(newEmail);
								testEmail.validate();   // Check email input syntax
								validEmail = true;
								
								String username = LoginScreenController.getSessionUsername();
								sql = "UPDATE Users SET Email = '" + newEmail + "' WHERE Username = '" + username + "'";
							} catch (AddressException e) {
								genericErrorAlert.setHeaderText("Invalid email syntax.");
								genericErrorAlert.showAndWait();
							}
						} else {
							genericErrorAlert.setHeaderText("Email is too long.");
							genericErrorAlert.showAndWait();
						}
					} 
					// User entered whitespace
					else {
						genericErrorAlert.setHeaderText("Input something besides spaces.");
						genericErrorAlert.showAndWait();
					}
				} 
				// User entered nothing
				else {
					genericErrorAlert.setHeaderText("Input *something*!");
					genericErrorAlert.showAndWait();
				}
			} else {
				validEmail = true;
			}
		} while (!validEmail);
		
		if (sql != null) {
			// Execute the SQL statement, which depends on what the user chose
			try (Connection conn = SQLDatabaseConnection.getConnection();
				 Statement st = conn.createStatement();) {
				int rowsAffected = st.executeUpdate(sql);
				System.out.println(rowsAffected + " rows affected.");
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
	}
	
	@FXML
	private void changePassword() {
		
		String sql = null;
		
		// Initial popup
		TextInputDialog change = new TextInputDialog();
		change.setTitle("Change Password");
		change.setHeaderText("Enter your new password.");
		change.setContentText("Use between 8 and 60 characters in your password.");
		
		// Create a generic error popup
		Alert genericErrorAlert = new Alert(AlertType.ERROR);
		genericErrorAlert.setTitle("Oops!");
		
		// Display the initial popup
		boolean validPassword = false;
		do {
			Optional<String> changeResult = change.showAndWait();
			if (changeResult.isPresent()) {
				String newPassword = changeResult.get();
				
				if (!changeResult.get().isEmpty()) {   // Check is user entered nothing
					// Password length check
					if (newPassword.length() >= 8 && newPassword.length() <= 60) {
						validPassword = true;
						String username = LoginScreenController.getSessionUsername();
						sql = "UPDATE Users SET Password = '" + newPassword + "' WHERE Username = '" + username + "'";
					} else if (newPassword.length() < 8) {
						genericErrorAlert.setHeaderText("Password is too short.");
						genericErrorAlert.showAndWait();
					} else {
						genericErrorAlert.setHeaderText("Password is too long");
						genericErrorAlert.showAndWait();
					} 
				} 
				// User entered nothing
				else {
					genericErrorAlert.setHeaderText("Input *something*!");
					genericErrorAlert.showAndWait();
				}
			} else {
				validPassword = true;
			}
		} while (!validPassword);
		
		if (sql != null) {
			// Execute the SQL statement, which depends on what the user chose
			try (Connection conn = SQLDatabaseConnection.getConnection();
				 Statement st = conn.createStatement();) {
				int rowsAffected = st.executeUpdate(sql);
				System.out.println(rowsAffected + " rows affected.");
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
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
