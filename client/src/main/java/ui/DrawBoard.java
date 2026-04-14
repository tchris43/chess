package ui;

import chess.*;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Collection;

import static ui.EscapeSequences.*;

public class DrawBoard {
    private static final int BOARD_SIZE_IN_SQUARES = 8;
    private static final int SQUARE_SIZE_IN_PADDED_CHARS = 1;

    public static final String EMPTY = " ";
    public static final String WHITE_KING = SET_TEXT_COLOR_RED + "K";
    public static final String WHITE_QUEEN = SET_TEXT_COLOR_RED + "Q";
    public static final String WHITE_BISHOP = SET_TEXT_COLOR_RED + "B";
    public static final String WHITE_KNIGHT = SET_TEXT_COLOR_RED + "N";
    public static final String WHITE_ROOK = SET_TEXT_COLOR_RED + "R";
    public static final String WHITE_PAWN = SET_TEXT_COLOR_RED + "P";
    public static final String BLACK_KING = SET_TEXT_COLOR_BLUE + "K";
    public static final String BLACK_QUEEN = SET_TEXT_COLOR_BLUE + "Q";
    public static final String BLACK_BISHOP = SET_TEXT_COLOR_BLUE + "B";
    public static final String BLACK_KNIGHT = SET_TEXT_COLOR_BLUE + "N";
    public static final String BLACK_ROOK = SET_TEXT_COLOR_BLUE + "R";
    public static final String BLACK_PAWN = SET_TEXT_COLOR_BLUE + "P";

    public static final String BLACK = SET_BG_COLOR_BLACK + EMPTY;
    public static final String GREY = SET_BG_COLOR_LIGHT_GREY + EMPTY;

    public static final String DG = SET_BG_COLOR_DARK_GREY + EMPTY;

    public String border = DG;

    public ChessBoard board;

    public String firstSquare = GREY;
    public String secondSquare = BLACK;
    public String firstHighlight = SET_BG_COLOR_GREEN + EMPTY;
    public String secondHighlight = SET_BG_COLOR_DARK_GREEN + EMPTY;


    public Collection<ChessMove> highlights;

    public void printBoard(ChessBoard board, ChessGame.TeamColor teamColor, Collection<ChessMove> highlights) {
        this.board = board;
        this.highlights = highlights;
        var out = new PrintStream(System.out, true, StandardCharsets.UTF_8);
        out.println();
        if (teamColor == ChessGame.TeamColor.WHITE) {
            printHeaderWhite(out);
            out.print(RESET_BG_COLOR);
            out.print(RESET_TEXT_COLOR);
            out.println();
            for (int row = 8; row > 0; row--){
                printRow(out, row, false);
                out.print(RESET_BG_COLOR);
                out.println();
            }
            printHeaderWhite(out);
            out.print(RESET_BG_COLOR);
            out.print(RESET_TEXT_COLOR);
            out.println();
        }
        else {
            printHeaderBlack(out);
            out.print(RESET_BG_COLOR);
            out.print(RESET_TEXT_COLOR);
            out.println();
            for (int row = 1; row < 9; row ++){
                printRow(out, row, true);
                out.print(RESET_BG_COLOR);
                out.println();
            }
            printHeaderBlack(out);
            out.print(RESET_BG_COLOR);
            out.print(RESET_TEXT_COLOR);
            out.println();
        }

    }


    private String getPiece(ChessPiece piece){
        String pieceText = "";
        if (piece != null) {
            ChessGame.TeamColor color = piece.getTeamColor();
            ChessPiece.PieceType type = piece.getPieceType();

            if (color == ChessGame.TeamColor.WHITE) {
                if (type == ChessPiece.PieceType.KING) {
                    pieceText = WHITE_KING;
                } else if (type == ChessPiece.PieceType.BISHOP) {
                    pieceText = WHITE_BISHOP;
                } else if (type == ChessPiece.PieceType.KNIGHT) {
                    pieceText = WHITE_KNIGHT;
                } else if (type == ChessPiece.PieceType.QUEEN) {
                    pieceText = WHITE_QUEEN;
                } else if (type == ChessPiece.PieceType.PAWN) {
                    pieceText = WHITE_PAWN;
                } else if (type == ChessPiece.PieceType.ROOK) {
                    pieceText = WHITE_ROOK;
                }
            } else {
                if (type == ChessPiece.PieceType.KING) {
                    pieceText = BLACK_KING;
                } else if (type == ChessPiece.PieceType.BISHOP) {
                    pieceText = BLACK_BISHOP;
                } else if (type == ChessPiece.PieceType.KNIGHT) {
                    pieceText = BLACK_KNIGHT;
                } else if (type == ChessPiece.PieceType.QUEEN) {
                    pieceText = BLACK_QUEEN;
                } else if (type == ChessPiece.PieceType.PAWN) {
                    pieceText = BLACK_PAWN;
                } else if (type == ChessPiece.PieceType.ROOK) {
                    pieceText = BLACK_ROOK;
                }
            }
        }
        return pieceText;
    }

