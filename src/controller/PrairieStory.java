package controller;

import model.Field;
import model.GameAudio;
import model.Round;
import model.entity.Location;
import model.entity.biology.animal.Human;
import model.entity.biology.animal.Wolf;
import model.entity.biology.animal.WolfPlayer;
import model.interfaces.Cell;
import model.interfaces.Player;
import view.View;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author Yhaobo
 */
public class PrairieStory extends JFrame implements KeyListener, MouseMotionListener, MouseWheelListener, MouseListener {
    private static Field field;
    private View view;
    private JLabel label;
    private Round round;
    private static Player player;
    private Cell placeCell;
    private static File archive = new File("青青草原的故事.data");
    private volatile boolean isPause = true;
    private volatile boolean isRestart;
    private volatile boolean isExit;
    private int speed = 50;
    /**
     * 既是时间(回合数)也是版本号
     */
    private long roundNumber;
    /**
     * 窗口像素宽
     */
    private final int windowWidth;
    /**
     * 窗口像素高
     */
    private final int windowHeight;
    /**
     * 格子的边长(大小)
     */
    public static byte gridSize = 4;
    /**
     * 系统可用cpu核心数-2(减去Swing的toolkit线程和事件派发线程EDT(Event Dispatcher Thread）)
     */
    public static final int availableProcessors = Runtime.getRuntime().availableProcessors() - 2 < 1 ? 1 : Runtime.getRuntime().availableProcessors() - 2;

    /**
     * 没有等待队列的线程池,且拒绝策略为调用者运行, 核心线程数和最大线程数都为系统可用cpu核心数
     */
    public static final ThreadPoolExecutor threadPool = new ThreadPoolExecutor(availableProcessors, availableProcessors,
            1, TimeUnit.MINUTES, new SynchronousQueue<>(), new ThreadPoolExecutor.CallerRunsPolicy());

    public PrairieStory(boolean isNewGame) {
        // 获取屏幕的宽高
        Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
        windowWidth = (int) (d.getWidth() * 0.95);
        windowHeight = (int) (d.getHeight() * 0.95);
        if (isNewGame) {
            // 新游戏
            field = new Field(windowWidth / gridSize, (windowHeight - 70) / gridSize, player);
            player.setField(field);
            //初始化
            field.init();
        } else {
            // 继续上次游戏
            player = field.getPlayer();
            roundNumber = field.getVersion();
        }
        view = new View(field, field.getGameStartTime(), player, windowWidth, windowHeight - 70);

        round = new Round(field, view);
        this.init();
    }

