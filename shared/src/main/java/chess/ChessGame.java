package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {

    private TeamColor teamTurn;
    private ChessBoard gameBoard = new ChessBoard();


    public ChessGame() {
        gameBoard.resetBoard();
        setTeamTurn(TeamColor.WHITE);
    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return teamTurn;
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        teamTurn = team;
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    private ChessPosition findKingPosition(TeamColor pieceColor){
        //check every spot
        int kingRow = 0;
        int kingCol = 0;
        for (int row = 1; row < 9; row ++) {
            for (int col = 1; col < 9; col ++) {
                ChessPosition position = new ChessPosition(row, col);
                ChessPiece piece = gameBoard.getPiece(position);
                if (piece != null) {
                    if (piece.getPieceType() == ChessPiece.PieceType.KING && piece.getTeamColor() == pieceColor) {
                        //This is the king
                        kingRow = row;
                        kingCol = col;
                    }
                }
            }
        }
        ChessPosition kingPosition = new ChessPosition(kingRow, kingCol);
        return kingPosition;
    }

    private ChessBoard testMove(ChessMove move) {
        ChessBoard testBoard = new ChessBoard(gameBoard);
        ChessPiece piece = testBoard.getPiece(move.getStartPosition());
        Collection<ChessMove> possibleMoves = piece.pieceMoves(gameBoard, move.getStartPosition());

        testBoard.addPiece(move.start, null);
        testBoard.addPiece(move.getEndPosition(), piece);

        return testBoard;
    }

    private Collection<ChessMove> getEnemyMoves(ChessBoard testBoard, TeamColor kingColor){
        //change team turn
        //loop through each piece in turn
        //add its moves to enemyMoves
        //return enemy moves

        Collection<ChessMove> enemyMoves = new ArrayList<>();

        for (int row = 1; row < 9; row++){
            for (int col = 1; col < 9; col ++){
                ChessPosition position = new ChessPosition(row,col);
                ChessPiece piece = testBoard.getPiece(position);
                if (piece != null && piece.getTeamColor() != kingColor) {
                    enemyMoves.addAll(piece.pieceMoves(testBoard, position));
                }
            }
        }

        return enemyMoves;

    }
    
    private boolean kingInDanger(ChessPosition kingPosition, Collection<ChessMove> enemyMoves){
        //return whether the king is in the enemy moves
        for (ChessMove move: enemyMoves){
            if (move.getEndPosition().getRow() == kingPosition.getRow() && move.getEndPosition().getColumn() == kingPosition.getColumn()){
                return true;
            }
        }
        return false;
    }
    
    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        ChessPiece piece = gameBoard.getPiece(startPosition);
        TeamColor pieceColor = piece.getTeamColor();
        if (piece == null){
            return null;
        }
        Collection<ChessMove> possibleMoves = piece.pieceMoves(gameBoard, startPosition);
        Collection<ChessMove> safeMoves = new ArrayList<>();

        if (piece.getPieceType() == ChessPiece.PieceType.KING){
            TeamColor kingColor = piece.getTeamColor();
            Collection<ChessMove> enemyMoves = getEnemyMoves(gameBoard, kingColor);
            for (ChessMove move : possibleMoves) {
                //only add the move if not in enemy moves
                boolean safeMove = true;
                for (ChessMove enemyMove : enemyMoves){
                    if (enemyMove.getEndPosition().equals(move.getEndPosition())){
                        safeMove = false;
                    }
                }
                if (safeMove){
                    safeMoves.add(move);
                }
            }
        }
        else {
            ChessPosition kingPosition = findKingPosition(pieceColor);
            ChessPiece king = gameBoard.getPiece(kingPosition);
            TeamColor kingColor = king.getTeamColor();
            for (ChessMove move : possibleMoves) {
                //test out the move on a board copy
                ChessBoard testBoard = testMove(move);
                //examine enemy's possible moves
                Collection<ChessMove> enemyMoves = getEnemyMoves(testBoard, kingColor);
                //only add the move if not in enemy moves
                if (!kingInDanger(kingPosition, enemyMoves)) {
                    safeMoves.add(move);
                }
            }
        }

        //If I move, will an enemy be able to attack
        //hypothetically move
        //hypothetically switch team turn
        //go through all possible moves looking for king's spot
        //** King is in danger if his spot exists
        


        
        return safeMoves;
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to perform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        Collection<ChessMove> possibleMoves = validMoves(move.getStartPosition());
        ChessPiece piece = gameBoard.getPiece(move.getStartPosition());
        if (!possibleMoves.contains(move) || piece.getTeamColor() != teamTurn){
            throw new InvalidMoveException("This move is invalid");
        }
        else {
            gameBoard.addPiece(move.start, null);
            gameBoard.addPiece(move.getEndPosition(), piece);
        }
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        //find king
        ChessPosition kingPosition = findKingPosition(TeamColor.WHITE);//UPDATE THIS
        ChessPiece king = gameBoard.getPiece(kingPosition);
        TeamColor kingColor = king.getTeamColor();
        //examine enemy's possible moves
        Collection<ChessMove> enemyMoves = getEnemyMoves(gameBoard, kingColor);
        //king is safe in not in enemy moves
        if (!kingInDanger(kingPosition, enemyMoves)) {
            return false;
        }
        return true;
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        //identify the spot that needs to be blocked
        //gather the possible moves I can make
        //see if the need to block spot is a possible move. If not return true for checkmate
        throw new RuntimeException("Not implemented");
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves while not in check.
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        gameBoard = board;
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return gameBoard;
    }


    @Override
    public boolean equals(Object obj) {
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        ChessGame that = (ChessGame)obj;
        return getTeamTurn().equals(that.getTeamTurn()) && getBoard().equals(that.getBoard());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getTeamTurn(), gameBoard);
    }

    @Override
    public String toString() {
        return "ChessGame{" +
                "teamTurn=" + teamTurn +
                ", gameBoard=" + gameBoard +
                '}';
    }
}
