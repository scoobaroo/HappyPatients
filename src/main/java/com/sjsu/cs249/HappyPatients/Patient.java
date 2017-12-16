package com.sjsu.cs249.HappyPatients;

import com.datastax.driver.core.utils.UUIDs;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Patient implements Serializable {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private UUID id;

    private String firstName;

    private String lastName;

    private String birthDate;

    private String address;
    private String diagnosis;
    private String phoneNumber;
    private String treatment;
    private String status;

    Patient() {
    }
    public Patient(UUID id, String firstName, String lastName, String birthDate, String address, String phoneNumber, String status, String diagnosis, String treatment) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.birthDate = birthDate;
        this.address = address;
        this.phoneNumber = phoneNumber;
        this.status = status;
        this.diagnosis = diagnosis;
        this.treatment = treatment;
    }
    public HashMap<String, String> toMap() {
    		HashMap<String, String> map = new HashMap<String, String>();
		map.put("id", id.toString());
		map.put("firstName", firstName);
		map.put("lastName", lastName);
		map.put("status", status);
		map.put("address", address);
		map.put("phoneNumber", phoneNumber);
		map.put("diagnosis", diagnosis);
		map.put("treatment", treatment);
		return map;
    }
    public String getDiagnosis() {
		return diagnosis;
	}
	public void setDiagnosis(String diagnosis) {
		this.diagnosis = diagnosis;
	}
	public String getTreatment() {
		return treatment;
	}
	public void setTreatment(String treatment) {
		this.treatment = treatment;
	}
	@Override
    public String toString() {
    		 return "id: " +id.toString()+"\nfirst name: " +firstName+"\nlast name: " +lastName+"\nbirth date: " +birthDate+"\naddress: " 
    				 +address+"\nphoneNumber: " +phoneNumber+"\nstatus: " +status +"\ndiagnosis: "+diagnosis+"\ntreatment: " + treatment ;
    }
    
    public UUID getId() {
        return id;
    }
    public String getStatus() {
    		return status;
    }
    public void setStatus(String status) {
    		this.status = status;
    }
    public void setId() { this.id = UUIDs.timeBased(); }
    public String getFirstName() {
        return firstName;
    }
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }
    public String getLastName() {
        return lastName;
    }
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
    public String getBirthDate() {
        return birthDate;
    }
    public void setBirthDate(String birthDate) {
        this.birthDate = birthDate;
    }
    public String getAddress() {
        return address;
    }
    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}
