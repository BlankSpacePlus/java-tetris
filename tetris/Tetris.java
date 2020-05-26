package com.games.tetris;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Arrays;
import java.util.Timer;
import java.util.TimerTask;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class Tetris extends JPanel {
    
    private static final long serialVersionUID = 1L;

	public static final int ROWS = 20, COLS = 10, FONT_COLOR = 0x667799, FONT_SIZE = 0x20, CELL_SIZE = 26;
	
	private static final int[] SCORE_TABLE={0, 1, 10, 30, 200}; 
	
	private static Image BACKGROUND;
	    
	public static Image I, J, L, S, Z, O, T;

	private int lines, scores;
	
    private boolean isPause, isGameOver;
    
    private Tetromino tetromino, nextOne;
    
    private Timer timer;
    
    private Cell[][] wall = new Cell[ROWS][COLS]; 
	
	static {
		try {
			BACKGROUND = ImageIO.read(Tetris.class.getResource("ico/tetris.png"));
			T = ImageIO.read(Tetris.class.getResource("ico/T.png"));
			I = ImageIO.read(Tetris.class.getResource("ico/I.png"));
			S = ImageIO.read(Tetris.class.getResource("ico/S.png"));
			Z = ImageIO.read(Tetris.class.getResource("ico/Z.png"));
			L = ImageIO.read(Tetris.class.getResource("ico/L.png"));
			J = ImageIO.read(Tetris.class.getResource("ico/J.png"));
			O = ImageIO.read(Tetris.class.getResource("ico/O.png"));
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public void action(){
		startAction();
		repaint();
		KeyAdapter adaptor = new KeyAdapter() {
			public void keyPressed(KeyEvent e) {
				int key = e.getKeyCode();
				if (key == KeyEvent.VK_Q) {
					System.exit(0);
				}
				if (isGameOver) {
					if (key == KeyEvent.VK_S) {
						startAction();
					}
					return;
				}
				if (isPause) {
					if (key == KeyEvent.VK_C) {	
					    continueAction();	
					}
					return;
				}
				switch (key) {
				    case KeyEvent.VK_RIGHT: 
				        moveRightAction(); 
				        break;
				    case KeyEvent.VK_LEFT: 
				        moveLeftAction();
				        break;
				    case KeyEvent.VK_DOWN: 
				        softDropAction();
				        break;
				    case KeyEvent.VK_UP: 
				        rotateRightAction();
				        break;
				    case KeyEvent.VK_Z: 
				        rotateLeftAction();
				        break;
				    case KeyEvent.VK_SPACE: 
				        hardDropAction();
				        break;
				    case KeyEvent.VK_P: 
				        pauseAction(); 
				        break;
				}
				repaint();
			}
		};
		this.requestFocus();
		this.addKeyListener(adaptor);
	}
	
	public void paint(Graphics g) {
		g.drawImage(BACKGROUND, 0, 0, null);
		g.translate(15, 15);
		paintTetromino(g);
		paintWall(g);
		paintNextOne(g);
		paintScore(g);
	}
	
	private void paintScore(Graphics g) {
		Font f = getFont();
		Font font = new Font(f.getName(), Font.BOLD, FONT_SIZE);
		int x = 290;
		int y = 162;
		g.setColor(new Color(FONT_COLOR));
		g.setFont(font);
		String str = "SCORE:"+this.scores;
		g.drawString(str, x, y);
		y+=56;
		str = "LINES:"+this.lines;
		g.drawString(str, x, y);
		y+=56;
		str = "[P]Pause";
		if(isPause){str = "[C]Continue";}
		if(isGameOver){	str = "[S]Start!";}
		g.drawString(str, x, y);
	}

	private void paintNextOne(Graphics g) {
		Cell[] cells = nextOne.getCells();
		for(int i = 0; i < cells.length; i++) {
			Cell c = cells[i];
			int x = (c.getCol()+10) * CELL_SIZE-1;
			int y = (c.getRow()+1) * CELL_SIZE-1;
			g.drawImage(c.getImage(), x, y, null);
		}	
	}
	
	private void paintTetromino(Graphics g) {
		Cell[] cells = tetromino.getCells();
		for(int i = 0; i < cells.length; i++){
			Cell c = cells[i];
			int x = c.getCol() * CELL_SIZE-1;
			int y = c.getRow() * CELL_SIZE-1;
			g.drawImage(c.getImage(), x, y, null);
		}		
	}
	
		private void paintWall(Graphics g){
			for(int row=0; row<wall.length; row++){
				Cell[] line = wall[row];
				for(int col=0; col<line.length; col++){
					Cell cell = line[col]; 
					int x = col*CELL_SIZE; 
					int y = row*CELL_SIZE;
					if (cell != null) {
					    g.drawImage(cell.getImage(), x-1, y-1, null);
					}
				}
			}
		}
	
		public void softDropAction(){
			if(tetrominoCanDrop()){
				tetromino.softDrop();
			}else{
				tetrominoLandToWall();
				destroyLines();
				checkGameOver();
				tetromino = nextOne;
				nextOne = Tetromino.randomTetromino();
			}
		}

		public void destroyLines(){
			int lines = 0;
			for(int row = 0; row<wall.length; row++){
				if(fullCells(row)){
					deleteRow(row);
					lines++;
				}
			}
			this.lines += lines;
			this.scores += SCORE_TABLE[lines];
		}
		
		public boolean fullCells(int row){
			Cell[] line = wall[row];
			for(int i=0; i<line.length; i++){
				if(line[i]==null){
					return false;
				}
			}
			return true;
		}
		
		public void deleteRow(int row){
			for(int i=row; i>=1; i--){
				System.arraycopy(wall[i-1], 0, wall[i], 0, COLS);
			}
			Arrays.fill(wall[0], null);
		}
		
		public boolean tetrominoCanDrop(){
			Cell[] cells = tetromino.getCells();
			for(int i = 0; i<cells.length; i++){
				Cell cell = cells[i];
				int row = cell.getRow();
				if(row == ROWS-1){
				    return false;
				}
			}
			for(int i = 0; i<cells.length; i++){
				Cell cell = cells[i];
				int row = cell.getRow(); int col = cell.getCol();
				if(wall[row+1][col] != null){
					return false;
				}
			}
			return true;
		}

		public void tetrominoLandToWall(){
			Cell[] cells = tetromino.getCells();
			for(int i=0; i<cells.length; i++){
				Cell cell = cells[i];
				int row = cell.getRow();
				int col = cell.getCol();
				wall[row][col] = cell;
			}
		}
		
		public void moveRightAction(){
			tetromino.moveRight();
			if(outOfBound() || coincide()){
				tetromino.moveLeft();
			}
		}
		public void moveLeftAction(){
			tetromino.moveLeft();
			if(outOfBound() || coincide()){
				tetromino.moveRight();
			}
		}

		private boolean outOfBound(){
			Cell[] cells = tetromino.getCells();
			for(int i=0; i<cells.length; i++){
				Cell cell = cells[i];
				int col = cell.getCol();
				if(col<0 || col>=COLS){
					return true;//������
				}
			}
			return false;
		}
		private boolean coincide(){
			Cell[] cells = tetromino.getCells();
			for(Cell cell: cells){
				int row = cell.getRow();
				int col = cell.getCol();
				if(row<0 || row>=ROWS || col<0 || col>=COLS || wall[row][col]!=null){
					return true;
				}
			}
			return false;
		} 

		public void rotateRightAction(){
			tetromino.rotateRight();
			if(outOfBound() || coincide()){
				tetromino.rotateLeft();
			}
		}

		public void rotateLeftAction(){
			tetromino.rotateLeft();
			if(outOfBound() || coincide()){
				tetromino.rotateRight();
			}
		}
		public void hardDropAction(){
			while(tetrominoCanDrop()){
				tetromino.softDrop();
			}
			tetrominoLandToWall();
			destroyLines();
			checkGameOver();
			tetromino = nextOne;
			nextOne = Tetromino.randomTetromino();
		}
		
		public void startAction(){
			clearWall();
			tetromino = Tetromino.randomTetromino();
			nextOne = Tetromino.randomTetromino();
			lines = 0; scores = 0;	isPause=false; isGameOver=false;
			timer = new Timer();
			timer.schedule(new TimerTask(){
				public void run() {
					softDropAction();
					repaint();
				}
			}, 700, 700);
		}
		
		private void clearWall(){
			for(int row=0; row<ROWS; row++){
				Arrays.fill(wall[row], null);
			}
		}
		
		public void pauseAction(){
			timer.cancel();
			isPause = true;
			repaint();
		}
		
		public void continueAction(){
			timer = new Timer();
			timer.schedule(new TimerTask() {
				public void run() {
					softDropAction();
					repaint();
				}
			}, 700, 700);
			isPause = false;
			repaint();
		}
		
		public void checkGameOver(){
			if(wall[0][4]==null){
				return;
			}
			isGameOver = true;
			timer.cancel();
			repaint();
		}
	

	public static void main(String[] args) {
		JFrame frame = new JFrame();
		Tetris tetris = new Tetris();
		frame.add(tetris);
		frame.setSize(525, 590);
		frame.setUndecorated(false);
		frame.setTitle("俄罗斯方块");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
		tetris.action();
	}

}
