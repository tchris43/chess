package server.websocket;




import dataaccess.DataAccessException;
import org.eclipse.jetty.websocket.api.Session;
import service.UserService;
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

    //TODO figure out how to remove just one value in a ConcurrentHashMap
    public void remove(Session session) {connections.remove(session);}

    public void broadcast(int gameID, Session excludeSession, ServerMessage notification) throws IOException {
        //------------- approved for connect tests 8:24 wed
        String msg = notification.toString();
        GameManager game = connections.get(gameID);
        for (Session s : game.getSessions()){
            if (s.isOpen()){
                if (notification.getServerMessageType() == ServerMessage.ServerMessageType.NOTIFICATION) {
                    if (!s.equals(excludeSession)) {
                        s.getRemote().sendString(msg);
                    }
                }
                else if (notification.getServerMessageType() == ServerMessage.ServerMessageType.LOAD_GAME){
                    if (s.equals(excludeSession)) {
                        s.getRemote().sendString(msg);
                    }
                }
            }
        }

    }
}
