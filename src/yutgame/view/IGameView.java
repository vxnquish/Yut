// src/yutgame/view/IGameView.java
package yutgame.view;

import yutgame.model.Piece;
import yutgame.model.Player;

import java.util.List;
import java.util.function.Consumer;

/**
 * MVC View interface to decouple controllers from Swing (or any UI).
 */
public interface IGameView {
    /** Make the view visible */
    void show();

    /** Register a handler when "Throw Yut" is clicked */
    void onThrow(Runnable handler);

    /** Register a handler when a piece is selected (either from board or inventory) */
    void onPieceSelected(Consumer<Piece> handler);

    /** Refresh the board display with the given piece positions */
    void refreshBoard(List<Piece> pieces);

    /** Refresh the inventory display (pieces still at position 0) */
    void refreshInventory(List<Piece> onBoard);

    /** Display the result of the yut throw */
    void showResult(int result);

    /** Highlight whose turn it is */
    void updateTurn(Player current);

    /** Display the current goal score */
    void updateScore(int score);
}
