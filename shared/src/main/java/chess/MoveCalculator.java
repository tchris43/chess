package chess;

import java.util.Collection;

public class MoveCalculator {

    ChessBoard board;
    ChessPosition position;
    Collection<ChessMove> possibleMoves;

    public MoveCalculator(ChessBoard currentBoard, ChessPosition currentPosition){
        board = currentBoard;
        position = currentPosition;
        possibleMoves = null;
    }

    public void calculateMoves(){}

    public void addMoves(int row, int col){
        ChessPosition newPosition = new ChessPosition(row, col);
        ChessMove move = new ChessMove(position, newPosition, null);
        possibleMoves.add(move);
    }



}
