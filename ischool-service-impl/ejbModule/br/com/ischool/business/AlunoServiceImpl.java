package br.com.ischool.business;

import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.EJB;
import javax.ejb.Local;
import javax.ejb.Stateless;

import br.com.ischool.dao.AlunoDAOLocal;
import br.com.ischool.entity.Aluno;
import br.com.ischool.entity.Classe;
import br.com.ischool.entity.Cliente;
import br.com.ischool.entity.Usuario;
import br.com.ischool.exceptions.DAOException;
import br.com.ischool.exceptions.ServicoException;
import br.com.ischool.util.DadosUtil;
import br.com.ischool.util.Paginacao;

/**
 * @author Daniel Souza de lima e-mail:daniesouza@gmail.com
 * Session Bean implementation class AlunoServiceImpl
 */
@Stateless
@Local(value=AlunoServiceLocal.class)
public class AlunoServiceImpl implements AlunoServiceLocal {

    /**
     * Default constructor. 
     */
    public AlunoServiceImpl() {
        // TODO Auto-generated constructor stub
    }
    
	@EJB
	private AlunoDAOLocal alunoDAO;
	
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
	public void salvarAluno(Aluno aluno) throws ServicoException {
		
		try {
			validarAluno(aluno);

			boolean existeAluno = existeAluno(aluno);
			if (existeAluno) {
				throw new ServicoException("ALUNO_EXISTENTE");
			}

			aluno.setDataCadastro(new Date());
			
			alunoDAO.salvarAluno(aluno);
			
			if(aluno.getUsuarios() != null && !aluno.getUsuarios().isEmpty()){
				
				for(Usuario usuario:aluno.getUsuarios()){
					usuario = servicoGenerico.obterListaLazy(usuario, Usuario.class, "alunos");	
					usuario.getAlunos().add(aluno);	
					usuario.setDataAtualizacao(new Date().getTime());
					servicoGenerico.atualizar(usuario);
				}
			}
			

			if(aluno.getClasses() != null && !aluno.getClasses().isEmpty()){
				
				for(Classe classe:aluno.getClasses()){
					classe = servicoGenerico.obterListaLazy(classe, Classe.class, "alunos");	
					classe.getAlunos().add(aluno);			
					servicoGenerico.atualizar(classe);
				}
			}
				
			
		} catch (DAOException e) {
			
			throw new ServicoException(e);
			
		} catch (ServicoException e) {
			throw e;
		} catch (Exception e) {
			
			throw new ServicoException(e);
		}
					
	}

	
	private void validarAluno(Aluno aluno) throws ServicoException {
		ServicoException se = new ServicoException();

		if (DadosUtil.isEmpty(aluno)) {
			se.adicionarMensagem("ALUNO_NAO_VAZIO");
			throw se;
		}
		
		if (DadosUtil.isEmpty(aluno.getNome())) {
			se.adicionarMensagem("NOME_NAO_VAZIO");
		}
		
		if (DadosUtil.isEmpty(aluno.getCodigoAluno())) {
			se.adicionarMensagem("CODIGO_NAO_VAZIO");
		}
		if (DadosUtil.isEmpty(aluno.getCpf())) {
			se.adicionarMensagem("CPF_NAO_VAZIO");
		}
		
		if (!DadosUtil.isEmpty(aluno.getClasses()) && aluno.getClasses().size()>1) {
			se.adicionarMensagem("ALUNO_CLASSES_INVALIDO");
		}

		if (se.existeErro()) {
			throw se;
		}
	}
	
