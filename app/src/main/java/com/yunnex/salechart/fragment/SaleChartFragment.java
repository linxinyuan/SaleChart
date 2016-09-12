package com.yunnex.salechart.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.yunnex.salechart.R;
import com.yunnex.salechart.entity.ChartData;
import com.yunnex.salechart.view.SaleChart;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Venco on 2016/9/9.
 */

public class SaleChartFragment extends Fragment {
    SaleChart saleChart;
    List<ChartData> lists = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.sale_chart_fragment, container, false);
        saleChart = (SaleChart) view.findViewById(R.id.sale_chart);
        initTestData();
        saleChart.setSaleChartData(lists);
        return view;
    }

    public void initTestData() {
        if (lists.size() == 0) {
            lists.add(new ChartData("7.01", 35));
            lists.add(new ChartData("7.02", 140));
            lists.add(new ChartData("7.03", 42));
            lists.add(new ChartData("7.04", 90));
            lists.add(new ChartData("7.05", 40));
            lists.add(new ChartData("7.06", 60));
            lists.add(new ChartData("7.07", 90));
        }
    }
}
