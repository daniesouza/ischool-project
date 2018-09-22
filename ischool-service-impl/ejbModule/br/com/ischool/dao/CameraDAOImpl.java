package br.com.ischool.dao;

import java.util.Collection;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import br.com.ischool.entity.Camera;
import br.com.ischool.exceptions.DAOException;
import br.com.ischool.util.DadosUtil;
import br.com.ischool.util.Paginacao;
import br.com.ischool.util.QueryUtil;

/**
 * Session Bean implementation class CameraDAOImpl
 *  @author Daniel Souza de lima e-mail:daniesouza@gmail.com
 */
@Stateless
@Local(value=CameraDAOLocal.class)
public class CameraDAOImpl extends GenericDAOImpl<Camera,Long> implements CameraDAOLocal {

    /**
     * Default constructor. 
     */
    public CameraDAOImpl() {
        // TODO Auto-generated constructor stub
    }

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
	public void salvarCamera(Camera camera) throws DAOException{
			
		try{
			
			save(camera);
		
		}catch(Exception e){
			throw new DAOException(e);
		}
	
	}
	
	@Override
	public void alterarCamera(Camera camera) throws DAOException {
	
		try{
			
			update(camera);
			
		}catch(Exception e){
			throw new DAOException(e);
		}
		
	}


	
	@Override
	public Collection<Camera> listarCameras() throws DAOException{
		
		try{			
	        return  getAll("Camera");
	        
		}catch(Exception e){
			throw new DAOException(e);
		}

	}
	
	@Override
	public Collection<Camera> listarCameras(Camera filtro,Paginacao paginacao) throws DAOException{
		
		try{
			
			QueryUtil queryCount = queryConsultarCamera(filtro, true);
			
			Number totalRegistros;
			
			try {
				totalRegistros = (Number) queryCount.obterQuery(getEntityManager()).getSingleResult();			
			} catch (NoResultException e) {
				totalRegistros = 0;
			}
			paginacao.setTotalRegistros(totalRegistros.intValue());
			
			QueryUtil queryRegistros = queryConsultarCamera(filtro, false);
			
			queryRegistros.setPaginacao(paginacao);
			
			Query query = queryRegistros.obterQuery(getEntityManager());
			
			@SuppressWarnings("unchecked")
			List<Camera> Cameras = query.getResultList();
			
			return Cameras;

		}catch(Exception e){
			throw new DAOException(e);
		}

	}
	
	
	private QueryUtil queryConsultarCamera(Camera filtro, boolean count) {
	
		String sql = "SELECT u FROM Camera u";
		QueryUtil queryUtil = new QueryUtil(sql, false, count);	
		
		
		if(!DadosUtil.isEmpty(filtro)) {
			
			if(!DadosUtil.isEmpty(filtro.getId())) {
				queryUtil.adicionarFiltroAproximado("u.id", filtro.getId());
				
				return queryUtil;
			}
			
			if(!DadosUtil.isEmpty(filtro.getCodigo())) {
				queryUtil.adicionarFiltroAproximado("u.codigo", filtro.getCodigo());
			}
			
			if(!DadosUtil.isEmpty(filtro.getNome())) {
				queryUtil.adicionarFiltroAproximado("u.nome", filtro.getNome());
			}
			
			if(!DadosUtil.isEmpty(filtro.getIp())) {
				queryUtil.adicionarFiltroAproximado("u.ip", filtro.getIp());
			}

			

		}
		return queryUtil;
	}

	@Override
	public void excluirCamera(Camera Camera) throws DAOException {
		try{
			delete(Camera);
		}catch(Exception e){
			throw new DAOException(e);
		}
	}

	
	@Override
	public Camera selectById(Long id) throws DAOException {
		
		return find(Camera.class, id);
	}
	
	@Override
	public int consultarQtde(Camera filtro) throws DAOException {
		QueryUtil queryCount = queryConsultarCamera(filtro, true);
		Number totalRegistros = (Number) queryCount.obterQuery(getEntityManager()).getSingleResult();
		return totalRegistros.intValue();
	}
    
}
