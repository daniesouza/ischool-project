package br.com.ischool.controller;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.context.RequestContext;
import org.primefaces.model.DualListModel;

import br.com.ischool.business.AlunoServiceLocal;
import br.com.ischool.business.ClienteServiceLocal;
import br.com.ischool.business.GenericServiceLocal;
import br.com.ischool.business.UsuarioServiceLocal;
import br.com.ischool.controller.datamodel.UsuarioDataModel;
import br.com.ischool.entity.Aluno;
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
public class ResponsavelBean extends AbstractViewHelper<Usuario> implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -3572873780481594189L;
	
	@EJB
	private UsuarioServiceLocal  usuarioService;
	
	@EJB
	private AlunoServiceLocal  	 alunoService;
	
	@EJB
	private ClienteServiceLocal  clienteService;

	@EJB
	private GenericServiceLocal servicoGenerico;	
	
	private UsuarioDataModel 	 usuarioDataModel;
	
	private DualListModel<Aluno>	   alunos;
	private List<Aluno> 		       listaAlunos;

	private Aluno aluno;
			
	
	@Override
	public void inicializar() {
		
		
		reset();
		
		setFiltro(new Usuario());
		getFiltro().setCliente(new Cliente());
		getFiltro().setAutoridade(Constantes.RESPONSAVEL);
		usuarioDataModel = new UsuarioDataModel();
		usuarioDataModel.setUsuarioLocal(usuarioService);
		alunos = new DualListModel<Aluno>();
		
		Usuario usuario = FacesUtil.recuperarUsuarioSessao();
		
		// SE O USUARIO FOR ADMIN GERAL NAO TERA UM CLIENTE ASSOCIADO
		if(usuario.getCliente() != null){
			
			if(filtro == null){
				filtro = new Usuario();
				filtro.setCliente(new Cliente());
			}
			filtro.getCliente().setIdCliente(usuario.getCliente().getIdCliente());
		}
		
		usuarioDataModel.setFiltro(filtro);
		aluno = new Aluno();
	}

	@Override
	public void pesquisar() {
		
		//usuarioDataModel.setFiltro(filtro);
		
	}

	@Override
	public void reset() {
		setEntidade(new Usuario());
		getEntidade().setAutoridade(Constantes.RESPONSAVEL);
	}

	@Override
	public void salvarImpl() throws ServicoException {
		
		entidade.setAlunos(alunos.getTarget());
		
		usuarioService.salvarUsuario(entidade);

	}

	@Override
	public void alterarImpl() throws ServicoException {
		
		entidade.setAlunos(alunos.getTarget());
		
		usuarioService.alterarUsuario(entidade);	
		
	}

	@Override
	public void excluirImpl() throws ServicoException {
		
		usuarioService.excluirUsuario(entidade);
		
	}
	
	
	public void novo() {
		
		try{

			reset();
			Usuario usuario = FacesUtil.recuperarUsuarioSessao();		
			getEntidade().setCliente(usuario.getCliente());
			getEntidade().setAtivo(true);
			
			carregarListaAlunos();
	
			alunos.setSource(listaAlunos);
			alunos.setTarget(new ArrayList<Aluno>());

		
		} catch (ServicoException se) {			
			FacesUtil.exibirErro("erro_carregar_listas",se);	
			se.printStackTrace();
		}
		catch(Exception e){			
			FacesUtil.exibirErro("erro_carregar_listas",e);	
			e.printStackTrace();
		}
	}
	
	public void editar(){
		
		try {

			carregarListaAlunos();

			alunos.setSource(listaAlunos);

			entidade = servicoGenerico.obterListaLazy(entidade, Usuario.class, "alunos");
			alunos.setTarget(new ArrayList<Aluno>());
			
			for(Aluno aluno:entidade.getAlunos()){
				
				if(aluno.getAtivo()){
					alunos.getTarget().add(aluno);
				}
			}
			
			alunos.getSource().removeAll(entidade.getAlunos());

		} catch (ServicoException se) {
			FacesUtil.exibirErro("erro_carregar_listas",se);	
			se.printStackTrace();
		}catch(Exception e){			
			FacesUtil.exibirErro("erro_carregar_listas",e);	
			e.printStackTrace();
		}
		
	}
	
	public void novoAluno(){
		
		aluno = new Aluno();
		
		Usuario usuario = FacesUtil.recuperarUsuarioSessao();		
		aluno.setCliente(usuario.getCliente());

	}
	
	
	public void salvarAluno(){
		
		
        RequestContext context = RequestContext.getCurrentInstance();  
        boolean error = false;

		try {
			alunoService.salvarAluno(aluno);
			
			carregarListaAlunos();

			alunos.setSource(listaAlunos);

			alunos.getTarget().add(aluno);
			
			alunos.getSource().remove(aluno);
			
			aluno = new Aluno();
			
			FacesUtil.exibirInfo("mensagem_registro_inserido");
			
		} catch (ServicoException se) {
			FacesUtil.exibirErro("erro_incluir",se);
			error = true;
			se.printStackTrace();
		}catch(Exception e){			
			FacesUtil.exibirErro("erro_incluir",e);	
			error = true;
			e.printStackTrace();
		}	
		
		context.addCallbackParam("error", error); 
		
	}
	
	
	public void carregarListaAlunos() throws ServicoException,Exception{
				
			Aluno filtro = new Aluno();
			
			filtro.setCliente(new Cliente());			
			filtro.setAtivo(true);
			filtro.getCliente().setIdCliente(FacesUtil.recuperarUsuarioSessao().getCliente().getIdCliente());
			
			listaAlunos	= (List<Aluno>) alunoService.listarAlunos(filtro, null);
	
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

	public DualListModel<Aluno> getAlunos() {
		return alunos;
	}

	public void setAlunos(DualListModel<Aluno> alunos) {
		this.alunos = alunos;
	}

	public Aluno getAluno() {
		return aluno;
	}

	public void setAluno(Aluno aluno) {
		this.aluno = aluno;
	}

	

}
