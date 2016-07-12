package jp.todo.controller;

import com.google.gson.Gson;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.MongoClient;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.directwebremoting.WebContext;
import org.directwebremoting.WebContextFactory;

public class AccountController{
    public AccountController(){
    }
    public Account getAccount(){
        String sessionId = getSessionId();
        Gson gson = new Gson();
        try {
            // MongoDBサーバに接続
            MongoClient client = new MongoClient("localhost", 27017);
            // 利用するDBを取得
            DB db = client.getDB( "todosample" );
            // collention の指定
            DBCollection coll = db.getCollection("account");
            //検索
            BasicDBObject query = new BasicDBObject("sessionId", sessionId);
            DBCursor cursor = coll.find(query);
            if(cursor.size() == 1){
                return gson.fromJson(cursor.next().toString(), Account.class);
            }else{
                return null;
            }
        } catch (UnknownHostException ex) {
            Logger.getLogger(LoginController.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
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