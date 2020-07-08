package model;

import model.entity.Location;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Yhaobo
 * @date 2020/7/7
 */
public class MyUtils {
    /**
     * 生成以自身为原点,半径为radius范围内的Location数组
     *
     * @param radius
     * @return
     */
    public static List<Location> generateVIEW_SCOPE(int radius) {
        List<Location> locations = new ArrayList<>((int) (Math.pow(1 + 2 * radius, 2) - 1));
        int row = -1;
        int col = 0;
        int flag;
        for (int i = 1; i <= radius; i++) {
            flag = 0;
            while (row <= i && col <= i && row >= -i && col >= -i) {
                switch (flag) {
                    case 0:
                        locations.add(new Location(row, col++));
                        if (col == i) {
                            flag++;
                        }
                        break;
                    case 1:
                        locations.add(new Location(row++, col));
                        if (row == i) {
                            flag++;
                        }
                        break;
                    case 2:
                        locations.add(new Location(row, col--));
                        if (col == -i) {
                            flag++;
                        }
                        break;
                    case 3:
                        locations.add(new Location(row--, col));
                }
            }
        }
        return locations;
    }

    public static void main(String[] args) {
        List<Location> locations = generateVIEW_SCOPE(3);
        for (Location location : locations) {
            System.out.println(location);
        }
    }
}
