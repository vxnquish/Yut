// src/yutgame/model/Player.java
package yutgame.model;

import java.util.ArrayList;
import java.util.List;

/**
 * 게임 참여자를 나타내는 모델 클래스
 */
public class Player {
    private final String id;
    private final List<Piece> pieces;
    private int currentPieceIndex;

    // ★ 여기에 추가
    private int score = 0;

    public Player(String id, int numPieces) {
        this.id = id;
        this.pieces = new ArrayList<>();
        for (int i = 1; i <= numPieces; i++) {
            // 각 말에 고유 ID 부여 (예: P1_1, P1_2)
            this.pieces.add(new Piece(id + "_" + i));
        }
        this.currentPieceIndex = 0;
    }

    public String getId() {
        return id;
    }

    public List<Piece> getPieces() {
        return pieces;
    }

    /**
     * 다음 이동할 말 선택 (예: 순차적 선택)
     */
    public Piece nextPiece() {
        Piece p = pieces.get(currentPieceIndex);
        currentPieceIndex = (currentPieceIndex + 1) % pieces.size();
        return p;
    }

    // ★ 점수 관련 메서드들
    /** 현재 골인(점수)된 말 개수 반환 */
    public int getScore() {
        return score;
    }

    /** 골인 시 호출해서 점수를 1 올립니다 */
    public void incrementScore() {
        score++;
    }
}
