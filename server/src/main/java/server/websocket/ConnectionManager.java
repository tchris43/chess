package server.websocket;




import chess.ChessGame;
import dataaccess.DataAccess;
import dataaccess.DataAccessException;
import dataaccess.MemoryDataAccess;
import org.eclipse.jetty.websocket.api.Session;
import service.UserService;
import websocket.messages.LoadGameMessage;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;


public class ConnectionManager {
    public final ConcurrentHashMap<Integer, GameManager> connections = new ConcurrentHashMap<>();
    String msg;
    GameManager game;
    Pipeline currentPipe;

    public void add(int gameID, Session session, UserService userService, String userName, String authToken) throws DataAccessException {
        GameManager game = connections.get(gameID);
        if (game == null){
            GameManager newGame = new GameManager(gameID, userService, authToken);
            connections.put(gameID, newGame);
            game = newGame;
        }

        game.addSession(session, userName);
    }

    public GameManager get(int gameID){
        return connections.get(gameID);
    }

    //TODO figure out how to remove just one value in a ConcurrentHashMap
    public void remove(Session session, int gameID, String userName) {
        GameManager game = connections.get(gameID);
        game.removeSession(session, userName);
    }

    public void updateGame(DataAccess dataAccess, String userName, int gameID, ChessGame.TeamColor playerColor, ChessGame game) throws DataAccessException {
        GameManager gameManager = connections.get(gameID);
        String whiteUsername = gameManager.whiteUserName;
        String blackUserName = gameManager.blackUserName;
        String gameName = gameManager.gameName;
        dataAccess.updateJustGame(whiteUsername, blackUserName, gameName, game, gameID);
    }

    public void updatePlayers(DataAccess dataAccess, String userName, int gameID, ChessGame.TeamColor playerColor, String whiteUsername, String blackUsername, ChessGame game) throws DataAccessException {
        String gameName = connections.get(gameID).gameName;
        dataAccess.updateGame(userName, gameID, playerColor, whiteUsername, blackUsername, gameName, game);
    }

    public void broadcast(int gameID, String excludeSession, ServerMessage notification, Session session) throws IOException {
        //------------- approved for connect tests 8:24 wed
        String msg = notification.toString();
        GameManager game = connections.get(gameID);
        Pipeline currentPipe = null;
        if (game != null){
            for (Pipeline p : game.getSessions()) {
                if (p.getUserName().equals(excludeSession)) {
                    currentPipe = p;
                }
            }
        }
        if (notification.getServerMessageType() == ServerMessage.ServerMessageType.ERROR) {
            session.getRemote().sendString(msg);
        }
        for (Pipeline s : game.getSessions()) {
            if (s.getSession().isOpen()) {
                if (notification.getServerMessageType() == ServerMessage.ServerMessageType.NOTIFICATION) {
                    if (!s.getUserName().equals(excludeSession)) {
                        s.getSession().getRemote().sendString(msg);
                    }
                } else if (notification.getServerMessageType() == ServerMessage.ServerMessageType.LOAD_GAME) {
                    LoadGameMessage loadGame = (LoadGameMessage) notification;
                    if (s.getUserName().equals(excludeSession)) {
                        s.getSession().getRemote().sendString(msg);
                    }
                    game.setGame(loadGame.getGame());
                }
            }
        }


    }

    public void setBroadcast(ServerMessage notification, int gameID, String excludeSession){
        this.msg = notification.toString();
        this.game = connections.get(gameID);
        this.currentPipe = null;
        for (Pipeline p : game.getSessions()){
            if (p.getUserName().equals(excludeSession)){
                currentPipe = p;
            }
        }
    }

    public void broadcastMove(int gameID, String excludeSession, ServerMessage notification, Session session) throws IOException {
        //------------- approved for connect tests 8:24 wed
        setBroadcast(notification, gameID, excludeSession);
        if (notification.getServerMessageType() == ServerMessage.ServerMessageType.ERROR){
            currentPipe.getSession().getRemote().sendString(msg);
        }
        for (Pipeline s : game.getSessions()){
            if (s.getSession().isOpen()){
                if (notification.getServerMessageType() == ServerMessage.ServerMessageType.NOTIFICATION) {
                    if (!s.getUserName().equals(excludeSession)) {
                        s.getSession().getRemote().sendString(msg);
                    }
                }
                else if (notification.getServerMessageType() == ServerMessage.ServerMessageType.LOAD_GAME){
                    LoadGameMessage loadGame = (LoadGameMessage) notification;
                    s.getSession().getRemote().sendString(msg);
                    game.setGame(loadGame.getGame());
                }
            }
        }

    }


    public void broadcastAll(int gameID, String excludeSession, ServerMessage notification) throws IOException {
        //------------- approved for connect tests 8:24 wed
        setBroadcast(notification, gameID, excludeSession);
        if (notification.getServerMessageType() == ServerMessage.ServerMessageType.ERROR){
            currentPipe.getSession().getRemote().sendString(msg);
        }
        for (Pipeline s : game.getSessions()){
            if (s.getSession().isOpen()){
                if (notification.getServerMessageType() == ServerMessage.ServerMessageType.NOTIFICATION) {
                    s.getSession().getRemote().sendString(msg);
                }
            }
        }

    }

}
