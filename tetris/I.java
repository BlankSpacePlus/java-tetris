package com.games.tetris;

public class I extends Tetromino {
    public I() {
        cells[0] = new Cell(0, 4, Tetris.I);
        cells[1] = new Cell(0, 3, Tetris.I);
        cells[2] = new Cell(0, 5, Tetris.I);
        cells[3] = new Cell(0, 6, Tetris.I);
        states = new State[]{
                new State(0,0, 0,1, 0,-1, 0,-2),
                new State(0,0, -1,0, 1,0,2,0)};
    }
}
