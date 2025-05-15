// src/yutgame/view/RectangleBoardView.java
package yutgame.view;

import yutgame.model.Piece;
import yutgame.model.SettingModel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class RectangleBoardView extends AbstractBoardView {
    private final Image boardImage;
    private List<Piece> piecePositions = Collections.emptyList();
    private Consumer<Piece> clickListener;

    // 사각형 외곽 20칸 + Node20 (출발점 한 바퀴 돈 지점)
    // Node20은 Node0과 같은 좌표지만 로직상 별개로 취급합니다.
    private static final Map<Integer, Point> NODE_COORDS = Map.ofEntries(
        Map.entry(0,  new Point(981, 980)),
        Map.entry(1,  new Point(981, 780)),
        Map.entry(2,  new Point(981, 620)),
        Map.entry(3,  new Point(981, 460)),
        Map.entry(4,  new Point(981, 300)),
        Map.entry(5,  new Point(981, 100)),
        Map.entry(6,  new Point(781, 100)),
        Map.entry(7,  new Point(621, 100)),
        Map.entry(8,  new Point(461, 100)),
        Map.entry(9,  new Point(301, 100)),
        Map.entry(10, new Point(100, 100)),
        Map.entry(11, new Point(100, 300)),
        Map.entry(12, new Point(100, 460)),
        Map.entry(13, new Point(100, 620)),
        Map.entry(14, new Point(100, 780)),
        Map.entry(15, new Point(100, 980)),
        Map.entry(16, new Point(301, 980)),
        Map.entry(17, new Point(461, 980)),
        Map.entry(18, new Point(621, 980)),
        Map.entry(19, new Point(781, 980)),
        Map.entry(20, new Point(981, 980))  // Node20: 출발점 한 바퀴 돈 위치
    );

    public RectangleBoardView(SettingModel config) {
        setPreferredSize(new Dimension(500, 500));
        setBackground(Color.WHITE);
        boardImage = new ImageIcon("src/yutgame/img/Board4.png").getImage();

        // 판 위 클릭 → pieceSelectedCallback 호출
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int sz = 500 / 12;
                Point click = e.getPoint();
                // 같은 칸에 모여있는 말들 중 대표 하나를 클릭
                Map<Integer, List<Piece>> grouped = piecePositions.stream()
                    .collect(Collectors.groupingBy(Piece::getPosition));
                for (var entry : grouped.entrySet()) {
                    Point loc = calculateLocationRelative(entry.getKey());
                    Rectangle r = new Rectangle(loc.x - sz/2, loc.y - sz/2, sz, sz);
                    if (r.contains(click) && clickListener != null) {
                        clickListener.accept(entry.getValue().get(0));
                        return;
                    }
                }
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(boardImage, 0, 0, 500, 500, this);

        int sz = 500 / 12;
        Map<Integer, List<Piece>> grouped = piecePositions.stream()
            .collect(Collectors.groupingBy(Piece::getPosition));
        for (var entry : grouped.entrySet()) {
            int pos = entry.getKey();
            List<Piece> list = entry.getValue();
            Piece rep = list.get(0);
            String playerId = rep.getId().split("_")[0];
            int count = list.size();

            // piece_<playerId>_<count>.png
            String path = "src/yutgame/img/piece_" + playerId + "_" + count + ".png";
            Image img = new ImageIcon(path).getImage();
            Point loc = calculateLocationRelative(pos);
            g.drawImage(img, loc.x - sz/2, loc.y - sz/2, sz, sz, this);
        }
    }

    @Override
    public void refresh(List<Piece> pieces) {
        // 표시할 위치: position>0 && position<=20
        this.piecePositions = pieces.stream()
            .filter(p -> {
                int pos = p.getPosition();
                return pos > 0 && NODE_COORDS.containsKey(pos);
            })
            .collect(Collectors.toList());
        repaint();
    }

    @Override
    public void highlightMoves(List<Piece> movable) {
        repaint();
    }

    @Override
    public void addPieceClickListener(Consumer<Piece> listener) {
        this.clickListener = listener;
    }

    @Override
    public List<Point> getNodeLocations() {
        List<Point> pts = new ArrayList<>();
        // 0~20까지 포함
        for (int i = 0; i <= 20; i++) {
            pts.add(calculateLocationRelative(i));
        }
        return Collections.unmodifiableList(pts);
    }

    @Override
    public void animatePiece(Piece piece, int from, int to) {
        // 필요 시 애니메이션 구현
    }

    private Point calculateLocationRelative(int pos) {
        Point orig = NODE_COORDS.get(pos);
        double scale = 500.0 / 1080.0;
        return new Point((int)(orig.x * scale), (int)(orig.y * scale));
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(500, 500);
    }
}
