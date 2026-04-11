package websocket.messages;

import chess.ChessGame;

public class LoadGameMessage extends ServerMessage{

    ChessGame game;
    ChessGame.TeamColor playerColor;

    public LoadGameMessage(ChessGame game) {
        super(ServerMessageType.LOAD_GAME);
        this.game = game;
    }

    public void setPlayerColor(ChessGame.TeamColor playerColor) {
        this.playerColor = playerColor;
    }

    public ChessGame.TeamColor getPlayerColor(){
        return this.playerColor;
    }

    public ChessGame getGame(){
        return game;
    }

}
