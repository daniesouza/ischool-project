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

import br.com.ischool.entity.Aluno;
import br.com.ischool.exceptions.DAOException;
import br.com.ischool.util.DadosUtil;
import br.com.ischool.util.Paginacao;
import br.com.ischool.util.QueryUtil;

/**
 *  @author Daniel Souza de lima e-mail:daniesouza@gmail.com
 * Session Bean implementation class AlunoDAOImpl
 */
@Stateless
@Local(value=AlunoDAOLocal.class)
public class AlunoDAOImpl extends GenericDAOImpl<Aluno,Long> implements AlunoDAOLocal {

    /**
     * Default constructor. 
     */
    public AlunoDAOImpl() {
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
	public void salvarAluno(Aluno aluno) throws DAOException{
			
		try{
			
			save(aluno);
		
		}catch(Exception e){
			throw new DAOException(e);
		}
	
	}
	
	@Override
	public void alterarAluno(Aluno aluno) throws DAOException {
	
		try{
			
			update(aluno);
			
		}catch(Exception e){
			throw new DAOException(e);
		}
		
	}


	
	@Override
	public Collection<Aluno> listarAlunos() throws DAOException{
		
		try{			
	        return  getAll("Aluno");
	        
		}catch(Exception e){
			throw new DAOException(e);
		}

	}
	
	@Override
	public Collection<Aluno> listarAlunos(Aluno filtro,Paginacao paginacao) throws DAOException{
		
		try{
			
			QueryUtil queryCount = queryConsultarAluno(filtro, true);
			
			Number totalRegistros;
			
			try {
				totalRegistros = (Number) queryCount.obterQuery(getEntityManager()).getSingleResult();			
			} catch (NoResultException e) {
				totalRegistros = 0;
			}
			paginacao.setTotalRegistros(totalRegistros.intValue());
			
			QueryUtil queryRegistros = queryConsultarAluno(filtro, false);
			
			queryRegistros.setPaginacao(paginacao);
			
			Query query = queryRegistros.obterQuery(getEntityManager());
			
			@SuppressWarnings("unchecked")
			List<Aluno> alunos = query.getResultList();
			
			return alunos;

		}catch(Exception e){
			throw new DAOException(e);
		}

	}
	
	
	private QueryUtil queryConsultarAluno(Aluno filtro, boolean count) {
	
		String sql = "SELECT u FROM Aluno u";
		QueryUtil queryUtil = new QueryUtil(sql, false, count);	
		
		
		if(!DadosUtil.isEmpty(filtro)) {
			
			if(!DadosUtil.isEmpty(filtro.getId())) {
				queryUtil.adicionarFiltroAproximado("u.id", filtro.getId());
				
				return queryUtil;
			}
			
			if(!DadosUtil.isEmpty(filtro.getNome())) {
				queryUtil.adicionarFiltroAproximado("u.nome", filtro.getNome());
			}
				
			if(!DadosUtil.isEmpty(filtro.getRg())) {
				queryUtil.adicionarFiltroExato("u.rg", filtro.getRg());
			}
			
			if(!DadosUtil.isEmpty(filtro.getCpf())) {
				queryUtil.adicionarFiltroExato("u.cpf", filtro.getCpf());
			}
			
			if(!DadosUtil.isEmpty(filtro.getCodigoAluno())) {
				queryUtil.adicionarFiltroExato("u.codigoAluno", filtro.getCodigoAluno());
			}
			
			if(!DadosUtil.isEmpty(filtro.getEndereco())) {
				queryUtil.adicionarFiltroAproximado("u.endereco", filtro.getEndereco());
			}
			
			
			if(!DadosUtil.isEmpty(filtro.getTelefone())) {
				queryUtil.adicionarFiltroAproximado("u.telefone", filtro.getTelefone());
			}
			
			if(!DadosUtil.isEmpty(filtro.getAtivo())) {
				queryUtil.adicionarFiltroExato("u.ativo", filtro.getAtivo());
			}
			
			if(!DadosUtil.isEmpty(filtro.getSemClassesAtribuidas())){
				queryUtil.adicionarFiltroAndIsEmpty("u.classes");
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
				
				queryUtil.adicionarFiltroEntre("u.dataCadastro", dataInicio.getTime() ,dataFim.getTime());
			}
			
			if(!DadosUtil.isEmpty(filtro.getDataNascimento())) {
				
				Calendar dataInicio = Calendar.getInstance();
				
				dataInicio.setTime(filtro.getDataNascimento());				
				dataInicio.set(Calendar.HOUR_OF_DAY, 0);
				dataInicio.set(Calendar.MINUTE, 0);
				dataInicio.set(Calendar.SECOND, 0);
				dataInicio.set(Calendar.MILLISECOND, 0);
				
				Calendar dataFim = Calendar.getInstance();
				dataFim.setTime(filtro.getDataNascimento());
				dataFim.set(Calendar.HOUR_OF_DAY, 23);
				dataFim.set(Calendar.MINUTE, 59);
				dataFim.set(Calendar.SECOND, 59);
				dataFim.set(Calendar.MILLISECOND, 999);
				
				queryUtil.adicionarFiltroEntre("u.dataNascimento", dataInicio.getTime(),dataFim.getTime());
			}			
			
			if(!DadosUtil.isEmpty(filtro.getEmail())) {
				queryUtil.adicionarFiltroAproximado("u.email", filtro.getEmail());
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
					
					queryUtil.adicionarFiltroEntre("u.cliente.dtCad", dataInicio.getTime(),dataFim.getTime());
				}
				
				if(!DadosUtil.isEmpty(filtro.getCliente().getEmail())) {
					queryUtil.adicionarFiltroAproximado("u.cliente.email", filtro.getCliente().getEmail());
				}
				
				if(!DadosUtil.isEmpty(filtro.getCliente().getAtivo())) {
					queryUtil.adicionarFiltroExato("u.cliente.ativo", filtro.getCliente().getAtivo());
				}
			}
			
			queryUtil.adicionarOrderBy("u.nome", "asc");
						

		}
		return queryUtil;
	}
	

	@Override
	public int consultarQtde(Aluno filtro) throws DAOException {
		try{
			QueryUtil queryCount = queryConsultarAluno(filtro, true);
			Number totalRegistros = (Number) queryCount.obterQuery(getEntityManager()).getSingleResult();
			return totalRegistros.intValue();
		}catch(Exception e){
			throw new DAOException(e);
		}
	}

	@Override
	public void excluirAluno(Aluno Aluno) throws DAOException {
		try{
			delete(Aluno);
		}catch(Exception e){
			throw new DAOException(e);
		}
	}

	@Override
	public Aluno selectById(Long id) throws DAOException {
		try{
		return find(Aluno.class, id);
		}catch(Exception e){
			throw new DAOException(e);
		}
	}
    
}
