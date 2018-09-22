package br.com.ischool.dao;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import br.com.ischool.exceptions.DAOException;
import br.com.ischool.util.Paginacao;

/**
 * @author Daniel Souza de lima e-mail:daniesouza@gmail.com
 * Session Bean implementation class GenericDAOImpl
 */

@Stateless
@Local(value=GenericDAOLocal.class)
public class GenericDAOImpl<Entity, PK extends Serializable> implements	GenericDAOLocal<Entity, PK> {

	@PersistenceContext
	private EntityManager entityManager;
	
	
    @PostConstruct
    public void carregarInformacoes()
    {
    	System.out.println("CARREGADO OS RECURSOS DO EJB "+this.getClass().getName());
    }
    
    @PreDestroy
    public void clear()
    {
    	System.out.println("LIBERANDO OS RECURSOS DO EJB "+this.getClass().getName());
    }

	@Override
	public Entity save(Entity entity) throws DAOException {

		try{
			this.entityManager.persist(entity);
		}catch (Exception e) {
			throw  new DAOException(e);
		}

		return entity;

	}

	@Override
	public Entity find(Class<Entity> entityClass, PK id) throws DAOException {

		try{
			return this.entityManager.find(entityClass, id);
		}catch (Exception e) {
			throw  new DAOException(e);
		}

	}
	
	@Override
	public void detach(Entity entity)throws DAOException {
		
		try{
			this.entityManager.detach(entity);
		}catch (Exception e) {
			throw  new DAOException(e);
		}
	}

	@Override
	public void delete(Entity entity) throws DAOException {

		try{
			this.entityManager.remove(this.entityManager.merge(entity)); // TODO WTF?? Verificar se precisa mesmo de merge antes
		}catch (Exception e) {
			throw  new DAOException(e);
		}

	}

	@Override
	public Entity update(Entity entity) throws DAOException {

		try{
			return entityManager.merge(entity);
		}catch (Exception e) {
			throw  new DAOException(e);
		}
	}
	
	
	@SuppressWarnings("unchecked")
	@Override
	public List<Entity> getAll(String classe) throws DAOException { //DAOEXCEPTION
		List<Entity> result = new ArrayList<Entity>();
		try {
			Query query = entityManager.createQuery("from "+classe);
			result = query.getResultList();
		} catch (Exception e) {
			throw  new DAOException(e);
		}
		return result;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public Entity loadLazyRelationship(Object myEntity, String classe, String relacionamento) throws DAOException {

		Entity objeto;
		try {
			StringBuilder sql = new StringBuilder();
			sql.append("select o from ");
			sql.append(classe);
			sql.append(" o LEFT JOIN FETCH o.");
			sql.append(relacionamento);
			sql.append(" r where o = :myEntity");

			String sqlStr = sql.toString();
			Query query = entityManager.createQuery(sqlStr);
			query.setParameter("myEntity", myEntity);
			objeto = (Entity) query.getResultList().get(0);
		} catch (Exception e) {
			throw new DAOException(e);
		}        
		return objeto;		
	}

	@Override
	public int count(Object myEntity, String classe, String relacionamento) throws DAOException {
		
		StringBuilder sql = new StringBuilder();
		sql.append("select count(o) from ");
		sql.append(classe);
		sql.append(" o where o.");
		sql.append(relacionamento);
		sql.append(" = :myEntity");
		
		Query query = entityManager.createQuery(sql.toString());
		query.setParameter("myEntity", myEntity);
		Number count = (Number) query.getSingleResult();
		return count.intValue();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Entity> getAll(String classe, Paginacao paginacao, String ordenacao) throws DAOException {
		List<Entity> result = new ArrayList<Entity>();
		try {
			Query queryCount = entityManager.createQuery("select count(o) from "+classe+" o");
			Number totalRegistros = (Number) queryCount.getSingleResult();
			paginacao.setTotalRegistros(totalRegistros.intValue());
			
			Query query = entityManager.createQuery("from "+classe+" order by "+ordenacao+" desc");
			query.setFirstResult(paginacao.getInicio());
			query.setMaxResults(paginacao.getQtdeRegistroPorPagina());			
			result = query.getResultList();
			
		} catch (Exception e) {
			throw new DAOException(e);
		}
		return result;
	}	

	public EntityManager getEntityManager() {
		return entityManager;
	}

	public void setEntityManager(EntityManager entityManager) {
		this.entityManager = entityManager;
	}

}
