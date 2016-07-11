package jp.todo.util;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.apache.commons.beanutils.PropertyUtils;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBObject;
import com.mongodb.Mongo;
import com.mongodb.MongoException;

/**
 * MongoDBを利用するためのユーティリティクラス．
 * このクラスはSingletonとして利用する．
 *
 * @author shinsuke-m
 */
public class DBUtils {
    private static Mongo m;
    private static DB db;
    private static final String dbName = "tem";

    private static DBUtils singleton = new DBUtils();

    /**
     * シングルトンオブジェクトの取得．
     * @return シングルトンオブジェクト
     */
    public static DBUtils getInstance() {
        return singleton;
    }

    // invisible singleton constructor
    private DBUtils() {
        try {
            m = new Mongo("localhost", 27017);
            db = m.getDB(dbName);
        } catch (UnknownHostException | MongoException e) {
            // TODO 自動生成された catch ブロック
            // どうすんだろこれ
            e.printStackTrace();
        }
    }

    /**
     * シングルトンDBコネクションの取得
     * @return DBコネクションオブジェクト
     */
    public DB getDb() {
        return db;
    }

    /**
     * <p>
     * HTML特殊文字のサニタイズを行う．
     * ユーザが入力した文字列のうち，セキュリティ上危険な特殊タグ文字を全て置換する．
     * 基本的に String に対する setter の全てで呼び出すこと．<br>
     * ※ユーザが入力した & や <> などの特殊文字をそのまま利用すると，
     * DBへの命令を直接埋め込まれる危険性がある（XSS，SQLインジェクション）．
     * </p><p>
     * 利用例：
     * </p>
     * <pre>
     *   public void setMessage(String message) {
     *     this.message = DBUtils.getInstance().sanitize(message);
     *
     *     // 直接代入しないこと
     *     // this.message = message;
     *   }
     * </pre>
     *
     * @param str サニタイズ対象の文字列
     * @return サニタイズ済み文字列
     */
    public static String sanitize(String str) {
        if (str == null) {
            return "";
        }
        str = str.replaceAll("&" , "&amp;" );
        str = str.replaceAll("<" , "&lt;"  );
        str = str.replaceAll(">" , "&gt;"  );
        str = str.replaceAll("\"", "&quot;");
        str = str.replaceAll("'" , "&#39;" );
        return str;
    }

    /**
     * <p>
     * DBObject → Objectの変換を行う．
     * MongoDBから取得したDBObjectのプロパティを，対応するObjectの属性に移し替える．
     * </p><p>
     * 利用手順は以下の通り．
     * </p>
     * <ol>
     *   <li>DBObjectをMongoDBから取り出す．</li>
     *   <li>対応するObjectのインスタンス（空）を作る．</li>
     *   <li>本メソッドを呼び出す．</li>
     * </ol>
     *
     * <p>利用例：</p>
     * <pre>
     *   DBCollection coll = DBUtils.getInstance().getDb().getCollection("event");
     *   DBObject object = coll.findOne(query);
     *   Event event = new Event();
     *   DBUtils.attachProperties(event, object);
     * </pre>
     *
     * @param dest 変換先のObject
     * @param from 変換元のDBObject
     */
    @SuppressWarnings("unchecked")
    public static void attachProperties(Object dest, DBObject from){
        //		System.out.println("========================="+dest.getClass() + " | " + from.getClass());
        Field[] fields = dest.getClass().getDeclaredFields();
        for (Field field : fields) {
            // 変換対象のオブジェクトプロパティを取得
            //Object property = PropertyUtils.getProperty(dest, field.getName());
            Object property = from.get(field.getName());

            // ネストされたオブジェクトを探す
            if (property != null) {
                //				System.out.println("  " + property.getClass() + " " + field.getName());

                // 継承先を全て探索
                for (Class<?> c : property.getClass().getInterfaces()) {
                    //					System.out.println("     " + c);
                    // List を発見した場合  TODO:他のコレクションクラス
                    if (c == com.mongodb.DBObject.class || c == java.util.List.class) {
                        try {
                            // genericsのClassオブジェクトを取得
                            ParameterizedType ptype =
                                (ParameterizedType)dest.getClass().getDeclaredField(field.getName()).getGenericType();
                            Class<?> cls = (Class<?>)ptype.getActualTypeArguments()[0];

                            List<Object> list = new ArrayList<Object>();
                            // 全てのリストを変換
                            for (DBObject dbo : (List<DBObject>)from.get(field.getName())) {
                                Object o = cls.newInstance();
                                attachProperties(o, dbo);
                                list.add(o);
                            }

                            property = list;
                            // 他の継承クラスは無視
                            break;
                        } catch (IllegalAccessException | InstantiationException |
                                 NoSuchFieldException | SecurityException e) { 
                            continue;
                        }
                    }
                }
            }
            try {
                PropertyUtils.setProperty(dest, field.getName(), property);
            } catch (IllegalAccessException | InvocationTargetException
                     | NoSuchMethodException e) {
                continue; //TODO
            }
        }
    }

