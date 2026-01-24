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
            checkCapture(row,col,row+1, col+1);
            checkCapture(row,col,row+1, col-1);

        }
        else if (piece.getTeamColor() == ChessGame.TeamColor.BLACK){
            checkAdd(row,col,row-1,col);
            if (row == 2){
                checkAdd(row,col,row-2,col);
            }
            checkCapture(row, col, row-1, col+1);
            checkCapture(row,col,row-1, col-1);
        }
    }

    public void checkCapture(int row, int col, int newRow, int newCol){
        if (newRow < 9 && newRow > 0 && newCol < 9 && newCol > 0) {
            ChessPiece piece = board.board[row - 1][col - 1];
            ChessPiece enemy = board.board[newRow - 1][newCol - 1];
            if (isImpeded(enemy,piece)){
                if (isEnemy(enemy, piece)) {
                    addMoves(newRow, newCol);
                }
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


    @Override
    public void addMoves(int Row, int Col){
        ChessPosition newPosition = new ChessPosition(Row, Col);
        if (Row == 1 || Row == 8){
            ChessMove queenMove = new ChessMove(position, newPosition, ChessPiece.PieceType.QUEEN);
            ChessMove rookMove = new ChessMove(position, newPosition, ChessPiece.PieceType.ROOK);
            ChessMove bishopMove = new ChessMove(position, newPosition, ChessPiece.PieceType.BISHOP);
            ChessMove knightMove = new ChessMove(position, newPosition, ChessPiece.PieceType.KNIGHT);

            possibleMoves.add(queenMove);
            possibleMoves.add(rookMove);
            possibleMoves.add(bishopMove);
            possibleMoves.add(knightMove);
        }
        else {
            ChessMove move = new ChessMove(position, newPosition, null);
            possibleMoves.add(move);
        }
    }


}
