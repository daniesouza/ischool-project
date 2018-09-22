package br.com.ischool.controller;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.model.DualListModel;

import br.com.ischool.business.AlunoServiceLocal;
import br.com.ischool.business.ClasseServiceLocal;
import br.com.ischool.business.GenericServiceLocal;
import br.com.ischool.business.UsuarioServiceLocal;
import br.com.ischool.controller.datamodel.ClasseDataModel;
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
public class ClasseBean extends AbstractViewHelper<Classe> implements Serializable{

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
	
	private ClasseDataModel classeDataModel = new ClasseDataModel();
	
	private DualListModel<Usuario>	   professores;
	private List<Usuario> 		       listaUsuarios;
	
	private DualListModel<Aluno>	   alunos;
	private List<Aluno> 		       listaAlunos;

			
	
	@Override
	public void inicializar() throws WebException {
				
		setEntidade(new Classe());	
		setFiltro(new Classe());
		getFiltro().setCliente(new Cliente());	
		professores = new DualListModel<Usuario>();
		alunos = new DualListModel<Aluno>();
		
		
		Usuario usuario = FacesUtil.recuperarUsuarioSessao();
		
		// SE O USUARIO FOR ADMIN GERAL NAO TERA UM CLIENTE ASSOCIADO
		if(usuario.getCliente() != null){
			
			if(filtro == null){
				filtro = new Classe();
				filtro.setCliente(new Cliente());
			}
			filtro.getCliente().setIdCliente(usuario.getCliente().getIdCliente());
		}
		
		classeDataModel.setClasseServiceLocal(classeService);
		classeDataModel.setFiltro(filtro);



	}
	
	public void carregarListaProfessores() throws ServicoException,Exception{
	
			Usuario filtro = new Usuario();
			
			filtro.setCliente(new Cliente());
			filtro.setAtivo(true);
			filtro.getCliente().setIdCliente(FacesUtil.recuperarUsuarioSessao().getCliente().getIdCliente());
			filtro.setSemClassesAtribuidas(true);
			filtro.setAutoridade(Constantes.PROFESSOR);
			
			listaUsuarios	= (List<Usuario>) usuarioService.listarUsuarios(filtro, null);
			
	}
	
	public void carregarListaAlunos() throws ServicoException,Exception{
		try{
			Aluno filtro = new Aluno();
			
			filtro.setCliente(new Cliente());
			filtro.setAtivo(true);
			filtro.setSemClassesAtribuidas(true);
			filtro.getCliente().setIdCliente(FacesUtil.recuperarUsuarioSessao().getCliente().getIdCliente());
			
			listaAlunos =  (List<Aluno>) alunoService.listarAlunos(filtro, null);
		} catch (ServicoException se) {
			FacesUtil.exibirErro("erro_carregar_listas",se);	
			se.printStackTrace();
		}
	}

	@Override
	public void pesquisar() {
		
		//classeDataModel.setFiltro(filtro);
		
	}

	@Override
	public void reset() {
		setEntidade(new Classe());	
	}
	
	public void novoClasseCliente(){
		
		try {
		
			reset();
			carregarListaProfessores();
			carregarListaAlunos();
	
			professores.setSource(listaUsuarios);
			professores.setTarget(new ArrayList<Usuario>());
			
			alunos.setSource(listaAlunos);
			alunos.setTarget(new ArrayList<Aluno>());
			
			Usuario usuario = FacesUtil.recuperarUsuarioSessao();		
			getEntidade().setCliente(usuario.getCliente());		
		
		}catch (ServicoException se) {
			FacesUtil.exibirErro("erro_carregar_listas",se);	
			se.printStackTrace();
		}catch(Exception e){			
			FacesUtil.exibirErro("erro_carregar_listas",e);	
			e.printStackTrace();
		}
		
	}
	
	public void editar(){
		
		try {

			carregarListaProfessores();
			carregarListaAlunos();

			
			professores.setSource(listaUsuarios);
			alunos.setSource(listaAlunos);

			entidade = servicoGenerico.obterListaLazy(entidade, Classe.class, "usuarios");
			professores.setTarget(new ArrayList<Usuario>());
			
			for(Usuario prof:entidade.getUsuarios()){
				
				if(prof.getAtivo()){
					professores.getTarget().add(prof);
				}
			}
			
			professores.getSource().removeAll(entidade.getUsuarios());
			
			entidade = servicoGenerico.obterListaLazy(entidade, Classe.class, "alunos");
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

	@Override
	public void salvarImpl() throws ServicoException {
		
		entidade.setUsuarios(professores.getTarget());
		entidade.setAlunos(alunos.getTarget());
		
		
		classeService.salvarClasse(entidade);	
		
	}

	@Override
	public void alterarImpl() throws ServicoException {
		
		entidade.setUsuarios(professores.getTarget());
		entidade.setAlunos(alunos.getTarget());
		
		classeService.alterarClasse(entidade);	
		
	}

	@Override
	public void excluirImpl() throws ServicoException {
		
		classeService.excluirClasse(entidade);
		
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

	public DualListModel<Usuario> getProfessores() {
		return professores;
	}

	public void setProfessores(DualListModel<Usuario> professores) {
		this.professores = professores;
	}

	public DualListModel<Aluno> getAlunos() {
		return alunos;
	}

	public void setAlunos(DualListModel<Aluno> alunos) {
		this.alunos = alunos;
	}

	public ClasseDataModel getClasseDataModel() {
		return classeDataModel;
	}

	public void setClasseDataModel(ClasseDataModel classeDataModel) {
		this.classeDataModel = classeDataModel;
	}




}
