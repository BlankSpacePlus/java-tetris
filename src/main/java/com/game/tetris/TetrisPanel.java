package com.game.tetris;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Arrays;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

/**
 * 游戏核心面板类
 */
public class TetrisPanel extends JPanel {

    /**
     * 确保序列化版本一致
     */
    private static final long serialVersionUID = 1L;

    /**
     * 行数，即一列中的方块数
     */
    public static final int ROWS = 20;

    /**
     * 列数，即一行中的方块数
     */
    public static final int COLS = 10;

    /**
     * 字体颜色
     */
    public static final int FONT_COLOR = 0x667799;

    /**
     * 字体大小32
     */
    public static final int FONT_SIZE = 0x20;

    /**
     * 方块大小
     */
    public static final int CELL_SIZE = 26;

    /**
     * 得分梯度
     */
    private static final int[] SCORE_CASCADES = {0, 1, 10, 30, 200};

    /**
     * 背景图片Image
     */
    private static Image BACKGROUND;

    /**
     * 七种方块Image
     */
    public static Image I, J, L, S, Z, O, T;

    /*
     * 静态初始化块，对Image进行初始化
     */
    static {
        try {
            BACKGROUND = ImageIO.read(Objects.requireNonNull(
                    TetrisPanel.class.getResource("../../../icon/tetris.png")));
            T = ImageIO.read(Objects.requireNonNull(
                    TetrisPanel.class.getResource("../../../icon/T.png")));
            I = ImageIO.read(Objects.requireNonNull(
                    TetrisPanel.class.getResource("../../../icon/I.png")));
            S = ImageIO.read(Objects.requireNonNull(
                    TetrisPanel.class.getResource("../../../icon/S.png")));
            Z = ImageIO.read(Objects.requireNonNull(
                    TetrisPanel.class.getResource("../../../icon/Z.png")));
            L = ImageIO.read(Objects.requireNonNull(
                    TetrisPanel.class.getResource("../../../icon/L.png")));
            J = ImageIO.read(Objects.requireNonNull(
                    TetrisPanel.class.getResource("../../../icon/J.png")));
            O = ImageIO.read(Objects.requireNonNull(
                    TetrisPanel.class.getResource("../../../icon/O.png")));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 游戏结果计数器，记录当前已消除行数
     */
    private int lines;

    /**
     * 游戏结果计数器，记录当前已获得分数，得分增幅有梯度
     */
    private int scores;

    /**
     * 游戏暂停标记
     */
    private boolean isPause;

    /**
     * 游戏结束标记
     */
    private boolean isGameOver;

    /**
     * 当前下落方块组
     */
    private Tetromino tetromino;

    /**
     * 下一个下落方块组
     */
    private Tetromino nextTetromino;

    /**
     * 系统定时器，此类游戏必备
     */
    private Timer timer;

    /**
     * 面板方块填充情况记录
     */
    private final Cell[][] wall = new Cell[ROWS][COLS];

    /**
     * 游戏核心流程入口
     */
    public void init() {
        // 开始游戏
        startGame();
        // 重新绘制内容
        repaint();
        // 创建键盘按键事件监听器
        KeyAdapter adaptor = new KeyAdapter() {
            /**
             * 获取键盘输入后，对合法的键盘输入执行不同的操作：<br>
             * VK_Q -> Q键 -> 退出游戏 <br>
             * VK_S -> S键 -> 如果游戏已结束则重新开始 <br>
             * VK_C -> C键 -> 如果游戏已暂停则恢复游戏 <br>
             * 不满足以上条件，则执行chooseKeyPressAction()的进一步判断
             *
             * @param e 键盘事件
             */
            @Override
            public void keyPressed(KeyEvent e) {
                int key = e.getKeyCode();
                if (key == KeyEvent.VK_Q) {
                    quitGame();
                }
                if (TetrisPanel.this.isGameOver) {
                    if (key == KeyEvent.VK_S) {
                        startGame();
                    }
                    return;
                }
                if (TetrisPanel.this.isPause) {
                    if (key == KeyEvent.VK_C) {
                        continueGame();
                    }
                    return;
                }
                chooseKeyPressAction(key);
                repaint();
            }
        };
        // 获取焦点
        this.requestFocus();
        // JPanel添加该事件监听器
        this.addKeyListener(adaptor);
    }

    /**
     * 退出游戏并终止进程
     */
    private void quitGame() {
        System.exit(0);
    }

    /**
     * 游戏开始的初始化过程
     */
    public void startGame() {
        clearWall();
        this.tetromino = Tetromino.randomTetromino();
        this.nextTetromino = Tetromino.randomTetromino();
        this.lines = 0;
        this.scores = 0;
        this.isPause = false;
        this.isGameOver = false;
        this.timer = new Timer();
        this.timer.schedule(new TimerTask() {
            @Override
            public void run() {
                dropSoft();
                repaint();
            }
        }, 700, 700);
    }

    /**
     * 暂停后继续游戏进程
     */
    private void continueGame() {
        this.timer = new Timer();
        this.timer.schedule(new TimerTask() {
            @Override
            public void run() {
                dropSoft();
                repaint();
            }
        }, 700, 700);
        this.isPause = false;
        repaint();
    }

    /**
     * 获取键盘输入后，对合法的键盘输入执行不同的操作：<br>
     * VK_RIGHT -> 右键 -> 快速右移一格 <br>
     * VK_LEFT -> 左键 -> 快速左移一格 <br>
     * VK_DOWN -> 下键 -> 快速下降一格 <br>
     * VK_UP -> 上键 -> 右旋 <br>
     * VK_Z -> Z键 -> 左旋 <br>
     * VK_SPACE -> 空格键 -> 直接落底 <br>
     * VK_P -> P键 -> 暂停 <br>
     * 以上操作均在判断退出、重开、恢复之后进行判定
     *
     * @param key 键盘输入编码
     */
    private void chooseKeyPressAction(int key) {
        switch (key) {
            case KeyEvent.VK_RIGHT:
                moveRight();
                break;
            case KeyEvent.VK_LEFT:
                moveLeft();
                break;
            case KeyEvent.VK_DOWN:
                dropSoft();
                break;
            case KeyEvent.VK_UP:
                rotateRight();
                break;
            case KeyEvent.VK_Z:
                rotateLeft();
                break;
            case KeyEvent.VK_SPACE:
                dropHard();
                break;
            case KeyEvent.VK_P:
                pauseGame();
                break;
        }
    }

    /**
     * 当前方块组快速右移一格
     */
    private void moveRight() {
        this.tetromino.moveRight();
        if (checkOutOfBound() || checkCoincide()) {
            this.tetromino.moveLeft();
        }
    }

    /**
     * 当前方块组快速左移一格
     */
    private void moveLeft() {
        this.tetromino.moveLeft();
        if (checkOutOfBound() || checkCoincide()) {
            this.tetromino.moveRight();
        }
    }

    /**
     * 当前方块组快速右旋一次
     */
    private void rotateRight() {
        this.tetromino.rotateRight();
        if (checkOutOfBound() || checkCoincide()) {
            this.tetromino.rotateLeft();
        }
    }

    /**
     * 当前方块组快速左旋一次
     */
    private void rotateLeft() {
        this.tetromino.rotateLeft();
        if (checkOutOfBound() || checkCoincide()) {
            this.tetromino.rotateRight();
        }
    }

    /**
     * 当前方块组快速下移一格
     */
    private void dropSoft() {
        if (checkCanDrop()) {
            this.tetromino.dropSoft();
        } else {
            tetrominoLandToWall();
            destroyLines();
            checkGameOver();
            this.tetromino = this.nextTetromino;
            this.nextTetromino = Tetromino.randomTetromino();
        }
    }

    /**
     * 当前方块组快速下落至底
     */
    private void dropHard() {
        while (checkCanDrop()) {
            this.tetromino.dropSoft();
        }
        tetrominoLandToWall();
        destroyLines();
        checkGameOver();
        this.tetromino = this.nextTetromino;
        this.nextTetromino = Tetromino.randomTetromino();
    }

    /**
     * 暂停游戏进程
     */
    private void pauseGame() {
        this.timer.cancel();
        this.isPause = true;
        repaint();
    }

    /**
     * 当前方块组横向平移越界检查<br>
     * 左移、右移、左旋、右旋四个操作都需要调用该检查<br>
     * 和checkCoincide()均需要符合要求
     *
     * @return 当前方块组所执行的操作是否出现横向越界
     */
    private boolean checkOutOfBound() {
        Cell[] cells = this.tetromino.getCells();
        for (Cell cell : cells) {
            int col = cell.getCol();
            if (col < 0 || col >= COLS) {
                return true;
            }
        }
        return false;
    }

    /**
     * 当前方块组横向平移重叠检查<br>
     * 左移、右移、左旋、右旋四个操作都需要调用该检查，确保不会叠到已有方块上<br>
     * 和checkOutOfBound()均需要符合要求
     *
     * @return 当前方块组所执行的操作是否出现横向重叠
     */
    private boolean checkCoincide() {
        Cell[] cells = this.tetromino.getCells();
        for (Cell cell : cells) {
            int row = cell.getRow();
            int col = cell.getCol();
            if (row < 0 || row >= ROWS || col < 0 || col >= COLS || this.wall[row][col] != null) {
                return true;
            }
        }
        return false;
    }

    /**
     * 当前方块组下落的可行性检查<br>
     * 下落前必须进行此操作，确保能合规下落
     *
     * @return 当前方块组是否可以继续下落
     */
    private boolean checkCanDrop() {
        Cell[] cells = this.tetromino.getCells();
        for (Cell cell : cells) {
            int row = cell.getRow();
            if (row == ROWS - 1) {
                return false;
            }
        }
        for (Cell cell : cells) {
            int row = cell.getRow();
            int col = cell.getCol();
            if (this.wall[row + 1][col] != null) {
                return false;
            }
        }
        return true;
    }

    /**
     * 下落后的方块数据更新<br>
     * 在destroyLines()前执行
     */
    private void tetrominoLandToWall() {
        Cell[] cells = this.tetromino.getCells();
        for (Cell cell : cells) {
            int row = cell.getRow();
            int col = cell.getCol();
            this.wall[row][col] = cell;
        }
    }

    /**
     * 消除所有已填满的行<br>
     * 下落后必须进行此操作，并且可能不止消除一行
     */
    private void destroyLines() {
        int lines = 0;
        for (int row = 0; row < wall.length; row++) {
            if (checkLineFull(row)) {
                destroyLine(row);
                lines++;
            }
        }
        this.lines += lines;
        this.scores += SCORE_CASCADES[lines];
    }

    /**
     * 检查当前行是否已满
     *
     * @param row 当前行号
     * @return 当前行是否已满
     */
    private boolean checkLineFull(int row) {
        Cell[] line = this.wall[row];
        for (Cell cell : line) {
            if (cell == null) {
                return false;
            }
        }
        return true;
    }

    /**
     * 消除已满的当前行
     *
     * @param row 需要消除的行号
     */
    private void destroyLine(int row) {
        for (int i = row; i >= 1; i--) {
            System.arraycopy(this.wall[i - 1], 0, this.wall[i], 0, COLS);
        }
        Arrays.fill(this.wall[0], null);
    }

    /**
     * 游戏结束判定
     */
    private void checkGameOver() {
        if (this.wall[0][4] == null) {
            return;
        }
        this.isGameOver = true;
        this.timer.cancel();
        repaint();
    }

    /**
     * 清空面板，重新开始
     */
    private void clearWall() {
        for (int row = 0; row < ROWS; row++) {
            Arrays.fill(this.wall[row], null);
        }
    }

    /**
     * 分步绘制完整画面
     *
     * @param g Graphics对象
     */
    @Override
    public void paint(Graphics g) {
        g.drawImage(BACKGROUND, 0, 0, null);
        g.translate(15, 15);
        paintTetromino(g);
        paintWall(g);
        paintNextTetromino(g);
        paintScoreCount(g);
    }

    /**
     * 绘制当前方块组的每个方块
     *
     * @param g Graphics对象
     */
    private void paintTetromino(Graphics g) {
        Cell[] cells = this.tetromino.getCells();
        for (Cell c : cells) {
            int x = c.getCol() * CELL_SIZE - 1;
            int y = c.getRow() * CELL_SIZE - 1;
            g.drawImage(c.getImage(), x, y, null);
        }
    }

    /**
     * 绘制面板
     *
     * @param g Graphics对象
     */
    private void paintWall(Graphics g) {
        for (int row = 0; row < this.wall.length; row++) {
            Cell[] line = this.wall[row];
            for (int col = 0; col < line.length; col++) {
                Cell cell = line[col];
                int x = col * CELL_SIZE;
                int y = row * CELL_SIZE;
                if (cell != null) {
                    g.drawImage(cell.getImage(), x - 1, y - 1, null);
                }
            }
        }
    }

    /**
     * 绘制下一个方块组的每个方块
     *
     * @param g Graphics对象
     */
    private void paintNextTetromino(Graphics g) {
        Cell[] cells = this.nextTetromino.getCells();
        for (Cell c : cells) {
            int x = (c.getCol() + 10) * CELL_SIZE - 1;
            int y = (c.getRow() + 1) * CELL_SIZE - 1;
            g.drawImage(c.getImage(), x, y, null);
        }
    }

    /**
     * 绘制记分版和提示版
     *
     * @param g Graphics对象
     */
    private void paintScoreCount(Graphics g) {
        Font font = new Font(getFont().getName(), Font.BOLD, FONT_SIZE);
        int x = 290, y = 162, height = 56;
        g.setColor(new Color(FONT_COLOR));
        g.setFont(font);
        String notice = "SCORE:" + this.scores;
        g.drawString(notice, x, y);
        y += height;
        notice = "LINES:" + this.lines;
        g.drawString(notice, x, y);
        y += height;
        notice = "[P]Pause";
        if (this.isPause) {
            notice = "[C]Continue";
        }
        if (this.isGameOver) {
            notice = "[S]ReStart!";
        }
        g.drawString(notice, x, y);
    }

}
