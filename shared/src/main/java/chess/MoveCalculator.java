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
            addMoves(currX-1, Y);
            currX = currX-1;
        }
        currX = X;
        while (currX < 8){
            addMoves(currX + 1, Y);
            currX = currX + 1;
        }
        while (currY > 1){
            addMoves(currY-1, X);
            currY = currY - 1;
        }
        currY = Y;
        while (currY < 8){
            addMoves(currY + 1, X);
            currY = currY + 1;
        }
    }

    public void moveDiagonally(){
        int X = position.getRow()+1;
        int Y = position.getColumn()+1;
        int currY = Y;
        int currX = X;
        while(currY < 8){
            addMoves(currX+1, currY+1);
            currX = currX +1;
            currY = currY +1;
        }
        currX = X;
        currY = Y;
        while(currY > 1){
            addMoves(currX-1, currY-1);
            currX = currX -1;
            currY = currY -1;
        }
        currX = X;
        currY = Y;
        while(currY < 8){
            addMoves(currX-1, currY+1);
            currX = currX-1;
            currY = currY+1;
        }
        currX = X;
        currY = Y;
        while(currY > 1){
            addMoves(currX+1, currY-1);
            currX = currX +1;
            currY = currY-1;
        }
    }

    public void addMoves(int X, int Y){
        ChessPosition newPosition = new ChessPosition(X, Y);
        ChessMove move = new ChessMove(position, newPosition, null);
        possibleMoves.add(move);
    }



}
