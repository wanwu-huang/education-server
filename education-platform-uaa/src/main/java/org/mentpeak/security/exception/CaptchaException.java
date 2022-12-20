package org.mentpeak.security.exception;

/**
 * 验证码异常
 * @author MyPC
 */
public class CaptchaException extends Exception {

	private static final long serialVersionUID = 8418679645900461144L;

	public CaptchaException(String message) {
		super(message);
	}
}
