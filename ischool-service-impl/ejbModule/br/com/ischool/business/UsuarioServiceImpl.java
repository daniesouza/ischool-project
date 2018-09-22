package br.com.ischool.business;

import java.security.NoSuchAlgorithmException;
import java.util.Collection;
import java.util.Date;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.EJB;
import javax.ejb.Local;
import javax.ejb.Stateless;

import br.com.ischool.dao.UsuarioDAOLocal;
import br.com.ischool.entity.Cliente;
import br.com.ischool.entity.Usuario;
import br.com.ischool.exceptions.DAOException;
import br.com.ischool.exceptions.ServicoException;
import br.com.ischool.security.MD5CheckSum;
import br.com.ischool.util.Constantes;
import br.com.ischool.util.DadosUtil;
import br.com.ischool.util.Paginacao;

/**
 * @author Daniel Souza de lima e-mail:daniesouza@gmail.com
 * Session Bean implementation class UsuarioServiceImpl
 */
@Stateless
@Local(value=UsuarioServiceLocal.class)
public class UsuarioServiceImpl implements UsuarioServiceLocal {

	@EJB
	private UsuarioDAOLocal usuarioDAO;
		
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
	
    
	private void validarUsuario(Usuario usuario) throws ServicoException {
		ServicoException se = new ServicoException();
		
		
		if (DadosUtil.isEmpty(usuario)) {
			se.adicionarMensagem("USUARIO_NAO_VAZIO");
			throw se;
		}

		if (DadosUtil.isEmpty(usuario.getUsuario())) {
			se.adicionarMensagem("USUARIO_NAO_VAZIO");
		}
		if (DadosUtil.isEmpty(usuario.getNome())) {
			se.adicionarMensagem("NOME_NAO_VAZIO");
		}
		if (DadosUtil.isEmpty(usuario.getSenha())) {
			se.adicionarMensagem("SENHA_NAO_VAZIO");
		}
		else if(usuario.getSenha().length()<Constantes.TAMANHO_SENHA){

			se.adicionarMensagem("TAMANHO_SENHA_PEQUENA");			
		}
		
		if (DadosUtil.isEmpty(usuario.getCpf())) {
			se.adicionarMensagem("CPF_NAO_VAZIO");
		}
		
		if (DadosUtil.isEmpty(usuario.getAutoridade())) {
			se.adicionarMensagem("AUTORIDADE_NAO_VAZIO");
		}
		
		// TODO SE UM DIA EXISTIR UMA TELA DE ALTERACAO DE PROFESSORES COM CLASSES.. DESCOMENTAR ESTA LINHA
//		if (!DadosUtil.isEmpty(usuario.getClasses()) && usuario.getClasses().size()>1) {
//			se.adicionarMensagem("PROFESSOR_CLASSES_INVALIDO");
//		}		

		if (se.existeErro()) {
			throw se;
		}
	}
	
	private boolean existeUsuario(Usuario usuario) throws ServicoException {
		try {
			if (!DadosUtil.isEmpty(usuario.getIdUsuario())) {
				Usuario usuarioAux = usuarioDAO.selectById(usuario.getIdUsuario());
				if (usuarioAux.getCpf().equals(usuario.getCpf())) {
					return false;
				}else{
					
					Usuario filtro = new Usuario();
					filtro.setCliente(new Cliente());
					filtro.getCliente().setId(usuario.getCliente().getId());				
					filtro.setCpf(usuario.getCpf());
					
					int qtdeAlunos = usuarioDAO.consultarQtde(filtro);
					
					return qtdeAlunos > 0;
					
				}
			}

			Usuario filtro = new Usuario();
			filtro.setUsuario(usuario.getUsuario());
			int qtdeUsuarios = usuarioDAO.consultarQtde(filtro);
			
			if(qtdeUsuarios > 0){
				return true;
			}
			
			filtro.setUsuario(null);
			filtro.setCpf(usuario.getCpf());
			qtdeUsuarios = usuarioDAO.consultarQtde(filtro);		
			
			return qtdeUsuarios > 0;

		} catch (DAOException e) {
			throw new ServicoException(e.getMessage());
		}
	}
    
    
	@Override
	public void salvarUsuario(Usuario usuario) throws ServicoException {
		
		try {
				validarUsuario(usuario);
				
				boolean existeUsuario = existeUsuario(usuario);
				if (existeUsuario) {
					throw new ServicoException("USUARIO_EXISTENTE");
				}
				
				usuario.setUsuario(usuario.getUsuario().trim());
				usuario.setSenha(MD5CheckSum.gerarCodigoHash(usuario.getSenha()));
				usuario.setDtCad(new Date());
				usuario.setDataSenha(new Date());
				usuario.setTrocarSenha(false);
				usuario.setTentativasSenhaIncorreta((byte)0);
				usuario.setDataAtualizacao(new Date().getTime());
							
				usuarioDAO.salvarUsuario(usuario);
				
			
				
			} catch (DAOException e) {				
				throw new ServicoException(e);				
			} catch (ServicoException e) {
				throw e;
			} catch (Exception e) {				
				throw new ServicoException(e);
			}
		
			
	}

