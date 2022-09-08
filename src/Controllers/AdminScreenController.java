import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;


public class AdminScreenController {
	
	@FXML private Button backButton, planButton, emailButton, fNameButton, lNameButton, deleteButton;
	
	/*
	 * BEGIN TableView code
	 */
	
	@FXML private TableView<User> table;
	@FXML private TableColumn<User, String> fNameCol, lNameCol, usernameCol, emailCol, planCol;
	private ObservableList<User> list;
	
	public class User {
		private String fName, lName, username, email, plan;
		
		public String getFName() {
			return fName;
		}
		public void setFName(String fname) {
			this.fName = fname;
		}
		public String getLName() {
			return lName;
		}
		public void setLName(String lname) {
			this.lName = lname;
		}
		public String getUsername() {
			return username;
		}
		public void setUsername(String username) {
			this.username = username;
		}
		public String getEmail() {
			return email;
		}
		public void setEmail(String email) {
			this.email = email;
		}
		public String getPlan() {
			return plan;
		}
		public void setPlan(String code) {
			this.plan = code;
		}
	}
	
	@FXML
	private void initialize() {
		fNameCol.setCellValueFactory(new PropertyValueFactory<User, String>("fName"));
		lNameCol.setCellValueFactory(new PropertyValueFactory<User, String>("lName"));
		usernameCol.setCellValueFactory(new PropertyValueFactory<User, String>("username"));
		emailCol.setCellValueFactory(new PropertyValueFactory<User, String>("email"));
		planCol.setCellValueFactory(new PropertyValueFactory<User, String>("plan"));
		
		getData();
	}
	
	@FXML
	private void clearTable() {
		table.getItems().clear();
	}
	
	@FXML
	private void getData() {
		list = FXCollections.observableArrayList();
		
		String select = "SELECT FirstName, LastName, Username, Email, PlanCode FROM Users";
		try (Connection conn = SQLDatabaseConnection.getConnection();
			 Statement st = conn.createStatement();
			 ResultSet rs = st.executeQuery(select);) {
			while(rs.next()) {
				User rowEntry = new User();
				rowEntry.setFName(rs.getString("FirstName"));
				rowEntry.setLName(rs.getString("LastName"));
				rowEntry.setUsername(rs.getString("Username"));
				rowEntry.setEmail(rs.getString("Email"));
				rowEntry.setPlan(rs.getString("PlanCode"));
				list.add(rowEntry);
			}
			table.setItems(list);
			} catch (SQLException ex) {
				System.out.println("Error getting the data from database.");
				ex.printStackTrace();
			}
	}
	
	/*
	 * END TableView code 
	 */

