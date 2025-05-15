package yutgame.view;

import yutgame.model.Piece;

import javax.swing.*;
import java.util.List;
import java.util.function.Consumer;

public abstract class AbstractBoardView extends JPanel {
    public abstract void refresh(List<Piece> piecePositions);
    public abstract void highlightMoves(List<Piece> movable);
    public abstract void addPieceClickListener(Consumer<Piece> listener);
}