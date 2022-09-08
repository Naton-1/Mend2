import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {
	
	// Load the initial screen and display it
	@Override
	public void start(Stage primaryStage) throws Exception {
		Parent loginScreen = FXMLLoader.load(getClass().getResource("LoginScreen.fxml"));
		primaryStage.setTitle("Login Screen");
		primaryStage.setScene(new Scene(loginScreen));
		primaryStage.show();
	}
	
	// Start the application
	public static void main(String[] args) {	
		launch(args);
	}
	
}