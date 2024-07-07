package com.sangs.lib.support.exception;

/**
 * 메시지처리 Exception 
 * <br>- 에러 메시지를 넣어서 Exception을 발생시키면 json 데이터 통신시 화면상에 에러 메시지를 Exception 발생 에러 메시지를 보여줍니다. 
 * <br>- RuntimeException를 상속 받은 Exception입니다.
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
public class SangsMessageException extends RuntimeException {
 
	private static final long serialVersionUID = -8409380411572300202L;
	
	//private String messageCd;
	
	public SangsMessageException() {
		super();
	}

	public SangsMessageException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
	public SangsMessageException(String message, Throwable cause) {
		super(message, cause);
	}
	public SangsMessageException(Throwable cause) {
		super(cause);
	}
	
	public SangsMessageException(String messageNm) {
		super(messageNm);
	}
	
	/*
	 * 
	public SangsRuntimeException(String messageCd, String[] args) {
		super(convertMessage(messageCd, args));
		this.messageCd = messageCd;
	}
	
	
	private static String convertMessage(String messageCd) {
		return CommonMessageService.getMessage(messageCd);
	}
	private static String convertMessage(String messageCd, String[] args) {
		return CommonMessageService.getMessage(messageCd, args);
	}
	
	public String getMessageCd() {
		return this.messageCd;
	}
	*/
	
}
