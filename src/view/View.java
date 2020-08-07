package view;

import controller.PrairieStory;
import model.entity.Location;
import model.entity.biology.animal.HumanPlayer;
import model.interfaces.Cell;
import model.Field;
import model.entity.biology.animal.Human;
import model.entity.biology.animal.Wolf;
import model.entity.biology.plant.Plant;
import model.interfaces.Player;

import javax.swing.*;
import java.awt.*;

public class View extends JPanel {
    private Field theField;
    private static JLabel label;
    private long time;
    private Player player;

    private long gameStartTime;

    private Location destCenterLocation;
    private Location realCenterLocation;
    /**
     * 窗口像素宽
     */
    private final int viewWidth;
    /**
     * 窗口像素高
     */
    private final int viewHeight;

    public View(Field field, long gameStartTime, Player player, int viewWidth, int viewHeight) {
        this.player = player;
        theField = field;
        this.gameStartTime = gameStartTime;
        this.viewHeight = viewHeight;
        this.viewWidth = viewWidth;
        realCenterLocation = field.getCenterLocation();
        label = new JLabel();
        label.setFont(new Font("宋体", 2, 20));
        this.add(label);
//        setSize(field.getWidth()*GRID_SIZE, field.getHeight()*GRID_SIZE); 没效果
    }

    public void setTime(long time) {
        this.time = time;
    }

    public Location getDestCenterLocation() {
        return destCenterLocation;
    }

    public void setDestCenterLocation(Location destCenterLocation) {
        if (destCenterLocation.getRow() > theField.getHeight()) {
            destCenterLocation.setRow(theField.getHeight());
        }
        if (destCenterLocation.getRow() < 0) {
            destCenterLocation.setRow(0);
        }
        if (destCenterLocation.getColumn() > theField.getWidth()) {
            destCenterLocation.setColumn(theField.getWidth());
        }
        if (destCenterLocation.getColumn() < 0) {
            destCenterLocation.setColumn(0);
        }
        this.destCenterLocation = destCenterLocation;
    }

    private int getRowNum() {
        return viewHeight / PrairieStory.gridSize;
    }

    private int getColNum() {
        return viewWidth / PrairieStory.gridSize;
    }

    public int getStartRow() {

        return realCenterLocation.getRow() - getRowNum() / 2;
//        return Math.max(startRow, 0);
    }

    public int getStartCol() {
        return realCenterLocation.getColumn() - getColNum() / 2;
//        return Math.max(startCol, 0);
    }

    private void centerLocationMove() {
        if (destCenterLocation == null) {
            return;
        }
        if (destCenterLocation.getRow() == realCenterLocation.getRow()) {
        } else if (destCenterLocation.getRow() > realCenterLocation.getRow()) {
            realCenterLocation.setRow(realCenterLocation.getRow() + 1);
        } else {
            realCenterLocation.setRow(realCenterLocation.getRow() - 1);
        }
        if (destCenterLocation.getColumn() == realCenterLocation.getColumn()) {
        } else if (destCenterLocation.getColumn() > realCenterLocation.getColumn()) {
            realCenterLocation.setColumn(realCenterLocation.getColumn() + 1);
        } else {
            realCenterLocation.setColumn(realCenterLocation.getColumn() - 1);
        }

        if (realCenterLocation.getRow() > theField.getHeight() - getRowNum() / 2) {
            realCenterLocation.setRow(theField.getHeight() - getRowNum() / 2);
        } else if (realCenterLocation.getRow() < getRowNum() / 2) {
            realCenterLocation.setRow(getRowNum() / 2);
        }

        if (realCenterLocation.getColumn() > theField.getWidth() - getColNum() / 2) {
            realCenterLocation.setColumn(theField.getWidth() - getColNum() / 2);
        } else if (realCenterLocation.getColumn() < getColNum() / 2) {
            realCenterLocation.setColumn(getColNum() / 2);
        }
    }

