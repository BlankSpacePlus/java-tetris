package com.games.tetris;

import java.util.Arrays;
import java.util.Random;

public class Tetromino {

    protected Cell[] cells = new Cell[4];

    protected State[] states;
    
    private int index = 100000;

    public static Tetromino randomTetromino(){
        Random r = new Random();
        int type = r.nextInt(7);
        switch (type) {
            case 0: 
                return new T();
            case 1: 
                return new I();
            case 2: 
                return new J();
            case 3: 
                return new L();
            case 4: 
                return new O();
            case 5: 
                return new S();
            case 6: 
                return new Z();
        }
        return null;
    }
    
    public Cell[] getCells() {
        return cells;
    }

    public void softDrop(){
        for(int i = 0; i < cells.length; i++){
            cells[i].moveDown();
        }
    }
    
    public void moveRight(){
        for(int i = 0; i < cells.length; i++){
            this.cells[i].moveRight();
        }
    } 
    public void moveLeft(){
        for(int i = 0; i < cells.length; i++){
            cells[i].moveLeft();
        }
    }
    
    public void rotateRight() {
        index++;
        State s = states[index%states.length];
        Cell o = cells[0];
        cells[1].setRow(o.getRow()+s.row1);
        cells[1].setCol(o.getCol()+s.col1);
        cells[2].setRow(o.getRow()+s.row2);
        cells[2].setCol(o.getCol()+s.col2);
        cells[3].setRow(o.getRow()+s.row3);
        cells[3].setCol(o.getCol()+s.col3);
    }

    public void rotateLeft() {
        index--;
        State s = states[index%states.length];
        Cell o = cells[0];
        cells[1].setRow(o.getRow()+s.row1);
        cells[1].setCol(o.getCol()+s.col1);
        cells[2].setRow(o.getRow()+s.row2);
        cells[2].setCol(o.getCol()+s.col2);
        cells[3].setRow(o.getRow()+s.row3);
        cells[3].setCol(o.getCol()+s.col3);
    }
    
    @Override
    public String toString() {
        return Arrays.toString(cells); 
    }

    protected class State{
        int row0, col0, row1, col1, row2, col2, row3, col3;
        public State(int row0, int col0, int row1, int col1, int row2, int col2, int row3, int col3) {
            this.row0 = row0;
            this.col0 = col0;
            this.row1 = row1;
            this.col1 = col1;
            this.row2 = row2;
            this.col2 = col2;
            this.row3 = row3;
            this.col3 = col3;
        }      
    }
    
}
