package yutgame.model;

/**
 * 게임의 말(Piece)을 나타내는 모델 클래스
 */
public class Piece {
    private final String id;
    private int position;

    public Piece(String id) {
        this.id = id;
        this.position = 0; // 출발 위치
    }

    public String getId() {
        return id;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }
}