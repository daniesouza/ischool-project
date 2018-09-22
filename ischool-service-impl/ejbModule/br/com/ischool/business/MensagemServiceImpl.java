package br.com.ischool.business;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.EJB;
import javax.ejb.Local;
import javax.ejb.Stateless;

import br.com.ischool.dao.MensagemDAOLocal;
import br.com.ischool.entity.Aluno;
import br.com.ischool.entity.Classe;
import br.com.ischool.entity.DeviceRegId;
import br.com.ischool.entity.Mensagem;
import br.com.ischool.entity.Notificacao;
import br.com.ischool.entity.Usuario;
import br.com.ischool.exceptions.DAOException;
import br.com.ischool.exceptions.ServicoException;
import br.com.ischool.gcm.Content;
import br.com.ischool.gcm.Post2Gcm;
import br.com.ischool.io.websocket.IschoolWebSocketServer;
import br.com.ischool.util.Constantes;
import br.com.ischool.util.DadosUtil;
import br.com.ischool.util.Paginacao;
import br.com.ischool.util.TipoMensagem;

/**
 * @author Daniel Souza de lima e-mail:daniesouza@gmail.com
 * Session Bean implementation class MensagemServiceImpl
 */
@Stateless
@Local(value=MensagemServiceLocal.class)
public class MensagemServiceImpl implements MensagemServiceLocal {

    /**
     * Default constructor. 
     */
    public MensagemServiceImpl() {
        // TODO Auto-generated constructor stub
    }
    
	@EJB
	private MensagemDAOLocal mensagemDAO;
	
	@EJB
	private GenericServiceLocal generico;

	@EJB
	private NotificacaoServiceLocal notificacaoService;
	
	@EJB
	private DeviceRegServiceLocal deviceService;
	
    @PostConstruct
    private void carregarInformacoes(){
    	System.out.println("CARREGADO OS RECURSOS DO EJB "+this.getClass().getName());
    }
    
    @PreDestroy
    private void clear(){
    	System.out.println("LIBERANDO OS RECURSOS DO EJB "+this.getClass().getName());
    }
    
    
	@Override
	public Mensagem mergeMensagemDevice(Mensagem mensagem) throws ServicoException {
		
		try {
			
			mensagem.setDataCadastro(new Date());
			
			validarMensagem(mensagem);
			
			if (DadosUtil.isEmpty(mensagem.getIdDeviceMensagem())) {
				throw new ServicoException("ID_DEVICE_MSG_NAO_VAZIO");
			}
			
			Mensagem msg = mensagemDAO.pesquisarMensagemDeviceId(mensagem.getIdDeviceMensagem());
			
			if(!DadosUtil.isEmpty(msg)){
				msg.setDataCadastro(mensagem.getDataCadastro());
				mensagemDAO.alterarMensagem(msg);
				
				return msg;
			}else{
				mensagem.setId(null);
				mensagemDAO.salvarMensagem(mensagem);
				
				return mensagem;
			}
		
					
		} catch (DAOException e) {			
			throw new ServicoException(e);			
		} catch (ServicoException e) {
			throw e;
		} catch (Exception e) {		
			throw new ServicoException(e);
		}
					
	}
    
	
	@Override
	public void salvarMensagem(Mensagem mensagem) throws ServicoException {
		
		try {
			
			mensagem.setDataCadastro(new Date());
			
			validarMensagem(mensagem);
			
			mensagemDAO.salvarMensagem(mensagem);
					
		} catch (DAOException e) {			
			throw new ServicoException(e);			
		} catch (ServicoException e) {
			throw e;
		} catch (Exception e) {		
			throw new ServicoException(e);
		}
					
	}

	
	private void validarMensagem(Mensagem mensagem) throws ServicoException {
		ServicoException se = new ServicoException();
		
		
		if (DadosUtil.isEmpty(mensagem)) {
			se.adicionarMensagem("MENSAGEM_NAO_VAZIO");
			throw se;
		}

		if (DadosUtil.isEmpty(mensagem.getMensagem())) {
			se.adicionarMensagem("MENSAGEM_NAO_VAZIO");
		}
		
		if (DadosUtil.isEmpty(mensagem.getDataCadastro())) {
			se.adicionarMensagem("DATA_CADASTRO_NAO_VAZIO");
		}
				
		if (DadosUtil.isEmpty(mensagem.getAluno()) || DadosUtil.isEmpty(mensagem.getAluno().getId())) {
			se.adicionarMensagem("ALUNO_NAO_VAZIO");
		}
		
		if (DadosUtil.isEmpty(mensagem.getUsuario()) || DadosUtil.isEmpty(mensagem.getUsuario().getId())) {
			se.adicionarMensagem("USUARIO_NAO_VAZIO");
		}
		
		if (DadosUtil.isEmpty(mensagem.getCliente()) || DadosUtil.isEmpty(mensagem.getCliente().getId())) {
			se.adicionarMensagem("CLIENTE_NAO_VAZIO");
		}

		if (se.existeErro()) {
			throw se;
		}
	}
	


