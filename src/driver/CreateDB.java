package mc.logicaloperators.driver;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CreateDB {
	static String[][] TRAINING_DATA_01 = {{"e0","e1", "e2", "e3","expected_result"},
		      {"1", "1", "1", "0","1"},
		      {"0", "1", "1", "0","1"},
		      {"1", "0", "1", "0","1"},
		      {"0", "1", "0", "0","0"},
		      {"1", "1", "1", "0","0"},
		      {"0", "1", "1", "0","0"}};
	public static void main(String[] args) throws SQLException {
		Connection connection = DriverManager.getConnection(DBDriver.url);
        if (connection != null) {
        	System.out.println("new db created.");
            trainingDataTable(connection, TRAINING_DATA_01); 
        }   

	}
	static void trainingDataTable(Connection connection, String[][] data) throws SQLException {
    	StringBuffer createSB = new StringBuffer("create table training_data(");
    	for (int i = 0; i<data[0].length-1; i++) createSB.append(data[0][i]+" text not null, ");
    	createSB.append(data[0][data[0].length-1]+" text not null)");
    	connection.createStatement().execute(createSB.toString());
    	for (int i=1; i <data.length; i++){
    		StringBuffer selectSB = new StringBuffer("select * from training_data where ");
    		for (int j = 0; j<data[0].length-2; j++) selectSB.append(data[0][j]+"='"+data[i][j]+"' and ");
    		selectSB.append(data[0][data[0].length-2]+"='"+data[i][data[0].length-2]+"';");
    		ResultSet rs = connection.createStatement().executeQuery(selectSB.toString());
    		if (!rs.next()) insert(connection, data[i], data[0]);
    	}
    }  
    static void insert(Connection connection, String[] values, String[] heading) throws SQLException { 
    	StringBuffer insertSB = new StringBuffer("insert into training_data(");
    	for (int i = 0; i<heading.length-1; i++) insertSB.append(heading[i]+", ");
    	insertSB.append(heading[heading.length-1]+ ") VALUES(");
    	for (int i = 0; i<heading.length-1; i++) insertSB.append("?,");
    	insertSB.append("?)");
    	PreparedStatement pStatement = connection.prepareStatement(insertSB.toString());  
    	for (int i = 0; i<heading.length; i++) pStatement.setString(i+1, values[i]);
        pStatement.executeUpdate();
    } 

}
