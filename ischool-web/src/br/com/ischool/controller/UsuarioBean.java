package br.com.ischool.controller;

import java.io.Serializable;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import br.com.ischool.business.ClienteServiceLocal;
import br.com.ischool.business.GenericServiceLocal;
import br.com.ischool.business.UsuarioServiceLocal;
import br.com.ischool.controller.datamodel.ClienteDataModel;
import br.com.ischool.controller.datamodel.UsuarioDataModel;
import br.com.ischool.entity.Cliente;
import br.com.ischool.entity.Usuario;
import br.com.ischool.exceptions.ServicoException;
import br.com.ischool.util.Constantes;
import br.com.ischool.util.FacesUtil;

/**
 * @author Daniel Souza de lima e-mail:daniesouza@gmail.com
 *      
 */

@ManagedBean
@ViewScoped
public class UsuarioBean extends AbstractViewHelper<Usuario> implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 6591098012361011680L;

	@EJB
	private UsuarioServiceLocal  usuarioService;
	@EJB
	private ClienteServiceLocal  clienteService;
	
	private UsuarioDataModel 	 usuarioDataModel;
	private ClienteDataModel	 clienteDataModel;

	@EJB
	private GenericServiceLocal servicoGenerico;
			
	
	@Override
	public void inicializar() {
		
		setFiltro(new Usuario());
		getFiltro().setCliente(new Cliente());
		getFiltro().setAutoridade(Constantes.PROFESSOR);
		usuarioDataModel = new UsuarioDataModel();
		usuarioDataModel.setUsuarioLocal(usuarioService);
		clienteDataModel = new ClienteDataModel();
		clienteDataModel.setClienteLocal(clienteService);
		setEntidade(new Usuario());
		
		Usuario usuario = FacesUtil.recuperarUsuarioSessao();
		
		//TODO RETIRAR ESSE PRIMEIRO IF QUANDO ESTIVER PRONTO
		
		if(usuario != null){
			
			// SE O USUARIO FOR ADMIN GERAL NAO TERA UM CLIENTE ASSOCIADO
			if(usuario.getCliente() != null){
				
				if(filtro == null){
					filtro = new Usuario();
					filtro.setCliente(new Cliente());
				}
				filtro.getCliente().setIdCliente(usuario.getCliente().getIdCliente());
			}
		}

		usuarioDataModel.setFiltro(filtro);

	}

	@Override
	public void pesquisar() {		
		//usuarioDataModel.setFiltro(filtro);

	}

	@Override
	public void reset() {
		setEntidade(new Usuario());
	}

	@Override
	public void salvarImpl() throws ServicoException {
		
		if(entidade.getAutoridade().equals(Constantes.ADMINISTRADOR)){
			entidade.setCliente(null);
		}
		
		usuarioService.salvarUsuario(entidade);

	}

	@Override
	public void alterarImpl() throws ServicoException {
		
		if(entidade.getAutoridade().equals(Constantes.ADMINISTRADOR)){
			entidade.setCliente(null);
		}
		
		usuarioService.alterarUsuario(entidade);
				
	}
	


	@Override
	public void excluirImpl() throws ServicoException {
		
		usuarioService.excluirUsuario(entidade);
		
	}
	
	public void editar() {

        
	}
	
	public void novo() {
		reset();		
		getEntidade().setAtivo(false);
	}
	
	public void novoUsuarioCliente() {
		
		reset();
		Usuario usuario = FacesUtil.recuperarUsuarioSessao();		
		getEntidade().setCliente(usuario.getCliente());
		getEntidade().setAtivo(true);
	}
	
		
	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public UsuarioServiceLocal getUsuarioService() {
		return usuarioService;
	}

	public void setUsuarioService(UsuarioServiceLocal usuarioService) {
		this.usuarioService = usuarioService;
	}

	public UsuarioDataModel getUsuarioDataModel() {
		return usuarioDataModel;
	}

	public void setUsuarioDataModel(UsuarioDataModel usuarioDataModel) {
		this.usuarioDataModel = usuarioDataModel;
	}

	public GenericServiceLocal getServicoGenerico() {
		return servicoGenerico;
	}

	public void setServicoGenerico(GenericServiceLocal servicoGenerico) {
		this.servicoGenerico = servicoGenerico;
	}

	public ClienteDataModel getClienteDataModel() {
		return clienteDataModel;
	}

	public void setClienteDataModel(ClienteDataModel clienteDataModel) {
		this.clienteDataModel = clienteDataModel;
	}

}