	@Override
	public void alterarMensagem(Mensagem Mensagem) throws ServicoException {
		
		try {
			validarMensagem(Mensagem);
			
			mensagemDAO.alterarMensagem(Mensagem);
			
			
		} catch (DAOException e) {			
			throw new ServicoException(e);			
		} catch (ServicoException e) {
			throw e;
		} catch (Exception e) {		
			throw new ServicoException(e);
		}
					
	}

	@Override
	public Collection<Mensagem> listarMensagems() throws ServicoException {
		
		try {
			
			return mensagemDAO.listarMensagems();
			
		} catch (DAOException e) {			
			throw new ServicoException(e);			
		} catch (Exception e) {		
			throw new ServicoException(e);
		}
	}

	@Override
	public Collection<Mensagem> listarMensagems(Mensagem filtro,Paginacao paginacao) throws ServicoException {

		try {
			
			if(DadosUtil.isEmpty(paginacao)){
				paginacao = new Paginacao(-1);
			}
			
			return mensagemDAO.listarMensagems(filtro,paginacao);
			
		} catch (DAOException e) {			
			throw new ServicoException(e);			
		} catch (Exception e) {		
			throw new ServicoException(e);
		}
	}
	

	@Override
	public void excluirMensagem(Mensagem Mensagem) throws ServicoException {
		
		try {
			
			mensagemDAO.excluirMensagem(Mensagem);
			
		} catch (DAOException e) {			
			throw new ServicoException(e);			
		} catch (Exception e) {		
			throw new ServicoException(e);
		}
		
	}

	@Override
	public Mensagem selectById(Long id) throws ServicoException {
		
		try {		
			return mensagemDAO.selectById(id);
			
		} catch (DAOException e) {			
			throw new ServicoException(e);			
		} catch (Exception e) {		
			throw new ServicoException(e);
		}
	}


	@Override
	public void enviarMensagemDevice(final Mensagem mensagem,final TipoMensagem tipoMensagem,final boolean enviaRemetente) throws ServicoException{
		
		validarMensagem(mensagem);
		
		
	    Runnable enviaMensagemRunnable = new Runnable(){

	        @Override
	        public void run(){
	        	try {
	            
	        		Content content = new Content();
	        		List<Long> idsDevice = new ArrayList<Long>();
	        		
		        	Usuario usuarioRemetente = mensagem.getUsuario();
		            	        	
					Aluno alunoEnv = generico.obterListaLazy(mensagem.getAluno(), Aluno.class, "usuarios");
	    
		            for(Usuario usuario:alunoEnv.getUsuarios()){
	           
		            	if(usuarioRemetente.getId().equals(usuario.getId()) && !enviaRemetente){
		            		continue;
		            	}
						usuario = generico.obterListaLazy(usuario, Usuario.class, "devicesRegistrados");
	
			            List<DeviceRegId> deviceRegs = usuario.getDevicesRegistrados();
			            for(DeviceRegId device:deviceRegs){
			            	
			                if(device.getDevice().getId() == Constantes.DEVICE_ANDROID){
			                    String registration_id = device.getRegid();	
			                    idsDevice.add(device.getId());
			                    content.addRegId(registration_id);
			                }
			            } 
			        }
		            
		            if(!idsDevice.isEmpty()){
			            
		            	String msg = mensagem.getMensagem();
		            	
//						if(msg.length()>28){
//							msg = msg.substring(0,28);
//						}
		            	
		            	content.putData("serverMsg", msg);
			            content.putData("idEventoExec",mensagem.getIdEventoExec() == null ? "" : mensagem.getIdEventoExec().toString());
			            content.putData("aluno_id", alunoEnv.getId().toString());
			           // content.putData("usuario_id",usuario.getId().toString());
			            content.putData("tipoMsg", tipoMensagem.toString());
	                   
			            List<Long> idsDeletar =  Post2Gcm.post(Constantes.API_KEY, content,idsDevice);
			            
			            if(idsDeletar != null){			            	
			            	deviceService.deleteByIds(idsDeletar);	            	
			            }
		            }
                                
				} catch (ServicoException e) {
					e.printStackTrace();
					System.out.println("NÃO ENVIOU MENSAGEM: "+e.getMessage());
				} catch (Exception e) {		
					e.printStackTrace();
					System.out.println("NÃO ENVIOU MENSAGEM: "+e.getMessage());
				}
			}  

	    };
	    
	    
        Thread t = new Thread(enviaMensagemRunnable);
        t.start();
		
	}
	
