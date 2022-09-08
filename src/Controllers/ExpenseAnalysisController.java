import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.util.ArrayList;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Tooltip;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.FileChooser.ExtensionFilter;


public class ExpenseAnalysisController {
	
	@FXML private Button backButton, exportButton;
	@FXML private BarChart<String, BigDecimal> catBarChart;
	@FXML private PieChart catPieChart;
	
	
	@FXML
	private void initialize() {
		populateCategoryBarChart();
		populateCategoryPieChart();
	}
	
	// CategoryData object used for calculating totals from SQL retrieved Expense data
	public class CategoryData {
		private BigDecimal total;
		private String category;
		
		public CategoryData() {
			System.out.println("A blank CategoryData was constructed!");
		}
		
		public CategoryData(BigDecimal num, String str) {
			total = num;
			category = str;
		}
		
		public void addAmount(BigDecimal input) {
			total = total.add(input);
		}
		
		public BigDecimal getTotal() {
			return total;
		}

		public String getCategory() {
			return category;
		}
	}
	
	
	private ArrayList<CategoryData> calculateCategoryTotals() {
		
		String planCode = LoginScreenController.getSessionPlanCode();
		
		String pullExpenses = "SELECT Amount, Category FROM Expenses WHERE PlanCode = '" + planCode + "' ORDER BY Category DESC";
		String getUniqueCategories = "SELECT DISTINCT Category FROM Expenses WHERE PlanCode = '" + planCode + "' ORDER BY Category DESC";
		
		try (Connection conn = SQLDatabaseConnection.getConnection();
			 Statement expST = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
			 Statement catST = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
			 ResultSet expRS = expST.executeQuery(pullExpenses);
			 ResultSet catRS = catST.executeQuery(getUniqueCategories)) {
			
			// Put all categories into an ArrayList
			ArrayList<CategoryData> catArrList = new ArrayList<CategoryData>();
			while (catRS.next()) {
				catArrList.add(new CategoryData(new BigDecimal(0), catRS.getString(1)));
			}
			
			// Find each category's total by summation
			while (expRS.next()) {
				String rowCategory = expRS.getString(2);
				BigDecimal rowAmount = expRS.getBigDecimal(1);
				
				for (CategoryData c : catArrList) {
					if (rowCategory.equals(c.getCategory())) {
						c.addAmount(rowAmount);
					}
				}
			}
			
			return catArrList;
			
		} catch (SQLException e) {
			System.out.println("Failed to retrieve expense data for bar chart.");
			e.printStackTrace();
		}
		
		return null;
		
	}
	
	// Populates the Bar Chart with data
	@FXML
	private void populateCategoryBarChart() {
		
		ArrayList<CategoryData> catArrList = calculateCategoryTotals();
		
		// Create a bar chart "series"
		XYChart.Series<String, BigDecimal> expenseSeries = new XYChart.Series<>();	
		
		// Add each category's total to the data series
		for (CategoryData c : catArrList) {
			String currentCategory = c.getCategory();
			BigDecimal currentTotal = c.getTotal();
			
			expenseSeries.getData().add(new XYChart.Data<>(currentCategory, currentTotal));
		}
		
		// Add the series (data) to the chart
		catBarChart.getData().addAll(expenseSeries);
		
		/******************
		 * DEPRECATED CODE
		 *****************/
		
		/* PLAN
		 * Add series at the end
		 * Parse through each row 
		 * If category matches a category in 
		 */
		
		/*
		XYChart.Series expenseSeries = new XYChart.Series();
			
		expRS.next();
		String currentCategory = expRS.getString("Category");
		BigDecimal tempTotal = new BigDecimal(0);
		do {
			String newCat = expRS.getString("Category");
			if (!(newCat.equals(currentCategory))) {
				expenseSeries.getData().add(new XYChart.Data(currentCategory, tempTotal));
				currentCategory = newCat;
				tempTotal = 0 + expRS.getBigDecimal("Amount");
			} else {
				tempTotal += expRS.getBigDecimal("Amount");
			}
		} while (expRS.next());
		catBarChart.getData().addAll(expenseSeries);
		*/
		
	}
	
