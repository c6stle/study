package com.sangs.lib.support.exception;

/**
 * 접근권한이 없을때 발생시키는 Exception
 * <br>- 해당 Exception 을 발생시키면 권한 없음 페이지로 forward 시킵니다.
 * 
 * @author id.yoon
 * @since 2022.05.02
 * @version 1.0
 * @see
 *
 * <pre>
 * &lt;&lt; 개정이력(Modification Information) &gt;&gt;
 *   수정일               수정자              수정내용
 *  -------       --------    ---------------------------
 *  2022.05.02    id.yoon     최초 생성
 * </pre>
 */
public class SangsNoneAuthException extends RuntimeException {
 
	private static final long serialVersionUID = -8409380411572300202L;
	
	public SangsNoneAuthException() {
		super("접근권한이 없습니다.");
	}

	public SangsNoneAuthException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
	public SangsNoneAuthException(String message, Throwable cause) {
		super(message, cause);
	}
	public SangsNoneAuthException(Throwable cause) {
		super(cause);
	}
	
	public SangsNoneAuthException(String messageNm) {
		super(messageNm);
	}
	
 
	
}
