package chess;



public class KingMoves extends MoveCalculator{


    public KingMoves(ChessBoard board, ChessPosition position){
          super(board, position);
    }

    @Override
    public void calculateMoves() {
        int X = position.getRow();
        int Y = position.getColumn();
        addMoves(X, Y+1);
        addMoves(X+1, Y+1);
        addMoves(X+1, Y);
        addMoves(X+1, Y-1);
        addMoves(X, Y-1);
        addMoves(X-1, Y-1);
        addMoves(X-1, Y);
        addMoves(X-1, Y+1);
    }

    @Override
    public void addMoves(int X, int Y) {
        super.addMoves(X, Y);
    }
}
