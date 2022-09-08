import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class ExpenseMainScreenController {
	@FXML private Button backButton, addExpense, expenseAnalysis, manageExpenses;
	
	@FXML
	private void goToAddExpense() throws Exception {		
		Stage popupStage = new Stage();
		FXMLLoader loader = new FXMLLoader(getClass().getResource("ExpenseNoReceiptPopup.fxml"));
		popupStage.initModality(Modality.APPLICATION_MODAL);
		popupStage.setTitle("Enter Expense");
		popupStage.setScene(new Scene(loader.load()));
		popupStage.showAndWait();
	}
	
	@FXML
	private void goToExpenseAnalysis() throws Exception {
		Stage stage;
		Parent expenseAnalysisScreen;
		
		stage = (Stage) expenseAnalysis.getScene().getWindow();
		expenseAnalysisScreen = FXMLLoader.load(getClass().getResource("ExpenseAnalysisScreen.fxml"));
		
		stage.setScene(new Scene(expenseAnalysisScreen));
		stage.setTitle("Analyze Expenses");
	}
	
	@FXML
	private void goToManageExpenses() throws Exception { 
		Stage stage;
		Parent expenseManageScreen;
		
		stage = (Stage) manageExpenses.getScene().getWindow();
		expenseManageScreen = FXMLLoader.load(getClass().getResource("ExpenseManageScreen.fxml"));
		
		stage.setScene(new Scene(expenseManageScreen));
		stage.setTitle("Manage Expenses");
	}
	
	@FXML
	private void goBack() throws Exception {
		Stage stage;
		Parent mainMenuScreen;
		
		stage = (Stage) backButton.getScene().getWindow();
		mainMenuScreen = FXMLLoader.load(getClass().getResource("MainMenuScreen.fxml"));
		
		stage.setScene(new Scene(mainMenuScreen));
		stage.setTitle("Main Menu");
	}
}
