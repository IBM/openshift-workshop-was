package com.ibm.ws.jbatch.sample.employee;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import javax.batch.api.Batchlet;
import javax.naming.InitialContext;
import javax.sql.DataSource;

/* A Batchlet is used for non-record related process.
 * For this sample, we'll use it to initialize the database with an empty employee table
 */
public class InitializationBatchlet implements Batchlet {

    /**
     * Default constructor. 
     */
    public InitializationBatchlet() {
    }

	/**
     * @see Batchlet#stop()
     */
    public void stop() {
    }

	/**
     * @see Batchlet#process()
     */
    public String process() throws Exception {
    	// Create empty table.
    	DataSource employeeDS;
       	employeeDS = (DataSource) InitialContext.doLookup("jdbc/employeeDS");
        
        Statement stmt = null;        
        Connection con = null;

        con = employeeDS.getConnection();
        stmt = con.createStatement();
        try {
             stmt.executeUpdate("drop table employee");
         } catch(SQLException ex) {
               	  // ignore
         }
         stmt.executeUpdate("create table employee (name varchar(100),address varchar(100),city varchar(100),state char(2),zipcode varchar(10),email varchar(100),employeeID Integer,phone varchar(100),annualIncome Integer)");
         System.out.println("Employee table created");
         stmt.close();
         con.close();
	     return null;
    }
}
