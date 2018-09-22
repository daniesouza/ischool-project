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
import br.com.ischool.business.ClasseServiceLocal;
import br.com.ischool.business.GenericServiceLocal;
import br.com.ischool.business.UsuarioServiceLocal;
import br.com.ischool.controller.datamodel.AlunoDataModel;
import br.com.ischool.entity.Aluno;
import br.com.ischool.entity.Classe;
import br.com.ischool.entity.Cliente;
import br.com.ischool.entity.Usuario;
import br.com.ischool.exceptions.ServicoException;
import br.com.ischool.exceptions.WebException;
import br.com.ischool.util.Constantes;
import br.com.ischool.util.FacesUtil;

/**
 * @author Daniel Souza de lima e-mail:daniesouza@gmail.com
 *      
 */

@ManagedBean
@ViewScoped
public class AlunoBean extends AbstractViewHelper<Aluno> implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 3725699119385532894L;

	@EJB
	private AlunoServiceLocal alunoService;
	
	@EJB
	private UsuarioServiceLocal usuarioService;
	
	@EJB
	private ClasseServiceLocal classeService;
	
	@EJB
	private GenericServiceLocal servicoGenerico;
	
	private AlunoDataModel alunoDataModel = new AlunoDataModel();
	
	private DualListModel<Usuario>	   responsaveis;
	private List<Usuario> 		       listaUsuarios;
	
	private DualListModel<Classe>	   classes;
	private List<Classe> 		       listaClasses;
	
	private Usuario responsavel;

			
	
	@Override
	public void inicializar() throws WebException {
				
		reset();	
		setFiltro(new Aluno());
		getFiltro().setCliente(new Cliente());	
		responsaveis = new DualListModel<Usuario>();
		classes 	 = new DualListModel<Classe>();
		
		
		Usuario usuario = FacesUtil.recuperarUsuarioSessao();
		
		// SE O USUARIO FOR ADMIN GERAL NAO TERA UM CLIENTE ASSOCIADO
		if(usuario.getCliente() != null){
			
			if(filtro == null){
				filtro = new Aluno();
				filtro.setCliente(new Cliente());
			}
			filtro.getCliente().setIdCliente(usuario.getCliente().getIdCliente());
		}
		
		
		alunoDataModel.setAlunoLocal(alunoService);
		alunoDataModel.setFiltro(filtro);
		
		responsavel = new Usuario();


	}
	
	public void carregarListaResponsaveis() throws ServicoException,Exception{
				
			Usuario filtro = new Usuario();
			
			filtro.setCliente(new Cliente());
			filtro.setAtivo(true);
			filtro.getCliente().setIdCliente(FacesUtil.recuperarUsuarioSessao().getCliente().getIdCliente());
			
			listaUsuarios	= (List<Usuario>) usuarioService.listarUsuarios(filtro, null);
	
	}
	
	public void carregarListaClasses() throws ServicoException,Exception{
			
			Usuario usuario = FacesUtil.recuperarUsuarioSessao();
			
			Classe filtro = new Classe();			
			filtro.setCliente(new Cliente());	
			filtro.setAtivo(true);
			filtro.getCliente().setIdCliente(usuario.getCliente().getIdCliente());
			//filtro.setAno(Calendar.getInstance().get(Calendar.YEAR));
			
			
			listaClasses =  (List<Classe>) classeService.listarClasses(filtro, null);	

	}

	@Override
	public void pesquisar() {
		
		//alunoDataModel.setFiltro(filtro);
		
	}

	@Override
	public void reset() {
		setEntidade(new Aluno());	
	}
	
	public void novo(){
		
		try{
			
			reset();
			carregarListaResponsaveis();
			carregarListaClasses();
	
			responsaveis.setSource(listaUsuarios);
			responsaveis.setTarget(new ArrayList<Usuario>());
			
			classes.setSource(listaClasses);
			classes.setTarget(new ArrayList<Classe>());
			
			Usuario usuario = FacesUtil.recuperarUsuarioSessao();		
			getEntidade().setCliente(usuario.getCliente());
		
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

			carregarListaResponsaveis();
			carregarListaClasses();

			
			responsaveis.setSource(listaUsuarios);
			classes.setSource(listaClasses);

			entidade = servicoGenerico.obterListaLazy(entidade, Aluno.class, "usuarios");
			responsaveis.setTarget(new ArrayList<Usuario>());
			
			for(Usuario prof:entidade.getUsuarios()){
				
				if(prof.getAtivo()){
					responsaveis.getTarget().add(prof);
				}
			}
			
			responsaveis.getSource().removeAll(entidade.getUsuarios());
			
			entidade = servicoGenerico.obterListaLazy(entidade, Aluno.class, "classes");
			classes.setTarget(new ArrayList<Classe>());
			
			for(Classe classe:entidade.getClasses()){
				
				if(classe.getAtivo()){
					classes.getTarget().add(classe);
				}
			}
					
			classes.getSource().removeAll(entidade.getClasses());
		
		} catch (ServicoException se) {
			FacesUtil.exibirErro("erro_carregar_listas",se);	
			se.printStackTrace();
		}catch(Exception e){			
			FacesUtil.exibirErro("erro_carregar_listas",e);	
			e.printStackTrace();
		}
		
	}
	
	
	public void novoResponsavel(){
		
		responsavel = new Usuario();
		
		Usuario user = FacesUtil.recuperarUsuarioSessao();		
		responsavel.setCliente(user.getCliente());
		responsavel.setAutoridade(Constantes.RESPONSAVEL);
		

	}
	
	
	public void salvarResponsavel(){
		
		
        RequestContext context = RequestContext.getCurrentInstance();  
        boolean error = false;

		try {
			usuarioService.salvarUsuario(responsavel);
			
			carregarListaResponsaveis();

			responsaveis.setSource(listaUsuarios);

			responsaveis.getTarget().add(responsavel);
			
			responsaveis.getSource().remove(responsavel);
			
			responsavel = new Usuario();
			
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
	

	@Override
	public void salvarImpl() throws ServicoException {
		
		entidade.setUsuarios(responsaveis.getTarget());
		entidade.setClasses(classes.getTarget());
		
		
		alunoService.salvarAluno(entidade);	
		
	}

	@Override
	public void alterarImpl() throws ServicoException {
		
		entidade.setUsuarios(responsaveis.getTarget());
		entidade.setClasses(classes.getTarget());
		
		alunoService.alterarAluno(entidade);	
		
	}

	@Override
	public void excluirImpl() throws ServicoException {
		
		alunoService.excluirAluno(entidade);
		
	}

	public AlunoServiceLocal getAlunoService() {
		return alunoService;
	}

	public void setAlunoService(AlunoServiceLocal AlunoService) {
		this.alunoService = AlunoService;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public AlunoDataModel getAlunoDataModel() {
		return alunoDataModel;
	}

	public void setAlunoDataModel(AlunoDataModel AlunoDataModel) {
		this.alunoDataModel = AlunoDataModel;
	}

	public DualListModel<Usuario> getResponsaveis() {
		return responsaveis;
	}

	public void setResponsaveis(DualListModel<Usuario> responsaveis) {
		this.responsaveis = responsaveis;
	}

	public DualListModel<Classe> getClasses() {
		return classes;
	}

	public void setClasses(DualListModel<Classe> classes) {
		this.classes = classes;
	}

	public Usuario getResponsavel() {
		return responsavel;
	}

	public void setResponsavel(Usuario responsavel) {
		this.responsavel = responsavel;
	}
	
	

}
