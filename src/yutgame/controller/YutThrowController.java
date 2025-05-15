// src/yutgame/controller/YutThrowController.java
package yutgame.controller;

import java.util.*;
import java.util.stream.Collectors;
import javax.swing.JButton;
import yutgame.model.GameModel;
import yutgame.model.Piece;
import yutgame.view.AbstractBoardView;
import yutgame.view.GameView;
import yutgame.view.YutResultView;

public class YutThrowController {
    private final JButton throwButton;
    private final YutResultView resultView;
    private final GameModel model;
    private final GameController gameController;
    private final GameView gameView;
    private final AbstractBoardView boardView;
    private List<Piece> lastMovable = List.of();
    // 한 번 뭉친 뒤엔 계속 그룹 이동
    private final Set<Piece> grouped = new HashSet<>();

    public YutThrowController(
        JButton throwButton,
        YutResultView resultView,
        GameModel model,
        GameController gameController,
        GameView gameView,
        AbstractBoardView boardView
    ) {
        this.throwButton    = throwButton;
        this.resultView     = resultView;
        this.model          = model;
        this.gameController = gameController;
        this.gameView       = gameView;
        this.boardView      = boardView;

        throwButton.addActionListener(e -> {
            int res = model.throwYut();
            resultView.showResult(res);

            lastMovable = model.getMovablePieces();
            boolean hasMove = lastMovable.stream()
                .anyMatch(p -> !(res == 0 && p.getPosition() == 0));
            if (!hasMove) {
                gameController.nextTurn();
                return;
            }
            boardView.highlightMoves(lastMovable);
        });

        boardView.addPieceClickListener(this::handleMove);
        gameView.addInventoryClickListener(this::handleMove);
    }

    private void handleMove(Piece p) {
        if (!lastMovable.contains(p)) return;

        int startPos = p.getPosition();
        String ownerId = p.getId().split("_")[0];

        // 같은 칸의 동일 소유자 말들
        List<Piece> cellPieces = model.getPiecePositions().stream()
            .filter(q -> q.getPosition() == startPos)
            .filter(q -> q.getId().split("_")[0].equals(ownerId))
            .collect(Collectors.toList());

        List<Piece> toMove;
        if (grouped.contains(p)) {
            // 이미 그룹에 속하면, 그 그룹 전체 이동
            toMove = new ArrayList<>();
            for (Piece gp : grouped) {
                if (gp.getId().split("_")[0].equals(ownerId)) {
                    toMove.add(gp);
                }
            }
        } else if (startPos != 0 && cellPieces.size() >= 2) {
            // 0이 아닌 칸에서 두 개 이상 만났을 때만 그룹화
            grouped.addAll(cellPieces);
            toMove = cellPieces;
        } else {
            // 그 외엔 단일 말만 이동
            toMove = List.of(p);
        }

        // 모두 이동
        for (Piece m : toMove) {
            model.movePiece(m);
        }
        int dest = toMove.get(0).getPosition();

        // 포획: dest에 상대 말들 start(0)로
        model.getPiecePositions().stream()
            .filter(q -> q.getPosition() == dest)
            .filter(q -> !q.getId().split("_")[0].equals(ownerId))
            .forEach(q -> q.setPosition(0));

        // 화면 갱신
        List<Piece> all = model.getPiecePositions();
        boardView.refresh(all);
        gameView.updateInventory(all, model.getPlayers());

        gameController.nextTurn();
    }
}
