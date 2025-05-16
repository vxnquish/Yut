// src/yutgame/model/RectanglePathStrategy.java
package yutgame.model;

/**
 * 사각형 보드를 위한 이동 전략.
 *   • 외곽(0–19)은 cur+steps 가 19 초과 시 곧바로 골인 처리
 *   • 분기점(5,10,20,25,26,22,27)은 각각 if‐else 로 처리
 */
public class RectanglePathStrategy implements PathStrategy {
    private static final int OUTER_MAX    = 19;
    private static final int FINISH_INDEX = 28;

    @Override
    public int next(int cur, int steps) {
        int dest;

        // ──────────────────────────────────
        //  분기점 전용 로직
        // ──────────────────────────────────
        if (cur == 5) {
            dest = switch (steps) {
                case -1 -> 4;
                case  1 -> 20;
                case  2 -> 21;
                case  3 -> 22;
                case  4 -> 23;
                case  5 -> 24;
                default -> cur;
            };
        }
        else if (cur == 10) {
            dest = switch (steps) {
                case -1 -> 9;
                case  1 -> 25;
                case  2 -> 26;
                case  3 -> 22;
                case  4 -> 27;
                case  5 -> 28;
                default -> cur;
            };
        }
        else if (cur == 20) {
            dest = switch (steps) {
                case -1 -> 5;
                case  1 -> 21;
                case  2 -> 22;
                case  3 -> 23;
                case  4 -> 24;
                case  5 -> 15;
                default -> cur;
            };
        }
        else if (cur == 25) {
            dest = switch (steps) {
                case -1 -> 10;
                case  1 -> 26;
                case  2 -> 22;
                case  3 -> 27;
                case  4 -> 28;
                case  5 -> 0;
                default -> cur;
            };
        }
        else if (cur == 26) {
            dest = switch (steps) {
                case -1 -> 25;
                case  1 -> 22;
                case  2 -> 27;
                case  3 -> 28;
                case  4 -> 0;
                case  5 -> FINISH_INDEX + 1; // 통과
                default -> cur;
            };
        }
        else if (cur == 22) {
            dest = switch (steps) {
                case -1 -> 21;
                case  1 -> 27;
                case  2 -> 28;
                case  3 -> 0;
                case  4, 5 -> FINISH_INDEX + 1; // 통과
                default -> cur;
            };
        }
        else if (cur == 27) {
            dest = switch (steps) {
                case -1 -> 22;
                case  1 -> 28;
                case  2 -> 0;
                case  3, 4, 5 -> FINISH_INDEX + 1; // 통과
                default -> cur;
            };
        }

        // ──────────────────────────────────
        //  일반 외곽(0–19) 이동: 19 초과 시 곧바로 골인
        // ──────────────────────────────────
        else {
            dest = cur + steps;
            if (dest > OUTER_MAX) {
                // 여기서 wrap‐around 대신 곧바로 finish 인덱스 초과를 반환
                dest = FINISH_INDEX + 1;
            }
            else if (dest < 0) {
                // 빽도 음수 방지
                dest = 0;
            }
        }

        return dest;
    }

    @Override
    public boolean isFinish(int idx) {
        return idx > FINISH_INDEX;
    }

    @Override
    public int maxIndex() {
        return FINISH_INDEX;
    }
}
