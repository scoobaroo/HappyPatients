package com.sjsu.cs249.Services;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.MediaType;
import com.sjsu.cs249.HappyPatients.*;
import com.sjsu.cs249.Services.Messager.AnalyticsConsumer;
import com.sjsu.cs249.Services.Messager.EmailConsumer;
import com.sjsu.cs249.Services.Messager.Producer;
import com.sjsu.cs249.Services.Memcached;
import org.apache.log4j.Logger;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.UUID;
import com.datastax.driver.core.Session;
import com.fasterxml.jackson.*;
import com.fasterxml.jackson.databind.ObjectMapper;
@Path("/system")
public class HappyPatientsService {
    CassandraConnector connector = new CassandraConnector();
    private static final Logger logger = Logger.getLogger(HappyPatientsService.class);
    Properties properties = new java.util.Properties();  
	String property;
	Memcached cache;
	ObjectMapper mapper = new ObjectMapper();

	public HappyPatientsService() throws IOException {
		cache = new Memcached();
		cache.start();
		InputStream propertiesStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("fetchPolicy.properties");
	    	if (propertiesStream != null) {
	    		try {
					properties.load(propertiesStream);
					propertiesStream.close();
					property = properties.getProperty("fetchPolicy");
				} catch (IOException e) {
					e.printStackTrace();
				}
	    	}
	    	populateCache();
	}
	
	@SuppressWarnings("unlikely-arg-type")
	public void populateCache() throws IOException {
		connector.connect("127.0.0.1", 9042);
        Session session = connector.getSession();
        KeyspaceRepository sr = new KeyspaceRepository(session);
        sr.useKeyspace("hospitalOps");
        PatientPersonalInfo ppi = new PatientPersonalInfo(session);
        ArrayList<Patient> patients = (ArrayList<Patient>) ppi.selectAll();
		@SuppressWarnings("unchecked")
		List<Patient> filtered = new ArrayList<Patient>();
		for(Patient p : patients) {
			if(p.getStatus().equalsIgnoreCase(property)) {
				filtered.add(p);
			}
		}
	    	for (Patient p : filtered) {
	    		HashMap<String, String> map = new HashMap<String,String>();
	    		System.out.println("Putting patient " + p.getFirstName() + " in cache");
	    		map.put("id", p.getId().toString());
	    		map.put("firstName", p.getFirstName());
	    		map.put("lastName", p.getLastName());
	    		map.put("status", p.getStatus());
	    		map.put("address", p.getAddress());
	    		map.put("phoneNumber", p.getPhoneNumber());
	    		cache.putContentInCache(p.getId().toString(), map);
	    	}
	    	connector.close();
	}
	
    @GET
    @Path("/testProperties")
    public Response testProperties() {
        return Response.status(200).entity(property).build();
    }
    @GET
    @Path("/retrievePatientsWithStatus/{status}")
    public Response retrievePatientsWithStatus(@PathParam("status") String status) throws IOException {
    		String resultSet = "";
        connector.connect("127.0.0.1", 9042);
        Session session = connector.getSession();
        KeyspaceRepository sr = new KeyspaceRepository(session);
        sr.useKeyspace("hospitalOps");
        return Response.status(200).entity("STATUS").build();
    }

    @GET
    @Path("/retrieveAll/")
    public Response retrieveAll() throws IOException {
        String resultSet = "";
        connector.connect("127.0.0.1", 9042);
        Session session = connector.getSession();
        KeyspaceRepository sr = new KeyspaceRepository(session);
        sr.useKeyspace("hospitalOps");
        PatientPersonalInfo ppi = new PatientPersonalInfo(session);
        List<Patient> patients = ppi.selectAll();
        connector.close();
        for ( Patient p : patients) {
        		resultSet += p.toString() + "</br>";
        }
        String jsonInString = mapper.writeValueAsString(patients);
        return Response.status(200).entity(jsonInString).build();
    }

