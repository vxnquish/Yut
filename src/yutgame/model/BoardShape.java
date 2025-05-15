// src/yutgame/model/BoardShape.java
package yutgame.model;

public enum BoardShape {
    SQUARE("사각형"),
    PENTAGON("오각형"),
    HEXAGON("육각형");

    private final String label;
    BoardShape(String label) { this.label = label; }
    @Override public String toString() { return label; }
}
