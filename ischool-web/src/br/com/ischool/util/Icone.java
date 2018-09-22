package br.com.ischool.util;

import java.io.Serializable;

/**
 * @author Daniel Souza de lima e-mail:daniesouza@gmail.com
 *      
 */

public class Icone implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 8998016977368007589L;
	private String imagem;
	private String nome;
	


	public Icone(String imagem, String nome) {
		super();
		this.imagem = imagem;
		this.nome = nome;
	}
	
	
	public String getImagem() {
		return imagem;
	}
	public void setImagem(String imagem) {
		this.imagem = imagem;
	}
	public String getNome() {
		return nome;
	}
	public void setNome(String nome) {
		this.nome = nome;
	}


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((imagem == null) ? 0 : imagem.hashCode());
		return result;
	}


	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Icone other = (Icone) obj;
		if (imagem == null) {
			if (other.imagem != null)
				return false;
		} else if (!imagem.equals(other.imagem))
			return false;
		return true;
	}
	
	
	
	
	
}
