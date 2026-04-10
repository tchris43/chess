package client;

import chess.ChessGame;
import client.websocket.NotificationHandler;
import client.websocket.WebSocketFacade;
import jakarta.websocket.DeploymentException;
import model.*;
import server.ResponseException;
import server.ServerFacade;
import ui.DrawBoard;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class GameClient implements NotificationHandler {
    private final ServerFacade server;
    private WebSocketFacade ws;

    public GameClient(ServerFacade serverFacade) throws ResponseException, URISyntaxException {
        server = serverFacade;
    }



    public String eval(String input) throws ResponseException{
        String cmd = input.toLowerCase();
        return switch (cmd){
            case "help" -> help();
//                case "redraw chess board" -> redrawChessBoard();
//                case "leave" -> leave();
//                case "make move" -> makeMove();
//                case "resign" -> resign();
//                case "highlight legal moves" -> highlightLegalMoves();
            default -> help();
        };
    }

    public String help() {
        return """
                redraw chess board
                leave
                make move
                resign
                highlight legal moves
                """;
    }

    @Override
    public void notify(ServerMessage message) {
        System.out.println(String.format("MADE IT!:%s",message.toString()));
    }
}
