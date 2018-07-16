package com.topstar.hometoday.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.topstar.hometoday.R;
import com.topstar.widget.TSBottomBar;

public class QAFragment extends TSBottomBar.Item {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return LayoutInflater.from(getActivity()).inflate(R.layout.fragment_qa, container, false);
    }

    @Override
    public int getTextResourcesId() {
        return R.string.qa_text_cn;
    }

    @Override
    public int getBeforeIconResourcesId() {
        return   R.drawable.icon_qa;
    }

    @Override
    public int getAfterIconResourcesId() {
        return R.drawable.icon_qa_press;
    }
}