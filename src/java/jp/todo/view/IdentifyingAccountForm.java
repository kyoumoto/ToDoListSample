package jp.todo.view;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;

/**
 * ログイン及び，アカウント登録時に利用されるエンティティクラス．
 *
 * @author shinsuke-m
 */
public class IdentifyingAccountForm extends ValidatedBean{
    @Length(min=4, max=12, message="ユーザIDには4から12文字の英数字のみが利用できます")
    @NotEmpty(message="ユーザIDには4から12文字の英数字のみが利用できます")
    private String user_id;

    /**
     * パスワードを表すフィールド．
     * チェック項目は以下の通り．
     * <ul>
     *   <li>長さが4文字以上，12文字以下である．</li>
     *   <li>空文字ではない．</li>
     *   <li>英数字のみが利用されている．</li>
     * </ul>
     */
    @Length(min=4, max=12, message="パスワードには4から12文字の英数字のみが利用できます")
    @NotEmpty(message="パスワードには4から12文字の英数字のみが利用できます")
    private String password;

    /**
     * パスワードを取得する．
     * @return パスワード
     */
    public String getPass(){
        return password;
    }

    /**
     * ユーザIDを取得する．
     * @return ユーザID
     */
    public String getUserId(){
        return user_id;
    }
}
