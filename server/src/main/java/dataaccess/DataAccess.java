package dataaccess;

import chess.ChessGame;
import model.*;
import server.ServerException;
import service.AlreadyTakenException;
import service.UnauthorizedException;

import java.util.List;

public interface DataAccess {
    String createUser(String username, UserData userData) throws DataAccessException;
    void createAuth(String authToken, AuthData authData) throws DataAccessException;
    List<UserData> getUsers() throws DataAccessException;
    List<AuthData> getAuths() throws DataAccessException;
    UserData getUser(String username) throws DataAccessException;
    AuthData getAuth(String authToken) throws UnauthorizedException, DataAccessException;
    void deleteAuth(String authToken) throws DataAccessException;
    GameList listGames() throws DataAccessException;
    int createGame(String gameName) throws DataAccessException;
    GameData getGame(int gameID) throws DataAccessException;
    GameData updateGame(String username, int gameID, ChessGame.TeamColor playerColor, String whiteUsername,
                        String blackUsername, String gameName, ChessGame game) throws AlreadyTakenException, DataAccessException;
    void deleteAllGames() throws ServerException, DataAccessException;
    void deleteAllUsers() throws ServerException, DataAccessException;
    void deleteAllAuths() throws ServerException, DataAccessException;

}
