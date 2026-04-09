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
    public void remove(Session session) {connections.remove(session);}

    public void updateGame(DataAccess dataAccess, String userName, int gameID, ChessGame.TeamColor playerColor, ChessGame game) throws DataAccessException {
        GameManager gameManager = connections.get(gameID);
        String whiteUsername = gameManager.whiteUserName;
        String blackUserName = gameManager.blackUserName;
        String gameName = gameManager.gameName;
        dataAccess.updateJustGame(whiteUsername, blackUserName, gameName, game, gameID);
        gameManager.setGame(game);
    }

    public void broadcast(int gameID, Session excludeSession, ServerMessage notification) throws IOException {
        //------------- approved for connect tests 8:24 wed
        String msg = notification.toString();
        GameManager game = connections.get(gameID);
        if (notification.getServerMessageType() == ServerMessage.ServerMessageType.ERROR){
            excludeSession.getRemote().sendString(msg);
        }
        for (Session s : game.getSessions()){
            if (s.isOpen()){
                if (notification.getServerMessageType() == ServerMessage.ServerMessageType.NOTIFICATION) {
                    if (!s.equals(excludeSession)) {
                        s.getRemote().sendString(msg);
                    }
                }
                else if (notification.getServerMessageType() == ServerMessage.ServerMessageType.LOAD_GAME){
                    LoadGameMessage loadGame = (LoadGameMessage) notification;
                    if (s.equals(excludeSession)) {
                        s.getRemote().sendString(msg);
                    }
                    game.setGame(loadGame.getGame());
                }
            }
        }

    }
}
