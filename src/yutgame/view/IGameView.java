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
    /** 뷰를 화면에 띄웁니다 */
    void show();

    /** 윷 던지기 버튼 클릭 시 콜백 등록 */
    void onThrow(Runnable handler);

    /** 판 위 또는 인벤토리에서 말 선택 시 콜백 등록 */
    void onPieceSelected(Consumer<Piece> handler);

    /** 보드 위의 말을 갱신합니다 */
    void refreshBoard(List<Piece> pieces);

    /** 인벤토리(보드 밖 위치==0) 갱신 */
    void refreshInventory(List<Piece> onBoard);

    /** 윷 던지기 결과 표시 */
    void showResult(int result);

    /** 현재 차례 플레이어 강조 */
    void updateTurn(Player current);

    /** 각 플레이어별 골인 점수 표시 */
    void updateScore(int ignored);

    /** 윷 던지기 버튼을 비활성화(눌리지 않도록) */
    void disableThrow();

    /** 윷 던지기 버튼을 다시 활성화 */
    void enableThrow();
}
