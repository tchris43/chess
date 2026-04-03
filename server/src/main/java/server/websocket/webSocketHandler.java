package server.websocket;

import com.google.gson.Gson;
import io.javalin.websocket.*;
import jakarta.websocket.Session;
import org.jetbrains.annotations.NotNull;
import websocket.commands.UserGameCommand;

import static websocket.commands.UserGameCommand.CommandType.*;

public class webSocketHandler implements WsConnectHandler, WsMessageHandler, WsCloseHandler {

    @Override
    public void handleConnect(WsConnectContext ctx{
        ctx.enableAutomaticPings();
        System.out.println("Websocket connected");
    }

    @Override
    public void handleMessage(WsMessageContext ctx) {
        try {
            UserGameCommand command = new Gson().fromJson(ctx.message(), UserGameCommand.class);
            //TODO: get userName
            switch (command.getCommandType()){
                case CONNECT -> connect(userName, ctx.session);
                case MAKE_MOVE -> makeMove(userName, ctx.session);
                case LEAVE -> leave(userName, ctx.session);
                case RESIGN -> resign(userName, ctx.session);
            }
        }
    }

    @Override
    public void handleClose(@NotNull WsCloseContext wsCloseContext) throws Exception {
        System.out.println("Websocket closed");
    }

    private void connect(String userName, Session session){
        
    }


}
