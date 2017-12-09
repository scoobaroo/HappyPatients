package com.sjsu.cs249.Services;
import net.spy.memcached.MemcachedClient;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;

import com.sjsu.cs249.HappyPatients.Patient;

public class Memcached {
	public String memcachedHost = "localhost";
	public int memcachedPort = 11211; //by default - 11211
	public MemcachedClient memcachedClient;
	 
	public void start() throws IOException {
		memcachedClient = new MemcachedClient(new InetSocketAddress(memcachedHost, memcachedPort));
		// Connecting to Memcached server on localhost
	}
	
	public void putContentInCache(String id, HashMap m) {
		System.out.println(id);
		System.out.println(m);
		memcachedClient.set(id, 200, m);
	}
		 
	public Map getContentInCache(String id) {
		return (Map) memcachedClient.get(id);
	}
		 
	public void deleteContentFromCache(String id) {
		memcachedClient.delete(id);
	}
}