package br.com.ischool.business;

import java.util.Date;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.EJB;
import javax.ejb.Local;
import javax.ejb.Stateless;

import br.com.ischool.entity.Aluno;
import br.com.ischool.entity.AlunoCrypt;
import br.com.ischool.entity.ClienteCrypt;
import br.com.ischool.entity.DeviceRegId;
import br.com.ischool.entity.Usuario;
import br.com.ischool.entity.UsuarioCrypt;
import br.com.ischool.exceptions.DAOException;
import br.com.ischool.exceptions.ServicoException;
import br.com.ischool.security.Crypto;
import br.com.ischool.util.Constantes;
import br.com.ischool.util.DadosUtil;

/**
 * @author Daniel Souza de lima e-mail:daniesouza@gmail.com
 * Session Bean implementation class LoginServiceImpl
 */
@Stateless
@Local(value=LoginServiceLocal.class)
public class LoginServiceImpl implements LoginServiceLocal {

    /**
     * Default constructor. 
     */
    public LoginServiceImpl() {
        // TODO Auto-generated constructor stub
    }
    
	@EJB
	private UsuarioServiceLocal usuarioService;;
	
	@EJB
	private DeviceRegServiceLocal deviceRegServiceLocal;
	
    @PostConstruct
    public void carregarInformacoes(){
    	System.out.println("CARREGADO OS RECURSOS DO EJB "+this.getClass().getName());
    }
    
    @PreDestroy
    public void clear(){
    	System.out.println("LIBERANDO OS RECURSOS DO EJB "+this.getClass().getName());
    }

	@Override
	public Usuario login(Usuario usuario) throws ServicoException {
		
		try {		
			
			Usuario us = usuarioService.pesquisarUsuarioNomeUsuario(usuario.getUsuario());
						
			if(!DadosUtil.isEmpty(us)){
														
				if(us.getAutoridade().equals(Constantes.ADMINISTRADOR)){
															
					if(Crypto.encriptSenhaMD5(usuario.getSenha()).equals(us.getSenha())){
						
						if(!us.getAtivo()){							
							throw new ServicoException("USUARIO_DESATIVADO");			        
						}
					}else{
			            throw new ServicoException("USUARIO_SENHA_INCORRETO");
					}
				}
				else if(us.getAutoridade().equals(Constantes.ADMIN_CLIENTE) || 
						us.getAutoridade().equals(Constantes.PROFESSOR) 	  || 
						us.getAutoridade().equals(Constantes.RESPONSAVEL)){
										
					if(Crypto.encriptSenhaMD5(usuario.getSenha()).equals(us.getSenha())){
						
						if(!us.getCliente().getAtivo()){
						    throw new ServicoException("CLIENTE_DESATIVADO");
						}
						
						if(!us.getAtivo()){								
							throw new ServicoException("USUARIO_DESATIVADO");			        
						}

					}else{
			            throw new ServicoException("USUARIO_SENHA_INCORRETO");
					}
					
				}else{
				    throw new ServicoException("USUARIO_SEM_ROLES");
				}
				
			}else{				
				throw new ServicoException("USUARIO_SENHA_INCORRETO");
			}
			
			return us;
		
		} catch (ServicoException e) {
			throw e;
		} catch (Exception e) {	
			e.printStackTrace();
			throw new ServicoException("SISTEMA_INDISPONIVEL");
		}
		
	}

