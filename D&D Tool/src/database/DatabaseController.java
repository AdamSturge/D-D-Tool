package database;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

public class DatabaseController {

	private static Connection c;
	private HashMap<ResultSet,PreparedStatement> StatementMap;
	
	public DatabaseController(String directory) {
		try {
			c=DriverManager.getConnection(directory);
		} catch (SQLException e) {
			System.out.println("failed to connect to " + directory);
			e.printStackTrace();
		}
		StatementMap = new HashMap<ResultSet,PreparedStatement>();
	}

	public boolean isOnline(){
		return true;	//currently implemented to test connection with GUI //Ji
	}


	public void write(String SQLStatement, ArrayList<Object> sqlData) throws SQLException {
		PreparedStatement pstmt = c.prepareStatement(SQLStatement);
		String className = "";
		for(int i = 0; i < sqlData.size(); ++i) {
			className = sqlData.get(i).getClass().getName();
			//add in cases to the switch statement as different sqlData types become required
			if(className.equals("java.lang.String")){
				pstmt.setString(i+1, (String)sqlData.get(i));
			}else if(className.equals("java.lang.Integer")){
				pstmt.setInt(i+1, (Integer)sqlData.get(i));
			}
			else if(className.equals("java.lang.Long")){
				pstmt.setLong(i+1,(Long)sqlData.get(i));
			} 
			else if(className.equals("java.lang.Boolean")){
				pstmt.setBoolean(i+1,(Boolean) sqlData.get(i));
			}
			else{
				pstmt.setObject(i+1,sqlData.get(i));
			}
		}
	
	pstmt.executeUpdate();
	pstmt.close();
}


public ResultSet read(String SQLStatement,ArrayList<Object> sqlData) throws SQLException, IOException, ClassNotFoundException{
	PreparedStatement pstmt = c.prepareStatement(SQLStatement);
	String className = "";
	for(int i = 0; i < sqlData.size(); ++i) {
		className = sqlData.get(i).getClass().getName();
		//add in cases to the switch statement as different sqlData types become required
		if(className.equals("java.lang.String")){
			pstmt.setString(i+1, (String)sqlData.get(i));
		}else if(className.equals("java.lang.Integer")){
			pstmt.setInt(i+1, (Integer) sqlData.get(i));
		}else if(className.equals("java.lang.Long")) {
			pstmt.setLong(i+1, (Long) sqlData.get(i));
		}else if(className.equals("java.lang.Boolean")){
			pstmt.setBoolean(i+1,(Boolean) sqlData.get(i));
		}else if(className.equals("java.lang.Character")){
			//pstmt.setCharacter(i+1,(Character) sqlData.get(i));
		}
		else{
		pstmt.setObject(i+1,sqlData.get(i));
		}
	}
	ResultSet rs = pstmt.executeQuery();
	StatementMap.put(rs, pstmt);
	return rs;
	
}

public void delete(String SQLStatement,ArrayList<Object> sqlData) throws SQLException, IOException, ClassNotFoundException{
	PreparedStatement pstmt = c.prepareStatement(SQLStatement);
	String className = "";
	for(int i = 0; i < sqlData.size(); ++i) {
		className = sqlData.get(i).getClass().getName();
		//add in cases to the switch statement as different sqlData types become required
		if(className.equals("java.lang.String")){
			pstmt.setString(i+1, (String)sqlData.get(i));
		}else if(className.equals("java.lang.Integer")){
			pstmt.setInt(i+1, (Integer)sqlData.get(i));
		}
		else if(className.equals("java.lang.Long")){
			pstmt.setLong(i+1, (Long)sqlData.get(i));
		}
	}

	pstmt.executeUpdate();
	pstmt.close();
}

//closes the connection between the passed result set to the database
public void closeQuery(ResultSet rs){
	try {
		StatementMap.get(rs).close();
	} catch (SQLException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
}

}