	@Override
	public void alterarUsuario(Usuario usuario) throws ServicoException {
		

		try {
			
			validarUsuario(usuario);
			
			boolean existeUsuario = existeUsuario(usuario);
			if (existeUsuario) {
				throw new ServicoException("USUARIO_EXISTENTE");
			}
			
			usuario.setDataAtualizacao(new Date().getTime());
			
			usuarioDAO.alterarUsuario(usuario);
			
			
		} catch (DAOException e) {			
			throw new ServicoException(e);			
		} catch (ServicoException e) {
			throw e;
		} catch (Exception e) {			
			throw new ServicoException(e);
		}
	}
	
	
	@Override
	public Collection<Usuario> listarUsuarios() throws ServicoException {
		
		try {
			
			return usuarioDAO.listarUsuarios();
			
		} catch (DAOException e) {			
			throw new ServicoException(e);			
		} catch (Exception e) {			
			throw new ServicoException(e);
		}
	}
	
	@Override
	public Collection<Usuario> listarUsuariosSemClienteSemAdmin() throws ServicoException {
		
		try {
			
			return usuarioDAO.listarUsuariosSemClienteSemAdmin();
			
		} catch (DAOException e) {			
			throw new ServicoException(e);			
		} catch (Exception e) {		
			throw new ServicoException(e);
		}
	}

	@Override
	public Collection<Usuario> listarUsuarios(Usuario filtro,Paginacao paginacao) throws ServicoException {

		try {
			
			if(DadosUtil.isEmpty(paginacao)){
				paginacao = new Paginacao(-1);
			}
			
			return usuarioDAO.listarUsuarios(filtro,paginacao);
			
		} catch (DAOException e) {			
			throw new ServicoException(e);			
		} catch (Exception e) {		
			throw new ServicoException(e);
		}
	}


	@Override
	public void excluirUsuario(Usuario usuario) throws ServicoException {
		
		try {
			
			usuarioDAO.excluirUsuario(usuario);
			
		} catch (DAOException e) {			
			throw new ServicoException(e);			
		} catch (Exception e) {		
			throw new ServicoException(e);
		}
		
	}

	@Override
	public Usuario selectById(Long id) throws ServicoException {
		
		try {
			
			return usuarioDAO.selectById(id);
			
		} catch (DAOException e) {			
			throw new ServicoException(e);			
		} catch (Exception e) {		
			throw new ServicoException(e);
		}
	}

	@Override
	public void alterarSenhaUsuario(Usuario usuario, String senhaAntiga,String senhaNova, String senhaConfirma) throws ServicoException {
		
		ServicoException se = new ServicoException();
		
			
		try {
			
			checkSenhaAntiga(usuario.getSenha(),senhaAntiga);
										
			if(senhaNova.length()<Constantes.TAMANHO_SENHA){

				se.adicionarMensagem("TAMANHO_SENHA_PEQUENA");			
			}
			else{
				
				if(senhaNova.equals(senhaConfirma)){
					
					usuario.setDataSenha(new Date());
					usuario.setTrocarSenha(false);
					usuario.setTentativasSenhaIncorreta((byte)0);
					usuario.setSenha(MD5CheckSum.gerarCodigoHash(senhaNova));
					
					
					alterarUsuario(usuario);
											
				}else{			
					se.adicionarMensagem("SENHA_NAO_CONFERE_NOVA");			
				}
				
			}
	
			if (se.existeErro()) {
				throw se;
			}


		} catch (NoSuchAlgorithmException e) {
			throw new ServicoException(e);
		} catch (ServicoException e) {
			throw e;
		} 
		
	}

	
	public void checkSenhaAntiga(String senhaNova, String senhaAntiga) throws ServicoException{
		
		ServicoException se = new ServicoException();

		
		if(!DadosUtil.isEmpty(senhaAntiga)){
			
			try {
				String pass = MD5CheckSum.gerarCodigoHash(senhaAntiga);
				
				if(!pass.equals(senhaNova)){								
					se.adicionarMensagem("SENHA_ANTIGA_INVALIDA");	
				}
			} catch (NoSuchAlgorithmException e) {			
				se.adicionarMensagem("FALHA_CHECAR_SENHA");	
				throw new ServicoException(e);
			}
		}
		else{			
			se.adicionarMensagem("SENHA_ANTIGA_NAO_VAZIA");	
		}
		

		if (se.existeErro()) {
			throw se;
		}
	
		
	}
	

	@Override
	public Usuario pesquisarUsuarioNomeUsuario(String nomeUsuario) throws ServicoException {
		
		try {
			
			return usuarioDAO.pesquisarUsuarioNomeUsuario(nomeUsuario);
			
		} catch (DAOException e) {			
			throw new ServicoException(e);			
		} catch (Exception e) {		
			throw new ServicoException(e);
		}
	}


}
