// src/yutgame/view/PentagonBoardView.java
package yutgame.view;

import yutgame.model.Piece;
import yutgame.model.SettingModel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;
import java.util.function.Consumer;

public class PentagonBoardView extends AbstractBoardView {
    private final Image boardImage;
    private List<Piece> piecePositions = Collections.emptyList();
    private Consumer<Piece> clickListener;

    // 1080×1080 원본 기준 오각형 노드 좌표 예시
    private static final Map<Integer, Point> NODE_COORDS = Map.of(
        0, new Point(540,  60),
        1, new Point(900, 240),
        2, new Point(810, 600),
        3, new Point(270, 600),
        4, new Point(180, 240)
        // … 추가 좌표는 실제 오각형 경로에 따라 채워주세요 …
    );

    public PentagonBoardView(SettingModel config) {
        // 이 패널을 500×500 고정 크기로
        setPreferredSize(new Dimension(500, 500));
        setMinimumSize(getPreferredSize());
        setMaximumSize(getPreferredSize());
        setBackground(Color.WHITE);

        // 배경 이미지 로드
        boardImage = new ImageIcon("src/yutgame/img/Board5.png").getImage();

        // 말 클릭 위치 판별
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int x = e.getX(), y = e.getY();
                int sz = 500 / 12;
                for (Piece p : piecePositions) {
                    Point loc = calculateLocationRelative(p.getPosition());
                    Rectangle r = new Rectangle(loc.x - sz/2, loc.y - sz/2, sz, sz);
                    if (r.contains(x, y) && clickListener != null) {
                        clickListener.accept(p);
                        break;
                    }
                }
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        // 500×500 크기로 배경 그리기
        g.drawImage(boardImage, 0, 0, 500, 500, this);
        // 말 그리기
        int sz = 500 / 12;
        for (Piece p : piecePositions) {
            Image img = new ImageIcon("src/yutgame/img/piece_" + p.getId() + ".png")
                            .getImage();
            Point loc = calculateLocationRelative(p.getPosition());
            g.drawImage(img, loc.x - sz/2, loc.y - sz/2, sz, sz, this);
        }
    }

    @Override
    public void refresh(List<Piece> pieces) {
        piecePositions = new ArrayList<>(pieces);
        repaint();
    }

    @Override
    public void highlightMoves(List<Piece> movable) {
        // 강조 로직 필요 시 여기에 추가
        repaint();
    }

    @Override
    public void addPieceClickListener(Consumer<Piece> listener) {
        clickListener = listener;
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(500, 500);
    }

    /** 1080×1080 기준 좌표 → 500×500으로 비례 변환 */
    private Point calculateLocationRelative(int pos) {
        Point orig = NODE_COORDS.get(pos);
        double factor = 500 / 1080.0;
        return new Point((int)(orig.x * factor), (int)(orig.y * factor));
    }
}
