package view;

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
    private final byte GRID_SIZE;
    private Field theField;
    private static JLabel label;
    private long time;
    private Player player;

    private long startTime = System.currentTimeMillis();

    public View(Field field, byte size) {
        theField = field;
        GRID_SIZE = size;
        label = new JLabel();
        label.setFont(new Font("宋体", 2, 20));
        this.add(label);
//        setSize(field.getWidth()*GRID_SIZE, field.getHeight()*GRID_SIZE); 没效果
    }

    public void setTime(long time) {
        this.time = time;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }
//    public void die() {
//        flag = true;
//        try {
//            audioClip= Applet.newAudioClip(new File("resource/死亡声音.wav").toURI().toURL());
//            audioClip.play();
//        } catch (MalformedURLException e) {
//            e.printStackTrace();
//        }
//    }

//    public boolean isDie() {
//        return flag;
//    }

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
//        if (player.isAlive()) {
//        count++;
//        System.out.println(count);
        int grass = 0;
        int human = 0;
        int wolf = 0;
        int sheep = 0;
        g.setColor(Color.LIGHT_GRAY);
        // 画行
        for (int row = 0; row < theField.getHeight(); row++) {
            g.drawLine(0, row * GRID_SIZE, theField.getWidth() * GRID_SIZE, row * GRID_SIZE);
        }
        // 画列
        for (int col = 0; col < theField.getWidth(); col++) {
            g.drawLine(col * GRID_SIZE, 0, col * GRID_SIZE, theField.getHeight() * GRID_SIZE);
        }
        // 画生物,同时记录和显示数据
        for (int row = 0; row < theField.getHeight(); row++) {
            for (int col = 0; col < theField.getWidth(); col++) {
                Cell cell = theField.getCell(row, col);
                if (cell != null) {
                    cell.draw(g, col * GRID_SIZE, row * GRID_SIZE, GRID_SIZE);
                    if (cell instanceof Plant) {
                        grass++;
                    } else if (cell instanceof Human) {
                        if (cell instanceof HumanPlayer) {
                            //添加十字线来标示player
                            g.setColor(Color.RED);
                            //短划虚线图案
                            float[] dash = {GRID_SIZE, GRID_SIZE};
                            //实例化新画刷
                            BasicStroke bs = new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, dash, 0.0f);
                            //设置新的画刷
                            ((Graphics2D) g).setStroke(bs);
                            g.drawLine(col * GRID_SIZE + GRID_SIZE / 2, 0, col * GRID_SIZE + GRID_SIZE / 2, theField.getHeight() * GRID_SIZE);
                            g.drawLine(0, row * GRID_SIZE + GRID_SIZE / 2, theField.getWidth() * GRID_SIZE, row * GRID_SIZE + GRID_SIZE / 2);
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
                grass, human, wolf, sheep, sum, time, (int) (time / ((double) (System.currentTimeMillis() - startTime) / 1000))));
//        } else {
//
//            g.setColor(Color.RED);
//            g.setFont(new Font("楷体", 1, 50));
//            g.drawString("失败乃成功之母! 按【回车键】重新开始吧!", theField.getWidth() * GRID_SIZE / 4, theField.getHeight() * GRID_SIZE / 3);
//            g.setFont(new Font("宋体", 1, 25));
//            g.drawString("狼的捕食范围为2格, 人的捕食范围为1格, 但是狼吃了人之后会不吃不动一回合, 你可以利用这个特点来捕猎狼(风险越高,收益越大!)", theField.getWidth() * GRID_SIZE / 50, theField.getHeight() * GRID_SIZE / 2);
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
        return new Dimension(theField.getWidth() * GRID_SIZE, theField.getHeight() * GRID_SIZE);
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
