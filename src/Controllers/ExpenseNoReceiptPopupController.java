import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Base64;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;


public class ExpenseNoReceiptPopupController {
	@FXML private TextField totalInput;
	@FXML private ComboBox<String> categoryInput;
	@FXML private DatePicker dateInput;
	@FXML private Button submitButton, uploadButton;
	@FXML private ImageView imagePreview;
	@FXML private Label errorMessage;
	
	private String trueTotal;
	private String base64Image = null;

	@FXML
	private void initialize() {
		
		//Restrict TextField input to numbers only
		DecimalFormat format = new DecimalFormat("#.00");
		totalInput.setTextFormatter(new TextFormatter<>(c -> {
		    if (c.getControlNewText().isEmpty()) {
		        return c;
		    }
		    ParsePosition parsePosition = new ParsePosition(0);
		    Object object = format.parse(c.getControlNewText(), parsePosition);
		    if (object == null || parsePosition.getIndex() < c.getControlNewText().length()) {
		        return null;
		    } else {
		        return c;
		    }
		}));
		
		// Initialize all category names from database into the ComboBox
		String getCategories = "SELECT DISTINCT Category FROM Expenses WHERE PlanCode = '" + LoginScreenController.getSessionPlanCode() + "'";
		try (Connection conn = SQLDatabaseConnection.getConnection();
			 Statement st = conn.createStatement();
			 ResultSet rs = st.executeQuery(getCategories);) {
			while (rs.next()) {
				categoryInput.getItems().add(rs.getString(1));
			}
			categoryInput.getItems().add("Add new...");   // Allows user to add a new category
		} catch (SQLException e) {
			System.out.println("Failed to retrieve categories.");
			e.printStackTrace();
		}
		
	}
	
	// Manage the "Add new..." ComboBox functionality
	@FXML
	private void checkCategoryInput() {
		
		if (categoryInput.getValue().equals("Add new...")) {
			categoryInput.setEditable(true);
		} else {
			categoryInput.setEditable(false);
		}
		
	}
	
	// Manage the Receipt input, preview, and conversion to Base64
	@FXML
	public void enterReceipt() {
		
		Stage stage = (Stage) uploadButton.getScene().getWindow();
		FileChooser fc = new FileChooser();
		fc.setTitle("Choose Image");
		fc.getExtensionFilters().addAll(new ExtensionFilter("Image Files", "*.png", "*.jpg", "*.tif", "*.tiff", "*.gif"));   // Limit the file types that can be chosen
		
		// Open the File Picker, convert file to image, then display the preview
		File file = fc.showOpenDialog(stage);
		Image image;
		if (file != null) {
			image = new Image(file.toURI().toString());
			imagePreview.setImage(image);
			
			// Encode the image to Base64 string
			try {
				byte[] imageBytes = Base64.getEncoder().encode(Files.readAllBytes(file.toPath()));
				base64Image = new String(imageBytes);
			} catch (Exception e) {
				System.out.println("Error converting image to Base64 string.");
				e.printStackTrace();
			}
		}
		
	}
	
