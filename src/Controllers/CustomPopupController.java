import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class CustomPopupController {
	
	@FXML private Label topMessage, bottomMessage;
	@FXML private TextField topInput, bottomInput;
	@FXML private Button close;
	
	
	@FXML
	private void close() {
		Stage stage = (Stage) close.getScene().getWindow();
	    stage.close();
	}

	@FXML
	public void hideHalfOfNodes() {
		bottomMessage.setVisible(false);
		bottomMessage.setManaged(false);
		bottomInput.setVisible(false);
		bottomInput.setManaged(false);
	}
	
	@FXML
	public void setTopMessage(String input) {
		topMessage.setText(input);
	}
	
	@FXML
	public void setBottomMessage(String input) {
		bottomMessage.setText(input);
	}
	
	@FXML
	public String getTopInput() {
		return topInput.getText();
	}
	
	@FXML 
	public String getBottomInput() {
		return bottomInput.getText();
	}
	
}
