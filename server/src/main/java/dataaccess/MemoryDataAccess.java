package dataaccess;

import chess.ChessGame;
import model.*;
import service.AlreadyTakenException;
import service.UnauthorizedException;

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

    public AuthData getAuth(String authToken) throws UnauthorizedException{
        if (authToken == null){
            throw new UnauthorizedException("Error: unauthorized");
        }
        return auths.get(authToken);
    }

    public void deleteAuth(String authToken) {
        auths.remove(authToken);
    }

    public GameList listGames(){
        return new GameList(games.values());
    }

    public int createGame(String gameName){
        ChessGame newGame = new ChessGame();
        gameID += 1;
        GameData newGameData = new GameData(gameID, null, null, gameName, newGame);
        games.put(gameID, newGameData);
        return gameID;
    }

    public GameData getGame(int gameID){
       return games.get(gameID);
    }



    public GameData updateGame(String username, int gameID, ChessGame.TeamColor playerColor, String whiteUsername,
                               String blackUsername, String gameName, ChessGame game) throws AlreadyTakenException{

        String[] usernames = updatePlayers(playerColor, username, whiteUsername, blackUsername);

        whiteUsername = usernames[0];
        blackUsername = usernames[1];

        GameData updatedGame = new GameData(gameID, whiteUsername, blackUsername, gameName, game);

        games.put(gameID, updatedGame);

        return updatedGame;


    }

    public void deleteAllGames(){
        games.clear();
        gameID = 0;
    }

    public void deleteAllUsers() {
        users.clear();
    }

    public void deleteAllAuths() {
        auths.clear();
    }
}
