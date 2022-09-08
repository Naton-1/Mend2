import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Locale;
import java.util.Optional;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;


public class ExpenseManageController {
	
	@FXML private Button backButton, deleteExpenseButton, editExpenseButton;
	
	/*
	 * BEGIN TableView code
	 */
	
	@FXML private TableView<Expense> table;
	@FXML private TableColumn<Expense, String> expenseIdCol, amountCol, dateCol, categoryCol, dateEnteredCol;   // Possible to change some to Date columns
	private ObservableList<Expense> list;
	
	public class Expense {
		private String expenseID, amount, date, category, dateEntered;
		
		public String getExpenseID() {
			return expenseID;
		}
		public void setExpenseID(String id) {
			this.expenseID = id;
		}
		public String getAmount() {
			return amount;
		}
		public void setAmount(String bigDecimal) {
			this.amount = bigDecimal;
		}
		public String getDate() {
			return date;
		}
		public void setDate(String input) {
			this.date = input;
		}
		public String getCategory() {
			return category;
		}
		public void setCategory(String input) {
			this.category = input;
		}
		public String getDateEntered() {
			return dateEntered;
		}
		public void setDateEntered(String input) {
			this.dateEntered = input;
		}
	}
	
	@FXML
	private void initialize() {
		expenseIdCol.setCellValueFactory(new PropertyValueFactory<Expense, String>("expenseID"));
		amountCol.setCellValueFactory(new PropertyValueFactory<Expense, String>("amount"));
		dateCol.setCellValueFactory(new PropertyValueFactory<Expense, String>("date"));
		categoryCol.setCellValueFactory(new PropertyValueFactory<Expense, String>("category"));
		dateEnteredCol.setCellValueFactory(new PropertyValueFactory<Expense, String>("dateEntered"));
		
		getData();
	}
	
	@FXML
	private void clearTable() {
		table.getItems().clear();
	}
	
