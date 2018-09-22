package br.com.ischool.dao;

import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import br.com.ischool.entity.Mensagem;
import br.com.ischool.exceptions.DAOException;
import br.com.ischool.util.DadosUtil;
import br.com.ischool.util.Paginacao;
import br.com.ischool.util.QueryUtil;

/**
 *  @author Daniel Souza de lima e-mail:daniesouza@gmail.com
 * Session Bean implementation class MensagemDAOImpl
 */
@Stateless
@Local(value=MensagemDAOLocal.class)
public class MensagemDAOImpl extends GenericDAOImpl<Mensagem,Long> implements MensagemDAOLocal {

    /**
     * Default constructor. 
     */
    public MensagemDAOImpl() {
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
	public void salvarMensagem(Mensagem mensagem) throws DAOException{
			
		try{
			
			save(mensagem);
		
		}catch(Exception e){
			throw new DAOException(e);
		}
	
	}
	
	@Override
	public void alterarMensagem(Mensagem mensagem) throws DAOException {
	
		try{
			
			update(mensagem);
			
		}catch(Exception e){
			throw new DAOException(e);
		}
		
	}


	
	@Override
	public Collection<Mensagem> listarMensagems() throws DAOException{
		
		try{			
	        return  getAll("Mensagem");
	        
		}catch(Exception e){
			throw new DAOException(e);
		}

	}
	
	@Override
	public Collection<Mensagem> listarMensagems(Mensagem filtro,Paginacao paginacao) throws DAOException{
		
		try{
					
			QueryUtil queryCount = queryConsultarMensagem(filtro, true);
			
			Number totalRegistros;
			
			try {
				totalRegistros = (Number) queryCount.obterQuery(getEntityManager()).getSingleResult();			
			} catch (NoResultException e) {
				totalRegistros = 0;
			}
			paginacao.setTotalRegistros(totalRegistros.intValue());
			
			QueryUtil queryRegistros = queryConsultarMensagem(filtro, false);
			
			queryRegistros.setPaginacao(paginacao);
			
			Query query = queryRegistros.obterQuery(getEntityManager());
			
			@SuppressWarnings("unchecked")
			List<Mensagem> mensagens = query.getResultList();
			
			return mensagens;

		}catch(Exception e){
			throw new DAOException(e);
		}

	}
	
	
	private QueryUtil queryConsultarMensagem(Mensagem filtro, boolean count) {
	
		String sql = "SELECT u FROM Mensagem u";
		QueryUtil queryUtil = new QueryUtil(sql, false, count);	
		
		
		if(!DadosUtil.isEmpty(filtro)) {
			
			if(!DadosUtil.isEmpty(filtro.getId())) {
				queryUtil.adicionarFiltroExato("u.id", filtro.getId());
				
				return queryUtil;
			}
			
			if(!DadosUtil.isEmpty(filtro.getIdDeviceMensagem())) {
				queryUtil.adicionarFiltroExato("u.idDeviceMensagem", filtro.getIdDeviceMensagem());
				
				return queryUtil;
			}
			
			if(!DadosUtil.isEmpty(filtro.getMensagem())) {
				queryUtil.adicionarFiltroExato("u.mensagem", filtro.getMensagem());
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
			
			if(!DadosUtil.isEmpty(filtro.getLidoDevice())) {
				queryUtil.adicionarFiltroExato("u.lidoDevice", filtro.getLidoDevice());
			}
						
			if(!DadosUtil.isEmpty(filtro.getCaminhoArquivo())) {
				queryUtil.adicionarFiltroExato("u.caminhoArquivo", filtro.getCaminhoArquivo());
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
			
			
			queryUtil.adicionarOrderBy("u.dataCadastro", "desc");

		}
		return queryUtil;
	}

	
	@Override
	public int consultarQtde(Mensagem filtro) throws DAOException {
		try{
			QueryUtil queryCount = queryConsultarMensagem(filtro, true);
			Number totalRegistros = (Number) queryCount.obterQuery(getEntityManager()).getSingleResult();
			return totalRegistros.intValue();
		}catch(Exception e){
			throw new DAOException(e);
		}
	}

	@Override
	public void excluirMensagem(Mensagem Mensagem) throws DAOException {
		try{
			delete(Mensagem);
		}catch(Exception e){
			throw new DAOException(e);
		}
	}

	@Override
	public Mensagem selectById(Long id) throws DAOException {
		try{
		return find(Mensagem.class, id);
		}catch(Exception e){
			throw new DAOException(e);
		}
	}

	@Override
	public Collection<Mensagem> listarMensagensNaoLidosDevice(Long idAluno,Collection<Long> idsNOTIN,Date dataCadastro) throws DAOException {
		
		try{	
			
			Calendar dataInicio = Calendar.getInstance();
			
			dataInicio.setTime(dataCadastro);			
			dataInicio.set(Calendar.HOUR_OF_DAY, 0);
			dataInicio.set(Calendar.MINUTE, 0);
			dataInicio.set(Calendar.SECOND, 0);
			dataInicio.set(Calendar.MILLISECOND, 0);
						
			Calendar dataFim = Calendar.getInstance();
			dataFim.setTime(dataCadastro);
			dataFim.set(Calendar.HOUR_OF_DAY, 23);
			dataFim.set(Calendar.MINUTE, 59);
			dataFim.set(Calendar.SECOND, 59);
			dataFim.set(Calendar.MILLISECOND, 999);
			
		
			@SuppressWarnings("unchecked")
			List<Mensagem> mensagens = getEntityManager().createQuery("SELECT u From Mensagem u WHERE "
					+ " u.idWebMensagem not IN (:idsMensagens)"
					+ " and u.aluno.idAluno = :idAluno and u.dataCadastro between :dataInicio and :dataFim order by u.dataCadastro asc")
					.setParameter("idAluno", idAluno)
					.setParameter("idsMensagens", idsNOTIN)
					.setParameter("dataInicio", dataInicio.getTime())
					.setParameter("dataFim", dataFim.getTime()).getResultList();
			
			return mensagens;
		}catch (NoResultException e) {
			return null;
		}catch(Exception e){			
			throw new DAOException(e);
		}	
	}
	
	@Override
	public Mensagem pesquisarMensagemDeviceId(Long idDeviceMensagem) throws DAOException{		        		
		try{		
			return (Mensagem) getEntityManager().createQuery("Select u from Mensagem u where u.idDeviceMensagem = :idDeviceMensagem").setParameter("idDeviceMensagem", idDeviceMensagem).getSingleResult();
		}catch (NoResultException e) {
			return null;
		}catch(Exception e){			
			throw new DAOException(e);
		}	
	}
    
}
