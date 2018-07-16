package com.topstar.hometoday.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.topstar.hometoday.R;
import com.topstar.widget.TSBottomBar;

public class WorkFragment extends TSBottomBar.Item {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return LayoutInflater.from(getActivity()).inflate(R.layout.fragment_work, container, false);
    }

    @Override
    public int getTextResourcesId() {
        return R.string.work_text_cn;
    }

    @Override
    public int getBeforeIconResourcesId() {
        return R.drawable.icon_work;
    }

    @Override
    public int getAfterIconResourcesId() {
        return R.drawable.icon_work_press;
    }
}