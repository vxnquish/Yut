// src/yutgame/view/IGameView.java
package yutgame.view;

import yutgame.model.Piece;
import yutgame.model.Player;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.IntConsumer;

/**
 * MVC View 인터페이스
 */
public interface IGameView {
    void show();
    void hide();

    /** 랜덤 던지기 */
    void onThrow(Runnable handler);
    /** 지정 던지기 (빽도·도·개·걸·윷·모) */
    void onFixedThrow(IntConsumer handler);
    /** pending 던지기 결과 중 하나 선택 */
    void onApplyThrow(IntConsumer handler);

    /** 말 선택 */
    void onPieceSelected(Consumer<Piece> handler);

    /** 보드/인벤토리 갱신 */
    void refreshBoard(List<Piece> pieces);
    void refreshInventory(List<Piece> onBoard);

    /** 결과 즉시 표시 */
    void showResult(int result);
    /** pending 리스트 표시 */
    void showPendingThrows(List<Integer> pending);

    /** 하이라이트 가능한 말 표시 */
    void highlightMoves(List<Piece> movable);

    /** 차례 표시 */
    void updateTurn(Player current);
    /** 점수 표시 */
    void updateScore(int ignored);

    /** 던지기 버튼 비활성/활성 */
    void disableThrow();
    void enableThrow();

    /** 게임 종료 다이얼로그: 승자, 재시작·종료 콜백 */
    void showGameOver(String winnerId, Runnable onRestart, Runnable onExit);
}
