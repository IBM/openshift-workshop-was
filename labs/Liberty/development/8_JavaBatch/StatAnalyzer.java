package com.ibm.ws.jbatch.sample.employee;

import java.io.Serializable;
import javax.batch.api.partition.PartitionAnalyzer;
import javax.batch.runtime.BatchStatus;
import javax.batch.runtime.context.StepContext;
import javax.inject.Inject;

/* The PartitionAnalyzer runs on the step's thread, and is used to analyze
 * data from the paritions running on different threads.
 */
public class StatAnalyzer implements PartitionAnalyzer {

	@Inject StepContext stepContext;
	
    /**
     * Default constructor. 
     */
    public StatAnalyzer() {
      
    }

	/**
     * @see PartitionAnalyzer#analyzeStatus(BatchStatus, String)
     */
    public void analyzeStatus(BatchStatus arg0, String arg1) {
    
    }

	/**
     * @see PartitionAnalyzer#analyzeCollectorData(Serializable)
     */
    public void analyzeCollectorData(Serializable arg0) {
    	// Add the number of records processed by the partition to our thread's step context.
    	Integer numRecords = (Integer) stepContext.getTransientUserData();
    	Integer chunkData = (Integer)arg0;
    	Integer newInt = numRecords + chunkData;
    	stepContext.setTransientUserData(newInt);
    }

}
