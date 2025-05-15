// src/yutgame/controller/GameController.java
package yutgame.controller;

import java.util.List;
import yutgame.model.SettingModel;
import yutgame.model.GameModel;
import yutgame.model.Player;
import yutgame.view.GameView;

public class GameController {
    private final GameModel model;
    private final GameView view;
    private final YutThrowController throwController;

    public GameController(SettingModel config) {
        // 1) 모델 초기화
        this.model = new GameModel(config);

        // 2) 뷰 초기화: 설정 모델 + 플레이어 리스트 전달
        List<Player> players = model.getPlayers();
        this.view = new GameView(config, players);
        view.setVisible(true);

        // 3) 서브 컨트롤러 연결
        this.throwController = new YutThrowController(
            view.getThrowButton(),
            view.getResultView(),
            model,
            this,
            view.getBoardView()
        );
    }

    public void nextTurn() {
        model.nextTurn();
        view.updateTurn(model.getCurrentPlayer());
    }
}
