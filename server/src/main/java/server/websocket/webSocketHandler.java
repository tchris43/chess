package server.websocket;

import com.google.gson.Gson;
import io.javalin.websocket.*;


import jakarta.websocket.Session;
import org.jetbrains.annotations.NotNull;
import websocket.commands.UserGameCommand;

import java.io.IOException;

import static websocket.commands.UserGameCommand.CommandType.*;

public class webSocketHandler implements WsConnectHandler, WsMessageHandler, WsCloseHandler {

    private final ConnectionManager connections = new ConnectionManager();

    @Override
    public void handleConnect(WsConnectContext ctx{
        ctx.enableAutomaticPings();
        System.out.println("Websocket connected");
    }

    @Override
    public void handleMessage(WsMessageContext ctx) {
        try {
            UserGameCommand command = new Gson().fromJson(ctx.message(), UserGameCommand.class);
            switch (command.getCommandType()){
                case CONNECT -> connect(command.getAuthToken(), ctx.session);
                case MAKE_MOVE -> makeMove(command.getAuthToken(), ctx.session);
                case LEAVE -> leave(command.getAuthToken(), ctx.session);
                case RESIGN -> resign(command.getAuthToken(), ctx.session);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void handleClose(@NotNull WsCloseContext wsCloseContext) throws Exception {
        System.out.println("Websocket closed");
    }

    private void connect(String userName, Session session){
        connections.add(session);
    }


}
