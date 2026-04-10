package server.websocket;

import chess.ChessGame;
import dataaccess.DataAccessException;
import model.GameData;
import model.GameList;
import org.eclipse.jetty.websocket.api.Session;
import service.UserService;

import java.util.ArrayList;
import java.util.List;

public class GameManager {
    Session white;
    Session black;
    List<Session> observers = new ArrayList<>();
    String whiteUserName;
    String blackUserName;
    ChessGame game;
    String gameName;

    public GameManager(int gameID, UserService userService, String authToken) throws DataAccessException{
        GameData game = getGame(gameID, userService, authToken);
        this.whiteUserName = game.whiteUsername();
        this.blackUserName = game.blackUsername();
        this.gameName = game.gameName();
    }

    public GameData getGame(int gameID, UserService userService, String authToken) throws DataAccessException {
        GameList games = userService.listGames(authToken);
        GameData game = null;
        for (GameData g : games){
            if (g.gameID() == gameID){
                game = g;
            }
        }
        if (game == null){
            throw new DataAccessException("Game does not exist");
        }
        return game;
    }


    public void addSession (Session session, String userName){
        //------- approved (connect) 6:54 wed
        if (userName != null) {
            if (userName.equals(whiteUserName)) {
                addWhite(session);
            } else if (userName.equals(blackUserName)) {
                addBlack(session);
            } else {
                addObserver(session);
            }
        }
    }

    public List<Session> getSessions(){
        //------------ approved
        List<Session> sessions = new ArrayList<>();
        if (white != null) {
            sessions.add(white);
        }
        if (black != null) {
            sessions.add(black);
        }
        if (observers != null) {
            sessions.addAll(observers);
        }
        return sessions;
    }

    public void setGame(ChessGame game){
        this.game = game;
    }

    private void addWhite(Session white){
        this.white = white;
    }

    private void addBlack(Session black){
        this.black = black;
    }

    private void addObserver(Session observer){
        this.observers.add(observer);
    }

    public void removeSession(Session session, String userName){
        if (userName.equals(whiteUserName)) {
            removeWhite();
        } else if (userName.equals(blackUserName)) {
            removeBlack();
        } else {
            removeObserver(session);
        }
    }

    private void removeWhite(){
        this.white = null;
    }

    private void removeBlack(){
        this.black = null;
    }

    private void removeObserver(Session observer){
        this.observers.remove(observer);
    }

}
