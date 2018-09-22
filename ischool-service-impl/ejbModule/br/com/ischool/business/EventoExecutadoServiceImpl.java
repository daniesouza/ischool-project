package br.com.ischool.business;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.EJB;
import javax.ejb.Local;
import javax.ejb.Stateless;

import br.com.ischool.dao.EventoExecutadoDAOLocal;
import br.com.ischool.entity.Aluno;
import br.com.ischool.entity.Classe;
import br.com.ischool.entity.Cliente;
import br.com.ischool.entity.EventoExecutado;
import br.com.ischool.entity.Usuario;
import br.com.ischool.exceptions.DAOException;
import br.com.ischool.exceptions.ServicoException;
import br.com.ischool.util.Constantes;
import br.com.ischool.util.DadosUtil;
import br.com.ischool.util.Paginacao;

/**
 * @author Daniel Souza de lima e-mail:daniesouza@gmail.com
 * Session Bean implementation class EventoExecutadoServiceImpl
 */
@Stateless
@Local(value=EventoExecutadoServiceLocal.class)
public class EventoExecutadoServiceImpl implements EventoExecutadoServiceLocal {

    /**
     * Default constructor. 
     */
    public EventoExecutadoServiceImpl() {
        // TODO Auto-generated constructor stub
    }
    
	@EJB
	private EventoExecutadoDAOLocal eventoExecutadoDAO;
	
	@EJB
	private GenericServiceLocal servicoGenerico;
	
		
    @PostConstruct
    public void carregarInformacoes(){
    	System.out.println("CARREGADO OS RECURSOS DO EJB "+this.getClass().getName());
    }
    
    @PreDestroy
    public void clear(){
    	System.out.println("LIBERANDO OS RECURSOS DO EJB "+this.getClass().getName());
    }
	
	@Override
	public void salvarEventoExecutado(EventoExecutado eventoExecutado) throws ServicoException {
		
		int eventoAnterior = eventoExecutado.getTipo();

		
		try {
			
			eventoExecutado.setDataCadastro(new Date());
			eventoExecutado.setDataAtualizacao(eventoExecutado.getDataCadastro().getTime());

			validarEventoExecutado(eventoExecutado);
			

//			if(eventoExecutado.getTipo() == Constantes.TIPO_INICIO_FIM){			
//				validarEventoInicioFim(eventoExecutado);				
//			}else if(eventoExecutado.getTipo() == Constantes.TIPO_INFORMATIVO){
//				validarEventoInformativo(eventoExecutado);
//			}
//			else if(eventoExecutado.getTipo() == Constantes.TIPO_ALIMENTO){
//				validarEventoAlimento(eventoExecutado);
//			}
//			else if(eventoExecutado.getTipo() == Constantes.TIPO_EVACUACAO){
//				validarEventoEvacuacao(eventoExecutado);
//			}			
//			else if(eventoExecutado.getTipo() == Constantes.TIPO_MEDICAMENTO){
//				validarEventoMedicamento(eventoExecutado);
//			}
//			
//			else if(eventoExecutado.getTipo() == Constantes.TIPO_ENVIAR){
//				validarEventoFavorEnviar(eventoExecutado);
//			}
								
			eventoExecutadoDAO.salvarEventoExecutado(eventoExecutado);
			
		} catch (DAOException e) {		
			clearEventoExecutado(eventoExecutado,eventoAnterior);
			throw new ServicoException(e);			
		} catch (ServicoException e) {			
			clearEventoExecutado(eventoExecutado,eventoAnterior);	
			throw e;
		} catch (Exception e) {				
			clearEventoExecutado(eventoExecutado,eventoAnterior);	
			throw new ServicoException(e);
		}				
	}
	
	private void clearEventoExecutado(EventoExecutado eventoExecutado,int eventoAnterior){
		eventoExecutado.setUsuario(null);
		eventoExecutado.setDataCadastro(null);
		eventoExecutado.setCliente(null);	
		eventoExecutado.setTipo(eventoAnterior);

	}
	
