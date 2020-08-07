package model;

import model.entity.Location;
import model.entity.biology.Biology;
import model.entity.biology.animal.Animal;
import model.entity.biology.animal.Sheep;
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
     * 游戏开始时间
     */
    private long gameStartTime = System.currentTimeMillis();

    /**
     * 为了提高性能,把半径从1到指定值的范围相对位置的列表都生成好
     */
    private static final List<List<Location>> SCOPE_RELATIVE_LOCATION_LISTS = new ArrayList<>(ConstantNum.INIT_MAX_SCOPE_RELATIVE_LOCATION_NUMBER.value);

    static {
        for (int i = 1; i <= ConstantNum.INIT_MAX_SCOPE_RELATIVE_LOCATION_NUMBER.value; i++) {
            SCOPE_RELATIVE_LOCATION_LISTS.add(MyUtils.generateSenseSope(i));
        }
    }

    public Field(int width, int height,Player player) {
        this.width = width;
        this.height = height;
        this.cells = new Cell[height][width];
        this.player = player;
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
        System.out.println("宽:" + width + "\t高:" + height);
        System.out.println("格子数: " + width * height);
        for (int row = 0; row < height; row++) {
            for (int col = 0; col < width; col++) {
                double probability = Math.random();
//                if (probability < 0.001) {
//                    place(row, col, new Human((int) (Math.random() * ConstantNum.ONE_YEAR_DAYS.value)));
//                } else
//                    if (probability < 0.003) {
//                    place(row, col, new Wolf((int) (Math.random() * ConstantNum.ONE_YEAR_DAYS.value)));
//                } else
                    if (probability < 0.01) {
                    place(row, col, new Sheep((int) (Math.random() * ConstantNum.ONE_YEAR_DAYS.value)));
                } else {
                    place(row, col, new Plant((int) (Math.random() * ConstantNum.ONE_YEAR_DAYS.value)));
                }
            }
        }
//        place(height / 2, width / 2, player);
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
        if (cell == null) {
            return;
        }
        cell.setLocation(row, col);
        place(cell);
    }

    private void place(Cell cell) {
        cells[cell.getRow()][cell.getColumn()] = cell;
    }

    /**
     * 根据行和列返回Cell
     *
     * @param row 行
     * @param col 列
     * @return
     */
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
        List<Location> relativeLocations = Field.getRelativeLocationList(radius);
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
    public List<Location> getFreeAdjacentLocations(int row, int col, int radius) {
        ArrayList<Location> list = new ArrayList<>((int) Math.pow((1 + radius * 2), 2) - 1);
        List<Location> relativeLocations = Field.getRelativeLocationList(radius);
        for (Location relativeLoc : relativeLocations) {
            int r = row + relativeLoc.getRow();
            int c = col + relativeLoc.getColumn();
            if (r >= 0 && r < height && c >= 0 && c < width && getCell(r, c) == null) {
                // 位置不越界,且没有生物
                list.add(new Location(r, c));
            }
        }
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
        List<Location> relativeLocations = Field.getRelativeLocationList(radius);
        for (Location relativeLoc : relativeLocations) {
            int r = row + relativeLoc.getRow();
            int c = col + relativeLoc.getColumn();
            if (r >= 0 && r < height && c >= 0 && c < width && !(getCell(r, c) instanceof Animal)) {
                // 位置不越界,且没有动物
                list.add(new Location(r, c));
            }
        }
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
            freeAdjacentLocation = getFreeAdjacentLocations(row, col, Plant.BREED_SCOPE);
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

    /**
     * 移动
     *
     * @param animal 动物
     * @param loc    新位置
     */
    public void move(Animal animal, Location loc) {
        if (loc == null || animal == null) {
            return;
        }
        remove(animal);
        animal.setLocation(loc.getRow(), loc.getColumn());
        place(animal);
    }

    public long getGameStartTime() {
        return gameStartTime;
    }

    public void setGameStartTime(long gameStartTime) {
        this.gameStartTime = gameStartTime;
    }

    public static List<Location> getRelativeLocationList(int radius) {
        return SCOPE_RELATIVE_LOCATION_LISTS.get(radius - 1);
    }

    public Location getCenterLocation() {
        return new Location(height/2,width/2);
    }
}
