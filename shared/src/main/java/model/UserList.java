package model;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Collection;

public class UserList extends ArrayList<UserData> {
    public UserList(){

    }

    public UserList(Collection<UserData> users){
        super(users);
    }

    public String toString (){
        return new Gson().toJson(this.toArray());
    }
}
