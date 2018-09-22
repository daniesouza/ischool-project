package br.com.ischool.io.websocket;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.java_websocket.WebSocket;
import org.java_websocket.WebSocketImpl;
import org.java_websocket.framing.Framedata;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import br.com.ischool.entity.Usuario;
import br.com.ischool.security.Crypto;
import br.com.ischool.util.Constantes;
import br.com.ischool.util.DadosUtil;

/**
 * A simple WebSocketServer implementation. Keeps track of a "chatroom".
 */
public class IschoolWebSocketServer extends WebSocketServer{
	
	
	public static IschoolWebSocketServer instance;
	
	private static Map<WebSocket,String> mapaUsuariosConectados = new HashMap<WebSocket,String>();

	public IschoolWebSocketServer( int port ) throws UnknownHostException {
		super( new InetSocketAddress( port ) );
	}

	public IschoolWebSocketServer( InetSocketAddress address ) {
		super( address );
	}

	@Override
	public void onOpen( WebSocket conn, ClientHandshake handshake ) {
	//	System.out.println( conn.getRemoteSocketAddress().getAddress().getHostAddress() + " entered!" );
	}

	@Override
	public void onClose( WebSocket conn, int code, String reason, boolean remote ) {
		System.out.println( conn + " Saiu!" );
		mapaUsuariosConectados.remove(conn);
	}

	@Override
	public void onMessage( WebSocket conn, String message ) {
		System.out.println( conn + ": " + message );
		
		if(message != null){
			
			try{					
				mapaUsuariosConectados.put(conn, message);
			}catch(Exception ex){
				conn.send( "error: Falha na conexão. não foi possivel identificar o usuario." );
				conn.close(1);
			}

		}
	}
	
	@Override
	public void onMessage( WebSocket conn, ByteBuffer message ) {
		System.out.println( conn + ": " + message );
	}

	@Override
	public void onFragment( WebSocket conn, Framedata fragment ) {
		System.out.println( "received fragment: " + fragment );
	}

	@Override
	public void onError( WebSocket conn, Exception ex ) {
		ex.printStackTrace();
		if( conn != null ) {
			// some errors like port binding failed may not be assignable to a specific websocket
			
			System.out.println( "Error: " + ex.getMessage());
		}
	}

	/**
	 * Sends <var>text</var> to all currently connected WebSocket clients.
	 * 
	 * @param text
	 *            The String to send across the network.
	 * @throws InterruptedException
	 *             When socket related I/O errors occur.
	 */
	public void sendToAll( String text ) {
		Collection<WebSocket> con = connections();
		synchronized ( con ) {
			for( WebSocket c : con ) {
				c.send( text );
			}
		}
	}
	
	public static void sendMessage(List<Usuario> usuarios,String message){
		
		synchronized(usuarios){
			
			for(Usuario user:usuarios){
				
				Iterator<WebSocket> it = mapaUsuariosConectados.keySet().iterator();
							
				while (it.hasNext()) {
					WebSocket conn = (WebSocket) it.next();
					
					String id = mapaUsuariosConectados.get(conn);
					
					if(id.equals(Crypto.encriptSenhaMD5(Constantes.FIXSALT+user.getId().toString()))){
						conn.send(message);
					//	break;
					}
				}

			}
		}

	}
	
	
	public static void startWebSocket() throws InterruptedException , IOException{
				
			WebSocketImpl.DEBUG = Constantes.WEBSOCKET_DEBUG;		
						
			if(Constantes.USE_INTERNET_WEBSOCKET_IP){
				//Constantes.WEBSOCKET_IP = getInternetIp();
				Constantes.WEBSOCKET_IP = "ischool.noip.me";
			}else{
				Constantes.WEBSOCKET_IP = getLocalIp();
			}
			
			if(DadosUtil.isEmpty(Constantes.WEBSOCKET_IP)){
				throw new IOException("Error obtaining ip address");
			}
			
			InetSocketAddress address = new InetSocketAddress(getLocalIp(),Constantes.WEBSOCKET_PORT);

			instance = new IschoolWebSocketServer( address );
			instance.start();
			System.out.println( "SERVER SOCKET started on " +instance.getAddress().getHostName() +":"+instance.getPort());
	
	}
	
	public static void stopWebSocket() throws InterruptedException , IOException{
		instance.stop();
	}
	
	public static String getLocalIp(){
		
		String ip = null;
		
		try {
			InetAddress inetAddress = InetAddress.getLocalHost();
			ip = inetAddress.getHostAddress();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		System.out.println("Current LAN IP address : " + ip);
		
		return ip;
	}
	
	public static String getInternetIp(){
		
		String ip = null;
		  try {
	 		
		      URL whatismyip = new URL("http://checkip.amazonaws.com");
		      BufferedReader in = null;
		        try {
		            in = new BufferedReader(new InputStreamReader(whatismyip.openStream()));
		            ip = in.readLine();
		            System.out.println("Current internet address : " + ip);
		        } catch (IOException e) {
					e.printStackTrace();
				} finally {
		            if (in != null) {
		                try {
		                    in.close();
		                } catch (IOException e) {
		                    e.printStackTrace();
		                }
		            }
		        }
		    
		  } catch (MalformedURLException e1) {
			e1.printStackTrace();
		}
		return ip;
	
	}
	
	public static void main( String[] args ) throws InterruptedException , IOException {
		WebSocketImpl.DEBUG = false;
		int port = 8444; // 843 flash policy port
		
		InetSocketAddress address = new InetSocketAddress(getLocalIp(),port);

		IschoolWebSocketServer s = new IschoolWebSocketServer( address );
		s.start();
		System.out.println( "SERVER SOCKET started on " +s.getAddress().getHostName() +":"+s.getPort());

//		BufferedReader sysin = new BufferedReader( new InputStreamReader( System.in ) );
//		while ( true ) {
//			String in = sysin.readLine();
//			s.sendToAll( in );
//			if( in.equals( "exit" ) ) {
//				s.stop();
//				break;
//			} else if( in.equals( "restart" ) ) {
//				s.stop();
//				s.start();
//				break;
//			}
//		}
	}
}
