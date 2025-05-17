// src/yutgame/view/IGameView.java
package yutgame.view;

import yutgame.model.Piece;
import yutgame.model.Player;

import java.util.List;
import java.util.function.Consumer;

/**
 * MVC View 인터페이스: Swing → GameController 결합 해제
 */
public interface IGameView {
    void show();
    void onThrow(Runnable handler);
    void onFixedThrow(Consumer<Integer> handler);  // ← 추가
    void onPieceSelected(Consumer<Piece> handler);
    void refreshBoard(List<Piece> pieces);
    void refreshInventory(List<Piece> onBoard);
    void showResult(int result);
    void updateTurn(Player current);
    void updateScore(int ignored);
    void disableThrow();
    void enableThrow();
}
