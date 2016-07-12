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
import jp.todo.Session;

public class RegisterTicketController{
    public RegisterTicketController(){
    }
    
    public void registerTicket(String form){
        Gson gson = new Gson();
        ToDo todo;
        todo = gson.fromJson(form, ToDo.class);
        try {
            // MongoDBサーバに接続
            MongoClient client = new MongoClient("localhost", 27017);
            // 利用するDBを取得
            DB db = client.getDB( "todosample" );
            // collention の指定
            DBCollection coll = db.getCollection("ticket");
            // dco の作成
            BasicDBObject doc = new BasicDBObject("title",todo.title)
                                    .append("responsible", todo.responsible)
                                    .append("deadline", todo.deadline)
                                    .append("projectName",todo.projectName)
                                    .append("state", todo.state);
            coll.insert(doc);
        } catch (UnknownHostException ex) {
            Logger.getLogger(LoginController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}