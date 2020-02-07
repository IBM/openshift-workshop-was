package com.ibm.ws.jbatch.sample.employee;

import javax.batch.api.partition.PartitionReducer;
import javax.batch.api.partition.PartitionReducer.PartitionStatus;
import javax.batch.runtime.context.StepContext;
import javax.inject.Inject;

/* The PartitionReducer is called during vaarios phases of partition processing.
 *  We use it to print the totoal number of records processed at the end of processing all partitions.
 */
public class StatReducer implements PartitionReducer {

	@Inject StepContext stepContext;
	
    /**
     * Default constructor. 
     */
    public StatReducer() {
  
    }

	/**
     * @see PartitionReducer#afterPartitionedStepCompletion(PartitionReducer.PartitionStatus)
     */
    public void afterPartitionedStepCompletion(PartitionReducer.PartitionStatus arg0) {
    	// Print out total number of records processed
        Integer numRecords = (Integer)stepContext.getTransientUserData();
    	System.out.println("All partition chunks collected " + numRecords + " records");
    }

	/**
     * @see PartitionReducer#rollbackPartitionedStep()
     */
    public void rollbackPartitionedStep() {
    }

	/**
     * @see PartitionReducer#beforePartitionedStepCompletion()
     */
    public void beforePartitionedStepCompletion() {
    
    }

	/**
     * @see PartitionReducer#beginPartitionedStep()
     */
    public void beginPartitionedStep() {
    	// Initialize number of records to 0
    	stepContext.setTransientUserData(new Integer(0));
    }

}
