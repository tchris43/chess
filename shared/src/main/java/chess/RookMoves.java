package chess;

import java.util.Collection;

public class RookMoves extends MoveCalculator{


    public RookMoves(ChessBoard board, ChessPosition position){
        super(board, position);
    }

    @Override
    public void calculateMoves() {
        moveOrthogonally();
    }


}

