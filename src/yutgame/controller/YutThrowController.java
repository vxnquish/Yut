package yutgame.controller;

import java.util.*;
import java.util.stream.Collectors;

import yutgame.model.GameModel;
import yutgame.model.Piece;
import yutgame.view.IGameView;

public class YutThrowController {
    private final IGameView view;
    private final GameModel model;
    private final GameController controller;
    private List<Piece> lastMovable = List.of();
    private final Set<String> groupedKeys = new HashSet<>();

    public YutThrowController(IGameView view, GameModel model, GameController controller) {
        this.view       = view;
        this.model      = model;
        this.controller = controller;

        // 1) 랜덤 던지기
        view.onThrow(() -> applyThrow(model.throwYut()));

        // 2) 지정 던지기
        view.onFixedThrow(code -> applyThrow(model.throwYut(code)));

        // 3) 말 선택
        view.onPieceSelected(this::handleMove);

        // 최초 버튼 활성화
        view.enableThrow();
    }

    private void applyThrow(int res) {
        view.showResult(res);
        // 윷·모(4,5)가 아니면 다시 던지기 차단
        if (res != 4 && res != 5) {
            view.disableThrow();
        }
        // 이동 가능한 말 체크
        lastMovable = model.getMovablePieces();
        boolean canMove = lastMovable.stream()
            .anyMatch(p -> !(res == 0 && p.getPosition() == 0));
        if (!canMove) {
            // 이동할 말이 없으면 턴 넘기고 버튼 다시 활성화
            controller.nextTurn();
            view.enableThrow();
        }
    }

    private void handleMove(Piece piece) {
        if (!lastMovable.contains(piece)) return;

        int start = piece.getPosition();
        String owner = piece.getId().split("_")[0];
        String key = owner + "@" + start;

        // 같은 칸, 같은 소유자 말들
        List<Piece> cell = model.getPiecePositions().stream()
            .filter(p -> p.getPosition() == start)
            .filter(p -> p.getId().startsWith(owner + "_"))
            .collect(Collectors.toList());

        // 그룹화 로직
        List<Piece> toMove;
        if (groupedKeys.contains(key)) {
            toMove = cell;
        } else if (start != 0 && cell.size() >= 2) {
            groupedKeys.add(key);
            toMove = cell;
        } else {
            toMove = List.of(piece);
        }

        // 이동 & 포획
        toMove.forEach(model::movePiece);
        int dest = toMove.get(0).getPosition();
        model.getPiecePositions().stream()
            .filter(p -> p.getPosition() == dest)
            .filter(p -> !p.getId().startsWith(owner + "_"))
            .forEach(p -> p.setPosition(0));

        // 뷰 갱신
        view.refreshBoard(model.getPiecePositions());
        view.refreshInventory(model.getPiecePositions());
        view.updateScore(0);

        // 턴 넘기기 or 유지
        if (model.isTurnOver()) {
            controller.nextTurn();
        }
        // 다시 던지기 허용
        view.enableThrow();
    }
}
