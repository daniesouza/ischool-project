package br.com.ischool.controller;

import java.io.IOException;
import java.io.Serializable;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.PhaseEvent;
import javax.faces.event.PhaseId;
import javax.faces.event.PhaseListener;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.web.WebAttributes;

import br.com.ischool.business.LoginServiceLocal;
import br.com.ischool.business.UsuarioServiceLocal;
import br.com.ischool.entity.Usuario;
import br.com.ischool.exceptions.ServicoException;
import br.com.ischool.exceptions.WebException;
import br.com.ischool.util.FacesUtil;

/**
 * @author Daniel Souza de lima e-mail:daniesouza@gmail.com
 *      
 */

@ManagedBean
@RequestScoped
public class LoginControllerBean extends AbstractViewHelper<Usuario> implements Serializable ,PhaseListener{

	/**
	 * 
	 */
	private static final long serialVersionUID = -3373998304619377312L;
	
	private String senhaAntiga;
	private String senhaNova;
	private String senhaConfirma;

	@EJB
	private LoginServiceLocal loginService;
	@EJB
	private UsuarioServiceLocal usuarioService;

	
    /**
    * @return
    * @throws ServletException
    * @throws IOException
    */
   public String doLogin() throws ServletException, IOException {
   
	   FacesUtil.addAtributoSessao("loginService", loginService);
	   
       ExternalContext context = FacesContext.getCurrentInstance().getExternalContext();
       RequestDispatcher dispatcher = ((ServletRequest) context.getRequest()).getRequestDispatcher("/j_spring_security_check");

       dispatcher.forward((ServletRequest) context.getRequest(), (ServletResponse) context.getResponse());

       FacesContext.getCurrentInstance().responseComplete();

       return null;
   }

	public void logOut() {
		try {
			ExternalContext context = FacesContext.getCurrentInstance().getExternalContext();
			HttpServletRequest request = (HttpServletRequest) context.getRequest();		
			context.redirect(request.getContextPath()+ "/j_spring_security_logout");
			
		} catch (IOException e) {
			System.out.println("ERROR: " + e.getMessage());
		}
	}

	// ENTIDADE É O USUARIO LOGADO
	public Usuario getUsuarioLogado() throws WebException {
		
		return FacesUtil.recuperarUsuarioSessao();
	}
	
	public void salvarSenha(){
		
		try {
			usuarioService.alterarSenhaUsuario(FacesUtil.recuperarUsuarioSessao(), senhaAntiga, senhaNova, senhaConfirma);
			
			FacesUtil.exibirInfo("mensagem_registro_atualizado");
			
		} catch (ServicoException se) {
			FacesUtil.exibirErro("erro_alterar",se);				
			se.printStackTrace();
		}catch(Exception e){			
			FacesUtil.exibirErro("erro_alterar",e);				
			e.printStackTrace();		
		}		
	}
	
    public void afterPhase(PhaseEvent event) {
    }


    public void beforePhase(PhaseEvent event) {
    	
    	String currentPage = FacesContext.getCurrentInstance().getViewRoot().getViewId();
    	
    	boolean isLoginPage = (currentPage.lastIndexOf("home.xhtml") > -1);
    	
    	if(isLoginPage){
            Exception e = (Exception) FacesContext.getCurrentInstance().
                    getExternalContext().getSessionMap().get(WebAttributes.AUTHENTICATION_EXCEPTION);
           
                  if (e instanceof AuthenticationServiceException || e instanceof BadCredentialsException) {
                      FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(WebAttributes.AUTHENTICATION_EXCEPTION, null);
                      FacesUtil.exibirErro("erro_login", new ServicoException(e.getMessage()));
                  }
    	}

    }

    /* (non-Javadoc)
     * @see javax.faces.event.PhaseListener#getPhaseId()
     * 
     * In which phase you want to interfere?
     */
    @Override
    public PhaseId getPhaseId() {
        return PhaseId.RENDER_RESPONSE;
    }	

	
	@Override
	public void salvarImpl() throws ServicoException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void alterarImpl() throws ServicoException {
	
		//usuarioService.alterarUsuario(entidade);	
	}

	@Override
	public void excluirImpl() throws ServicoException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void inicializar() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void pesquisar() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void reset() {
		// TODO Auto-generated method stub
		
	}

	public String getSenhaAntiga() {
		return senhaAntiga;
	}

	public void setSenhaAntiga(String senhaAntiga) {
		this.senhaAntiga = senhaAntiga;
	}

	public String getSenhaNova() {
		return senhaNova;
	}

	public void setSenhaNova(String senhaNova) {
		this.senhaNova = senhaNova;
	}

	public String getSenhaConfirma() {
		return senhaConfirma;
	}

	public void setSenhaConfirma(String senhaConfirma) {
		this.senhaConfirma = senhaConfirma;
	}

}
