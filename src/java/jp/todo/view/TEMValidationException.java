package jp.todo.view;

/**
 * フォームクラス，エンティティクラスの検証が失敗した時に投げられる例外クラス．
 *
 * @author fukuyasu
 */
public class TEMValidationException extends TEMViewException {

	private static final long serialVersionUID = 4214304664949192996L;

	public TEMValidationException (String message) {
		super(message);
	}

}
