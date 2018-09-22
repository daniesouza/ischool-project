package br.com.ischool.controller;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;

import br.com.ischool.business.EventoExecutadoServiceLocal;
import br.com.ischool.entity.Aluno;
import br.com.ischool.entity.Classe;
import br.com.ischool.entity.Entidade;
import br.com.ischool.exceptions.ServicoException;
import br.com.ischool.exceptions.WebException;
import br.com.ischool.util.FacesUtil;

/**
 * @author Daniel Souza de lima e-mail:daniesouza@gmail.com
 *      
 */

@ManagedBean
@SessionScoped
public class HistoricoBean extends AbstractViewHelper<Entidade> implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -1281611039656933934L;
	
	private List<Aluno> listaAlunosClasse = new ArrayList<>();
	
	private Aluno alunoSelecionado;
	
	private Date dataSelecionada;
	
	@EJB
	private EventoExecutadoServiceLocal eventoExecutadoService;
	
	@Override
	public void inicializar() throws WebException {
		
		 dataSelecionada = new Date();
		 listaAlunosClasse = new ArrayList<>();
		 
		 System.out.println("Iniciou");
	}
	
	
	@Override
	public void reset() {

	}

	
	public void carregarHistoricoAluno() throws IOException{
		
		listaAlunosClasse.clear();
	
		listaAlunosClasse.add(alunoSelecionado);

		 FacesContext context = FacesContext.getCurrentInstance();  
		 context.getExternalContext().redirect("agenda.jsf");
	
		//return "agenda?faces-redirect=true";
	}
	
	public void carregarHistoricoClasse(Classe classeSelecionada) throws IOException{
		
		try{
			listaAlunosClasse = (List<Aluno>) eventoExecutadoService.carregarSessaoAlunoProfessor(classeSelecionada,new Date());
		} catch (ServicoException se) {
			FacesUtil.exibirErro("erro_carregar_listas",se);	
			se.printStackTrace();
		}catch(Exception e){			
			FacesUtil.exibirErro("erro_carregar_listas",e);	
			e.printStackTrace();			
		}
	
		 FacesContext context = FacesContext.getCurrentInstance();  
		 context.getExternalContext().redirect("agenda.jsf");
	
		//return "agenda?faces-redirect=true";
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


	public List<Aluno> getListaAlunosClasse() {
		return listaAlunosClasse;
	}


	public void setListaAlunosClasse(List<Aluno> listaAlunosClasse) {
		this.listaAlunosClasse = listaAlunosClasse;
	}


	public Aluno getAlunoSelecionado() {
		return alunoSelecionado;
	}


	public void setAlunoSelecionado(Aluno alunoSelecionado) {
		this.alunoSelecionado = alunoSelecionado;
	}


	public static long getSerialversionuid() {
		return serialVersionUID;
	}


	public Date getDataSelecionada() {
		return dataSelecionada;
	}


	public void setDataSelecionada(Date dataSelecionada) {
		this.dataSelecionada = dataSelecionada;
	}	

}
