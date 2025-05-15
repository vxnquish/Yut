// src/yutgame/controller/YutThrowController.java
package yutgame.controller;

import java.util.*;
import yutgame.model.*;
import yutgame.view.*;

/**
 * 윷 던지기 버튼과 말 이동을 제어하는 컨트롤러입니다.
 */
public class YutThrowController {
    private final IGameView view;
    private final GameModel model;
    private final GameController controller;
    private List<Piece> lastMovable = List.of();
    // "<ownerId>@<startPos>" 형태의 키로 한 번 뭉친 그룹을 추적합니다.
    private final Set<String> groupedKeys = new HashSet<>();

    public YutThrowController(IGameView view, GameModel model, GameController controller) {
        this.view       = view;
        this.model      = model;
        this.controller = controller;

        // 1) 윷 던지기 이벤트
        view.onThrow(() -> {
            int res = model.throwYut();
            view.showResult(res);

            lastMovable = model.getMovablePieces();
            boolean hasMove = lastMovable.stream()
                .anyMatch(p -> !(res == 0 && p.getPosition() == 0));
            if (!hasMove) {
                controller.nextTurn();
            }
        });

        // 2) 말 선택 이벤트 (인벤토리 또는 보드)
        view.onPieceSelected(this::handleMove);
    }

    private void handleMove(Piece piece) {
        if (!lastMovable.contains(piece)) return;

        int startPos = piece.getPosition();
        String ownerId = piece.getId().split("_")[0];
        String key = ownerId + "@" + startPos;

        // 같은 칸에 있는, 같은 플레이어 소유의 말들
        List<Piece> cellPieces = model.getPiecePositions().stream()
            .filter(p -> p.getPosition() == startPos)
            .filter(p -> p.getId().startsWith(ownerId + "_"))
            .toList();

        List<Piece> toMove;
        if (groupedKeys.contains(key)) {
            // 이미 그룹에 묶인 경우: 계속 그룹 전체 이동
            toMove = cellPieces;
        } else if (startPos != 0 && cellPieces.size() >= 2) {
            // 0이 아닌 칸에서 처음 두 개 이상 뭉친 순간
            groupedKeys.add(key);
            toMove = cellPieces;
        } else {
            // 그 외: 클릭한 말 하나만 이동
            toMove = List.of(piece);
        }

        // 이동 처리
        toMove.forEach(model::movePiece);
        int dest = toMove.get(0).getPosition();

        // 포획 처리: 도착지에 상대말이 있으면 start(0)로 돌려보냄
        model.getPiecePositions().stream()
            .filter(p -> p.getPosition() == dest)
            .filter(p -> !p.getId().startsWith(ownerId + "_"))
            .forEach(p -> p.setPosition(0));

        // 뷰 갱신
        view.refreshBoard(model.getPiecePositions());
        view.refreshInventory(model.getPiecePositions());

        // 턴 종료
        controller.nextTurn();
    }
}
