package br.com.ischool.io;

import java.io.IOException;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.ejb.Startup;

import br.com.ischool.business.GenericServiceLocal;
import br.com.ischool.business.LoginServiceLocal;
import br.com.ischool.business.MensagemServiceLocal;
import br.com.ischool.io.socket.FileServerSocket;
import br.com.ischool.io.websocket.IschoolWebSocketServer;

@Startup
@Singleton
public class SocketStarterListener{
	
	@EJB
	private LoginServiceLocal loginService;
	
	@EJB
	private GenericServiceLocal generico;
	
	@EJB
	private MensagemServiceLocal mensagemService;

	 
	@PreDestroy
	public void contextDestroyed() {
		try {
			IschoolWebSocketServer.stopWebSocket();
			FileServerSocket.stop();
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		}
	}
 
	@PostConstruct
	public void contextInitialized() {
		
		try {
			IschoolWebSocketServer.startWebSocket();
			FileServerSocket.start();
			FileServerSocket.loginService = loginService;
			FileServerSocket.generico = generico;
			FileServerSocket.mensagemService = mensagemService;
		} catch (InterruptedException | IOException e) {
			e.printStackTrace();
		}
	}

}
