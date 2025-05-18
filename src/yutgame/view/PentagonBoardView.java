// src/yutgame/view/PentagonBoardView.java
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

public class PentagonBoardView extends AbstractBoardView {
    private final Image boardImage;
    // private final Image debugImage; // 각 노드에 말 표시하기 (1/3)
    private List<Piece> piecePositions = Collections.emptyList();
    private Consumer<Piece> clickListener;

    private static final Map<Integer, Point> NODE_COORDS = Map.ofEntries(
        Map.entry(0,  new Point(827, 980)),
        Map.entry(36, new Point(827, 980)),

        Map.entry(1,  new Point(870, 846)),
        Map.entry(2,  new Point(900, 750)),
        Map.entry(3,  new Point(930, 659)),
        Map.entry(4,  new Point(960, 568)),

        Map.entry(5,  new Point(1004, 435)),
        Map.entry(6,  new Point(890, 355)),
        Map.entry(7,  new Point(815, 300)),
        Map.entry(8,  new Point(740, 240)),
        Map.entry(9,  new Point(665, 180)),

        Map.entry(10, new Point(540, 110)),
        Map.entry(11, new Point(425, 180)),
        Map.entry(12, new Point(350, 238)),
        Map.entry(13, new Point(275, 295)),
        Map.entry(14, new Point(200, 350)),

        Map.entry(15, new Point(80, 435)),
        Map.entry(16, new Point(130, 568)),
        Map.entry(17, new Point(160, 659)),
        Map.entry(18, new Point(190, 750)),
        Map.entry(19, new Point(220, 846)),

        Map.entry(20, new Point(255, 980)),
        Map.entry(21, new Point(400, 980)),
        Map.entry(22, new Point(500, 980)),
        Map.entry(23, new Point(595, 980)),
        Map.entry(24, new Point(690, 980)),

        Map.entry(25, new Point(845, 480)),
        Map.entry(26, new Point(700, 520)),

        Map.entry(27, new Point(545, 565)),
        Map.entry(28, new Point(645, 725)),
        Map.entry(29, new Point(725, 835)),

        Map.entry(30, new Point(430, 725)),
        Map.entry(31, new Point(362, 835)),

        Map.entry(32, new Point(540, 270)),
        Map.entry(33, new Point(540, 400)),

        Map.entry(34, new Point(250, 485)),
        Map.entry(35, new Point(388, 524))
    );

    public PentagonBoardView(SettingModel config) {
        setPreferredSize(new Dimension(500, 500));
        setBackground(Color.WHITE);
        boardImage = new ImageIcon("src/yutgame/img/Board5.png").getImage();
        // debugImage = new ImageIcon("src/yutgame/img/piece_P1_0.png").getImage(); // 각 노드에 말 표시하기 (2/3)

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int sz = 500 / 12;
                Point click = e.getPoint();
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
        // 배경
        g.drawImage(boardImage, 0, 0, 500, 500, this);

        
        int sz = 500 / 12;

        // (디버깅용) 노드 번호 및 debug 이미지, 오름차순
        g.setColor(Color.BLACK);
        g.setFont(new Font("SansSerif", Font.BOLD, 12));
        for (Integer node : new TreeSet<>(NODE_COORDS.keySet())) {
            Point loc = calculateLocationRelative(node);
            g.drawString(String.valueOf(node), loc.x - 6, loc.y - 6);
            // g.drawImage(debugImage, loc.x - sz/2, loc.y - sz/2, sz, sz, this); // 각 노드에 말 표시하기 (3/3)
        }
        
        // 실제 말 그리기
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
        // Node0 은 절대 보드에 표시되지 않음 (인벤토리만 사용)
        this.piecePositions = pieces.stream()
            .filter(p -> p.getPosition() > 0)
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
        for (Integer node : new TreeSet<>(NODE_COORDS.keySet())) {
            pts.add(calculateLocationRelative(node));
        }
        return Collections.unmodifiableList(pts);
    }

    @Override
    public void animatePiece(Piece piece, int from, int to) { /* 미구현 */ }

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
