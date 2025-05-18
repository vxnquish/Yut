// src/yutgame/model/RectanglePathStrategy.java
package yutgame.model;

public class RectanglePathStrategy implements PathStrategy {
    private static final int OUTER_MAX    = 19; // Rec * 5 - 1 = 4 * 5 - 1 = 19
    private static final int FINISH_INDEX = 29; // 20 + 8 + 1

    @Override
    public int next(int cur, int steps) {
        int dest;

        // ──────────────────────────────────
        //  특수 로직
        // ──────────────────────────────────
        if (cur == 1) {
            dest = switch (steps) {
                case -1 -> 29;
                case  1 -> 2;
                case  2 -> 3;
                case  3 -> 4;
                case  4 -> 5;
                case  5 -> 6;
                default -> cur;
            };
        }
        else if (cur == 5) {
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
                case  3 -> 29;
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
                case  5 -> 29;
                default -> cur;
            };
        }
        else if (cur == 26) {
            dest = switch (steps) {
                case -1 -> 25;
                case  1 -> 22;
                case  2 -> 27;
                case  3 -> 28;
                case  4 -> 29;
                case  5 -> FINISH_INDEX + 1;
                default -> cur;
            };
        }
        else if (cur == 27) {
            dest = switch (steps) {
                case -1 -> 22;
                case  1 -> 28;
                case  2 -> 29;
                case  3, 4, 5 -> FINISH_INDEX + 1;
                default -> cur;
            };
        }
        else if (cur == 28) {
            dest = switch (steps) {
                case -1 -> 27;
                case  1 -> 29;
                case  2, 3, 4, 5 -> FINISH_INDEX + 1;
                default -> cur;
            };
        }
        else if (cur == 29) {
            // 출발점 (Node29)는 Node0으로 이동함
            dest = switch (steps) {
                case -1 -> 28; // 되돌릴 경우엔 28로
                case  1, 2, 3, 4, 5 -> FINISH_INDEX + 1;
                default -> cur;
            };
        }

        // ──────────────────────────────────
        //  일반 외곽(0–19) 이동
        // ──────────────────────────────────
        else {
            int raw = cur + steps;

            if (raw > OUTER_MAX + 1) {
                return FINISH_INDEX + 1;
            }
            if (raw == OUTER_MAX + 1) {
                return FINISH_INDEX; // 한 바퀴 완주 후 출발점(Node29) 도달
            }
            if (raw > OUTER_MAX) {
                dest = raw - (OUTER_MAX + 1);
            }
            else if (raw < 0) {
                dest = FINISH_INDEX; // 되돌아와도 출발점(Node29)
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
