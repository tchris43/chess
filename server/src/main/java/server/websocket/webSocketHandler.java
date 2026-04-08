package server.websocket;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import io.javalin.websocket.*;



import model.AuthData;
import model.UserData;
import org.eclipse.jetty.websocket.api.Session;
import org.jetbrains.annotations.NotNull;
import service.UserService;
import websocket.commands.UserGameCommand;
import websocket.messages.ServerMessage;

import javax.xml.crypto.Data;
import java.io.IOException;
import java.util.List;
import java.util.Objects;

import static websocket.commands.UserGameCommand.CommandType.*;

public class webSocketHandler implements WsConnectHandler, WsMessageHandler, WsCloseHandler {

    private final ConnectionManager connections = new ConnectionManager();
    private final UserService userService;

    public webSocketHandler(UserService userService){
        this.userService = userService;
    }

    @Override
    public void handleConnect(WsConnectContext ctx) {
        ctx.enableAutomaticPings();
        System.out.println("Websocket connected");
    }

    @Override
    public void handleMessage(WsMessageContext ctx) throws DataAccessException {
        try {
            UserGameCommand command = new Gson().fromJson(ctx.message(), UserGameCommand.class);
            String userName = null;
            List<AuthData> auths = userService.getAuths();
            for (AuthData auth : auths){
                if (Objects.equals(auth.authToken(), command.getAuthToken())){
                    userName = auth.username();
                }
            }
            // ---------- Verified up to here (connect) 1:27 Wed
            //Throw an exception if userName is not found
            if (userName == null){
                throw new DataAccessException("Unauthorized");
            }

            switch (command.getCommandType()){
                case CONNECT -> connect(command.getGameID(), userName, ctx.session, command.getAuthToken());
//                case MAKE_MOVE -> makeMove(userName, ctx.session);
//                case LEAVE -> leave(userName, ctx.session);
//                case RESIGN -> resign(userName, ctx.session);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void handleClose(@NotNull WsCloseContext wsCloseContext) throws Exception {
        System.out.println("Websocket closed");
    }

    private void connect(int gameID, String userName, Session session, String authToken) throws IOException{
        System.out.println("CONNECT: connecting to server through websocket");
        //----------verified up to here 1:29 tuesday
        connections.add(gameID, session, userService, userName, authToken);
        //TODO look at server messages to create the subclasses with messages
        var message = String.format("%s has joined the game", userName);
        var notification = new ServerMessage(ServerMessage.ServerMessageType.LOAD_GAME);
        connections.broadcast(gameID, session, notification);
    }


}