	private void validarEventoEvacuacao(EventoExecutado eventoExecutado) throws ServicoException {

		ServicoException se = new ServicoException();
		
		if(eventoExecutado.getIcone().equals(Constantes.ICONE_NAO_OK)){
			
			if(DadosUtil.isEmpty(eventoExecutado.getDataInicio())){		
				se.adicionarMensagem("DATA_INICIO_NAO_VAZIA");
			}
			
			if(DadosUtil.isEmpty(eventoExecutado.getAvaliacaoEvento())){				
				se.adicionarMensagem("AVALIACAO_NAO_VAZIA");
			}
			
			if(DadosUtil.isEmpty(eventoExecutado.getTomouBanho())){				
				se.adicionarMensagem("TOMOU_BANHO_NAO_VAZIO");
			}
			
			if (se.existeErro()) {
				throw se;
			}

			eventoExecutado.setIcone(Constantes.ICONE_OK);
			
		}

	}
	
	private void validarLembreteGenerico(EventoExecutado eventoExecutado) throws ServicoException {

		ServicoException se = new ServicoException();
		
		if(eventoExecutado.getIcone().equals(Constantes.ICONE_NAO_OK)){
			
			if(DadosUtil.isEmpty(eventoExecutado.getObservacoes())){		
				se.adicionarMensagem("MENSAGEM_NAO_VAZIA");
			}
						
			if (se.existeErro()) {
				throw se;
			}

			eventoExecutado.setIcone(Constantes.ICONE_OK);
			
		}

	}	
	
	
	private void validarLembrete(EventoExecutado eventoExecutado) throws ServicoException {

		ServicoException se = new ServicoException();
		
		if(eventoExecutado.getIcone().equals(Constantes.ICONE_NAO_OK)){
			
			if(DadosUtil.isEmpty(eventoExecutado.getDataInicio())){		
				se.adicionarMensagem("DATA_INICIO_NAO_VAZIA");
			}
			
			if(DadosUtil.isEmpty(eventoExecutado.getEnviarOutros())  && 
								 !eventoExecutado.getEnviarFralda()  &&
								 !eventoExecutado.getEnviarLeite()   &&
								 !eventoExecutado.getEnviarLencos()  &&
								 !eventoExecutado.getEnviarPomada() ){
				
				se.adicionarMensagem("ENVIAR_NAO_VAZIO");
			}
			
			if (se.existeErro()) {
				throw se;
			}

			eventoExecutado.setIcone(Constantes.ICONE_OK);
			
		}

	}
	
	private void validarEventoMedicamento(EventoExecutado eventoExecutado) throws ServicoException {

		ServicoException se = new ServicoException();
		
		if(eventoExecutado.getIcone().equals(Constantes.ICONE_NAO_OK)){
			
			if(DadosUtil.isEmpty(eventoExecutado.getDataInicio())){		
				se.adicionarMensagem("DATA_INICIO_NAO_VAZIA");
			}
			
			if(DadosUtil.isEmpty(eventoExecutado.getMedicamentos())){				
				se.adicionarMensagem("MEDICAMENTO_NAO_VAZIO");
			}
			
			if (se.existeErro()) {
				throw se;
			}

			eventoExecutado.setIcone(Constantes.ICONE_OK);
			
		}

	}
	
	
	private void validarEventoInformativo(EventoExecutado eventoExecutado) throws ServicoException {

		ServicoException se = new ServicoException();
		
		if(eventoExecutado.getIcone().equals(Constantes.ICONE_NAO_OK)){
			
			if(DadosUtil.isEmpty(eventoExecutado.getDataInicio())){		
				se.adicionarMensagem("DATA_INICIO_NAO_VAZIA");
			}
			
			if(DadosUtil.isEmpty(eventoExecutado.getQuantidade())){				
				se.adicionarMensagem("QUANTIDADE_NAO_VAZIA");
			}
			
			if (se.existeErro()) {
				throw se;
			}

			eventoExecutado.setIcone(Constantes.ICONE_OK);
			
		}

	}
	
	
	private void validarEventoAlimento(EventoExecutado eventoExecutado) throws ServicoException {

		ServicoException se = new ServicoException();
		
		if(eventoExecutado.getIcone().equals(Constantes.ICONE_NAO_OK)){
			
			if(DadosUtil.isEmpty(eventoExecutado.getDataInicio())){		
				se.adicionarMensagem("DATA_INICIO_NAO_VAZIA");
			}
			
			if(DadosUtil.isEmpty(eventoExecutado.getAvaliacaoEvento())){				
				se.adicionarMensagem("AVALIACAO_NAO_VAZIA");
			}
			
			if (se.existeErro()) {
				throw se;
			}

			eventoExecutado.setIcone(Constantes.ICONE_OK);
			
		}


	}
	
