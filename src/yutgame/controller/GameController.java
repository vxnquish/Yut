// src/yutgame/controller/GameController.java
package yutgame.controller;

// import java.util.List;
import yutgame.model.*;
import yutgame.view.*;

/**
 * 게임의 메인 컨트롤러입니다.
 * 모델과 뷰를 연결하고, 턴 로직을 처리합니다.
 */
public class GameController {
    private final GameModel model;
    private final IGameView view;
    private final YutThrowController throwController;

    public GameController(SettingModel config) {
        // 모델 초기화
        this.model = new GameModel(config);

        // SwingGameView를 IGameView로 생성 및 보여주기
        this.view = new SwingGameView(config, model.getPlayers());
        view.show();

        // 초기 화면 세팅
        view.refreshBoard(model.getPiecePositions());
        view.refreshInventory(model.getPiecePositions());
        view.updateTurn(model.getCurrentPlayer());

        // 윷 던지기 컨트롤러 연결
        this.throwController = new YutThrowController(view, model, this);
    }

    /** 다음 턴으로 넘기고 뷰 업데이트 */
    public void nextTurn() {
        model.nextTurn();
        view.updateTurn(model.getCurrentPlayer());
    }
}
