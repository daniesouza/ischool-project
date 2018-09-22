package br.com.ischool.exceptions;

/**
 * @author Daniel Souza de lima e-mail:daniesouza@gmail.com
 *      
 */
public class WebException extends Exception {

	public WebException(Throwable e) {
		super(e);
	}

	public WebException(String str) {
		super(str);
	}	

	private static final long serialVersionUID = 1L;

}
