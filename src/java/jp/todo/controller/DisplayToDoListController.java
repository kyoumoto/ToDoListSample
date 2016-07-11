package jp.todo.controller;

import com.google.gson.Gson;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.MongoClient;
import java.net.UnknownHostException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DisplayToDoListController{
    public DisplayToDoListController(){
    }
    
    public List<ToDo> execute(String form){
        List<ToDo> todo = new ArrayList<ToDo>();
        Gson gson = new Gson();
        ToDo data;  //検索するアカウントのデータ.
        data = gson.fromJson(form, ToDo.class);
        try {
            // MongoDBサーバに接続
            MongoClient client = new MongoClient("localhost", 27017);
            // 利用するDBを取得
            DB db = client.getDB( "todo" );
            // collention の指定
            DBCollection coll = db.getCollection("ticket");
            // 検索
            BasicDBObject query = new BasicDBObject();
            // 担当者などで, 内容を絞る必要あり.
            DBCursor cursor = coll.find(query);
            while(cursor.hasNext()){
                ToDo t = new ToDo();
                t = gson.fromJson(cursor.next().toString(), ToDo.class);
                
                todo.add(t);
            }
            /*
            ToDo t = new ToDo();
            t.deadline="deadline";
            t.responsibl = "resposible";
            t.state = "state";
            t.title="title";
            */

            return todo;
        } catch (UnknownHostException ex) {
            Logger.getLogger(LoginController.class.getName()).log(Level.SEVERE, null, ex);
        }

        return todo;
    }
}
