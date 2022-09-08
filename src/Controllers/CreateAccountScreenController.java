import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
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
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class CreateAccountScreenController {
	
	@FXML private AnchorPane pane;
	@FXML private Button backButton, createAccountButton;
	@FXML private TextField fName, lName, email, username;
	@FXML private PasswordField password, confPassword;
	@FXML private Label errorLog;
	
	@FXML
	private void initialize() {
		pane.requestFocus();
	}
	
	// Check all inputs
	@FXML
	private void checkInputs() throws Exception {
			
		errorLog.setText("");
		
		// First name length check
		boolean fNameFits = false;
		if (!fName.getText().isEmpty()) {
			if (fName.getText().trim().length() <= 25) {
				fNameFits = true;
				fName.setStyle("-fx-border-color: transparent;");
			} else {
				errorLog.setText(errorLog.getText() + "First name is too long.\n");
			}
		} else {
			fName.setStyle("-fx-border-color: red;");
		}
		
		// Last name length check
		boolean lNameFits = false;
		if (!lName.getText().isEmpty()) {
			if (lName.getText().trim().length() <= 30) {
				lNameFits = true;
				lName.setStyle("-fx-border-color: transparent;");
			} else {
				errorLog.setText(errorLog.getText() + "Last name is too long.\n");
			}
		} else {
			lName.setStyle("-fx-border-color: red;");
		}
		
		// Email length check
		boolean emailFits = false;
		boolean validEmail = false;
		if (!email.getText().isEmpty()) {
			if (email.getText().trim().length() <= 254) {
				emailFits = true;
				// Valid email check
				try {
					InternetAddress testEmail = new InternetAddress(email.getText());
					testEmail.validate();   // Check email input syntax
					validEmail = true;
					email.setStyle("-fx-border-color: transparent;");
				}
				catch (AddressException e) {
					errorLog.setText(errorLog.getText() + "Invalid email syntax.\n");
				}
			} else {
				errorLog.setText(errorLog.getText() + "Email is too long.\n");
			}
		} else {
			email.setStyle("-fx-border-color: red;");
		}
		
		// Password length check
		boolean longPassword = false;
		if (!password.getText().isEmpty()) {
			if (password.getText().length() >= 8 && password.getText().length() <= 60) {
				longPassword = true;
				password.setStyle("-fx-border-color: transparent;");
			} else if (password.getText().length() < 8) {
				errorLog.setText(errorLog.getText() + "Password is too short.\n");
			} else {
				errorLog.setText(errorLog.getText() + "Password is too long.\n");
			} 
		} else {
			password.setStyle("-fx-border-color: red;");
		}
		
		// Confirm Password check
		boolean passwordConfirmed = false;
		if (!confPassword.getText().isEmpty()) {
			if (password.getText().equals(confPassword.getText())) {
				passwordConfirmed = true;
				confPassword.setStyle("-fx-border-color: transparent;");
			} else {
				errorLog.setText(errorLog.getText() + "Both passwords are not the same.\n");
			}
		} else {
			confPassword.setStyle("-fx-border-color: red;");
		}
		
		// Duplicate usernames check
		boolean uniqueUsername = false;
		if (!username.getText().isEmpty()) {
			String checkUsername = "SELECT Username FROM Users WHERE Username = '" + username.getText() + "'";
			try (Connection conn = SQLDatabaseConnection.getConnection();
				 Statement st = conn.createStatement();
				 ResultSet rs = st.executeQuery(checkUsername);) {
				if (rs.isBeforeFirst()) {   // If the ResultSet returned values, then that username already exists in the database
					errorLog.setText(errorLog.getText() + "That username is already associated with another account.\n");   // Display warning message
				} 
				else {
					uniqueUsername = true;
					username.setStyle("-fx-border-color: transparent;");
				}
			}
			catch (SQLException e) {
				System.out.println("Failed to retrieve username from SQL server.");   // Print error message to console
				e.printStackTrace();
			}
		} else {
			username.setStyle("-fx-border-color: red;");
		}
		
		// Duplicate email check
		boolean uniqueEmail = false;
		if (!email.getText().isEmpty()) {
			String checkEmail = "SELECT Email FROM Users WHERE Email = '" + email.getText() + "'";
			try (Connection conn = SQLDatabaseConnection.getConnection();
				 Statement st = conn.createStatement();
				 ResultSet rs = st.executeQuery(checkEmail);) {
				if (rs.isBeforeFirst()) {	// If the ResultSet returned values, then that email already exists in the database
					errorLog.setText(errorLog.getText() + "That email is already associated with another account.\n");
				}
				
				else {
					uniqueEmail = true;
					email.setStyle("-fx-border-color: transparent;");
				}
			}
			catch (SQLException e) {
				System.out.println("Failed to retrieve email from SQL server.");   // Print error message to console
				e.printStackTrace();
			}
		} else {
			email.setStyle("-fx-border-color: red;");
		}
		
		// If all validators pass, then create the account and insert data into the database
		if (fNameFits && lNameFits && emailFits && validEmail && longPassword && passwordConfirmed && uniqueUsername && uniqueEmail) {
			displayIntro();
		}
		
	}
	
	// Prompts to generate a Plan Code
	@FXML
	private void displayIntro() {
		
		String planCode = "";
		
		Alert checkForPlan = new Alert(AlertType.CONFIRMATION);
		checkForPlan.setTitle("Introduction");
		checkForPlan.setHeaderText("Are you making a new Plan or joining an existing Plan?");
		ButtonType join = new ButtonType("Join");
		ButtonType make = new ButtonType("Make");
		ButtonType cancel = new ButtonType("Cancel", ButtonData.CANCEL_CLOSE);
		checkForPlan.getButtonTypes().setAll(join, make, cancel);
		
		Optional<ButtonType> response = checkForPlan.showAndWait();
		if (response.get() == join) {
			
			TextInputDialog joinDialog = new TextInputDialog();
			joinDialog.setTitle("Join a Plan");
			joinDialog.setHeaderText("Enter the invite code of the Plan you are joining.");
			boolean validLength = false;
			do {
				Optional<String> input = joinDialog.showAndWait();
				if (input.isPresent()) {
					if (input.get().trim().length() == 5) {
						validLength = true;
						planCode = input.get().trim();
						
						createAccount(planCode);
					} else {
						joinDialog.setContentText("A Plan code is exactly 5 numbers.");
					}
				} else {
					validLength = true;
				}
			} while (!validLength);
			
		} else if (response.get() == make) {
			
			//Generate the new plan code for the user
			String getTopPlanCode = "SELECT PlanCode FROM Users ORDER BY PlanCode DESC";   
			try (Connection conn = SQLDatabaseConnection.getConnection();
				 Statement st = conn.createStatement();
				 ResultSet rs = st.executeQuery(getTopPlanCode);) {
				rs.next();
				planCode = String.format("%05d", (Integer.parseInt(rs.getString("PlanCode") + 1)));
				
				createAccount(planCode);
			} catch (SQLException e) {
				System.out.println("Failed to retrieve top plan code.");
				e.printStackTrace();
			}
			
		} else {
			checkForPlan.close();
		}
		
	}
	
	// Create the account in the database
	@FXML
	private void createAccount(String planCode) {
		
		String insertAndCreate = "INSERT INTO Users (Username, Password, Email, FirstName, LastName, PlanCode, IsAdmin) VALUES (?, ?, ?, ?, ?, ?, ?)";
		try (Connection conn = SQLDatabaseConnection.getConnection();
			 PreparedStatement ps = conn.prepareStatement(insertAndCreate)) {
			ps.setString(1, username.getText().trim());
			ps.setString(2, password.getText().trim());
			ps.setString(3, email.getText().trim());
			ps.setString(4, fName.getText().trim());
			ps.setString(5, lName.getText().trim());
			ps.setString(6, planCode);
			ps.setBoolean(7, false);
			ps.executeUpdate();
			
			// Clear all TextFields and tell the user to log in now
			username.clear();
			password.clear();
			confPassword.clear();
			email.clear();
			fName.clear();
			lName.clear();
			errorLog.setTextFill(Color.web("#40da2b"));
			errorLog.setText("Your account has been created. Please log in.");
		}
		catch (SQLException e) {
			System.out.println("Failed to create a new user.");   // Print error message to console
			e.printStackTrace();
		}
		
	}
	
	// Go back to the Login Screen
	@FXML
	private void goBack() throws Exception {
		
		Stage stage;
		Parent loginScreen;
		 
		stage = (Stage) backButton.getScene().getWindow();
		loginScreen = FXMLLoader.load(getClass().getResource("LoginScreen.fxml"));
		
		stage.setScene(new Scene(loginScreen));
		stage.setTitle("Login");
		
	}
	
}