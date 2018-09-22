package br.com.ischool.controller;

import java.io.Serializable;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import br.com.ischool.business.CameraServiceLocal;
import br.com.ischool.controller.datamodel.CameraDataModel;
import br.com.ischool.entity.Camera;
import br.com.ischool.exceptions.ServicoException;

/**
 * @author Daniel Souza de lima e-mail:daniesouza@gmail.com
 *      
 */

@ManagedBean
@ViewScoped
public class CameraBean extends AbstractViewHelper<Camera> implements Serializable{



	/**
	 * 
	 */
	private static final long serialVersionUID = -5300994858688793669L;

	@EJB
	private CameraServiceLocal cameraService;
	
	private CameraDataModel cameraDataModel = new CameraDataModel();

			
	
	@Override
	public void inicializar() {
		
		filtro  = new Camera();
		//getFiltro().setCliente(new Cliente());	
		cameraDataModel.setCameraLocal(cameraService);
		
	}

	@Override
	public void pesquisar() {
		
		cameraDataModel.setFiltro(filtro);
		
	}

	@Override
	public void reset() {
		setEntidade(new Camera());	
	}

	@Override
	public void salvarImpl() throws ServicoException {
		       
		cameraService.salvarCamera(entidade);
		reset();		   
	}

	@Override
	public void alterarImpl() throws ServicoException {
		
		cameraService.alterarCamera(entidade);
		reset();	
	}

	@Override
	public void excluirImpl() throws ServicoException {
		
		cameraService.excluirCamera(entidade);
		
		reset();	
	}
	
	public void novo(){
		reset();
	}

	public CameraServiceLocal getCameraService() {
		return cameraService;
	}

	public void setCameraService(CameraServiceLocal CameraService) {
		this.cameraService = CameraService;
	}

	public CameraDataModel getCameraDataModel() {
		return cameraDataModel;
	}

	public void setCameraDataModel(CameraDataModel CameraDataModel) {
		this.cameraDataModel = CameraDataModel;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	

}
