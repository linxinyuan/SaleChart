package com.yunnex.salechart.view;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;

import com.yunnex.salechart.R;
import com.yunnex.salechart.entity.ChartData;
import com.yunnex.salechart.provider.ISaleChartDataProvider;
import com.yunnex.salechart.provider.ISaleChartViewProvider;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Venco on 2016/9/8.
 */
public class SaleChart extends View implements ISaleChartDataProvider, ISaleChartViewProvider {

    private Context mContext;
    //视图宽高
    private int mWidth;
    private int mHeight;
    //图标宽高
    private int mChartWidth;
    private int mChartHeight;
    //自定义属性
    private int broadSide;
    private int dateTextSize;
    private int dateTextColor;
    private int lineSize;
    private int lineColor;
    private int pointSize;
    private int pointColor;
    private Rect dateTextRect;
    private Rect saleTextRect;
    //左上角提示字样相关属性
    private String chartExplainStr;
    private Rect chartExplainRect;
    private int explinMarginLeft = 8;
    private int explainMarginTop = 10;
    //辅助类
    private Paint paint;
    private Resources res;
    private DisplayMetrics dm;
    //绘制选项
    private boolean isPointFill = false;//是否绘制实心点
    private boolean isDrawCurve = true;//是否绘制直线
    //y轴选项
    private int yMaxValue = 250;//y轴最大值(默认值为200)
    private int yMarginBottom = 32;//y轴距离底部距离(默认距离32,单位dp)
    //绘制点间距
    private int pointSpacing;
    //属性默认值
    private static final int DEFAULT_LINE_SIZE = 3;//默认线的大小(dip)
    private static final int DEFAULT_BROAD_SIZE = 8;//默认左右间距大小(dp)
    private static final float DEFAULT_POINT_SIZE = 8;//默认点的大小(dip)
    private static final int DEFAULT_DATE_TEXT_SIZE = 8;//默认时间字体大小(sp)
    private static final int DEFAULT_DATE_TEXT_PADDING = 18;//字体距离图标间距(dp) --> 不属于自定义属性
    //临时测试数据
    List<ChartData> lists = new ArrayList<>();

    public SaleChart(Context context) {
        this(context, null);
    }

