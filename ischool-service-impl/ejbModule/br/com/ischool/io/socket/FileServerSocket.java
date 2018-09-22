package br.com.ischool.io.socket;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import br.com.ischool.business.GenericServiceLocal;
import br.com.ischool.business.LoginServiceLocal;
import br.com.ischool.business.MensagemServiceLocal;
import br.com.ischool.util.Constantes;

public class FileServerSocket implements Runnable{

    private static ServerSocket serverSocket;
    private static Socket clientSocket = null;
    private static FileServerSocket instance;
    private static Thread thread;
    
    public static  LoginServiceLocal loginService;
    public static  GenericServiceLocal generico;
    public static  MensagemServiceLocal mensagemService;

    public static void start() throws IOException,InterruptedException {
    	
    	if(instance == null){
    		instance = new FileServerSocket();
    	}
    	
    	thread = new Thread(instance);
    	thread.start();
    }
    
    
    public static void stop() throws IOException,InterruptedException{
    	serverSocket.close();
    	clientSocket.close();   	
    	thread.interrupt();
    }
    
    


	@Override
	public void run() {
        try {       	
            serverSocket = new ServerSocket(Constantes.FILESOCKET_PORT);
            System.out.println("Servidor de arquivos iniciado na porta "+Constantes.FILESOCKET_PORT);
        } catch (Exception e) {
            System.err.println("Porta em uso.");
        }

        while (true) {
            try {
                clientSocket = serverSocket.accept();
                System.out.println("Accepted connection : " + clientSocket);

                Thread t = new Thread(new CLIENTConnection(clientSocket,loginService,generico,mensagemService));

                t.start();

            } catch (Exception e) {
                System.err.println("Error in connection attempt.");
            }
        }
		
	}
}
