package br.com.ischool.controller.authentication;

import java.util.ArrayList;
import java.util.List;
import org.springframework.dao.DataAccessException;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.GrantedAuthorityImpl;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import br.com.ischool.business.LoginServiceLocal;
import br.com.ischool.entity.Usuario;
import br.com.ischool.exceptions.ServicoException;
import br.com.ischool.util.FacesUtil;
 
 
public class CustomAuthenticationProvider implements AuthenticationProvider,UserDetailsService {
	 
    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String username = authentication.getName();
        String password = (String) authentication.getCredentials();
        
        LoginServiceLocal loginService = (LoginServiceLocal) FacesUtil.getAtributoSessao("loginService");
        
    	Usuario usuario = new Usuario();
    	
    	usuario.setUsuario(username);
    	usuario.setSenha(password);

    	try {
			usuario = loginService.login(usuario);
			
	    	List<GrantedAuthority> autoridades = new ArrayList<GrantedAuthority>();       
	    	
	    	autoridades.add(new GrantedAuthorityImpl(usuario.getAutoridade()));
	    	
	    	FacesUtil.cadastrarUsuarioSessao(usuario);
	    	FacesUtil.removeAtributoSessao("loginService");
			
	        return new UsernamePasswordAuthenticationToken(usuario.getUsuario(), usuario.getSenha(),  autoridades);
		} catch (ServicoException e) {
			 throw new AuthenticationServiceException(e.getMessage(),e);
		}

    }
    
    @Override
    public UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException, DataAccessException {       
        return null;
    }

 
    @Override
    public boolean supports(Class<?> arg0) {
        return true;
    }
}
