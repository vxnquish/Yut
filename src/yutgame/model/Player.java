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
}