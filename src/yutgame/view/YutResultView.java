// src/yutgame/view/YutResultView.java
package yutgame.view;

import javax.swing.*;
import java.awt.*;

public class YutResultView extends JPanel {
    private final JLabel lblResult;

    public YutResultView() {
        // 결과 라벨 초기화
        lblResult = new JLabel("결과: -", SwingConstants.CENTER);
        lblResult.setFont(new Font("맑은 고딕", Font.BOLD, 16));

        // 레이아웃 설정 및 라벨 추가
        setLayout(new FlowLayout(FlowLayout.CENTER, 10, 10));
        add(lblResult);
    }

    /**
     * 윷 던지기 결과를 텍스트로 표시합니다.
     * @param result -1=빽도, 1=도, 2=개, 3=걸, 4=윷, 5=모
     */
    public void showResult(int result) {
        String text;
        switch (result) {
            case 0: text = "빽도"; break;
            case 1: text = "도";   break;
            case 2: text = "개";   break;
            case 3: text = "걸";   break;
            case 4: text = "윷";   break;
            case 5: text = "모";   break;
            default: text = "-";    break;
        }
        lblResult.setText("결과: " + text);
    }
}