    @GET
    @Path("/retrievePatient/{param}")
    public Response retrievePatient(@PathParam("param") UUID patientId) throws IOException {
        String patientName = "";
        connector.connect("127.0.0.1", 9042);
        Session session = connector.getSession();
        KeyspaceRepository sr = new KeyspaceRepository(session);
        sr.useKeyspace("hospitalOps");
        PatientPersonalInfo ppi = new PatientPersonalInfo(session);
        patientName = ppi.selectById(patientId).getFirstName() + " " + ppi.selectById(patientId).getLastName();
        connector.close();
        String output = "Hello Patient : " + patientName;
        return Response.status(200).entity(output).build();
    }

    @POST
    @Path("/addPatient")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response addPatient(Patient patient) throws IOException {
		String phoneNumber = patient.getPhoneNumber();
		String birthDate = patient.getBirthDate();
		String firstName = patient.getFirstName();
		String lastName = patient.getLastName();
		String address = patient.getAddress();
		String status = patient.getStatus();
		String regexPhoneNumber = "^(1\\-)?[0-9]{3}\\-?[0-9]{3}\\-?[0-9]{4}$";
		String output = "";
		String regexBirthDate = "^(0[1-9]|1[012])[- /.](0[1-9]|[12][0-9]|3[01])[- /.](19|20)\\d\\d$";
		if (!phoneNumber.matches(regexPhoneNumber)) {
			output = "Invalid phone number.";
			return Response.status(200).entity(output).build();
		}
		if (!birthDate.matches(regexBirthDate)) {
			output = "Invalid birth date.";
			return Response.status(200).entity(output).build();
		}
		if (((firstName.length()) > 50) || (firstName.length() == 0)) {
			output = "Invalid first name. First Name cannot be kept blank and it cannot have more than 50 characters.";
			return Response.status(200).entity(output).build();
		}
		if ((lastName.length()) > 50 || (lastName.length() == 0)) {
			output = "Invalid last name. Last Name cannot be kept blank and it cannot have more than 50 characters.";
			return Response.status(200).entity(output).build();
		}
		if ((address.length()) > 200 || (address.length() == 0)) {
			output = "Invalid address. Address cannot be kept blank and it cannot have more than 200 characters.";
			return Response.status(200).entity(output).build();
		}
		if ((!status.equals("ICU")) && (!status.equals("Observation")) && (!status.equals("Urgent Care"))
				&& (!status.equals("Discharged"))) {
			output = "Invalid status. It can be either ICU, Discharged, Obervation or Urgent Care.";
			return Response.status(200).entity(output).build();
		}
        connector.connect("127.0.0.1", 9042);
        Session session = connector.getSession();
        KeyspaceRepository sr = new KeyspaceRepository(session);
        sr.useKeyspace("hospitalOps");
        PatientPersonalInfo ppi = new PatientPersonalInfo(session);
        patient.setId();
        ppi.insertPatient(patient);
		Messager.thread(new Producer(), false);
		Messager.thread(new Messager.EmailConsumer(), false);
		Messager.thread(new Messager.AnalyticsConsumer(), false);
        connector.close();
        output = "Welcome Patient : " + patient.getFirstName() + ". Here is your ID: " + patient.getId();
        if (status.equals(property)) {
			HashMap<String, String> map = new HashMap<String, String>();
			System.out.println("Putting patient " + patient.getFirstName() + " in cache");
			map.put("id", patient.getId().toString());
			map.put("firstName", patient.getFirstName());
			map.put("lastName", patient.getLastName());
			map.put("status", patient.getStatus());
			map.put("address", patient.getAddress());
			map.put("phoneNumber", patient.getPhoneNumber());
			cache.putContentInCache(patient.getId().toString(), map);
        }
        return Response.status(200).entity(output).build();
    }
    
