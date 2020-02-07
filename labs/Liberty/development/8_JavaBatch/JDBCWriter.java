package com.ibm.ws.jbatch.sample.employee;

import java.io.Serializable;
import java.util.List;
import javax.sql.DataSource;
import java.sql.Connection;

import java.sql.Statement;
import javax.naming.InitialContext;
import javax.batch.api.chunk.ItemWriter;

// This class implements an ItemWriter used to populate the database
public class JDBCWriter implements ItemWriter {
	
	DataSource employeeDS = null;
   Connection conn = null;
   Statement stmt = null;

    /**
     * Default constructor. 
     */
    public JDBCWriter() {
    }

	/**
     * @see ItemWriter#open(Serializable)
     */
    public void open(Serializable arg0) throws Exception {
       	// initialize the connection to the database
   		employeeDS = InitialContext.doLookup("jdbc/employeeDS");
   	    conn = employeeDS.getConnection();   

    }

	/**
     * @see ItemWriter#close()
     */
    public void close() throws Exception {
        conn.close();
    }

	/**
     * @see ItemWriter#writeItems(List<java.lang.Object>)
     * Write the item coming from ItemProcessor
     */
    public void writeItems(List<java.lang.Object> arg0) throws Exception {
        
        stmt = conn.createStatement();
       
    	for (Object obj: arg0) {
    	    Employee employee = (Employee)obj;
    	    String insertStmt = "insert into employee values ("
    	       + "'" + employee.getName() + "'" 
    	       + ", '" + employee.getAddress() + "'" 
    	       + ", '" + employee.getCity() + "'" 
    	       + ", '" + employee.getState() + "'" 
    	       + ", '" + employee.getZipcode() + "'" 
    	       + ", '" + employee.getEmail() + "'" 
    	       + ", " + employee.getEmployeeID()
    	       + ", '" + employee.getPhone() + "'" 
    	       + "," + employee.getAnnualIncome() 
    	       + ")";
  	    	;
  	    	
  	    	stmt.executeUpdate(insertStmt);
  	    	

    	}
    }

	/**
     * @see ItemWriter#checkpointInfo()
     */
    public Serializable checkpointInfo() {
    	// no checkpoint information for ItemWriter.
		return null;
    }

}