	private boolean existeAluno(Aluno aluno) throws ServicoException {
		try {
			if (!DadosUtil.isEmpty(aluno.getIdAluno())) {
				Aluno alunoAux = alunoDAO.selectById(aluno.getIdAluno());
				if (alunoAux.getCpf().equals(aluno.getCpf())) {
					return false;
				}else{
					
					Aluno filtro = new Aluno();
					filtro.setCliente(new Cliente());
					filtro.getCliente().setId(aluno.getCliente().getId());				
					filtro.setCpf(aluno.getCpf());
					
					int qtdeAlunos = alunoDAO.consultarQtde(filtro);
					
					return qtdeAlunos > 0;
					
				}
			}

			Aluno filtro = new Aluno();
			filtro.setCodigoAluno(aluno.getCodigoAluno());
			filtro.setCliente(new Cliente());
			filtro.getCliente().setId(aluno.getCliente().getId());

			int qtdeAlunos = alunoDAO.consultarQtde(filtro);
			
			if(qtdeAlunos > 0){
				return true;
			}
			
			filtro.setCodigoAluno(null);
			filtro.setCpf(aluno.getCpf());
			
			qtdeAlunos = alunoDAO.consultarQtde(filtro);
			
			return qtdeAlunos > 0;

		} catch (DAOException e) {		
			throw new ServicoException(e);			
		} catch (Exception e) {			
			throw new ServicoException(e);
		}
	}

	@Override
	public void alterarAluno(Aluno aluno) throws ServicoException {
		
		try {
			validarAluno(aluno);

			boolean existeAluno = existeAluno(aluno);
			if (existeAluno) {
				throw new ServicoException("ALUNO_EXISTENTE");
			}
					
			
			List<Usuario> listaRemoverUsuarios = ((Aluno) servicoGenerico.obterListaLazy(aluno, Aluno.class, "usuarios")).getUsuarios();

			for(Usuario usuario:listaRemoverUsuarios){				
				usuario.getAlunos().remove(aluno);
				usuario.setDataAtualizacao(new Date().getTime());
				servicoGenerico.atualizar(usuario);				
			}

			for(Usuario usuario:aluno.getUsuarios()){				
				usuario = servicoGenerico.obterListaLazy(usuario, Usuario.class, "alunos");			
				usuario.getAlunos().add(aluno);
				usuario.setDataAtualizacao(new Date().getTime());
				servicoGenerico.atualizar(usuario);							
			}
			
			
			List<Classe> listaRemoverClasses = ((Aluno) servicoGenerico.obterListaLazy(aluno, Aluno.class, "classes")).getClasses();
			
			for(Classe classe:listaRemoverClasses){
				classe.getAlunos().remove(aluno);
				servicoGenerico.atualizar(classe);			
			}
			
						
			for(Classe classe:aluno.getClasses()){
				classe = servicoGenerico.obterListaLazy(classe, Classe.class, "alunos");	
				classe.getAlunos().add(aluno);
				servicoGenerico.atualizar(classe);		
			}


			alunoDAO.alterarAluno(aluno);

			
		} catch (DAOException e) {			
			throw new ServicoException(e);			
		} catch (ServicoException e) {
			throw e;
		} catch (Exception e) {			
			throw new ServicoException(e);
		}
					
	}

	@Override
	public Collection<Aluno> listarAlunos() throws ServicoException {
		
		try {
			
			return alunoDAO.listarAlunos();
			
		} catch (DAOException e) {
			
			throw new ServicoException(e);
			
		} catch (Exception e) {
			
			throw new ServicoException(e);
		}
	}

	@Override
	public Collection<Aluno> listarAlunos(Aluno filtro,Paginacao paginacao) throws ServicoException {

		try {
			
			if(DadosUtil.isEmpty(paginacao)){
				paginacao = new Paginacao(-1);
			}
			
			return alunoDAO.listarAlunos(filtro,paginacao);
			
		} catch (DAOException e) {
			
			throw new ServicoException(e);
			
		} catch (Exception e) {
			
			throw new ServicoException(e);
		}
	}


	@Override
	public void excluirAluno(Aluno Aluno) throws ServicoException {
		
		try {
			
			alunoDAO.excluirAluno(Aluno);
			
		} catch (DAOException e) {
			
			throw new ServicoException(e);
			
		} catch (Exception e) {
			
			throw new ServicoException(e);
		}
		
	}

	@Override
	public Aluno selectById(Long id) throws ServicoException {
		
		try {
			
			return alunoDAO.selectById(id);

			
		} catch (DAOException e) {
			
			throw new ServicoException(e);
			
		} catch (Exception e) {
			
			throw new ServicoException(e);
		}
	}


}
