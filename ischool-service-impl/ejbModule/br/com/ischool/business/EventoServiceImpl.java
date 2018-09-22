package br.com.ischool.business;

import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.EJB;
import javax.ejb.Local;
import javax.ejb.Stateless;

import br.com.ischool.dao.ClasseDAOLocal;
import br.com.ischool.dao.EventoDAOLocal;
import br.com.ischool.entity.Classe;
import br.com.ischool.entity.Cliente;
import br.com.ischool.entity.Evento;
import br.com.ischool.exceptions.DAOException;
import br.com.ischool.exceptions.ServicoException;
import br.com.ischool.util.DadosUtil;
import br.com.ischool.util.Paginacao;

/**
 * @author Daniel Souza de lima e-mail:daniesouza@gmail.com
 * Session Bean implementation class EventoServiceImpl
 */
@Stateless
@Local(value=EventoServiceLocal.class)
public class EventoServiceImpl implements EventoServiceLocal {

    /**
     * Default constructor. 
     */
    public EventoServiceImpl() {
        // TODO Auto-generated constructor stub
    }
    
	@EJB
	private EventoDAOLocal eventoDAO;
	
	@EJB
	private ClasseDAOLocal classeDAO;
	
	@EJB
	private GenericServiceLocal generico;
	
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
	public void salvarEvento(Evento evento) throws ServicoException {
		
		try {
					
			validarEvento(evento);

			boolean existeEvento = existeEvento(evento);
			if (existeEvento) {
				throw new ServicoException("EVENTO_EXISTENTE");
			}

			evento.setDataCadastro(new Date());
			
			if(!evento.getNome().startsWith("#")){
				evento.setNome("#"+evento.getNome());
			}
			
			Classe filtro = new Classe();
			filtro.setCliente(new Cliente());
			filtro.getCliente().setId(evento.getCliente().getId());
			
			evento.setClasses((List<Classe>) classeDAO.listarClasses(filtro, new Paginacao(-1)));
			
			for(Classe classe:evento.getClasses()){
				classe.getEventos().add(evento);
			}
						
			eventoDAO.salvarEvento(evento);
					
		} catch (DAOException e) {			
			throw new ServicoException(e);			
		} catch (ServicoException e) {
			throw e;
		} catch (Exception e) {		
			throw new ServicoException(e);
		}
					
	}

	
	private void validarEvento(Evento evento) throws ServicoException {
		ServicoException se = new ServicoException();
		
		
		if (DadosUtil.isEmpty(evento)) {
			se.adicionarMensagem("EVENTO_NAO_VAZIO");
			throw se;
		}

		if (DadosUtil.isEmpty(evento.getNome())) {
			se.adicionarMensagem("NOME_NAO_VAZIO");
		}
		
		if (DadosUtil.isEmpty(evento.getCodigoEvento())) {
			se.adicionarMensagem("CODIGO_NAO_VAZIO");
		}
		
		if(DadosUtil.isEmpty(evento.getCliente()) || DadosUtil.isEmpty(evento.getCliente().getIdCliente())){
			se.adicionarMensagem("CLIENTE_NAO_VAZIO");
		}

		if (se.existeErro()) {
			throw se;
		}
	}
	
	private boolean existeEvento(Evento evento) throws ServicoException {
		try {
			if (!DadosUtil.isEmpty(evento.getIdEvento())) {
				Evento EventoAux = eventoDAO.selectById(evento.getIdEvento());
				if (EventoAux.getCodigoEvento().equals(evento.getCodigoEvento())) {
					return false;
				}
			}

			Evento filtro = new Evento();
			filtro.setCodigoEvento(evento.getCodigoEvento());
			filtro.setCliente(new Cliente());
			filtro.getCliente().setId(evento.getCliente().getId());

			int qtdeEventos = eventoDAO.consultarQtde(filtro);
			return qtdeEventos > 0;

		} catch (DAOException e) {			
			throw new ServicoException(e);			
		} catch (Exception e) {		
			throw new ServicoException(e);
		}
	}

	@Override
	public void alterarEvento(Evento evento) throws ServicoException {
		
		try {
			validarEvento(evento);

			boolean existeEvento = existeEvento(evento);
			if (existeEvento) {
				throw new ServicoException("EVENTO_EXISTENTE");
			}

			if(!evento.getNome().startsWith("#")){
				evento.setNome("#"+evento.getNome());
			}
			
			eventoDAO.alterarEvento(evento);
			
			
		} catch (DAOException e) {			
			throw new ServicoException(e);			
		} catch (ServicoException e) {
			throw e;
		} catch (Exception e) {		
			throw new ServicoException(e);
		}
					
	}

	@Override
	public Collection<Evento> listarEventos() throws ServicoException {
		
		try {
			
			return eventoDAO.listarEventos();
			
		} catch (DAOException e) {			
			throw new ServicoException(e);			
		} catch (Exception e) {		
			throw new ServicoException(e);
		}
	}

	@Override
	public Collection<Evento> listarEventos(Evento filtro,Paginacao paginacao) throws ServicoException {

		try {
			
			if(DadosUtil.isEmpty(paginacao)){
				paginacao = new Paginacao(-1);
			}
			
			return eventoDAO.listarEventos(filtro,paginacao);
			
		} catch (DAOException e) {			
			throw new ServicoException(e);			
		} catch (Exception e) {		
			throw new ServicoException(e);
		}
	}
	

	@Override
	public void excluirEvento(Evento Evento) throws ServicoException {
		
		try {
			
			eventoDAO.excluirEvento(Evento);
			
		} catch (DAOException e) {			
			throw new ServicoException(e);			
		} catch (Exception e) {		
			throw new ServicoException(e);
		}
		
	}

	@Override
	public Evento selectById(Long id) throws ServicoException {
		
		try {
			
			return eventoDAO.selectById(id);
			
		} catch (DAOException e) {			
			throw new ServicoException(e);			
		} catch (Exception e) {		
			throw new ServicoException(e);
		}
	}




}
