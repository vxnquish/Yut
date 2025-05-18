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
import java.util.function.IntConsumer;
import java.util.function.Consumer;

public class SwingGameView implements IGameView {
    private final JFrame frame;
    private final AbstractBoardView boardView;
    private final JPanel boardWrapper, inventoryPanel, fixedPanel, pendingPanel;
    private final JButton throwButton;
    private final YutResultView resultView;
    private final PlayerInfoView infoView;
    private final List<Player> players;
    private final Map<Player, JLabel> scoreLabels = new HashMap<>();

    private Runnable throwCallback;
    private IntConsumer fixedThrowCallback;
    private IntConsumer applyThrowCallback;
    private Consumer<Piece> pieceSelectedCallback;

    public SwingGameView(SettingModel config, List<Player> players) {
        this.players = players;

        // Frame
        frame = new JFrame("윷놀이 게임");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1280, 720);
        frame.setResizable(false);   
        frame.setLocationRelativeTo(null);
        frame.getContentPane().setBackground(Color.WHITE);

        // BoardView
        boardView = BoardViewFactory.create(config.getBoardShape(), config);
        boardView.setPreferredSize(new Dimension(500, 500));
        boardWrapper = new JPanel(new GridBagLayout());
        boardWrapper.setBackground(Color.WHITE);
        boardWrapper.add(boardView);
        boardView.addPieceClickListener(p -> {
            if (pieceSelectedCallback != null) pieceSelectedCallback.accept(p);
        });

        // Inventory
        inventoryPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        inventoryPanel.setBackground(Color.WHITE);
        inventoryPanel.setBorder(BorderFactory.createTitledBorder("인벤토리"));

        // Controls
        infoView = new PlayerInfoView(players);
        infoView.setAlignmentX(Component.CENTER_ALIGNMENT);

        // 랜덤 던지기
        throwButton = new JButton("랜덤 윷 던지기");
        throwButton.setPreferredSize(new Dimension(200, 60));
        throwButton.setFont(new Font("맑은 고딕", Font.BOLD, 16));
        throwButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        throwButton.addActionListener(e -> {
            if (throwCallback != null) throwCallback.run();
        });

        // 지정 던지기
        fixedPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 5));
        fixedPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        String[] names = { "빽도", "도", "개", "걸", "윷", "모" };
        for (int i = 0; i < 6; i++) {
            int code = i;
            JButton b = new JButton(names[i]);
            b.setPreferredSize(new Dimension(60, 40));
            b.setAlignmentX(Component.CENTER_ALIGNMENT);
            b.addActionListener(e -> {
                if (fixedThrowCallback != null) fixedThrowCallback.accept(code);
            });
            fixedPanel.add(b);
        }

        // pending 표시
        pendingPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 5));
        pendingPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // 결과뷰
        resultView = new YutResultView();
        resultView.setAlignmentX(Component.CENTER_ALIGNMENT);

        // 점수판
        JPanel scorePanel = new JPanel(new GridLayout(1, players.size(), 10, 0));
        scorePanel.setBackground(Color.WHITE);
        scorePanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        for (Player p : players) {
            JLabel lbl = new JLabel(p.getId() + ": 0", SwingConstants.CENTER);
            lbl.setFont(new Font("맑은 고딕", Font.PLAIN, 14));
            lbl.setAlignmentX(Component.CENTER_ALIGNMENT);
            scoreLabels.put(p, lbl);
            scorePanel.add(lbl);
        }

        // 컨트롤 패널
        JPanel ctrl = new JPanel();
        ctrl.setBackground(Color.WHITE);
        ctrl.setLayout(new BoxLayout(ctrl, BoxLayout.Y_AXIS));
        ctrl.setAlignmentX(Component.CENTER_ALIGNMENT);

        ctrl.add(infoView);
        ctrl.add(Box.createVerticalStrut(10));

        JPanel throwRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
        throwRow.setBackground(Color.WHITE);
        throwRow.setAlignmentX(Component.CENTER_ALIGNMENT);
        throwRow.add(throwButton);
        throwRow.add(fixedPanel);
        ctrl.add(throwRow);

        ctrl.add(Box.createVerticalStrut(10));
        ctrl.add(pendingPanel);
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

    // IGameView 구현
    @Override public void show()                                   { frame.setVisible(true); }
    @Override public void hide()                                   { frame.dispose(); }
    @Override public void onThrow(Runnable r)                      { this.throwCallback = r; }
    @Override public void onFixedThrow(IntConsumer c)              { this.fixedThrowCallback = c; }
    @Override public void onApplyThrow(IntConsumer c)              { this.applyThrowCallback = c; }
    @Override public void onPieceSelected(Consumer<Piece> c)       { this.pieceSelectedCallback = c; }
    @Override public void refreshBoard(List<Piece> pcs)            { boardView.refresh(pcs); }
    @Override
    public void refreshInventory(List<Piece> onBoard) {
        inventoryPanel.removeAll();
        for (Player p : players) {
            p.getPieces().stream()
             // position == 0 인 말은 전부 인벤토리에 표시
             .filter(pc -> pc.getPosition() == 0)
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
    @Override public void showResult(int result)                   { resultView.showResult(result); }
    @Override public void showPendingThrows(List<Integer> pend) {
        pendingPanel.removeAll();
        for (Integer code : pend) {
            String name = switch (code) {
                case 0 -> "빽도"; case 1 -> "도"; case 2 -> "개";
                case 3 -> "걸"; case 4 -> "윷"; case 5 -> "모";
                default-> String.valueOf(code);
            };
            JButton b = new JButton(name);
            b.setPreferredSize(new Dimension(60, 40));
            b.setAlignmentX(Component.CENTER_ALIGNMENT);
            b.addActionListener(e -> applyThrowCallback.accept(code));
            pendingPanel.add(b);
        }
        pendingPanel.revalidate();
        pendingPanel.repaint();
    }
    @Override public void highlightMoves(List<Piece> mv)           { boardView.highlightMoves(mv); }
    @Override public void updateTurn(Player cur)                   { infoView.updateCurrentPlayer(cur); }
    @Override public void updateScore(int ignored)                 {
        players.forEach(p-> scoreLabels.get(p).setText(p.getId()+": "+p.getScore()));
    }
    @Override public void disableThrow()                           { throwButton.setEnabled(false); }
    @Override public void enableThrow()                            { throwButton.setEnabled(true); }
    @Override public void showGameOver(String winnerId, Runnable onRestart, Runnable onExit) {
        int ans = JOptionPane.showOptionDialog(
            frame,
            winnerId + "님이 승리했습니다!\n재시작하시겠습니까?",
            "게임 종료",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.INFORMATION_MESSAGE,
            null,
            new String[]{ "재시작", "종료" },
            "재시작"
        );
        if (ans == JOptionPane.YES_OPTION) onRestart.run();
        else                                onExit.run();
    }
}
