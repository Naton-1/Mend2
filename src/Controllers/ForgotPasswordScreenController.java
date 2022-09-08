
import java.io.UnsupportedEncodingException;
import java.sql.*;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import javafx.event.ActionEvent;
import javafx.fxml.*;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class ForgotPasswordScreenController {

	@FXML private Button confirmButton, backButton;
	@FXML private TextField email;
	@FXML private Label message;
	
	@FXML
	private void initialize() {
		message.setText("Enter your email.");
		message.setTextFill(Color.web("#000000"));
	}
	
	// Check for valid email against database and take correct course of action based on validation results
	@FXML
	private void sendPassword(ActionEvent event) {
		
		try {
			
			String userEmail = email.getText();
			
			InternetAddress testEmail = new InternetAddress(userEmail);
			testEmail.validate();   // Check email input syntax
			
			String checkEmail = "SELECT Email FROM Users WHERE Email = '" + userEmail + "'";
			try (Connection conn = SQLDatabaseConnection.getConnection();
				 Statement st = conn.createStatement();
				 ResultSet rs = st.executeQuery(checkEmail);) {
				if (rs.isBeforeFirst()) {	// If the ResultSet returned values, then that email exists in the database
					message.setTextFill(Color.web("#40da2b"));
					message.setText("An account has been found with that email. Please check your email inbox and/or spam folder.");
					
					String getPassword = "SELECT Password FROM Users WHERE Email = '" + userEmail + "'";
					ResultSet rs2 = st.executeQuery(getPassword);
					rs2.next();
					String retrievedPassword = rs2.getString(1);
					
					try {
						sendEmail(userEmail, retrievedPassword);
					} catch (Exception e) {
						e.printStackTrace();
					}
					
				}
				else {
					message.setTextFill(Color.web("#ff0000"));
					message.setText("No accounts were found with that email. Try again, or create a new account.");
				}
			}
			catch (SQLException e) {
				System.out.println("Failed to retrieve email from SQL server.");   // Print error message to console
				e.printStackTrace();
			}
		}
		catch (AddressException e) {
			message.setTextFill(Color.web("#ff0000"));
			message.setText("Invalid email syntax.");
		}
		
	}
	
	// Send the Forgot Password email to the user
	public void sendEmail(String email, String password) throws AddressException, MessagingException {
		
		Properties mailServerProperties;
		mailServerProperties = System.getProperties();
		mailServerProperties.put("mail.smtp.port", "587");
		mailServerProperties.put("mail.smtp.auth", "true");
		mailServerProperties.put("mail.smtp.starttls.enable", "true");
		
		Session getMailSession;
		MimeMessage emailMessage;
		getMailSession = Session.getDefaultInstance(mailServerProperties, null);
		emailMessage = new MimeMessage(getMailSession);
		try {
			emailMessage.setFrom(new InternetAddress("[REDACTED]", "Mend2 App"));
		} catch (UnsupportedEncodingException e) {
			System.out.println("Error trying to set the name header of the email.");
			e.printStackTrace();
		}
		emailMessage.addRecipient(Message.RecipientType.TO, new InternetAddress(email));
		emailMessage.setSubject("Your Mend2 Password");
		String emailBody = "Greetings from the Mend2 Java app.<br /><br />"
						 + "Your password is:<br /><br />"
						 + "<div style=\"font-size:20px;\"><strong>" + password + "</strong></div>";
		emailMessage.setContent(emailBody, "text/html");
		
		Transport transport = getMailSession.getTransport("smtp");
		transport.connect("smtp.gmail.com", "[REDACTED]", "[REDACTED]");
		transport.sendMessage(emailMessage, emailMessage.getAllRecipients());
		transport.close();
		
		System.out.println("Email sent successfully!");
		
	}
	
	// Go to the previous screen
	@FXML
	private void goBack(ActionEvent event) throws Exception {
		Stage stage;
		Parent loginScreen;
		
		stage = (Stage) backButton.getScene().getWindow();
		loginScreen = FXMLLoader.load(getClass().getResource("LoginScreen.fxml"));
		
		stage.setScene(new Scene(loginScreen));
		stage.setTitle("Login Screen");
	}
}
