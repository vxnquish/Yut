// src/yutgame/view/PlayerInfoView.java
package yutgame.view;

import yutgame.model.Player;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class PlayerInfoView extends JPanel {
    private final List<Player> players;
    private int currentIndex = -1;

    public PlayerInfoView(List<Player> players) {
        this.players = players;
        setLayout(new GridLayout(1, players.size(), 10, 0));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // 각 플레이어 ID를 라벨로 추가
        for (Player p : players) {
            JLabel lbl = new JLabel(p.getId(), SwingConstants.CENTER);
            lbl.setFont(new Font("맑은 고딕", Font.PLAIN, 14));
            lbl.setBorder(BorderFactory.createLineBorder(Color.GRAY));
            add(lbl);
        }
    }

    /** 
     * 현재 차례인 플레이어를 호출할 때마다 라벨을 강조합니다.
     */
    public void updateCurrentPlayer(Player current) {
        if (currentIndex != -1) {
            // 이전 강조 해제
            JLabel prev = (JLabel) getComponent(currentIndex);
            prev.setBorder(BorderFactory.createLineBorder(Color.GRAY));
            prev.setFont(new Font("맑은 고딕", Font.PLAIN, 14));
        }
        // 새로 강조
        int idx = players.indexOf(current);
        if (idx >= 0) {
            JLabel curr = (JLabel) getComponent(idx);
            curr.setBorder(BorderFactory.createLineBorder(Color.GREEN, 2));
            curr.setFont(new Font("맑은 고딕", Font.BOLD, 16));
            currentIndex = idx;
        }
    }
}
