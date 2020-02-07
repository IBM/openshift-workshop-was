package com.ibm.ws.jbatch.sample.employee;
import java.sql.Connection;
import java.sql.ResultSet;


// This class is stored within the step context of each chunk to facilitate processing
// for a transactional datasource.  According to the java Batch specification, a transaction is
// committed after processing each chunk. Therefore, a new SQL query is required before processing
// a new chunck of data.  This class holds the information required to
//  1) start a new query for the check
//  2) start a new query from check point
//  3) propagate statistics by the  PartitionCollector
public class JDBCDataHolder implements java.io.Serializable {
  
	private static final long serialVersionUID = 1L;
	
	private transient Connection conn;                     // SQL connection
    private transient ResultSet resultSet;                 // result set for chunk
    private Integer firstID;                               // firstID to read for chunk
    private Integer lastID;                                // lastID to read for chunk
    private Integer lastRead = new Integer(-1);  // actual employeeID last record, or -1
    private transient Integer numRead = new Integer(0); // number of records read within the cunck
    
    
    public Connection getConnection() {
    	return conn;
    }
    
    public void setConnection(Connection c) {
    	conn = c;
    }
    
    public ResultSet getResultSet(){
    	return resultSet;
    }
    
    public void setResultSet(ResultSet r) {
    	resultSet = r;
    }
    
    public Integer getFirstID() {
    	return firstID;
    }
    public void setFirstID(Integer f) {
    	firstID = f;
    }
    
    public Integer getLastID() {
    	return lastID;
    }
    
    public void setLastID(Integer l){
    	lastID = l;
    }
    
    public Integer getLastRead() {
    	return lastRead;
    }
    public void setLastRead(Integer l) {
    	lastRead = l;
    }
    
     
    public Integer getNumRead() {
    	return numRead;
    }
    
    public void setNumRead(Integer n) {
         numRead = n;
    }
    
	
}