	@Override
	public UsuarioCrypt loginDevice(Usuario usuario,DeviceRegId deviceRegId) throws ServicoException {

		try {	
			Usuario us = usuarioService.pesquisarUsuarioNomeUsuario(usuario.getUsuario());
 	
			if(!DadosUtil.isEmpty(us)){
				
				String senhaHash = Crypto.encriptSenhaMD5(Constantes.FIXSALT+us.getSenha()+Constantes.FIXSALT);
				
				// ATUALMENTE SO E POSSIVEL LOGAR COMO RESPONSAVEL POR QUE NO DEVICE SO EXISTE UM PERFIL
				// FUTURAMENTE SE PUDER LOGAR COM OUTROS PERFIS.. APAGAR ESTE IF E SUBSTITUIR PELO BLOCO COMENTADO ABAIXO.
				// Daniel.. 08/02/2015
				if(us.getAutoridade().equals(Constantes.RESPONSAVEL)){
					
					if(usuario.getSenha().equals(senhaHash)){
						
						if(!us.getCliente().getAtivo()){
						    throw new ServicoException("CLIENTE_DESATIVADO");
						}
						
						if(!us.getAtivo()){							
							throw new ServicoException("USUARIO_DESATIVADO");			        
						}
					}else{
			            throw new ServicoException("USUARIO_SENHA_INCORRETO");
					}
				}
				
//				if(us.getAutoridade().equals(Constantes.ADMINISTRADOR)){
//															
//					if(usuario.getSenha().equals(senhaHash)){
//						
//						if(!us.getAtivo()){							
//							throw new ServicoException("USUARIO_DESATIVADO");			        
//						}
//					}else{
//			            throw new ServicoException("USUARIO_SENHA_INCORRETO");
//					}
//				}
//				else if(us.getAutoridade().equals(Constantes.ADMIN_CLIENTE) || 
//						us.getAutoridade().equals(Constantes.PROFESSOR) 	  || 
//						us.getAutoridade().equals(Constantes.RESPONSAVEL)){
//										
//					if(usuario.getSenha().equals(senhaHash)){
//						
//						if(!us.getCliente().getAtivo()){
//						    throw new ServicoException("CLIENTE_DESATIVADO");
//						}
//						
//						if(!us.getAtivo()){								
//							throw new ServicoException("USUARIO_DESATIVADO");			        
//						}
//
//					}else{
//			            throw new ServicoException("USUARIO_SENHA_INCORRETO");
//					}
					
				else{
				    throw new ServicoException("USUARIO_SEM_ROLES");
				}
				
			}else{				
				throw new ServicoException("USUARIO_SENHA_INCORRETO");
			}	    	
	    		

			deviceRegId.setUsuario(us);
			deviceRegId = deviceRegServiceLocal.mergeDeviceRegId(deviceRegId);
   		
			us.setDataValidadeHash(deviceRegId.getDataValidadeHash());
			us.setSecurityHashKey(deviceRegId.getSecurityHashKey());
			us.setDeviceId(deviceRegId.getId());
    		    			
			UsuarioCrypt usuarioCrypt = us.encriptarUsuario(); 				
			ClienteCrypt clienteCrypt = us.getCliente().encriptarCliente();
			

			AlunoCrypt[] alunos = new AlunoCrypt[us.getAlunos().size()];
			
			int i=0;
			
			for(Aluno aluno:us.getAlunos()){
				AlunoCrypt alunoCrypt = aluno.encryptarAluno();
							
				alunos[i] = alunoCrypt;
				i++;
			}
			
			usuarioCrypt.setCliente(clienteCrypt);
			usuarioCrypt.setAlunos(alunos);
			
			return usuarioCrypt;

    	
		} catch (DAOException e) {
			e.printStackTrace();
			throw new ServicoException("SISTEMA_INDISPONIVEL");			
		} catch (ServicoException e) {
			throw e;
		} catch (Exception e) {	
			e.printStackTrace();
			throw new ServicoException("SISTEMA_INDISPONIVEL");
		}
	}

	@Override
	public Usuario validarAcessoMetodo(Long idUsuario, String keyHash,long dataAtualizacao) throws ServicoException {
    	if(DadosUtil.isEmpty(idUsuario)){
    		throw new ServicoException("USUARIO_NOT_NULL");
    	}
    	
    	if(DadosUtil.isEmpty(keyHash)){
    		throw new ServicoException("HASH_NOT_NULL");
    	}
		
    	Usuario usuario = new Usuario();
    	usuario.setId(idUsuario);

    	usuario = usuarioService.selectById(idUsuario);
    	
    	if(DadosUtil.isEmpty(usuario)){
    		throw new ServicoException("USUARIO_NAO_ENCONTRADO");
    	}
    	
 		
		DeviceRegId deviceLogado = deviceRegServiceLocal.selectBySecurityHash(keyHash);
		
   	
    	if(deviceLogado != null){		
    		if(deviceLogado.getDataValidadeHash()<new Date().getTime()){
    			throw new ServicoException("DATA_HASH_EXPIRADO");
    		}else if (!usuario.getDataAtualizacao().equals(dataAtualizacao)){
    			throw new ServicoException("USUARIO_ATUALIZADO");
    		}
    	}else{
    		throw new ServicoException("HASH_INVALIDO");
    	}
    	
    	return usuario;
	}
	
	

}
