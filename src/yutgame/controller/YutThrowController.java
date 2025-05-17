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
    // 마지막으로 윷 던진 후 이동할 수 있는 말 목록
    private List<Piece> lastMovable = List.of();
    // "<ownerId>@<startPos>" 로 그룹화된 키를 기록
    private final Set<String> groupedKeys = new HashSet<>();

    public YutThrowController(IGameView view, GameModel model, GameController controller) {
        this.view       = view;
        this.model      = model;
        this.controller = controller;

        // 1) 윷 던지기 이벤트 등록
        view.onThrow(() -> {
            int res = model.throwYut();
            view.showResult(res);

            // 윷·모가 아니면 버튼 잠그기
            if (res != 4 && res != 5) {
                view.disableThrow();
            }

            // 이동 가능 말 계산
            lastMovable = model.getMovablePieces();
            boolean hasMove = lastMovable.stream()
                .anyMatch(p -> !(res == 0 && p.getPosition() == 0));
            if (!hasMove) {
                // 이동할 말이 없으면 바로 턴 넘기고 버튼 다시 활성화
                controller.nextTurn();
                view.enableThrow();
            }
        });

        // 2) 말 선택 이벤트 (인벤토리·보드 공통)
        view.onPieceSelected(this::handleMove);
    }

    private void handleMove(Piece piece) {
        // 던진 뒤 이동 가능하지 않으면 무시
        if (!lastMovable.contains(piece)) return;

        int startPos = piece.getPosition();
        String ownerId = piece.getId().split("_")[0];
        String key = ownerId + "@" + startPos;

        // 같은 칸, 같은 플레이어 소유의 말들
        List<Piece> cellPieces = model.getPiecePositions().stream()
            .filter(p -> p.getPosition() == startPos)
            .filter(p -> p.getId().startsWith(ownerId + "_"))
            .collect(Collectors.toList());

        // 그룹화 로직
        List<Piece> toMove;
        if (groupedKeys.contains(key)) {
            // 이미 그룹에 속해 있으면 전부 이동
            toMove = cellPieces;
        } else if (startPos != 0 && cellPieces.size() >= 2) {
            // 처음 두 개 이상 뭉쳤을 때 그룹에 등록
            groupedKeys.add(key);
            toMove = cellPieces;
        } else {
            // 그 외엔 클릭한 말 하나만 이동
            toMove = List.of(piece);
        }

        // 이동 처리
        toMove.forEach(model::movePiece);
        int dest = toMove.get(0).getPosition();

        // 상대 말 포획: dest 에 상대말이 있으면 출발점으로
        model.getPiecePositions().stream()
            .filter(p -> p.getPosition() == dest)
            .filter(p -> !p.getId().startsWith(ownerId + "_"))
            .forEach(p -> p.setPosition(0));

        // 뷰 갱신
        view.refreshBoard(model.getPiecePositions());
        view.refreshInventory(model.getPiecePositions());
        view.updateScore(0);  // SwingGameView 쪽에서 실제 score를 읽어 옵니다

        // extraTurn 여부에 따라 차례 유지 or 넘기기
        if (model.isTurnOver()) {
            // 추가 턴 없음 → 다음 턴으로
            controller.nextTurn();
        }
        // 윷(4)·모(5) 였으면 같은 플레이어가 또 던지기 가능

        // 버튼 다시 활성화
        view.enableThrow();
    }
}
