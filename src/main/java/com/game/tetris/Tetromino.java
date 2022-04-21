package com.game.tetris;

import java.util.Arrays;
import java.util.Random;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 四格拼板类，对应俄罗斯方块的一个方块组
 */
public class Tetromino {

    /**
     * 随机数生成器
     */
    private static final Random RANDOM = new Random();

    /**
     * 四枚方块的集合
     */
    private final Cell[] cells;

    /**
     * 方块组可能的状态集合
     */
    private final State[] states;

    /**
     * 设置一个大的数为了避免出现
     */
    private int index;

    public Tetromino(Cell[] cells, State[] states) {
        this.cells = cells;
        this.states = states;
        this.index = 100000;
    }

    /**
     * 方块组当前状态内部类
     */
    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    private static class State {
        private int row0, col0, row1, col1, row2, col2, row3, col3;
    }

    /**
     * 根据生成的随机数生成Tetromino对象：<br>
     * case 0 -> T <br>
     * case 1 -> I <br>
     * case 2 -> J <br>
     * case 3 -> L <br>
     * case 4 -> O <br>
     * case 5 -> S <br>
     * case 6 -> Z <br>
     * default -> 不存在
     *
     * @return 对应的Tetromino对象
     */
    public static Tetromino randomTetromino() {
        switch (RANDOM.nextInt(7)) {
            case 0:
                return new Tetromino(
                        new Cell[]{
                                new Cell(0, 4, TetrisPanel.T),
                                new Cell(0, 3, TetrisPanel.T),
                                new Cell(0, 5, TetrisPanel.T),
                                new Cell(1, 4, TetrisPanel.T)
                        },
                        new Tetromino.State[]{
                                new State(0, 0, 0, -1, 0, 1, 1, 0),
                                new State(0, 0, -1, 0, 1, 0, 0, -1),
                                new State(0, 0, 0, 1, 0, -1, -1, 0),
                                new State(0, 0, 1, 0, -1, 0, 0, 1)
                        }
                );
            case 1:
                return new Tetromino(
                        new Cell[]{
                                new Cell(0, 4, TetrisPanel.I),
                                new Cell(0, 3, TetrisPanel.I),
                                new Cell(0, 5, TetrisPanel.I),
                                new Cell(0, 6, TetrisPanel.I)
                        },
                        new Tetromino.State[]{
                                new State(0, 0, 0, 1, 0, -1, 0, -2),
                                new State(0, 0, -1, 0, 1, 0, 2, 0)
                        }
                );
            case 2:
                return new Tetromino(
                        new Cell[]{
                                new Cell(0, 4, TetrisPanel.J),
                                new Cell(0, 3, TetrisPanel.J),
                                new Cell(0, 5, TetrisPanel.J),
                                new Cell(1, 5, TetrisPanel.J)
                        },
                        new Tetromino.State[]{
                                new State(0, 0, 0, -1, 0, 1, 1, 1),
                                new State(0, 0, -1, 0, 1, 0, 1, -1),
                                new State(0, 0, 0, 1, 0, -1, -1, -1),
                                new State(0, 0, 1, 0, -1, 0, -1, 1)
                        }
                );
            case 3:
                return new Tetromino(
                        new Cell[]{
                                new Cell(0, 4, TetrisPanel.L),
                                new Cell(0, 3, TetrisPanel.L),
                                new Cell(0, 5, TetrisPanel.L),
                                new Cell(1, 3, TetrisPanel.L)
                        },
                        new Tetromino.State[]{
                                new State(0, 0, 0, -1, 0, 1, 1, -1),
                                new State(0, 0, -1, 0, 1, 0, -1, -1),
                                new State(0, 0, 0, 1, 0, -1, -1, 1),
                                new State(0, 0, 1, 0, -1, 0, 1, 1)
                        }
                );
            case 4:
                return new Tetromino(
                        new Cell[]{
                                new Cell(0, 4, TetrisPanel.O),
                                new Cell(0, 5, TetrisPanel.O),
                                new Cell(1, 4, TetrisPanel.O),
                                new Cell(1, 5, TetrisPanel.O)
                        },
                        new Tetromino.State[]{
                                new State(0, 0, 0, 1, 1, 0, 1, 1),
                                new State(0, 0, 0, 1, 1, 0, 1, 1)
                        }
                );
            case 5:
                return new Tetromino(
                        new Cell[]{
                                new Cell(0, 4, TetrisPanel.S),
                                new Cell(0, 5, TetrisPanel.S),
                                new Cell(1, 3, TetrisPanel.S),
                                new Cell(1, 4, TetrisPanel.S)
                        },
                        new Tetromino.State[]{
                                new State(0, 0, 0, 1, 1, -1, 1, 0),
                                new State(0, 0, -1, 0, 1, 1, 0, 1)
                        }
                );
            case 6:
                return new Tetromino(
                        new Cell[]{
                                new Cell(1, 4, TetrisPanel.Z),
                                new Cell(0, 3, TetrisPanel.Z),
                                new Cell(0, 4, TetrisPanel.Z),
                                new Cell(1, 5, TetrisPanel.Z)
                        },
                        new Tetromino.State[]{
                                new State(0, 0, -1, -1, -1, 0, 0, 1),
                                new State(0, 0, -1, 1, 0, 1, 1, 0)
                        }
                );
            default:
                return null;
        }
    }

    /**
     * 只能有get不能有set
     *
     * @return 获取Cell集合
     */
    public Cell[] getCells() {
        return this.cells;
    }

    /**
     * 所有方块下落一格
     */
    public void dropSoft() {
        for (Cell cell : this.cells) {
            cell.moveDown();
        }
    }

    /**
     * 所有方块右移一格
     */
    public void moveRight() {
        for (Cell cell : this.cells) {
            cell.moveRight();
        }
    }

    /**
     * 所有方块左移一格
     */
    public void moveLeft() {
        for (Cell cell : this.cells) {
            cell.moveLeft();
        }
    }

    /**
     * 方块组左转一次
     */
    public void rotateRight() {
        this.index++;
        rotate();
    }

    /**
     * 方块组右转一次
     */
    public void rotateLeft() {
        this.index--;
        rotate();
    }

    /**
     * 方块组旋转，通过获取上一个或下一个状态来实现，需要取模运算，基于大的index初始值防止出现负数<br>
     * 围绕着cells[0]旋转所以其作为基准不变
     */
    private void rotate() {
        State state = this.states[this.index % this.states.length];
        Cell cell = this.cells[0];
        this.cells[1].setRow(cell.getRow() + state.getRow1());
        this.cells[1].setCol(cell.getCol() + state.getCol1());
        this.cells[2].setRow(cell.getRow() + state.getRow2());
        this.cells[2].setCol(cell.getCol() + state.getCol2());
        this.cells[3].setRow(cell.getRow() + state.getRow3());
        this.cells[3].setCol(cell.getCol() + state.getCol3());
    }

    @Override
    public String toString() {
        return Arrays.toString(this.cells);
    }

}
