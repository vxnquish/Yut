// src/yutgame/view/AbstractBoardView.java
package yutgame.view;

import yutgame.model.Piece;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.function.Consumer;

public abstract class AbstractBoardView extends JPanel {
    /**
     * 화면에 보이는 말 위치들을 갱신합니다.
     */
    public abstract void refresh(List<Piece> piecePositions);

    /**
     * 이동 가능한 말들을 강조 표시합니다.
     */
    public abstract void highlightMoves(List<Piece> movable);

    /**
     * 말 클릭 시 동작할 리스너를 등록합니다.
     */
    public abstract void addPieceClickListener(Consumer<Piece> listener);

    /**
     * 각 노드의 픽셀 좌표 리스트를 반환합니다.
     */
    public abstract List<Point> getNodeLocations();

    /**
     * 주어진 말(Piece)을 from 인덱스에서 to 인덱스로 애니메이션 이동시킵니다.
     */
    public abstract void animatePiece(Piece piece, int from, int to);

    /**
     * (선택적 확장) 노드 클릭 시 호출될 리스너 등록.
     * 필요 시 각 보드 구현체에서 override하세요.
     */
    public void addNodeClickListener(Consumer<Integer> listener) {
        // 기본 구현 없음. RectangleBoardView 등에서 override 가능.
    }
}