	private void validarEventoInicioFim(EventoExecutado eventoExecutado) throws ServicoException {

		ServicoException se = new ServicoException();
		
		if(eventoExecutado.getIcone().equals(Constantes.ICONE_NAO_OK)){
			
			if(DadosUtil.isEmpty(eventoExecutado.getDataInicio())){		
				se.adicionarMensagem("DATA_INICIO_NAO_VAZIA");
				throw se;
			}
			else{
				eventoExecutado.setIcone(Constantes.ICONE_WARNING);
			}
			
		}
		else if(eventoExecutado.getIcone().equals(Constantes.ICONE_WARNING) || eventoExecutado.getIcone().equals(Constantes.ICONE_OK)){
				
			if(DadosUtil.isEmpty(eventoExecutado.getDataFim())){
				
				se.adicionarMensagem("DATA_FIM_NAO_VAZIA");
				throw se;
			}
			else{
				eventoExecutado.setIcone(Constantes.ICONE_OK);
			}
		}

	}
	
	
	private void validarEventoExecutado(EventoExecutado eventoExecutado) throws ServicoException {
		ServicoException se = new ServicoException();
		
		if (DadosUtil.isEmpty(eventoExecutado)) {
			se.adicionarMensagem("EVENTO_NAO_VAZIO");
		}
		
//		if (DadosUtil.isEmpty(eventoExecutado.getQuantidade())) {
//			se.adicionarMensagem("QUANTIDADE_NAO_VAZIO");
//		}
		
		if (DadosUtil.isEmpty(eventoExecutado.getDataCadastro())) {
			se.adicionarMensagem("DATA_CADASTRO_NAO_VAZIO");
		}
		
		if (DadosUtil.isEmpty(eventoExecutado.getClasse())) {
			se.adicionarMensagem("CLASSE_NAO_VAZIO");
		}
		
		if (DadosUtil.isEmpty(eventoExecutado.getCliente())) {
			se.adicionarMensagem("CLIENTE_NAO_VAZIO");
		}
		
		if (DadosUtil.isEmpty(eventoExecutado.getEvento())) {
			se.adicionarMensagem("EVENTO_NAO_VAZIO");
		}
		
		if (DadosUtil.isEmpty(eventoExecutado.getAluno())) {
			se.adicionarMensagem("ALUNO_NAO_VAZIO");
		}
		
		if (DadosUtil.isEmpty(eventoExecutado.getUsuario())) {
			se.adicionarMensagem("USUARIO_NAO_VAZIO");
		}	
		
		if (DadosUtil.isEmpty(eventoExecutado.getTipo())) {
			se.adicionarMensagem("TIPO_NAO_VAZIO");
		}	

		if (se.existeErro()) {
			throw se;
		}
	}
	
//	private boolean existeEventoExecutado(EventoExecutado EventoExecutado) throws ServicoException {
//		try {
//			if (!DadosUtil.isEmpty(EventoExecutado.getIdEventoExecutado())) {
//				EventoExecutado EventoExecutadoAux = EventoExecutadoDAO.selectById(EventoExecutado.getIdEventoExecutado());
//				if (EventoExecutadoAux.getIdEventoExecutado().equals(EventoExecutado.getIdEventoExecutado())) {
//					return false;
//				}
//			}
//
//			EventoExecutado filtro = new EventoExecutado();
//			filtro.setIdEventoExecutado(EventoExecutado.getIdEventoExecutado());
//
//			int qtdeEventoExecutados = EventoExecutadoDAO.consultarQtde(filtro);
//			return qtdeEventoExecutados > 0;
//
//		} catch (DAOException e) {
//			throw new ServicoException(e.getMessage());
//		}
//	}

