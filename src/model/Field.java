package model;

import model.biology.Biology;
import model.biology.animal.Human;
import model.biology.animal.Sheep;
import model.biology.animal.Wolf;
import model.biology.plant.Grass;
import model.biology.plant.Plant;

import java.applet.Applet;
import java.applet.AudioClip;
import java.io.File;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

/**
 * 容纳所有cell的场地
 * @author Yhaobo
 */
public class Field implements Serializable {
    private static final long serialVersionUID = 42L;
    private static final Location[] adjacent = {new Location(-1, -1), new Location(-1, 0),
            new Location(-1, 1), new Location(0, -1),
            new Location(0, 1), new Location(1, -1),
            new Location(1, 0), new Location(1, 1)};
    private final int width;
    private final int height;
    private Cell[][] field;
    private Actor actor;
    AudioClip wolfAudio;
    AudioClip sheepAudio;

    public Field(int width, int height) {
        this.width = width;
        this.height = height;
        field = new Cell[height][width];
        try {
            wolfAudio = Applet.newAudioClip(this.getClass().getResource("/resource/狼叫声.wav"));
            sheepAudio = Applet.newAudioClip(this.getClass().getResource("/resource/羊叫声.wav"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Actor getActor() {
        return actor;
    }

    public void init(Actor actor) {
        this.actor = actor;
        for (int row = 0; row < height; row++) {
            for (int col = 0; col < width; col++) {
                double probability = Math.random();
                if (probability < 0.001) {
                    place(row, col, new Wolf((int) (Math.random() * 3650)));
                } else if (probability < 0.01) {
                    place(row, col, new Sheep((int) (Math.random() * 3650)));
                } else {
                    place(row, col, new Grass((int) (Math.random() * 3650)));
                }
            }
        }
        place(height / 2, width / 2, actor);
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    /**
     *  放置格子(把动物放进去)
     */
    public void place(int row, int col, Cell cell) {
        Biology biology = (Biology) cell;
        biology.setLocation(row, col);
        field[row][col] = biology;
//		return field[row][col];
    }

    public Cell get(int row, int col) {
        return field[row][col];
    }

    /**
     * 得到周围一圈的所有cell
     * @param row
     * @param col
     * @return
     */
    public Cell[] getNeighbour(int row, int col) {
        ArrayList<Cell> list = new ArrayList<>();
        for (Location loc : adjacent) {
            int r = row + loc.getColumn();
            int c = col + loc.getRow();
            if (r > -1 && r < height && c > -1 && c < width && !(r == row && c == col)) {
                list.add(field[r][c]);
            }
        }
        return list.toArray(new Cell[list.size()]);
    }

    public Cell[] WolfgetNeighbour(int row, int col) {
        ArrayList<Cell> list = new ArrayList<>();
        for (int i = -2; i <= 2; i++) {
            for (int j = -2; j <= 2; j++) {
                int r = row + i;
                int c = col + j;
                if (r > -1 && r < height && c > -1 && c < width && !(r == row && c == col)) {
                    list.add(field[r][c]);
                }
            }
        }
        return list.toArray(new Cell[list.size()]);
    }

    public Location[] getFreeNeighbour(int row, int col) {
        ArrayList<Location> list = new ArrayList<>();

//		for (int i = -1; i < 2; i++) {
//			for (int j = -1; j < 2; j++) {
//				int r = row + i;
//				int c = col + j;
//				if (r > -1 && r < height && c > -1 && c < width && field[r][c] == null) {
//					list.add(new Location(r, c));
//				}
//			}
//		}

        for (Location loc : adjacent) {
            int r = row + loc.getColumn();
            int c = col + loc.getRow();
            if (r > -1 && r < height && c > -1 && c < width && field[r][c] == null) {
                list.add(new Location(r, c));
            }
        }
        return list.toArray(new Location[list.size()]);
    }

    public Location[] getNeighbourLocation(int row, int col) {
        ArrayList<Location> list = new ArrayList<>();

//		for (int i = -1; i < 2; i++) {
//			for (int j = -1; j < 2; j++) {
//				int r = row + i;
//				int c = col + j;
//				if (r > -1 && r < height && c > -1 && c < width && field[r][c] == null) {
//					list.add(new Location(r, c));
//				}
//			}
//		}

        for (Location loc : adjacent) {
            int r = row + loc.getColumn();
            int c = col + loc.getRow();
            if (r > -1 && r < height && c > -1 && c < width) {
                list.add(new Location(r, c));
            }
        }
        return list.toArray(new Location[list.size()]);
    }
//    public void average() {
//        System.out.println(sum / count);
//    }

    public Location[] PlantgetFreeNeighbour(int row, int col) {
        ArrayList<Location> list = new ArrayList<>();
        int scope = 7;//范围
        for (int i = -scope; i <= scope; i++) {
            for (int j = -scope; j <= scope; j++) {
                int r = row + i;
                int c = col + j;
                if (r >= 0 && r < height && c >= 0 && c < width && field[r][c] == null) {
                    list.add(new Location(r, c));
                }
            }
        }
//        for (Location loc : adjacent) {
//            int r = row + loc.getColumn();
//            int c = col + loc.getRow();
//            if (r > -1 && r < height && c > -1 && c < width && field[r][c] == null) {
//                list.add(new Location(r, c));
//            }
//        }
        return list.toArray(new Location[list.size()]);
    }

    //放置新生命
    public boolean placeRandomAdj(int row, int col, Cell cell) {
        Biology biology = (Biology) cell;
        boolean ret = false;
        Location[] freeAdj;
        if (cell instanceof Plant) {
            freeAdj = PlantgetFreeNeighbour(row, col);
        } else {
            freeAdj = getNeighbourLocation(row, col);
        }
        if (freeAdj.length > 0) {
            int idx = (int) (Math.random() * freeAdj.length);
            int idxRow=freeAdj[idx].getRow();
            int idxColumn = freeAdj[idx].getColumn();
            biology.setLocation(idxRow, idxColumn);//设置坐标属性
            field[idxRow][idxColumn] = biology;//放置
            ret = true;
        }
        return ret;
    }

    public void remove(int row, int col) {
        field[row][col] = null;
    }

    public void remove(Cell cell) {
        here:
        for (int row = 0; row < height; row++) {
            for (int col = 0; col < width; col++) {
                if (field[row][col] == cell) {
                    field[row][col] = null;
                    break here;
                }
            }
        }
    }

    public void eat(Cell hunter, Cell prey) {
        Biology hunterB = (Biology) hunter;
        Biology preyB = (Biology) prey;
        field[preyB.getRow()][preyB.getColumn()] = hunterB;
        field[hunterB.getRow()][hunterB.getColumn()] = null;
        hunterB.setLocation(preyB.getRow(), preyB.getColumn());
//        int count = 0;
//        for (int row = 0; row < height; row++) {
//            for (int col = 0; col < width; col++) {
//                if (field[row][col] == hunter) {
//                    field[row][col] = null;
//                    count++;
//                }
//                if (field[row][col] == prey) {
//                    field[row][col] = hunter;
//                    count++;
//                }
//                if (count == 2) {
//                    return;
//                }
//            }
//        }
//        here:
//        for (int row = 0; row < height; row++) {
//            for (int col = 0; col < width; col++) {
//                if (field[row][col] == prey) {
//                    field[row][col]=null;
//                    field[row][col] = hunter;
//                    break here;
//                }
//            }
//        }
//        remove(hunter);
    }

    public boolean actorMove(Actor actor, Location loc) {
//        if (loc == null) {
//            return false;
//        }
        //判断actor有没有被吃掉,如果被吃掉则返回false
        if (actor.isAlive() && (get(actor.getRow(), actor.getColumn()) instanceof Actor)) {
            int rowLoc = loc.getRow();
            int colLoc = loc.getColumn();
            short eatWolf = 365;
            short eatSheep = 7;
            short eatPlant = 1;
            //actor的feed()的实现
            if (rowLoc > -1 && colLoc > -1 && rowLoc < height && colLoc < width && !(field[rowLoc][colLoc] instanceof Human)) {
                if (field[rowLoc][colLoc] instanceof Wolf) {
                    actor.longerLife(eatWolf, actor.LIFE_TIME);
                    wolfAudio.play();
                } else if (field[rowLoc][colLoc] instanceof Sheep) {
                    actor.longerLife(eatSheep, actor.LIFE_TIME);
                    if (sheepAudio != null) {
                        sheepAudio.stop();
                    }
                    sheepAudio.play();
                } else if (field[rowLoc][colLoc] instanceof Grass) {
                    actor.longerLife(eatPlant, actor.LIFE_TIME);
                }
                remove(actor.getRow(), actor.getColumn());
                actor.setLocation(loc.getRow(), loc.getColumn());
                field[rowLoc][colLoc] = actor;
                return true;
            }
            //空格键快速捕食
            if (rowLoc == -1 && colLoc == -1) {
                Cell[] cells = getNeighbour(actor.getRow(), actor.getColumn());
                boolean flag = false;// 避免吃完动物继续吃植物
                for (Cell cell : cells) {
                    if (cell instanceof Wolf) {
                        wolfAudio.play();
                        eat(actor, cell);
                        actor.longerLife(eatWolf, actor.LIFE_TIME);
                        flag = true;
                    } else if (cell instanceof Sheep) {
                        if (sheepAudio != null) {
                            sheepAudio.stop();
                        }
                        sheepAudio.play();
                        eat(actor, cell);
                        actor.longerLife(eatSheep, actor.LIFE_TIME);
                        flag = true;
                        break;
                    }
                }
                if (!flag) {
                    List<Cell> list = new ArrayList<>();
                    for (Cell cell : cells) {
                        if (cell instanceof Grass) {
                            list.add(cell);
                        }
                    }
                    if (list.size() > 0) {
                        Cell prey = list.get((int) (list.size() * Math.random()));
                        eat(actor, prey);
                        actor.longerLife(eatPlant, actor.LIFE_TIME);
                    }
                }
            }
            return true;
        }
        return false;
    }

    public void move(int row, int col, Location loc) {
        if (loc == null) {
            return;
        }
        Biology biology = (Biology) field[row][col];
        biology.setLocation(loc.getRow(), loc.getColumn());
        field[loc.getRow()][loc.getColumn()] = biology;
        remove(row, col);
    }

    public void clearAudio() {
       wolfAudio=null;
       sheepAudio=null;
    }

    public void initAudio() {
        try {
            wolfAudio = Applet.newAudioClip(new File("resource/狼叫声.wav").toURI().toURL());
            sheepAudio = Applet.newAudioClip(new File("resource/羊叫声.wav").toURI().toURL());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }
}
