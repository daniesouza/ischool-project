package br.com.ischool.controller.datamodel;

import java.util.List;

import br.com.ischool.business.ClasseServiceLocal;
import br.com.ischool.entity.Classe;
import br.com.ischool.exceptions.ServicoException;
import br.com.ischool.util.Paginacao;

public class ClasseDataModel extends GenericDataModel<Classe> {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 7624444062873031697L;
	
	private ClasseServiceLocal classeServiceLocal;
	
	@Override
	public List<Classe> obterResultados(Classe filtro, Paginacao paginacao) throws ServicoException {
		
		List<Classe> listaClasse = (List<Classe>) classeServiceLocal.listarClasses(filtro, paginacao);
			
		return listaClasse;

	}

	public ClasseServiceLocal getClasseServiceLocal() {
		return classeServiceLocal;
	}



	public void setClasseServiceLocal(ClasseServiceLocal classeServiceLocal) {
		this.classeServiceLocal = classeServiceLocal;
	}



	public static long getSerialversionuid() {
		return serialVersionUID;
	}


	

}
