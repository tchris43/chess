package server.websocket;



import jakarta.websocket.Session;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class ConnectionManager {
    public final ConcurrentHashMap<Integer, List<Session>> connections = new ConcurrentHashMap<>();

    public void add(int gameID, Session session) { connections.put(gameID, session);}

    public void remove(Session session) {connections.remove(session);}

    public void broadcast(int gameID, Session excludeSession, ServerMessage notification) throws IOException {
        String msg = notification.toString();
        for (Session s : connections.get(gameID)){
            if (s.isOpen()){
                if (!s.equals(excludeSession)) {
                    //TODO: verify this command and correct session dependency
                    s.getBasicRemote().sendText(msg);
                }
            }
        }

    }
}
