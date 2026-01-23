package chess;

import java.util.Collection;

public class KingMoves extends MoveCalculator{

    ChessBoard board;
    ChessPosition position;
    Collection<ChessMove> possibleMoves;

    public KingMoves(ChessBoard board, ChessPosition position){
          super(board, position);
    }

    @Override
    public void calculateMoves() {
        int row = position.getRow();
        int col = position.getColumn();
        ChessPosition up = new ChessPosition(row+1, col+2);
        ChessPosition northEast = new ChessPosition(row+2, col+2);
        ChessPosition right = new ChessPosition(row+2, col+1);
        ChessPosition southEast = new ChessPosition(row+2, col);
        ChessPosition down = new ChessPosition(row+1, col);
        ChessPosition southWest = new ChessPosition(row, col);
        ChessPosition left = new ChessPosition(row, col+1);
        ChessPosition northWest = new ChessPosition(row, col+2);
        ChessMove moveUp = new ChessMove(position,up,null);
        ChessMove moveNorthEast = new ChessMove(position,northEast,null);

        possibleMoves.add(moveUp);

    }


}
