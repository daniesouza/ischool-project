package br.com.ischool.webservice;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;

import br.com.ischool.business.ArquivoServiceLocal;
import br.com.ischool.business.EventoExecutadoServiceLocal;
import br.com.ischool.business.GenericServiceLocal;
import br.com.ischool.business.LoginServiceLocal;
import br.com.ischool.business.MensagemServiceLocal;
import br.com.ischool.business.NotificacaoServiceLocal;
import br.com.ischool.entity.Aluno;
import br.com.ischool.entity.ClasseCrypt;
import br.com.ischool.entity.Cliente;
import br.com.ischool.entity.Device;
import br.com.ischool.entity.DeviceRegId;
import br.com.ischool.entity.EventoCrypt;
import br.com.ischool.entity.EventoExeCrypt;
import br.com.ischool.entity.EventoExecutado;
import br.com.ischool.entity.Mensagem;
import br.com.ischool.entity.MensagemCrypt;
import br.com.ischool.entity.Usuario;
import br.com.ischool.entity.UsuarioCrypt;
import br.com.ischool.exceptions.ServicoException;
import br.com.ischool.security.Crypto;
import br.com.ischool.util.DadosUtil;
import br.com.ischool.util.TipoMensagem;
import br.com.ischool.webservice.IschoolWebServiceLocal;

/**
 * Session Bean implementation class MensagemWebServiceImpl
 */
@Stateless
@TransactionAttribute(TransactionAttributeType.NEVER)
@LocalBean
@WebService
public class IschoolWebServiceImpl implements IschoolWebServiceLocal {

	@EJB
	private MensagemServiceLocal mensagemService;
	
	@EJB
	private EventoExecutadoServiceLocal eventoExecutadoService;
	
	@EJB
	private NotificacaoServiceLocal notificacaoService;
	
	@EJB 
	private ArquivoServiceLocal arquivoService;
	
	@EJB
	private GenericServiceLocal generico;
	
	@EJB
	private LoginServiceLocal loginService;
	
