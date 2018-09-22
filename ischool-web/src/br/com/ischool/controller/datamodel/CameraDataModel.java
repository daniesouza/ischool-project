package br.com.ischool.controller.datamodel;

import java.util.List;

import br.com.ischool.business.CameraServiceLocal;
import br.com.ischool.entity.Camera;
import br.com.ischool.exceptions.ServicoException;
import br.com.ischool.util.Paginacao;

public class CameraDataModel extends GenericDataModel<Camera> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6516792638584439219L;
	
	private CameraServiceLocal cameraLocal;
	
	@Override
	public List<Camera> obterResultados(Camera filtro, Paginacao paginacao) throws ServicoException {

		List<Camera> listaCamera = (List<Camera>) cameraLocal.listarCameras(filtro, paginacao);
			
		return listaCamera;

	}

	public CameraServiceLocal getCameraLocal() {
		return cameraLocal;
	}



	public void setCameraLocal(CameraServiceLocal CameraLocal) {
		this.cameraLocal = CameraLocal;
	}



	public static long getSerialversionuid() {
		return serialVersionUID;
	}


	

}
