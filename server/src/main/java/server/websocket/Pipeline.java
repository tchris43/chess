package server.websocket;

import org.eclipse.jetty.websocket.api.Session;

public class Pipeline {
    private final String userName;
    private final Session session;
    public Pipeline(Session session, String userName){
        this.userName = userName;
        this.session = session;
    }

    public String getUserName(){
        return userName;
    }

    public Session getSession(){
        return session;
    }
}
