package yutgame.view;

import yutgame.model.SettingModel;
import yutgame.model.BoardShape;

public class BoardViewFactory {
    public static AbstractBoardView create(BoardShape shape, SettingModel config) {
        switch (shape) {
            case SQUARE:
                return new RectangleBoardView(config);
            case PENTAGON:
                return new PentagonBoardView(config);
            case HEXAGON:
                return new HexagonBoardView(config);
            default:
                throw new IllegalArgumentException("Unknown shape: " + shape);
        }
    }
}