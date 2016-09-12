package com.yunnex.salechart.provider;

import android.graphics.Canvas;

/**
 * Created by Venco on 2016/9/9.
 */
public interface ISaleChartViewProvider {
    void drawChartBackground(Canvas canvas);//绘制图表区背景

    //drawHorizontalLine(canvas);//绘制所有横向直线

    //drawVerticalLine(canvas);//绘制所有纵向直线

    void drawChartExplain(Canvas canvas);//绘制图标说明-"7天营业额"

    void drawDataPoint(Canvas canvas);//绘制图标上数据点

    void drawConnPointLine(Canvas canvas);//绘制数据点间的连线

    void drawSaleDataAbovePoint(Canvas canvas);//绘制点上的销量字样

    void drawDateText(Canvas canvas);//绘制图标下方的日期

    void setDrawBackgroundPaintStyle();//设置绘制背景画笔样式

    //void setDrawAxisPaintStyle();//设置绘横纵线直线画笔样式

    void setDrawChartExplainPainStyle(); //设置绘制图标提示信息画笔样式

    void setDrawPointPaintStyle();//设置绘制数据点画笔样式

    void setDrawConnPointLinePaintStyle();//设置绘制数据点之间连线的画笔样式

    void setDrawSaleDataAbovePointPaintStyle();//设置绘制点上方销量字样的画笔样式

    void setDrawDateTextPaintStyle();//设置绘制日期的画笔样式
}
