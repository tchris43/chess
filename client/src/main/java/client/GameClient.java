package client;

import chess.*;
import client.websocket.NotificationHandler;
import client.websocket.WebSocketFacade;
import jakarta.websocket.DeploymentException;
import model.*;
import server.ResponseException;
import server.ServerFacade;
import ui.DrawBoard;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.*;

public class GameClient implements NotificationHandler {
    private final ServerFacade server;
    private WebSocketFacade ws;
    private int gameID;
    private ChessBoard board;
    private ChessGame game;
    private ChessGame.TeamColor playerColor;

    private static DrawBoard drawBoard = new DrawBoard();

    public GameClient(ServerFacade serverFacade, int gameID, JoinRequest joinRequest) throws ResponseException, URISyntaxException, DeploymentException, IOException {
        server = serverFacade;

        server.joinGame(joinRequest);
        this.ws = new WebSocketFacade(server.getUrl(), this);
        ws.connect(server.getAuth(), gameID);

        this.gameID = gameID;
        this.ws.setNotificationHandler(this);
        this.playerColor = joinRequest.playerColor();

    }




    public String eval(String input) throws ResponseException{
        String[] tokens = input.toLowerCase().split(" ");
        String cmd = (tokens.length > 0) ? tokens[0] : "help";
        String[] params = Arrays.copyOfRange(tokens, 1, tokens.length);
        return switch (cmd){
            case "help" -> help();
            case "redraw" -> redrawChessBoard();
            case "leave" -> leave();
            case "move" -> makeMove(params[0], params[1], params[2], params[3]);
            case "resign" -> resign();
            case "highlight" -> highlightLegalMoves(params[0], params[1]);
            default -> help();
        };
    }

    public String help() {
        return """
                help
                redraw
                leave
                move <column> <row> <column> <row>
                resign
                highlight <col> <row>
                """;
    }

    public String leave() throws ResponseException {
        ws.leave(server.getAuth(), gameID);
        return "quit";
    }

    public int convert(String col){
        return switch(col){
            case "a" -> 1;
            case "b" -> 2;
            case "c" -> 3;
            case "d" -> 4;
            case "e" -> 5;
            case "f" -> 6;
            case "g" -> 7;
            case "h" -> 8;
            default -> throw new IllegalStateException("Unexpected value: " + col);
        };
    }

    public ChessPiece.PieceType getPromotion(ChessPosition start, ChessPosition end){
        if (board.getPiece(start).getPieceType().equals(ChessPiece.PieceType.PAWN) && (end.getRow() == 8 || end.getRow() == 1)){
            Scanner scanner = new Scanner(System.in);
            System.out.print("choose promotion <queen> <rook> <bishop> <knight>");
            String promotion = scanner.nextLine();
            return switch(promotion){
                case "queen" -> ChessPiece.PieceType.QUEEN;
                case "rook" -> ChessPiece.PieceType.ROOK;
                case "bishop" -> ChessPiece.PieceType.BISHOP;
                case "knight" -> ChessPiece.PieceType.KNIGHT;
                default -> throw new IllegalStateException("Unexpected value: " + promotion);
            };
        }
        else {
            return null;
        }
    }

    public String makeMove(String startCol, String startRow, String endCol, String endRow) throws ResponseException {
        int convertedCol = convert(startCol);
        int convertedEndCol = convert(endCol);


        ChessPosition start = new ChessPosition(Integer.parseInt(startRow), convertedCol);
        ChessPosition end = new ChessPosition(Integer.parseInt(endRow), convertedEndCol);
        ChessPiece.PieceType promotion = getPromotion(start, end);
        ChessMove move = new ChessMove(start, end, promotion);
        ws.makeMove(server.getAuth(), gameID, move);
        return "made move";
    }

    public String resign() throws ResponseException {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Do you really want to resign? <yes/no>");
        String confirm = scanner.nextLine();
        if (Objects.equals(confirm, "yes")) {
            ws.resign(server.getAuth(), gameID);
            return "resigned";
        }
        else {
            return "keep playing";
        }
    }

    public Collection<ChessMove> getValidSpots(ChessPosition position){
        return game.validMoves(position);
    }

    public String highlightLegalMoves(String col, String row) throws ResponseException {
        int convertedCol = convert(col);
        ChessPosition position = new ChessPosition(Integer.parseInt(row), convertedCol);
        var validSpots = getValidSpots(position);
        drawBoard.printBoard(board, playerColor, validSpots);
        return "highlight legal moves";
    }

    public String redrawChessBoard() {
        drawBoard.printBoard(board, playerColor, null);
        return "(redrew chess board)";
    }


    @Override
    public void notify(ServerMessage message) {
        switch(message.getServerMessageType()){
            case ServerMessage.ServerMessageType.LOAD_GAME : {
                LoadGameMessage loadGame = (LoadGameMessage) message;
                ChessGame chessGame = loadGame.getGame();
                this.board = chessGame.getBoard();
                this.game = chessGame;
                drawBoard.printBoard(board, playerColor, null);
            }
            case ServerMessage.ServerMessageType.NOTIFICATION : {
                NotificationMessage notification = (NotificationMessage) message;
                System.out.print(notification);
            }
            case ServerMessage.ServerMessageType.ERROR : {
                ErrorMessage errorMessage = (ErrorMessage) message;
                System.out.print(errorMessage);
            }
        }


    }

//    @Override
//    public void notify(LoadGameMessage message) {
//        ServerMessage serverMessage = (ServerMessage) message;
//        LoadGameMessage loadGame = (LoadGameMessage) message;
//        ChessBoard chessBoard = loadGame.getGame().getBoard();
//        receiveBoard(chessBoard, loadGame.getGame(), loadGame.getPlayerColor());
//    }




}
