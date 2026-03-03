package service;

import dataaccess.DataAccess;
import dataaccess.DataAccessException;
import model.AuthData;
import model.LoginRequest;
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
        //check the name is not taken
        if (dataAccess.getUser(registerRequest.username()) != null){
            throw new DataAccessException("Username already taken");
        }
        String username = dataAccess.createUser(registerRequest.username(), registerRequest);
        String authToken = generateToken();
        dataAccess.createAuth(authToken, new AuthData(authToken, username));
        return new LoginResult(username, authToken);
    }

    public LoginResult login(LoginRequest loginRequest) throws DataAccessException{
        UserData user = dataAccess.getUser(loginRequest.username());
        String authToken = generateToken();
        dataAccess.createAuth(authToken, new AuthData(authToken, loginRequest.username()));
        if (user != null) {
            AuthData authData = dataAccess.getAuth(authToken);
            return new LoginResult(user.username(), authData.authToken());
        }
        throw new DataAccessException("Invalid login attempt");
    }

    public void logout(String authToken) throws DataAccessException{
        AuthData authData = dataAccess.getAuth(authToken);
        if (authData != null) {
            dataAccess.deleteAuth(authToken);
        }
        else {
            throw new DataAccessException("Expired AuthToken");
        }
    }

//    public void clear() throws DataAccessException{
//        dataAccess.deleteAllGames();
//        dataAccess.deleteAllUsers();
//        dataAccess.deleteAllAuths();
//    }

    public List<UserData> getUsers() throws DataAccessException{
        return dataAccess.getUsers();
    }

    public List<AuthData> getAuths() throws DataAccessException{
        return dataAccess.getAuths();
    }

}
