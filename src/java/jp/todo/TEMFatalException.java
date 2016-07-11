package jp.todo;

/**
 * アプリケーションでは復帰不可能な例外を表すクラス．
 *
 * @author fukuyasu
 */
public class TEMFatalException extends TEMException {
	private static final long serialVersionUID = -4332934532867614822L;

	public TEMFatalException(Throwable cause) {
		super(cause);
	}
}
