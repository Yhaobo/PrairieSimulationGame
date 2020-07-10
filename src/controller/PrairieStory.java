package controller;

import model.Field;
import model.Round;
import model.interfaces.Player;
import view.View;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author Yhaobo
 */
public class PrairieStory extends JFrame implements KeyListener {
    private static Field field;
    private View view;
    private JLabel label;
    private Round round;
    private Player player;
    private static File archive = new File("存档.data");
    private volatile boolean isPause;
    private volatile boolean isRestart;
    private volatile boolean isExit;
    private int speed = 10;
    /**
     * 既是时间(回合数)也是版本号
     */
    private long roundNumber;
    /**
     * 窗口像素宽
     */
    private int windowWidth;
    /**
     * 窗口像素高
     */
    private int windowHeight;
    /**
     * 格子的边长(大小)
     */
    private static final byte GRID_SIZE = 7;

    int threadNum = Runtime.getRuntime().availableProcessors() / 2;
    private ThreadPoolExecutor threadPool = new ThreadPoolExecutor(1, threadNum,
            100, TimeUnit.SECONDS, new ArrayBlockingQueue<>(threadNum * 3));

    public PrairieStory(boolean isNewGame) {
        Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
        windowWidth = (int) (d.getWidth() * 0.95);
        windowHeight = (int) (d.getHeight() * 0.95);
        if (isNewGame) {
//            actor = new Actor();
            field = new Field(windowWidth / GRID_SIZE, (windowHeight - 70) / GRID_SIZE);
            //初始化
            field.init();
        } else {
            player = field.getPlayer();
            roundNumber = field.getVersion();
        }
        this.init();
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
        try {
            final PrairieStory[] game = {null};
            SwingUtilities.invokeAndWait(() -> {
                try {
                    if (archive.exists()) {
                        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(archive))) {
                            field = (Field) in.readObject();
                        } catch (Exception e) {
                            e.printStackTrace();
                            archive.delete();
                            throw e;
                        }
                        game[0] = new PrairieStory(false);

//                        //创建一个选择弹出窗口，默认三选项：是，否，取消，返回值对应为0,1,2
//                        int num = JOptionPane.showConfirmDialog(null, "是否扮演<恐怖直立猿>?");
//                        if (num == 0) {
//                            game[0].player = new HumanPlayer();
//                        } else if (num == 1) {
//                            game[0].player = new WolfPlayer();
//                        } else {
//                            System.exit(0);
//                        }
                    } else {
                        game[0] = new PrairieStory(true);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    game[0] = new PrairieStory(true);
                }
            });
            if (game[0] != null) {
                game[0].view.repaint();
                game[0].start();
            }
        } catch (InterruptedException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    private void restart() {
        field.init();
        roundNumber = 0;
        view.setStartTime(System.currentTimeMillis());
        isRestart = false;
    }

    private void start() {
        while (!isExit) {
            while (!isPause) {
                if (isRestart) {
                    // 游戏重置
                    restart();
                }
                step();
                try {
                    Thread.sleep(speed);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        // 游戏结束之后自动保存
        saveToDisk();
    }

    private void init() {
        addKeyListener(this);
        view = new View(field, GRID_SIZE);
        round = new Round(field, view, threadPool);
        view.setBackground(new Color(255, 148, 0));
//        theView.setSize(0, 0);  //发现panel组件设置大小没效果, 原因是布局管理器layout

        setTitle("青青草原的故事");
        setSize(windowWidth, windowHeight);
        setWindowCenter(this);
//        setLayout(new FlowLayout());

        //关闭窗口时自动存档
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                isPause = true;
                isExit = true;
            }
        });

        //创建新的面板区域
        JPanel panel = new JPanel();
        panel.setBackground(Color.LIGHT_GRAY);
        label = new JLabel();
        label.setFont(new Font("楷体", Font.BOLD, 20));
        label.setText("操作说明: 通过【方向键】(精确)或【W A S D】(快速) 来移动, 按【空格键】可以自动捕食范围内的猎物(食物)");
        panel.add(label);

        add(view, BorderLayout.CENTER);
        add(panel, BorderLayout.SOUTH);
//        add(btnPause);
//        add(btnStep);
        //窗口不能改变大小
        setResizable(false);
        //窗口自适应内容大小
        pack();
        requestFocus();
        setVisible(true);

//        setBackground(Color.orange);
//        getContentPane().setBackground(Color.BLUE);
    }

    private void saveToDisk() {
//                if (!theView.isDie()) {
        try (ObjectOutputStream out = new ObjectOutputStream(
                new FileOutputStream(archive))) {
            field.setVersion((int) roundNumber);
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
//                JOptionPane.showMessageDialog(PrairieStory.this, "保存成功");
        System.exit(0);
    }

    private static void setWindowCenter(JFrame frame) {
        Toolkit kit = Toolkit.getDefaultToolkit();
        Dimension d = kit.getScreenSize();
        double screenWidth = d.getWidth();
        double screenHeight = d.getHeight();
        int width = frame.getWidth();
        int height = frame.getHeight();
        frame.setLocation((int) (screenWidth - width) / 2, (int) (screenHeight - height) / 3);
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
//        System.out.println(e.getKeyCode());
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
//            case 32://空格
//                break;
//            case 10://回车
//                break;
//        }
//            if (actor.isAlive() && field.actorMove(actor, location)) {
//                actorAction();
//            } else {
//                if (!theView.isDie()) {
//                    theView.die();
//                }
//            theView.repaint();
        if (e.getKeyCode() == 8) {
            isPause = false;
            isRestart = true;
        } else if (e.getKeyCode() == 32) {
            if (!isPause) {
                isPause = true;
                try {
                    Thread.sleep(200);
                } catch (InterruptedException exception) {
                    exception.printStackTrace();
                }
            }
//            System.out.println("getCorePoolSize:"+threadPool.getCorePoolSize());
//            System.out.println("getPoolSize:"+threadPool.getPoolSize());
//            System.out.println("getMaximumPoolSize:"+threadPool.getMaximumPoolSize());
//            System.out.println("getActiveCount:"+threadPool.getActiveCount());
//            System.out.println("getLargestPoolSize:"+threadPool.getLargestPoolSize());
//            System.out.println("getTaskCount:"+threadPool.getTaskCount());
//            System.out.println("getCompletedTaskCount:"+threadPool.getCompletedTaskCount());

            if (threadPool.getTaskCount() == threadPool.getCompletedTaskCount()) {
                threadPool.execute(this::step);
            }
        }
//            }

    }

    @Override
    public void keyReleased(KeyEvent e) {
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
    }

    private void step() {
        view.setTime(++roundNumber);
        round.oneRound((int) roundNumber);
        view.repaint();
//        if (player instanceof Human) {
//            Human player = (Human) this.player;
//            if (player.getRemainTime() > 0) {
//                label.setText("注意! 你将在 " + player.getRemainTime() + " 天后饿死! 可以通过【捕食羊、狼(危险)或者吃植物】来增加或者维持生命! 小心被狼吃了!");
//            } else {
//                label.setFont(new Font("楷体", 3, 20));
//                label.setText("有限的食物永远是导致大部分生物死亡的主要原因! 所以为了生存, 努力捕猎吧! (植物只能维持温饱)");
//            }
//        } else if (player instanceof Wolf) {
//
//        }

    }
}