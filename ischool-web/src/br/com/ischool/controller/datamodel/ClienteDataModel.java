package br.com.ischool.controller.datamodel;

import java.util.List;

import br.com.ischool.business.ClienteServiceLocal;
import br.com.ischool.entity.Cliente;
import br.com.ischool.exceptions.ServicoException;
import br.com.ischool.util.Paginacao;

public class ClienteDataModel extends GenericDataModel<Cliente> {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -2575105380004396272L;
	
	private ClienteServiceLocal clienteLocal;
	
	@Override
	public List<Cliente> obterResultados(Cliente filtro, Paginacao paginacao) throws ServicoException {

		List<Cliente> listaCliente = (List<Cliente>) clienteLocal.listarClientes(filtro, paginacao);
			
		return listaCliente;

	}

	public ClienteServiceLocal getClienteLocal() {
		return clienteLocal;
	}



	public void setClienteLocal(ClienteServiceLocal clienteLocal) {
		this.clienteLocal = clienteLocal;
	}



	public static long getSerialversionuid() {
		return serialVersionUID;
	}


	

}
