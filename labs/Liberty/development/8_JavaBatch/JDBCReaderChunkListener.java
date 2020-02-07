package com.ibm.ws.jbatch.sample.employee;
import javax.batch.api.chunk.listener.ChunkListener;
import javax.batch.runtime.context.StepContext;
import javax.inject.Inject;
import javax.naming.InitialContext;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;
import javax.sql.DataSource;


/* Since we are using a transactional datasource, a commit happens at the end of processing each chunk.
 * We use the ChunkListener to start a new query at the end of each chunk.
 */
public class JDBCReaderChunkListener implements ChunkListener {

	
	@Inject StepContext stepContext;
	

    /**
     * Default constructor. 
     */
    public JDBCReaderChunkListener() {
    }

	/**
     * @see ChunkListener#beforeChunk()
     */
    public void beforeChunk() throws Exception {
    	// Start a new query
    	JDBCDataHolder holder = (JDBCDataHolder) stepContext.getTransientUserData();
    	Connection conn = holder.getConnection();
    	if ( conn == null ) {
    		// Initialize connection for first time
		    DataSource employeeDS = (DataSource) InitialContext.doLookup("jdbc/employeeDS");
	        conn = employeeDS.getConnection();   
	        holder.setConnection(conn);
    	}
	   
	    String query =  "select * from employee ";
	    
	    if ( holder.getLastRead() >= 0  ) {
	    	// start from last check point or last chunck read
	    	// Note that the next record has to be > the checkpointed record
	    	query += " where employeeID > "+ holder.getLastRead(); 
	    } else {
    		// First query. Start from the first ID
	    	query += " where employeeID >= "+ holder.getFirstID() ;
	    }  
	
	    if ( holder.getLastID() != null) {
	    	// lastID was specified for the chunk. Use it in the query.
	    	query += " and employeeID <= " + holder.getLastID();
	    }
	    // order the result by employeeID so that we are able to restart from the last ID read
	    query += " order by employeeID";
	    
	    
	    Statement stmt = conn.createStatement();
	    ResultSet result = 	stmt.executeQuery(query);
	    holder.setResultSet(result);
	    
	    // Reset number of records read for the chunk to 0 when starting a new chunk
	    holder.setNumRead(0); 
	
    }

	/**
     * @see ChunkListener#onError(Exception)
     */
    public void onError(Exception arg0) {
    }

	/**
     * @see ChunkListener#afterChunk()
     */
    public void afterChunk() throws Exception {
    	
    	// Close out the Resultset at end of the chunk
    	JDBCDataHolder holder = (JDBCDataHolder) stepContext.getTransientUserData();
    	ResultSet rs = holder.getResultSet();
    	if ( rs != null) {
    		rs.close();
    		holder.setResultSet(null);
    	}
    }

}
