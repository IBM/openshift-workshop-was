package com.ibm.ws.jbatch.sample.employee;

import javax.batch.api.chunk.ItemProcessor;

/* Convert String from text file to Employee record
 * 
 */
public class TextProcessor implements ItemProcessor {

    /**
     * Default constructor. 
     */
    public TextProcessor() {
        // TODO Auto-generated constructor stub
    }

	/**
     * @see ItemProcessor#processItem(Object)
     */
    public Employee processItem(Object arg0) {
        Employee employee = new Employee();
        String line = (String) arg0;
        String[] strings = line.split("\\|");
 
        employee.setName(strings[0]);
        employee.setAddress(strings[1]);
        employee.setCity(strings[2]);
        employee.setState(strings[3]);
        employee.setZipcode(strings[4]);
        employee.setEmail(strings[5]);
        employee.setEmployeeID(Integer.parseInt(strings[6]));
        employee.setPhone(strings[7]);
        int income = Integer.parseInt(strings[8]);
        employee.setAnnualIncome(income);
	    
        return employee;
    }

}
