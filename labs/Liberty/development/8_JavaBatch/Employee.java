package com.ibm.ws.jbatch.sample.employee;

import java.io.Serializable;

// This class holds information about one employee
public class Employee implements Serializable {

	private static final long serialVersionUID = -316663557455967458L;
	
	private String name;
	private String address;
    private String city;
    private String state;
    private String zipcode;
    private String email;
    private int employeeID;
    private String phone;
    private int annualIncome;
   
    public String getName() {
    	return name;
    }
    public void setName(String n) {
    	name = n;
    }
    
    public String getAddress(){
    	return address;
    }
    
    public void setAddress(String s) {
    	address = s;
    }
    
    public String getCity(){
    	return city;
    }
    
    public void setCity(String c) {
    	city = c;
    }
    
    public String getState() {
    	return state;
    }
    
    public void setState(String s) {
    	state = s;
    }
    
    public String getZipcode() {
    	return zipcode;
    }
    
    public void setZipcode(String z) {
    	zipcode = z;
    }
    
    
    public String getEmail() {
    	return email;
    }
    
    public void setEmail(String e) {
    	email = e;
    }
    
    public int getEmployeeID() {
    	return employeeID;
    }
    
    public void setEmployeeID(int e){
    	employeeID = e;
    }
    
    public String getPhone() {
    	return phone;
    }
    
    public void setPhone(String p) {
    	phone = p;
    }
    
    public int getAnnualIncome() {
    	return annualIncome;
    }
    
    public void setAnnualIncome(int i){
    	annualIncome = i;
    }
}

