package jp.todo.controller;

import java.util.ArrayList;
import java.util.List;

public class Account{
    String userId;
    String pass;
    String sessionId;
    List<String> projectNames = new ArrayList<String>();
 
    public String getUserId(){
        return this.userId;
    }
    public String getPass(){
        return this.pass;
    }
    public String getSessionId(){
        return this.sessionId;
    }
    public List<String> getprojectNames(){
        return this.projectNames;
    }
}