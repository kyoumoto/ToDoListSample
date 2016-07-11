package jp.todo.view;

import java.util.Iterator;
import java.util.Set;
import java.util.logging.Logger;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

/**
 * HibernateValidatorを用いて検証を行うクラス．
 *
 * @author shinsuke-m
 */
public class ValidatedBean {

    /**
     * フィールドの検証を行うメソッド．
     * 具体的な検証内容は各フィールドのアノテーションを参照すること．
     * このメソッドは以下の処理が行われる．
     * <ol>
     *   <li>どのクラスが検証を用いたのか，クラス名のログを取る．</li>
     *   <li>HibernateValidatorを利用して検証を行う．</li>
     *   <li>制約違反が発生した場合は，{@link TEMValidationException <code>TEMValidationException</code>}を投げる．</li>
     *   <li>制約違反がなかった場合，trueを返す．</li>
     * </ol>
     */
    public boolean validate() throws TEMValidationException{
        String className = getClass().getName();
        int index = className.lastIndexOf(".");
        className = className.substring(index + 1);
        Logger logger = Logger.getLogger(getClass().getName());
        logger.info(className + ".validate");

        try{
            return ValidatedBean.<ValidatedBean>validate(this);
        } catch(TEMValidationException e){
            logger.warning(className + ".validate: " + e.getMessage());
            throw e;
        }
    }

    /**
     * HibernateValidatorを利用して検証を行うメソッド．
     */
    private static <T extends ValidatedBean> boolean validate(T bean) throws TEMValidationException {
        // Hibernate Validator (HB) のvalidatorインスタンス生成
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();

        // validation
        Set<ConstraintViolation<T>> constraintViolations = validator.validate(bean);

        // 制約違反発見
        if (constraintViolations.size() > 0) {
            Iterator<ConstraintViolation<T>> iterator = constraintViolations.iterator();

            // 各制約違反に対する処理
            while (iterator.hasNext()) {
                // 例外をメッセージ付きで投げる．
                throw new TEMValidationException(iterator.next().getMessage());
            }
        }
        return true;
    }
}