	@Override
	public void alterarEventoExecutado(EventoExecutado eventoExecutado) throws ServicoException {
		
		int eventoAnterior = eventoExecutado.getTipo();

		
		try {

			validarEventoExecutado(eventoExecutado);
			
			switch (eventoExecutado.getTipo()) {
			case Constantes.TIPO_INICIO_FIM:
				validarEventoInicioFim(eventoExecutado);	
				break;
				
			case Constantes.TIPO_INFORMATIVO:
				validarEventoInformativo(eventoExecutado);	
				break;
				
			case Constantes.TIPO_ALIMENTO:
				validarEventoAlimento(eventoExecutado);
				break;
			case Constantes.TIPO_EVACUACAO:
				validarEventoEvacuacao(eventoExecutado);
				break;
			case Constantes.TIPO_MEDICAMENTO:
				validarEventoMedicamento(eventoExecutado);
				break;
			case Constantes.TIPO_LEMBRETE:
				validarLembrete(eventoExecutado);
				break;
			case Constantes.TIPO_LEMBRETE_GEN:
				validarLembreteGenerico(eventoExecutado);
				break;				

			default:
				break;
			}

			eventoExecutado.setDataAtualizacao(new Date().getTime());

											
			eventoExecutadoDAO.alterarEventoExecutado(eventoExecutado);
			
			
		} catch (DAOException e) {
			eventoExecutado.setTipo(eventoAnterior);
			throw new ServicoException(e);			
		} catch (ServicoException e) {
			eventoExecutado.setTipo(eventoAnterior);
			throw e;
		} catch (Exception e) {
			eventoExecutado.setTipo(eventoAnterior);
			throw new ServicoException(e);
		}
					
	}

	@Override
	public Collection<EventoExecutado> listarEventoExecutados() throws ServicoException {
		
		try {
			
			return eventoExecutadoDAO.listarEventoExecutados();
			
		} catch (DAOException e) {			
			throw new ServicoException(e);			
		} catch (Exception e) {		
			throw new ServicoException(e);
		}
	}

	@Override
	public Collection<EventoExecutado> listarEventoExecutados(EventoExecutado filtro,Paginacao paginacao) throws ServicoException {

		try {
			
			if(DadosUtil.isEmpty(paginacao)){
				paginacao = new Paginacao(-1);
			}
			
			return eventoExecutadoDAO.listarEventoExecutados(filtro,paginacao);
		
		} catch (DAOException e) {			
			throw new ServicoException(e);			
		} catch (Exception e) {		
			throw new ServicoException(e);
		}
	}


	@Override
	public void excluirEventoExecutado(EventoExecutado eventoExecutado) throws ServicoException {
		
		try {
			
			eventoExecutadoDAO.excluirEventoExecutado(eventoExecutado);
			
		} catch (DAOException e) {			
			throw new ServicoException(e);			
		} catch (Exception e) {		
			throw new ServicoException(e);
		}
		
	}

	@Override
	public EventoExecutado selectById(Long id) throws ServicoException {
		
		try {
			
			return eventoExecutadoDAO.selectById(id);
			
		} catch (DAOException e) {			
			throw new ServicoException(e);			
		} catch (Exception e) {		
			throw new ServicoException(e);
		}
	}

	
	@Override
	public Collection<Aluno> carregarSessaoAlunoProfessor(Classe classe,Date data) throws ServicoException {
		
		ServicoException se = new ServicoException();
		
		if (DadosUtil.isEmpty(classe) || DadosUtil.isEmpty(classe.getIdClasse())) {
			se.adicionarMensagem("CLASSE_NAO_VAZIO");
		}
				
		if (DadosUtil.isEmpty(classe.getCliente()) || DadosUtil.isEmpty(classe.getCliente().getIdCliente())) {
			se.adicionarMensagem("CLIENTE_NAO_VAZIO");
		}
		
		if (DadosUtil.isEmpty(data)) {
			se.adicionarMensagem("DATA_NAO_VAZIO");
		}
		
		
		
		if (se.existeErro()) {
			throw se;
		}
			
		try {
			
			List<Aluno> listaAlunos = ( (Classe) servicoGenerico.obterListaLazy(classe, Classe.class, "alunos")).getAlunos();
			
			if(!DadosUtil.isEmpty(listaAlunos)){
				
				List<Aluno> listaRemoverDesativados = new ArrayList<Aluno>();
				
				for(Aluno aluno:listaAlunos){
					if(!aluno.getAtivo()){
						listaRemoverDesativados.add(aluno);
					}
				}
				
				listaAlunos.removeAll(listaRemoverDesativados);
				
				carregarEventosExecutadosAluno(listaAlunos,classe,data);
			}
			
			return listaAlunos;
	
		} catch (ServicoException e) {
			throw se;
		}catch(Exception e){			
			throw new ServicoException(e);
		}
		
	}
	

