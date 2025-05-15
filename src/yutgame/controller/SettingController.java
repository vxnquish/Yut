package yutgame.controller;

import yutgame.model.SettingModel;
import yutgame.view.SettingView;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class SettingController {
    private final SettingView view;
    private final SettingModel config;

    public SettingController() {
        this.view = new SettingView();
        this.config = new SettingModel();

        view.addStartButtonListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // 1) 설정값 저장
                config.setNumPlayers(view.getNumPlayers());
                config.setPiecesPerPlayer(view.getPiecesPerPlayer());
                config.setBoardShape(view.getBoardShape());

                // 2) 설정 창 닫기
                view.dispose();

                // 3) 게임 컨트롤러 시작
                new GameController(config);
            }
        });

        view.setVisible(true);
    }
}