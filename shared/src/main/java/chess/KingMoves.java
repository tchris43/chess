package chess;



public class KingMoves extends MoveCalculator{


    public KingMoves(ChessBoard board, ChessPosition position){
          super(board, position);
    }

    @Override
    public void calculateMoves() {
        int row = position.getRow();
        int col = position.getColumn();
        checkAdd(row, col, row, col+1);
        checkAdd(row, col, row+1, col+1);
        checkAdd(row, col, row+1, col);
        checkAdd(row, col, row+1, col-1);
        checkAdd(row, col, row, col-1);
        checkAdd(row, col, row-1, col-1);
        checkAdd(row, col, row-1, col);
        checkAdd(row, col, row-1, col+1);
    }

}
