// src/yutgame/view/GameView.java
package yutgame.view;

import yutgame.model.Piece;
import yutgame.model.Player;
import yutgame.model.SettingModel;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class GameView extends JFrame {
    private final AbstractBoardView boardView;
    private final JButton throwButton;
    private final YutResultView resultView;
    private final PlayerInfoView infoView;
    private final JLabel scoreLabel;
    private final JPanel inventoryPanel;
    private final Map<Piece, JButton> invButtons = new HashMap<>();
    private Consumer<Piece> inventoryClickListener;

    public GameView(SettingModel config, List<Player> players) {
        super("윷놀이 게임");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1280, 720);
        setResizable(false);
        setLocationRelativeTo(null);
        getContentPane().setBackground(Color.WHITE);
        setLayout(new BorderLayout(20, 0));

        // ── 보드
        boardView = BoardViewFactory.create(config.getBoardShape(), config);
        boardView.setPreferredSize(new Dimension(500, 500));
        JPanel wrap = new JPanel(new GridBagLayout());
        wrap.setBackground(Color.WHITE);
        wrap.add(boardView);
        add(wrap, BorderLayout.CENTER);

        // ── 오른쪽: 인벤토리 + 컨트롤
        JPanel east = new JPanel();
        east.setBackground(Color.WHITE);
        east.setLayout(new BoxLayout(east, BoxLayout.Y_AXIS));
        east.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 20));

        // 인벤토리 (position==0)
        inventoryPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        inventoryPanel.setBackground(Color.WHITE);
        inventoryPanel.setBorder(BorderFactory.createTitledBorder("인벤토리"));
        east.add(inventoryPanel);
        east.add(Box.createVerticalStrut(15));

        // 컨트롤
        infoView    = new PlayerInfoView(players);
        throwButton = new JButton("윷 던지기");
        throwButton.setPreferredSize(new Dimension(140, 50));
        resultView  = new YutResultView();
        scoreLabel  = new JLabel("골인: 0", SwingConstants.CENTER);
        scoreLabel.setFont(new Font("맑은 고딕", Font.BOLD, 16));

        JPanel ctrl = new JPanel();
        ctrl.setBackground(Color.WHITE);
        ctrl.setLayout(new BoxLayout(ctrl, BoxLayout.Y_AXIS));
        ctrl.add(infoView);
        ctrl.add(Box.createVerticalStrut(10));
        ctrl.add(throwButton);
        ctrl.add(Box.createVerticalStrut(10));
        ctrl.add(resultView);
        ctrl.add(Box.createVerticalStrut(10));
        ctrl.add(scoreLabel);

        east.add(ctrl);
        add(east, BorderLayout.EAST);
    }

    /** 인벤토리 업데이트: position==0 말들만 아이콘으로 표시 */
    public void updateInventory(List<Piece> onBoard, List<Player> players) {
        inventoryPanel.removeAll();
        invButtons.clear();

        for (Player p : players) {
            // 플레이어의 position==0 말 개수 계산
            long cnt = p.getPieces().stream()
                        .filter(pc -> pc.getPosition() == 0)
                        .count();
            // cnt 만큼 동일한 아이콘(piece_<playerId>_0.png) 표시
            for (int i = 0; i < cnt; i++) {
                String resource = "/yutgame/img/piece_" + p.getId() + "_0.png";
                ImageIcon ico = new ImageIcon(getClass().getResource(resource));
                Image img = ico.getImage()
                               .getScaledInstance(40, 40, Image.SCALE_SMOOTH);
                JButton b = new JButton(new ImageIcon(img));
                b.setPreferredSize(new Dimension(50, 50));
                b.setBorder(BorderFactory.createEmptyBorder());
                b.setContentAreaFilled(false);
                // 클릭 시 해당 플레이어의 첫 번째 position==0 말을 선택
                b.addActionListener(e -> {
                    if (inventoryClickListener != null) {
                        p.getPieces().stream()
                         .filter(pc -> pc.getPosition() == 0)
                         .findFirst()
                         .ifPresent(inventoryClickListener);
                    }
                });
                invButtons.put(null, b);
                inventoryPanel.add(b);
            }
        }

        inventoryPanel.revalidate();
        inventoryPanel.repaint();
    }

    /** 인벤토리 클릭 리스너 등록 */
    public void addInventoryClickListener(Consumer<Piece> listener) {
        this.inventoryClickListener = listener;
    }

    public AbstractBoardView getBoardView()  { return boardView; }
    public JButton           getThrowButton(){ return throwButton; }
    public YutResultView     getResultView() { return resultView; }
    public void updateTurn(Player cur)       { infoView.updateCurrentPlayer(cur); }
    public void updateScore(int c)           { scoreLabel.setText("골인: " + c); }
}
