package br.com.ischool.gcm;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.io.Serializable;
import java.util.Map;

public class Content implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 4180852319657919190L;
	
	private List<String> registration_ids;
	private Map<String, String> data;
	
	public void addRegId(String regId) {
		if(registration_ids == null) {
			registration_ids = new LinkedList<String>();
		}
		
		registration_ids.add(regId);
	} 
	
	public void putData(String field, String value) {
		if(data == null) {
			data = new HashMap<String, String>();
		}
		
		data.put(field, value);

	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	
	
}
