package jp.todo.controller;

import java.util.logging.Level;
import java.util.logging.Logger;
import jp.todo.view.TEMValidationException;
import com.google.gson.Gson;
import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoException;
import java.io.Console;
import java.net.UnknownHostException;
import jp.todo.Session;
import jp.todo.TEMFatalException;
import org.directwebremoting.WebContext;
import org.directwebremoting.WebContextFactory;
import jp.todo.controller.Account;

public class LoginController
{
    public LoginController(){        
    }
    public boolean execute(String form){
        Gson gson = new Gson();
        Account account;
        account = gson.fromJson(form, Account.class);
        Session session = new Session();
        
        try {
            // MongoDBサーバに接続
            MongoClient client = new MongoClient("localhost", 27017);
            // 利用するDBを取得
            DB db = client.getDB( "todosample" );
            // collention の指定
            DBCollection coll = db.getCollection("account");
            // 検索
            BasicDBObject query = new BasicDBObject("userId", account.userId)
                                        .append("pass", account.pass);
            DBCursor cursor = coll.find(query);
            if(cursor.size() == 1){
                String sessionId = getSessionId();
                // sessionId 削除
                query = new BasicDBObject();
                BasicDBObject update = new BasicDBObject();
                //query.put("userId","user1");
                update.put("sessionId", "");
                coll.updateMulti(query, new BasicDBObject("$set",update));
                
                // DB登録
                query = new BasicDBObject();
                update = new BasicDBObject();
                query.put("userId", account.userId);
                update.put("sessionId", sessionId);
                coll.update(query, new BasicDBObject("$set",update));
                return true;
            }else{
                return false;
            }
        } catch (UnknownHostException ex) {
            Logger.getLogger(LoginController.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }
    
    private String getSessionId() {
        WebContext ctx = WebContextFactory.get();

        // ローカルテスト時
        if (ctx == null) {
            return "THIS_IS_A_TEST_SESSION_ID";
        }

        return ctx.getScriptSession().getId().split("/")[0];
    }

}

