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


    Pipeline white;
    Pipeline black;
    List<Pipeline> observers = new ArrayList<>();
    String whiteUserName;
    String blackUserName;
    ChessGame game;
    String gameName;

    public GameManager(int gameID, UserService userService, String authToken) throws DataAccessException{
        GameData game = getGame(gameID, userService, authToken);
        this.whiteUserName = game.whiteUsername();
        this.blackUserName = game.blackUsername();
        this.gameName = game.gameName();
        this.game = game.game();
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
                addWhite(session, userName);
            } else if (userName.equals(blackUserName)) {
                addBlack(session, userName);
            } else {
                addObserver(session, userName);
            }
        }
    }

    public List<Pipeline> getSessions(){
        //------------ approved
        List<Pipeline> sessions = new ArrayList<>();
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

    private void addWhite(Session white, String userName){
        Pipeline pipe = new Pipeline(white, userName);
        this.white = pipe;
    }

    private void addBlack(Session black, String userName){
        Pipeline pipe = new Pipeline(black, userName);
        this.black = pipe;
    }

    private void addObserver(Session observer, String userName){
        Pipeline pipe = new Pipeline(observer, userName);
        this.observers.add(pipe);
    }

    public void removeSession(Session session, String userName){
        if (userName.equals(whiteUserName)) {
            removeWhite();
        } else if (userName.equals(blackUserName)) {
            removeBlack();
        } else {
            removeObserver(session, userName);
        }
    }

    private void removeWhite(){
        this.white = null;
    }

    private void removeBlack(){
        this.black = null;
    }

    private void removeObserver(Session observer, String userName){
        Pipeline current = null;
        for (Pipeline user: this.observers){
            if (user.getUserName().equals(userName)){
                current = user;
            }
        }
        this.observers.remove(current);

    }

}
