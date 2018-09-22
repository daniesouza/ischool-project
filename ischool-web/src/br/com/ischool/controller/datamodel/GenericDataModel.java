package br.com.ischool.controller.datamodel;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;

import br.com.ischool.entity.Entidade;
import br.com.ischool.exceptions.ServicoException;
import br.com.ischool.util.FacesUtil;
import br.com.ischool.util.Paginacao;

public abstract class GenericDataModel<T extends Entidade> extends LazyDataModel<T>{

	

	/**
	 * 
	 */
	private static final long serialVersionUID = 4761742662242459196L;

	private T filtro;
	
	private List<T> resultado = new ArrayList<T>();

	public abstract List<T> obterResultados(T filtro, Paginacao paginacao) throws ServicoException;
	
	@Override
	public List<T> load(int inicio, int registrosPorPagina, String sortField,SortOrder sortOrder, Map<String, Object> filters) {

		try {
			
			Paginacao paginacao = new Paginacao(inicio);
			paginacao.setQtdeRegistroPorPagina(registrosPorPagina);				
			resultado = obterResultados(filtro, paginacao);
			setPageSize(registrosPorPagina);
			setRowCount(paginacao.getTotalRegistros());
			
		} catch (ServicoException se) {
			FacesUtil.exibirErro("erro_carregar_listas",se);	
			se.printStackTrace();
		}catch (Exception e) {
			FacesUtil.exibirErro("erro_carregar_listas",e);	
			e.printStackTrace();
		}
		

		return resultado;
	}
	

	
	

	public T getFiltro() {
		return filtro;
	}

	public void setFiltro(T filtro) {
		this.filtro = filtro;
	}

	@Override
	public Object getRowKey(T t) {
		return t.getId();
	}

	@Override
	public T getRowData(String idUsuario) {
		Integer id = Integer.valueOf(idUsuario);

		for (T t : resultado) {
			if(id.equals(t.getId())){
				return t;
			}
		}

		return null;
	}
	
	@Override
	public void setRowIndex(int rowIndex) {
		if(getPageSize() != 0) {
			int newRowIndex = rowIndex == -1 ? rowIndex : (rowIndex % getPageSize());
			super.setRowIndex(newRowIndex);
		}
	}
}
