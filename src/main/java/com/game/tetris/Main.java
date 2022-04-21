package com.game.tetris;

import javax.swing.JFrame;

public class Main {

    public static void main(String[] args) {
        JFrame frame = new JFrame();
        TetrisPanel tetrisPanel = new TetrisPanel();
        frame.add(tetrisPanel);
        frame.setSize(540, 595);
        frame.setUndecorated(false);
        frame.setTitle("俄罗斯方块");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        tetrisPanel.init();
    }

}
