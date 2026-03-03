package service;

import dataaccess.DataAccess;
import dataaccess.DataAccessException;
import model.AuthData;
import model.UserData;
import model.LoginResult;

import java.util.List;
import java.util.UUID;

public class UserService {

    private final DataAccess dataAccess;

    public UserService(DataAccess dataAccess){
        this.dataAccess = dataAccess;
    }

    public static String generateToken(){
        return UUID.randomUUID().toString();
    }

    public LoginResult register(UserData registerRequest) throws DataAccessException {
        String username = dataAccess.createUser(registerRequest);
        String authToken = generateToken();
        dataAccess.createAuth(new AuthData(authToken, username));
        return new LoginResult(username, authToken);
    }

    public List<UserData> getUsers() throws DataAccessException{
        return dataAccess.getUsers();
    }

    public List<AuthData> getAuths() throws DataAccessException{
        return dataAccess.getAuths();
    }

}
