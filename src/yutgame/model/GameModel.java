// src/yutgame/model/GameModel.java
package yutgame.model;

import java.util.*;

public class GameModel {
    private final SettingModel config;
    private final PathStrategy path;
    private final List<Player> players;
    private int currentPlayerIndex;
    private int lastThrow;
    private boolean extraTurn;
    private final Random random = new Random();

    public GameModel(SettingModel config) {
        this.config = config;
        this.players = new ArrayList<>();
        for (int i = 1; i <= config.getNumPlayers(); i++) {
            players.add(new Player("P" + i, config.getPiecesPerPlayer()));
        }
        this.currentPlayerIndex = 0;
        this.extraTurn = false;
        this.path = PathStrategyFactory.of(config.getBoardShape());
    }

    public int throwYut() {
        int r = random.nextInt(6);
        lastThrow = r;
        extraTurn = (r == 4 || r == 5);
        return r;
    }

    public int throwYut(int forcedResult) {
        lastThrow = forcedResult;
        extraTurn = (forcedResult == 4 || forcedResult == 5);
        return forcedResult;
    }

    public List<Piece> getMovablePieces() {
        Player cur = getCurrentPlayer();
        List<Piece> lst = new ArrayList<>();
        for (Piece p : cur.getPieces()) {
            if (lastThrow == 0 && p.getPosition() == 0) continue;
            lst.add(p);
        }
        return Collections.unmodifiableList(lst);
    }

    public void movePiece(Piece piece) {
        int steps;
        switch (lastThrow) {
            case 0: steps = -1; break;
            case 1: steps =  1; break;
            case 2: steps =  2; break;
            case 3: steps =  3; break;
            case 4: steps =  4; break;
            case 5: steps =  5; break;
            default: steps =  0; break;
        }
        int dest = path.next(piece.getPosition(), steps);
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
        if (!extraTurn) {
            currentPlayerIndex = (currentPlayerIndex + 1) % players.size();
        }
        extraTurn = false;
    }

    public Player getCurrentPlayer() {
        return players.get(currentPlayerIndex);
    }
    public List<Player> getPlayers() {
        return Collections.unmodifiableList(players);
    }

    public boolean isGameOver() {
        int target = config.getPiecesPerPlayer();
        return players.stream().anyMatch(p -> p.getScore() == target);
    }
    public Player getWinner() {
        int target = config.getPiecesPerPlayer();
        return players.stream()
                      .filter(p -> p.getScore() == target)
                      .findFirst()
                      .orElse(null);
    }

    private Player findPlayer(String id) {
        return players.stream()
            .filter(p -> p.getId().equals(id))
            .findFirst()
            .orElseThrow();
    }
}
