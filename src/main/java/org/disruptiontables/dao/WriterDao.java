package org.disruptiontables.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.mysql.jdbc.Driver;


public class WriterDao {
	
	
		  private static Connection connect = null;
		  private Statement statement = null;
		  private PreparedStatement preparedStatement = null;
		  private ResultSet resultSet = null;

		  //TODO: move connection to singelton class or get connections pool
		  //TODO: http://www.tutorialspoint.com/javaexamples/jdbc_executebatch.htm add batch statements
		  public void setDBConnection()
		  {
			  try {
			      // this will load the MySQL driver, each DB has its own driver
			      Class.forName("com.mysql.jdbc.Driver");
			      // setup the connection with the DB.
			      connect = DriverManager.getConnection("jdbc:mysql://localhost:3306/tlf_resource?"
			              + "user=root&password=root");

			    } 
			  	catch (Exception e) {
			     System.out.println("db set connection problem: "+ e.toString());
			    } 
		  }
		  
		  
		  private void close() {
		    	try {
		    	//if we have a statement + results from querying then we close everything 
		    	if (statement!=null){
		    		statement.close();
		    		connect.close();
		    		resultSet.close();
		    	}
		    	
		    	else
		    		connect.close();
			    
		    	} catch (SQLException e) {
		    		 System.out.println("SQL Error: " +e.toString());
				}
		    	catch (Exception ex){
		    		 System.out.println("Error: " +ex.toString());
		    	}
		      }
		  
		  
		  /**
		   * Get all lines from DB
		   * */
		  public List<Line> getAllLines() throws Exception {
			    try {
			      
			    	List<Line> tableList = new ArrayList<Line>();
			    	Line line;
			    	// statements allow to issue SQL queries to the database
			      statement = connect.createStatement();
			      resultSet = statement.executeQuery("select * from line");
			      
			      //writeResultSet(resultSet);
			      
			      while (resultSet.next()) {      
				      line = new Line();
				      line.setId(resultSet.getInt("idline"));
				      line.setLineName(resultSet.getString("line_name"));
				      line.setLineDirection(resultSet.getString("line_direction"));
				      line.setLineType(resultSet.getString("line_type"));
				      tableList.add(line);
				    }
			      
			     return tableList;

			    } catch (Exception e) {
			      throw e;
			    } finally {
			      close();
			    }
			}
		  
		  /**
		   * Get all line branches
		   * */
		  public List<Branch> getAllLineBranches(int lineId) throws Exception {
			    try {
			      
			    	List<Branch> branchList = new ArrayList<Branch>();
			    	Line line;
			    	Branch branch;
			    	// statements allow to issue SQL queries to the database
			      statement = connect.createStatement();
			      resultSet = statement.executeQuery("select idnext_branch, branch_name, branch.idbranch, next_branch.next_branch_id, line.idline, line_name from branch inner join line on line.idline=branch.idline inner join next_branch on next_branch.idbranch=branch.idbranch where line.idline="+lineId);
			      System.out.println("DB read ok!");
			      
			      while (resultSet.next()) {      
			    	  branch = new Branch();
			    	  branch.setId(resultSet.getInt("idbranch"));
			    	  branch.setName(resultSet.getString("branch_name"));
				      branch.setNextId(resultSet.getInt("next_branch_id"));
				      branchList.add(branch);
				    }
			      
			     return branchList;

			    } catch (Exception e) {
			      throw e;
			    } finally {
			     // close();
			    }
			}
		  
