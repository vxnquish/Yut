package yutgame.controller;

import yutgame.model.GameModel;
import yutgame.model.Piece;
import yutgame.view.AbstractBoardView;
import yutgame.view.YutResultView;

import javax.swing.*;
import java.util.List;

public class YutThrowController {
    private final JButton throwButton;
    private final YutResultView resultView;
    private final GameModel model;
    private final GameController gameController;
    private final AbstractBoardView boardView;

    public YutThrowController(
            JButton throwButton,
            YutResultView resultView,
            GameModel model,
            GameController gameController,
            AbstractBoardView boardView) {
        this.throwButton    = throwButton;
        this.resultView     = resultView;
        this.model          = model;
        this.gameController = gameController;
        this.boardView      = boardView;

        // 윷 던지기 버턴 이벤트
        throwButton.addActionListener(e -> {
            int result = model.throwYut();
            resultView.showResult(result);
            List<Piece> movable = model.getMovablePieces();
            boardView.highlightMoves(movable);
        });

        // 말 클릭 시 이동
        boardView.addPieceClickListener(piece -> {
            model.movePiece(piece);
            boardView.refresh(model.getPiecePositions());

            if (model.isTurnOver()) {
                gameController.nextTurn();
            }
        });
    }
}