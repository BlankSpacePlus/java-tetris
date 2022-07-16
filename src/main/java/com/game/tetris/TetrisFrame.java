package com.game.tetris;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Objects;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

public class TetrisFrame extends JFrame {

    /**
     * 确保序列化版本一致
     */
    private static final long serialVersionUID = 1L;

    public TetrisFrame() {
        TetrisPanel tetrisPanel = new TetrisPanel();
        this.add(tetrisPanel);
        ImageIcon imageIcon = new ImageIcon(Objects.requireNonNull(this.getClass().getResource("../../../icon/tetris-icon.png")));
        this.setIconImage(imageIcon.getImage());
        this.setSize(540, 595);
        this.setUndecorated(false);
        this.setTitle("俄罗斯方块");
        this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        this.setLocationRelativeTo(null);
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                tetrisPanel.pauseGame();
                int option = JOptionPane.showConfirmDialog(TetrisFrame.this, "确定退出游戏？", "退出游戏", JOptionPane.YES_NO_OPTION);
                if (option == JOptionPane.YES_OPTION && e.getWindow() == TetrisFrame.this) {
                    TetrisFrame.this.dispose();
                    System.exit(0);
                }
                tetrisPanel.continueGame();
            }
        });
        this.setVisible(true);
        tetrisPanel.init();
    }

}
