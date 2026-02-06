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

    private ChessPosition findKingPosition(){
        //check every spot
        int kingRow = 0;
        int kingCol = 0;
        for (int row = 1; row < 9; row ++) {
            for (int col = 1; col < 9; col ++) {
                ChessPosition position = new ChessPosition(row, col);
                ChessPiece piece = gameBoard.getPiece(position);
                if (piece.getPieceType() == ChessPiece.PieceType.KING && piece.getTeamColor() == getTeamTurn()) {
                    //This is the king
                    kingRow = row;
                    kingCol = col;
                }
            }
        }
        ChessPosition kingPosition = new ChessPosition(kingRow, kingCol);
        return kingPosition;
    }

    private ChessBoard testMove(ChessMove move) throws CloneNotSupportedException {
        return (ChessBoard) gameBoard.clone();
    }

    private Collection<ChessMove> getEnemyMoves(ChessBoard testBoard){
        //change team turn
        //loop through each piece in turn
        //add its moves to enemyMoves
        //return enemy moves

        Collection<ChessMove> enemyMoves = new ArrayList<>();

        TeamColor enemyColor;
        if (getTeamTurn() == TeamColor.WHITE){
            enemyColor = TeamColor.BLACK;
        }
        else {
            enemyColor = TeamColor.WHITE;
        }

        for (int row = 1; row < 9; row++){
            for (int col = 1; col < 9; col ++){
                ChessPosition position = new ChessPosition(row,col);
                ChessPiece piece = testBoard.getPiece(position);
                enemyMoves.addAll(piece.pieceMoves(testBoard, position));
            }
        }

        return enemyMoves;

    }
    
    private boolean kingInDanger(ChessPosition kingPosition, Collection<ChessMove> enemyMoves){
        //return whether the king is in the enemy moves
        for (ChessMove move: enemyMoves){
            if (move.getEndPosition() == kingPosition){
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
        if (piece == null){
            return null;
        }
        Collection<ChessMove> possibleMoves = piece.pieceMoves(gameBoard, startPosition);

        //If I move, will an enemy be able to attack
        //hypothetically move
        //hypothetically switch team turn
        //go through all possible moves looking for king's spot
        //** King is in danger if his spot exists
        
        Collection<ChessMove> safeMoves = new ArrayList<>();
        //find where the king is
        ChessPosition kingPosition = findKingPosition();
        for (ChessMove move : possibleMoves){
            //test out the move on a board copy
            ChessBoard testBoard = testMove(move);
            //examine enemy's possible moves
            Collection<ChessMove> enemyMoves = getEnemyMoves(testBoard);
            //only add the move if not in enemy moves
            if (!kingInDanger(kingPosition, enemyMoves)){
                safeMoves.add(move);
            }
        }
        
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
        if (!possibleMoves.contains(move)){
            throw new InvalidMoveException("This move is invalid");
        }
        else {
            ChessPiece piece = gameBoard.getPiece(move.getStartPosition());
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
        throw new RuntimeException("Not implemented");
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
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
