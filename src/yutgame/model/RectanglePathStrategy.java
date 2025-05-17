// src/yutgame/model/RectanglePathStrategy.java
package yutgame.model;

/**
 * 사각형 보드를 위한 이동 전략.
 *  • 외곽(0–19)은 wrap‐around 로 cur+steps
 *  • 분기점(5,10,20,25,26,22,27)만 아래 if‐else 로 처리
 */
public class RectanglePathStrategy implements PathStrategy {
    private static final int OUTER_MAX    = 19;
    private static final int FINISH_INDEX = 28;

    @Override
    public int next(int cur, int steps) {
        int dest;

        // ──────────────────────────────────
        //  분기점 전용 로직 (생략… 기존과 동일)
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
        else if (cur == 21) {
            dest = switch (steps) {
                case -1 -> 20;
                case  1 -> 22;
                case  2 -> 23;
                case  3 -> 24;
                case  4 -> 15;
                case  5 -> 16;
                default -> cur;
            };
        }
        else if (cur == 22) {
            dest = switch (steps) {
                case -1 -> 21;
                case  1 -> 27;
                case  2 -> 28;
                case  3 -> 0;
                case  4, 5 -> FINISH_INDEX + 1;
                default -> cur;
            };
        }
        else if (cur == 23) {
            dest = switch (steps) {
                case -1 -> 22;
                case  1 -> 23;
                case  2 -> 15;
                case  3 -> 16;
                case  4 -> 17;
                case  5 -> 18;
                default -> cur;
            };
        }
        else if (cur == 24) {
            dest = switch (steps) {
                case -1 -> 23;
                case  1 -> 15;
                case  2 -> 16;
                case  3 -> 17;
                case  4 -> 18;
                case  5 -> 19;
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
                case  5 -> FINISH_INDEX + 1;
                default -> cur;
            };
        }
        else if (cur == 27) {
            dest = switch (steps) {
                case -1 -> 22;
                case  1 -> 28;
                case  2 -> 0;
                case  3, 4, 5 -> FINISH_INDEX + 1;
                default -> cur;
            };
        }

        // ──────────────────────────────────
        //  일반 외곽(0–19) 이동
        // ──────────────────────────────────
        else {
            int raw = cur + steps;

            // ▶ 한 바퀴+1칸(raw > OUTER_MAX+1) 넘어가면 골인
            if (raw > OUTER_MAX + 1) {
                return FINISH_INDEX + 1;
            }
            // ▶ 딱 한 바퀴(raw == OUTER_MAX+1)이면 출발점(0)으로
            if (raw == OUTER_MAX + 1) {
                return 0;
            }
            // ▶ 한 바퀴 이내(raw <= OUTER_MAX)면 wrap
            if (raw > OUTER_MAX) {
                dest = raw - (OUTER_MAX + 1);
            }
            else if (raw < 0) {
                dest = 0;
            }
            else {
                dest = raw;
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
