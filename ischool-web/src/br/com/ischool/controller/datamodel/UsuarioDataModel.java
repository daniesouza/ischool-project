package br.com.ischool.controller.datamodel;

import java.util.List;

import br.com.ischool.business.UsuarioServiceLocal;
import br.com.ischool.entity.Usuario;
import br.com.ischool.exceptions.ServicoException;
import br.com.ischool.util.Paginacao;




public class UsuarioDataModel extends GenericDataModel<Usuario> {

	

	/**
	 * 
	 */
	private static final long serialVersionUID = 4714236311597872095L;
	
	private UsuarioServiceLocal usuarioLocal;
	
	@Override
	public List<Usuario> obterResultados(Usuario filtro, Paginacao paginacao) throws ServicoException {
	
		List<Usuario> listaUsuario = (List<Usuario>) usuarioLocal.listarUsuarios(filtro, paginacao);
			
		return listaUsuario;


	}

	public UsuarioServiceLocal getUsuarioLocal() {
		return usuarioLocal;
	}

	public void setUsuarioLocal(UsuarioServiceLocal usuarioLocal) {
		this.usuarioLocal = usuarioLocal;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}


	
}
