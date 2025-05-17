// src/yutgame/model/GameModel.java
package yutgame.model;

import java.util.*;

public class GameModel {
    private final PathStrategy path;
    private final List<Player> players;
    private int currentPlayerIndex;
    private int lastThrow;
    private boolean extraTurn;
    private final Random random = new Random();

    public GameModel(SettingModel config) {
        this.players = new ArrayList<>();
        for (int i = 1; i <= config.getNumPlayers(); i++) {
            players.add(new Player("P" + i, config.getPiecesPerPlayer()));
        }
        this.currentPlayerIndex = 0;
        this.extraTurn = false;
        this.path = PathStrategyFactory.of(config.getBoardShape());
    }

    /** 랜덤 윷 던지기 */
    public int throwYut() {
        int r = random.nextInt(6);
        lastThrow = r;
        extraTurn = (r == 4 || r == 5);
        return r;
    }

    /** 지정 윷 던지기 */
    public int throwYut(int forcedResult) {
        lastThrow = forcedResult;
        extraTurn = (forcedResult == 4 || forcedResult == 5);
        return forcedResult;
    }

    /** 이동 가능한 말 반환 (빽도 시 position==0인 말 제외) */
    public List<Piece> getMovablePieces() {
        Player cur = getCurrentPlayer();
        List<Piece> lst = new ArrayList<>();
        for (Piece p : cur.getPieces()) {
            if (lastThrow == 0 && p.getPosition() == 0) continue;
            lst.add(p);
        }
        return Collections.unmodifiableList(lst);
    }

    /** 선택된 말을 이동시키고, 골인 처리까지 담당 */
    public void movePiece(Piece piece) {
        int steps = switch (lastThrow) {
            case 0 -> -1;
            case 1 -> 1;
            case 2 -> 2;
            case 3 -> 3;
            case 4 -> 4;
            case 5 -> 5;
            default -> 0;
        };
        int cur = piece.getPosition();
        int dest = path.next(cur, steps);

        if (path.isFinish(dest)) {
            piece.setPosition(-1);
            findPlayer(piece.getId().split("_")[0]).incrementScore();
        } else if (dest < 0) {
            piece.setPosition(0);
        } else {
            piece.setPosition(dest);
        }
    }

    public List<Piece> getPiecePositions() {
        List<Piece> all = new ArrayList<>();
        for (Player p : players) all.addAll(p.getPieces());
        return Collections.unmodifiableList(all);
    }

    public void nextTurn() {
        if (!extraTurn) currentPlayerIndex = (currentPlayerIndex + 1) % players.size();
        extraTurn = false;
    }

    public boolean isTurnOver()       { return !extraTurn; }
    public Player getCurrentPlayer() { return players.get(currentPlayerIndex); }
    public List<Player> getPlayers() { return Collections.unmodifiableList(players); }

    private Player findPlayer(String id) {
        return players.stream()
                      .filter(p -> p.getId().equals(id))
                      .findFirst()
                      .orElseThrow();
    }
}
