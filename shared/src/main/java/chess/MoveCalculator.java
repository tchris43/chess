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
        int X = position.getRow()+1;
        int Y = position.getColumn()+1;
        int currX = X;
        int currY = Y;
        while (currX > 1){
            currX = currX-1;
        }
        addMoves(currX-1, Y);
        currX = X;
        while (currX < 8){
            currX = currX + 1;
        }
        addMoves(currX + 1, Y);
        while (currY > 1){
            currY = currY - 1;
        }
        addMoves(currY-1, X);
        currY = Y;
        while (currY < 8){
            currY = currY + 1;
        }
        addMoves(currY + 1, X);
    }

    public void moveDiagonally(){
        int X = position.getRow();
        int Y = position.getColumn();
        int currY = Y;
        int currX = X;
        while(currY < 8 && currX < 8){
            currX = currX +1;
            currY = currY +1;
            if (board.board[currX - 1][currY - 1] != null) {
                break;
            }
            addMoves(currX, currY);
        }
        currX = X;
        currY = Y;
        while(currY > 1 && currX > 1){
            currX = currX -1;
            currY = currY -1;
            if (board.board[currX-1][currY-1] != null){
                break;
            }
            addMoves(currX, currY);
        }
        currX = X;
        currY = Y;
        while(currY < 8 && currX > 1){
            currX = currX-1;
            currY = currY+1;
            if (board.board[currX-1][currY-1] != null){
                break;
            }
            addMoves(currX, currY);
        }
        currX = X;
        currY = Y;
        while(currY > 1 && currX < 8){
            currX = currX +1;
            currY = currY-1;
            if (board.board[currX-1][currY-1] != null){
                break;
            }
            addMoves(currX, currY);
        }

    }

    public void addMoves(int X, int Y){
        ChessPosition newPosition = new ChessPosition(X, Y);
        ChessMove move = new ChessMove(position, newPosition, null);
        possibleMoves.add(move);
    }



}