    public static void main(String[] args) {
        try {
            final PrairieStory[] game = {null};
            // swing相关的操作都交给事件处理线程
            SwingUtilities.invokeAndWait(() -> {
                try {
//                    if (archive.exists()) {
//                        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(archive))) {
//                            field = (Field) in.readObject();
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                            archive.delete();
//                            throw e;
//                        }
//                        game[0] = new PrairieStory(false);
//                        //创建一个选择弹出窗口，默认三选项：是，否，取消，返回值对应为0,1,2
//                        int result = JOptionPane.showConfirmDialog(null, "继续上次游戏?", "", JOptionPane.YES_NO_OPTION);
//                        if (result == JOptionPane.NO_OPTION) {
//                            result = JOptionPane.showConfirmDialog(null, "是否扮演<恐怖直立猿>?", "", JOptionPane.YES_NO_OPTION);
//                            if (result == JOptionPane.YES_OPTION) {
//                                PrairieStory.player = new HumanPlayer(field);
//                            } else if (result == JOptionPane.NO_OPTION) {
//                                PrairieStory.player = new WolfPlayer();
//                            } else {
//                                System.exit(0);
//                            }
//                            game[0].dispose();
////                            game[0].round.stopAudio();
//                            game[0] = new PrairieStory(true);
//                        }
//                    } else {
//                        int result = JOptionPane.showConfirmDialog(null, "是否扮演<恐怖直立猿>?", "", JOptionPane.YES_NO_OPTION);
//                        if (result == JOptionPane.YES_OPTION) {
//                            PrairieStory.player = new HumanPlayer();
//                        } else if (result == JOptionPane.NO_OPTION) {
                    PrairieStory.player = new WolfPlayer();
//                        } else {
//                            System.exit(0);
//                        }
                    game[0] = new PrairieStory(true);
//                    }
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
        player.reset();
        field.init();
        roundNumber = 0;
        view.setGameStartTime(System.currentTimeMillis());
        isRestart = false;
    }

    private void start() {
        while (!isExit) {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            while (!isPause) {
                if (isRestart) {
                    // 游戏重置
                    restart();
                }
                loop();
                try {
                    Thread.sleep(speed);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        // 游戏结束之后自动保存
        saveToDisk();
    }

    private void loop() {
        view.setTime(++roundNumber);
        round.oneRoundByLoop((int) roundNumber);
        view.repaint();
    }

    private void step() {
        view.setTime(++roundNumber);
        round.oneRoundByPlayer((int) roundNumber);
        view.repaint();
        if (player instanceof Human) {
            if (((Human) player).getRemainTime() >= -1) {
                label.setText("注意! 你将在 " + (((Human) player).getRemainTime() + 1) + " 天后饿死! 可以通过【捕食羊、狼(危险)或者吃植物】来增加或者维持生命! 小心被狼吃了!");
            } else {
                label.setFont(new Font("楷体", 3, 20));
                label.setText("有限的食物永远是导致大部分生物死亡的主要原因! 所以为了生存, 努力捕猎吧! (植物只能维持温饱)");
            }
        } else if (player instanceof Wolf) {

        }

    }

    private void init() {
        view.addKeyListener(this);
        view.addMouseMotionListener(this);
        view.addMouseWheelListener(this);
        view.addMouseListener(this);
        view.setBackground(new Color(255, 148, 0));

        setTitle("青青草原的故事");
        setSize(windowWidth, windowHeight);
        setWindowCenter(this);

        //关闭窗口时自动存档
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                isPause = true;
                isExit = true;
            }
        });

        //创建新的面板区域
        JPanel functionPanel = new JPanel();
        functionPanel.setMaximumSize(new Dimension(windowWidth, 70));
        functionPanel.setBackground(Color.LIGHT_GRAY);

        JButton highSpeed = new JButton("快速");
        highSpeed.addActionListener(e -> speed = 25);
        JButton midSpeed = new JButton("中速");
        midSpeed.addActionListener(e -> speed = 50);
        JButton slowSpeed = new JButton("慢速");
        slowSpeed.addActionListener(e -> speed = 100);

        functionPanel.add(highSpeed);
        functionPanel.add(midSpeed);
        functionPanel.add(slowSpeed);

        label = new JLabel();
        label.setFont(new Font("楷体", Font.BOLD, 20));
        label.setText("操作说明: 按【空格键】解除暂停或暂停,鼠标滚轮缩放视野,通过【W A S D】移动视野");
        functionPanel.add(label);

        JButton placeWolf = new JButton("放置狼");
        placeWolf.addActionListener(e -> placeCell = new Wolf());
        JButton placeHuman = new JButton("放置人");
        placeHuman.addActionListener(e -> placeCell = new Human());

        functionPanel.add(placeWolf);
        functionPanel.add(placeHuman);

        add(view, BorderLayout.CENTER);
        add(functionPanel, BorderLayout.SOUTH);
//        add(btnPause);
//        add(btnStep);
        //窗口不能改变大小
        setResizable(false);
        //窗口自适应内容大小
        pack();
        requestFocus();
        setVisible(true);

        view.requestFocus();

//        setBackground(Color.orange);
//        getContentPane().setBackground(Color.BLUE);
    }

    /**
     * 将field对象保存至磁盘
     */
    private void saveToDisk() {
//        if (!player.isDie()) {
//            try (ObjectOutputStream out = new ObjectOutputStream(
//                    new FileOutputStream(archive))) {
//                field.setVersion((int) roundNumber);
//                field.setGameStartTime(view.getGameStartTime());
//                out.writeObject(field);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        } else {
//            archive.delete();
//        }
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
        Location location = null;
        Location centerPointLocation = view.getDestCenterLocation();
        switch (e.getKeyCode()) {
//            case KeyEvent.VK_UP:
//                location = player.front();
//                break;
//            case KeyEvent.VK_DOWN:
//                location = player.back();
//                break;
//            case KeyEvent.VK_LEFT:
//                location = player.left();
//                break;
//            case KeyEvent.VK_RIGHT:
//                location = player.right();
//                break;
            case KeyEvent.VK_W:
                centerPointLocation.setRow(centerPointLocation.getRow() - 1);
                break;
            case KeyEvent.VK_S:
                centerPointLocation.setRow(centerPointLocation.getRow() + 1);
                break;
            case KeyEvent.VK_A:
                centerPointLocation.setColumn(centerPointLocation.getColumn() - 1);
                break;
            case KeyEvent.VK_D:
                centerPointLocation.setColumn(centerPointLocation.getColumn() + 1);
                break;
            case KeyEvent.VK_SPACE:
                if (!isPause) {
                    GameAudio.getMusic().stop();
                } else {
                    GameAudio.getMusic().loop();
                }
                isPause = !isPause;

//                if (!isPause) {
//                    isPause = true;
//                    try {
//                        Thread.sleep(200);
//                    } catch (InterruptedException exception) {
//                        exception.printStackTrace();
//                    }
//                }
//                if (threadPool.getTaskCount() == threadPool.getCompletedTaskCount() && threadPool.getActiveCount() == 0) {
//                    threadPool.execute(this::step);
//                }
                break;
            case 8:
                // 退格键
                isPause = false;
                isRestart = true;
                break;
            case KeyEvent.VK_END:
                // 重新开始
                restart();
            default:

        }
//        if (threadPool.getTaskCount() == threadPool.getCompletedTaskCount() && threadPool.getActiveCount() == 0) {
//            Location finalLocation = location;
//            threadPool.execute(() -> {
//                if (player.isAlive()) {
//                    if (finalLocation != null) {
//                        player.playerMove(finalLocation);
//                    } else {
//                        player.playerQuickMove();
//                    }
//                    view.repaint();
//                    try {
//                        Thread.sleep(200);
//                    } catch (InterruptedException exception) {
//                        exception.printStackTrace();
//                    }
//                    player.playerEat();
//                    step();
//                } else {
//
//                }
//            });
//        }

        if (centerPointLocation != null) {
            view.setDestCenterLocation(centerPointLocation);
        }
        view.repaint();
    }

    @Override
    public void keyReleased(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_W:
                player.setFront(false);
                break;
            case KeyEvent.VK_S:
                player.setBack(false);
                break;
            case KeyEvent.VK_A:
                player.setLeft(false);
                break;
            case KeyEvent.VK_D:
                player.setRight(false);
                break;
            default:
        }
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        if (mouseButton == MouseEvent.BUTTON3) {
            Location location = new Location(e.getY() / gridSize + view.getStartRow(), e.getX() / gridSize + view.getStartCol());
            view.setDestCenterLocation(location);
            view.repaint();
        } else if (mouseButton == MouseEvent.BUTTON1) {
            if (placeCell instanceof Wolf) {
                field.place(e.getY() / gridSize + view.getStartRow(), e.getX() / gridSize + view.getStartCol(), new Wolf());
            } else if (placeCell instanceof Human) {
                field.place(e.getY() / gridSize + view.getStartRow(), e.getX() / gridSize + view.getStartCol(), new Human());
            }
            view.repaint();

        }
    }

    @Override
    public void mouseMoved(MouseEvent e) {
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        int wheelRotation = e.getWheelRotation();
        if (gridSize > 4 && gridSize < 50) {
            gridSize += -wheelRotation;
        } else if (-wheelRotation > 0 && gridSize < 50) {
            gridSize += -wheelRotation;
        } else if (-wheelRotation < 0 && gridSize > 4) {
            gridSize += -wheelRotation;
        }
        if (e.getWheelRotation() < 0) {
            Location location = new Location(e.getY() / gridSize + view.getStartRow(), e.getX() / gridSize + view.getStartCol());
            view.setDestCenterLocation(location);
        }

//        view.setCenterPointLocation(view.getCenterPointLocation());
        view.repaint();
    }

    @Override
    public void mouseClicked(MouseEvent e) {
    }

    private int mouseButton;

    @Override
    public void mousePressed(MouseEvent e) {
        mouseButton = e.getButton();
        if (e.getButton() == MouseEvent.BUTTON1) {
            if (placeCell instanceof Wolf) {
                field.place(e.getY() / gridSize + view.getStartRow(), e.getX() / gridSize + view.getStartCol(), new Wolf());
            } else if (placeCell instanceof Human) {
                field.place(e.getY() / gridSize + view.getStartRow(), e.getX() / gridSize + view.getStartCol(), new Human());
            }
            view.repaint();
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {
        e.getComponent().requestFocus();
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }
}