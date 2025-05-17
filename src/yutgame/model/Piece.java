// src/yutgame/model/Piece.java
package yutgame.model;

public class Piece {
    private final String id;
    private int position = 0;
    private boolean hasMoved = false;

    public Piece(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int pos) {
        // position 이 0 → 1 이상으로 처음 바뀔 때 한 번도 움직인 적 없었던 말이라면
        if (!hasMoved && this.position == 0 && pos > 0) {
            hasMoved = true;
        }
        this.position = pos;
    }

    // ✚ 한 번이라도 움직였는지 검사
    public boolean hasMoved() {
        return hasMoved;
    }
}
