package br.com.marcenaria.client.exception;

import java.util.Map;

import javax.ejb.ApplicationException;

import lombok.Getter;

@ApplicationException(rollback = true)
public class BusinessException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1782491378374060599L;
	
	@Getter
	private final String message;
	
	@Getter
	private final transient Map<String, Object> args;
	
	public BusinessException(String message) {
		this.message = message;
		this.args = null;
	}
	
	public BusinessException(String message, Map<String, Object> args) {
		this.message = message;
		this.args = args;
	}
	
	public static boolean hasCause(Throwable e, String cause) {
		if (e.getCause() == null)
			return false;
		
		if (e.getCause().getMessage().equals(cause))
			return true;
		
		return hasCause(e.getCause(), cause);
	}
}