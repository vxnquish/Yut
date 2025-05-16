// src/yutgame/model/PentagonPathStrategy.java
package yutgame.model;

public class PentagonPathStrategy implements PathStrategy {
    private static final int FINISH = 35;
    @Override
    public int next(int cur, int steps) {
        // TODO: 오각형 전용 분기 로직 구현
        return cur + steps; 
    }
    @Override public boolean isFinish(int idx) { return idx > FINISH; }
    @Override public int maxIndex()      { return FINISH; }
}