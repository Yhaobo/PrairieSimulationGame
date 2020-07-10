package model;

import model.entity.Location;
import model.entity.biology.Biology;
import model.entity.biology.animal.Animal;
import model.entity.biology.animal.Human;
import model.entity.biology.animal.Sheep;
import model.entity.biology.animal.Wolf;
import model.entity.biology.plant.Plant;
import model.interfaces.Cell;
import model.interfaces.Player;
import util.ConstantNum;
import util.MyUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 容纳所有Cell的场地
 *
 * @author Yhaobo
 */
public class Field implements Serializable {
    private static final long serialVersionUID = 42L;
    private final int width;
    private final int height;
    /**
     * 由Cell组成的矩阵
     */
    private final Cell[][] cells;
    private Player player;

    /**
     * 存档时才保存的版本号
     */
    private int version;

    /**
     * 为了提高性能,把半径从1到Plant.BREED_SCOPE的相对位置都生成好
     */
    private static final List<List<Location>> RELATIVE_LOCATION_LISTS = new ArrayList<>(Plant.BREED_SCOPE);

    static {
        for (int i = 0; i < Plant.BREED_SCOPE; i++) {
            RELATIVE_LOCATION_LISTS.add(MyUtils.generateSenseSope(i + 1));
        }
    }

    public Field(int width, int height) {
        this.width = width;
        this.height = height;
        System.out.println("格子数: " + width * height);
        this.cells = new Cell[height][width];
        try {

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Player getPlayer() {
        return player;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public long getVersion() {
        return version;
    }

    public void init() {
//        this.actor = actor;
        for (int row = 0; row < height; row++) {
            for (int col = 0; col < width; col++) {
                double probability = Math.random();
                if (probability < 0.001) {
                    place(row, col, new Human((int) (Math.random() * ConstantNum.ONE_YEAR_DAYS.value)));
                } else if (probability < 0.003) {
                    place(row, col, new Wolf((int) (Math.random() * ConstantNum.ONE_YEAR_DAYS.value)));
                } else if (probability < 0.03) {
                    place(row, col, new Sheep((int) (Math.random() * ConstantNum.ONE_YEAR_DAYS.value)));
                } else {
                    place(row, col, new Plant((int) (Math.random() * ConstantNum.ONE_YEAR_DAYS.value)));
                }
            }
        }
        place(height / 2, width / 2, new Human());
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    /**
     * 放置格子(把动物放进去)
     */
    public void place(int row, int col, Cell cell) {
        cell.setLocation(row, col);
        place(cell);
    }

    private void place(Cell cell) {
        cells[cell.getRow()][cell.getColumn()] = cell;
    }

    public Cell getCell(int row, int col) {
        if (row >= 0 && row < height && col >= 0 && col < width) {
            return cells[row][col];
        }
        return null;
    }

    /**
     * 得到周围半径为radius的所有生物(包括已死亡)
     *
     * @param row
     * @param col
     * @return
     */
    public List<Biology> getNeighbour(int row, int col, int radius) {
        List<Biology> list = new ArrayList<>((int) Math.pow((1 + radius * 2), 2) - 1);
        List<Location> relativeLocations = RELATIVE_LOCATION_LISTS.get(radius - 1);
        for (Location relativeLoc : relativeLocations) {
            int r = row + relativeLoc.getRow();
            int c = col + relativeLoc.getColumn();
            Cell cell = this.getCell(r, c);
            if (cell != null) {
                list.add((Biology) cell);
            }
        }

        return list;
    }

    /**
     * 获取周围半径为radius的空的位置
     *
     * @param row
     * @param col
     * @return
     */
    public List<Location> getFreeAdjacentLocation(int row, int col, int radius) {
        ArrayList<Location> list = new ArrayList<>((int) Math.pow((1 + radius * 2), 2) - 1);
        List<Location> relativeLocations = RELATIVE_LOCATION_LISTS.get(radius - 1);
        for (Location relativeLoc : relativeLocations) {
            int r = row + relativeLoc.getRow();
            int c = col + relativeLoc.getColumn();
            if (r >= 0 && r < height && c >= 0 && c < width && getCell(r, c) == null) {
                // 位置不越界,且没有生物
                list.add(new Location(r, c));
            }
        }
//        for (int i = -radius; i <= radius; i++) {
//            for (int j = -radius; j <= radius; j++) {
//                int r = row + i;
//                int c = col + j;
//                if (r >= 0 && r < height && c >= 0 && c < width && getCell(r, c) == null) {
//                    // 位置不越界,且没有生物
//                    list.add(new Location(r, c));
//                }
//            }
//        }
        return list;
    }

    /**
     * 获取周围半径为radius的位置
     *
     * @param row
     * @param col
     * @return
     */
    public List<Location> getNeighbourLocation(int row, int col, int radius) {
        ArrayList<Location> list = new ArrayList<>((int) Math.pow((1 + radius * 2), 2) - 1);
        List<Location> relativeLocations = RELATIVE_LOCATION_LISTS.get(radius - 1);
        for (Location relativeLoc : relativeLocations) {
            int r = row + relativeLoc.getRow();
            int c = col + relativeLoc.getColumn();
            if (r >= 0 && r < height && c >= 0 && c < width && !(getCell(r, c) instanceof Animal)) {
                // 位置不越界,且没有动物
                list.add(new Location(r, c));
            }
        }
//        for (int i = -1; i <= radius; i++) {
//            for (int j = -1; j <= radius; j++) {
//                int r = row + i;
//                int c = col + j;
//                if (r >= 0 && r < height && c >= 0 && c < width && !(i == 0 && j == 0) && !(getCell(r, c) instanceof Animal)) {
//                    // 位置不越界,且没有动物
//                    list.add(new Location(r, c));
//                }
//            }
//        }
        return list;
    }

    /**
     * 放置新生命
     *
     * @param row
     * @param col
     * @param cell
     * @return
     */
    public void placeNewBiology(int row, int col, Cell cell) {
        List<Location> freeAdjacentLocation;
        if (cell instanceof Plant) {
            freeAdjacentLocation = getFreeAdjacentLocation(row, col, Plant.BREED_SCOPE);
        } else {
            freeAdjacentLocation = getNeighbourLocation(row, col, 1);
        }
        if (!freeAdjacentLocation.isEmpty()) {
            Location freeLocation = freeAdjacentLocation.get((int) (Math.random() * freeAdjacentLocation.size()));
            //设置坐标属性
            cell.setLocation(freeLocation.getRow(), freeLocation.getColumn());
            place(cell);
        }
    }

    private void remove(int row, int col) {
        cells[row][col] = null;
    }

    public void remove(Biology biology) {
        this.remove(biology.getRow(), biology.getColumn());
    }

    /**
     * 猎人取代猎物的位置, 猎人原位置置空
     *
     * @param hunter 猎人
     * @param prey   猎物
     */
    public void replace(Cell hunter, Cell prey) {
        remove(hunter.getRow(), hunter.getColumn());
        // 猎人自身的位置属性更新
        hunter.setLocation(prey.getRow(), prey.getColumn());
        place(hunter);
    }
//    public boolean actorMove(Actor actor, Location loc) {
////        if (loc == null) {
////            return false;
////        }
//        //判断actor有没有被吃掉,如果被吃掉则返回false
//        if (actor.isAlive() && (getCell(actor.getRow(), actor.getColumn()) instanceof Actor)) {
//            int rowLoc = loc.getRow();
//            int colLoc = loc.getColumn();
//            short eatWolf = 365;
//            short eatSheep = 7;
//            short eatPlant = 1;
//            //actor的feed()的实现
//            if (rowLoc > -1 && colLoc > -1 && rowLoc < height && colLoc < width && !(field[rowLoc][colLoc] instanceof Human)) {
//                if (field[rowLoc][colLoc] instanceof Wolf) {
//                    actor.longerLife(eatWolf, actor.MAX_LIFETIME);
//                    wolfAudio.play();
//                } else if (field[rowLoc][colLoc] instanceof Sheep) {
//                    actor.longerLife(eatSheep, actor.MAX_LIFETIME);
//                    if (sheepAudio != null) {
//                        sheepAudio.stop();
//                    }
//                    sheepAudio.play();
//                } else if (field[rowLoc][colLoc] instanceof Plant) {
//                    actor.longerLife(eatPlant, actor.MAX_LIFETIME);
//                }
//                remove(actor.getRow(), actor.getColumn());
//                actor.setLocation(loc.getRow(), loc.getColumn());
//                field[rowLoc][colLoc] = actor;
//                return true;
//            }
//            //空格键快速捕食
//            if (rowLoc == -1 && colLoc == -1) {
//                Cell[] cells = getNeighbour(actor.getRow(), actor.getColumn());
//                boolean flag = false;// 避免吃完动物继续吃植物
//                for (Cell cell : cells) {
//                    if (cell instanceof Wolf) {
//                        wolfAudio.play();
//                        instead(actor, cell);
//                        actor.longerLife(eatWolf, actor.MAX_LIFETIME);
//                        flag = true;
//                    } else if (cell instanceof Sheep) {
//                        if (sheepAudio != null) {
//                            sheepAudio.stop();
//                        }
//                        sheepAudio.play();
//                        instead(actor, cell);
//                        actor.longerLife(eatSheep, actor.MAX_LIFETIME);
//                        flag = true;
//                        break;
//                    }
//                }
//                if (!flag) {
//                    List<Cell> list = new ArrayList<>();
//                    for (Cell cell : cells) {
//                        if (cell instanceof Plant) {
//                            list.add(cell);
//                        }
//                    }
//                    if (list.size() > 0) {
//                        Cell prey = list.get((int) (list.size() * Math.random()));
//                        instead(actor, prey);
//                        actor.longerLife(eatPlant, actor.MAX_LIFETIME);
//                    }
//                }
//            }
//            return true;
//        }
//        return false;
//    }

    /**
     * 移动
     *
     * @param row
     * @param col
     * @param loc 新位置
     */
    public void move(int row, int col, Location loc) {
        if (loc == null) {
            return;
        }
        Cell cell = getCell(row, col);
        if (cell != null) {
            remove(row, col);
            cell.setLocation(loc.getRow(), loc.getColumn());
            place(cell);
        }
    }
}
