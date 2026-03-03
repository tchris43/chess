package dataaccess;

import model.AuthData;
import model.UserData;

import java.util.List;

public interface DataAccess {
    String createUser(UserData userData) throws DataAccessException;
    void createAuth(AuthData authData) throws DataAccessException;
    List<UserData> getUsers() throws DataAccessException;
    List<AuthData> getAuths() throws DataAccessException;
}
