package br.com.ischool.business;

import java.util.Collection;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.EJB;
import javax.ejb.Local;
import javax.ejb.Stateless;

import br.com.ischool.dao.NotificacaoDAOLocal;
import br.com.ischool.entity.Notificacao;
import br.com.ischool.exceptions.DAOException;
import br.com.ischool.exceptions.ServicoException;
import br.com.ischool.util.DadosUtil;
import br.com.ischool.util.Paginacao;

/**
 * @author Daniel Souza de lima e-mail:daniesouza@gmail.com
 * Session Bean implementation class NotificacaoServiceImpl
 */
@Stateless
@Local(value=NotificacaoServiceLocal.class)
public class NotificacaoServiceImpl implements NotificacaoServiceLocal {

    /**
     * Default constructor. 
     */
    public NotificacaoServiceImpl() {
        // TODO Auto-generated constructor stub
    }
    
	@EJB
	private NotificacaoDAOLocal notificacaoDAO;
	
	@EJB
	private GenericServiceLocal generico;
	
    @PostConstruct
    private void carregarInformacoes()
    {
    	System.out.println("CARREGADO OS RECURSOS DO EJB "+this.getClass().getName());
    }
    
    @PreDestroy
    private void clear()
    {
    	System.out.println("LIBERANDO OS RECURSOS DO EJB "+this.getClass().getName());
    }
	
	@Override
	public void salvarNotificacao(Notificacao notificacao) throws ServicoException {
		
		try {
					
			validarNotificacao(notificacao);
			
			notificacaoDAO.salvarNotificacao(notificacao);
					
		} catch (DAOException e) {			
			throw new ServicoException(e);			
		} catch (ServicoException e) {
			throw e;
		} catch (Exception e) {		
			throw new ServicoException(e);
		}
					
	}

	
	private void validarNotificacao(Notificacao notificacao) throws ServicoException {
		ServicoException se = new ServicoException();
				
		if (DadosUtil.isEmpty(notificacao)) {
			se.adicionarMensagem("Notificacao_NAO_VAZIO");
			throw se;
		}
			
		if (DadosUtil.isEmpty(notificacao.getAluno()) || DadosUtil.isEmpty(notificacao.getAluno().getId())) {
			se.adicionarMensagem("ALUNO_NAO_VAZIO");
		}
		
		if (DadosUtil.isEmpty(notificacao.getUsuario()) || DadosUtil.isEmpty(notificacao.getUsuario().getId())) {
			se.adicionarMensagem("USUARIO_NAO_VAZIO");
		}
		
		if (DadosUtil.isEmpty(notificacao.getQuantidadeNotificacoes())) {
			se.adicionarMensagem("QUANTIDADE_NAO_VAZIO");
		}
		
		if (DadosUtil.isEmpty(notificacao.getNomeClasse())) {
			se.adicionarMensagem("CLASSE_NAO_VAZIO");
		}
		
		if (se.existeErro()) {
			throw se;
		}
	}
	


	@Override
	public void alterarNotificacao(Notificacao notificacao) throws ServicoException {
		
		try {
			validarNotificacao(notificacao);
			
			notificacaoDAO.alterarNotificacao(notificacao);
			
			
		} catch (DAOException e) {			
			throw new ServicoException(e);			
		} catch (ServicoException e) {
			throw e;
		} catch (Exception e) {		
			throw new ServicoException(e);
		}
					
	}

	@Override
	public Collection<Notificacao> listarNotificacoes() throws ServicoException {
		
		try {
			
			return notificacaoDAO.listarNotificacoes();
			
		} catch (DAOException e) {			
			throw new ServicoException(e);			
		} catch (Exception e) {		
			throw new ServicoException(e);
		}
	}

	@Override
	public Collection<Notificacao> listarNotificacoes(Notificacao filtro,Paginacao paginacao) throws ServicoException {

		try {
			
			if(DadosUtil.isEmpty(paginacao)){
				paginacao = new Paginacao(-1);
			}
			
			return notificacaoDAO.listarNotificacoes(filtro,paginacao);
			
		} catch (DAOException e) {			
			throw new ServicoException(e);			
		} catch (Exception e) {		
			throw new ServicoException(e);
		}
	}
	

	@Override
	public void excluirNotificacao(Notificacao notificacao) throws ServicoException {
		
		try {
			
			notificacaoDAO.excluirNotificacao(notificacao);
			
		} catch (DAOException e) {			
			throw new ServicoException(e);			
		} catch (Exception e) {		
			throw new ServicoException(e);
		}
		
	}

	@Override
	public Notificacao selectById(Long id) throws ServicoException {
		
		try {		
			return notificacaoDAO.selectById(id);
			
		} catch (DAOException e) {			
			throw new ServicoException(e);			
		} catch (Exception e) {		
			throw new ServicoException(e);
		}
	}

	@Override
	public void excluirByIdAlunoAndIdUsuario(Notificacao notificacao)	throws ServicoException {
		
		try {
			
			validarNotificacao(notificacao);
			
			notificacaoDAO.excluirByIdAlunoAndIdUsuario(notificacao);
			
		} catch (DAOException e) {			
			throw new ServicoException(e);			
		} catch (Exception e) {		
			throw new ServicoException(e);
		}
		
	}	

}
