package br.com.ischool.controller;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import br.com.ischool.business.EventoServiceLocal;
import br.com.ischool.controller.datamodel.EventoDataModel;
import br.com.ischool.entity.Cliente;
import br.com.ischool.entity.Evento;
import br.com.ischool.entity.Usuario;
import br.com.ischool.exceptions.ServicoException;
import br.com.ischool.exceptions.WebException;
import br.com.ischool.util.Constantes;
import br.com.ischool.util.FacesUtil;
import br.com.ischool.util.Icone;

/**
 * @author Daniel Souza de lima e-mail:daniesouza@gmail.com
 *      
 */

@ManagedBean
@ViewScoped
public class EventoBean extends AbstractViewHelper<Evento> implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 3725699119385532894L;

	@EJB
	private EventoServiceLocal eventoService;
	
	private EventoDataModel eventoDataModel = new EventoDataModel();
	
	private Icone iconeSelecionado;
	private List<Icone> listaIcones = new ArrayList<Icone>();
	

			
	
	@Override
	public void inicializar() throws WebException {
		
		reset();

		setFiltro(new Evento());
		getFiltro().setCliente(new Cliente());
		
		Usuario usuario = FacesUtil.recuperarUsuarioSessao();
		
		// SE O USUARIO FOR ADMIN GERAL NAO TERA UM CLIENTE ASSOCIADO
		if(usuario.getCliente() != null){			
			getFiltro().getCliente().setIdCliente(usuario.getCliente().getIdCliente());
		}
			
		eventoDataModel.setEventoLocal(eventoService);
		eventoDataModel.setFiltro(filtro);
	
		listaIcones.add(new Icone("icone_evento.png","Evento Generico"));
		listaIcones.add(new Icone("star_gold_256.png","Estrela"));
	}
	


	@Override
	public void pesquisar() {
		
//		eventoDataModel.setFiltro(filtro);
		
	}

	@Override
	public void reset() {
		setEntidade(new Evento());	
		getEntidade().setPreCadastro(false);
		getEntidade().setTipo(Constantes.TIPO_INFORMATIVO); //TODO RETIRAR DAQUI E COLOCAR NA TELA
	}
	
	public void novoEventoCliente(){
		
		reset();
		
		Usuario usuario = FacesUtil.recuperarUsuarioSessao();		
		getEntidade().setCliente(usuario.getCliente());
		getEntidade().setIcone("icone_evento.png");

		iconeSelecionado = new Icone("icone_evento.png","Evento Generico");

		
	}
	
	public void editar(){

		iconeSelecionado = new Icone("","");
		iconeSelecionado.setImagem(entidade.getIcone());
	}
	
	@Override
	public void salvarImpl() throws ServicoException {
				
		getEntidade().setIcone(iconeSelecionado.getImagem());		
	
		eventoService.salvarEvento(entidade);	
		
	}

	@Override
	public void alterarImpl() throws ServicoException {

		getEntidade().setIcone(iconeSelecionado.getImagem());

		eventoService.alterarEvento(entidade);	
		
	}

	@Override
	public void excluirImpl() throws ServicoException {
		
		eventoService.excluirEvento(entidade);
		
	}

	public EventoServiceLocal getEventoService() {
		return eventoService;
	}

	public void setEventoService(EventoServiceLocal EventoService) {
		this.eventoService = EventoService;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public EventoDataModel getEventoDataModel() {
		return eventoDataModel;
	}

	public void setEventoDataModel(EventoDataModel EventoDataModel) {
		this.eventoDataModel = EventoDataModel;
	}



	public Icone getIconeSelecionado() {
		return iconeSelecionado;
	}



	public void setIconeSelecionado(Icone iconeSelecionado) {
		this.iconeSelecionado = iconeSelecionado;
	}



	public List<Icone> getListaIcones() {
		return listaIcones;
	}



	public void setListaIcones(List<Icone> listaIcones) {
		this.listaIcones = listaIcones;
	}

	
}
