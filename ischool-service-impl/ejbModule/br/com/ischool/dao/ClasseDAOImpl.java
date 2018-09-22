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

import br.com.ischool.entity.Classe;
import br.com.ischool.exceptions.DAOException;
import br.com.ischool.util.DadosUtil;
import br.com.ischool.util.Paginacao;
import br.com.ischool.util.QueryUtil;

/**
 *  @author Daniel Souza de lima e-mail:daniesouza@gmail.com
 * Session Bean implementation class ClasseDAOImpl
 */
@Stateless
@Local(value=ClasseDAOLocal.class)
public class ClasseDAOImpl extends GenericDAOImpl<Classe,Long> implements ClasseDAOLocal {

    /**
     * Default constructor. 
     */
    public ClasseDAOImpl() {
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
	public void salvarClasse(Classe classe) throws DAOException{
			
		try{
			
			save(classe);
		
		}catch(Exception e){
			throw new DAOException(e);
		}
	
	}
	
	@Override
	public void alterarClasse(Classe classe) throws DAOException {
	
		try{
			
			update(classe);
			
		}catch(Exception e){
			throw new DAOException(e);
		}
		
	}


	
	@Override
	public Collection<Classe> listarClasses() throws DAOException{
		
		try{			
	        return  getAll("Classe");
	        
		}catch(Exception e){
			throw new DAOException(e);
		}

	}
	
	@Override
	public Collection<Classe> listarClasses(Classe filtro,Paginacao paginacao) throws DAOException{
		
		try{
			
			if(paginacao.getInicio() >-1){
				
				QueryUtil queryCount = queryConsultarClasse(filtro, true);
				
				Number totalRegistros;
				
				try {
					totalRegistros = (Number) queryCount.obterQuery(getEntityManager()).getSingleResult();			
				} catch (NoResultException e) {
					totalRegistros = 0;
				}
				paginacao.setTotalRegistros(totalRegistros.intValue());
			}

			QueryUtil queryRegistros = queryConsultarClasse(filtro, false);
			
			queryRegistros.setPaginacao(paginacao);
			
			Query query = queryRegistros.obterQuery(getEntityManager());
			
			@SuppressWarnings("unchecked")
			List<Classe> classes = query.getResultList();
			
			return classes;

		}catch(Exception e){
			throw new DAOException(e);
		}

	}
	
	
	private QueryUtil queryConsultarClasse(Classe filtro, boolean count) {
	
		String sql = "SELECT u FROM Classe u";
		QueryUtil queryUtil = new QueryUtil(sql, false, count);	
		
		
		if(!DadosUtil.isEmpty(filtro)) {
			
			if(!DadosUtil.isEmpty(filtro.getId())) {
				queryUtil.adicionarFiltroExato("u.id", filtro.getId());
				
				return queryUtil;
			}
			
			if(!DadosUtil.isEmpty(filtro.getCodigoClasse())) {
				queryUtil.adicionarFiltroExato("u.codigoClasse", filtro.getCodigoClasse());
			}
			
			if(!DadosUtil.isEmpty(filtro.getNome())) {
				queryUtil.adicionarFiltroAproximado("u.nome", filtro.getNome());
			}
			
			if(!DadosUtil.isEmpty(filtro.getTurma())) {
				queryUtil.adicionarFiltroAproximado("u.turma", filtro.getTurma());
			}
			
			if(!DadosUtil.isEmpty(filtro.getAno())) {
				queryUtil.adicionarFiltroExato("u.ano", filtro.getAno());
			}
			if(!DadosUtil.isEmpty(filtro.getAtivo())) {
				queryUtil.adicionarFiltroExato("u.ativo", filtro.getAtivo());
			}
			
			if(!DadosUtil.isEmpty(filtro.getCliente())) {
				
				if(!DadosUtil.isEmpty(filtro.getCliente().getIdCliente())) {
					queryUtil.adicionarFiltroExato("u.cliente.id", filtro.getCliente().getIdCliente());
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
					
					queryUtil.adicionarFiltroEntre("u.cliente.dtCad", dataInicio.getTime() ,dataFim.getTime());
				}
				
				if(!DadosUtil.isEmpty(filtro.getCliente().getEmail())) {
					queryUtil.adicionarFiltroAproximado("u.cliente.email", filtro.getCliente().getEmail());
				}
				
				if(!DadosUtil.isEmpty(filtro.getCliente().getAtivo())) {
					queryUtil.adicionarFiltroExato("u.cliente.ativo", filtro.getCliente().getAtivo());
				}

			}	

		}
		return queryUtil;
	}


	@Override
	public int consultarQtde(Classe filtro) throws DAOException {
		try{
			QueryUtil queryCount = queryConsultarClasse(filtro, true);
			Number totalRegistros = (Number) queryCount.obterQuery(getEntityManager()).getSingleResult();
			return totalRegistros.intValue();
		}catch(Exception e){
			throw new DAOException(e);
		}
	}

	@Override
	public void excluirClasse(Classe Classe) throws DAOException {
		try{
			delete(Classe);
		}catch(Exception e){
			throw new DAOException(e);
		}
	}

	@Override
	public Classe selectById(Long id) throws DAOException {
		try{
		return find(Classe.class, id);
		}catch(Exception e){
			throw new DAOException(e);
		}
	}
    
}
