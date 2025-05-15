// src/yutgame/view/SettingView.java
package yutgame.view;

import yutgame.model.BoardShape;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class SettingView extends JFrame {
    private final JSpinner spnPlayers;
    private final JSpinner spnPieces;
    private final JComboBox<BoardShape> cmbShape;
    private final JButton btnStart;

    public SettingView() {
        super("게임 설정");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(400, 320);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(12, 12, 12, 12);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1.0;
        c.anchor = GridBagConstraints.CENTER;

        Font labelFont = new Font("맑은 고딕", Font.PLAIN, 16);
        Font controlFont = new Font("맑은 고딕", Font.PLAIN, 14);

        // 플레이어 수
        c.gridy = 0; c.gridx = 0; c.gridwidth = 1;
        JLabel lblPlayers = new JLabel("플레이어 수:");
        lblPlayers.setFont(labelFont);
        lblPlayers.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(lblPlayers, c);

        spnPlayers = new JSpinner(new SpinnerNumberModel(2, 2, 4, 1));
        spnPlayers.setFont(controlFont);
        spnPlayers.setPreferredSize(new Dimension(80, 30));
        c.gridx = 1;
        panel.add(spnPlayers, c);

        // 말 개수
        c.gridy = 1; c.gridx = 0;
        JLabel lblPieces = new JLabel("말의 개수:");
        lblPieces.setFont(labelFont);
        lblPieces.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(lblPieces, c);

        spnPieces = new JSpinner(new SpinnerNumberModel(2, 2, 5, 1));
        spnPieces.setFont(controlFont);
        spnPieces.setPreferredSize(new Dimension(80, 30));
        c.gridx = 1;
        panel.add(spnPieces, c);

        // 판 모양
        c.gridy = 2; c.gridx = 0;
        JLabel lblShape = new JLabel("판 모양:");
        lblShape.setFont(labelFont);
        lblShape.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(lblShape, c);

        cmbShape = new JComboBox<>(BoardShape.values());
        cmbShape.setFont(controlFont);
        cmbShape.setPreferredSize(new Dimension(140, 30));
        c.gridx = 1;
        panel.add(cmbShape, c);

        // 시작 버튼 (가로 두 칸 차지)
        btnStart = new JButton("게임 시작");
        btnStart.setFont(new Font("맑은 고딕", Font.BOLD, 16));
        btnStart.setPreferredSize(new Dimension(160, 40));
        c.gridy = 3; c.gridx = 0; c.gridwidth = 2;
        panel.add(btnStart, c);

        setContentPane(panel);
        setVisible(true);
    }

    public int getNumPlayers() {
        return (int) spnPlayers.getValue();
    }
    public int getPiecesPerPlayer() {
        return (int) spnPieces.getValue();
    }
    public BoardShape getBoardShape() {
        return (BoardShape) cmbShape.getSelectedItem();
    }
    public void addStartButtonListener(ActionListener l) {
        btnStart.addActionListener(l);
    }
}