	@FXML
	private void checkInputs() {
		
		errorMessage.setText("");
		
		// Check if total is a valid number
		boolean validTotal = false;
		DecimalFormat format = new DecimalFormat("#.00");
		if (!totalInput.getText().isEmpty()) {
			String tempTotal = format.format(Float.valueOf(totalInput.getText()));   // Cut off numbers past two leading zeros
			
			if (Float.valueOf(tempTotal) > 0) {
				trueTotal = tempTotal;
				validTotal = true;
			} else {
				errorMessage.setText(errorMessage.getText() + "Total must be greater than 0 and in correct format!\n");
			}
		} else {
			errorMessage.setText(errorMessage.getText() + "Please enter a total.\n");
		}
		
		// Check if date is after today
		boolean validDate = false;
		if (dateInput.getValue() == null)
			errorMessage.setText(errorMessage.getText() + "Please enter a date.\n");
		else if (dateInput.getValue().isAfter(LocalDate.now())) {
			errorMessage.setText(errorMessage.getText() + "Date entered is in the future!\n");
		} else {
			validDate = true;
		}
		
		// Check if category has been selected
		boolean validCategory = false;
		if (!categoryInput.isEditable()) {   // If the ComboBox is editable, that means the user entered a new category.
			if (categoryInput.getValue() == null) {
				errorMessage.setText(errorMessage.getText() + "Please apply a category.\n");
			} else {
				validCategory = true;
			}
		} else {
			if (!categoryInput.getEditor().getText().isEmpty()) {
				if (categoryInput.getEditor().getText().trim().length() <= 20) {   // Check if custom category entered is too long
					validCategory = true;
				} else {
					errorMessage.setText(errorMessage.getText() + "Category name is too long.\n");
				}
			} else {
				errorMessage.setText(errorMessage.getText() + "Please enter a category.\n");
			}
		}
		
		if (validTotal && validDate && validCategory) {
			enterExpense();
		}
	
	}
	
	// Enters the expense into the database
	@FXML
	private void enterExpense() {
		
		// Get the user's plan code; required for the "Expenses" database
		String retrievedPlanCode = LoginScreenController.getSessionPlanCode();
		
		// Insert the new expense into the database.
		Date currentDate = Date.valueOf(LocalDate.now());   // Get the current date (as of when the button was pressed)
		DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		
		String insertExpense = "INSERT INTO Expenses (ExpenseID, PlanCode, Amount, Date, Category, DateEntered, Image) VALUES (?, ?, ?, ?, ?, ?, ?)";
		try (Connection conn = SQLDatabaseConnection.getConnection();
			 PreparedStatement ps = conn.prepareStatement(insertExpense)) {
			String expenseID = generateExpenseID(retrievedPlanCode);
			
			ps.setString(1, expenseID);
			ps.setString(2, retrievedPlanCode);
			ps.setBigDecimal(3, new BigDecimal(trueTotal));   // Resource: https://docs.oracle.com/javase/tutorial/java/data/converting.html
			ps.setDate(4, Date.valueOf(dateInput.getValue()));   // Check for correct date format
			ps.setDate(6, currentDate);
			ps.setString(7, base64Image);
			
			// If the ComboBox is editable, that means the user entered a new category. 
			if (categoryInput.getEditor() == null) {
				ps.setString(5, categoryInput.getValue());
			} else {
				ps.setString(5, categoryInput.getEditor().getText().trim());
			}
			
			ps.executeUpdate();
			goBack();
		} catch (SQLException e) {
			System.out.println("Failed to insert the new expense.");
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("Failed to close the popup.");
			e.printStackTrace();
		}	
		
	}
	
	// Close the screen upon successful completion
	@FXML
	private void goBack() throws IOException {
		
		Stage stage = (Stage) uploadButton.getScene().getWindow();
		stage.close();
		
	}
	
	// Generate a new ExpenseID
	private String generateExpenseID(String planCode) {
		
		String expenseID = null;
		
		String getTopExpenseID = "SELECT ExpenseID FROM Expenses ORDER BY ExpenseID DESC";
		try (Connection conn = SQLDatabaseConnection.getConnection();
			 Statement st = conn.createStatement();
			 ResultSet rs = st.executeQuery(getTopExpenseID);) {
			
			if (!rs.isBeforeFirst()) {
				expenseID = "00001";
			} else if (rs.next()) {
				String oldID = rs.getString(1);
				expenseID = String.format("%05d",   // Zero-padding with a length of 5
						(Integer.parseInt(oldID) + 1));   // Increment previous ExpenseID by 1 
			}
			
		} catch (SQLException e) {
			System.out.println("Failed to generate ExpenseID.");
			e.printStackTrace();
		}
		return expenseID;
		
	}
}
