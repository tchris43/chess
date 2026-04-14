package client.websocket;

import chess.ChessMove;
import com.google.gson.Gson;
import jakarta.websocket.*;
import server.ResponseException;
import websocket.commands.MakeMoveCommand;
import websocket.commands.UserGameCommand;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class WebSocketFacade extends Endpoint {
    Session session;
    NotificationHandler notificationHandler;
    URI socketURI;

    public WebSocketFacade(String url, NotificationHandler notificationHandler) throws URISyntaxException,
            ResponseException, DeploymentException, IOException {

        url = url.replace("http", "ws");
        this.socketURI = new URI(url + "/ws");
        this.notificationHandler = notificationHandler;

        WebSocketContainer container = ContainerProvider.getWebSocketContainer();
        this.session = container.connectToServer(this, socketURI);

        this.session.addMessageHandler(new MessageHandler.Whole<String>() {
            @Override
            public void onMessage(String message){
                ServerMessage notification = new Gson().fromJson(message, ServerMessage.class);
                ServerMessage.ServerMessageType type = notification.getServerMessageType();
                switch(type){
                    case ServerMessage.ServerMessageType.LOAD_GAME : {
                        LoadGameMessage loadGame = new Gson().fromJson(message, LoadGameMessage.class);
                        notification = loadGame;
                        break;
                    }
                    case ServerMessage.ServerMessageType.NOTIFICATION : {
                        NotificationMessage notificationMessage = new Gson().fromJson(message, NotificationMessage.class);
                        notification = notificationMessage;
                        break;
                    }
                    case ServerMessage.ServerMessageType.ERROR: {
                        ErrorMessage errorMessage = new Gson().fromJson(message, ErrorMessage.class);
                        notification = errorMessage;
                        break;
                    }

                }
                notificationHandler.notify(notification);
            }
        });

    }

    public void setNotificationHandler(NotificationHandler handler){
        this.notificationHandler = handler;
    }

    public void connect(String authToken, int gameID) throws ResponseException {
        try {
            var command = new UserGameCommand(UserGameCommand.CommandType.CONNECT, authToken, gameID);
            this.session.getBasicRemote().sendText(new Gson().toJson(command));
        } catch (IOException ex) {
            throw new ResponseException(ex.getMessage());
        }
    }

    public void leave(String authToken, int gameID) throws ResponseException {
        try {
            var command = new UserGameCommand(UserGameCommand.CommandType.LEAVE, authToken, gameID);
            this.session.getBasicRemote().sendText(new Gson().toJson(command));
        } catch (IOException ex) {
            throw new ResponseException(ex.getMessage());
        }
    }

    public void makeMove(String authToken, int gameID, ChessMove move) throws ResponseException {
        try {
            var command = new MakeMoveCommand(UserGameCommand.CommandType.MAKE_MOVE, authToken, gameID, move);
            this.session.getBasicRemote().sendText(new Gson().toJson(command));
        } catch (IOException ex) {
            throw new ResponseException(ex.getMessage());
        }
    }

    public void resign(String authToken, int gameID) throws ResponseException {
        try {
            var command = new UserGameCommand(UserGameCommand.CommandType.RESIGN, authToken, gameID);
            this.session.getBasicRemote().sendText(new Gson().toJson(command));
        } catch (IOException ex) {
            throw new ResponseException(ex.getMessage());
        }
    }


    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig){
    }



}