    /**
     * Actually, in Swing, you should change paintComponent() instead of paint(),
     * as paint calls paintBorder(), paintComponent() and paintChildren().
     *
     * @param g
     */
    @Override
    public void paintComponent(Graphics g) {
        // 消去之前的画面,如果用paint(), 不用写这行代码
        super.paintComponent(g);

        centerLocationMove();

//        if (player.isAlive()) {
        int grass = 0;
        int human = 0;
        int wolf = 0;
        int sheep = 0;
        g.setColor(Color.LIGHT_GRAY);
//        if (PrairieStory.gridSize > 7) {
//            // 画行
//            for (int row = 0; row < theField.getHeight(); row++) {
//                g.drawLine(0, row * PrairieStory.gridSize, theField.getWidth() * PrairieStory.gridSize, row * PrairieStory.gridSize);
//            }
//            // 画列
//            for (int col = 0; col < theField.getWidth(); col++) {
//                g.drawLine(col * PrairieStory.gridSize, 0, col * PrairieStory.gridSize, theField.getHeight() * PrairieStory.gridSize);
//            }
//        }
        // 画生物,同时记录和显示数据
        for (int row = 0; row <= getRowNum(); row++) {
            for (int col = 0; col <= getColNum(); col++) {
                Cell cell = theField.getCell(getStartRow() + row, getStartCol() + col);
                if (cell != null) {
                    cell.draw(g, col * PrairieStory.gridSize, row * PrairieStory.gridSize, PrairieStory.gridSize);
                    if (cell instanceof Plant) {
                        grass++;
                    } else if (cell instanceof Human) {
                        if (cell instanceof HumanPlayer) {
                            //添加十字线来标示player
                            g.setColor(Color.RED);
                            //短划虚线图案
                            float[] dash = {PrairieStory.gridSize, PrairieStory.gridSize};
                            //实例化新画刷
                            BasicStroke bs = new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, dash, 0.0f);
                            //设置新的画刷
                            ((Graphics2D) g).setStroke(bs);
                            g.drawLine(col * PrairieStory.gridSize + PrairieStory.gridSize / 2, 0, col * PrairieStory.gridSize + PrairieStory.gridSize / 2, theField.getHeight() * PrairieStory.gridSize);
                            g.drawLine(0, row * PrairieStory.gridSize + PrairieStory.gridSize / 2, theField.getWidth() * PrairieStory.gridSize, row * PrairieStory.gridSize + PrairieStory.gridSize / 2);
                        }
                        human++;
                    } else if (cell instanceof Wolf) {
                        wolf++;
                    } else {
                        sheep++;
                    }
                }
            }
        }
        int sum = grass + human + wolf + sheep;

        //使用String.format()方法来格式化
        label.setText(String.format("植物【绿色】: %04d株   人【蓝色】：%04d个    狼【红色】：%04d匹    羊【白色】：%04d只    总数: %05d个    时间: %d天    平均帧率: %d ",
                grass, human, wolf, sheep, sum, time, (int) (time / ((double) (System.currentTimeMillis() - gameStartTime) / 1000))));
//        } else {
//            g.setColor(Color.RED);
//            g.setFont(new Font("楷体", 1, 50));
//            g.drawString("失败乃成功之母! 按【END键】重新开始吧!", theField.getWidth() * GRID_SIZE / 4, theField.getHeight() * GRID_SIZE / 3);
//            g.setFont(new Font("宋体", 1, 25));
////            g.drawString("狼的捕食范围为2格, 人的捕食范围为1格, 但是狼吃了人之后会不吃不动一回合, 你可以利用这个特点来捕猎狼(风险越高,收益越大!)", theField.getWidth() * GRID_SIZE / 50, theField.getHeight() * GRID_SIZE / 2);
//            g.setColor(Color.BLACK);
//            g.setFont(new Font("宋体", 3, 25));
//            g.drawString("游戏制作者: 不青山的青山", theField.getWidth() * GRID_SIZE / 2, theField.getHeight() * GRID_SIZE - 100);
//        }
    }

    /**
     * 通过重写此方法可以配合JFrame.pack()方法来实现JFrame容器匹配组件的大小
     *
     * @return
     */
    @Override
    public Dimension getPreferredSize() {
        return new Dimension(theField.getWidth() * PrairieStory.gridSize, theField.getHeight() * PrairieStory.gridSize);
    }

    public void setGameStartTime(long gameStartTime) {
        this.gameStartTime = gameStartTime;
    }

    public long getGameStartTime() {
        return this.gameStartTime;
    }

//	public static void main(String[] args) {
//		Field field = new Field(10, 10);
//		for (int row = 0; row < field.getHeight(); row++) {
//			for (int col = 0; col < field.getWidth(); col++) {
//				field.place(row, col, new Cell());
//			}
//		}
//
//		View init = new View(field);
//		JFrame frame = new JFrame();
//		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//		frame.setResizable(false);
//		frame.setTitle("Cells");
//		frame.add(init);
//		frame.pack();
//		frame.setVisible(true);
//	}

}
