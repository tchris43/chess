package chess;


public class        QueenMoves extends MoveCalculator{


    public QueenMoves(ChessBoard board, ChessPosition position){
        super(board, position);
    }

    @Override
    public void calculateMoves() {
        moveOrthogonally();
        moveDiagonally();
    }


}

