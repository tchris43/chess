package chess;

import java.util.ArrayList;
import java.util.List;

public class MoveCalculator {

    ChessBoard board;
    ChessPosition position;
    List<ChessMove> possibleMoves = new ArrayList<>();

    public MoveCalculator(ChessBoard currentBoard, ChessPosition currentPosition){
        board = currentBoard;
        position = currentPosition;
    }

    public void calculateMoves(){}

    public void moveOrthogonally(){
        int originalRow = position.getRow();
        int originalCol = position.getColumn();
        ChessPiece piece = board.board[originalRow-1][originalCol-1];
        int curroriginalRow = originalRow;
        int curroriginalCol = originalCol;
        while (curroriginalRow > 1){
            curroriginalRow = curroriginalRow-1;
            ChessPiece enemy = board.board[curroriginalRow - 1][curroriginalCol - 1];
            if (isImpeded(enemy,piece)) {
                if (isEnemy(enemy,piece)){
                    addMoves(curroriginalRow, curroriginalCol);
                }
                break;
            }
            addMoves(curroriginalRow, curroriginalCol);
        }
        curroriginalRow = originalRow;
        while (curroriginalRow < 8){
            curroriginalRow = curroriginalRow + 1;
            ChessPiece enemy = board.board[curroriginalRow-1][curroriginalCol - 1];
            if (isImpeded(enemy,piece)) {
                if (isEnemy(enemy,piece)){
                    addMoves(curroriginalRow, curroriginalCol);
                }
                break;
            }
            addMoves(curroriginalRow, curroriginalCol);
        }
        curroriginalRow = originalRow;
        while (curroriginalCol > 1){
            curroriginalCol = curroriginalCol - 1;
            ChessPiece enemy = board.board[curroriginalRow - 1][curroriginalCol-1];
            if (isImpeded(enemy,piece)) {
                if (isEnemy(enemy,piece)){
                    addMoves(curroriginalRow, curroriginalCol);
                }
                break;
            }
            addMoves(curroriginalRow, curroriginalCol);
        }
        curroriginalCol = originalCol;
        while (curroriginalCol < 8){
            curroriginalCol = curroriginalCol + 1;
            ChessPiece enemy = board.board[curroriginalRow - 1][curroriginalCol - 1];
            if (isImpeded(enemy,piece)) {
                if (isEnemy(enemy,piece)){
                    addMoves(curroriginalRow, curroriginalCol);
                }
                break;
            }
            addMoves(curroriginalRow, curroriginalCol);
        }

    }

    public boolean isImpeded(ChessPiece enemy, ChessPiece piece){
        return enemy != null;
    }

    public boolean isEnemy(ChessPiece enemy, ChessPiece piece){
        return enemy.getTeamColor() != piece.getTeamColor();

    }

    public void checkAdd(int originalRow, int originalCol, int neworiginalRow, int neworiginalCol){
        //check in  bounds
        if (neworiginalRow < 9 && neworiginalRow > 0 && neworiginalCol < 9 && neworiginalCol > 0) {
            ChessPiece piece = board.board[originalRow - 1][originalCol - 1];
            ChessPiece enemy = board.board[neworiginalRow - 1][neworiginalCol - 1];
            if (isImpeded(enemy, piece)) {
                if (isEnemy(enemy, piece)) {
                    addMoves(neworiginalRow, neworiginalCol);
                }
            }
            else {
                addMoves(neworiginalRow, neworiginalCol);
            }
        }
    }

    public void moveDiagonally(){
        int originalRow = position.getRow();
        int originalCol = position.getColumn();
        ChessPiece piece = board.board[originalRow-1][originalCol-1];
        int curroriginalCol = originalCol;
        int curroriginalRow = originalRow;
        while(curroriginalCol < 8 && curroriginalRow < 8){
            curroriginalRow = curroriginalRow +1;
            curroriginalCol = curroriginalCol +1;
            ChessPiece enemy = board.board[curroriginalRow - 1][curroriginalCol - 1];
            if (isImpeded(enemy,piece)) {
                if (isEnemy(enemy,piece)){
                   addMoves(curroriginalRow, curroriginalCol);
                }
                break;
            }
            addMoves(curroriginalRow, curroriginalCol);
        }
        curroriginalRow = originalRow;
        curroriginalCol = originalCol;
        while(curroriginalCol > 1 && curroriginalRow > 1){
            curroriginalRow = curroriginalRow -1;
            curroriginalCol = curroriginalCol -1;
            ChessPiece enemy = board.board[curroriginalRow - 1][curroriginalCol - 1];
            if (isImpeded(enemy,piece)) {
                if (isEnemy(enemy,piece)){
                    addMoves(curroriginalRow, curroriginalCol);
                }
                break;
            }
            addMoves(curroriginalRow, curroriginalCol);
        }
        curroriginalRow = originalRow;
        curroriginalCol = originalCol;
        while(curroriginalCol < 8 && curroriginalRow > 1){
            curroriginalRow = curroriginalRow-1;
            curroriginalCol = curroriginalCol+1;
            ChessPiece enemy = board.board[curroriginalRow - 1][curroriginalCol - 1];
            if (isImpeded(enemy,piece)) {
                if (isEnemy(enemy,piece)){
                    addMoves(curroriginalRow, curroriginalCol);
                }
                break;
            }
            addMoves(curroriginalRow, curroriginalCol);
        }
        curroriginalRow = originalRow;
        curroriginalCol = originalCol;
        while(curroriginalCol > 1 && curroriginalRow < 8){
            curroriginalRow = curroriginalRow +1;
            curroriginalCol = curroriginalCol-1;
            ChessPiece enemy = board.board[curroriginalRow - 1][curroriginalCol - 1];
            if (isImpeded(enemy,piece)) {
                if (isEnemy(enemy,piece)){
                    addMoves(curroriginalRow, curroriginalCol);
                }
                break;
            }
            addMoves(curroriginalRow, curroriginalCol);
        }

    }

    public void addMoves(int originalRow, int originalCol){
        ChessPosition newPosition = new ChessPosition(originalRow, originalCol);
        ChessMove move = new ChessMove(position, newPosition, null);
        possibleMoves.add(move);
    }



}
