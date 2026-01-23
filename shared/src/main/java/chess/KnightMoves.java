package chess;



public class KnightMoves extends MoveCalculator{


    public KnightMoves(ChessBoard board, ChessPosition position){
        super(board, position);
    }

    @Override
    public void calculateMoves() {
        int row = position.getRow();
        int col = position.getColumn();
        checkAdd(row, col, row-2, col+1);
        checkAdd(row, col, row-1, col+2);
        checkAdd(row, col, row+1, col+2);
        checkAdd(row, col, row+2, col+1);
        checkAdd(row, col, row+2, col-1);
        checkAdd(row, col, row+1, col-2);
        checkAdd(row, col, row-1, col-2);
        checkAdd(row, col, row-2, col-1);
    }

}
