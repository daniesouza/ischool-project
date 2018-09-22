package br.com.ischool.gcm;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Post2Gcm {
	public static List<Long> post(String apiKey, Content content,List<Long> deviceIds) throws Exception {
		
			URL url = new URL("https://android.googleapis.com/gcm/send");
			
			System.out.println("\nEnviando mensagem 'POST' para request URL:" + url);
			
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			
			conn.setRequestMethod("POST");
			
			conn.setRequestProperty("Content-Type", "application/json");
			conn.setRequestProperty("Authorization", "key="+apiKey);
			
			conn.setDoOutput(true);
			
			ObjectMapper mapper = new ObjectMapper();
			
			mapper.setVisibility(PropertyAccessor.FIELD, Visibility.ANY);
			
			DataOutputStream wr = new DataOutputStream(conn.getOutputStream());
			
			mapper.writeValue(wr, content);
			
			wr.flush();	
			wr.close();
			
			int responseCode = conn.getResponseCode();
			
			if(responseCode != 200){// success
				throw new Exception("ERRO_ENVIAR_MENSAGEM PARA DEVICE");
			}
			
			System.out.println("Response Code: " + responseCode);
			
			BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			String inputLine;
			StringBuffer response = new StringBuffer();
			
			while((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			
			in.close();

			
			final JsonNode arrNode = new ObjectMapper().readTree(response.toString());
			
			System.out.println(response.toString());
			
			/*
			 * ESTE BLOCO ABAIXO SERVE PARA VALIDAR DUPLICIDADES DE DEVICE REG ID NO MESMO DEVICE.
			 * SE RETORNAR UM canonical_ids maior que zero.. O CODIGO ABAIXO RETORNARA UMA LISTA PARA substituir/REMOVER regids duplicados..
			 * 
			 * o funcionamneto do algoritmo abaixo e.
			 * os INDEX da lista do google que voltar com registration_id devem ser pego na lista deviceIds exatamente no mesmo index.
			 */
			
			int canonicalIds = arrNode.get("canonical_ids").intValue();
			final JsonNode results = arrNode.get("results");
			
			List<Long> idsDeletar = null;
			if(canonicalIds > 0){

				if (results.isArray()) {
					int index = 0;
				    for (final JsonNode objNode : results) {			    	
				    	if(objNode.get("registration_id") != null){
				    		
				    		if(idsDeletar == null){
				    			idsDeletar = new ArrayList<Long>();
				    		}
				    		
				    		idsDeletar.add(deviceIds.get(index));			    		
					        System.out.println("Substituir "+deviceIds.get(index) +" por "+objNode.get("registration_id").textValue());			       
				    	}
				    	
				    	index++;
				    	
				    }
				}
			}
			

			/*
			 * ESTE BLOCO ABAIXO SERVE PARA APAGAR OS DEVICES QUE NAO ESTAO MAIS REGISTRADOS NO GOOGLE.
			 * SE RETORNAR UM error: NotRegistered .. O CODIGO ABAIXO RETORNARA UMA LISTA PARA REMOVER regids duplicados..
			 * 
			 * o funcionamneto do algoritmo abaixo e.
			 * os INDEX da lista do google que voltar com registration_id devem ser pego na lista deviceIds exatamente no mesmo index.
			 */
			
			if (results.isArray()) {
				int index = 0;
			    for (final JsonNode objNode : results) {			    	
			    	if(objNode.get("error") != null){
			    		
			    		String msg = objNode.get("error").textValue();
			    		
			    		if(msg.equals("NotRegistered")){
			    						    		
				    		if(idsDeletar == null){
				    			idsDeletar = new ArrayList<Long>();
				    		}
				    		
				    		idsDeletar.add(deviceIds.get(index));			    		
					        System.out.println("DELETAR DEVICE NAO REGISTRADO NO GOOGLE "+deviceIds.get(index));		
			    		}			    		  
			    	}
			    	
			    	index++;
			    	
			    }
			}
							
		
		return idsDeletar;

	}
}
