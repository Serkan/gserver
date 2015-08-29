package org.test.gserver;

/**
 * General graph exception.
 *
 * @author serkan
 */
public class GraphException extends Exception {

	public static final int ONGOING_TX_ERR = 1;

	public static final int NO_TX = 2;

	public static final int ZERO_STATE = 3;

	public static final int LAST_POS = 4;

	private final int errCode;

	public GraphException(String message, int errCode) {
		super(message);
		this.errCode = errCode;
	}

	public GraphException(String message, int errCode, Throwable cause) {
		super(message, cause);
		this.errCode = errCode;
	}

	public int getErrCode() {
		return errCode;
	}
}
