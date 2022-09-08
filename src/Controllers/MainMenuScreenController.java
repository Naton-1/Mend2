import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class MainMenuScreenController {
	
	@FXML private AnchorPane pane;
	@FXML private Button expensesButton, trustedContactsButton, settingsButton, manageAccountsButton, logOutButton;
	@FXML private Label planCode;

	
	// Anytime the user goes to the Main Menu screen, their Plan Code is displayed in the bottom-left corner
	@FXML
	private void initialize() {
		pane.requestFocus();
		
		// Retrieve User's plan code and display it on the main menu
			
		String username = LoginScreenController.getSessionUsername();
		String getUserDetails = "SELECT PlanCode, IsAdmin FROM Users WHERE Username = '" + username + "'";
		try (Connection conn = SQLDatabaseConnection.getConnection();
			 Statement st = conn.createStatement();
			 ResultSet rs = st.executeQuery(getUserDetails);) {
			
			rs.next();
			String retrievedPlanCode = rs.getString(1);
			int retrievedAdminStatus = rs.getInt(2);
			
			LoginScreenController.setSessionPlanCode(retrievedPlanCode);
			planCode.setText("Plan Code: " + retrievedPlanCode);
			
			if (retrievedAdminStatus == 0) {   // If User is not an Admin, hide the "Manage Accounts" button
				manageAccountsButton.setVisible(false);
			}
		} catch (SQLException e) {
			System.out.println("Failed to retrieve plan code.");
			e.printStackTrace();
		}
	}
	
	// Go to the Expenses Screen
	@FXML 
	private void goToExpenses() throws Exception {
		Stage stage;
		Parent expensesScreen;
		
		stage = (Stage) expensesButton.getScene().getWindow();
		expensesScreen = FXMLLoader.load(getClass().getResource("ExpenseMainScreen.fxml"));
		
		stage.setScene(new Scene(expensesScreen));
		stage.setTitle("Expenses");
	}
	
	// Go to the Contacts Screen
	@FXML
	private void goToContacts() throws Exception {
		Stage stage;
		Parent contactsScreen;
		
		stage = (Stage) trustedContactsButton.getScene().getWindow();
		contactsScreen = FXMLLoader.load(getClass().getResource("ContactsScreen.fxml"));
		
		stage.setScene(new Scene(contactsScreen));
		stage.setTitle("Contacts");
	}
	
	// Go to the Settings Screen
	@FXML
	private void goToSettings() throws Exception {
		Stage stage;
		Parent settingsScreen;
		
		stage = (Stage) settingsButton.getScene().getWindow();
		settingsScreen = FXMLLoader.load(getClass().getResource("SettingsScreen.fxml"));
		
		stage.setScene(new Scene(settingsScreen));
		stage.setTitle("Settings");
	}
	
	// Go to the Manage Account screen
	@FXML
	private void goToManage() throws Exception {
		Stage stage;
		Parent manageScreen;
		
		stage = (Stage) manageAccountsButton.getScene().getWindow();
		manageScreen = FXMLLoader.load(getClass().getResource("AdminScreen.fxml"));
		
		stage.setScene(new Scene(manageScreen));
		stage.setTitle("Admin Console");
	}
	
	// Go back to the Login screen and clear the login session data
	@FXML
	private void logOut() throws Exception {
		Stage stage;
		Parent loginScreen;
		
		stage = (Stage) logOutButton.getScene().getWindow();
		loginScreen = FXMLLoader.load(getClass().getResource("LoginScreen.fxml"));
		
		stage.setScene(new Scene(loginScreen));
		stage.setTitle("Login");
		
		LoginScreenController.clearLoginSession();   // Clear the current login session
	}
	
}
