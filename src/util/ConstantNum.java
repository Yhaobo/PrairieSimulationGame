package util;

/**
 * 程序中所用的常量(数字)
 *
 * @author Yhaobo
 * @date 2020/7/10
 */
public enum ConstantNum {
    /**
     * 生物剩余寿命低于此值颜色会变透明
     */
    REMAIN_TIME_WARNING(5)
    /**
     * 这个游戏中一年的天数
     */
    , ONE_YEAR_DAYS(100)
    /**
     * 初始化最大的范围相对位置的值
     */
    , INIT_MAX_SCOPE_RELATIVE_LOCATION_NUMBER(10);

    public final int value;

    ConstantNum(int num) {
        this.value = num;
    }

}
