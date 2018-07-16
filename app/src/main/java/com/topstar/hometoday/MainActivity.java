package com.topstar.hometoday;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.topstar.hometoday.fragment.DiscoverFragment;
import com.topstar.hometoday.fragment.HomeFragment;
import com.topstar.hometoday.fragment.MineFragment;
import com.topstar.hometoday.fragment.QAFragment;
import com.topstar.hometoday.fragment.WorkFragment;
import com.topstar.widget.TSBottomBar;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TSBottomBar bottomBar = findViewById(R.id.bottom_bar);// 获取底部导航栏
        bottomBar.setContainer(R.id.content);// 设置底部导航栏操作对应的页面区域
        bottomBar.setBeforeAndAfterTextColor(R.color.darkGray, R.color.colorAccent);//字体选中前后的颜色
        bottomBar.addItems(HomeFragment.class, QAFragment.class, WorkFragment.class, DiscoverFragment.class, MineFragment.class);
    }
}