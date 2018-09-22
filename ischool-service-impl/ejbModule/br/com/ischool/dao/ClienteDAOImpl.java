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

import br.com.ischool.entity.Cliente;
import br.com.ischool.exceptions.DAOException;
import br.com.ischool.util.DadosUtil;
import br.com.ischool.util.Paginacao;
import br.com.ischool.util.QueryUtil;

/**
 *  @author Daniel Souza de lima e-mail:daniesouza@gmail.com
 * Session Bean implementation class ClienteDAOImpl
 */
@Stateless
@Local(value=ClienteDAOLocal.class)
public class ClienteDAOImpl extends GenericDAOImpl<Cliente,Long> implements ClienteDAOLocal {

    /**
     * Default constructor. 
     */
    public ClienteDAOImpl() {
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
	public void salvarCliente(Cliente cliente) throws DAOException{
			
		try{
			
			save(cliente);
		
		}catch(Exception e){
			throw new DAOException(e);
		}
	
	}
	
	@Override
	public void alterarCliente(Cliente cliente) throws DAOException {
	
		try{
			
			update(cliente);
			
		}catch(Exception e){
			throw new DAOException(e);
		}
		
	}


	
	@Override
	public Collection<Cliente> listarClientes() throws DAOException{
		
		try{			
	        return  getAll("Cliente");
	        
		}catch(Exception e){
			throw new DAOException(e);
		}

	}
	
	@Override
	public Collection<Cliente> listarClientes(Cliente filtro,Paginacao paginacao) throws DAOException{
		
		try{
			
			QueryUtil queryCount = queryConsultarCliente(filtro, true);
			
			Number totalRegistros;
			
			try {
				totalRegistros = (Number) queryCount.obterQuery(getEntityManager()).getSingleResult();			
			} catch (NoResultException e) {
				totalRegistros = 0;
			}
			paginacao.setTotalRegistros(totalRegistros.intValue());
			
			QueryUtil queryRegistros = queryConsultarCliente(filtro, false);
			
			queryRegistros.setPaginacao(paginacao);
			
			Query query = queryRegistros.obterQuery(getEntityManager());
			
			@SuppressWarnings("unchecked")
			List<Cliente> clientes = query.getResultList();
			
			return clientes;

		}catch(Exception e){
			throw new DAOException(e);
		}

	}
	
	
	private QueryUtil queryConsultarCliente(Cliente filtro, boolean count) {
	
		String sql = "SELECT u FROM Cliente u";
		QueryUtil queryUtil = new QueryUtil(sql, false, count);	
		
		
		if(!DadosUtil.isEmpty(filtro)) {
			
			if(!DadosUtil.isEmpty(filtro.getId())) {
				queryUtil.adicionarFiltroExato("u.id", filtro.getId());
				
				return queryUtil;
			}
			
			if(!DadosUtil.isEmpty(filtro.getRazaoSocial())) {
				queryUtil.adicionarFiltroAproximado("u.razaoSocial", filtro.getRazaoSocial());
			}
			
			if(!DadosUtil.isEmpty(filtro.getNome())) {
				queryUtil.adicionarFiltroAproximado("u.nome", filtro.getNome());
			}
			
			if(!DadosUtil.isEmpty(filtro.getCnpj())) {
				queryUtil.adicionarFiltroExato("u.cnpj", filtro.getCnpj());
			}
			
			if(!DadosUtil.isEmpty(filtro.getEndereco())) {
				queryUtil.adicionarFiltroAproximado("u.endereco", filtro.getEndereco());
			}
			
			
			if(!DadosUtil.isEmpty(filtro.getTelefone())) {
				queryUtil.adicionarFiltroAproximado("u.telefone", filtro.getTelefone());
			}
			
			if(!DadosUtil.isEmpty(filtro.getDtCad())) {
				
				Calendar dataInicio = Calendar.getInstance();
				dataInicio.setTime(filtro.getDtCad());
				dataInicio.set(Calendar.HOUR_OF_DAY, 0);
				dataInicio.set(Calendar.MINUTE, 0);
				dataInicio.set(Calendar.SECOND, 0);
				dataInicio.set(Calendar.MILLISECOND, 0);
				
				Calendar dataFim = Calendar.getInstance();
				dataFim.setTime(filtro.getDtCad());
				dataFim.set(Calendar.HOUR_OF_DAY, 23);
				dataFim.set(Calendar.MINUTE, 59);
				dataFim.set(Calendar.SECOND, 59);
				dataFim.set(Calendar.MILLISECOND, 999);
				
				queryUtil.adicionarFiltroEntre("u.dtCad", dataInicio.getTime(),dataFim.getTime());
			}
			
			if(!DadosUtil.isEmpty(filtro.getEmail())) {
				queryUtil.adicionarFiltroAproximado("u.email", filtro.getEmail());
			}
			
			if(!DadosUtil.isEmpty(filtro.getAtivo())) {
				queryUtil.adicionarFiltroExato("u.ativo", filtro.getAtivo());
			}
						

		}
		return queryUtil;
	}
	

	@Override
	public int consultarQtde(Cliente filtro) throws DAOException {
		try{
			QueryUtil queryCount = queryConsultarCliente(filtro, true);
			Number totalRegistros = (Number) queryCount.obterQuery(getEntityManager()).getSingleResult();
			return totalRegistros.intValue();
		}catch(Exception e){
			throw new DAOException(e);
		}
	}

	@Override
	public void excluirCliente(Cliente Cliente) throws DAOException {
		try{
			delete(Cliente);
		}catch(Exception e){
			throw new DAOException(e);
		}
	}

	@Override
	public Cliente selectById(Long id) throws DAOException {
		try{
		return find(Cliente.class, id);
		}catch(Exception e){
			throw new DAOException(e);
		}
	}

    
}
