package br.com.ischool.business;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

import br.com.ischool.entity.Aluno;
import br.com.ischool.exceptions.ServicoException;
import br.com.ischool.util.Constantes;
import br.com.ischool.util.DadosUtil;

/**
 * @author Daniel Souza de lima e-mail:daniesouza@gmail.com
 * Session Bean implementation class ArquivoServiceImpl
 */
@Stateless
@TransactionAttribute(TransactionAttributeType.NEVER)
@Local(value=ArquivoServiceLocal.class)
public class ArquivoServiceImpl implements ArquivoServiceLocal {

    /**
     * Default constructor. 
     */
    public ArquivoServiceImpl() {
        // TODO Auto-generated constructor stub
    }
    
	
    @PostConstruct
    private void carregarInformacoes()
    {
    	System.out.println("CARREGADO OS RECURSOS DO EJB "+this.getClass().getName());
    }
    
    @PreDestroy
    private void clear()
    {
    	System.out.println("LIBERANDO OS RECURSOS DO EJB "+this.getClass().getName());
    }
	

	private void validarArquivo(InputStream arquivo,Aluno aluno,String nomeArquivo) throws ServicoException {
		ServicoException se = new ServicoException();
		
		if (DadosUtil.isEmpty(arquivo)) {
			se.adicionarMensagem("ARQUIVO_NAO_VAZIO");
			
			throw se;
		}
		
		if (DadosUtil.isEmpty(aluno) || DadosUtil.isEmpty(aluno.getId())) {
			se.adicionarMensagem("ALUNO_NAO_VAZIO");
			
			throw se;
		}
		
		if (DadosUtil.isEmpty(nomeArquivo)) {
			se.adicionarMensagem("NOME_NAO_VAZIO");
			throw se;
		}

	}


	@Override
	public File salvarArquivo(InputStream arquivo,Aluno aluno,String nomeArquivo) throws ServicoException{
		try {
			
			validarArquivo(arquivo,aluno,nomeArquivo);
			
			File diretorio = new File(Constantes.LOCAL_SALVAR_ARQUIVO+File.separator+aluno.getId());
			
			if (!diretorio.exists()){
				
				boolean criouDiretorio = diretorio.mkdirs();
				
				if(!criouDiretorio){
					throw new ServicoException("ERRO_CRIAR_DIRETORIO");
				}
			}
			
			File file = new File(diretorio,nomeArquivo);
	          
	        OutputStream out = new FileOutputStream(file);
	       
            int read = 0;
            byte[] bytes = new byte[1024];
          
            while ((read = arquivo.read(bytes)) != -1) {
                out.write(bytes, 0, read);
            }
	        
	        out.flush();
	        out.close();
	               
	    	return file;
	        
		} catch (Exception e) {		
			throw new ServicoException(e);
		}
		
	}
	
	
	@Override
	public BufferedInputStream carregarArquivo(final String filePath) throws ServicoException {
		
		try{
			
			BufferedInputStream input = new BufferedInputStream(new FileInputStream(filePath));
		
	
			return input;
			
		} catch (Exception e) {		
			throw new ServicoException(e);
		}
	
	}
	
	@Override
	public byte[] carregarArquivo(final String filePath,Long offset) throws ServicoException {
		
		try{
			
			int transfLength = Constantes.TAXA_TRANSF_BYTES;
			
			RandomAccessFile file = new RandomAccessFile(filePath, "r");
			
			if(offset + Constantes.TAXA_TRANSF_BYTES > file.length()){
				transfLength = (int) (file.length() - offset);
			}
			
			file.seek(offset);
			byte[] bytes = new byte[transfLength];
					
			file.read(bytes);			
			file.close();
	
			return bytes;
			
		} catch (Exception e) {		
			throw new ServicoException(e);
		}
	
	}

}
