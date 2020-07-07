package controller;

import model.Field;
import model.Round;
import view.View;

import javax.swing.*;
import java.applet.Applet;
import java.applet.AudioClip;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.*;

//public class PrairieStory extends JFrame implements KeyListener {
public class PrairieStory extends JFrame {
    private static Field field;
    private View theView;
    private Round round;
    private JLabel label;
    private static File archive = new File("存档.data");
    private volatile boolean pause = false;
    private long speed = 10L;
    private long time;
    private int x;
    private int y;
    private static final byte GRID_SIZE = 3;
    //    private Actor actor;
    private static AudioClip audioClip;

    public PrairieStory(boolean isNewGame) {
        Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
        x = (int) (d.getWidth() * .9);
        y = (int) (d.getHeight() * .9);
        if (isNewGame) {
//            actor = new Actor();
            field = new Field(x / GRID_SIZE, (y - 70) / GRID_SIZE);
            //初始化
            field.init();
            init();
        } else {
//            actor = field.getActor();
            init();
            field.initAudio();
        }

//        while (actor.isAlive()) {
//            if (!actor.move().equals(actor.getLocation())) {
//                //如果没有操作,则不执行
//                if (field.actorMove(actor, actor.move())) {
//                    actorAction();
//                } else {
//                    if (!theView.isDie()) {
//                        theView.die();
//                    }
//                    theView.repaint();
//                }
//            }
//            try {
//                Thread.sleep(speed);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//        }

    }

    public static void main(String[] args) {
        PrairieStory game = null;
        try {
            if (archive.exists()) {
                try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(archive))) {
                    field = (Field) in.readObject();
                } catch (Exception e) {
                    e.printStackTrace();
                    archive.delete();
                    throw e;
                }
                game = new PrairieStory(false);

                //创建一个选择弹出窗口，默认三选项：是，否，取消，返回值对应为0,1,2
                int num = JOptionPane.showConfirmDialog(null, "是否继续上次的游戏?");
                if (num == 0) {
//                story.start();
                } else if (num == 1) {
                    game.dispose();
                    if (audioClip != null) {
                        audioClip.stop();
                    }
                    game = new PrairieStory(true);
                } else {
                    System.exit(0);
                }
            } else {
                game = new PrairieStory(true);
            }
        } catch (Exception e) {
            e.printStackTrace();
            game = new PrairieStory(true);
        }
        game.gameStart();
    }

    private void gameStart() {
        while (!pause) {
            step();
            try {
                Thread.sleep(speed);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void init() {
        try {
            audioClip = Applet.newAudioClip(this.getClass().getResource("/resource/背景音乐.wav"));
            audioClip.loop();
        } catch (Exception e) {
            e.printStackTrace();
        }
//        addKeyListener(this);
        theView = new View(field, GRID_SIZE);
        round = new Round(field);
        theView.setBackground(Color.yellow);
//        theView.setSize(0, 0);  //发现panel组件设置大小没效果, 原因是布局管理器layout

        setTitle("青青草原的故事");
        setSize(x, y);
        setFrameCenter(this);//窗口左上角位置设置为屏幕中间
//        setLayout(new FlowLayout());

        //关闭窗口时自动存档
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                pause = true;
//                if (!theView.isDie()) {
                try (ObjectOutputStream out = new ObjectOutputStream(
                        new FileOutputStream(archive))) {
                    field.clearAudio();
                    out.writeObject(field);
                } catch (FileNotFoundException e1) {
                    e1.printStackTrace();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
//                } else {
//                    archive.delete();
//                }
//                System.out.println("保存成功");
//                dispose();
//                JOptionPane.showMessageDialog(PrairieStory.this, "保存成功");
                System.exit(0);
            }
        });

        //创建新的面板区域
        JPanel panel = new JPanel();
        panel.setBackground(Color.LIGHT_GRAY);
        label = new JLabel();
        label.setFont(new Font("楷体", Font.BOLD, 20));
        label.setText("操作说明: 通过【方向键】(精确)或【W A S D】(快速) 来移动, 按【空格键】可以自动捕食范围内的猎物(食物)");
        panel.add(label);
//        panel.add(btnPause);
//        panel.add(btnStep);
//        panel.add(button);
//        panel.add(speedUp);
//        panel.add(speedDown);
//        panel.setBackground(Color.black);

        add(theView, BorderLayout.CENTER);
        add(panel, BorderLayout.SOUTH);
//        add(btnPause);
//        add(btnStep);
//        pack();
        setResizable(false);
        setVisible(true);
        pack();
        requestFocus();

//        setBackground(Color.orange);
//        getContentPane().setBackground(Color.BLUE);
    }

    private static void setFrameCenter(JFrame frame) {
        Toolkit kit = Toolkit.getDefaultToolkit();
        Dimension d = kit.getScreenSize();
        double screenWidth = d.getWidth();
        double screenHeight = d.getHeight();
        int width = frame.getWidth();
        int height = frame.getHeight();
        frame.setLocation((int) (screenWidth - width) / 2, (int) (screenHeight - height) / 2);
    }

//    @Override
//    public void keyTyped(KeyEvent e) {
//
//    }

//    @Override
//    public void keyPressed(KeyEvent e) {
//        Location location = null;
//        switch (e.getKeyCode()) {
//            case KeyEvent.VK_UP:
//                location = actor.front();
//                break;
//            case KeyEvent.VK_DOWN:
//                location = actor.back();
//                break;
//            case KeyEvent.VK_LEFT:
//                location = actor.left();
//                break;
//            case KeyEvent.VK_RIGHT:
//                location = actor.right();
//                break;
//            case KeyEvent.VK_W:
//                actor.front = true;
//                break;
//            case KeyEvent.VK_S:
//                actor.back = true;
//                break;
//            case KeyEvent.VK_A:
//                actor.left = true;
//                break;
//            case KeyEvent.VK_D:
//                actor.right = true;
//                break;
//            case 32://空格自动捕食
//                location = new Location(-1, -1);
//                break;
//            case 10://回车
//                location = new Location(-100, -100);
//                break;
//        }
//        if (location != null) {
//            if (actor.isAlive() && field.actorMove(actor, location)) {
//                actorAction();
//            } else {
//                if (!theView.isDie()) {
//                    theView.die();
//                }
//                theView.repaint();
//                if (e.getKeyCode() == 10) {
//                    if (audioClip != null) {
//                        audioClip.stop();
//                    }
//                    round.stopAudio();
//
//                    dispose();
//                    new PrairieStory(true);
//                }
//            }
//        }
//    }

//    @Override
//    public void keyReleased(KeyEvent e) {
//        switch (e.getKeyCode()) {
//            case KeyEvent.VK_W:
//                actor.front = false;
//                break;
//            case KeyEvent.VK_S:
//                actor.back = false;
//                break;
//            case KeyEvent.VK_A:
//                actor.left = false;
//                break;
//            case KeyEvent.VK_D:
//                actor.right = false;
//                break;
//        }
//    }

    private void step() {
        theView.setTime(++time);
        round.oneRound((int) time);
        theView.repaint();
//        if (actor.getRemainingTime() > 0) {
//            label.setText("注意! 你将在 " + actor.getRemainingTime() + " 天后饿死! 可以通过【捕食羊、狼(危险)或者吃植物】来增加或者维持生命! 小心被狼吃了!");
//        } else {
//            label.setFont(new Font("楷体", 3, 20));
//            label.setText("有限的食物永远是导致大部分生物死亡的主要原因! 所以为了生存, 努力捕猎吧! (植物只能维持温饱)");
//        }

    }
}