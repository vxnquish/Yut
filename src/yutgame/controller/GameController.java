// src/yutgame/controller/GameController.java
package yutgame.controller;

import yutgame.model.GameModel;
import yutgame.model.Player;
import yutgame.model.SettingModel;
import yutgame.view.IGameView;
import yutgame.view.SwingGameView;

public class GameController {
    private final GameModel model;
    private final IGameView view;
    private final YutThrowController throwController;
    private final SettingController settingCtrl;

    /**
     * @param settingCtrl   재시작 시 세팅창으로 복귀하기 위해 넘겨받습니다.
     * @param config        사용자가 지정한 설정 모델
     */
    public GameController(SettingController settingCtrl, SettingModel config) {
        this.settingCtrl = settingCtrl;

        // 1) 모델 초기화
        this.model = new GameModel(config);

        // 2) 뷰 초기화
        this.view = new SwingGameView(config, model.getPlayers());
        view.show();
        view.refreshBoard(model.getPiecePositions());
        view.refreshInventory(model.getPiecePositions());
        view.updateTurn(model.getCurrentPlayer());

        // 3) 던지기 컨트롤러 연결
        this.throwController = new YutThrowController(view, model, this);
    }

    /**
     * YutThrowController 에서 턴을 넘기라고 호출될 때마다 수행됩니다.
     * 게임이 종료되었으면 우승자 다이얼로그, 아니면 다음 플레이어로.
     */
    public void nextTurn() {
        if (model.isGameOver()) {
            Player winner = model.getWinner();
            view.showGameOver(
                winner.getId(),
                // 재시작 콜백
                () -> {
                    view.hide();
                    settingCtrl.show();
                },
                // 종료 콜백
                () -> System.exit(0)
            );
        } else {
            model.nextTurn();
            view.updateTurn(model.getCurrentPlayer());
        }
    }
}
