package chess;

import java.util.Collection;
import java.util.Objects;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {

    ChessGame.TeamColor color;
    ChessPiece.PieceType type;

    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType pieceType) {
        color = pieceColor;
        type = pieceType;
    }

    /**
     * The various different chess piece options
     */
    public enum PieceType {
        KING,
        QUEEN,
        BISHOP,
        KNIGHT,
        ROOK,
        PAWN
    }

    /**
     * @return Which team this chess piece belongs to
     */
    public ChessGame.TeamColor getTeamColor() {
        return color;
    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {
        return type;
    }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        if (type == PieceType.KING) {
            KingMoves moves = new KingMoves(board, myPosition);
            moves.calculateMoves();
            return moves.possibleMoves;
        }
        else if (type == PieceType.QUEEN){
            QueenMoves moves = new QueenMoves(board, myPosition);
            moves.calculateMoves();
            return moves.possibleMoves;
        }
        else if (type == PieceType.BISHOP){
            BishopMoves moves = new BishopMoves(board, myPosition);
            moves.calculateMoves();
            return moves.possibleMoves;
        }
        else if (type == PieceType.KNIGHT){
            KnightMoves moves = new KnightMoves(board, myPosition);
            moves.calculateMoves();
            return moves.possibleMoves;
        }
        else{
            return null;
        }


//        else if (type == PieceType.ROOK){
//            RookMoves moves = new RookMoves(board, myPosition);
//            return moves.possibleMoves;
//        }
//        else if (type == PieceType.PAWN){
//            PawnMoves moves = new PawnMoves(board, myPosition);
//            return moves.possibleMoves;
//        }
    }


    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessPiece that = (ChessPiece) o;
        return color == that.color && type == that.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(color, type);
    }

    @Override
    public String toString() {
        return String.format("%s %s", color, type) ;
    }
}
