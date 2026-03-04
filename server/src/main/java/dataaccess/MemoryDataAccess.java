package dataaccess;

import chess.ChessGame;
import model.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

public class MemoryDataAccess implements DataAccess {
    private final HashMap<String, UserData> users = new HashMap<>();
    private final HashMap<String, AuthData> auths = new HashMap<>();
    private final HashMap<Integer, GameData> games = new HashMap<>();
    private int gameID = 0;

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

    public UserData getUser(String username) {
        return users.get(username);
    }

    public AuthData getAuth(String authToken){
        return auths.get(authToken);
    }

    public void deleteAuth(String authToken) {
        auths.remove(authToken);
    }

    public GameList listGames(){
        return new GameList(games.values());
    }

    public String createGame(String gameName){
        ChessGame newGame = new ChessGame();
        GameData newGameData = new GameData(gameID++, null, null, gameName, newGame);
        games.put(gameID++, newGameData);
        return gameName;
    }

    public GameData getGame(int gameID){
       return games.get(gameID);
    }

    public boolean notTaken(String username){
        return username == null;
    }

    public GameData updateGame(String username, int gameID, ChessGame.TeamColor playerColor, String whiteUsername,
                               String blackUsername, String gameName, ChessGame game) throws DataAccessException{
        if (playerColor == ChessGame.TeamColor.WHITE){
            if (notTaken(whiteUsername)) {
                whiteUsername = username;
            }
            else {
                throw new DataAccessException("Already Taken");
            }
        }
        else {
            if (notTaken(blackUsername)) {
                blackUsername = username;
            }
            else {
                throw new DataAccessException("Already Taken");
            }

        }

        GameData updatedGame = new GameData(gameID, whiteUsername, blackUsername, gameName, game);

        games.put(gameID, updatedGame);

        return updatedGame;


    }

    public void deleteAllGames(){
        games.clear();
    }

    public void deleteAllUsers() {
        users.clear();
    }

    public void deleteAllAuths() {
        auths.clear();
    }
}
