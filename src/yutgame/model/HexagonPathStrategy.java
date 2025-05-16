// src/yutgame/model/HexagonPathStrategy.java
package yutgame.model;

public class HexagonPathStrategy implements PathStrategy {
    private static final int FINISH = 42;
    @Override
    public int next(int cur, int steps) {
        // TODO: 육각형 전용 분기 로직 구현
        return cur + steps;
    }
    @Override public boolean isFinish(int idx) { return idx > FINISH; }
    @Override public int maxIndex()      { return FINISH; }
}