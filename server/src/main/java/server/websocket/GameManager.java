package server.websocket;

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
    List<Session> observers;
    String whiteUserName;
    String blackUserName;

    public GameManager(int gameID, UserService userService, String authToken) throws DataAccessException{
        GameData game = getGame(gameID, userService, authToken);
        this.whiteUserName = game.whiteUsername();
        this.blackUserName = game.blackUsername();
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
        if (userName.equals(whiteUserName)){
            addWhite(session);
        }
        else if (userName.equals(blackUserName)){
            addBlack(session);
        }
        else {
            addObserver(session);
        }
    }

    public List<Session> getSessions(){
        List<Session> sessions = new ArrayList<>();
        sessions.add(white);
        sessions.add(black);
        sessions.addAll(observers);
        return sessions;
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

}