    private boolean inHighlights(ChessPosition pos){
        if (highlights != null) {
            for (ChessMove highlight:highlights){
                if (highlight.getEndPosition().equals(pos)) {
                    return true;
                }
            }
        }
        return false;
    }

    private void printRow(PrintStream out, int rowNum, boolean black){
        out.print(border + SET_TEXT_COLOR_BLACK + rowNum + border);
        int col;
        if (black){
            col = 8;
        }
        else {
            col = 1;
        }
        for (int i = 0; i < 4; i++){
            ChessPosition pos = new ChessPosition(rowNum, col);
            String piece = getPiece(board.getPiece(pos));
            if (piece.isEmpty()){
                if (inHighlights(pos)){
                    piece = firstHighlight;
                }
                else {
                    piece = firstSquare;
                }
            }
            if (black){
                col = col - 1;
            }
            else {
                col = col + 1;
            }
            ChessPosition pos2 = new ChessPosition(rowNum, col);
            String piece2 = getPiece(board.getPiece(pos2));
            if (piece2.isEmpty()){
                if (inHighlights(pos2)) {
                    piece2 = secondHighlight;
                }
                else {
                    piece2 = secondSquare;
                }
            }

            if (inHighlights(pos)){
                out.print(firstHighlight + piece + firstHighlight);
            }
            else {
                out.print(firstSquare + piece + firstSquare);
            }
            if (inHighlights(pos2)){
                out.print(secondHighlight + piece2 + secondHighlight);
            }
            else {
                out.print(secondSquare + piece2 + secondSquare);
            }

            if (black){
                col = col - 1;
            }
            else {
                col = col + 1;
            }

        }
        out.print(RESET_TEXT_COLOR);
        out.print(border + SET_TEXT_COLOR_BLACK+ rowNum + border);
        out.print(RESET_TEXT_COLOR);

        var tmp = firstSquare;
        firstSquare = secondSquare;
        secondSquare = tmp;
        var tmpHighlight = firstHighlight;
        firstHighlight = secondHighlight;
        secondHighlight = tmpHighlight;

    }

    private void printHeaderBlack(PrintStream out){
        out.print(DG + DG + DG);
        out.print(DG + SET_TEXT_COLOR_BLACK + "h" + DG);
        out.print(DG + SET_TEXT_COLOR_BLACK + "g" + DG);
        out.print(DG + SET_TEXT_COLOR_BLACK + "f" + DG);
        out.print(DG + SET_TEXT_COLOR_BLACK + "e" + DG);
        out.print(DG + SET_TEXT_COLOR_BLACK + "d" + DG);
        out.print(DG + SET_TEXT_COLOR_BLACK + "c" + DG);
        out.print(DG + SET_TEXT_COLOR_BLACK + "b" + DG);
        out.print(DG + SET_TEXT_COLOR_BLACK + "a" + DG);
        out.print(DG + DG + DG);
        out.print(RESET_BG_COLOR);
    }

    private void printHeaderWhite(PrintStream out){
        out.print(DG + DG + DG);
        out.print(DG + SET_TEXT_COLOR_BLACK + "a" + DG);
        out.print(DG + SET_TEXT_COLOR_BLACK + "b" + DG);
        out.print(DG + SET_TEXT_COLOR_BLACK + "c" + DG);
        out.print(DG + SET_TEXT_COLOR_BLACK + "d" + DG);
        out.print(DG + SET_TEXT_COLOR_BLACK + "e" + DG);
        out.print(DG + SET_TEXT_COLOR_BLACK + "f" + DG);
        out.print(DG + SET_TEXT_COLOR_BLACK + "g" + DG);
        out.print(DG + SET_TEXT_COLOR_BLACK + "h" + DG);
        out.print(DG + DG + DG);
        out.print(RESET_BG_COLOR);
    }
}
