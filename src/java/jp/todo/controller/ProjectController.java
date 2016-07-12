package jp.todo.controller;

import com.google.gson.Gson;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.MongoClient;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ProjectController{
    public ProjectController(){
    }
    public List<Project> getProjectList(String userId){
        List<Project> projects = new ArrayList<Project>();
        Gson gson = new Gson();
        try {
            // MongoDBサーバに接続
            MongoClient client = new MongoClient("localhost", 27017);
            // 利用するDBを取得
            DB db = client.getDB( "todosample" );
            // collention の指定
            DBCollection coll = db.getCollection("project");
            // 検索
            BasicDBObject query = new BasicDBObject("members",userId);
            DBCursor cursor = coll.find(query);
            if(cursor.size() >= 1){
                while(cursor.hasNext()) {                    
                    Project p = new Project();
                    p = gson.fromJson(cursor.next().toString(), Project.class);
                    projects.add(p);
                }
                return projects;
            }
        } catch (UnknownHostException ex) {
            Logger.getLogger(LoginController.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
}