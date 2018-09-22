package br.com.ischool.controller;

import java.io.File;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import br.com.ischool.business.ArquivoServiceLocal;
import br.com.ischool.business.GenericServiceLocal;
import br.com.ischool.business.MensagemServiceLocal;
import br.com.ischool.business.NotificacaoServiceLocal;
import br.com.ischool.entity.Aluno;
import br.com.ischool.entity.Classe;
import br.com.ischool.entity.Mensagem;
import br.com.ischool.entity.Notificacao;
import br.com.ischool.entity.Usuario;
import br.com.ischool.exceptions.ServicoException;
import br.com.ischool.exceptions.WebException;
import br.com.ischool.security.Crypto;
import br.com.ischool.security.MD5CheckSum;
import br.com.ischool.util.Constantes;
import br.com.ischool.util.FacesUtil;
import br.com.ischool.util.Paginacao;
import br.com.ischool.util.TipoMensagem;

@ManagedBean
@ViewScoped
public class MensagemBean extends AbstractViewHelper<Mensagem> implements	Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5154877681610269351L;
	
	@EJB
	private MensagemServiceLocal mensagemService;
	
	@EJB
	private NotificacaoServiceLocal notificacaoService;
	
	@EJB
	private GenericServiceLocal servicoGenerico;
	
	@EJB
	private ArquivoServiceLocal arquivoService;
	
	private List<Notificacao> notificacoes;
	
	private Aluno alunoSelecionado;
	
	private Classe classeSelecionada;

	private boolean openMessageStatus = false;
	
    private StreamedContent file;
    
	private String 	textoMensagemClasse;


	@Override
	public void inicializar() throws WebException {
		setFiltro(new Mensagem());
		setEntidade(new Mensagem());
		
		setNotificacoes(new ArrayList<Notificacao>());
				
	}

	@Override
	public void salvarImpl() throws ServicoException {

		Usuario usuario = FacesUtil.recuperarUsuarioSessao();

		entidade.setAluno(alunoSelecionado);
 		entidade.setUsuario(usuario);
		entidade.setCliente(usuario.getCliente());
		entidade.setLidoDevice(false);
		mensagemService.salvarMensagem(entidade);		
		mensagemService.enviarMensagemDevice(entidade,TipoMensagem.TEXTO,true);
		mensagemService.enviarMensagemWeb(entidade,false);
		
		alunoSelecionado.getMensagems().add(entidade);
		
		setEntidade(new Mensagem());
	}
	
	
	public void carregarNotificacoes(){
		
		Notificacao filtro = new Notificacao();
		
		filtro.setUsuario(new Usuario());
		
		filtro.getUsuario().setId(FacesUtil.recuperarUsuarioSessao().getId());
		
		try {
			notificacoes = (List<Notificacao>) notificacaoService.listarNotificacoes(filtro, null);
		} catch (ServicoException se) {			
			FacesUtil.exibirErro("erro_carregar_listas",se);	
			se.printStackTrace();
		}
		catch(Exception e){			
			FacesUtil.exibirErro("erro_carregar_listas",e);	
			e.printStackTrace();
		}
	}
	
	public void carregarMensagensAluno(){
	
		try {
			
			filtro.setAluno(alunoSelecionado);
			filtro.setDataCadastro(new Date());
			entidade.setMensagem("");
			
			alunoSelecionado.setMensagems((List<Mensagem>) mensagemService.listarMensagems(filtro, new Paginacao(0,15)));
			
			Collections.reverse(alunoSelecionado.getMensagems());
			
			excluirNotificacoes(alunoSelecionado.getId());
						
			carregarNotificacoes();
			
			dialogMessagesOpened();
		} catch (ServicoException se) {			
			FacesUtil.exibirErro("erro_carregar_listas",se);	
			se.printStackTrace();
		}
		catch(Exception e){			
			FacesUtil.exibirErro("erro_carregar_listas",e);	
			e.printStackTrace();
		}
	}
	
	
	public void carregarMensagensAluno(Long idAluno){
	
		try {
		
			alunoSelecionado = servicoGenerico.obterEntidade(idAluno, Aluno.class);
			
			filtro.setAluno(alunoSelecionado);
			filtro.setDataCadastro(new Date());
			alunoSelecionado.setMensagems((List<Mensagem>) mensagemService.listarMensagems(filtro, new Paginacao(0,15)));
			
			Collections.reverse(alunoSelecionado.getMensagems());
			
			excluirNotificacoes(idAluno);
			
			carregarNotificacoes();
						
			dialogMessagesOpened();
			
		} catch (ServicoException se) {			
			FacesUtil.exibirErro("erro_carregar_listas",se);	
			se.printStackTrace();
		}
		catch(Exception e){			
			FacesUtil.exibirErro("erro_carregar_listas",e);	
			e.printStackTrace();
		}
	}
	
	public void excluirNotificacoes(Long idAluno) throws ServicoException{
		
		Notificacao notificacao = new Notificacao();
		
		notificacao.setAluno(new Aluno());
		notificacao.setUsuario(new Usuario());
		
		notificacao.getAluno().setId(idAluno);
		notificacao.getUsuario().setId(FacesUtil.recuperarUsuarioSessao().getId());
		
		notificacao.setNomeClasse("excl");
		notificacao.setQuantidadeNotificacoes(1);
		
		notificacaoService.excluirByIdAlunoAndIdUsuario(notificacao);
	}
	
	
    public void handleFileUpload(FileUploadEvent event){ 	
    	try {
			            
            File arquivoSalvo = arquivoService.salvarArquivo(event.getFile().getInputstream(), alunoSelecionado, event.getFile().getFileName());
            
    		String hashArquivo   = MD5CheckSum.getMD5Checksum(arquivoSalvo.getAbsolutePath());
    		Long   tamanhoBytes = (long) arquivoSalvo.length();
			 
	       	entidade.setMensagem(event.getFile().getFileName());
	       	entidade.setCaminhoArquivo(arquivoSalvo.getAbsolutePath());
	       	entidade.setHashArquivo(hashArquivo);
	       	entidade.setTamanhoBytes(tamanhoBytes);
	       		       	
			salvarImpl();
			
			FacesUtil.exibirInfo("Arquivo "+ event.getFile().getFileName() + " enviado com sucesso!.");		
    	} catch (ServicoException se) {			
			FacesUtil.exibirErro("erro_carregar_listas",se);	
			se.printStackTrace();
		}
		catch(Exception e){			
			FacesUtil.exibirErro("erro_carregar_listas",e);	
			e.printStackTrace();
		}
    	
    }
    

	public void downloadFile(Mensagem mensagem) {        
		try {
	        InputStream stream = arquivoService.carregarArquivo(mensagem.getCaminhoArquivo());
			
	        file = new DefaultStreamedContent(stream, mensagem.getMensagem().substring(mensagem.getMensagem().lastIndexOf(".")+1), mensagem.getMensagem());

		} catch (ServicoException e) {
			e.printStackTrace();
		}
    }
	

    
    public void enviarMensagemClasse(){
    	
    	try {
			Classe classe = servicoGenerico.obterListaLazy(classeSelecionada, Classe.class, "alunos");
			
			List<Aluno> listaAlunos = classe.getAlunos();
			
			Usuario usuario = FacesUtil.recuperarUsuarioSessao();
			List<Mensagem> mensagens = new ArrayList<Mensagem>();

			if(listaAlunos != null && !listaAlunos.isEmpty()){
				
				for(Aluno aluno:listaAlunos){
					
					Mensagem msg = new Mensagem();

					msg.setMensagem(textoMensagemClasse);
					msg.setAluno(aluno);
					msg.setUsuario(usuario);
					msg.setCliente(usuario.getCliente());
					msg.setLidoDevice(false);
					
					mensagens.add(msg);

				}
				
				mensagemService.salvarMensagens(mensagens);		
				mensagemService.enviarMensagensDevice(mensagens,TipoMensagem.TEXTO);
				
				FacesUtil.exibirInfo("Mensagens enviadas com sucesso!");
				
				textoMensagemClasse = "";
				
			}else{
				FacesUtil.exibirAlerta("classe_vazia");	
			}
			

			
    	} catch (ServicoException se) {			
			FacesUtil.exibirErro("erro_carregar_listas",se);	
			se.printStackTrace();
		}
		catch(Exception e){			
			FacesUtil.exibirErro("erro_carregar_listas",e);	
			e.printStackTrace();
		}
    	
    }

	
	public String getHashUsuarioLogado(){	
		return Crypto.encriptSenhaMD5(Constantes.FIXSALT+FacesUtil.recuperarUsuarioSessao().getId().toString());
	}

	public String getIpConexaoWebSocket(){
		  // TODO SOLUCAO TEMPORARIA PARA TESTES
//		if(FacesUtil.recuperarUsuarioSessao().getAutoridade().equals(Constantes.RESPONSAVEL)){			
//			return Constantes.TIPO_WEBSOCKET+"192.168.1.2:"+Constantes.WEBSOCKET_PORT;
//		}
		return Constantes.TIPO_WEBSOCKET+Constantes.WEBSOCKET_IP+":"+Constantes.WEBSOCKET_PORT;
	}
	
	@Override
	public void alterarImpl() throws ServicoException {
		// TODO Auto-generated method stub

	}

	@Override
	public void excluirImpl() throws ServicoException {
		// TODO Auto-generated method stub

	}

	@Override
	public void pesquisar() {
		// TODO Auto-generated method stub

	}

	@Override
	public void reset() {
		// TODO Auto-generated method stub

	}

    
	public Aluno getAlunoSelecionado() {
		return alunoSelecionado;
	}

	public void setAlunoSelecionado(Aluno alunoSelecionado) {
		this.alunoSelecionado = alunoSelecionado;
	}

	public void dialogMessagesOpened() {
		openMessageStatus = true;
	}

	public void dialogMessagesClosed() {
		openMessageStatus = false;
	}

	public boolean isOpenMessageStatus() {
		return openMessageStatus;
	}

	public void setOpenMessageStatus(boolean openMessageStatus) {
		this.openMessageStatus = openMessageStatus;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public List<Notificacao> getNotificacoes() {
		return notificacoes;
	}

	public void setNotificacoes(List<Notificacao> notificacoes) {
		this.notificacoes = notificacoes;
	}

	public Classe getClasseSelecionada() {
		return classeSelecionada;
	}

	public void setClasseSelecionada(Classe classeSelecionada) {
		this.classeSelecionada = classeSelecionada;
	}
	
    public void setFile(StreamedContent file) {
		this.file = file;
	}
 
    public StreamedContent getFile() {
        return file;
    }

	public String getTextoMensagemClasse() {
		return textoMensagemClasse;
	}

	public void setTextoMensagemClasse(String textoMensagemClasse) {
		this.textoMensagemClasse = textoMensagemClasse;
	}
	
	

}
