package com.games.tetris;

public class L extends Tetromino {
    public L() {
        cells[0] = new Cell(0, 4, Tetris.L);
        cells[1] = new Cell(0, 3, Tetris.L);
        cells[2] = new Cell(0, 5, Tetris.L);
        cells[3] = new Cell(1, 3, Tetris.L);
        states = new State[]{
                new State(0, 0, 0, -1, 0, 1, 1, -1),
                new State(0, 0, -1, 0, 1, 0, -1, -1),
                new State(0, 0, 0, 1, 0, -1, -1, 1),
                new State(0, 0, 1, 0, -1, 0, 1, 1)};    
    }
}
