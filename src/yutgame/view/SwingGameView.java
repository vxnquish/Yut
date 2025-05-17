// src/yutgame/view/SwingGameView.java
package yutgame.view;

import yutgame.model.Player;
import yutgame.model.Piece;
import yutgame.model.SettingModel;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class SwingGameView implements IGameView {
    private final JFrame frame;
    private final AbstractBoardView boardView;
    private final JPanel boardWrapper;
    private final JPanel inventoryPanel;
    private final JButton throwButton;
    private final YutResultView resultView;
    private final PlayerInfoView infoView;
    private final List<Player> players;

    private final Map<Player, JLabel> scoreLabels = new HashMap<>();

    private Runnable throwCallback;
    private Consumer<Integer> fixedThrowCallback; // ← 추가
    private Consumer<Piece> pieceSelectedCallback;

    public SwingGameView(SettingModel config, List<Player> players) {
        this.players = players;

        frame = new JFrame("윷놀이 게임");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1280, 720);
        frame.setLocationRelativeTo(null);
        frame.getContentPane().setBackground(Color.WHITE);

        // ── BoardView ─────────────────────────
        boardView = BoardViewFactory.create(config.getBoardShape(), config);
        boardView.setPreferredSize(new Dimension(500, 500));
        boardWrapper = new JPanel(new GridBagLayout());
        boardWrapper.setBackground(Color.WHITE);
        boardWrapper.add(boardView);
        boardView.addPieceClickListener(p -> {
            if (pieceSelectedCallback != null) pieceSelectedCallback.accept(p);
        });

        // ── Inventory ─────────────────────────
        inventoryPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        inventoryPanel.setBackground(Color.WHITE);
        inventoryPanel.setBorder(BorderFactory.createTitledBorder("인벤토리"));

        // ── 컨트롤 패널 ────────────────────────
        infoView = new PlayerInfoView(players);
        throwButton = new JButton("랜덤 윷 던지기");
        throwButton.setPreferredSize(new Dimension(200, 60));
        throwButton.setFont(new Font("맑은 고딕", Font.BOLD, 16));
        throwButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        throwButton.addActionListener(e -> {
            if (throwCallback != null) throwCallback.run();
        });

        // 고정 던지기 버튼 6개
        JPanel fixedPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 5));
        String[] names = { "빽도", "도", "개", "걸", "윷", "모" };
        for (int i = 0; i < 6; i++) {
            int code = i;
            JButton b = new JButton(names[i]);
            b.setPreferredSize(new Dimension(60, 40));
            b.addActionListener(e -> {
                if (fixedThrowCallback != null) fixedThrowCallback.accept(code);
            });
            fixedPanel.add(b);
        }

        resultView = new YutResultView();
        resultView.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPanel scorePanel = new JPanel(new GridLayout(1, players.size(), 10, 0));
        scorePanel.setBackground(Color.WHITE);
        for (Player p : players) {
            JLabel lbl = new JLabel(p.getId() + ": 0", SwingConstants.CENTER);
            lbl.setFont(new Font("맑은 고딕", Font.PLAIN, 14));
            scoreLabels.put(p, lbl);
            scorePanel.add(lbl);
        }
        scorePanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPanel ctrl = new JPanel();
        ctrl.setBackground(Color.WHITE);
        ctrl.setLayout(new BoxLayout(ctrl, BoxLayout.Y_AXIS));
        ctrl.add(infoView);
        ctrl.add(Box.createVerticalStrut(10));
        ctrl.add(throwButton);
        ctrl.add(Box.createVerticalStrut(5));
        ctrl.add(fixedPanel);            // ← 고정 던지기 버튼들
        ctrl.add(Box.createVerticalStrut(10));
        ctrl.add(resultView);
        ctrl.add(Box.createVerticalStrut(10));
        ctrl.add(scorePanel);

        JPanel rightPane = new JPanel();
        rightPane.setBackground(Color.WHITE);
        rightPane.setLayout(new BoxLayout(rightPane, BoxLayout.Y_AXIS));
        rightPane.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        rightPane.add(inventoryPanel);
        rightPane.add(Box.createVerticalStrut(15));
        rightPane.add(ctrl);

        JSplitPane split = new JSplitPane(
            JSplitPane.HORIZONTAL_SPLIT,
            boardWrapper,
            rightPane
        );
        split.setDividerLocation(540);
        split.setEnabled(false);

        frame.getContentPane().add(split);
    }

    @Override public void show()                        { frame.setVisible(true); }
    @Override public void onThrow(Runnable r)           { this.throwCallback = r; }
    @Override public void onFixedThrow(Consumer<Integer> c) { this.fixedThrowCallback = c; } // ← 구현
    @Override public void onPieceSelected(Consumer<Piece> c) { this.pieceSelectedCallback = c; }
    @Override public void showResult(int result)        { resultView.showResult(result); }
    @Override public void refreshBoard(List<Piece> pcs) { boardView.refresh(pcs); }

    @Override
    public void refreshInventory(List<Piece> onBoard) {
        inventoryPanel.removeAll();
        for (Player p : players) {
            // position==0 이면서 한 번도 움직이지 않은 말만
            p.getPieces().stream()
             .filter(pc -> pc.getPosition() == 0 && !pc.hasMoved())
             .forEach(pc -> {
                String path = "/yutgame/img/piece_" + p.getId() + "_0.png";
                ImageIcon ico = new ImageIcon(getClass().getResource(path));
                Image img = ico.getImage()
                               .getScaledInstance(40, 40, Image.SCALE_SMOOTH);
                JButton btn = new JButton(new ImageIcon(img));
                btn.setBorder(BorderFactory.createEmptyBorder());
                btn.setContentAreaFilled(false);
                btn.setPreferredSize(new Dimension(50, 50));
                btn.addActionListener(e -> {
                    if (pieceSelectedCallback != null)
                        pieceSelectedCallback.accept(pc);
                });
                inventoryPanel.add(btn);
             });
        }
        inventoryPanel.revalidate();
        inventoryPanel.repaint();
    }

    @Override public void updateTurn(Player cur)          { infoView.updateCurrentPlayer(cur); }
    @Override public void updateScore(int ignored)        {
        players.forEach(p-> scoreLabels.get(p)
            .setText(p.getId() + ": " + p.getScore()));
    }
    @Override public void disableThrow()                  { throwButton.setEnabled(false); }
    @Override public void enableThrow()                   { throwButton.setEnabled(true); }
}
