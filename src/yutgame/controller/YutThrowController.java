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

        // 1) 랜덤 던지기: 윷(4)·모(5)가 나올 동안 계속 뽑아서 pending 에 저장
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

        // 2) 지정 던지기: 원하는 결과 하나만 pending
        view.onFixedThrow(code -> {
            pending.clear();
            pending.add(code);
            view.showPendingThrows(pending);
            view.disableThrow();
            awaitingMove = false;
        });

        // 3) 결과 적용 버튼: 누른 code 가 pending 에 있으면 소진하고 적용
        view.onApplyThrow(code -> {
            if (awaitingMove) return;                    // 이미 이동 대기 중이면 무시
            if (!pending.remove((Integer)code)) return;  // pending 에 없으면 무시

            // 모델에 lastThrow 세팅 & 결과 표시
            model.throwYut(code);
            view.showResult(code);

            // ◆ 빽도인 경우 ◆  
            //   - 내 모든 말이 인벤토리(0)일 때만 턴 건너뛰기
            boolean allAtHome = model.getCurrentPlayer().getPieces()
                .stream().allMatch(p -> p.getPosition() == 0);
            if (code == 0 && allAtHome) {
                pending.clear();
                controller.nextTurn();
                view.enableThrow();
                return;
            }

            // ◆ 이동할 말이 없으면 바로 다음 결과 혹은 턴 종료
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

            // ◆ 정상적으로 이동 대기 상태 ◆
            awaitingMove = true;
            view.highlightMoves(movable);
            view.showPendingThrows(pending);
        });

        // 4) 말 선택 이벤트: 반드시 awaitingMove==true 일 때만 이동
        view.onPieceSelected(piece -> {
            if (!awaitingMove) return;

            int startPos = piece.getPosition();
            String owner = piece.getId().split("_")[0];
            String key   = owner + "@" + startPos;

            // 같은 칸, 같은 플레이어 소유 말들
            List<Piece> cell = model.getPiecePositions().stream()
                .filter(p -> p.getPosition() == startPos)
                .filter(p -> p.getId().startsWith(owner + "_"))
                .collect(Collectors.toList());

            // 그룹화 로직: 이미 묶였으면 계속 묶음, 아니면 처음에만 묶음
            List<Piece> toMove;
            if (groupedKeys.contains(key)) {
                toMove = cell;
            } else if (startPos != 0 && cell.size() >= 2) {
                groupedKeys.add(key);
                toMove = cell;
            } else {
                toMove = List.of(piece);
            }

            // 4-1) 실제 이동
            toMove.forEach(p -> {
                model.movePiece(p);
                p.setHasMoved(true);  // 한 번이라도 움직였음을 표시
            });
            int dest = toMove.get(0).getPosition();

            // 4-2) 포획 처리: 도착지에 상대말이 있으면 인벤토리(0)로 복귀
            model.getPiecePositions().stream()
                .filter(p -> p.getPosition() == dest)
                .filter(p -> !p.getId().startsWith(owner + "_"))
                .forEach(p -> {
                    p.setPosition(0);
                    p.setHasMoved(false);   // 다시 “집” 상태로
                    // 혹시 묶음키에 남아있다면 제거
                    String capKey = p.getId().split("_")[0] + "@" + dest;
                    groupedKeys.remove(capKey);
                });

            // 4-3) 뷰 갱신
            view.refreshBoard(model.getPiecePositions());
            view.refreshInventory(model.getPiecePositions());
            view.updateScore(0);

            awaitingMove = false;

            // 4-4) pending 이 다 소진되면 턴 종료 및 던지기 버튼 재활성화
            if (pending.isEmpty()) {
                // 다음 턴으로 넘길 때 그룹 정보도 초기화
                groupedKeys.clear();
                controller.nextTurn();
                view.enableThrow();
            }
        });

        // 초기 상태: 던지기 버튼 활성화
        view.enableThrow();
    }
}