    public IschoolWebServiceImpl() {
    }
    
   
	@Override
	@WebMethod
    public UsuarioCrypt loginDevice(@WebParam(name="encUsuario") final byte[] encUsuario,
    								@WebParam(name="encPass") final byte[] encPass,
    								@WebParam(name="deviceRegId") final byte[] deviceRegId,
    								@WebParam(name="strDeviceReg") final byte[] strDeviceReg,
    								@WebParam(name="tipoDevice") 	 final byte[] tipoDevice) throws ServicoException{
    	
    	if(DadosUtil.isEmpty(encUsuario)){
    		throw new ServicoException("USUARIO_NOT_NULL");
    	}    	
    	if(DadosUtil.isEmpty(encPass)){
    		throw new ServicoException("PASSWORD_NOT_NULL");
    	}    	
    	if(DadosUtil.isEmpty(deviceRegId) && DadosUtil.isEmpty(strDeviceReg)){
    		throw new ServicoException("DEVICE_REG_NOT_NULL");
    	}
    	if(DadosUtil.isEmpty(tipoDevice)){
    		throw new ServicoException("TIPO_DEVICE_NOT_NULL");
    	}
    	
    	String userName;
    	String md5pass;
    	DeviceRegId deviceReg;
    	Usuario usuario;
		try {
			userName = new String(Crypto.decrypt(encUsuario));
			md5pass = Crypto.removeSalt(new String(Crypto.decrypt(encPass)));	

			usuario = new Usuario();			
	    	usuario.setUsuario(userName);
	    	usuario.setSenha(md5pass);
			
			deviceReg = new DeviceRegId();			
			deviceReg.setId(Long.parseLong((new String(Crypto.decrypt(deviceRegId)))));
			deviceReg.setRegid((new String(Crypto.decrypt(strDeviceReg))));
			deviceReg.setDevice(new Device());
			deviceReg.getDevice().setId(Long.parseLong((new String(Crypto.decrypt(tipoDevice)))));
			
		} catch (NumberFormatException e) {
			throw new ServicoException(e.getMessage());
		} catch (Exception e) {
			throw new ServicoException(e.getMessage());
		}
    	
    	return loginService.loginDevice(usuario,deviceReg);

    }

    
	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	@WebMethod
	public byte[] enviarMensagemWeb(
			@WebParam(name="encIdUsuario") final byte[] encIdUsuario, 
			@WebParam(name="mensagemCrypt") final MensagemCrypt mensagemCrypt,
			@WebParam(name="encDataAtualizacao") final byte[] encDataAtualizacao,
			@WebParam(name="keyHash") final byte[] keyHash) throws ServicoException{
		
		try {
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
			Usuario usRemetente = loginService.validarAcessoMetodo(Long.parseLong(new String(Crypto.decrypt(encIdUsuario))),
																	new String(Crypto.decrypt(keyHash)),
																	Long.parseLong(new String(Crypto.decrypt(encDataAtualizacao))));
			
			Mensagem mensagem = new Mensagem();	
			
			// ID DO DEVICE E O ID DO DEVICE CONCATENADO COM O ID DA MENSAGEM GERADO NO DEVICE	(ISSO FOI FEITO PARA O ID SER UNICO POR DEVICE)
			mensagem.setIdDeviceMensagem(Long.parseLong(new String(Crypto.decrypt(mensagemCrypt.getIdDeviceMensagem()))));

			mensagem.setLidoDevice(true);
			mensagem.setAluno(new Aluno());
			mensagem.getAluno().setId(Long.parseLong(new String(Crypto.decrypt(mensagemCrypt.getIdAluno()))));
			
			mensagem.setCliente(new Cliente());
			mensagem.getCliente().setId(Long.parseLong(new String(Crypto.decrypt(mensagemCrypt.getIdCliente()))));
			
			mensagem.setUsuario(new Usuario());
			mensagem.getUsuario().setId(Long.parseLong(new String(Crypto.decrypt(mensagemCrypt.getIdUsuario()))));
			mensagem.getUsuario().setNome(usRemetente.getNome());
			mensagem.getUsuario().setAutoridade(usRemetente.getAutoridade());
			mensagem.getUsuario().setEmail(usRemetente.getEmail());
			mensagem.getUsuario().setAtivo(usRemetente.getAtivo());
			
			mensagem.setMensagem(new String(Crypto.decrypt(mensagemCrypt.getMensagem()),"UTF-8"));
			mensagem.setHashArquivo(DadosUtil.isEmpty(mensagemCrypt.getHashArquivo()) ? null : new String(Crypto.decrypt(mensagemCrypt.getHashArquivo())));
			mensagem.setTamanhoBytes(DadosUtil.isEmpty(mensagemCrypt.getTamanhoBytes()) ? null : Long.valueOf(new String(Crypto.decrypt(mensagemCrypt.getHashArquivo()))));
			
			mensagem = mensagemService.mergeMensagemDevice(mensagem);		
			mensagemService.enviarMensagemWeb(mensagem,true);
			mensagemService.enviarMensagemDevice(mensagem, TipoMensagem.TEXTO,false);
			
			//TODO REVER ISTO
			return Crypto.encrypt((mensagem.getId().toString()+";"+dateFormat.format(mensagem.getDataCadastro())).getBytes("UTF-8"));
									
		} catch (ServicoException e) {
			throw e;
		} catch (Exception e) {		
			throw new ServicoException(e.getMessage());
		}
		 		
	}
	
	@Override
	@WebMethod
	public byte[] carregarArquivo(@WebParam(name="encIdUsuario")  final byte[] encIdUsuario,
								  @WebParam(name="encIdMensagem") final byte[] encIdMensagem,
								  @WebParam(name="encOffset") 	  final byte[] encOffset,
								  @WebParam(name="encDataAtualizacao") final byte[] encDataAtualizacao,
								  @WebParam(name="keyHash") 	  final byte[] keyHash) throws ServicoException {
		
		try{	
			loginService.validarAcessoMetodo(Long.parseLong(new String(Crypto.decrypt(encIdUsuario))),
											new String(Crypto.decrypt(keyHash)),
											Long.parseLong(new String(Crypto.decrypt(encDataAtualizacao))));
			
			if(DadosUtil.isEmpty(encIdMensagem)){
				throw new ServicoException("ID_MENSAGEM_NAO_VAZIO");
			}
			if(DadosUtil.isEmpty(encOffset)){
				throw new ServicoException("OFFSET_NAO_VAZIO");
			}			
			
	    	Long idMensagem;
	    	Long offset;
			try {
				idMensagem = Long.parseLong(new String(Crypto.decrypt(encIdMensagem)));
				offset 		= Long.parseLong(new String(Crypto.decrypt(encOffset)));
			} catch (NumberFormatException e) {
				throw new ServicoException(e.getMessage());
			} catch (Exception e) {
				throw new ServicoException(e.getMessage());
			}
			
			Mensagem msg = generico.obterEntidade(idMensagem, Mensagem.class);
			
			if(DadosUtil.isEmpty(msg)){
				throw new ServicoException("MENSAGEM_NAO_ENCONTRADA");
			}
			
			if(DadosUtil.isEmpty(msg.getCaminhoArquivo())){
				throw new ServicoException("CAMINHO_ARQUIVO_NAO_VAZIO");
			}
					
			return arquivoService.carregarArquivo(msg.getCaminhoArquivo(),offset);
			
		} catch (ServicoException e) {
			throw e;
		} catch (Exception e) {		
			throw new ServicoException(e.getMessage());
		}
	}
	