    @PUT
	@Path("/updatePatient/{id}")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response updatePatient(@PathParam("id") String id, Patient patient) throws IOException {
		String phoneNumber = patient.getPhoneNumber();
		String birthDate = patient.getBirthDate();
		String firstName = patient.getFirstName();
		String lastName = patient.getLastName();
		String address = patient.getAddress();
		String status = patient.getStatus();

		String regexPhoneNumber = "^(1\\-)?[0-9]{3}\\-?[0-9]{3}\\-?[0-9]{4}$";
		String output = "";
		String regexBirthDate = "^(0[1-9]|1[012])[- /.](0[1-9]|[12][0-9]|3[01])[- /.](19|20)\\d\\d$";

		if (!phoneNumber.matches(regexPhoneNumber)) {
			output = "Invalid phone number.";
			return Response.status(200).entity(output).build();
		}
		if (!birthDate.matches(regexBirthDate)) {
			output = "Invalid birth date.";
			return Response.status(200).entity(output).build();
		}
		if (((firstName.length()) > 50) || (firstName.length() == 0)) {
			output = "Invalid first name. First Name cannot be kept blank and it cannot have more than 50 characters.";
			return Response.status(200).entity(output).build();
		}
		if ((lastName.length()) > 50 || (lastName.length() == 0)) {
			output = "Invalid last name. Last Name cannot be kept blank and it cannot have more than 50 characters.";
			return Response.status(200).entity(output).build();
		}
		if ((address.length()) > 200 || (address.length() == 0)) {
			output = "Invalid address. Address cannot be kept blank and it cannot have more than 200 characters.";
			return Response.status(200).entity(output).build();
		}	
		if ((!status.equals("ICU")) && (!status.equals("Observation")) && (!status.equals("Urgent Care"))
				&& (!status.equals("Discharged"))) {
			output = "Invalid status. It can be either ICU, Discharged, Obervation or Urgent Care.";
			return Response.status(200).entity(output).build();
		}

		logger.debug("Updating Patient info");
		connector.connect("127.0.0.1", 9042);
		Session session = connector.getSession();
		KeyspaceRepository sr = new KeyspaceRepository(session);
		sr.useKeyspace("hospitalOps");
		PatientPersonalInfo ppi = new PatientPersonalInfo(session);
		ppi.updatePatient(id, patient);
		Patient p = ppi.selectById(UUID.fromString(id));
		connector.close();
		if (p == null) {
			output = "Patient with given Id does not exist.";
			return Response.status(200).entity(output).build();
		} else {
			output = "Patient info updated: " + p.toString();
			Messager.thread(new Producer(), false);
			Messager.thread(new Messager.EmailConsumer(), false);
			Messager.thread(new Messager.AnalyticsConsumer(), false);
			if (status.equals(property)) {
				HashMap<String, String> map = new HashMap<String, String>();
				System.out.println("Putting patient " + patient.getFirstName() + " in cache");
				map.put("firstName", firstName);
				map.put("lastName", lastName);
				map.put("status", status);
				map.put("address", address);
				map.put("phoneNumber", phoneNumber);
				cache.putContentInCache(id, map);
			}
			return Response.status(200).entity(output).build();
		}
	}
    
    @DELETE
    @Path("/deletePatient/{param}")
    public Response deletePatient(@PathParam("param") UUID patientId) throws IOException {
        String patientName = "";
        connector.connect("127.0.0.1", 9042);
        Session session = connector.getSession();
        KeyspaceRepository sr = new KeyspaceRepository(session);
        sr.useKeyspace("hospitalOps");
        PatientPersonalInfo ppi = new PatientPersonalInfo(session);
        patientName = ppi.selectById(patientId).getFirstName() + " " + ppi.selectById(patientId).getLastName();
        ppi.deletePatientById(patientId);
        connector.close();
        String output = "Removed Patient : " + patientName;
        return Response.status(200).entity(output).build();
    }

    @DELETE
    @Path("/deleteSystem/{param}")
    public Response deleteSystem(@PathParam("param") String keyspaceName) {
        connector.connect("127.0.0.1", 9042);
        Session session = connector.getSession();
        KeyspaceRepository sr = new KeyspaceRepository(session);
        sr.useKeyspace(keyspaceName);
        sr.deleteKeyspace(keyspaceName);
        connector.close();
        String output = "System Deleted : " + keyspaceName;
        return Response.status(200).entity(output).build();
    }
}