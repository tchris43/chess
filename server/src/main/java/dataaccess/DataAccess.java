package dataaccess;

import chess.ChessGame;
import model.*;

import java.util.List;

public interface DataAccess {
    String createUser(String username, UserData userData) throws DataAccessException;
    void createAuth(String authToken, AuthData authData) throws DataAccessException;
    List<UserData> getUsers() throws DataAccessException;
    List<AuthData> getAuths() throws DataAccessException;
    UserData getUser(String username) throws DataAccessException;
    AuthData getAuth(String username) throws DataAccessException;
    void deleteAuth(String authToken) throws DataAccessException;
    GameList listGames() throws DataAccessException;
    String createGame(String gameName) throws DataAccessException;
    GameData getGame(int gameID) throws DataAccessException;
    GameData updateGame(String username, int gameID, ChessGame.TeamColor playerColor, String whiteUsername,
                        String blackUsername, String gameName, ChessGame game) throws DataAccessException;
//    void deleteAllGames() throws DataAccessException;
//    void deleteAllUsers() throws DataAccessException;
//    void deleteAllAuths() throws DataAccessException;

}
