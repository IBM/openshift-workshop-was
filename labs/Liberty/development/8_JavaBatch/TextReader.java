package com.ibm.ws.jbatch.sample.employee;

// Implementation of a reader that reads lines in a text file
import java.io.Serializable;
import java.io.BufferedReader;
import java.io.FileReader;

import javax.batch.api.BatchProperty;
import javax.batch.api.chunk.ItemReader;
import javax.inject.Inject;

/* Read input line from text file
 * 
 */
public class TextReader implements ItemReader {

	@Inject
    @BatchProperty(name = "input.text.file.name")
    String inputFileName;
	
	private BufferedReader input = null;
	
	// last record read. Used for checkpoint
	private Long lastRecord = new Long(0);
    
    /**
     * Default constructor. 
     */
    public TextReader() {
    }

	/**
     * @see ItemReader#readItem()
     */
    public Object readItem() throws java.io.IOException {
			String temp = input.readLine();
			lastRecord++;
			return temp;
    }

	/**
     * @see ItemReader#open(Serializable)
     */
    public void open(Serializable checkPoint) throws java.io.IOException {
    	
           input = new BufferedReader(new FileReader(inputFileName));
           lastRecord = (Long)checkPoint;
           if ( lastRecord == null) {
        	   // No previous checkpoint. Read from beginning
    	       lastRecord = new Long(0);
           }   
           // skip up to last record
           for (long i=0; i < lastRecord.longValue(); i++){
    	       input.readLine();
           }
     }

	/**
     * @see ItemReader#close()
     */
    public void close() throws java.io.IOException {
    	if ( input != null) {
    	   	input.close();
    	}
    }

	/**
     * @see ItemReader#checkpointInfo()
     */
    public Serializable checkpointInfo() {
			return lastRecord;
    }

}
