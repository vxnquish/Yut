// src/yutgame/controller/YutThrowController.java
package yutgame.controller;

import java.util.*;
import java.util.stream.Collectors;

import yutgame.model.GameModel;
import yutgame.model.Piece;
import yutgame.view.IGameView;

/**
 * 윷 던지기 버튼과 말 이동을 제어하는 컨트롤러입니다.
 */
public class YutThrowController {
    private final IGameView view;
    private final GameModel model;
    private final GameController controller;

    // 대기 중인 던지기 결과 리스트
    private final List<Integer> pending = new ArrayList<>();
    // 결과를 누른 뒤에만 말을 움직일 수 있도록 하는 플래그
    private boolean awaitingMove = false;
    // 같은 칸에서 뭉친 그룹을 턴 전체에 걸쳐 추적
    private final Set<String> groupedKeys = new HashSet<>();

    public YutThrowController(IGameView view, GameModel model, GameController controller) {
        this.view       = view;
        this.model      = model;
        this.controller = controller;

        // 1) 랜덤 던지기
        view.onThrow(() -> {
            pending.clear();
            int r;
            do {
                r = model.throwYut();
                pending.add(r);
            } while (r == 4 || r == 5);
            view.showPendingThrows(pending);
            view.disableThrow();
            awaitingMove = false;
        });

        // 2) 지정 던지기
        view.onFixedThrow(code -> {
            pending.clear();
            pending.add(code);
            view.showPendingThrows(pending);
            view.disableThrow();
            awaitingMove = false;
        });

        // 3) 결과 적용
        view.onApplyThrow(code -> {
            if (awaitingMove) return;
            if (!pending.remove((Integer)code)) return;

            model.throwYut(code);
            view.showResult(code);

            // 빽도 특별 처리
            boolean allAtHome = model.getCurrentPlayer().getPieces()
                .stream().allMatch(p -> p.getPosition() == 0);
            if (code == 0 && allAtHome) {
                pending.clear();
                controller.nextTurn();
                view.enableThrow();
                return;
            }

            // 이동 가능 검사
            List<Piece> movable = model.getMovablePieces();
            boolean hasMove = movable.stream()
                .anyMatch(p -> !(code == 0 && p.getPosition() == 0));
            if (!hasMove) {
                if (pending.isEmpty()) {
                    controller.nextTurn();
                    view.enableThrow();
                } else {
                    view.showPendingThrows(pending);
                }
                return;
            }

            awaitingMove = true;
            view.highlightMoves(movable);
            view.showPendingThrows(pending);
        });

        // 4) 말 선택
        view.onPieceSelected(piece -> {
            if (!awaitingMove) return;

            // 내 말인지 체크
            String owner = model.getCurrentPlayer().getId();
            if (!piece.getId().startsWith(owner + "_")) return;

            int startPos = piece.getPosition();
            String key   = owner + "@" + startPos;
            List<Piece> cell = model.getPiecePositions().stream()
                .filter(p -> p.getPosition() == startPos)
                .filter(p -> p.getId().startsWith(owner + "_"))
                .collect(Collectors.toList());

            List<Piece> toMove;
            if (groupedKeys.contains(key)) {
                toMove = cell;
            } else if (startPos != 0 && cell.size() >= 2) {
                groupedKeys.add(key);
                toMove = cell;
            } else {
                toMove = List.of(piece);
            }

            // 이동
            toMove.forEach(model::movePiece);
            int dest = toMove.get(0).getPosition();

            // 포획 처리
            List<Piece> captured = new ArrayList<>();
            model.getPiecePositions().stream()
                .filter(p -> p.getPosition() == dest)
                .filter(p -> !p.getId().startsWith(owner + "_"))
                .forEach(p -> {
                    captured.add(p);
                    p.setPosition(0);
                });

            // 뷰 갱신
            view.refreshBoard(model.getPiecePositions());
            view.refreshInventory(model.getPiecePositions());
            view.updateScore(0);

            awaitingMove = false;

            if (!captured.isEmpty()) {
                // 포획했으면 추가 턴!
                groupedKeys.clear();
                pending.clear();
                view.showPendingThrows(pending);
                view.enableThrow();
                return;
            }

            // 남은 pending 없으면 턴 종료
            if (pending.isEmpty()) {
                groupedKeys.clear();
                controller.nextTurn();
                view.enableThrow();
            }
        });

        // 초기 상태
        view.enableThrow();
    }
}
