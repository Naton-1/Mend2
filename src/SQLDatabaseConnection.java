import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class SQLDatabaseConnection {
	
	private static Connection conn = null;
	
    // Connect to my personal Azure cloud database
    public static Connection getConnection() {
        String connectionUrl = "jdbc:sqlserver://[REDACTED]:1433;"
                             + "database=Mend2;"
                             + "user=[REDACTED];"
                             + "password=[REDACTED];"
                             + "encrypt=true;"
                             + "trustServerCertificate=false;"
                             + "hostNameInCertificate=*.database.windows.net;"
                             + "loginTimeout=10;";

        // Load SQL Server JDBC driver and establish connection.
        System.out.println("Connecting to SQL Server ... ");
        try {
        	 conn = DriverManager.getConnection(connectionUrl);
        	 System.out.println("Connection complete.");
        }
        // Handle any errors that may have occurred.
        catch (SQLException e) {
        	System.out.println("Connection failed.");
            e.printStackTrace();
        }
        return conn;
    }
}

// SQL EXAMPLES ON https://www.microsoft.com/en-us/sql-server/developer-get-started/java/windows/step/2.html