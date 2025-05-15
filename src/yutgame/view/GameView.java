// src/yutgame/view/GameView.java
package yutgame.view;

import yutgame.model.SettingModel;
import yutgame.model.Player;
import yutgame.model.Piece;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GameView extends JFrame {
    private final AbstractBoardView boardView;
    private final JButton throwButton;
    private final YutResultView resultView;
    private final PlayerInfoView infoView;
    private final JLabel scoreLabel;
    private final JPanel inventoryPanel;
    private final Map<Player, JPanel> invSubPanels = new HashMap<>();

    public GameView(SettingModel config, List<Player> players) {
        super("윷놀이 게임");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1280, 720);
        setResizable(false);
        setLocationRelativeTo(null);

        getContentPane().setBackground(Color.WHITE);
        setLayout(new BorderLayout(20, 0));  // EAST-WEST 사이 20px 간격

        // ── 보드 뷰 (CENTER) ───────────────────────────────────────
        boardView = BoardViewFactory.create(config.getBoardShape(), config);
        boardView.setPreferredSize(new Dimension(500, 500));
        JPanel boardWrapper = new JPanel(new GridBagLayout());
        boardWrapper.setBackground(Color.WHITE);
        boardWrapper.add(boardView);
        add(boardWrapper, BorderLayout.CENTER);

        // ── 오른쪽: 인벤토리 + 컨트롤 (EAST) ────────────────────────
        // 인벤토리 (각 플레이어 남은 말)
        inventoryPanel = new JPanel();
        inventoryPanel.setBackground(Color.WHITE);
        inventoryPanel.setLayout(new BoxLayout(inventoryPanel, BoxLayout.Y_AXIS));
        inventoryPanel.setBorder(new EmptyBorder(20, 10, 20, 10));
        for (Player p : players) {
            JPanel pPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 5));
            pPanel.setBackground(Color.WHITE);
            pPanel.setBorder(BorderFactory.createTitledBorder(p.getId()));
            invSubPanels.put(p, pPanel);
            inventoryPanel.add(pPanel);
        }

        // 컨트롤
        infoView    = new PlayerInfoView(players);
        infoView.setBackground(Color.WHITE);
        infoView.setOpaque(true);

        throwButton = new JButton("윷 던지기");
        throwButton.setPreferredSize(new Dimension(140, 50));

        resultView  = new YutResultView();
        resultView.setBackground(Color.WHITE);
        resultView.setOpaque(true);

        scoreLabel = new JLabel("골인: 0");
        scoreLabel.setFont(new Font("맑은 고딕", Font.BOLD, 16));
        scoreLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPanel ctrl = new JPanel();
        ctrl.setBackground(Color.WHITE);
        ctrl.setLayout(new BoxLayout(ctrl, BoxLayout.Y_AXIS));
        ctrl.setBorder(new EmptyBorder(20, 10, 20, 10));
        ctrl.add(infoView);
        ctrl.add(Box.createVerticalStrut(15));
        ctrl.add(throwButton);
        ctrl.add(Box.createVerticalStrut(15));
        ctrl.add(resultView);
        ctrl.add(Box.createVerticalStrut(15));
        ctrl.add(scoreLabel);

        JPanel east = new JPanel();
        east.setBackground(Color.WHITE);
        east.setLayout(new BoxLayout(east, BoxLayout.Y_AXIS));
        east.add(inventoryPanel);
        east.add(ctrl);
        add(east, BorderLayout.EAST);

        // 초기 인벤토리 채우기
        updateInventory(Collections.emptyList(), players);
    }

    public void updateInventory(List<Piece> onBoard, List<Player> players) {
        for (Player p : players) {
            JPanel panel = invSubPanels.get(p);
            panel.removeAll();
            for (Piece pc : p.getPieces()) {
                if (!onBoard.contains(pc)) {
                    panel.add(new JLabel(pc.getId()));
                }
            }
            panel.revalidate();
            panel.repaint();
        }
    }

    public void updateScore(int count) {
        scoreLabel.setText("골인: " + count);
    }

    public AbstractBoardView getBoardView()  { return boardView; }
    public JButton getThrowButton()          { return throwButton; }
    public YutResultView getResultView()     { return resultView; }
    public void updateTurn(Player cur)       { infoView.updateCurrentPlayer(cur); }
}
