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

    // 각 플레이어별 골인 점수 라벨
    private final Map<Player, JLabel> scoreLabels = new HashMap<>();

    // 콜백
    private Runnable throwCallback;
    private Consumer<Piece> pieceSelectedCallback;

    public SwingGameView(SettingModel config, List<Player> players) {
        this.players = players;

        // ── Frame 세팅 ─────────────────────────────────────
        frame = new JFrame("윷놀이 게임");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1280, 720);
        frame.setLocationRelativeTo(null);
        frame.getContentPane().setBackground(Color.WHITE);

        // ── BoardView ──────────────────────────────────────
        boardView = BoardViewFactory.create(config.getBoardShape(), config);
        boardView.setPreferredSize(new Dimension(500, 500));
        boardWrapper = new JPanel(new GridBagLayout());
        boardWrapper.setBackground(Color.WHITE);
        boardWrapper.add(boardView);

        // 판 위 클릭 → 선택 콜백
        boardView.addPieceClickListener(p -> {
            if (pieceSelectedCallback != null) pieceSelectedCallback.accept(p);
        });

        // ── Inventory ──────────────────────────────────────
        inventoryPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        inventoryPanel.setBackground(Color.WHITE);
        inventoryPanel.setBorder(BorderFactory.createTitledBorder("인벤토리"));

        // ── 정보 / 던지기 / 결과 / 점수 ────────────────────────
        // (1) 차례 표시 (작게)
        infoView = new PlayerInfoView(players) {{
            for (Component c : getComponents()) {
                if (c instanceof JLabel l) {
                    l.setFont(l.getFont().deriveFont(12f));
                    l.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
                }
            }
            setAlignmentX(Component.CENTER_ALIGNMENT);
        }};

        // (2) 윷 던지기 버튼 크게 + 중앙 정렬
        throwButton = new JButton("윷 던지기");
        throwButton.setPreferredSize(new Dimension(200, 60));
        throwButton.setMaximumSize(new Dimension(200, 60));
        throwButton.setFont(new Font("맑은 고딕", Font.BOLD, 16));
        throwButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        throwButton.addActionListener(e -> {
            if (throwCallback != null) throwCallback.run();
        });

        // (3) 결과뷰 폰트 작게 + 중앙 정렬
        resultView = new YutResultView() {{
            for (Component c : getComponents()) {
                if (c instanceof JLabel l) {
                    l.setFont(new Font(l.getFont().getName(), Font.BOLD, 14));
                }
            }
            setAlignmentX(Component.CENTER_ALIGNMENT);
        }};

        // (4) 각 플레이어별 골인 점수 패널
        JPanel scorePanel = new JPanel(new GridLayout(1, players.size(), 10, 0));
        scorePanel.setBackground(Color.WHITE);
        for (Player p : players) {
            JLabel lbl = new JLabel(p.getId() + ": 0", SwingConstants.CENTER);
            lbl.setFont(new Font("맑은 고딕", Font.PLAIN, 14));
            scoreLabels.put(p, lbl);
            scorePanel.add(lbl);
        }
        scorePanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // 컨트롤 패널 구성
        JPanel ctrl = new JPanel();
        ctrl.setBackground(Color.WHITE);
        ctrl.setLayout(new BoxLayout(ctrl, BoxLayout.Y_AXIS));
        ctrl.add(infoView);
        ctrl.add(Box.createVerticalStrut(8));
        ctrl.add(throwButton);
        ctrl.add(Box.createVerticalStrut(8));
        ctrl.add(resultView);
        ctrl.add(Box.createVerticalStrut(8));
        ctrl.add(scorePanel);

        // ── 우측 패널 ───────────────────────────────────────
        JPanel rightPane = new JPanel();
        rightPane.setBackground(Color.WHITE);
        rightPane.setLayout(new BoxLayout(rightPane, BoxLayout.Y_AXIS));
        rightPane.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        rightPane.add(inventoryPanel);
        rightPane.add(Box.createVerticalStrut(15));
        rightPane.add(ctrl);

        // ── SplitPane ──────────────────────────────────────
        JSplitPane split = new JSplitPane(
            JSplitPane.HORIZONTAL_SPLIT,
            boardWrapper,
            rightPane
        );
        split.setDividerLocation(540);
        split.setEnabled(false);

        frame.getContentPane().add(split);
    }

    // ■ IGameView 구현 ■

    /** 화면을 보여줍니다. */
    @Override
    public void show() {
        frame.setVisible(true);
    }

    /** 윷 던지기 콜백 등록 */
    @Override
    public void onThrow(Runnable r) {
        this.throwCallback = r;
    }

    /** 판 위 또는 인벤토리에서 말 선택 콜백 등록 */
    @Override
    public void onPieceSelected(Consumer<Piece> c) {
        this.pieceSelectedCallback = c;
    }

    /** 결과 텍스트 표시 */
    @Override
    public void showResult(int result) {
        resultView.showResult(result);
    }

    /** 보드 위의 말을 갱신합니다. */
    @Override
    public void refreshBoard(List<Piece> pieces) {
        boardView.refresh(pieces);
    }

    /** 인벤토리(포지션==0) 상태만 갱신합니다. */
    @Override
    public void refreshInventory(List<Piece> onBoard) {
        inventoryPanel.removeAll();
        for (Player p : players) {
            long cnt = p.getPieces().stream()
                        .filter(pc -> pc.getPosition() == 0)
                        .count();
            for (int i = 0; i < cnt; i++) {
                String path = "/yutgame/img/piece_" + p.getId() + "_0.png";
                ImageIcon ico = new ImageIcon(getClass().getResource(path));
                Image img = ico.getImage()
                               .getScaledInstance(40, 40, Image.SCALE_SMOOTH);
                JButton btn = new JButton(new ImageIcon(img));
                btn.setBorder(BorderFactory.createEmptyBorder());
                btn.setContentAreaFilled(false);
                btn.setPreferredSize(new Dimension(50, 50));
                btn.addActionListener(e -> {
                    p.getPieces().stream()
                     .filter(pc -> pc.getPosition() == 0)
                     .findFirst()
                     .ifPresent(pieceSelectedCallback);
                });
                inventoryPanel.add(btn);
            }
        }
        inventoryPanel.revalidate();
        inventoryPanel.repaint();
    }

    /** 현재 차례 플레이어 표시 */
    @Override
    public void updateTurn(Player current) {
        infoView.updateCurrentPlayer(current);
    }

    /** 골인 점수 표시 (각 player별) */
    @Override
    public void updateScore(int ignored) {
        for (Player p : players) {
            int sc = p.getScore();  // Player 클래스에 getScore() 구현 필요
            scoreLabels.get(p).setText(p.getId() + ": " + sc);
        }
    }
}
