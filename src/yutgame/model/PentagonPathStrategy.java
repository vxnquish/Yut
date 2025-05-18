// src/yutgame/model/PentagonPathStrategy.java
package yutgame.model;

public class PentagonPathStrategy implements PathStrategy {
    private static final int OUTER_MAX    = 24; // Pen * 5 - 1 = 5 * 5 - 1 = 24
    private static final int FINISH_INDEX = 36; // 25 + 10 + 1

    @Override
    public int next(int cur, int steps) {
        int dest;

        // ──────────────────────────────────
        //  특수 로직
        // ──────────────────────────────────
        if (cur == 1) {
            dest = switch (steps) {
                case -1 -> 36;
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
                case  1 -> 25;
                case  2 -> 26;
                case  3 -> 27;
                case  4 -> 30;
                case  5 -> 31;
                default -> cur;
            };
        }
        else if (cur == 10) {
            dest = switch (steps) {
                case -1 -> 9;
                case  1 -> 32;
                case  2 -> 33;
                case  3 -> 27;
                case  4 -> 30;
                case  5 -> 31;
                default -> cur;
            };
        }
        else if (cur == 15) {
            dest = switch (steps) {
                case -1 -> 14;
                case  1 -> 34;
                case  2 -> 35;
                case  3 -> 27;
                case  4 -> 28;
                case  5 -> 29;
                default -> cur;
            };
        }
        else if (cur == 20) {
            dest = switch (steps) {
                case -1 -> 19;
                case  1 -> 21;
                case  2 -> 22;
                case  3 -> 23;
                case  4 -> 24;
                case  5 -> 36;
                default -> cur;
            };
        }
        else if (cur == 21) {
            dest = switch (steps) {
                case -1 -> 20;
                case  1 -> 22;
                case  2 -> 23;
                case  3 -> 24;
                case  4 -> 36;
                case  5 -> FINISH_INDEX + 1;
                default -> cur;
            };
        }
        else if (cur == 22) {
            dest = switch (steps) {
                case -1 -> 21;
                case  1 -> 23;
                case  2 -> 24;
                case  3 -> 36;
                case  4, 5 -> FINISH_INDEX + 1;
                default -> cur;
            };
        }
        else if (cur == 23) {
            dest = switch (steps) {
                case -1 -> 22;
                case  1 -> 24;
                case  2 -> 36;
                case  3, 4, 5 -> FINISH_INDEX + 1;
                default -> cur;
            };
        }
        else if (cur == 24) {
            dest = switch (steps) {
                case -1 -> 23;
                case  1 -> 36;
                case  2, 3, 4, 5 -> FINISH_INDEX + 1;
                default -> cur;
            };
        }
        else if (cur == 25) {
            dest = switch (steps) {
                case -1 -> 5;
                case  1 -> 26;
                case  2 -> 27;
                case  3 -> 30;
                case  4 -> 31;
                case  5 -> 20;
                default -> cur;
            };
        }
        else if (cur == 26) {
            dest = switch (steps) {
                case -1 -> 25;
                case  1 -> 27;
                case  2 -> 30;
                case  3 -> 31;
                case  4 -> 20;
                case  5 -> 21;
                default -> cur;
            };
        }
        else if (cur == 27) {
            dest = switch (steps) {
                case -1 -> 26; // 26 or 33 or 35
                case  1 -> 28;
                case  2 -> 29;
                case  3 -> 36; 
                case  4, 5 -> FINISH_INDEX + 1;
                default -> cur;
            };
        }
        else if (cur == 28) {
            dest = switch (steps) {
                case -1 -> 27;
                case  1 -> 29;
                case  2 -> 36;
                case  3, 4, 5 -> FINISH_INDEX + 1;
                default -> cur;
            };
        }
        else if (cur == 29) {
            dest = switch (steps) {
                case -1 -> 28;
                case  1 -> 36;
                case  2, 3, 4, 5 -> FINISH_INDEX + 1;
                default -> cur;
            };
        }
        else if (cur == 30) {
            dest = switch (steps) {
                case -1 -> 27;
                case  1 -> 31;
                case  2 -> 20;
                case  3 -> 21;
                case  4 -> 22;
                case  5 -> 23;
                default -> cur;
            };
        }
        else if (cur == 31) {
            dest = switch (steps) {
                case -1 -> 30;
                case  1 -> 20;
                case  2 -> 21;
                case  3 -> 22;
                case  4 -> 23;
                case  5 -> 24;
                default -> cur;
            };
        }
        else if (cur == 32) {
            dest = switch (steps) {
                case -1 -> 10;
                case  2 -> 27;
                case  3 -> 30;
                case  4 -> 31;
                case  5 -> 20;
                default -> cur;
            };
        }
        else if (cur == 33) {
            dest = switch (steps) {
                case -1 -> 32;
                case  1 -> 27;
                case  2 -> 30;
                case  3 -> 31;
                case  4 -> 20;
                case  5 -> 21;
                default -> cur;
            };
        }
        else if (cur == 34) {
            dest = switch (steps) {
                case -1 -> 15;
                case  1 -> 35;
                case  2 -> 27;
                case  3 -> 28;
                case  4 -> 29;
                case  5 -> 26;
                default -> cur;
            };
        }
        else if (cur == 35) {
            dest = switch (steps) {
                case -1 -> 34;
                case  1 -> 27;
                case  2 -> 28;
                case  3 -> 29;
                case  4 -> 36;
                case  5 -> FINISH_INDEX + 1;
                default -> cur;
            };
        }
        else if (cur == 36) {
            dest = switch (steps) {
                case -1 -> 24;
                case  1,2,3,4,5 -> FINISH_INDEX + 1;
                default -> cur;
            };
        }

        // ──────────────────────────────────
        //  일반 외곽(0–24) 이동
        // ──────────────────────────────────
        else {
            int raw = cur + steps;

            // 한 바퀴+1칸 넘어가면 골인
            if (raw > OUTER_MAX + 1) {
                return FINISH_INDEX + 1;
            }
            // 딱 한 바퀴(raw==25)이면 출발점(36)으로
            if (raw == OUTER_MAX + 1) {
                return FINISH_INDEX;
            }
            if (raw > OUTER_MAX) {
                dest = raw - (OUTER_MAX + 1);
            } else if (raw < 0) {
                dest = FINISH_INDEX;
            } else {
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
