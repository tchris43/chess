package chess;



public class PawnMoves extends MoveCalculator{


    public PawnMoves(ChessBoard board, ChessPosition position){
        super(board, position);
    }

    @Override
    public void calculateMoves() {
        int row = position.getRow();
        int col = position.getColumn();
        ChessPiece piece = board.board[row-1][col-1];
        if (piece.getTeamColor() == ChessGame.TeamColor.WHITE){
            checkAdd(row,col,row+1,col);
            if (row == 7){
                checkAdd(row,col,row+2,col);
            }
        }
        else if (piece.getTeamColor() == ChessGame.TeamColor.BLACK){
            checkAdd(row,col,row-1,col);
            if (row == 2){
                checkAdd(row,col,row-2,col);
            }
        }
    }

    @Override
    public void checkAdd(int row, int col, int newRow, int newCol){
        //check in  bounds
        if (newRow < 9 && newRow > 0 && newCol < 9 && newCol > 0) {
            ChessPiece piece = board.board[row - 1][col - 1];
            ChessPiece enemy = board.board[newRow - 1][newCol - 1];
            if (!isImpeded(enemy, piece)) {
                addMoves(newRow, newCol);
            }
        }
    }


}