	@Override
	public Collection<Aluno> carregarSessaoAlunoResponsavel(Usuario usuario,Date data) throws ServicoException {

		try {	
			
			
			ServicoException se = new ServicoException();
			
			if (DadosUtil.isEmpty(usuario) || DadosUtil.isEmpty(usuario.getId())) {
				se.adicionarMensagem("USUARIO_NAO_VAZIO");
			}
			
			if (DadosUtil.isEmpty(data)) {
				se.adicionarMensagem("DATA_NAO_VAZIO");
			}
			
			
			if (se.existeErro()) {
				throw se;
			}
			
			
			Collection<Aluno> listaAlunos = ((Usuario) servicoGenerico.obterListaLazy(usuario, Usuario.class, "alunos")).getAlunos();
			

			if(!DadosUtil.isEmpty(listaAlunos)){
				
				List<Aluno> listaRemoverDesativados = new ArrayList<Aluno>();
				
				for(Aluno aluno:listaAlunos){
					if(!aluno.getAtivo()){
						listaRemoverDesativados.add(aluno);
					}
				}
				
				listaAlunos.removeAll(listaRemoverDesativados);
				
				carregarEventosExecutadosAluno(listaAlunos,data);			
			}
			
			return listaAlunos;
			
		} catch (ServicoException se) {
			throw se;
		}catch(Exception e){			
			throw e;
		}
	
	}
	
	
	private void carregarEventosExecutadosAluno(Collection<Aluno> listaAlunos,Classe classe,Date data) throws ServicoException {

		try {	
			
			EventoExecutado filtro = new EventoExecutado();

			Classe classeFiltro = new Classe();					
			classeFiltro.setIdClasse(classe.getIdClasse());
			filtro.setClasse(classeFiltro);
			
			Cliente clienteFiltro = new Cliente();
			clienteFiltro.setIdCliente(classe.getCliente().getIdCliente());
			filtro.setCliente(clienteFiltro);
						
			for(Aluno aluno:listaAlunos){
					
					filtro.setDataCadastro(data);
					
					Aluno alunoFiltro = new Aluno();
					alunoFiltro.setIdAluno(aluno.getIdAluno());
					filtro.setAluno(alunoFiltro);
					
					aluno.setEventosExecutados((List<EventoExecutado>) listarEventoExecutados(filtro, null));
						
			}
			
		} catch (ServicoException se) {
			throw se;
		}catch(Exception e){			
			throw e;
		}
	
	}	
	
	
	private void carregarEventosExecutadosAluno(Collection<Aluno> listaAlunos,Date data) throws ServicoException {

		try {	

			for(Aluno aluno:listaAlunos){
								
				
				if(aluno.getClasses() != null && !aluno.getClasses().isEmpty()){
									

					EventoExecutado filtro = new EventoExecutado();
					Classe classe = new Classe();					
					classe.setIdClasse(aluno.getClasses().get(0).getId());
					filtro.setClasse(classe);
					
					Cliente cliente = new Cliente();
					cliente.setIdCliente(aluno.getCliente().getIdCliente());
					filtro.setCliente(cliente);
					
					filtro.setDataCadastro(data);
					
					Aluno alunoFiltro = new Aluno();
					alunoFiltro.setIdAluno(aluno.getIdAluno());
					filtro.setAluno(alunoFiltro);
					
					
					aluno.setEventosExecutados((List<EventoExecutado>) listarEventoExecutados(filtro, null));
			
				}
	
			}
			
		} catch (ServicoException se) {
			throw se;
		}catch(Exception e){			
			throw e;
		}
	
	}

	@Override
	public void cancelarEventoExecutado(EventoExecutado eventoExecutado) throws ServicoException {	
		
		String iconeAnterior = eventoExecutado.getIcone();
		Integer statusAnterior = eventoExecutado.getStatusEventoExecutado();
		
		try{			
			eventoExecutado.setIcone(Constantes.ICONE_CANCELADO);
			eventoExecutado.setStatusEventoExecutado(Constantes.STATUS_CANCELADO);
			alterarEventoExecutado(eventoExecutado);
				
		} catch (ServicoException e) {	
			eventoExecutado.setIcone(iconeAnterior);
			eventoExecutado.setStatusEventoExecutado(statusAnterior);
			throw e;
		}
		
	}
	
	
	@Override
	public Collection<EventoExecutado> listarEventosExecutadosNaoLidosDevice(Long idAluno,Collection<Long> datasAtualizacao, Date dataCadastro) throws ServicoException {

		try {
				
			return eventoExecutadoDAO.listarEventosExecutadosNaoLidosDevice(idAluno,datasAtualizacao, dataCadastro);
		
		} catch (DAOException e) {			
			throw new ServicoException(e);			
		} catch (Exception e) {		
			throw new ServicoException(e);
		}
	}


}
