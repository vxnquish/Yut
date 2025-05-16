// src/yutgame/model/PathStrategy.java
package yutgame.model;

public interface PathStrategy {
    /** cur 위치에서 steps 만큼 이동했을 때 도착 위치 인덱스 반환 */
    int next(int cur, int steps);
    /** idx 가 결승(골인) 상태인지 검사 */
    boolean isFinish(int idx);
    /** 보드의 마지막 유효 인덱스 (finish 바로 전) */
    int maxIndex();
}
