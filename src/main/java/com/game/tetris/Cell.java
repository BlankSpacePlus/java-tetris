package com.game.tetris;

import java.awt.Image;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 方块类
 */
@AllArgsConstructor
@Data
@NoArgsConstructor
public class Cell {

    private int row;

    private int col;

    private Image image;

    public void moveRight() {
        this.col++;
    }

    public void moveLeft() {
        this.col--;
    }

    public void moveDown() {
        this.row++;
    }

}
