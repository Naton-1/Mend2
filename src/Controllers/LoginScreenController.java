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
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

public class LoginScreenController {

	private static String username;
	private static String planCode;
	
	@FXML private GridPane pane;
	@FXML private Button loginButton, createAccountButton, forgotPasswordButton;
	@FXML private TextField usernameInput;
	@FXML private PasswordField passwordInput;
	@FXML private Label message; 
	
	@FXML
	private void initialize() {
		pane.requestFocus();
	}
	
	// Log the user in by connecting to database and check credentials against it
	@FXML
	private void login() throws Exception {
		if (usernameInput.getText().isEmpty() || passwordInput.getText().isEmpty()) {   // Check to see if either TextField is empty; display warning message if true
			message.setText("Enter credentials in both fields.");
		} else if (Character.isWhitespace(usernameInput.getText().charAt(0))) {   // Check to see if just spaces were entered
			message.setText(message.getText() + "\nNo spaces are allowed before the username.");
		} else {
			boolean credentials = false;
			String login = "SELECT Username, Password FROM Users WHERE Username = '" + usernameInput.getText() + "' AND Password = '" + passwordInput.getText() + "'";
			
			try (Connection conn = SQLDatabaseConnection.getConnection();
				 Statement st = conn.createStatement();
				 ResultSet rs = st.executeQuery(login);) {	// Enclosing statements within the try statement ensures the connection automatically gets closed
				if (rs.isBeforeFirst()) {	// If the ResultSet returned values, then credentials are valid
					credentials = true;
				}
			}
			catch (SQLException e) {
				e.printStackTrace();
			}

			// Switch to the Main Menu screen if credentials are valid
			if (credentials) {
				setSessionUsername(usernameInput.getText());
				Stage stage;
				Parent mainMenuScreen;
				
				stage = (Stage) loginButton.getScene().getWindow();
				mainMenuScreen = FXMLLoader.load(getClass().getResource("MainMenuScreen.fxml"));
				
				stage.setScene(new Scene(mainMenuScreen));
			} else if (!credentials) {
				message.setText("Incorrect credentials.");   // Display warning message on GUI if credentials are invalid
			}
		}
	}
	
	private static void setSessionUsername(String username) {
		LoginScreenController.username = username;
	}

	public static String getSessionUsername() {
		return LoginScreenController.username;
	}
	
	public static void setSessionPlanCode(String code) {
		LoginScreenController.planCode = code;
	}
	
	public static String getSessionPlanCode() {
		return LoginScreenController.planCode;
	}
	
	public static void clearLoginSession() {
		LoginScreenController.username = null;
		LoginScreenController.planCode = null;
	}
	
	// Switch to the Create Account screen if "Create Account" button is pressed
	@FXML
	private void createAccount() throws Exception {
		Stage stage;
		Parent createAccountScreen;
		
		stage = (Stage) createAccountButton.getScene().getWindow();
		createAccountScreen = FXMLLoader.load(getClass().getResource("CreateAccountScreen.fxml"));
		
		stage.setScene(new Scene(createAccountScreen));
		stage.setTitle("Create Account");
	}
	
	// Switch to the Forgot Password screen if "Forgot Password" button is pressed
	@FXML
	private void forgotPassword() throws Exception {
		Stage stage;
		Parent forgotPasswordScreen;
		
		stage = (Stage) forgotPasswordButton.getScene().getWindow();
		forgotPasswordScreen = FXMLLoader.load(getClass().getResource("ForgotPasswordScreen.fxml"));
		
		stage.setScene(new Scene(forgotPasswordScreen));
		stage.setTitle("Forgot Password");
	}
}
