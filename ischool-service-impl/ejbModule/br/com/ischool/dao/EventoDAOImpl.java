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

import br.com.ischool.entity.Evento;
import br.com.ischool.exceptions.DAOException;
import br.com.ischool.util.DadosUtil;
import br.com.ischool.util.Paginacao;
import br.com.ischool.util.QueryUtil;

/**
 *  @author Daniel Souza de lima e-mail:daniesouza@gmail.com
 * Session Bean implementation class EventoDAOImpl
 */
@Stateless
@Local(value=EventoDAOLocal.class)
public class EventoDAOImpl extends GenericDAOImpl<Evento,Long> implements EventoDAOLocal {

    /**
     * Default constructor. 
     */
    public EventoDAOImpl() {
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
	public void salvarEvento(Evento evento) throws DAOException{
			
		try{
			
			save(evento);
		
		}catch(Exception e){
			throw new DAOException(e);
		}
	
	}
	
	@Override
	public void alterarEvento(Evento evento) throws DAOException {
	
		try{
			
			update(evento);
			
		}catch(Exception e){
			throw new DAOException(e);
		}
		
	}


	
	@Override
	public Collection<Evento> listarEventos() throws DAOException{
		
		try{			
	        return  getAll("Evento");
	        
		}catch(Exception e){
			throw new DAOException(e);
		}

	}
	
	@Override
	public Collection<Evento> listarEventos(Evento filtro,Paginacao paginacao) throws DAOException{
		
		try{
			
			QueryUtil queryCount = queryConsultarEvento(filtro, true);
			
			Number totalRegistros;
			
			try {
				totalRegistros = (Number) queryCount.obterQuery(getEntityManager()).getSingleResult();			
			} catch (NoResultException e) {
				totalRegistros = 0;
			}
			paginacao.setTotalRegistros(totalRegistros.intValue());
			
			QueryUtil queryRegistros = queryConsultarEvento(filtro, false);
			
			queryRegistros.setPaginacao(paginacao);
			
			Query query = queryRegistros.obterQuery(getEntityManager());
			
			@SuppressWarnings("unchecked")
			List<Evento> eventos = query.getResultList();
			
			return eventos;

		}catch(Exception e){
			throw new DAOException(e);
		}

	}
	
	
	private QueryUtil queryConsultarEvento(Evento filtro, boolean count) {
	
		String sql = "SELECT u FROM Evento u";
		QueryUtil queryUtil = new QueryUtil(sql, false, count);	
		
		
		if(!DadosUtil.isEmpty(filtro)) {
			
			if(!DadosUtil.isEmpty(filtro.getId())) {
				queryUtil.adicionarFiltroExato("u.id", filtro.getId());
				
				return queryUtil;
			}
			
			if(!DadosUtil.isEmpty(filtro.getCodigoEvento())) {
				queryUtil.adicionarFiltroAproximado("u.codigoEvento", filtro.getCodigoEvento());
			}			
			
			if(!DadosUtil.isEmpty(filtro.getNome())) {
				queryUtil.adicionarFiltroAproximado("u.nome", filtro.getNome());
			}
				
			if(!DadosUtil.isEmpty(filtro.getIcone())) {
				queryUtil.adicionarFiltroExato("u.icone", filtro.getIcone());
			}
			
			if(!DadosUtil.isEmpty(filtro.getAtivo())) {
				queryUtil.adicionarFiltroExato("u.ativo", filtro.getAtivo());
			}
			
			if(!DadosUtil.isEmpty(filtro.getTipo())) {
				queryUtil.adicionarFiltroExato("u.tipo", filtro.getTipo());
			}
			
			if(!DadosUtil.isEmpty(filtro.getUnidadeMedida())) {
				queryUtil.adicionarFiltroExato("u.unidadeMedida", filtro.getUnidadeMedida());
			}
			
			if(!DadosUtil.isEmpty(filtro.getPreCadastro())) {
				queryUtil.adicionarFiltroExato("u.preCadastro", filtro.getPreCadastro());
			}
			
					
			if(!DadosUtil.isEmpty(filtro.getDataCadastro())) {
				
				Calendar dataInicio = Calendar.getInstance();
				dataInicio.setTime(filtro.getDataCadastro());
				dataInicio.set(Calendar.HOUR_OF_DAY, 0);
				dataInicio.set(Calendar.MINUTE, 0);
				dataInicio.set(Calendar.SECOND, 0);
				dataInicio.set(Calendar.MILLISECOND, 0);
				
				Calendar dataFim = Calendar.getInstance();
				dataFim.setTime(filtro.getDataCadastro());
				dataFim.set(Calendar.HOUR_OF_DAY, 23);
				dataFim.set(Calendar.MINUTE, 59);
				dataFim.set(Calendar.SECOND, 59);
				dataFim.set(Calendar.MILLISECOND, 999);
				
				queryUtil.adicionarFiltroEntre("u.dataCadastro", dataInicio.getTime(),dataFim.getTime());
			}

			
			if(!DadosUtil.isEmpty(filtro.getCliente())) {
				
				if(!DadosUtil.isEmpty(filtro.getCliente().getIdCliente())) {
					queryUtil.adicionarFiltroExatoOuNulo("u.cliente.id", filtro.getCliente().getIdCliente());
				}				
				
				if(!DadosUtil.isEmpty(filtro.getCliente().getRazaoSocial())) {
					queryUtil.adicionarFiltroAproximado("u.cliente.razaoSocial", filtro.getCliente().getRazaoSocial());
				}
				
				if(!DadosUtil.isEmpty(filtro.getCliente().getNome())) {
					queryUtil.adicionarFiltroAproximado("u.cliente.nome", filtro.getCliente().getNome());
				}
				
				if(!DadosUtil.isEmpty(filtro.getCliente().getCnpj())) {
					queryUtil.adicionarFiltroExato("u.cliente.cnpj", filtro.getCliente().getCnpj());
				}
				
				if(!DadosUtil.isEmpty(filtro.getCliente().getEndereco())) {
					queryUtil.adicionarFiltroAproximado("u.cliente.endereco", filtro.getCliente().getEndereco());
				}
				
				
				if(!DadosUtil.isEmpty(filtro.getCliente().getTelefone())) {
					queryUtil.adicionarFiltroAproximado("u.cliente.telefone", filtro.getCliente().getTelefone());
				}
				
				if(!DadosUtil.isEmpty(filtro.getCliente().getDtCad())) {
					
					Calendar dataInicio = Calendar.getInstance();
					dataInicio.setTime(filtro.getCliente().getDtCad());
					dataInicio.set(Calendar.HOUR_OF_DAY, 0);
					dataInicio.set(Calendar.MINUTE, 0);
					dataInicio.set(Calendar.SECOND, 0);
					dataInicio.set(Calendar.MILLISECOND, 0);
					
					Calendar dataFim = Calendar.getInstance();
					dataFim.setTime(filtro.getCliente().getDtCad());
					dataFim.set(Calendar.HOUR_OF_DAY, 23);
					dataFim.set(Calendar.MINUTE, 59);
					dataFim.set(Calendar.SECOND, 59);
					dataFim.set(Calendar.MILLISECOND, 999);
					
					queryUtil.adicionarFiltroEntre("u.cliente.dtCad", dataInicio.getTime(),dataFim.getTime());
				}
				
				if(!DadosUtil.isEmpty(filtro.getCliente().getEmail())) {
					queryUtil.adicionarFiltroAproximado("u.cliente.email", filtro.getCliente().getEmail());
				}
				
				if(!DadosUtil.isEmpty(filtro.getCliente().getAtivo())) {
					queryUtil.adicionarFiltroExato("u.cliente.ativo", filtro.getCliente().getAtivo());
				}
			}
			
			queryUtil.adicionarOrderBy("u.id", "asc");
						

		}
		return queryUtil;
	}

	
	@Override
	public int consultarQtde(Evento filtro) throws DAOException {
		try{
			QueryUtil queryCount = queryConsultarEvento(filtro, true);
			Number totalRegistros = (Number) queryCount.obterQuery(getEntityManager()).getSingleResult();
			return totalRegistros.intValue();
		}catch(Exception e){
			throw new DAOException(e);
		}
	}

	@Override
	public void excluirEvento(Evento Evento) throws DAOException {
		try{
			delete(Evento);
		}catch(Exception e){
			throw new DAOException(e);
		}
	}

	@Override
	public Evento selectById(Long id) throws DAOException {
		try{
		return find(Evento.class, id);
		}catch(Exception e){
			throw new DAOException(e);
		}
	}
    
}