		  /**
		   * Get specific line from the DB
		   * */
		  public Line getLine(int i) throws Exception {
			    try {
			      
			    	Line line = new Line();
			    	// statements allow to issue SQL queries to the database
			      statement = connect.createStatement();
			      resultSet = statement.executeQuery("select * from line where idline="+Integer.toString(i));
			      
			      while (resultSet.next()) {
				      line.setId(resultSet.getInt("idline"));
				      line.setLineName(resultSet.getString("line_name"));
				      line.setLineDirection(resultSet.getString("line_direction"));
				      line.setLineType(resultSet.getString("line_type"));
				    }
			      
			     return line;

			    } catch (Exception e) {
			      throw e;
			    } finally {
			      close();
			    }
			}
		  
		  
		  /**
		   * Get all stations form DB
		   * */
		  public List<Station> getAllStations(){
			  try{
				  //to do: celanup connections and change them to dao factory
				  if(connect==null)
					  connect = DriverManager.getConnection("jdbc:mysql://localhost:3306/tlf_resource?"
				              + "user=root&password=root");
				  
				  Station station;
				  List<Station> allStations = new ArrayList<Station>();
				  
				  statement = connect.createStatement();
				  resultSet = statement.executeQuery("select station.idstation, name, station_order from station inner join station_uniq on station.idstation = station_uniq.idstation");
				  
				  while(resultSet.next()){
					  station = new Station();
					  station.setStationId(resultSet.getInt("idstation"));
					  station.setName(resultSet.getString("name"));
					  station.setStationOrder(resultSet.getInt("station_order"));
					  //to-do: add line object to result set
					  // station.setLine(resultSet.geto(""));
					  allStations.add(station);
				  }
				  return allStations;
			  } 
			  catch (Exception e){
				  System.out.println(e.toString());
				  close();
		      }
			  return null;
		  }
			  
		  
		  
		  /**
		   * Get station id by 
		   * */
		  public Station getStationId(String stationName){
			  try{
				  //to do: celanup connections and change them to dao factory
				  if(connect==null)
					  connect = DriverManager.getConnection("jdbc:mysql://localhost:3306/tlf_resource?"
				              + "user=root&password=root");
				  
				  Station station = new Station();
				  
				  statement = connect.createStatement();
				  resultSet = statement.executeQuery("select station.idstation, name, station_order from station inner join station_uniq on station.idstation = station_uniq.idstation where name LIKE '%"+stationName+"%'");
					  
				  while(resultSet.next()){
				  	  //fetches first result only
				  	  station.setStationId(resultSet.getInt("idstation"));
					  station.setName(resultSet.getString("name"));
					  station.setStationOrder(resultSet.getInt("station_order"));
				  }
				  return station;
			  } 
			  catch (Exception e){
				  System.out.println(e.toString());
				  close();
		      }
			  return null;
		  }
			   

		  public List<Station> getSortedStations(List<Branch> branches){
			  try{
				  
				  if(connect==null)
					  connect = DriverManager.getConnection("jdbc:mysql://localhost:3306/tlf_resource?"
				              + "user=root&password=root");
				  
				  Station station;
				  List<Station> allStations = new ArrayList<Station>();
					
				  for(int i=0; i<branches.size();i++){ 
						
						  statement = connect.createStatement();
						 
						  resultSet = statement.executeQuery("select * from station_uniq inner join station on station_uniq.idstation = station.idstation where idbranch ="+ branches.get(i).getId()+" order by station_order ASC");
						  
						  while(resultSet.next()){
							  station = new Station();
							  station.setStationId(resultSet.getInt("idstation"));
							  station.setName(resultSet.getString("name"));
							  station.setStationOrder(resultSet.getInt("station_order"));
							  //to-do: add line object to result set
							  // station.setLine(resultSet.geto(""));
							  allStations.add(station);
						  }
					  }
				  return allStations;
			  } 
			  catch (Exception e){
				  System.out.println(e.toString());
				  close();
		      }
			  return null;
		  }
		  
		  //TODO: cleanup this mess with connection lifecycle
		  public int getBranchByStationId(int stationId){
			  try{
				  
				  if(connect==null)
					  connect = DriverManager.getConnection("jdbc:mysql://localhost:3306/tlf_resource?"
				              + "user=root&password=root");
				  
				  Branch  branch = new Branch();		
				  statement = connect.createStatement();	 
				  resultSet = statement.executeQuery("select branch.idbranch from branch inner join station_uniq on branch.idbranch = station_uniq.idbranch where station_uniq.idstation="+stationId);
						  
				  while(resultSet.next()){
					  branch = new Branch();
					  branch.setId(resultSet.getInt("branch.idbranch"));				 
				  }
					  
				 return branch.getId();
			  } 
			  catch (Exception e){
				  System.out.println(e.toString());
				  close();
		      }
			  return -1;
		  }
		  
		  
		  
}
