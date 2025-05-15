// src/yutgame/model/SettingModel.java
package yutgame.model;

public class SettingModel {
    private int numPlayers;
    private int piecesPerPlayer;
    private BoardShape boardShape;

    public int getNumPlayers() {
        return numPlayers;
    }
    public void setNumPlayers(int numPlayers) {
        this.numPlayers = numPlayers;
    }
    public int getPiecesPerPlayer() {
        return piecesPerPlayer;
    }
    public void setPiecesPerPlayer(int piecesPerPlayer) {
        this.piecesPerPlayer = piecesPerPlayer;
    }
    public BoardShape getBoardShape() {
        return boardShape;
    }
    public void setBoardShape(BoardShape boardShape) {
        this.boardShape = boardShape;
    }

    @Override
    public String toString() {
        return String.format("SettingModel[players=%d, pieces=%d, shape=%s]",
                numPlayers, piecesPerPlayer, boardShape);
    }
}
