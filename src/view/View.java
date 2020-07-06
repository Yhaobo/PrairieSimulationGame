package view;

import model.Actor;
import model.Cell;
import model.Field;
import model.biology.animal.Human;
import model.biology.animal.Wolf;
import model.biology.plant.Plant;

import javax.swing.*;
import java.awt.*;

public class View extends JPanel {
    //    private static final long serialVersionUID = -5258995676212660595L;
    private final byte GRID_SIZE;
    private Field theField;
    private static JLabel label;
    private long time;
    private boolean flag;
//    private int count;
//    AudioClip audioClip;

    public View(Field field, byte size) {
        theField = field;
        GRID_SIZE = size;
        label = new JLabel();
        label.setFont(new Font("宋体", 2, 20));
        add(label);
//        setSize(field.getWidth()*GRID_SIZE, field.getHeight()*GRID_SIZE); 没效果
    }

    public void setTime(long time) {
        this.time = time;
    }

    public void die() {
        flag = true;
//        try {
//            audioClip= Applet.newAudioClip(new File("resource/死亡声音.wav").toURI().toURL());
//            audioClip.play();
//        } catch (MalformedURLException e) {
//            e.printStackTrace();
//        }
    }

    public boolean isDie() {
        return flag;
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (!flag) {
//        count++;
//        System.out.println(count);
            int grass = 0;
            int human = 0;
            int wolf = 0;
            int sheep = 0;
            g.setColor(Color.GRAY);
            //画行
            for (int row = 0; row < theField.getHeight(); row++) {
                g.drawLine(0, row * GRID_SIZE, theField.getWidth() * GRID_SIZE, row * GRID_SIZE);
            }
            //画列
            for (int col = 0; col < theField.getWidth(); col++) {
                g.drawLine(col * GRID_SIZE, 0, col * GRID_SIZE, theField.getHeight() * GRID_SIZE);
            }
            for (int row = 0; row < theField.getHeight(); row++) {
                for (int col = 0; col < theField.getWidth(); col++) {
                    Cell cell = theField.getCell(row, col);
                    if (cell != null) {
                        cell.draw(g, col * GRID_SIZE, row * GRID_SIZE, GRID_SIZE);
                        if (cell instanceof Plant) {
                            grass++;
                        } else if (cell instanceof Human) {
                            if (cell instanceof Actor) {//添加十字线来标示actor
                                g.setColor(Color.RED);
                                //画虚线
                                float[] dash = {GRID_SIZE, GRID_SIZE}; //短划线图案
                                BasicStroke bs = new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, dash, 0.0f); //实例化新画刷
                                ((Graphics2D) g).setStroke(bs); //设置新的画刷
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
//                else {
//                    g.setColor(new Color(255, 255, 0));
//                    g.fillRect(col * GRID_SIZE, row * GRID_SIZE, GRID_SIZE, GRID_SIZE);
//                }
                }
            }
            int sum = grass + human + wolf + sheep;

            //使用String.format()方法来格式化
            label.setText(String.format("植物【绿色】: %04d株   人【红色】：%04d个    狼【黑色】：%04d匹    羊【蓝色】：%04d只    总数: %05d个    时间: %d天",
                    grass, human, wolf, sheep, sum, time));
        } else {

            g.setColor(Color.RED);
            g.setFont(new Font("楷体", 1, 50));
            g.drawString("失败乃成功之母! 按【回车键】重新开始吧!", theField.getWidth() * GRID_SIZE / 4, theField.getHeight() * GRID_SIZE / 3);
            g.setFont(new Font("宋体", 1, 25));
            g.drawString("狼的捕食范围为2格, 人的捕食范围为1格, 但是狼吃了人之后会不吃不动一回合, 你可以利用这个特点来捕猎狼(风险越高,收益越大!)", theField.getWidth() * GRID_SIZE / 50, theField.getHeight() * GRID_SIZE / 2);
            g.setColor(Color.BLACK);
            g.setFont(new Font("宋体", 3, 25));
            g.drawString("游戏制作者: 不青山的青山", theField.getWidth() * GRID_SIZE / 2, theField.getHeight() * GRID_SIZE - 100);
        }
    }

    //通过重写此方法可以配合JFrame.pack()方法来实现JFrame容器匹配组件的大小
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
