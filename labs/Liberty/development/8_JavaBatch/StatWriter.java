package com.ibm.ws.jbatch.sample.employee;

import java.io.Serializable;
import java.util.List;
import javax.batch.api.chunk.ItemWriter;
import javax.batch.runtime.context.StepContext;
import javax.inject.Inject;

/* This ItemWriter is called to output each item read.
 * For our sample, we do not output anything during the processing of each item. 
 * Instead, we rely on the PartitionReducer to output statistics (the total number of records) at the end
 * of partition processing. 
 */
public class StatWriter implements ItemWriter {

	@Inject StepContext stepContext;
	
	private static Integer numRecords;
	
    /**
     * Default constructor. 
     */
    public StatWriter() {
      
    }

	/**
     * @see ItemWriter#open(Serializable)
     */
    public void open(Serializable arg0) {
 
    }

	/**
     * @see ItemWriter#close()
     */
    public void close() {
    }

	/**
     * @see ItemWriter#writeItems(List<java.lang.Object>)
     */
    public void writeItems(List<java.lang.Object> arg0) {
    }

	/**
     * @see ItemWriter#checkpointInfo()
     */
    public Serializable checkpointInfo() {
			return null;
    }

}
