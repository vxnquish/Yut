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

    // 1080×1080 기준: 외곽 0–19 + 내부 대각선 20–28 (총 29개 노드)
    private static final Map<Integer, Point> NODE_COORDS = Map.ofEntries(
        // ── 외곽 0–19 ───────────────────────────────────
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
        // ── 대각선 내부 9개 노드 20–28 ────────────────────
        Map.entry(20, new Point(811, 250)),
        Map.entry(21, new Point(691, 380)),
        Map.entry(22, new Point(540, 540)),
        Map.entry(23, new Point(390, 690)),
        Map.entry(24, new Point(271, 805)),
        Map.entry(25, new Point(271, 250)),
        Map.entry(26, new Point(390, 380)),
        Map.entry(27, new Point(691, 690)),
        Map.entry(28, new Point(811, 805))
    );

    public RectangleBoardView(SettingModel config) {
        setPreferredSize(new Dimension(500, 500));
        setBackground(Color.WHITE);
        boardImage = new ImageIcon("src/yutgame/img/Board4.png").getImage();

        // 판 위 클릭 → 말 선택
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int sz = 500 / 12;
                Point click = e.getPoint();
                Map<Integer, List<Piece>> grouped = piecePositions.stream()
                    .collect(Collectors.groupingBy(Piece::getPosition));
                for (var entry : grouped.entrySet()) {
                    Point loc = calculateLocationRelative(entry.getKey());
                    Rectangle r = new Rectangle(
                        loc.x - sz/2, loc.y - sz/2, sz, sz
                    );
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
        // 배경 그림
        g.drawImage(boardImage, 0, 0, 500, 500, this);

        // (디버깅용) 노드 번호 그리기
        g.setColor(Color.BLACK);
        g.setFont(new Font("SansSerif", Font.BOLD, 12));
        for (var entry : NODE_COORDS.entrySet()) {
            Point loc = calculateLocationRelative(entry.getKey());
            g.drawString(String.valueOf(entry.getKey()), loc.x - 6, loc.y - 6);
        }

        // 말 그리기: 같은 위치 말은 갯수만큼 합쳐서 하나의 이미지(count 표시)
        int sz = 500 / 12;
        Map<Integer, List<Piece>> grouped = piecePositions.stream()
            .collect(Collectors.groupingBy(Piece::getPosition));
        for (var entry : grouped.entrySet()) {
            int pos = entry.getKey();
            List<Piece> list = entry.getValue();
            Piece rep = list.get(0);
            String pid = rep.getId().split("_")[0];
            int count = list.size();
            String imgPath = "src/yutgame/img/piece_" + pid + "_" + count + ".png";
            Image img = new ImageIcon(imgPath).getImage();
            Point loc = calculateLocationRelative(pos);
            g.drawImage(img, loc.x - sz/2, loc.y - sz/2, sz, sz, this);
        }
    }

    @Override
    public void refresh(List<Piece> pieces) {
        this.piecePositions = pieces.stream()
            // position<0 은 골인(사라짐)
            // position>0 은 정상
            // position==0 이지만 이미 움직인 말만 보여줌
            .filter(p -> p.getPosition() > 0 || (p.getPosition() == 0 && p.hasMoved()))
            .collect(Collectors.toList());
        repaint();
    }

    @Override
    public void highlightMoves(List<Piece> m) {
        repaint();
    }

    @Override
    public void addPieceClickListener(Consumer<Piece> l) {
        this.clickListener = l;
    }

    @Override
    public List<Point> getNodeLocations() {
        List<Point> pts = new ArrayList<>();
        for (int i = 0; i <= 28; i++) {
            pts.add(calculateLocationRelative(i));
        }
        return Collections.unmodifiableList(pts);
    }

    @Override
    public void animatePiece(Piece piece, int from, int to) {
        // 애니메이션 구현 시 사용
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
