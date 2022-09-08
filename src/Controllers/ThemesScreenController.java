import java.io.IOException;

import com.sun.javafx.css.StyleManager;

import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class ThemesScreenController {
	
	@FXML private AnchorPane pane;
	@FXML private Button backButton, seaWavesButton, defaultButton;
	
	@FXML
	private void initialize() {
		pane.requestFocus();
	}
	
	@FXML
	private void setDefault() {
		Application.setUserAgentStylesheet(Application.STYLESHEET_MODENA);
	}
	
	@FXML
	private void setBlue() {
		Application.setUserAgentStylesheet(Application.STYLESHEET_MODENA);
		StyleManager.getInstance().addUserAgentStylesheet(getClass().getResource("SeaWavesTheme.css").toString());
	}
	
	@FXML
	private void goBack() throws IOException {
		Stage stage;
		Parent settingScreen;
		
		stage = (Stage) backButton.getScene().getWindow();
		settingScreen = FXMLLoader.load(getClass().getResource("SettingsScreen.fxml"));
		
		stage.setScene(new Scene(settingScreen));
		stage.setTitle("Settings");
	}
}

/* THEMES
 * Red: #FFEBEE and #EF5350
 * Blue-green: #E0F2F1 and #A7FFEB
 */
