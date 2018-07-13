package com.topstar.hometoday;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import com.topstar.hometoday.fragment.DiscoverFragment;
import com.topstar.hometoday.fragment.HomeFragment;
import com.topstar.hometoday.fragment.MineFragment;
import com.topstar.hometoday.fragment.QAFragment;
import com.topstar.hometoday.fragment.WorkFragment;
import com.topstar.widget.TSBottomBar;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TSBottomBar bottomBar = findViewById(R.id.bottom_bar);// 获取底部导航栏
        bottomBar.setContainer(R.id.content)// 设置底部导航栏操作对应的页面区域
                .setBeforeAndAfterNameColor(R.color.darkGray, R.color.colorAccent)//字体选中前后的颜色
                .addItem(HomeFragment.class,
                        R.string.home_name,
                        R.drawable.icon_home,
                        R.drawable.icon_home_press)
                .addItem(QAFragment.class,
                        R.string.qa_name,
                        R.drawable.icon_qa,
                        R.drawable.icon_qa_press)
                .addItem(WorkFragment.class,
                        R.string.work_name,
                        R.drawable.icon_work,
                        R.drawable.icon_work_press)
                .addItem(DiscoverFragment.class,
                        R.string.discover_name,
                        R.drawable.icon_discover,
                        R.drawable.icon_discover_press)
                .addItem(MineFragment.class,
                        R.string.mine_name,
                        R.drawable.icon_mine,
                        R.drawable.icon_mine_press)
                .build();
    }
}