	@FXML
	private void changePlanCode() throws IOException {
		Stage popupStage = new Stage();
		FXMLLoader loader = new FXMLLoader(getClass().getResource("Popup.fxml"));
		popupStage.initModality(Modality.APPLICATION_MODAL);
		popupStage.setTitle("Info");
		popupStage.setScene(new Scene(loader.load()));
		CustomPopupController controller = loader.<CustomPopupController>getController();
		controller.setTopMessage("Enter the email of the user who you want to modify.");
		controller.setBottomMessage("Enter the new plan code.");
		popupStage.showAndWait();
		
		String returnedEmail = controller.getTopInput();
		String newPlanCode = controller.getBottomInput();
		String update = "UPDATE Users SET PlanCode = '" + newPlanCode + "' WHERE Email = '" + returnedEmail + "'";
		
		try (Connection conn = SQLDatabaseConnection.getConnection();
			 Statement st = conn.createStatement();) {
			int rowsAffected = st.executeUpdate(update);
			System.out.println(rowsAffected + " rows affected.");
			clearTable();
			getData();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	@FXML
	private void changeEmail() throws IOException {
		Stage popupStage = new Stage();
		FXMLLoader loader = new FXMLLoader(getClass().getResource("Popup.fxml"));
		popupStage.initModality(Modality.APPLICATION_MODAL);
		popupStage.setTitle("Info");
		popupStage.setScene(new Scene(loader.load()));
		CustomPopupController controller = loader.<CustomPopupController>getController();
		controller.setTopMessage("Enter the email of the user who you want to modify.");
		controller.setBottomMessage("Enter the new email.");
		popupStage.showAndWait();
		
		String returnedEmail = controller.getTopInput();
		String newEmail = controller.getBottomInput();
		String update = "UPDATE Users SET Email = '" + newEmail + "' WHERE Email = '" + returnedEmail + "'";
		
		try (Connection conn = SQLDatabaseConnection.getConnection();
			 Statement st = conn.createStatement();) {
			int rowsAffected = st.executeUpdate(update);
			System.out.println(rowsAffected + " rows affected.");
			clearTable();
			getData();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	@FXML
	private void changeFName() throws IOException {
		Stage popupStage = new Stage();
		FXMLLoader loader = new FXMLLoader(getClass().getResource("Popup.fxml"));
		popupStage.initModality(Modality.APPLICATION_MODAL);
		popupStage.setTitle("Info");
		popupStage.setScene(new Scene(loader.load()));
		CustomPopupController controller = loader.<CustomPopupController>getController();
		controller.setTopMessage("Enter the email of the user who you want to modify.");
		controller.setBottomMessage("Enter the new name.");
		popupStage.showAndWait();
		
		String returnedEmail = controller.getTopInput();
		String newFName = controller.getBottomInput();
		String update = "UPDATE Users SET FirstName = '" + newFName + "' WHERE Email = '" + returnedEmail + "'";
		
		try (Connection conn = SQLDatabaseConnection.getConnection();
			 Statement st = conn.createStatement();) {
			int rowsAffected = st.executeUpdate(update);
			System.out.println(rowsAffected + " rows affected.");
			clearTable();
			getData();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	@FXML
	private void changeLName() throws IOException {
		Stage popupStage = new Stage();
		FXMLLoader loader = new FXMLLoader(getClass().getResource("Popup.fxml"));
		popupStage.initModality(Modality.APPLICATION_MODAL);
		popupStage.setTitle("Info");
		popupStage.setScene(new Scene(loader.load()));
		CustomPopupController controller = loader.<CustomPopupController>getController();
		controller.setTopMessage("Enter the email of the user who you want to modify.");
		controller.setBottomMessage("Enter the new name.");
		popupStage.showAndWait();
		
		String returnedEmail = controller.getTopInput();
		String newLName = controller.getBottomInput();
		String update = "UPDATE Users SET LastName = '" + newLName + "' WHERE Email = '" + returnedEmail + "'";
		
		try (Connection conn = SQLDatabaseConnection.getConnection();
			 Statement st = conn.createStatement();) {
			int rowsAffected = st.executeUpdate(update);
			System.out.println(rowsAffected + " rows affected.");
			clearTable();
			getData();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	@FXML
	private void deleteAccount() throws IOException {
		Stage popupStage = new Stage();
		FXMLLoader loader = new FXMLLoader(getClass().getResource("Popup.fxml"));
		popupStage.initModality(Modality.APPLICATION_MODAL);
		popupStage.setTitle("Info");
		popupStage.setScene(new Scene(loader.load()));
		CustomPopupController controller = loader.<CustomPopupController>getController();
		controller.hideHalfOfNodes();
		controller.setTopMessage("Enter the email of the user who you want to modify.");
		popupStage.showAndWait();
		
		String returnedEmail = controller.getTopInput();
		String delete = "DELETE FROM Users WHERE Email = '" + returnedEmail + "'";
		
		Alert warning = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you want to delete this account?");
		warning.showAndWait().ifPresent(response -> {
		     if (response == ButtonType.OK) {
		    	 try (Connection conn = SQLDatabaseConnection.getConnection();
		    			 Statement st = conn.createStatement();) {
		    			int rowsAffected = st.executeUpdate(delete);
		    			System.out.println(rowsAffected + " rows deleted.");
		    			clearTable();
		    			getData();
		    		} catch (SQLException e) {
		    			e.printStackTrace();
		    		}
		     }
		 });
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
