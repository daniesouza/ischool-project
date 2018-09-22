package br.com.ischool.controller;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.context.RequestContext;

import br.com.ischool.business.EventoExecutadoServiceLocal;
import br.com.ischool.business.GenericServiceLocal;
import br.com.ischool.business.MensagemServiceLocal;
import br.com.ischool.entity.Aluno;
import br.com.ischool.entity.Classe;
import br.com.ischool.entity.Evento;
import br.com.ischool.entity.EventoExecutado;
import br.com.ischool.entity.Mensagem;
import br.com.ischool.entity.Usuario;
import br.com.ischool.exceptions.ServicoException;
import br.com.ischool.exceptions.WebException;
import br.com.ischool.util.Constantes;
import br.com.ischool.util.DadosUtil;
import br.com.ischool.util.FacesUtil;
import br.com.ischool.util.TipoMensagem;

/**
 * @author Daniel Souza de lima e-mail:daniesouza@gmail.com
 *      
 */

@ManagedBean
@ViewScoped
public class GerenciamentoAulaBean extends AbstractViewHelper<EventoExecutado> implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 6390001812920897476L;
	
	

	
	@EJB
	private EventoExecutadoServiceLocal eventoExecutadoService;
	
	@EJB
	private GenericServiceLocal servicoGenerico;
	
	@EJB
	private MensagemServiceLocal mensagemService;

	private List<Classe>	   	listaClasses;
	private List<Aluno>		   	listaAlunos;
	private Evento[]			eventosSelecionados;

	
	@Override
	public void inicializar() throws WebException {
			
		setEntidade(new EventoExecutado());
		entidade.setDataCadastro(new Date());
		listaClasses   = new ArrayList<Classe>();
		
		carregarListaClasses();
	}
	
	
	@Override
	public void reset() {

	}

	public void carregarListaClasses() throws WebException{
		try{
			
			Usuario professor = servicoGenerico.obterListaLazy(FacesUtil.recuperarUsuarioSessao(), Usuario.class, "classes");
			//TODO refatorar e por a regra abaixo no classe service
			for(Classe classe:professor.getClasses()){
				
				if(classe.getAtivo()){
					
					listaClasses.add(classe);
				}
				
			}
			
//			for(Classe classe:professor.getClasses()){
//				
//				if(classe.getAno() == Calendar.getInstance().get(Calendar.YEAR)){
//					
//					listaClasses.add(classe);
//				}
//				
//			}
						
		} catch (ServicoException se) {
			FacesUtil.exibirErro("erro_carregar_listas",se);	
			se.printStackTrace();
		}catch(Exception e){			
			throw new WebException(e.getMessage());
		}
	}
	
	public void carregarSessaoClasse(){
		
		try{
			listaAlunos = (List<Aluno>) eventoExecutadoService.carregarSessaoAlunoProfessor(entidade.getClasse(),new Date());

		} catch (ServicoException se) {
			FacesUtil.exibirErro("erro_carregar_listas",se);	
			se.printStackTrace();
		}catch(Exception e){			
			FacesUtil.exibirErro("erro_carregar_listas",e);	
			e.printStackTrace();			
		}
	
	}
	
    
    public void adicionarDataHoje(){
    	
    	if(entidade.getIcone().equals(entidade.getIconeNaoOk())){
        	entidade.setDataInicio(new Date());	
    	}
    	else if(entidade.getIcone().equals(entidade.getIconeWarning())){
    		entidade.setDataFim(new Date());
    	}
    }
    
	

	@Override
	public void salvarImpl() throws ServicoException {

//		entidade.setUsuario(FacesUtil.recuperarUsuarioSessao());
//		entidade.setCliente(entidade.getUsuario().getCliente());
//		
//		Calendar calendar = Calendar.getInstance();		
//		calendar.setTime(entidade.getDataInicio());
//		
//		Calendar dataInicio = Calendar.getInstance();
//		
//		dataInicio.set(Calendar.HOUR_OF_DAY, calendar.get(Calendar.HOUR_OF_DAY));
//		dataInicio.set(Calendar.MINUTE, calendar.get(Calendar.MINUTE));
//		dataInicio.set(Calendar.SECOND, calendar.get(Calendar.SECOND));
//
//		entidade.setDataInicio(dataInicio.getTime());
//
//		eventoExecutadoService.salvarEventoExecutado(entidade);


	}
	

	@Override
	public void alterarImpl() throws ServicoException {
		
		entidade.setUsuario(FacesUtil.recuperarUsuarioSessao());
		entidade.setCliente(entidade.getUsuario().getCliente());
		
		if(!DadosUtil.isEmpty(entidade.getDataFim())){
			
			Calendar calendar = Calendar.getInstance();		
			calendar.setTime(entidade.getDataFim());
			
			Calendar dataInicio = Calendar.getInstance();
			
			dataInicio.set(Calendar.HOUR_OF_DAY, calendar.get(Calendar.HOUR_OF_DAY));
			dataInicio.set(Calendar.MINUTE, calendar.get(Calendar.MINUTE));
			dataInicio.set(Calendar.SECOND, calendar.get(Calendar.SECOND));

			entidade.setDataFim(dataInicio.getTime());
		}
	
		eventoExecutadoService.alterarEventoExecutado(entidade);
		if(entidade.getTipo() == Constantes.TIPO_LEMBRETE || entidade.getTipo() == Constantes.TIPO_LEMBRETE_GEN){
			enviarMensagemEventoExecutado(entidade,TipoMensagem.LEMBRETE);
		}else{
			enviarMensagemEventoExecutado(entidade,TipoMensagem.EVENTO);
		}
				
	}
	
	
	public void cancelarEventoExecutado(){
		
        RequestContext context = RequestContext.getCurrentInstance();  
        boolean error = false; 
		
		try {
			eventoExecutadoService.cancelarEventoExecutado(entidade);
			
			if(entidade.getTipo() == Constantes.TIPO_LEMBRETE || entidade.getTipo() == Constantes.TIPO_LEMBRETE_GEN){
				enviarMensagemEventoExecutado(entidade,TipoMensagem.LEMBRETE_CANC);
			}else{
				enviarMensagemEventoExecutado(entidade,TipoMensagem.EVENTO_CANC);
			}
						
			FacesUtil.exibirInfo("mensagem_registro_atualizado");
		}catch (ServicoException se) {
			FacesUtil.exibirErro("erro_alterar",se);
			error = true;
			se.printStackTrace();
		} catch (Exception e) {
			FacesUtil.exibirErro("erro_alterar",e);
			error = true;
			e.printStackTrace();
		}
		
        context.addCallbackParam("error", error); 

	}
	
	private void enviarMensagemEventoExecutado(EventoExecutado eventoExecutado,TipoMensagem tipoMensagem) throws ServicoException{
		
		
		Mensagem msg = new Mensagem();
		
		msg.setAluno(eventoExecutado.getAluno());
		msg.setCliente(eventoExecutado.getCliente());
		//TODO REFATORAR ISTO UM DIA.. NAO SE PODE PASSAR HTML E NEM TEXTO FIXO PARA O DEVICE.. FIZ ISSO POR QUE ESTAVA COM PRESSA
		if(tipoMensagem == TipoMensagem.EVENTO || tipoMensagem == TipoMensagem.LEMBRETE){
			SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
			msg.setMensagem("<b>"+eventoExecutado.getEvento().getCodigoEvento().replace("#", "")+"</b>"+":Iniciou as "+sdf.format(eventoExecutado.getDataInicio()));
		}else{
			msg.setMensagem("<b>"+eventoExecutado.getEvento().getCodigoEvento().replace("#", "")+"</b>"+":Cancelado");
		}
		msg.setIdEventoExec(eventoExecutado.getId());
		msg.setUsuario(eventoExecutado.getUsuario());
		msg.setDataCadastro(new Date());
		
		mensagemService.enviarMensagemDevice(msg,tipoMensagem,true);
		
	}
