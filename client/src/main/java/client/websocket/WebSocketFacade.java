package client.websocket;

import com.google.gson.Gson;
import jakarta.websocket.*;
import server.ResponseException;
import websocket.commands.UserGameCommand;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class WebSocketFacade extends Endpoint {
    Session session;
    NotificationHandler notificationHandler;
    URI socketURI;

    public WebSocketFacade(String url, NotificationHandler notificationHandler) throws URISyntaxException, ResponseException, DeploymentException, IOException {

        url = url.replace("http", "ws");
        this.socketURI = new URI(url + "/ws");
        this.notificationHandler = notificationHandler;

        WebSocketContainer container = ContainerProvider.getWebSocketContainer();
        this.session = container.connectToServer(this, socketURI);

        this.session.addMessageHandler(new MessageHandler.Whole<String>() {
            @Override
            public void onMessage(String message){
                ServerMessage notification = new Gson().fromJson(message, ServerMessage.class);
                notificationHandler.notify(notification);
            }
        });

    }

    public void connect(String authToken, int gameID) throws ResponseException {
        try {
            var command = new UserGameCommand(UserGameCommand.CommandType.CONNECT, authToken, gameID);
            this.session.getBasicRemote().sendText(new Gson().toJson(command));
        } catch (IOException ex) {
            throw new ResponseException(ex.getMessage());
        }
    }

    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig){
    }



}
