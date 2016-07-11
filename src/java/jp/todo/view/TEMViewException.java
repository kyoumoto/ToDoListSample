package jp.todo.view;

import jp.todo.TEMException;

/**
 * 画面にエラーメッセージを返す際に用いる例外クラス．
 * 基本的に他の例外クラスをラップして用いる．
 *
 * @author fukuyasu
 */
public class TEMViewException extends TEMException {
	private static final long serialVersionUID = -3130558751477090056L;

	public TEMViewException (String message) {
		super(message);
	}
	public TEMViewException (String message, Throwable cause) {
		super(message, cause);
	}
}