	@FXML
	private void getData() {
		
		list = FXCollections.observableArrayList();
		
		String retrievedPlanCode = LoginScreenController.getSessionPlanCode();
		
		DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
		String select = "SELECT ExpenseID, Amount, Date, Category, DateEntered FROM Expenses WHERE PlanCode = '" + retrievedPlanCode + "'";
		try (Connection conn = SQLDatabaseConnection.getConnection();
			 Statement st = conn.createStatement();
			 ResultSet rs = st.executeQuery(select);) {
			
			while (rs.next()) {
				Expense rowEntry = new Expense();
				rowEntry.setExpenseID(rs.getString("ExpenseID"));
				rowEntry.setAmount(NumberFormat.getCurrencyInstance().format(rs.getBigDecimal("Amount")));   // Format BigDecimal value to 2 decimal places
				rowEntry.setDate(formatter.format(rs.getDate("Date")));
				rowEntry.setCategory(rs.getString("Category"));
				rowEntry.setDateEntered(formatter.format(rs.getDate("DateEntered")));
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
	
	// Deletes an Expense record
	@FXML
	private void deleteExpense() throws IOException {
		
		Stage popupStage = new Stage();
		FXMLLoader loader = new FXMLLoader(getClass().getResource("Popup.fxml"));
		popupStage.initModality(Modality.APPLICATION_MODAL);
		popupStage.setTitle("Info");
		popupStage.setScene(new Scene(loader.load()));
		CustomPopupController controller = loader.<CustomPopupController>getController();
		controller.setTopMessage("Enter the ExpenseID of the entry you want to delete.");
		controller.hideHalfOfNodes();
		popupStage.showAndWait();
		
		String returnedExpenseID = controller.getTopInput();
		String delete = "DELETE FROM Expenses WHERE ExpenseID = '" + returnedExpenseID + "'";
		
		try (Connection conn = SQLDatabaseConnection.getConnection();
			 Statement st = conn.createStatement();) {
			int rowsAffected = st.executeUpdate(delete);
			System.out.println(rowsAffected + " rows affected.");
			clearTable();
			getData();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
	}
	
	// Edits an Expense record
	@FXML
	private void editExpense() throws IOException {
		
		String sql = null;
		
		// Make the initial popup
		TextInputDialog edit1 = new TextInputDialog();
		edit1.setTitle("Edit Expense");
		edit1.setHeaderText("Enter the ExpenseID of the entry you want to edit.");
		edit1.setContentText("ExpenseID:");
		
		// Create a generic error popup
		Alert genericErrorAlert = new Alert(AlertType.ERROR);
		genericErrorAlert.setTitle("Oops!");
		
		// Display the initial popup
		Optional<String> edit1Result = edit1.showAndWait();
		if (edit1Result.isPresent()) {
			String returnedExpenseID = edit1Result.get();
			boolean foundExpense = false;
			
			// Look for ExpenseID in the table rows
			for (Expense e : list) {
				if (e.getExpenseID().equals(returnedExpenseID)) {
					foundExpense = true;
				}
			}
			
			// If a table row with that ExpenseID is found, proceed
			if (foundExpense) {
				ChoiceDialog<String> edit2 = new ChoiceDialog<>("Amount", "Amount", "Date entered", "Category");
				edit2.setTitle("Edit Expense");
				edit2.setHeaderText("What do you want to change?");
				
				Optional<String> edit2Result = edit2.showAndWait();
				if (edit2Result.isPresent()) {   // Check to see if user cancelled dialog
					TextInputDialog genericInput = new TextInputDialog();
					genericInput.setTitle("Edit Expense");
					
					// User wants to change Amount
					if (edit2Result.get().equals("Amount")) {
						genericInput.setHeaderText("Enter the new amount.");
						genericInput.setContentText("Digits beyond 2 decimal places will be cut off.");
						boolean correctAmount = false;
						
						do {
							Optional<String> genericResult = genericInput.showAndWait();
							if (genericResult.isPresent()) {
								
								// Check if total is a valid number
								try {
									DecimalFormat format = new DecimalFormat("#.00");
									String tempTotal = format.format(Float.valueOf(genericResult.get()));  // Cut off numbers past two leading zeros
									
									// Check if value is greater than 0
									if (Float.valueOf(tempTotal) > 0) {
										correctAmount = true;
										sql = "UPDATE Expenses SET Amount = " + tempTotal + " WHERE ExpenseID = '" + returnedExpenseID + "'";
									} else {
										genericErrorAlert.setHeaderText("Enter a value greater than 0!");
										genericErrorAlert.showAndWait();
									}
								} 
								// Exception is thrown if user enters a non-numeric char
								catch (NumberFormatException e) {
									genericErrorAlert.setHeaderText("Enter numbers only!");
									genericErrorAlert.showAndWait();
								}
							}
							// If user presses cancel button, end the loop
							else {
								correctAmount = true;
							}
						} while (!correctAmount);
					}
					
					// User wants to change Date Entered
					else if (edit2Result.get().equals("Date entered")) {
						genericInput.setHeaderText("Enter the new date in yyyy-mm-dd format.");
						boolean correctDate = false;
						
						do {
							Optional<String> genericResult = genericInput.showAndWait();
							if (genericResult.isPresent()) {
								DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd");
								LocalDate formattedDate = null;
								
								if (!genericResult.get().isEmpty()) {
									try {
										formattedDate = LocalDate.parse(genericResult.get(), format);
										
										// Check for future dates, show error popup if so
										if (!formattedDate.isAfter(LocalDate.now())) {
											correctDate = true;
											sql = "UPDATE Expenses SET DateEntered = '" + formattedDate + "' WHERE ExpenseID = '" + returnedExpenseID + "'";
										} else {
											genericErrorAlert.setHeaderText("The date entered is in the future!");
											genericErrorAlert.showAndWait();
										}
									} 
									// Exception is thrown if user inputs dumb date
									catch (DateTimeParseException e) {
										genericErrorAlert.setHeaderText("The date entered is not in the correct format, or is an incorrect date.");
										genericErrorAlert.showAndWait();
									}
								} else {
									genericErrorAlert.setHeaderText("Input *something*!.");
									genericErrorAlert.showAndWait();
								}
							} 
							// If user presses cancel button, end the loop
							else {
								correctDate = true;
							}
						} while (!correctDate);
					} 
					
					// User wants to change Category
					else {
						String categories = "";
						
						// Retrieve categories from database
						String pullCategories = "SELECT DISTINCT Category FROM Expenses WHERE PlanCode = '" + LoginScreenController.getSessionPlanCode() + "'";
						try (Connection conn = SQLDatabaseConnection.getConnection();
							 Statement st = conn.createStatement();
							 ResultSet rs = st.executeQuery(pullCategories);) {
							int counter = 0;
							
							while (rs.next()) {
								categories += rs.getString(1) + ", ";
								counter++;
								
								// Every 4 pulled categories, insert a new line
								if (counter % 4 == 0) {
									categories += "\n";
								}
							}
							
						} catch (SQLException e) {
							System.out.println("Failed to retrieve categories.");
							e.printStackTrace();
						}
						
						genericInput.setHeaderText("Choose from an existing category, or enter a new one.");
						genericInput.setContentText("Options: " + categories + "\n or enter your own:");
						boolean validCategory = false;
						
						do {
							Optional<String> genericResult = genericInput.showAndWait();
							if (genericResult.isPresent()) {
								if (!genericResult.get().isEmpty()) {
									if (!Character.isWhitespace(genericResult.get().charAt(0))) {   // Check if user inputed whitespace only
										validCategory = true;
										sql = "UPDATE Expenses SET Category = '" + genericResult.get() + "' WHERE ExpenseID = '" + returnedExpenseID + "'";
									} 
									// User entered just whitespace
									else {
										genericErrorAlert.setHeaderText("Input something besides spaces!");
										genericErrorAlert.showAndWait();
									}
								} 
								// User entered nothing
								else {
									genericErrorAlert.setHeaderText("Input *something*!");
									genericErrorAlert.showAndWait();
								}
							}
							// If user presses cancel button, end the loop
							else {
								validCategory = true;
							}
						} while (!validCategory);
					}
				}
			}
			// If a table row with that ExpenseID is not found, display error popup
			else {
				genericErrorAlert.setHeaderText("There is no entry with that ExpenseID.");
				genericErrorAlert.showAndWait();
			}
		}
		
		if (sql != null) {
			// Execute the SQL statement, which depends on what the user chose
			try (Connection conn = SQLDatabaseConnection.getConnection();
				 Statement st = conn.createStatement();) {
				int rowsAffected = st.executeUpdate(sql);
				System.out.println(rowsAffected + " rows affected.");
				clearTable();
				getData();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
	}
	
	// Go to the previous screen
	@FXML
	private void goBack() throws IOException {
		Stage stage;
		Parent expenseMainScreen;
		
		stage = (Stage) backButton.getScene().getWindow();
		expenseMainScreen = FXMLLoader.load(getClass().getResource("ExpenseMainScreen.fxml"));
		
		stage.setScene(new Scene(expenseMainScreen));
		stage.setTitle("Expenses");
	}
}
