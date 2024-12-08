package com.system.assessment.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class MathUtils {
    public static Double transformer(Double score){
        BigDecimal bd = BigDecimal.valueOf(score);
        bd = bd.setScale(1, RoundingMode.HALF_UP); // 保留一位小数，四舍五入
        Double newScoreScale = bd.doubleValue();
        return newScoreScale;
    };
}
