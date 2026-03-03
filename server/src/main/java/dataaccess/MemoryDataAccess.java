package dataaccess;

import model.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MemoryDataAccess implements DataAccess {
    private final HashMap<String, UserData> users = new HashMap<>();
    private final HashMap<String, AuthData> auths = new HashMap<>();

    public String createUser(String username, UserData registerRequest){
        users.put(registerRequest.username(), registerRequest);
        return registerRequest.username();
    }

    public void createAuth(String authToken, AuthData authRequest){
        auths.put(authRequest.authToken(), authRequest);
    }

    public List<UserData> getUsers() {
        return new UserList(users.values());
    }

    public List<AuthData> getAuths() {
        return new AuthList(auths.values());
    }

    public UserData getUser(String username){
        return users.get(username);
    }

    public AuthData getAuth(String authToken){
        return auths.get(authToken);
    }

    public void deleteAuth(String authToken) {
        auths.remove(authToken);
    }
}
