// src/yutgame/model/PathStrategyFactory.java
package yutgame.model;

import yutgame.model.SettingModel;
import yutgame.model.BoardShape;

public class PathStrategyFactory {
    public static PathStrategy of(BoardShape shape) {
        return switch(shape) {
            case SQUARE   -> new RectanglePathStrategy();
            case PENTAGON -> new PentagonPathStrategy();
            case HEXAGON  -> new HexagonPathStrategy();
        };
    }
}
