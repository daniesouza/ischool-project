package br.com.ischool.controller;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.model.DualListModel;

import br.com.ischool.business.ClienteServiceLocal;
import br.com.ischool.business.GenericServiceLocal;
import br.com.ischool.business.UsuarioServiceLocal;
import br.com.ischool.controller.datamodel.ClienteDataModel;
import br.com.ischool.entity.Cliente;
import br.com.ischool.entity.Usuario;
import br.com.ischool.exceptions.ServicoException;
import br.com.ischool.exceptions.WebException;
import br.com.ischool.util.FacesUtil;

/**
 * @author Daniel Souza de lima e-mail:daniesouza@gmail.com
 *      
 */

@ManagedBean
@ViewScoped
public class ClienteBean extends AbstractViewHelper<Cliente> implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 3725699119385532894L;

	@EJB
	private ClienteServiceLocal clienteService;
	
	
	@EJB
	private UsuarioServiceLocal usuarioService;
	
	@EJB
	private GenericServiceLocal servicoGenerico;
	
	private ClienteDataModel clienteDataModel = new ClienteDataModel();
	
	private DualListModel<Usuario>	   usuarios;
	private List<Usuario> 		       listaUsuarios;

			
	
	@Override
	public void inicializar() throws WebException {
		
		
			setEntidade(new Cliente());	
			setFiltro(new Cliente());
			clienteDataModel.setClienteLocal(clienteService);
			usuarios = new DualListModel<Usuario>();

	}
	
	public void carregarListaUsuarios() throws ServicoException,Exception{

		listaUsuarios = (List<Usuario>) usuarioService.listarUsuariosSemClienteSemAdmin();
	
	}

	@Override
	public void pesquisar() {
		
		clienteDataModel.setFiltro(filtro);
		
	}

	@Override
	public void reset() {
		setEntidade(new Cliente());	
	}
	
	public void novo(){
		
		try{
	
			carregarListaUsuarios();
	
			usuarios.setSource(listaUsuarios);
			usuarios.setTarget(new ArrayList<Usuario>());
			
		}catch (ServicoException se) {
			FacesUtil.exibirErro("erro_carregar_listas",se);	
			se.printStackTrace();					
		}catch(Exception e){			
			FacesUtil.exibirErro("erro_carregar_listas",e);	
			e.printStackTrace();
		}

		
		reset();
	}
	
	public void editar(){
		
		try {

			carregarListaUsuarios();
			usuarios.setSource(listaUsuarios);

			entidade = servicoGenerico.obterListaLazy(entidade, Cliente.class, "usuarios");
			usuarios.setTarget(new ArrayList<Usuario>(entidade.getUsuarios()));
		
		} catch (ServicoException se) {
			FacesUtil.exibirErro("erro_carregar_listas",se);	
			se.printStackTrace();
		}catch(Exception e){			
			FacesUtil.exibirErro("erro_carregar_listas",e);	
			e.printStackTrace();
		}
		
	}

	@Override
	public void salvarImpl() throws ServicoException {
		
		entidade.setUsuarios(usuarios.getTarget());
		
		clienteService.salvarCliente(entidade);	
		
	}

	@Override
	public void alterarImpl() throws ServicoException {
		
		entidade.setUsuarios(usuarios.getTarget());
		
		clienteService.alterarCliente(entidade);	
		
	}

	@Override
	public void excluirImpl() throws ServicoException {
		
	//	clienteService.excluirCliente(entidade);
		
	}

	public ClienteServiceLocal getClienteService() {
		return clienteService;
	}

	public void setClienteService(ClienteServiceLocal ClienteService) {
		this.clienteService = ClienteService;
	}



	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public ClienteDataModel getClienteDataModel() {
		return clienteDataModel;
	}

	public void setClienteDataModel(ClienteDataModel clienteDataModel) {
		this.clienteDataModel = clienteDataModel;
	}

	public DualListModel<Usuario> getUsuarios() {
		return usuarios;
	}

	public void setUsuarios(DualListModel<Usuario> usuarios) {
		this.usuarios = usuarios;
	}

	

}
