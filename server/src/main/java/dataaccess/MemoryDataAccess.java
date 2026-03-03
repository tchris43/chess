package dataaccess;

import model.UserData;
import model.AuthData;

import java.util.ArrayList;
import java.util.List;

public class MemoryDataAccess implements DataAccess {
    private final List<UserData> users = new ArrayList<>();
    private final List<AuthData> auths = new ArrayList<>();

    public String createUser(UserData registerRequest){
        users.add(registerRequest);
        return registerRequest.username();
    }

    public void createAuth(AuthData authRequest){
        auths.add(authRequest);
    }

    public List<UserData> getUsers() {
        return users;
    }

    public List<AuthData> getAuths() {
        return auths;
    }
}
