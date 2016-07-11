package jp.todo.controller;

import java.util.ArrayList;
import java.util.List;

public class Project{
    String projectName;
    List<String> members = new ArrayList<String>();
    public Project(){
    }
    
    public String getProjectName(){
        return this.projectName;
    }
    
    public List<String> getMembers(){
        return members;
    }
}