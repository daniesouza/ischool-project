package br.com.ischool.controller;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import br.com.ischool.business.EventoExecutadoServiceLocal;
import br.com.ischool.business.GenericServiceLocal;
import br.com.ischool.entity.Aluno;
import br.com.ischool.entity.Classe;
import br.com.ischool.entity.EventoExecutado;
import br.com.ischool.exceptions.ServicoException;
import br.com.ischool.exceptions.WebException;
import br.com.ischool.util.FacesUtil;

/**
 * @author Daniel Souza de lima e-mail:daniesouza@gmail.com
 *      
 */

@ManagedBean
@ViewScoped
public class GerenciadorAlunoBean extends AbstractViewHelper<EventoExecutado> implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 6390001812920897476L;
	
		
	@EJB
	private EventoExecutadoServiceLocal eventoExecutadoService;
	
	@EJB
	private GenericServiceLocal servicoGenerico;

	private List<Classe>	   listaClasses;
	private List<Aluno>		   listaAlunos;
	
	
	
	@Override
	public void inicializar() throws WebException {
			
		setEntidade(new EventoExecutado());
		carregarSessaoClasse();		
	}
	
	
	@Override
	public void reset() {

	}


	
	public void carregarSessaoClasse(){
		
		try{
			listaAlunos = (List<Aluno>) eventoExecutadoService.carregarSessaoAlunoResponsavel(FacesUtil.recuperarUsuarioSessao(), new Date());
			
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


	}


	@Override
	public void alterarImpl() throws ServicoException {
				
	}

	@Override
	public void excluirImpl() throws ServicoException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void pesquisar() {
		// TODO Auto-generated method stub
		
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
	

}