//	
//	public void deletarEvento(){
//		try{				
//			eventoExecutadoService.excluirEventoExecutado(entidade);
//			entidade.getAluno().getEventosExecutados().remove(entidade);
//		} catch (ServicoException se) {
//			FacesUtil.exibirErro("erro_excluir",se);	
//			se.printStackTrace();
//		}catch(Exception e){			
//			FacesUtil.exibirErro("erro_excluir",e);	
//			e.printStackTrace();			
//		}
//	}
	
	public void adicionarEvento(){
		
		try{
		
			if(eventosSelecionados != null){
				for(Evento evento:eventosSelecionados){
					
					EventoExecutado eventoExecutado = new EventoExecutado();
					
					eventoExecutado.setEvento(evento);				
					eventoExecutado.setClasse(entidade.getClasse());
					eventoExecutado.setTipo(evento.getTipo());
					eventoExecutado.setIcone(Constantes.ICONE_NAO_OK);
					eventoExecutado.setStatusEventoExecutado(Constantes.STATUS_OK);
					eventoExecutado.setAluno(entidade.getAluno());
					
					Usuario us = FacesUtil.recuperarUsuarioSessao();
					
					eventoExecutado.setUsuario(us);
					eventoExecutado.setCliente(us.getCliente());
					

					eventoExecutadoService.salvarEventoExecutado(eventoExecutado);				
					
					entidade.getAluno().getEventosExecutados().add(eventoExecutado);
				}
			}

		} catch (ServicoException se) {
			FacesUtil.exibirErro("erro_incluir",se);	
			se.printStackTrace();
		}catch(Exception e){			
			FacesUtil.exibirErro("erro_incluir",e);	
			e.printStackTrace();			
		}
	
	}
	
	@Override
	public void excluirImpl() throws ServicoException {
		
	}

	@Override
	public void pesquisar() {
		
	}

	public List<Classe> getListaClasses() {
		return listaClasses;
	}


	public void setListaClasses(List<Classe> listaClasses) {
		this.listaClasses = listaClasses;
	}


	public List<Aluno> getListaAlunos() {
		return listaAlunos;
	}


	public void setListaAlunos(List<Aluno> listaAlunos) {
		this.listaAlunos = listaAlunos;
	}


	public Evento[] getEventosSelecionados() {
		return eventosSelecionados;
	}


	public void setEventosSelecionados(Evento[] eventosSelecionados) {
		this.eventosSelecionados = eventosSelecionados;
	}

	

}
