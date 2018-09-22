package br.com.ischool.io.socket;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.logging.Level;
import java.util.logging.Logger;

import br.com.ischool.business.GenericServiceLocal;
import br.com.ischool.business.LoginServiceLocal;
import br.com.ischool.business.MensagemServiceLocal;
import br.com.ischool.entity.Aluno;
import br.com.ischool.entity.Mensagem;
import br.com.ischool.entity.Usuario;
import br.com.ischool.exceptions.ServicoException;
import br.com.ischool.security.Crypto;
import br.com.ischool.security.MD5CheckSum;
import br.com.ischool.util.Constantes;
import br.com.ischool.util.DadosUtil;
import br.com.ischool.util.TipoMensagem;


public class CLIENTConnection implements Runnable {

    private Socket clientSocket;    
	private LoginServiceLocal loginService;
	private MensagemServiceLocal mensagemService;
	private GenericServiceLocal generico;
	private String option;
	private Long bytesPosition;
	private Long idUsuario;
	private Long idMensagem;
	private Long dataAtualizacao;
	private String hashUsuario;
	
	
    public CLIENTConnection(Socket client,LoginServiceLocal loginService,GenericServiceLocal generico,MensagemServiceLocal mensagemService) {
        this.clientSocket = client;
        this.loginService = loginService;
        this.generico = generico;
        this.mensagemService = mensagemService;
    }

    @Override
    public void run() {
        try {        	
        	DataInputStream input = new DataInputStream(clientSocket.getInputStream());
        	
            byte[] buff  = new byte[input.readInt()]; 
            input.read(buff, 0, buff.length);
        	String dados[] = new String(Crypto.decrypt(buff)).split(";");
        	
        	option = dados[0];
        	bytesPosition = Long.valueOf(dados[1]);
        	idUsuario = Long.valueOf(dados[2]);
        	idMensagem = Long.valueOf(dados[3]);
        	dataAtualizacao = Long.valueOf(dados[4]);
        	hashUsuario = dados[5];
        	
            switch (option) {
                case "1":
                    receiveFile();
                    break;
                case "2":
                    sendFile();
                    break;
                default:
                    System.out.println("Incorrect command received.");
                    break;
            }
                      
        } catch (ServicoException e) {
          
        	try{
	           	DataOutputStream dos = new DataOutputStream(clientSocket.getOutputStream());
	            dos.writeUTF(e.getMessage());
        	}catch(Exception ex){
        	   ex.printStackTrace();
        	}
        	
        } catch (IOException e) {
            Logger.getLogger(CLIENTConnection.class.getName()).log(Level.SEVERE, null, e);
        } catch (Exception ex) {
            Logger.getLogger(CLIENTConnection.class.getName()).log(Level.SEVERE, null, ex);
        }finally{
        	 try {        		 
				if(!clientSocket.isClosed()){
	        		clientSocket.shutdownInput();
	        		clientSocket.shutdownOutput();
					clientSocket.close();
				}			
			} catch (IOException e) {
				e.printStackTrace();
			}       	 
        }
    }

