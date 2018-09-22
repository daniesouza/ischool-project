package br.com.ischool.controller;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

import javax.annotation.PostConstruct;

import org.primefaces.context.RequestContext;

import br.com.ischool.entity.Entidade;
import br.com.ischool.exceptions.ServicoException;
import br.com.ischool.exceptions.WebException;
import br.com.ischool.util.FacesUtil;
import br.com.ischool.util.WebConstantes;

/**
 * @author Daniel Souza de lima e-mail:daniesouza@gmail.com
 *      
 */

public abstract class AbstractViewHelper<T extends Entidade> implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -3804684865408766365L;

	private int registroPorPagina;


	protected T filtro;
	
	protected T[] selecionados;

	protected T entidade;
	
	

	@PostConstruct
	public final void init() {
		
		try {
		
		setRegistroPorPagina(WebConstantes.REGISTROS_PAGINA);
		
		inicializar();
		
		reset();
		
		} catch (WebException e) {
			FacesUtil.exibirErro(e.getMessage(),e);	
			e.printStackTrace();		

		}

	}
		
	public final void salvarOuAtualizar() {
		
        RequestContext context = RequestContext.getCurrentInstance();  
        boolean error = false; 
        
		if(entidade.getId() == null){
			
			try {
				
				salvarImpl();
			
				FacesUtil.exibirInfo("mensagem_registro_inserido");
				
			} catch (ServicoException se) {
				FacesUtil.exibirErro("erro_incluir",se);				
				error = true;
				se.printStackTrace();
			}catch (Exception e) {				
				FacesUtil.exibirErro("erro_incluir",e);				
				error = true;
				e.printStackTrace();
			}	
		
		}else{
			
			try {
				
				alterarImpl();
						
				FacesUtil.exibirInfo("mensagem_registro_atualizado");
			} catch (ServicoException se) {
				FacesUtil.exibirErro("erro_alterar",se);				
				error = true;
				se.printStackTrace();
			}catch (Exception e) {			
				FacesUtil.exibirErro("erro_alterar",e);				
				error = true;
				e.printStackTrace();
			}

		}
        
        context.addCallbackParam("error", error); 
		  
	}
	

	public final void excluir() {
		
        RequestContext context = RequestContext.getCurrentInstance();  
        boolean error = false; 
        
		try {
			excluirImpl();
			FacesUtil.exibirInfo("mensagem_registro_excluido");
		} catch (ServicoException se) {
			FacesUtil.exibirErro("erro_excluir",se);				
			error = true;
			se.printStackTrace();
		}catch (Exception e) {			
			FacesUtil.exibirErro("erro_excluir",e);				
			error = true;
			e.printStackTrace();
		}
		
		context.addCallbackParam("error", error); 
		
	}
	
	
	public abstract void salvarImpl() throws ServicoException;
	
	public abstract void alterarImpl() throws ServicoException;

	public abstract void excluirImpl() throws ServicoException;
	
	public abstract void inicializar() throws WebException;
	
	public abstract void pesquisar();

	public abstract void reset();
	
	
	public List<T> getSelecionadosAsList() {
		List<T> entidades = Arrays.asList(getSelecionados());
		return entidades;
	}

	public int getRegistroPorPagina() {
		return registroPorPagina;
	}

	public void setRegistroPorPagina(int registroPorPagina) {
		this.registroPorPagina = registroPorPagina;
	}

	public T getFiltro() {
		return filtro;
	}

	public void setFiltro(T filtro) {
		this.filtro = filtro;
	}

	public T[] getSelecionados() {
		return selecionados;
	}

	public void setSelecionados(T[] selecionados) {
		this.selecionados = selecionados;
	}

	public T getEntidade() {
		return entidade;
	}

	public void setEntidade(T entidade) {
		this.entidade = entidade;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}	
	
}