	@Override
	public void enviarMensagensDevice(final Collection<Mensagem> mensagens,final TipoMensagem tipoMensagem) throws ServicoException{
		
		for(Mensagem mensagem:mensagens){
			validarMensagem(mensagem);
		}
		
	    Runnable enviaMensagemRunnable = new Runnable(){

	        @Override
	        public void run(){
	        	try {
	        		
	        		Content content = new Content();
            	            
		            for(Mensagem mensagem:mensagens){
	
		            	List<Long> idsDevice = new ArrayList<Long>();
						Aluno alunoEnv = generico.obterListaLazy(mensagem.getAluno(), Aluno.class, "usuarios");
		    
			            for(Usuario usuario:alunoEnv.getUsuarios()){
		           
							usuario = generico.obterListaLazy(usuario, Usuario.class, "devicesRegistrados");
		
				            Collection<DeviceRegId> deviceRegs = usuario.getDevicesRegistrados();
				            for(DeviceRegId device:deviceRegs){
				                if(device.getDevice().getId() == Constantes.DEVICE_ANDROID){
				                    String registration_id = device.getRegid();
				                    idsDevice.add(device.getId());
				                    content.addRegId(registration_id);
								}
							}
						}
		            
			            if(!idsDevice.isEmpty()){
			            	
			            	String msg = mensagem.getMensagem();
			            	
							if(msg.length()>28){
								msg = msg.substring(0,28);
							}
					          
			            	content.putData("serverMsg", msg);
				            content.putData("idEventoExec",mensagem.getIdEventoExec() == null ? "" : mensagem.getIdEventoExec().toString());
				            content.putData("aluno_id", alunoEnv.getId().toString());
				           // content.putData("usuario_id",usuario.getId().toString());
				            content.putData("tipoMsg", tipoMensagem.toString());
		                   
				            List<Long> idsDeletar =  Post2Gcm.post(Constantes.API_KEY, content,idsDevice);
				            
				            if(idsDeletar != null){				            	
					           deviceService.deleteByIds(idsDeletar);	            					            			            	
				            }
			            }			            
					}
		            
				} catch (ServicoException e) {
					e.printStackTrace();
					System.out.println("NÃO ENVIOU MENSAGEM: "+e.getMessage());
				} catch (Exception e) {		
					e.printStackTrace();
					System.out.println("NÃO ENVIOU MENSAGEM: "+e.getMessage());
				}
			}  

	    };
	    
	    
        Thread t = new Thread(enviaMensagemRunnable);
        t.start();
		
	}
	
	
	@Override
	public void enviarMensagemWeb(final Mensagem mensagem,boolean enviaRemetente) throws ServicoException{
				
		try {
			
			validarMensagem(mensagem);
			
			mensagem.setAluno((Aluno) generico.obterEntidade(mensagem.getAluno().getId(), Aluno.class));

				
			List<Classe> listaClasses = mensagem.getAluno().getClasses();
			
			List<Usuario> listaUsuariosEnviarNot = new ArrayList<Usuario>();
			//TODO TALVEZ JOGAR NUMA THREAD			
			for(Classe classe:listaClasses){	
	
				// FOR PARA USUARIOS ASSOCIADOS A CLASSE... professores.. diretores.. etc
 				for(Usuario usuario:classe.getUsuarios()){
					
 					if(usuario.getId().equals(mensagem.getUsuario().getId())  && !enviaRemetente){
 						continue;
 					}
 					
					Notificacao filtro = new Notificacao();			
					filtro.setUsuario(usuario);
 					filtro.setAluno(mensagem.getAluno());
					
					List<Notificacao> listaNotificacoes = (List<Notificacao>) notificacaoService.listarNotificacoes(filtro, null);
					
					Notificacao notificacao = listaNotificacoes.isEmpty() ? new Notificacao() : listaNotificacoes.get(0);
					
					notificacao.setAluno(filtro.getAluno());
					notificacao.setUsuario(filtro.getUsuario());
					notificacao.setNomeClasse(classe.getNome());
					notificacao.setNomeRemetente(mensagem.getUsuario().getNome());
					
					if(notificacao.getQuantidadeNotificacoes() == null){
						notificacao.setQuantidadeNotificacoes(0);
					}
					
					notificacao.setQuantidadeNotificacoes(notificacao.getQuantidadeNotificacoes() + 1);
					
					notificacaoService.alterarNotificacao(notificacao);
					
					listaUsuariosEnviarNot.add(usuario);	

				}
 				
				// FOR PARA RESPONSAVEIS PELO ALUNO
 				for(Usuario usuario:mensagem.getAluno().getUsuarios()){
					
 					if(usuario.getId().equals(mensagem.getUsuario().getId())  && !enviaRemetente){
 						continue;
 					}
 					
					Notificacao filtro = new Notificacao();			
					filtro.setUsuario(usuario);
 					filtro.setAluno(mensagem.getAluno());
					
					List<Notificacao> listaNotificacoes = (List<Notificacao>) notificacaoService.listarNotificacoes(filtro, null);
					
					Notificacao notificacao = listaNotificacoes.isEmpty() ? new Notificacao() : listaNotificacoes.get(0);
					
					notificacao.setAluno(filtro.getAluno());
					notificacao.setUsuario(filtro.getUsuario());
					notificacao.setNomeClasse(classe.getNome());
					notificacao.setNomeRemetente(mensagem.getUsuario().getNome());
					
					if(notificacao.getQuantidadeNotificacoes() == null){
						notificacao.setQuantidadeNotificacoes(0);
					}
					
					notificacao.setQuantidadeNotificacoes(notificacao.getQuantidadeNotificacoes() + 1);
					
					notificacaoService.alterarNotificacao(notificacao);
					
					listaUsuariosEnviarNot.add(usuario);	

				}
				
			}
			
			
			IschoolWebSocketServer.sendMessage(listaUsuariosEnviarNot, mensagem.getAluno().getId().toString());
			
					
		} catch (ServicoException e) {
			throw e;
		} catch (Exception e) {		
			throw new ServicoException(e);
		}
	}

	@Override
	public void salvarMensagens(Collection<Mensagem> mensagens)	throws ServicoException {
		
		if(mensagens == null || mensagens.isEmpty()){
			throw new ServicoException("MENSAGEM_NAO_VAZIO");
		}
		
		for(Mensagem mensagem:mensagens){			
			salvarMensagem(mensagem);
		}
	}

	@Override
	public Collection<Mensagem> listarMensagensNaoLidosDevice(Long idAluno,
			Collection<Long> idsNOTIN, Date dataCadastro)
			throws ServicoException {
		try {
			
			return mensagemDAO.listarMensagensNaoLidosDevice(idAluno,idsNOTIN, dataCadastro);
		
		} catch (DAOException e) {			
			throw new ServicoException(e);			
		} catch (Exception e) {		
			throw new ServicoException(e);
		}
	}	
	

}