    public void receiveFile() throws ServicoException, IOException {
    	
    	OutputStream output = null;
    	
    	try {   
            
    		Usuario us = loginService.validarAcessoMetodo(idUsuario,hashUsuario,dataAtualizacao);
                 
            DataInputStream clientData = new DataInputStream(clientSocket.getInputStream());

            Long idALuno = bytesPosition;
            Long idMensagemDevice = idMensagem;
 
                   
            File diretorio = new File(Constantes.LOCAL_SALVAR_ARQUIVO+File.separator+idALuno);
            
			if (!diretorio.exists()){
				
				boolean criouDiretorio = diretorio.mkdirs();
				
				if(!criouDiretorio){
					throw new ServicoException("ERRO_CRIAR_DIRETORIO");
				}
			}
			
            DataOutputStream dos = new DataOutputStream(clientSocket.getOutputStream());
            
            dos.writeUTF("OK");
            dos.flush();
            
            String fileName = clientData.readUTF();
            long size = clientData.readLong();
            String hashArq = clientData.readUTF();
			
			File dataFile = new File(diretorio,fileName);
			
            long totalRead = 0;
            
            if(dataFile.exists()){
            	totalRead = dataFile.length();
            }
              
            dos.writeLong(totalRead);
            dos.flush();
            
            size = size - totalRead;
            
            output = (totalRead==0) ? new FileOutputStream(dataFile) : new FileOutputStream(dataFile,true);        
           
            byte[] buffer = new byte[Constantes.TAMANHO_BUFFER];
           
            int bytesRead;           
            while (size > 0 && (bytesRead = clientData.read(buffer, 0, (int) Math.min(buffer.length, size))) != -1) {
                output.write(buffer, 0, bytesRead);
                size -= bytesRead;
            }
            
            output.flush();
            
          //TODO SEPARAR ESTE BLOCO NUM METODO
            try {
				String hashArquivo = MD5CheckSum.getMD5Checksum(dataFile.getAbsolutePath());
				
				if(hashArquivo.equals(hashArq)){		            
		            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
		    		Mensagem mensagem = new Mensagem();	
		    		
		    		// ID DO DEVICE E O ID DO DEVICE CONCATENADO COM O ID DA MENSAGEM GERADO NO DEVICE	(ISSO FOI FEITO PARA O ID SER UNICO POR DEVICE)
		    		mensagem.setIdDeviceMensagem(idMensagemDevice);

		    		mensagem.setLidoDevice(true);
		    		mensagem.setAluno(new Aluno());
		    		mensagem.getAluno().setId(idALuno);
		    		
		    		mensagem.setCliente(us.getCliente());
		    		
		    		mensagem.setUsuario(us);
		    		
		    		mensagem.setMensagem(dataFile.getName());
		    		mensagem.setHashArquivo(hashArquivo);
		    		mensagem.setTamanhoBytes(dataFile.length());
		    		mensagem.setCaminhoArquivo(dataFile.getAbsolutePath());
		    		
		    		mensagem = mensagemService.mergeMensagemDevice(mensagem);		
		    		mensagemService.enviarMensagemWeb(mensagem,true);
		    		mensagemService.enviarMensagemDevice(mensagem, TipoMensagem.TEXTO,false);
		    		
		            dos.writeUTF("OK");
		            dos.flush();
		    		
		    		//TODO REVER ISTO
		    		byte[] byteDados = Crypto.encrypt((mensagem.getId().toString()+";"+dateFormat.format(mensagem.getDataCadastro())).getBytes("UTF-8"));
		            
		    		dos.writeInt(byteDados.length);
		    		dos.write(byteDados);                 
		    		dos.flush();
		            
				}else{
					output.close();
					dataFile.delete();
					throw new ServicoException("ARQUIVO_CORROMPIDO");
				}
			} catch (Exception e) {
				throw new ServicoException("ERRO_INESPERADO");
			}


            System.out.println("File "+fileName+" received from client.");
        } catch (IOException ex) {
            System.err.println("Client error. Connection closed.");
        }finally{
        	try {
				if(output != null){
					output.close();
				}       
			} catch (IOException e) {
				e.printStackTrace();
			}
        }
    }

    public void sendFile() throws ServicoException, IOException {
    	
    	InputStream fis = null;  	
    	
        try {
	
        	loginService.validarAcessoMetodo(idUsuario,	hashUsuario,dataAtualizacao);
						
			Mensagem msg = generico.obterEntidade(idMensagem, Mensagem.class);
			
			if(DadosUtil.isEmpty(msg)){
				throw new ServicoException("MENSAGEM_NAO_ENCONTRADA");
			}
			
			if(DadosUtil.isEmpty(msg.getCaminhoArquivo())){
				throw new ServicoException("CAMINHO_ARQUIVO_NAO_VAZIO");
			}
					        	
            File myFile = new File(msg.getCaminhoArquivo());
            try{
            	fis = new FileInputStream(myFile);
            }catch(FileNotFoundException ex){
            	throw new ServicoException("ARQUIVO_NAO_ENCONTRADO");
            }
                  
            DataOutputStream dos = new DataOutputStream(clientSocket.getOutputStream());
          
            dos.writeUTF("OK");

            dos.writeLong(myFile.length());
            
            fis.skip(bytesPosition);
                        
            int read = 0;
            byte[] bytes = new byte[Constantes.TAMANHO_BUFFER];
          
            while ((read = fis.read(bytes)) != -1) {
            	dos.write(bytes, 0, read);
            }
        }finally{
        	try {
				if(fis != null){
		            fis.close();
				}       
			} catch (IOException e) {
				e.printStackTrace();
			}
        }
    }
    
    
    public void enviarMensagem(){

    }
}