package ui;

import chess.ChessGame;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

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
    public static final String BLACK_QUEEN = SET_BG_COLOR_BLUE + "Q";
    public static final String BLACK_BISHOP = SET_TEXT_COLOR_BLUE + "B";
    public static final String BLACK_KNIGHT = SET_TEXT_COLOR_BLUE + "N";
    public static final String BLACK_ROOK = SET_TEXT_COLOR_BLUE + "R";
    public static final String BLACK_PAWN = SET_TEXT_COLOR_BLUE + "P";

    public static final String BLACK = SET_BG_COLOR_BLACK + EMPTY;
    public static final String GREY = SET_BG_COLOR_LIGHT_GREY + EMPTY;

    public static final String DG = SET_BG_COLOR_DARK_GREY + EMPTY;

    public static void draw(ChessGame.TeamColor teamColor) {
        var out = new PrintStream(System.out, true, StandardCharsets.UTF_8);

        out.print(ERASE_SCREEN);

        if (teamColor == ChessGame.TeamColor.BLACK){

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
            out.println();

            out.print(DG + SET_TEXT_COLOR_BLACK + "1" + DG);
            out.print(GREY + WHITE_ROOK + GREY);
            out.print(BLACK + WHITE_KNIGHT + BLACK);
            out.print(GREY + WHITE_BISHOP + GREY);
            out.print(BLACK + WHITE_QUEEN + BLACK);
            out.print(GREY + WHITE_KING + GREY);
            out.print(BLACK + WHITE_BISHOP + BLACK);
            out.print(GREY + WHITE_KNIGHT + GREY);
            out.print(BLACK + WHITE_ROOK + BLACK);
            out.print(DG + SET_TEXT_COLOR_BLACK + "1" + DG);
            out.print(RESET_BG_COLOR);
            out.println();
            out.print(DG + SET_TEXT_COLOR_BLACK + "2" + DG);
            out.print(BLACK + WHITE_PAWN + BLACK);
            out.print(GREY + WHITE_PAWN + GREY);
            out.print(BLACK + WHITE_PAWN + BLACK);
            out.print(GREY + WHITE_PAWN + GREY);
            out.print(BLACK + WHITE_PAWN + BLACK);
            out.print(GREY + WHITE_PAWN + GREY);
            out.print(BLACK + WHITE_PAWN + BLACK);
            out.print(GREY + WHITE_PAWN + GREY);
            out.print(DG + SET_TEXT_COLOR_BLACK + "1" + DG);
            out.print(RESET_BG_COLOR);
            out.println();


            for (int row = 2; row < 6; row++) {
                out.print(DG + SET_TEXT_COLOR_BLACK + String.valueOf(row+1) + DG);
                for (int i = 0; i < 8; i++) {
                    if (row % 2 == 0) {
                        if (i % 2 == 0) {
                            out.print(GREY + GREY + GREY);
                        } else {
                            out.print(BLACK + BLACK + BLACK);
                        }
                    } else {
                        if (i % 2 == 0) {
                            out.print(BLACK + BLACK + BLACK);
                        } else {
                            out.print(GREY + GREY + GREY);
                        }
                    }
                }
                out.print(DG + SET_TEXT_COLOR_BLACK + String.valueOf(row+1) + DG);
                out.print(RESET_BG_COLOR);
                out.println();
            }

            out.print(DG + SET_TEXT_COLOR_BLACK + "7" + DG);
            out.print(GREY + BLACK_PAWN + GREY);
            out.print(BLACK + BLACK_PAWN + BLACK);
            out.print(GREY + BLACK_PAWN + GREY);
            out.print(BLACK + BLACK_PAWN + BLACK);
            out.print(GREY + BLACK_PAWN + GREY);
            out.print(BLACK + BLACK_PAWN + BLACK);
            out.print(GREY + BLACK_PAWN + GREY);
            out.print(BLACK + BLACK_PAWN + BLACK);
            out.print(DG + SET_TEXT_COLOR_BLACK + "7" + DG);
            out.print(RESET_BG_COLOR);
            out.println();
            out.print(DG + SET_TEXT_COLOR_BLACK + "8" + DG);
            out.print(BLACK + BLACK_ROOK + BLACK);
            out.print(GREY + BLACK_KNIGHT + GREY);
            out.print(BLACK + BLACK_BISHOP + BLACK);
            out.print(GREY + BLACK_QUEEN + GREY);
            out.print(BLACK + BLACK_KING + BLACK);
            out.print(GREY + BLACK_BISHOP + GREY);
            out.print(BLACK + BLACK_KNIGHT + BLACK);
            out.print(GREY + BLACK_ROOK + GREY);
            out.print(DG + SET_TEXT_COLOR_BLACK + "8" + DG);
            out.print(RESET_BG_COLOR);
            out.println();

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
            out.print(RESET_TEXT_COLOR);
        }
        else if (teamColor == ChessGame.TeamColor.WHITE){

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
            out.println();

            out.print(DG + SET_TEXT_COLOR_BLACK + "8" + DG);
            out.print(GREY + BLACK_ROOK + GREY);
            out.print(BLACK + BLACK_KNIGHT + BLACK);
            out.print(GREY + BLACK_BISHOP + GREY);
            out.print(BLACK + BLACK_QUEEN + BLACK);
            out.print(GREY + BLACK_KING + GREY);
            out.print(BLACK + BLACK_BISHOP + BLACK);
            out.print(GREY + BLACK_KNIGHT + GREY);
            out.print(BLACK + BLACK_ROOK + BLACK);
            out.print(DG + SET_TEXT_COLOR_BLACK + "8" + DG);
            out.print(RESET_BG_COLOR);
            out.println();
            out.print(DG + SET_TEXT_COLOR_BLACK + "7" + DG);
            out.print(BLACK + BLACK_PAWN + BLACK);
            out.print(GREY + BLACK_PAWN + GREY);
            out.print(BLACK + BLACK_PAWN + BLACK);
            out.print(GREY + BLACK_PAWN + GREY);
            out.print(BLACK + BLACK_PAWN + BLACK);
            out.print(GREY + BLACK_PAWN + GREY);
            out.print(BLACK + BLACK_PAWN + BLACK);
            out.print(GREY + BLACK_PAWN + GREY);
            out.print(DG + SET_TEXT_COLOR_BLACK + "7" + DG);
            out.print(RESET_BG_COLOR);
            out.println();

            for (int row = 2; row < 6; row++) {
                out.print(DG + SET_TEXT_COLOR_BLACK + String.valueOf(8-row) + DG);
                for (int i = 0; i < 8; i++) {
                    if (row % 2 == 0) {
                        if (i % 2 == 0) {
                            out.print(GREY + GREY + GREY);
                        } else {
                            out.print(BLACK + BLACK + BLACK);
                        }
                    } else {
                        if (i % 2 == 0) {
                            out.print(BLACK + BLACK + BLACK);
                        } else {
                            out.print(GREY + GREY + GREY);
                        }
                    }
                }
                out.print(DG + SET_TEXT_COLOR_BLACK + String.valueOf(8-row) + DG);
                out.print(RESET_BG_COLOR);
                out.println();
            }

            out.print(DG + SET_TEXT_COLOR_BLACK + "2" + DG);
            out.print(GREY + WHITE_PAWN + GREY);
            out.print(BLACK + WHITE_PAWN + BLACK);
            out.print(GREY + WHITE_PAWN + GREY);
            out.print(BLACK + WHITE_PAWN + BLACK);
            out.print(GREY + WHITE_PAWN + GREY);
            out.print(BLACK + WHITE_PAWN + BLACK);
            out.print(GREY + WHITE_PAWN + GREY);
            out.print(BLACK + WHITE_PAWN + BLACK);
            out.print(DG + SET_TEXT_COLOR_BLACK + "2" + DG);
            out.print(RESET_BG_COLOR);
            out.println();
            out.print(DG + SET_TEXT_COLOR_BLACK + "1" + DG);
            out.print(BLACK + WHITE_ROOK + BLACK);
            out.print(GREY + WHITE_KNIGHT + GREY);
            out.print(BLACK + WHITE_BISHOP + BLACK);
            out.print(GREY + WHITE_KING + GREY);
            out.print(BLACK + WHITE_QUEEN + BLACK);
            out.print(GREY + WHITE_BISHOP + GREY);
            out.print(BLACK + WHITE_KNIGHT + BLACK);
            out.print(GREY + WHITE_ROOK + GREY);
            out.print(RESET_BG_COLOR);
            out.print(DG + SET_TEXT_COLOR_BLACK + "1" + DG);
            out.print(RESET_TEXT_COLOR);
            out.println();



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
            out.print(RESET_TEXT_COLOR);

        }





    }

    private static void drawHeaders(PrintStream out){

    }

}
