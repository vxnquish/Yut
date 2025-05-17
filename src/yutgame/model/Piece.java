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

    /**
     * 말의 위치를 설정합니다.
     *  - position 0 → >0 으로 처음 움직일 때 hasMoved=true
     *  - 이후 외부에서 setHasMoved(false) 로 리셋할 수도 있습니다.
     */
    public void setPosition(int pos) {
        // “출발점(0)에서 처음으로 보드 위(>0)로” 이동하면 hasMoved=true
        if (!hasMoved && this.position == 0 && pos > 0) {
            hasMoved = true;
        }
        this.position = pos;
    }

    /**
     * 한 번이라도 움직인 적이 있는지 반환합니다.
     */
    public boolean hasMoved() {
        return hasMoved;
    }

    /**
     * 외부에서 움직인 상태를 강제로 설정할 수 있도록 추가합니다.
     * (예: 잡혀서 인벤토리로 돌아갈 때 hasMoved=false)
     */
    public void setHasMoved(boolean hasMoved) {
        this.hasMoved = hasMoved;
    }
}
