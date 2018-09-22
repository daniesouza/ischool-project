package br.com.ischool.business;

import java.util.Calendar;
import java.util.Collection;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.EJB;
import javax.ejb.Local;
import javax.ejb.Stateless;

import br.com.ischool.dao.DeviceRegDAOLocal;
import br.com.ischool.entity.DeviceRegId;
import br.com.ischool.exceptions.DAOException;
import br.com.ischool.exceptions.ServicoException;
import br.com.ischool.security.Crypto;
import br.com.ischool.util.Constantes;
import br.com.ischool.util.DadosUtil;
import br.com.ischool.util.Paginacao;

/**
 * @author Daniel Souza de lima e-mail:daniesouza@gmail.com
 * Session Bean implementation class DeviceRegIdServiceImpl
 */
@Stateless
@Local(value=DeviceRegServiceLocal.class)
public class DeviceRegServiceImpl implements DeviceRegServiceLocal {

	@EJB
	private DeviceRegDAOLocal deviceRegDAO;
		
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
	
    
	private void validarDeviceRegId(DeviceRegId deviceRegId) throws ServicoException {
		ServicoException se = new ServicoException();
		
		
		if (DadosUtil.isEmpty(deviceRegId)) {
			se.adicionarMensagem("DEVICEREGID_NAO_VAZIO");
			throw se;
		}

		if (DadosUtil.isEmpty(deviceRegId.getId()) && DadosUtil.isEmpty(deviceRegId.getRegid())) {
			se.adicionarMensagem("DEVICEREG_ID_NAO_VAZIO");
		}
		
		if (DadosUtil.isEmpty(deviceRegId.getDevice()) || DadosUtil.isEmpty(deviceRegId.getDevice().getId())) {
			se.adicionarMensagem("DEVICE_NAO_VAZIO");
		}
		
		if (DadosUtil.isEmpty(deviceRegId.getUsuario()) || DadosUtil.isEmpty(deviceRegId.getUsuario().getId())) {
			se.adicionarMensagem("USUARIO_NAO_VAZIO");
		}
		if (se.existeErro()) {
			throw se;
		}
	}
	
//	private boolean existeDeviceRegId(DeviceRegId deviceRegId) throws ServicoException {
//		try {
//			int qtdeDevices = 0;
//			if (!DadosUtil.isEmpty(deviceRegId.getId())) {
//				qtdeDevices = deviceRegDAO.consultarQtde(deviceRegId);
//			}
//			
//			return qtdeDevices > 0;
//
//		} catch (DAOException e) {
//			throw new ServicoException(e.getMessage());
//		}
//	}
    
    
	@Override
	public DeviceRegId mergeDeviceRegId(DeviceRegId deviceRegId) throws ServicoException {
		
		try {
				validarDeviceRegId(deviceRegId);
				
				
	    		Calendar calendar = Calendar.getInstance();
	    		
	    		calendar.add(Calendar.MINUTE, Constantes.MINUTOSVALIDADEHASH);
	    		
	    		deviceRegId.setDataValidadeHash(calendar.getTimeInMillis());
	    		
	    		String securityHashKey = Crypto.encriptSenhaMD5(Crypto.gerarSalt()+deviceRegId.getDataValidadeHash()+Crypto.gerarSalt());
	    		
	    		deviceRegId.setSecurityHashKey(securityHashKey);	
				
	    		DeviceRegId devReg = null;
	    		if(!DadosUtil.isEmpty(deviceRegId.getId())){
	    			devReg = deviceRegDAO.selectById(deviceRegId.getId());
	    		}else{
	    			devReg = deviceRegDAO.pesquisarDeviceRegIdNomeDeviceRegId(deviceRegId.getRegid());
	    		}
				
				
				if(devReg != null){
					
					devReg.setUsuario(deviceRegId.getUsuario());
					devReg.setSecurityHashKey(deviceRegId.getSecurityHashKey());
					devReg.setDataValidadeHash(deviceRegId.getDataValidadeHash());
					
					deviceRegDAO.alterarDeviceRegId(devReg);
					
					return devReg;
					
				}else{
					deviceRegId.setId(null);
					deviceRegDAO.salvarDeviceRegId(deviceRegId);
					
					return deviceRegId;
				}
	
			} catch (DAOException e) {				
				throw new ServicoException(e);				
			} catch (ServicoException e) {
				throw e;
			} catch (Exception e) {				
				throw new ServicoException(e);
			}
		
			
	}

	
	@Override
	public Collection<DeviceRegId> listarDeviceRegIds() throws ServicoException {
		
		try {
			
			return deviceRegDAO.listarDeviceRegIds();
			
		} catch (DAOException e) {			
			throw new ServicoException(e);			
		} catch (Exception e) {			
			throw new ServicoException(e);
		}
	}
	

	@Override
	public Collection<DeviceRegId> listarDeviceRegIds(DeviceRegId filtro,Paginacao paginacao) throws ServicoException {

		try {
			
			if(DadosUtil.isEmpty(paginacao)){
				paginacao = new Paginacao(-1);
			}
			
			return deviceRegDAO.listarDeviceRegIds(filtro,paginacao);
			
		} catch (DAOException e) {			
			throw new ServicoException(e);			
		} catch (Exception e) {		
			throw new ServicoException(e);
		}
	}


	@Override
	public void excluirDeviceRegId(DeviceRegId deviceRegId) throws ServicoException {
		
		try {
			
			deviceRegDAO.excluirDeviceRegId(deviceRegId);
			
		} catch (DAOException e) {			
			throw new ServicoException(e);			
		} catch (Exception e) {		
			throw new ServicoException(e);
		}
		
	}

	@Override
	public DeviceRegId selectById(Long id) throws ServicoException {
		
		try {			
			return deviceRegDAO.selectById(id);			
		} catch (DAOException e) {			
			throw new ServicoException(e);			
		} catch (Exception e) {		
			throw new ServicoException(e);
		}
	}
	
	@Override
	public DeviceRegId selectBySecurityHash(String hash) throws ServicoException {
		
		try {			
			return deviceRegDAO.selectBySecurityHash(hash);			
		} catch (DAOException e) {			
			throw new ServicoException(e);			
		} catch (Exception e) {		
			throw new ServicoException(e);
		}
	}

	@Override
	public void deleteByIds(Collection<Long> idsIN) throws ServicoException {
		try {			
			deviceRegDAO.deleteByIds(idsIN);			
		} catch (DAOException e) {			
			throw new ServicoException(e);			
		} catch (Exception e) {		
			throw new ServicoException(e);
		}
		
	}


}
