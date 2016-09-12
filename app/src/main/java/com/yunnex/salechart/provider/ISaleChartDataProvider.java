package com.yunnex.salechart.provider;

import com.yunnex.salechart.entity.ChartData;

import java.util.List;

/**
 * Created by Venco on 2016/9/8.
 */

public interface ISaleChartDataProvider {
    //获取坐标点数据
    public List<ChartData> getSaleChartData();
    //设置坐标点数据
    public void setSaleChartData(List<ChartData> lists);
}
