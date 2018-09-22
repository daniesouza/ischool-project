package br.com.ischool.controller.datamodel;

import java.util.List;

import br.com.ischool.business.AlunoServiceLocal;
import br.com.ischool.entity.Aluno;
import br.com.ischool.exceptions.ServicoException;
import br.com.ischool.util.Paginacao;

public class AlunoDataModel extends GenericDataModel<Aluno> {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -2575105380004396272L;
	
	private AlunoServiceLocal alunoLocal;
	
	@Override
	public List<Aluno> obterResultados(Aluno filtro, Paginacao paginacao) throws ServicoException {
		
		List<Aluno> listaAluno = (List<Aluno>) alunoLocal.listarAlunos(filtro, paginacao);
			
		return listaAluno;

	}

	public AlunoServiceLocal getAlunoLocal() {
		return alunoLocal;
	}



	public void setAlunoLocal(AlunoServiceLocal AlunoLocal) {
		this.alunoLocal = AlunoLocal;
	}



	public static long getSerialversionuid() {
		return serialVersionUID;
	}


	

}
