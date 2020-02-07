package com.ibm.ws.jbatch.sample.employee;

import java.io.Serializable;
import java.sql.ResultSet;

import javax.batch.api.BatchProperty;
import javax.batch.api.chunk.ItemReader;
import javax.batch.runtime.context.StepContext;
import javax.inject.Inject;


// This class implements an ItemReader to read records from a datasource.
public class JDBCReader implements ItemReader {

	@Inject StepContext stepContext;
	
	@Inject
    @BatchProperty(name = "firstID")
    String firstID;
	
	@Inject
    @BatchProperty(name = "lastID")
    String lastID;
    
	
	
    /**
     * Default constructor. 
     */
    public JDBCReader() {
    }

	/**
     * @see ItemReader#readItem()
     */
    public Object readItem() throws Exception {
    	// The JDBCHolder contains the RsultSet needed to read next chunk.
        JDBCDataHolder holder = (JDBCDataHolder) stepContext.getTransientUserData();
        ResultSet result = holder.getResultSet();
    	     
        if ( result.next() ) {
        	Employee employee = new Employee();
         	employee.setName(result.getString(1));
           	employee.setAddress(result.getString(2));
           	employee.setCity(result.getString(3));
           	employee.setState(result.getString(4));
           	employee.setZipcode(result.getString(5));
           	employee.setEmail(result.getString(6));
           	employee.setEmployeeID(result.getInt(7));
           	employee.setPhone(result.getString(8));
           	employee.setAnnualIncome(result.getInt(9));
         	
           	holder.setLastRead(employee.getEmployeeID());
           	holder.setNumRead(holder.getNumRead()+1);
    	    return employee;
    	}
    	else return null;

    }

	/**
     * @see ItemReader#open(Serializable)
     */
    public void open(Serializable checkpt) throws Exception {
   
    	    JDBCDataHolder holder = null;
    	    if ( checkpt == null ) {
    	    	// First time 
    	    	holder = new JDBCDataHolder();
    	    } else {
    	    	// Restore from last checkpoint.
    	    	holder = (JDBCDataHolder) checkpt;
    	    }
    	    
    	    holder.setFirstID(Integer.parseInt(firstID));
    	    if ( lastID != null )
    	        holder.setLastID(Integer.parseInt(lastID));
    	    stepContext.setTransientUserData(holder);
      	    
    }

	/**
     * @see ItemReader#close()
     */
    public void close() throws Exception{
    }

	/**
     * @see ItemReader#checkpointInfo()
     */
    public Serializable checkpointInfo() {
        // JDBCDataHolder stored in the step context is our checkpoint data
		return (Serializable) stepContext.getTransientUserData();
    }

}
