// src/yutgame/model/GameModel.java
package yutgame.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * 게임의 핵심 로직을 담은 모델 클래스
 */
public class GameModel {
    private final SettingModel config;
    private final List<Player> players;
    private int currentPlayerIndex;
    private int lastThrow;
    private boolean extraTurn;
    private final Random random = new Random();

    // 사각형 보드에서 'Node0'을 Node20으로 간주하기 위해 20 사용
    private static final int FINISH_INDEX = 20;

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
     * 0=빽도,1=도,2=개,3=걸,4=윷,5=모
     * 윷(4) 또는 모(5)가 나오면 추가 턴(extraTurn=true)
     */
    public int throwYut() {
        int r = random.nextInt(6);
        lastThrow = r;
        extraTurn = (r == 4 || r == 5);
        return r;
    }

    /**
     * 현재 플레이어가 이동시킬 수 있는 말 목록을 반환합니다.
     * (빽도인 경우 position==0인 말은 제외)
     */
    public List<Piece> getMovablePieces() {
        Player current = getCurrentPlayer();
        List<Piece> list = new ArrayList<>();
        for (Piece p : current.getPieces()) {
            if (lastThrow == 0 && p.getPosition() == 0) continue;
            list.add(p);
        }
        return Collections.unmodifiableList(list);
    }

    /**
     * 선택된 말을 lastThrow 만큼 이동시킵니다.
     * FINISH_INDEX(=20)보다 크게 이동할 때 비로소 골인 처리합니다.
     */
    public void movePiece(Piece piece) {
        int steps;
        switch (lastThrow) {
            case 0: steps = -1; break;
            case 1: steps = 1;  break;
            case 2: steps = 2;  break;
            case 3: steps = 3;  break;
            case 4: steps = 4;  break;
            case 5: steps = 5;  break;
            default: steps = 0;
        }
        int dest = piece.getPosition() + steps;

        if (dest > FINISH_INDEX) {
            // FINISH_INDEX(20)를 넘어야만 골인으로 인정
            piece.setPosition(-1);  // 보드/인벤토리에서 모두 제외
            Player owner = findPlayer(piece.getId().split("_")[0]);
            owner.incrementScore();
        } else if (dest < 0) {
            // 빽도 시 음수 위치 방지
            piece.setPosition(0);
        } else {
            piece.setPosition(dest);
        }
    }

    /**
     * 현재 모든 말의 리스트 반환 (position == -1인 골인 말도 포함).
     */
    public List<Piece> getPiecePositions() {
        List<Piece> all = new ArrayList<>();
        for (Player p : players) {
            all.addAll(p.getPieces());
        }
        return Collections.unmodifiableList(all);
    }

    /** 추가 턴이 아닐 때만 턴을 넘깁니다. */
    public void nextTurn() {
        if (!extraTurn) {
            currentPlayerIndex = (currentPlayerIndex + 1) % players.size();
        }
        extraTurn = false;
    }

    /** 현재 턴이 끝났는지 여부 */
    public boolean isTurnOver() {
        return !extraTurn;
    }

    /** 현재 차례인 플레이어 */
    public Player getCurrentPlayer() {
        return players.get(currentPlayerIndex);
    }

    /** 읽기 전용 플레이어 목록 */
    public List<Player> getPlayers() {
        return Collections.unmodifiableList(players);
    }

    // ── 헬퍼 ──

    /** ID로 Player 객체 찾기 */
    private Player findPlayer(String id) {
        return players.stream()
            .filter(p -> p.getId().equals(id))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("Unknown player: " + id));
    }
}
