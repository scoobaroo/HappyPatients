package com.sjsu.cs249.HappyPatients;

import com.datastax.driver.core.Session;
import com.datastax.driver.core.utils.UUIDs;
import com.sjsu.cs249.Services.Messager;
import com.sjsu.cs249.Services.Messager.Producer;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.List;
import java.util.UUID;


public class CassandraInitializer {
    private static final Logger logger = Logger.getLogger(CassandraInitializer.class);
    public static void main(String args[]) throws IOException
    {
        CassandraConnector connector = new CassandraConnector();
        connector.connect("127.0.0.1", 9042);
        Session session = connector.getSession();
        KeyspaceRepository sr = new KeyspaceRepository(session);
        sr.createKeyspace("hospitalOps", "SimpleStrategy", 1);
        sr.useKeyspace("hospitalOps");
        PatientPersonalInfo ppi = new PatientPersonalInfo(session);
//        ppi.deleteTable("PatientPersonalInfo");
        ppi.createTablePatients();
        Patient patient = new Patient(UUIDs.timeBased(), "John", "Doe", "1992-09-22", "555 SJSU drive San Jose, CA", "408-555-5555","ICU" , "Cancer", "Chemo");
        ppi.insertPatient(patient);
        Patient patient2 = new Patient(UUIDs.timeBased(), "Eric", "Han", "1985-02-21", "1256 Stevens Creek Blvd, Cupertino, CA", "408-609-8811","Observation","Cancer", "Chemo");
        ppi.insertPatient(patient2);
        Patient patient3 = new Patient(UUIDs.timeBased(), "Mayuri", "Wadkar", "1994-03-24", "1998 Brokaw Rd, San Jose, CA", "408-342-9921","Ongoing","Cancer", "Chemo");
        ppi.insertPatient(patient3);
        Patient patient4 = new Patient(UUIDs.timeBased(), "Sonali", "Mishra", "1985-11-01", "Mumbai Blvd, India, IN", "668-952-3353", "Urgent Care","Cancer", "Chemo");
        ppi.insertPatient(patient4);
        List<Patient> patients = ppi.selectAll();
        for(Patient p : patients) {
        		System.out.println(p.toString());
        		System.out.println("-------------------------------------------");
        }
        Messager.thread(new Producer(), false);
        Messager.thread(new Producer(), false);
        Messager.thread(new Messager.EmailConsumer(), false);
        Messager.thread(new Messager.AnalyticsConsumer(), false);
        connector.close();
    }
}
