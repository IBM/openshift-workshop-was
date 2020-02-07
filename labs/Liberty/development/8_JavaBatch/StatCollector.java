package com.ibm.ws.jbatch.sample.employee;

import java.io.Serializable;
import javax.batch.api.partition.PartitionCollector;
import javax.inject.Inject;
import javax.batch.runtime.context.StepContext;

/* The PartitionCollector is used to collect data for one thread executing one partition. 
 * It is called at the end of each chunk processing, and the data it collects is sent to the PartitionAnalyzer
 */
public class StatCollector implements PartitionCollector {

	@Inject StepContext stepContext;
	
    /**
     * Default constructor. 
     */
    public StatCollector() {
    }

	/**
     * @see PartitionCollector#collectPartitionData()
     */
    public Serializable collectPartitionData() {
    	// Return the number of records read during chunk processing.
    	JDBCDataHolder holder = (JDBCDataHolder)stepContext.getTransientUserData();
	    return new Integer(holder.getNumRead());
    }

}
