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

import br.com.ischool.entity.Notificacao;
import br.com.ischool.exceptions.DAOException;
import br.com.ischool.util.DadosUtil;
import br.com.ischool.util.Paginacao;
import br.com.ischool.util.QueryUtil;

/**
 *  @author Daniel Souza de lima e-mail:daniesouza@gmail.com
 * Session Bean implementation class NotificacaoDAOImpl
 */
@Stateless
@Local(value=NotificacaoDAOLocal.class)
public class NotificacaoDAOImpl extends GenericDAOImpl<Notificacao,Long> implements NotificacaoDAOLocal {

    /**
     * Default constructor. 
     */
    public NotificacaoDAOImpl() {
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
	public void salvarNotificacao(Notificacao notificacao) throws DAOException{
			
		try{
			
			save(notificacao);
		
		}catch(Exception e){
			throw new DAOException(e);
		}
	
	}
	
	@Override
	public void alterarNotificacao(Notificacao notificacao) throws DAOException {
	
		try{
			
			update(notificacao);
			
		}catch(Exception e){
			throw new DAOException(e);
		}
		
	}


	
	@Override
	public Collection<Notificacao> listarNotificacoes() throws DAOException{
		
		try{			
	        return  getAll("Notificacao");
	        
		}catch(Exception e){
			throw new DAOException(e);
		}

	}
	
	@Override
	public Collection<Notificacao> listarNotificacoes(Notificacao filtro,Paginacao paginacao) throws DAOException{
		
		try{
			
			QueryUtil queryCount = queryConsultarNotificacao(filtro, true);
			
			Number totalRegistros;
			
			try {
				totalRegistros = (Number) queryCount.obterQuery(getEntityManager()).getSingleResult();			
			} catch (NoResultException e) {
				totalRegistros = 0;
			}
			paginacao.setTotalRegistros(totalRegistros.intValue());
			
			QueryUtil queryRegistros = queryConsultarNotificacao(filtro, false);
			
			queryRegistros.setPaginacao(paginacao);
			
			Query query = queryRegistros.obterQuery(getEntityManager());
			
			@SuppressWarnings("unchecked")
			List<Notificacao> Notificacaos = query.getResultList();
			
			return Notificacaos;

		}catch(Exception e){
			throw new DAOException(e);
		}

	}
	
	
	private QueryUtil queryConsultarNotificacao(Notificacao filtro, boolean count) {
	
		String sql = "SELECT u FROM Notificacao u";
		QueryUtil queryUtil = new QueryUtil(sql, false, count);	
		
		
		if(!DadosUtil.isEmpty(filtro)) {
			
			if(!DadosUtil.isEmpty(filtro.getId())) {
				queryUtil.adicionarFiltroExato("u.id", filtro.getId());
				
				return queryUtil;
			}

			if(!DadosUtil.isEmpty(filtro.getAluno())) {
				
				
				if(!DadosUtil.isEmpty(filtro.getAluno().getId())) {
					queryUtil.adicionarFiltroExato("u.aluno.id", filtro.getAluno().getId());
				}
				
				if(!DadosUtil.isEmpty(filtro.getAluno().getNome())) {
					queryUtil.adicionarFiltroAproximado("u.aluno.nome", filtro.getAluno().getNome());
				}
					
				if(!DadosUtil.isEmpty(filtro.getAluno().getRg())) {
					queryUtil.adicionarFiltroExato("u.aluno.rg", filtro.getAluno().getRg());
				}
				
				if(!DadosUtil.isEmpty(filtro.getAluno().getCpf())) {
					queryUtil.adicionarFiltroExato("u.aluno.cpf", filtro.getAluno().getCpf());
				}
				
				if(!DadosUtil.isEmpty(filtro.getAluno().getCodigoAluno())) {
					queryUtil.adicionarFiltroExato("u.aluno.codigoAluno", filtro.getAluno().getCodigoAluno());
				}
				
				if(!DadosUtil.isEmpty(filtro.getAluno().getEndereco())) {
					queryUtil.adicionarFiltroAproximado("u.aluno.endereco", filtro.getAluno().getEndereco());
				}
				
				
				if(!DadosUtil.isEmpty(filtro.getAluno().getTelefone())) {
					queryUtil.adicionarFiltroAproximado("u.aluno.telefone", filtro.getAluno().getTelefone());
				}
				
				if(!DadosUtil.isEmpty(filtro.getAluno().getAtivo())) {
					queryUtil.adicionarFiltroExato("u.aluno.ativo", filtro.getAluno().getAtivo());
				}
				
				if(!DadosUtil.isEmpty(filtro.getAluno().getDataCadastro())) {
					
					Calendar dataInicio = Calendar.getInstance();
									
					dataInicio.setTime(filtro.getAluno().getDataCadastro());
					dataInicio.set(Calendar.HOUR_OF_DAY, 0);
					dataInicio.set(Calendar.MINUTE, 0);
					dataInicio.set(Calendar.SECOND, 0);
					dataInicio.set(Calendar.MILLISECOND, 0);
					
					Calendar dataFim = Calendar.getInstance();
					dataFim.setTime(filtro.getAluno().getDataCadastro());
					dataFim.set(Calendar.HOUR_OF_DAY, 23);
					dataFim.set(Calendar.MINUTE, 59);
					dataFim.set(Calendar.SECOND, 59);
					dataFim.set(Calendar.MILLISECOND, 999);
					
					queryUtil.adicionarFiltroEntre("u.aluno.dataCadastro", dataInicio.getTime() ,dataFim.getTime());
				}
				
				if(!DadosUtil.isEmpty(filtro.getAluno().getDataNascimento())) {
					
					Calendar dataInicio = Calendar.getInstance();
					
					dataInicio.setTime(filtro.getAluno().getDataNascimento());				
					dataInicio.set(Calendar.HOUR_OF_DAY, 0);
					dataInicio.set(Calendar.MINUTE, 0);
					dataInicio.set(Calendar.SECOND, 0);
					dataInicio.set(Calendar.MILLISECOND, 0);
					
					Calendar dataFim = Calendar.getInstance();
					dataFim.setTime(filtro.getAluno().getDataNascimento());
					dataFim.set(Calendar.HOUR_OF_DAY, 23);
					dataFim.set(Calendar.MINUTE, 59);
					dataFim.set(Calendar.SECOND, 59);
					dataFim.set(Calendar.MILLISECOND, 999);
					
					queryUtil.adicionarFiltroEntre("u.aluno.dataNascimento", dataInicio.getTime(),dataFim.getTime());
				}			
				
				if(!DadosUtil.isEmpty(filtro.getAluno().getEmail())) {
					queryUtil.adicionarFiltroAproximado("u.aluno.email", filtro.getAluno().getEmail());
				}
			}
			
			
			if(!DadosUtil.isEmpty(filtro.getUsuario())) {
				
				if(!DadosUtil.isEmpty(filtro.getUsuario().getId())) {
					queryUtil.adicionarFiltroExato("u.usuario.id", filtro.getUsuario().getId());
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
	public void excluirByIdAlunoAndIdUsuario(Notificacao notificacao) throws DAOException {
		try{
					
			String sql = "DELETE FROM Notificacao u";
			QueryUtil queryUtil = new QueryUtil(sql, false, false);	
			
			queryUtil.adicionarFiltroExato("u.usuario.id", notificacao.getUsuario().getId());
			queryUtil.adicionarFiltroExato("u.aluno.id", notificacao.getAluno().getId());
	
			Query query = queryUtil.obterQuery(getEntityManager());
			
			query.executeUpdate();
			
		}catch(Exception e){
			throw new DAOException(e);
		}
	}
	
	@Override
	public void excluirNotificacao(Notificacao Notificacao) throws DAOException {
		try{
			delete(Notificacao);
		}catch(Exception e){
			throw new DAOException(e);
		}
	}

	@Override
	public Notificacao selectById(Long id) throws DAOException {
		try{
		return find(Notificacao.class, id);
		}catch(Exception e){
			throw new DAOException(e);
		}
	}
    
}
