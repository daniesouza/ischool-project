package br.com.ischool.dao;

import java.util.Calendar;
import java.util.Collection;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import br.com.ischool.entity.DeviceRegId;
import br.com.ischool.exceptions.DAOException;
import br.com.ischool.util.DadosUtil;
import br.com.ischool.util.Paginacao;
import br.com.ischool.util.QueryUtil;

/**
 * @author Daniel Souza de lima e-mail:daniesouza@gmail.com
 * Session Bean implementation class DeviceRegIdService
 */
@Stateless
@Local(DeviceRegDAOLocal.class)
public class DeviceRegDAOImpl extends GenericDAOImpl<DeviceRegId,Long> implements DeviceRegDAOLocal {


    /**
     * Default constructor. 
     */
    public DeviceRegDAOImpl() {
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
	public void salvarDeviceRegId(DeviceRegId deviceRegId) throws DAOException{
			
		try{
			
			save(deviceRegId);
		
		}catch(Exception e){
			throw new DAOException(e);
		}
	
	}
	
	@Override
	public void alterarDeviceRegId(DeviceRegId deviceRegId) throws DAOException {
	
		try{
			
			update(deviceRegId);
			
		}catch(Exception e){
			throw new DAOException(e);
		}
		
	}


	
	@Override
	public Collection<DeviceRegId> listarDeviceRegIds() throws DAOException{
		
		try{			
	        return  getAll("DeviceRegId");
	        
		}catch(Exception e){
			throw new DAOException(e);
		}

	}
	
	@Override
	public Collection<DeviceRegId> listarDeviceRegIds(DeviceRegId filtro,Paginacao paginacao) throws DAOException{
		
		try{
			
			if(paginacao.getInicio() >-1){
				
				QueryUtil queryCount = queryConsultarDeviceRegId(filtro, true);
				
				Number totalRegistros;
				
				try {
					totalRegistros = (Number) queryCount.obterQuery(getEntityManager()).getSingleResult();			
				} catch (NoResultException e) {
					totalRegistros = 0;
				}
				paginacao.setTotalRegistros(totalRegistros.intValue());
			}
			
			QueryUtil queryRegistros = queryConsultarDeviceRegId(filtro, false);
			
			queryRegistros.setPaginacao(paginacao);
			
			Query query = queryRegistros.obterQuery(getEntityManager());
			
			@SuppressWarnings("unchecked")
			List<DeviceRegId> deviceRegIds = query.getResultList();
			
			return deviceRegIds;

		}catch(Exception e){
			throw new DAOException(e);
		}

	}
	
	
	private QueryUtil queryConsultarDeviceRegId(DeviceRegId filtro, boolean count) {
	
		String sql = "SELECT u FROM DeviceRegId u";
		QueryUtil queryUtil = new QueryUtil(sql, false, count);	
		
		
		if(!DadosUtil.isEmpty(filtro)) {
			
			if(!DadosUtil.isEmpty(filtro.getId())) {
				queryUtil.adicionarFiltroExato("u.id", filtro.getId());
				
				return queryUtil;
			}
			
			if(!DadosUtil.isEmpty(filtro.getDataValidadeHash())) {
				queryUtil.adicionarFiltroExato("u.autoridade", filtro.getDataValidadeHash());
			}
			
			if(!DadosUtil.isEmpty(filtro.getRegid())) {
				queryUtil.adicionarFiltroExato("u.regid", filtro.getRegid());
			}
			
			if(!DadosUtil.isEmpty(filtro.getSecurityHashKey())) {
				queryUtil.adicionarFiltroAproximado("u.securityHashKey", filtro.getSecurityHashKey());
			}
					
			
			if(!DadosUtil.isEmpty(filtro.getDevice())) {
				
				if(!DadosUtil.isEmpty(filtro.getDevice().getId())) {
					queryUtil.adicionarFiltroExato("u.device.id", filtro.getDevice().getId());
				}				
				
				if(!DadosUtil.isEmpty(filtro.getDevice().getDevice())) {
					queryUtil.adicionarFiltroAproximado("u.device.device", filtro.getDevice().getDevice());
				}
			}
			
			if(DadosUtil.isEmpty(filtro.getUsuario())){
				
				if(!DadosUtil.isEmpty(filtro.getUsuario().getId())) {
					queryUtil.adicionarFiltroExato("u.usuario.id", filtro.getUsuario().getId());
					
					return queryUtil;
				}
				
				if(!DadosUtil.isEmpty(filtro.getUsuario().getAutoridade())) {
					queryUtil.adicionarFiltroExato("u.usuario.autoridade", filtro.getUsuario().getAutoridade());
				}
				
				if(!DadosUtil.isEmpty(filtro.getUsuario().getUsuario())) {
					queryUtil.adicionarFiltroExato("u.usuario.usuario", filtro.getUsuario().getUsuario());
				}
				
				if(!DadosUtil.isEmpty(filtro.getUsuario().getNome())) {
					queryUtil.adicionarFiltroAproximado("u.usuario.nome", filtro.getUsuario().getNome());
				}
				
				if(!DadosUtil.isEmpty(filtro.getUsuario().getDtCad())) {
					
					Calendar dataInicio = Calendar.getInstance();
					dataInicio.setTime(filtro.getUsuario().getDtCad());
					dataInicio.set(Calendar.HOUR_OF_DAY, 0);
					dataInicio.set(Calendar.MINUTE, 0);
					dataInicio.set(Calendar.SECOND, 0);
					dataInicio.set(Calendar.MILLISECOND, 0);
					
					Calendar dataFim = Calendar.getInstance();
					dataFim.setTime(filtro.getUsuario().getDtCad());
					dataFim.set(Calendar.HOUR_OF_DAY, 23);
					dataFim.set(Calendar.MINUTE, 59);
					dataFim.set(Calendar.SECOND, 59);
					dataFim.set(Calendar.MILLISECOND, 999);
					
					queryUtil.adicionarFiltroEntre("u.usuario.dtCad", dataInicio.getTime(),dataFim.getTime());
				}
				
				if(!DadosUtil.isEmpty(filtro.getUsuario().getEndereco())) {
					queryUtil.adicionarFiltroAproximado("u.usuario.endereco", filtro.getUsuario().getEndereco());
				}
				if(!DadosUtil.isEmpty(filtro.getUsuario().getTelefone())) {
					queryUtil.adicionarFiltroAproximado("u.usuario.telefone", filtro.getUsuario().getTelefone());
				}
				
				if(!DadosUtil.isEmpty(filtro.getUsuario().getAtivo())) {
					queryUtil.adicionarFiltroExato("u.usuario.ativo", filtro.getUsuario().getAtivo());
				}
				
				if(!DadosUtil.isEmpty(filtro.getUsuario().getCpf())) {
					queryUtil.adicionarFiltroExato("u.usuario.cpf", filtro.getUsuario().getCpf());
				}
				
				if(!DadosUtil.isEmpty(filtro.getUsuario().getRg())) {
					queryUtil.adicionarFiltroExato("u.usuario.rg", filtro.getUsuario().getRg());
				}	
			}

		}
		return queryUtil;
	}

	
	@Override
	public DeviceRegId selectById(Long id) throws DAOException {
		try {
			return find(DeviceRegId.class, id);
		} catch (Exception e) {
			throw new DAOException(e);
		}

	}
	

	@Override
	public int consultarQtde(DeviceRegId filtro) throws DAOException {

		try {
			QueryUtil queryCount = queryConsultarDeviceRegId(filtro, true);
			Number totalRegistros = (Number) queryCount.obterQuery(getEntityManager()).getSingleResult();
			return totalRegistros.intValue();

		} catch (Exception e) {
			throw new DAOException(e);
		}
	}

	@Override
	public void excluirDeviceRegId(DeviceRegId deviceRegId) throws DAOException {
		try{
			delete(deviceRegId);
		}catch(Exception e){
			throw new DAOException(e);
		}
	}

	@Override
	public DeviceRegId pesquisarDeviceRegIdNomeDeviceRegId(String regId) throws DAOException{		        		
		try{		
			return (DeviceRegId) getEntityManager().createQuery("Select u from DeviceRegId u where u.regid = :regId").setParameter("regId", regId).getSingleResult();
		}catch (NoResultException e) {
			return null;
		}catch(Exception e){			
			throw new DAOException(e);
		}
	
	}

	@Override
	public DeviceRegId selectBySecurityHash(String hash) throws DAOException {
		try{		
			return (DeviceRegId) getEntityManager().createQuery("Select u from DeviceRegId u where u.securityHashKey = :securityHashKey").setParameter("securityHashKey", hash).getSingleResult();
		}catch (NoResultException e) {
			return null;
		}catch(Exception e){			
			throw new DAOException(e);
		}
	}

	@Override
	public void deleteByIds(Collection<Long> idsIN) throws DAOException {
		getEntityManager().createQuery("DELETE from DeviceRegId u where u.idReg IN (:idsIN)").setParameter("idsIN", idsIN).executeUpdate();
	}

}
