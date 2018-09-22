package br.com.ischool.controller.datamodel;

import java.util.List;

import br.com.ischool.business.EventoServiceLocal;
import br.com.ischool.entity.Evento;
import br.com.ischool.exceptions.ServicoException;
import br.com.ischool.util.Paginacao;

public class EventoDataModel extends GenericDataModel<Evento> {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -2575105380004396272L;
	
	private EventoServiceLocal eventoLocal;
	
	@Override
	public List<Evento> obterResultados(Evento filtro, Paginacao paginacao) throws ServicoException {

		
		List<Evento> listaEvento = (List<Evento>) eventoLocal.listarEventos(filtro, paginacao);
			
		return listaEvento;

	}

	public EventoServiceLocal getEventoLocal() {
		return eventoLocal;
	}



	public void setEventoLocal(EventoServiceLocal EventoLocal) {
		this.eventoLocal = EventoLocal;
	}



	public static long getSerialversionuid() {
		return serialVersionUID;
	}


	

}
