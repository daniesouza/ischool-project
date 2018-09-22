package br.com.ischool.service;

import java.util.Collection;

import javax.ejb.Local;

import br.com.ischool.entity.EventoExeCrypt;
import br.com.ischool.entity.MensagemCrypt;
import br.com.ischool.entity.UsuarioCrypt;
import br.com.ischool.exceptions.ServicoException;

@Local
public interface IschoolWebServiceLocal {
	
	public UsuarioCrypt loginDevice(final byte[] encUsuario,final byte[] encPass,final byte[] deviceRegId,final byte[] strDeviceReg,final byte[] tipoDevice) throws ServicoException;

	public byte[] enviarMensagemWeb(final byte[] encIdUsuario, final MensagemCrypt mensagemCript,final byte[] encDataAtualizacao, final byte[] keyHash) throws ServicoException;

	public byte[] carregarArquivo(final byte[] encIdUsuario,final byte[] encIdMensagem,final byte[] encOffset,final byte[] encDataAtualizacao, final byte[] keyHash) throws ServicoException;

	public Collection<MensagemCrypt> listarMensagens(final byte[] encIdUsuario, final byte[] encIdAluno,final byte[] encIdsMensagens,final byte[] encDataAtualizacao, final byte[] keyHash) throws ServicoException;

	public Collection<EventoExeCrypt> listarEventosExecutados(byte[] encIdUsuario,byte[] encIdAluno,byte[] datasAtualizacao,final byte[] encDataAtualizacao,final byte[] encDataPesquisa, 
			byte[] keyHash) throws ServicoException;


}