    /**
     * <p>
     * Object → DBObjectの変換を行う．
     * 作成したObjectのプロパティを，MongoDBのDBObjectに移し替える．
     * </p><p>
     * 利用手順は以下の通り．
     * </p>
     * <ol>
     *   <li>変換したいObjectを取得する．</li>
     *   <li>DBObject（空）を生成する．</li>
     *   <li>本メソッドを呼び出す．</li>
     * </ol>
     *
     * <p>利用例：</p>
     * <pre>
     *   Event event = getEvent(query);
     *   DBObject object = new BasicDBObject();
     *   DBUtils.convertToDBObject(object, event);
     * </pre>
     *
     * @param dest 変換先のDBObject
     * @param from 変換元のObject
     */
    @SuppressWarnings("unchecked")
    public static void convertToDBObject(DBObject dest, Object from){
        try {
            // from側の属性から探索する
            Field[] fields = from.getClass().getDeclaredFields();
            for (Field field : fields) {
                //TODO カバレッジツールが"$jaccoData"というフィールドを
                //カバレッジ計測のためにクラスに埋め込むので，回避するために実装を修正した．挙動的に問題がないか要確認
                if(field.getName().equals("$jacocoData")) continue;
                // destの同名オブジェクトプロパティを取得
                Object property = PropertyUtils.getProperty(from, field.getName());

                // ネストされたオブジェクトを探す
                if (property != null) {
                    // 継承先を全て探索
                    for (Class<?> c : property.getClass().getInterfaces()) {
                        // List を発見した場合
                        // TODO Map等の他のコレクションはどうするか
                        if (c == java.util.List.class) {
                            List<DBObject> list = new ArrayList<DBObject>();
                            for (Object p : (List<Object>)property) {
                                // 再帰呼び出しで変換
                                DBObject o = new BasicDBObject();
                                convertToDBObject(o, p);
                                list.add(o);
                            }
                            property = list;

                            // 他の継承クラスは無視
                            break;
                        }
                    }
                }
                dest.put(field.getName(), property);
            }
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            Logger logger = Logger.getLogger(DBUtils.class.getName());
            logger.warning("DBUtils.convertToDBObject: " + e.getMessage());
            //System.out.println("DBUtils.convertToDBObject: " + e.getMessage());

            // RuntimeExceptionをスロー
            throw new IllegalArgumentException();
        }
    }

    /**
     * {@link #attachProperties <code>attachProperties</code>}の機能に
     * 無視するプロパティを追加したもの．無視するプロパティは ignoreProperties 配列で指定する．
     * 
     * @param dest
     * @param from
     * @param ignoreProperties
     */
    public static void attachProperties2(Object dest, DBObject from, String[] ignoreProperties){
        try {
            for(String name: from.keySet()){
                // Mongo固有フィールドである _id は差し替えない
                if (name.equals("_id")) continue;
                for(String ignore:ignoreProperties){
                    if (name.equals(ignore)) continue;
                }
                PropertyUtils.setProperty(dest, name, from.get(name));
            }
        } catch (IllegalAccessException | InvocationTargetException
                 | NoSuchMethodException e) {
            Logger logger = Logger.getLogger(DBUtils.class.getName());
            logger.warning("DBUtils.attachProperties: " + e.getMessage());

            // RuntimeExceptionをスロー
            throw new IllegalArgumentException();
        }
    }

}
