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
        int Row = position.getRow()+1;
        int Col = position.getColumn()+1;
        int currRow = Row;
        int currCol = Col;
        while (currRow > 1){
            currRow = currRow-1;
        }
        addMoves(currRow-1, Col);
        currRow = Row;
        while (currRow < 8){
            currRow = currRow + 1;
        }
        addMoves(currRow + 1, Col);
        while (currCol > 1){
            currCol = currCol - 1;
        }
        addMoves(currCol-1, Row);
        currCol = Col;
        while (currCol < 8){
            currCol = currCol + 1;
        }
        addMoves(currCol + 1, Row);
    }

    public boolean isImpeded(ChessPiece enemy, ChessPiece piece){
        return enemy != null;
    }

    public boolean isEnemy(ChessPiece enemy, ChessPiece piece){
        return enemy.getTeamColor() != piece.getTeamColor();

    }

    public void checkAdd(int row, int col, int newRow, int newCol){
        //check in  bounds
        if (newRow < 9 && newRow > 0 && newCol < 9 && newCol > 0) {
            ChessPiece piece = board.board[row - 1][col - 1];
            ChessPiece enemy = board.board[newRow - 1][newCol - 1];
            if (isImpeded(enemy, piece)) {
                if (isEnemy(enemy, piece)) {
                    addMoves(newRow, newCol);
                }
            } else {
                addMoves(newRow, newCol);
            }
        }
    }

    public void moveDiagonally(){
        int Row = position.getRow();
        int Col = position.getColumn();
        ChessPiece piece = board.board[Row-1][Col-1];
        int currCol = Col;
        int currRow = Row;
        while(currCol < 8 && currRow < 8){
            currRow = currRow +1;
            currCol = currCol +1;
            ChessPiece enemy = board.board[currRow - 1][currCol - 1];
            if (isImpeded(enemy,piece)) {
                if (isEnemy(enemy,piece)){
                   addMoves(currRow, currCol);
                }
                break;
            }
            addMoves(currRow, currCol);
        }
        currRow = Row;
        currCol = Col;
        while(currCol > 1 && currRow > 1){
            currRow = currRow -1;
            currCol = currCol -1;
            ChessPiece enemy = board.board[currRow - 1][currCol - 1];
            if (isImpeded(enemy,piece)) {
                if (isEnemy(enemy,piece)){
                    addMoves(currRow, currCol);
                }
                break;
            }
            addMoves(currRow, currCol);
        }
        currRow = Row;
        currCol = Col;
        while(currCol < 8 && currRow > 1){
            currRow = currRow-1;
            currCol = currCol+1;
            ChessPiece enemy = board.board[currRow - 1][currCol - 1];
            if (isImpeded(enemy,piece)) {
                if (isEnemy(enemy,piece)){
                    addMoves(currRow, currCol);
                }
                break;
            }
            addMoves(currRow, currCol);
        }
        currRow = Row;
        currCol = Col;
        while(currCol > 1 && currRow < 8){
            currRow = currRow +1;
            currCol = currCol-1;
            if (board.board[currRow-1][currCol-1] != null){
                break;
            }
            ChessPiece enemy = board.board[currRow - 1][currCol - 1];
            if (isImpeded(enemy,piece)) {
                if (isEnemy(enemy,piece)){
                    addMoves(currRow, currCol);
                }
                break;
            }
            addMoves(currRow, currCol);
        }

    }

    public void addMoves(int Row, int Col){
        ChessPosition newPosition = new ChessPosition(Row, Col);
        ChessMove move = new ChessMove(position, newPosition, null);
        possibleMoves.add(move);
    }



}
