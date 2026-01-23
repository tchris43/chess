package chess;

import java.util.Collection;

public class BishopMoves extends MoveCalculator{


    public BishopMoves(ChessBoard board, ChessPosition position){
        super(board, position);
    }

    @Override
    public void calculateMoves() {
        moveDiagonally();
    }


}

