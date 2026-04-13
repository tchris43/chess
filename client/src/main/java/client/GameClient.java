package client;

import chess.*;
import client.websocket.NotificationHandler;
import client.websocket.WebSocketFacade;
import jakarta.websocket.DeploymentException;
import model.*;
import server.ResponseException;
import server.ServerFacade;
import ui.DrawBoard;
import websocket.messages.LoadGameMessage;
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

    public GameClient(ServerFacade serverFacade, int gameID, JoinRequest joinRequest) throws ResponseException, URISyntaxException, DeploymentException, IOException {
        server = serverFacade;

        server.joinGame(joinRequest);
        this.ws = new WebSocketFacade(server.getUrl(), this);
        ws.connect(server.getAuth(), gameID);

        this.gameID = gameID;
        this.ws.setNotificationHandler(this);

    }

    public void setBoard(ChessBoard board){
        this.board = board;
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
        if (board.getPiece(start).getPieceType().equals(ChessPiece.PieceType.PAWN)){
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
        ws.resign(server.getAuth(), gameID);
        return "resigned";
    }

    public Collection<ChessMove> getValidSpots(ChessPosition position){
        return game.validMoves(position);
    }

    public String highlightLegalMoves(String col, String row) throws ResponseException {
        ChessPosition position = new ChessPosition(Integer.parseInt(row), Integer.parseInt(col));
        var drawBoard = new DrawBoard();
        var validSpots = getValidSpots(position);
        drawBoard.printBoard(board, game.getTeamTurn(), validSpots);
        return "highlight legal moves";
    }

    public String redrawChessBoard() {
        LoadGameMessage loadGame = new LoadGameMessage(game);
        notify(loadGame);
        return "(redrew chess board)";
    }

    private void receiveBoard(ChessBoard chessBoard, ChessGame chessGame, ChessGame.TeamColor playerColor){
        this.board = chessBoard;
        this.game = chessGame;
        var drawBoard = new DrawBoard();
        drawBoard.printBoard(board, playerColor, null);
    }

    @Override
    public void notify(LoadGameMessage message) {
        ChessBoard chessBoard = message.getGame().getBoard();
        receiveBoard(chessBoard, message.getGame(), message.getPlayerColor());
    }
}
