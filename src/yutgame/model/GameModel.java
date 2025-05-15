// src/yutgame/model/GameModel.java
package yutgame.model;

import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class GameModel {
    private final SettingModel config;
    private final List<Player> players;
    private int currentPlayerIndex;
    private int lastThrow;
    private boolean extraTurn;
    private final Random random = new Random();

    public GameModel(SettingModel config) {
        this.config = config;
        this.currentPlayerIndex = 0;
        this.players = new ArrayList<>();
        for (int i = 1; i <= config.getNumPlayers(); i++) {
            players.add(new Player("P" + i, config.getPiecesPerPlayer()));
        }
        this.extraTurn = false;
    }

    /**
     * 윷을 던져서 결과 코드를 반환합니다.
     * 0=빽도, 1=도, 2=개, 3=걸, 4=윷, 5=모
     */
    public int throwYut() {
        int r = random.nextInt(6);
        lastThrow = r;
        extraTurn = (r == 4 || r == 5);  // 윷 또는 모일 때 추가 턴
        return r;
    }

    /**
     * 현재 플레이어가 이동시킬 수 있는 말 목록을 반환합니다.
     * (단순화하여 항상 모든 말을 이동 가능으로 처리)
     */
    public List<Piece> getMovablePieces() {
        Player current = getCurrentPlayer();
        return new ArrayList<>(current.getPieces());
    }

    /**
     * 선택한 말(piece)을 lastThrow 결과만큼 이동시킵니다.
     */
    public void movePiece(Piece piece) {
        int steps;
        switch (lastThrow) {
            case 0: steps = -1; break;  // 빽도
            case 1: steps = 1;  break;  // 도
            case 2: steps = 2;  break;  // 개
            case 3: steps = 3;  break;  // 걸
            case 4: steps = 4;  break;  // 윷
            case 5: steps = 5;  break;  // 모
            default: steps = 0;
        }
        piece.setPosition(piece.getPosition() + steps);
    }

    /**
     * 보드 위 모든 말의 현재 위치 리스트를 반환합니다.
     */
    public List<Piece> getPiecePositions() {
        List<Piece> all = new ArrayList<>();
        for (Player p : players) {
            all.addAll(p.getPieces());
        }
        return Collections.unmodifiableList(all);
    }

    /**
     * 추가 턴 여부가 false일 때만 턴 종료로 간주합니다.
     */
    public boolean isTurnOver() {
        return !extraTurn;
    }

    /**
     * 턴을 다음 플레이어로 넘깁니다. (단, extraTurn이면 그대로 유지)
     */
    public void nextTurn() {
        if (!extraTurn) {
            currentPlayerIndex = (currentPlayerIndex + 1) % players.size();
        }
        extraTurn = false;
    }

    /** 현재 차례인 플레이어를 반환합니다. */
    public Player getCurrentPlayer() {
        return players.get(currentPlayerIndex);
    }

    /** 외부에서 읽기 전용으로 플레이어 목록을 가져갑니다. */
    public List<Player> getPlayers() {
        return Collections.unmodifiableList(players);
    }
}