    public SaleChart(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SaleChart(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        //初始化辅助类
        initAssistTool(context);
        //获取自定义属性
        TypedArray typedArray = mContext.getTheme().obtainStyledAttributes(attrs, R.styleable.SaleChart, defStyleAttr, 0);
        int attrCount = typedArray.getIndexCount();
        for (int i = 0; i < attrCount; i++) {
            int attr = typedArray.getIndex(i);
            switch (attr) {
                case R.styleable.SaleChart_chart_explain:
                    chartExplainStr = typedArray.getString(attr);
                    break;
                case R.styleable.SaleChart_broad_side:
                    //使用默认的左右间距是8dp
                    broadSide = typedArray.getDimensionPixelSize(attr, getAttrInDip(SaleChart.DEFAULT_BROAD_SIZE));
                    break;
                case R.styleable.SaleChart_date_size:
                    //使用默认时间字体大小是8sp
                    dateTextSize = typedArray.getDimensionPixelSize(attr, getAttrInSp(SaleChart.DEFAULT_DATE_TEXT_SIZE));
                    break;
                case R.styleable.SaleChart_line_size:
                    //使用默认绘制线大小为3dip
                    lineSize = typedArray.getDimensionPixelSize(attr, getAttrInSp(SaleChart.DEFAULT_LINE_SIZE));
                    break;
                case R.styleable.SaleChart_point_size:
                    //使用默认绘制点大小为3dip
                    pointSize = typedArray.getDimensionPixelSize(attr, getAttrInSp((int) SaleChart.DEFAULT_POINT_SIZE));
                    break;
                case R.styleable.SaleChart_date_color:
                    dateTextColor = typedArray.getColor(attr, res.getColor(R.color.chart_text_color));
                    break;
                case R.styleable.SaleChart_line_color:
                    lineColor = typedArray.getColor(attr, res.getColor(R.color.chart_line_color));
                    break;
                case R.styleable.SaleChart_point_color:
                    pointColor = typedArray.getColor(attr, res.getColor(R.color.chart_point_color));
                    break;
                default:
                    break;
            }
        }
        typedArray.recycle();
    }

    public void initAssistTool(Context context) {
        mContext = context;
        paint = new Paint();
        dm = new DisplayMetrics();
        res = mContext.getResources();
        //控件区域矩形初始化
        dateTextRect = new Rect();
        chartExplainRect = new Rect();
        saleTextRect = new Rect();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(setWidth(widthMeasureSpec), setHeight(heightMeasureSpec));
    }

    protected int setWidth(int widthMeasureSpec) {
        int mode = MeasureSpec.getMode(widthMeasureSpec);
        int size = MeasureSpec.getSize(widthMeasureSpec);
        if (mode == MeasureSpec.EXACTLY)
            mWidth = size;
        //获得图标宽度
        mChartWidth = mWidth;
        //获得点间距=（图标宽度-2*首尾间距）/ (lists长度 - 1);
        pointSpacing = (mChartWidth - broadSide * 2) / (lists.size() - 1);
        return mWidth;
    }

    protected int setHeight(int heightMeasureSpec) {
        int mode = MeasureSpec.getMode(heightMeasureSpec);
        int size = MeasureSpec.getSize(heightMeasureSpec);
        if (mode == MeasureSpec.EXACTLY) {
            mHeight = size;
        } else {
            int desire = getAttrInDip(yMarginBottom + yMaxValue);
            if (mode == MeasureSpec.AT_MOST)
                mHeight = Math.min(desire, size);
        }
        //获得图标高度=布局高度-y轴距离底部高度
        mChartHeight = mHeight - getAttrInDip(yMarginBottom);
        return mHeight;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (lists.size() != 0) {
            drawChartBackground(canvas);//绘制图表区背景

            drawChartExplain(canvas);//绘制图标说明-"7天营业额"

            drawDateText(canvas);//绘制图标下方的日期

            //drawHorizontalLine(canvas);//绘制所有横向直线

            //drawVerticalLine(canvas);//绘制所有纵向直线

            drawDataPoint(canvas);//绘制图标上数据点

            drawSaleDataAbovePoint(canvas);//绘制点上的销量字样

            drawConnPointLine(canvas);//绘制数据点间的连线
        }
    }

    public void drawChartBackground(Canvas canvas) {
        setDrawBackgroundPaintStyle();
        //绘制首块矩形区域
        paint.setColor(res.getColor(R.color.chart_bg_1));
        canvas.drawRect(0, 0, broadSide, mChartHeight, paint);
        //绘制中间矩形区域
        int remain = lists.size() - 1;
        for (int i = 0; i < remain; i++) {
            //背景颜色_2
            if (i % 2 == 0) {
                paint.setColor(res.getColor(R.color.chart_bg_2));
            }
            //背景颜色_1
            else {
                paint.setColor(res.getColor(R.color.chart_bg_1));
            }
            canvas.drawRect(i * pointSpacing + broadSide, 0, (i + 1) * pointSpacing + broadSide, mChartHeight, paint);
        }
        //绘制尾部矩形区域
        if (lists.size() % 2 != 0)
            paint.setColor(res.getColor(R.color.chart_bg_2));
        else
            paint.setColor(res.getColor(R.color.chart_bg_1));
        canvas.drawRect(broadSide + pointSpacing * remain, 0, mChartWidth, mChartHeight, paint);
    }

//    //绘制横向直线
//    public void drawHorizontalLine(Canvas canvas) {
//        setDrawAxisPaintStyle();
//        //绘制x轴直线
//        if (isDrawXAxis)
//            canvas.drawLine(0, mChartHeight, getWidth(), mChartHeight, paint);
//    }

//    //绘制纵向直线
//    public void drawVerticalLine(Canvas canvas) {
//        setDrawAxisPaintStyle();
//        //绘制左边间距首线
//        canvas.drawLine(broadSide, 0, broadSide, mChartHeight, paint);
//        //绘制右边间距尾线
//        canvas.drawLine(mChartWidth - broadSide, 0, mChartWidth - broadSide, mChartHeight, paint);
//        //绘制中间剩余部分的直线
//        int remain = lists.size() - 2;
//        for (int i = 1; i <= remain; i++) {
//            canvas.drawLine(i * pointSpacing + broadSide, 0, i * pointSpacing + broadSide, mChartHeight, paint);
//        }
//    }

    //绘制图标左上角说明 - 7天营业额
    public void drawChartExplain(Canvas canvas) {
        setDrawChartExplainPainStyle();
        //获得字体区域大小
        paint.getTextBounds(chartExplainStr, 0, chartExplainStr.length(), chartExplainRect);
        canvas.drawText(chartExplainStr, getAttrInDip(explinMarginLeft), chartExplainRect.height() / 2 + getAttrInDip(explainMarginTop), paint);
    }

    //绘制所有的点
    public void drawDataPoint(Canvas canvas) {
        setDrawPointPaintStyle();
        //绘制7天销售额数据点，y轴坐标为百分比
        for (int i = 0; i < lists.size(); i++) {
            float precent = (float) lists.get(i).getSale() / (float) yMaxValue;
            float y = mChartHeight * precent;
            canvas.drawCircle(i * pointSpacing + broadSide, mChartHeight - y, DEFAULT_POINT_SIZE, paint);
        }
    }

    //绘制点之间的连线
    public void drawConnPointLine(Canvas canvas) {
        setDrawConnPointLinePaintStyle();
        //绘制点之间的连接曲线
        for (int i = 0; i < lists.size() - 1; i++) {
            //获得当前起点横纵坐标
            float currentPrecent = (float) lists.get(i).getSale() / (float) yMaxValue;
            float startPointX = i * pointSpacing + broadSide + DEFAULT_BROAD_SIZE;
            float startPointY = mChartHeight - mChartHeight * currentPrecent;
            //获取下一个点的横纵坐标
            float nextPrecent = (float) lists.get(i + 1).getSale() / (float) yMaxValue;
            float nextPointX = (i + 1) * pointSpacing + broadSide - DEFAULT_BROAD_SIZE;
            float nextPointY = mChartHeight - mChartHeight * nextPrecent;
            if (isDrawCurve) {
                //生成贝塞尔曲线控制点
                int wt = (int) ((startPointX + nextPointX) / 2);
                Point control_1 = new Point();
                Point control_2 = new Point();
                control_1.y = (int) startPointY;
                control_1.x = wt;
                control_2.y = (int) nextPointY;
                control_2.x = wt;
                //生成路径Path
                Path path = new Path();
                //循环移动点
                path.moveTo(startPointX, startPointY);
                //绘制两个点的贝塞尔曲线
                path.cubicTo(control_1.x, control_1.y, control_2.x, control_2.y, nextPointX, nextPointY);
                canvas.drawPath(path, paint);
            } else {
                //绘制直线（备用）
                canvas.drawLine(startPointX, startPointY, nextPointX, nextPointY, paint);
            }
        }
    }

    //绘制数据点上方的数据
    public void drawSaleDataAbovePoint(Canvas canvas) {
        setDrawSaleDataAbovePointPaintStyle();
        for (int i = 0; i < lists.size(); i++) {
            String sale = String.valueOf(lists.get(i).getSale());
            paint.getTextBounds(sale, 0, sale.length(), saleTextRect);
            float precent = (float) lists.get(i).getSale() / (float) yMaxValue;
            float y = mChartHeight * precent;
            canvas.drawText(sale, i * pointSpacing + broadSide, mChartHeight - (y + saleTextRect.height()), paint);
        }
    }

    public void drawDateText(Canvas canvas) {
        setDrawDateTextPaintStyle();
        //绘制首日期
        canvas.drawText(lists.get(0).getDate(), broadSide, mChartHeight + getAttrInDip(DEFAULT_DATE_TEXT_PADDING), paint);
        //绘制尾日期
        paint.getTextBounds(lists.get(0).getDate(), 0, lists.get(0).getDate().length(), dateTextRect);//计算字体包裹区域大小
        //尾部日期在计算距离上左移3dip
        canvas.drawText(lists.get(lists.size() - 1).getDate(), mChartWidth - broadSide - dateTextRect.width() - getAttrInDip(3), mChartHeight + getAttrInDip(DEFAULT_DATE_TEXT_PADDING), paint);
        //绘制中间日期
        paint.setTextAlign(Paint.Align.CENTER);
        int remain = lists.size() - 2;
        for (int i = 1; i <= remain; i++)
            canvas.drawText(lists.get(i).getDate(), i * pointSpacing + broadSide, mChartHeight + getAttrInDip(DEFAULT_DATE_TEXT_PADDING), paint);
    }

    //设置绘制背景画笔样式
    public void setDrawBackgroundPaintStyle() {
        paint.reset();
        //区域颜色填充
        paint.setStyle(Paint.Style.FILL);
    }

//    //设置绘横纵线直线画笔样式
//    public void setDrawAxisPaintStyle() {
//        paint.setColor(res.getColor(R.color.chart_line_color));
//        paint.setStrokeWidth((float) DEFAULT_LINE_SIZE);
//    }

    //设置绘制图标提示信息画笔样式
    public void setDrawChartExplainPainStyle() {
        paint.reset();
        paint.setAntiAlias(true);
        paint.setColor(dateTextColor);
        paint.setTextSize(dateTextSize);
        paint.setStyle(Paint.Style.FILL);
    }

    //设置绘制数据点画笔样式
    public void setDrawPointPaintStyle() {
        paint.reset();
        paint.setAntiAlias(true);
        paint.setColor(pointColor);
        paint.setStrokeWidth(getAttrInDip(2));
        if (isPointFill)
            paint.setStyle(Paint.Style.FILL);
        else
            paint.setStyle(Paint.Style.STROKE);
    }

    //设置绘制数据点之间连线的画笔样式
    public void setDrawConnPointLinePaintStyle() {
        paint.reset();
        paint.setAntiAlias(true);
        paint.setColor(lineColor);
        paint.setStrokeWidth(lineSize);
        paint.setStyle(Paint.Style.STROKE);
    }

    //设置绘制点上方销量字样的画笔样式
    public void setDrawSaleDataAbovePointPaintStyle() {
        paint.reset();
        paint.setAntiAlias(true);
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setColor(dateTextColor);
        paint.setTextSize(dateTextSize);
        paint.setStyle(Paint.Style.FILL);
    }

    //设置绘制日期的画笔样式
    public void setDrawDateTextPaintStyle() {
        paint.reset();
        paint.setAntiAlias(true);
        paint.setColor(dateTextColor);
        paint.setTextSize(dateTextSize);
        paint.setStyle(Paint.Style.FILL);
    }

    @Override
    public List<ChartData> getSaleChartData() {
        return lists;
    }

    @Override
    public void setSaleChartData(List<ChartData> lists) {
        this.lists = lists;
        invalidate();
    }

    //数据点空心或者实心
    public void setPointFill(boolean pointFill) {
        isPointFill = pointFill;
        invalidate();
    }

    //图表曲线是折线或者曲线
    public void setDrawCurve(boolean drawCurve) {
        isDrawCurve = drawCurve;
    }

    protected int getAttrInSp(int def) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,
                def, getResources().getDisplayMetrics());
    }

    protected int getAttrInDip(int def) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                def, getResources().getDisplayMetrics());
    }
}
