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
        addMoves(row+1, col+2);
        addMoves(row+2, col+2);
        addMoves(row+2, col+1);
        addMoves(row+2, col);
        addMoves(row+1, col);
        addMoves(row, col);
        addMoves(row, col+1);
        addMoves(row, col+2);


    }


}
