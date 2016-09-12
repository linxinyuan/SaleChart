package com.yunnex.salechart;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.yunnex.salechart.fragment.DateTimeFragment;
import com.yunnex.salechart.fragment.SaleChartFragment;
import com.yunnex.salechart.fragment.TradeStatusFragment;

public class MainActivity extends FragmentActivity {
    FragmentManager fm;
    FragmentTransaction ft;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportFragmentManager().beginTransaction().replace(R.id.time_container,new DateTimeFragment()).commit();
        getSupportFragmentManager().beginTransaction().replace(R.id.chart_container,new SaleChartFragment()).commit();
        getSupportFragmentManager().beginTransaction().replace(R.id.trade_container,new TradeStatusFragment()).commit();
    }
}
