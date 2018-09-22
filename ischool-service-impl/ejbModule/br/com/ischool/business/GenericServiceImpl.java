package br.com.ischool.business;

import java.util.Collection;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Local;
import javax.ejb.Stateless;

import br.com.ischool.dao.GenericDAOLocal;
import br.com.ischool.entity.Entidade;
import br.com.ischool.exceptions.DAOException;
import br.com.ischool.exceptions.ServicoException;
import br.com.ischool.util.Paginacao;

/**
 * @author Daniel Souza de lima e-mail:daniesouza@gmail.com
 * Session Bean implementation class GenericServiceImpl
 */
@Stateless
@Local(value=GenericServiceLocal.class)
public class GenericServiceImpl implements GenericServiceLocal {

	@SuppressWarnings("rawtypes")
	@EJB
	private GenericDAOLocal dao;	
	


	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public <T> T obterEntidade(Long id, Class clazz) throws ServicoException {
		try {
			T objeto = (T) dao.find(clazz,id);
			return objeto;
		} catch (DAOException e) {
			throw new ServicoException(e.getMessage());
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public <T> Collection<T> obterEntidades(Class clazz) throws ServicoException {
		try {
			Collection<T> objetos = dao.getAll(clazz.getName());
			return objetos;
		} catch (DAOException e) {
			throw new ServicoException(e.getMessage());
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public <T> T obterListaLazy(Object objeto, Class clazz, String relacionamento) throws ServicoException {
		try {
			T objetoRetorno = (T) dao.loadLazyRelationship(objeto, clazz.getName(), relacionamento);
			return objetoRetorno;
		} catch (DAOException e) {
			throw new ServicoException(e.getMessage());
		}
	}

	@SuppressWarnings("rawtypes")
	@Override
	public int count(Object objeto, Class clazz, String relacionamento) throws ServicoException {
		try {
			int count = dao.count(objeto, clazz.getName(), relacionamento);
			return count;
		} catch (DAOException e) {
			throw new ServicoException(e.getMessage());
		}
	}


	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public <T> List<T> obterEntidades(Class clazz, Paginacao paginacao, String ordenacao) throws ServicoException {
		try {
			List<T> objetos = dao.getAll(clazz.getName(), paginacao, ordenacao);
			return objetos;
		} catch (DAOException e) {
			throw new ServicoException(e.getMessage());
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> void remover(Collection<T> objetos) throws ServicoException {
		try {
			for(T obj : objetos) {
				dao.delete(obj);				
			}
		} catch (DAOException e) {
			throw new ServicoException(e.getMessage());
		}
	}
	
	@SuppressWarnings("unchecked")
	public <T> void remover(T objeto) throws ServicoException {
		try {
			dao.delete(objeto);
		} catch (DAOException e) {
			throw new ServicoException(e.getMessage());
		}
	}	

	@SuppressWarnings("unchecked")
	@Override
	public <T extends Entidade> void salvar(T objeto) throws ServicoException {
		try {
			dao.save(objeto);
		} catch (DAOException e) {
			throw new ServicoException(e.getMessage());
		}
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public <T extends Entidade> void salvar(Collection<T> objetos)
			throws ServicoException {
		
		try {
			for(T obj : objetos) {
				dao.save(obj);			
			}
		} catch (DAOException e) {
			throw new ServicoException(e.getMessage());
		}
		
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public <T> void atualizar(T objeto) throws ServicoException {
		try {
			dao.update(objeto);
		} catch (DAOException e) {
			throw new ServicoException(e.getMessage());
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T extends Entidade> void atualizar(Collection<T> objetos)
			throws ServicoException {
		
		try {
			for(T obj : objetos) {
				dao.update(obj);			
			}
		} catch (DAOException e) {
			throw new ServicoException(e.getMessage());
		}
		
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public <T> void detach(T objeto) throws ServicoException {
		
		try {
			dao.detach(objeto);
		} catch (DAOException e) {
			throw new ServicoException(e.getMessage());
		}
	}




}