	@Override
	@WebMethod
	public Collection<MensagemCrypt> listarMensagens(
			@WebParam(name="encIdUsuario") final byte[] encIdUsuario,
			@WebParam(name="encIdAluno") final byte[] encIdAluno,
			@WebParam(name="encIdsMensagens") final byte[] encIdsMensagens,
			@WebParam(name="encDataAtualizacao") final byte[] encDataAtualizacao,
			@WebParam(name="keyHash") final byte[] keyHash) throws ServicoException{
		
    	Long idAluno;
    	List<Long> listaIdsMensagensNOTIN;
		try {
			loginService.validarAcessoMetodo(Long.parseLong(new String(Crypto.decrypt(encIdUsuario))),
											new String(Crypto.decrypt(keyHash)),
											Long.parseLong(new String(Crypto.decrypt(encDataAtualizacao))));
			
			idAluno = Long.parseLong(new String(Crypto.decrypt(encIdAluno)));
			
			
			String[] listaIds = new String(Crypto.decrypt(encIdsMensagens)).split(",");
			
			listaIdsMensagensNOTIN = new ArrayList<Long>();
			
			for(String ids:listaIds){
				if(!ids.isEmpty()){
					listaIdsMensagensNOTIN.add(Long.valueOf(ids));				
				}
			}
			
			if(listaIdsMensagensNOTIN.isEmpty()){
				listaIdsMensagensNOTIN.add(0l);
			}
			
		} catch (NumberFormatException e) {
			throw new ServicoException(e.getMessage());
		} catch (Exception e) {
			throw new ServicoException(e.getMessage());
		}
			
		Aluno aluno = new Aluno();
		aluno.setId(idAluno);
		
		
		Collection<MensagemCrypt> listReturn = new ArrayList<MensagemCrypt>();
		
		Collection<Mensagem> list =  mensagemService.listarMensagensNaoLidosDevice(idAluno, listaIdsMensagensNOTIN, new Date());
		
		for(Mensagem msg:list){			
			
			MensagemCrypt msgCrypt = new MensagemCrypt();			
			try {
				msgCrypt.setIdAluno(Crypto.encrypt(msg.getAluno().getId().toString().getBytes("UTF-8")));			
				msgCrypt.setIdCliente(Crypto.encrypt(msg.getCliente().getId().toString().getBytes("UTF-8")));			
				msgCrypt.setIdUsuario(Crypto.encrypt(msg.getUsuario().getId().toString().getBytes("UTF-8")));
				msgCrypt.setNomeUsuario(Crypto.encrypt(msg.getUsuario().getNome().toString().getBytes("UTF-8")));
				msgCrypt.setLoginUsuario(Crypto.encrypt(msg.getUsuario().getUsuario().toString().getBytes("UTF-8")));
				msgCrypt.setIdWebMensagem(Crypto.encrypt(msg.getId().toString().getBytes("UTF-8")));
				msgCrypt.setIdDeviceMensagem(msg.getIdDeviceMensagem() == null ? null : Crypto.encrypt(msg.getIdDeviceMensagem().toString().getBytes("UTF-8")));
				msgCrypt.setMensagem(Crypto.encrypt(msg.getMensagem().getBytes("UTF-8")));
				msgCrypt.setDataCadastro(msg.getDataCadastro() == null ? null : Crypto.encrypt(msg.getDataCadastro().toString().getBytes("UTF-8")));
				msgCrypt.setHashArquivo(msg.getHashArquivo() == null ? null : Crypto.encrypt(msg.getHashArquivo().getBytes("UTF-8")));
				msgCrypt.setTamanhoBytes(msg.getTamanhoBytes() == null ? null : Crypto.encrypt(msg.getTamanhoBytes().toString().getBytes("UTF-8")));
				
				listReturn.add(msgCrypt);
			
			} catch (ServicoException e) {
				throw e;
			} catch (Exception e) {		
				throw new ServicoException(e.getMessage());
			}
			
		}
			
		return listReturn;
	}
	
	
	@Override
	@WebMethod
	public Collection<EventoExeCrypt> listarEventosExecutados(@WebParam(name="encIdUsuario") final byte[] encIdUsuario,@WebParam(name="encIdAluno") final byte[] encIdAluno,
			@WebParam(name="datasAtualizacao") final byte[] datasAtualizacao,
			@WebParam(name="encDataAtualizacao") final byte[] encDataAtualizacao,
			@WebParam(name="encDataPesquisa") final byte[] encDataPesquisa,
			@WebParam(name="keyHash") final byte[] keyHash) throws ServicoException{
		
    	Long idAluno;
    	List<Long> listaDatasAtualizacao;
    	Long dataPesquisa;
		try {
			
			loginService.validarAcessoMetodo(Long.parseLong(new String(Crypto.decrypt(encIdUsuario))),
					new String(Crypto.decrypt(keyHash)),
					Long.parseLong(new String(Crypto.decrypt(encDataAtualizacao))));
			
			dataPesquisa = Long.parseLong(new String(Crypto.decrypt(encDataPesquisa)));
			
			idAluno = Long.valueOf(new String(Crypto.decrypt(encIdAluno)));
			
			String[] listaIdsEventos = new String(Crypto.decrypt(datasAtualizacao)).split(",");
			
			listaDatasAtualizacao = new ArrayList<Long>();
			
			for(String ids:listaIdsEventos){
				if(!ids.isEmpty()){
					listaDatasAtualizacao.add(Long.valueOf(ids));				
				}
			}
			
			if(listaDatasAtualizacao.isEmpty()){
				listaDatasAtualizacao.add(0l);
			}

		} catch (NumberFormatException e) {
			throw new ServicoException(e.getMessage());
		} catch (Exception e) {
			throw new ServicoException(e.getMessage());
		}
			

		
		Collection<EventoExeCrypt> listReturn = new ArrayList<EventoExeCrypt>();

		
		Collection<EventoExecutado> list = eventoExecutadoService.listarEventosExecutadosNaoLidosDevice(idAluno, listaDatasAtualizacao, new Date(dataPesquisa));
		
			
		for(EventoExecutado evtExe:list){
			
			
			EventoExeCrypt evtExeCrypt = new EventoExeCrypt();			
			try {
			
				evtExeCrypt.setIdEventoExecutado(evtExe.getId() == null  ? null : Crypto.encrypt(evtExe.getId().toString().getBytes("UTF-8")));			
				evtExeCrypt.setIdAluno(evtExe.getAluno().getId() == null ? null : Crypto.encrypt(evtExe.getAluno().getId().toString().getBytes("UTF-8")));			
				
				evtExeCrypt.setIdCliente(evtExe.getCliente().getId() == null ? null : Crypto.encrypt(evtExe.getCliente().getId().toString().getBytes("UTF-8")));					
				evtExeCrypt.setIdUsuario(evtExe.getUsuario().getId() == null ? null : Crypto.encrypt(evtExe.getUsuario().getId().toString().getBytes("UTF-8")));
							
				evtExeCrypt.setObservacoes(evtExe.getObservacoes() == null ? null : Crypto.encrypt(evtExe.getObservacoes().getBytes("UTF-8")));				
				evtExeCrypt.setQuantidade(evtExe.getQuantidade() == null ? null : Crypto.encrypt(evtExe.getQuantidade().toString().getBytes("UTF-8")));
				evtExeCrypt.setDataCadastro(evtExe.getDataCadastro() == null ? null : Crypto.encrypt(evtExe.getDataCadastro().toString().getBytes("UTF-8")));
				evtExeCrypt.setPeriodoEvento(evtExe.getPeriodoEvento() == null ? null : Crypto.encrypt(evtExe.getPeriodoEvento().toString().getBytes("UTF-8")));
				
				evtExeCrypt.setTipo(evtExe.getTipo() == null ? null : Crypto.encrypt(evtExe.getTipo().toString().getBytes("UTF-8")));
				
				evtExeCrypt.setDataInicio(evtExe.getDataInicio() == null ? null : Crypto.encrypt(evtExe.getDataInicio().toString().getBytes("UTF-8")));
				evtExeCrypt.setDataFim(evtExe.getDataFim() == null ? null : Crypto.encrypt(evtExe.getDataFim().toString().getBytes("UTF-8")));
				evtExeCrypt.setAvaliacaoEvento(evtExe.getAvaliacaoEvento() == null ? null : Crypto.encrypt(evtExe.getAvaliacaoEvento().toString().getBytes("UTF-8")));
				evtExeCrypt.setTomouBanho(evtExe.getTomouBanho() == null ? null : Crypto.encrypt(evtExe.getTomouBanho().toString().getBytes("UTF-8")));
				
				evtExeCrypt.setMedicamentos(evtExe.getMedicamentos() == null ? null : Crypto.encrypt(evtExe.getMedicamentos().getBytes("UTF-8")));
				evtExeCrypt.setEnviarFralda(evtExe.getEnviarFralda() == null ? null : Crypto.encrypt(evtExe.getEnviarFralda().toString().getBytes("UTF-8")));
				evtExeCrypt.setEnviarLencos(evtExe.getEnviarLencos() == null ? null : Crypto.encrypt(evtExe.getEnviarLencos().toString().getBytes("UTF-8")));
				evtExeCrypt.setEnviarPomada(evtExe.getEnviarPomada() == null ? null : Crypto.encrypt(evtExe.getEnviarPomada().toString().getBytes("UTF-8")));
				evtExeCrypt.setEnviarLeite(evtExe.getEnviarLeite()   == null ? null : Crypto.encrypt(evtExe.getEnviarLeite().toString().getBytes("UTF-8")));
				evtExeCrypt.setEnviarOutros(evtExe.getEnviarOutros() == null ? null : Crypto.encrypt(evtExe.getEnviarOutros().getBytes("UTF-8")));
				evtExeCrypt.setIcone(evtExe.getIcone() == null ? null : Crypto.encrypt(evtExe.getIcone().getBytes("UTF-8")));
				evtExeCrypt.setStatusEventoExecutado(evtExe.getStatusEventoExecutado() == null ? null : Crypto.encrypt(evtExe.getStatusEventoExecutado().toString().getBytes("UTF-8")));
				evtExeCrypt.setDataAtualizacao(evtExe.getDataAtualizacao() == null ? null : Crypto.encrypt(evtExe.getDataAtualizacao().toString().getBytes("UTF-8")));
				
				
				EventoCrypt eventoCrypt = new EventoCrypt();
				
				eventoCrypt.setIdEvento(Crypto.encrypt(evtExe.getEvento().getId().toString().getBytes("UTF-8")));
				eventoCrypt.setAtivo(Crypto.encrypt(evtExe.getEvento().getAtivo().toString().getBytes("UTF-8")));
				eventoCrypt.setCodigoEvento(Crypto.encrypt(evtExe.getEvento().getCodigoEvento().getBytes("UTF-8")));
				eventoCrypt.setDataCadastro(Crypto.encrypt(evtExe.getEvento().getDataCadastro().toString().getBytes("UTF-8")));
				eventoCrypt.setIcone(Crypto.encrypt(evtExe.getEvento().getIcone().getBytes("UTF-8")));
				eventoCrypt.setNome(Crypto.encrypt(evtExe.getEvento().getNome().getBytes("UTF-8")));
				eventoCrypt.setPreCadastro(Crypto.encrypt(evtExe.getEvento().getPreCadastro().toString().getBytes("UTF-8")));
				eventoCrypt.setUnidadeMedida(Crypto.encrypt(evtExe.getEvento().getUnidadeMedida().getBytes("UTF-8")));
				
				evtExeCrypt.setEvento(eventoCrypt);				
				
				ClasseCrypt classeCrypt = new ClasseCrypt();

				classeCrypt.setIdClasse(Crypto.encrypt(evtExe.getClasse().getId().toString().getBytes("UTF-8")));
				classeCrypt.setAno(Crypto.encrypt(evtExe.getClasse().getAno().toString().getBytes("UTF-8")));
				classeCrypt.setAtivo(Crypto.encrypt(evtExe.getClasse().getAtivo().toString().getBytes("UTF-8")));
				classeCrypt.setCodigoClasse(Crypto.encrypt(evtExe.getClasse().getCodigoClasse().getBytes("UTF-8")));
				classeCrypt.setDataCadastro(Crypto.encrypt(evtExe.getClasse().getDataCadastro().toString().getBytes("UTF-8")));				
				classeCrypt.setNome(Crypto.encrypt(evtExe.getClasse().getNome().getBytes("UTF-8")));
				classeCrypt.setTurma(Crypto.encrypt(evtExe.getClasse().getTurma().getBytes("UTF-8")));
				
				evtExeCrypt.setClasse(classeCrypt);

				
				listReturn.add(evtExeCrypt);
			
			} catch (ServicoException e) {
				throw e;
			} catch (Exception e) {		
				throw new ServicoException(e.getMessage());
			}
			
		}
			
		return listReturn;
		
	}
	
		
}
