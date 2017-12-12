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
import java.util.Properties;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import com.datastax.driver.core.Session;
import javax.naming.InitialContext;
import java.util.UUID;

@Path("/patient_system")
public class HappyPatientsService {
    CassandraConnector connector = new CassandraConnector();
    private static final Logger logger = Logger.getLogger(HappyPatientsService.class);
    Properties properties = new java.util.Properties();  
	String property;
	Memcached cache;
	
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
			if(p.getStatus().getClass().equals(property)) {
				filtered.add(p);
			}
		}
	    	for (Patient p : filtered) {
	    		HashMap<String, String> map = new HashMap<>();
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
        		resultSet += p.getFirstName() + " " + p.getLastName() + "</br>";
        }
        return Response.status(200).entity(resultSet).build();
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
        connector.connect("127.0.0.1", 9042);
        Session session = connector.getSession();
        KeyspaceRepository sr = new KeyspaceRepository(session);
        sr.useKeyspace("hospitalOps");
        PatientPersonalInfo ppi = new PatientPersonalInfo(session);
        patient.setId();
        ppi.insertPatient(patient);
        connector.close();
        String output = "Welcome Patient : " + patient.getFirstName() + ". Here is your ID: " + patient.getId();
        return Response.status(200).entity(output).build();
    }

    @PUT
    @Path("/updatePatient/{patientId}")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response updatePatient(@PathParam("param") UUID patientId) throws IOException {
        logger.debug("Updating Patient info");
        String patientName = "";
        connector.connect("127.0.0.1", 9042);
        Session session = connector.getSession();
        KeyspaceRepository sr = new KeyspaceRepository(session);
        sr.useKeyspace("hospitalOps");
        Producer m = new Messager.Producer();
        m.sendMessage("Hello World!");
        AnalyticsConsumer consumer1 = new Messager.AnalyticsConsumer();
        EmailConsumer consumer2 = new Messager.EmailConsumer();
        consumer1.run();
        consumer2.run();
        PatientPersonalInfo ppi = new PatientPersonalInfo(session);
        ppi.updatePatient(patientId, patient);
        patientName = ppi.selectById(patient.getId()).getFirstName() + " " + ppi.selectById(patient.getId()).getLastName();
        connector.close();
        String output = "Patient info updated: " + patientName;
        return Response.status(200).entity(output).build();
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