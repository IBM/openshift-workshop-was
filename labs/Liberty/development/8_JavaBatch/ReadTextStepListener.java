package com.ibm.ws.jbatch.sample.employee;

import javax.batch.api.listener.StepListener;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javax.naming.InitialContext;


/* The StepListener is notified of the different phases of a step.
 * For this sample, ReadTextStepListener is use log the number of records in the database
 * 
 */
public class ReadTextStepListener implements StepListener {

       /**
     * Default constructor. 
     */
    public ReadTextStepListener() {
    }

	/**
     * @see StepListener#beforeStep()
     */
    public void beforeStep() {
  
    }

	/**
     * @see StepListener#afterStep()
     */
    public void afterStep() throws Exception {
       DataSource employeeDS = (DataSource) InitialContext.doLookup("jdbc/employeeDS");

       String countStmt = "select count(*) from employee";   
       Statement stmt = null;    
       Connection con = null;

 
       con = employeeDS.getConnection();
       stmt = con.createStatement();
       ResultSet result = stmt.executeQuery(countStmt);
       result.next();
       long num = result.getLong(1);
       System.out.println("Employee table populated with " + num + " records");
          
    }

}
