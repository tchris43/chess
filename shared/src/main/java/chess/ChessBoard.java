package chess;

import java.util.Arrays;
import java.util.Objects;

/**
 * A chessboard that can hold and rearrange chess pieces.
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessBoard {

    ChessPiece[][] board = new ChessPiece[8][8];

    public ChessBoard() {

    }

    /**
     * Adds a chess piece to the chessboard
     *
     * @param position where to add the piece to
     * @param piece    the piece to add
     */
    public void addPiece(ChessPosition position, ChessPiece piece) {
        int row = position.getRow();
        int col = position.getColumn();
        board[row-1][col-1] = piece;
    }

    /**
     * Gets a chess piece on the chessboard
     *
     * @param position The position to get the piece from
     * @return Either the piece at the position, or null if no piece is at that
     * position
     */
    public ChessPiece getPiece(ChessPosition position) {
        int row = position.getRow();
        int col = position.getColumn();
        return board[row-1][col-1];
    }

    /**
     * Sets the board to the default starting board
     * (How the game of chess normally starts)
     */

    public void initializePiece(ChessPiece.PieceType type, ChessGame.TeamColor color, int row, int col){
        ChessPiece piece = new ChessPiece(color, type);
        ChessPosition position = new ChessPosition(row,col);
        addPiece(position, piece);
    }

    public void resetBoard() {
        //Add white pawns
        for (int col = 1; col < 9; col++){
            initializePiece(ChessPiece.PieceType.PAWN, ChessGame.TeamColor.WHITE, 2, col);
        }
        //Add black pawns
        for (int col = 1; col < 9; col++) {
            initializePiece(ChessPiece.PieceType.PAWN, ChessGame.TeamColor.BLACK, 7, col);
        }
        //white rooks
        initializePiece(ChessPiece.PieceType.ROOK, ChessGame.TeamColor.WHITE, 1, 1);
        initializePiece(ChessPiece.PieceType.ROOK, ChessGame.TeamColor.WHITE, 1, 8);
        //black rooks
        initializePiece(ChessPiece.PieceType.ROOK, ChessGame.TeamColor.BLACK, 8, 1);
        initializePiece(ChessPiece.PieceType.ROOK, ChessGame.TeamColor.BLACK, 8, 8);
        //white knights
        initializePiece(ChessPiece.PieceType.KNIGHT, ChessGame.TeamColor.WHITE, 1, 2);
        initializePiece(ChessPiece.PieceType.KNIGHT, ChessGame.TeamColor.WHITE, 1, 5);
        initializePiece(ChessPiece.PieceType.KNIGHT, ChessGame.TeamColor.WHITE, 1, 7);
        //black knights
        initializePiece(ChessPiece.PieceType.KNIGHT, ChessGame.TeamColor.BLACK, 8, 2);
        initializePiece(ChessPiece.PieceType.KNIGHT, ChessGame.TeamColor.BLACK, 8, 5);
        initializePiece(ChessPiece.PieceType.KNIGHT, ChessGame.TeamColor.BLACK, 8, 7);
        //white bishops
        initializePiece(ChessPiece.PieceType.BISHOP, ChessGame.TeamColor.WHITE, 1, 3);
        initializePiece(ChessPiece.PieceType.BISHOP, ChessGame.TeamColor.WHITE, 1, 6);
        //black bishops
        initializePiece(ChessPiece.PieceType.BISHOP, ChessGame.TeamColor.BLACK, 8, 3);
        initializePiece(ChessPiece.PieceType.BISHOP, ChessGame.TeamColor.BLACK, 8, 6);
        //white king
        initializePiece(ChessPiece.PieceType.KING, ChessGame.TeamColor.WHITE, 1, 5);
        //black king
        initializePiece(ChessPiece.PieceType.KING, ChessGame.TeamColor.BLACK, 8, 5);
        //white queen
        initializePiece(ChessPiece.PieceType.QUEEN, ChessGame.TeamColor.WHITE, 1, 4);
        //black queen
        initializePiece(ChessPiece.PieceType.QUEEN, ChessGame.TeamColor.BLACK, 8, 4);

    }

    @Override
    public String toString() {
        String boardRep = "";
        for (int row = 0; row < 8; row++){
            String rowRep = "";
            for (int col = 0; col < 8; col++){
                rowRep += board[row][col];
                rowRep += " | ";
            }
            boardRep += rowRep;
            boardRep += "\n";
        }
        return boardRep;
    }



    }
}
