package yutgame.controller;

import java.util.Collections;
import java.util.List;
import yutgame.model.SettingModel;
import yutgame.model.GameModel;
import yutgame.model.Player;
import yutgame.view.GameView;
import yutgame.view.AbstractBoardView;

public class GameController {
    private final GameModel model;
    private final GameView view;
    private final YutThrowController throwController;

    public GameController(SettingModel config) {
        // 1) 모델 초기화
        this.model = new GameModel(config);

        // 2) 뷰 초기화 (Setting + 플레이어 리스트)
        List<Player> players = model.getPlayers();
        this.view = new GameView(config, players);
        view.setVisible(true);

        // 3) 서브 컨트롤러 연결 (GameView 까지 전달!)
        this.throwController = new YutThrowController(
            view.getThrowButton(),
            view.getResultView(),
            model,
            this,
            view,
            view.getBoardView()
        );

        // 4) 초기 인벤토리 세팅 (모두 position==0)
        view.updateInventory(Collections.emptyList(), players);
    }

    /** 턴 넘기기 */
    public void nextTurn() {
        model.nextTurn();
        view.updateTurn(model.getCurrentPlayer());
    }
}
