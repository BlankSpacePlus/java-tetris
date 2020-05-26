package com.games.tetris;

public class T extends Tetromino {
    
    public T() {
        cells[0] = new Cell(0, 4, Tetris.T);
        cells[1] = new Cell(0, 3, Tetris.T);
        cells[2] = new Cell(0, 5, Tetris.T);
        cells[3] = new Cell(1, 4, Tetris.T);
        states = new State[] {
                new State(0, 0, 0, -1, 0, 1, 1, 0),
                new State(0, 0, -1, 0, 1, 0, 0, -1),
                new State(0, 0, 0, 1, 0, -1, -1, 0),
                new State(0, 0, 1, 0, -1, 0, 0, 1)
        };
    }
    
}