	// Populates the Pie Chart with data
	@FXML
	private void populateCategoryPieChart() {
		
		ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();
		
		ArrayList<CategoryData> catArrList = calculateCategoryTotals();
		BigDecimal total = new BigDecimal(0);
		
		// Add each category's total to the overall data
		for (CategoryData c : catArrList) {
			String currentCategory = c.getCategory();
			BigDecimal currentTotal = c.getTotal();
					
			total = total.add(currentTotal);
			pieChartData.add(new PieChart.Data(currentCategory, currentTotal.longValue()));
		}
		
		// Set data to the Pie Chart
		catPieChart.setData(pieChartData);
		
		// Display tooltip with %
		long l = total.longValue();
		catPieChart.getData().stream().forEach(data -> {
		    Tooltip tooltip = new Tooltip();
		    DecimalFormat format = new DecimalFormat("#.##");
		    String formattedNum = format.format((data.getPieValue() / l) * 100);
		    tooltip.setText(formattedNum + "%");
		    Tooltip.install(data.getNode(), tooltip);
		    data.pieValueProperty().addListener((observable, oldValue, newValue) -> 
		        tooltip.setText(newValue + "%"));
		});
		
		/****************** 
		 * DEPRECATED CODE
		 *****************/
		
		/*
		String pullExpenses1 = "SELECT Amount, Date, Category FROM Expenses ORDER BY Category DESC";
		try (Connection conn = SQLDatabaseConnection.getConnection();
			 Statement st = conn.createStatement();
			 ResultSet rs = st.executeQuery(pullExpenses1);){
			
			// Get size of ResultSet
			int rowcount = 0;
			if (rs.last()) {
			  rowcount = rs.getRow();
			  rs.beforeFirst();
			}
			
			long[] expenseArr = new long[rowcount];
			String currentCategory = "";
			long total = 0;
			
			rs.next();
			currentCategory = rs.getString("Category");
			do {
				total += rs.getLong("Amount");
				if (!(rs.getString("Category").equals(currentCategory))) {
					currentCategory = rs.getString("Category");
					
				}
			} while (rs.next());
		} catch (SQLException e) {
			System.out.println("Failed to retrieve ");
			e.printStackTrace();
		}
		*/
		
	}

	// Exporting expense records to a CSV file
	@FXML
	private void exportExpenses() {

		String planCode = LoginScreenController.getSessionPlanCode();
		
		String pullAllExpenses = "SELECT Amount, Date, Category, DateEntered, Image FROM Expenses WHERE PlanCode = '" + planCode + "'";
		try (Connection conn = SQLDatabaseConnection.getConnection();
			 Statement st = conn.createStatement();
			 ResultSet rs = st.executeQuery(pullAllExpenses);) {
			
			StringBuilder sb = new StringBuilder();

			// Enter the column names
			sb.append("Amount");
			sb.append(',');
			sb.append("Date");
			sb.append(',');
			sb.append("Category");
			sb.append(',');
			sb.append("Date Entered");
			sb.append(',');
			sb.append("Image in Base64");
			sb.append('\n');

			// Enter each row's data
			while (rs.next()) {
				sb.append(rs.getBigDecimal(1));
				sb.append(',');
				sb.append(rs.getDate(2));
				sb.append(',');
				sb.append("\"" + rs.getString(3) + "\"");
				sb.append(',');
				sb.append(rs.getDate(4));
				sb.append(',');
				sb.append(rs.getString(5));
				sb.append('\n');
			}

			// Show save prompt and save the file
			Stage stage = (Stage) exportButton.getScene().getWindow();
			FileChooser fc = new FileChooser();
			fc.setTitle("Save Spreadsheet");
			fc.getExtensionFilters().addAll(new ExtensionFilter("CSV File", "*.csv"));
			File file = fc.showSaveDialog(stage);
			if (file != null) {
				try {
					FileWriter writer = new FileWriter(file);
					writer.write(sb.toString());
					writer.close(); // Close resources
					
					// Display success popup
					Alert successPopup = new Alert(AlertType.INFORMATION);
					successPopup.setTitle("Success!");
					successPopup.setHeaderText("File successfully saved!");
					successPopup.showAndWait();
				} catch (IOException e) {
					System.out.println("Failed to write the CSV file.");
					e.printStackTrace();
				}
			}
			
			
	
		} catch (SQLException e) {
			System.out.println("Failed to retreive expenses for exporting.");
			e.printStackTrace();
		}
		
	}
		
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


/*  
 * For each row in the ResultSet, parse it and add it's Amount to the Total.
 * When the Category of the current row differs from the last row, add the Total to the long[] array. Reset the Total to 0, and add the current row's Amount to it. 
 * Repeat until there are no more rows.
 * 
 * Insert each category's total into the Pie Chart
 */