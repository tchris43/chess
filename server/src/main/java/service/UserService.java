package service;

import chess.ChessGame;
import dataaccess.DataAccess;
import dataaccess.DataAccessException;
import model.*;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

public class UserService {

    private final DataAccess dataAccess;

    public UserService(DataAccess dataAccess){
        this.dataAccess = dataAccess;
    }

    public boolean isAuthorized(String authToken) throws DataAccessException{
        AuthData authData = dataAccess.getAuth(authToken);
        return authData != null;
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
        if (isAuthorized(authToken)) {
            dataAccess.deleteAuth(authToken);
        }
        else {
            throw new DataAccessException("Expired AuthToken");
        }
    }

    public GameList listGames(String authToken) throws DataAccessException {
        if (isAuthorized(authToken)){
            return dataAccess.listGames();
        }
        else {
            throw new DataAccessException("Expired AuthToken");
        }
    }

    public GameResult createGame(String authToken, String gameName) throws DataAccessException {
        if (isAuthorized(authToken)){
            dataAccess.createGame(gameName);
            return new GameResult(gameName);
        }
        else {
            throw new DataAccessException("Expired Auth Token");
        }

    }

    public String joinGame(String authToken, ChessGame.TeamColor playerColor, int gameID) throws DataAccessException {
        try {
            if (isAuthorized(authToken)){
                GameData game = dataAccess.getGame(gameID);
                AuthData authData = dataAccess.getAuth(authToken);
                String username = authData.username();
                if (game != null){
                    GameData updatedGame = dataAccess.updateGame(username, gameID, playerColor, game.whiteUsername(), game.blackUsername(),
                            game.gameName(), game.game());
                    return updatedGame.gameName();
                }
                else {
                    throw new DataAccessException("Game does not exist");
                }

            }
            else {
                throw new DataAccessException("Expired Auth Token");
            }
        }
        catch (DataAccessException e){
            throw new DataAccessException(e.getMessage());
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
