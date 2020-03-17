package com.app.nfcwork;

import android.content.Context;
import android.icu.text.DateFormatSymbols;
import android.icu.util.Calendar;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import java.util.ArrayList;
import java.util.List;

public class ViewPagerAdapter extends FragmentStateAdapter {
    int NUM_TABS = 3;
    Context mContext;
    List<String> tabTitles;

    @RequiresApi(api = Build.VERSION_CODES.N)
    public ViewPagerAdapter(@NonNull FragmentActivity fragmentActivity, Context context) {
        super(fragmentActivity);
        mContext = context;

        tabTitles = new ArrayList<>();
        DateFormatSymbols dfs = new DateFormatSymbols();
        String[] months = dfs.getMonths();
        for (int i = 0; i < NUM_TABS; i++) {
            Calendar date = Calendar.getInstance();
            date.add(Calendar.MONTH, -NUM_TABS + i + 1);
            tabTitles.add(months[date.get(Calendar.MONTH)]);
        }
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return new TabFragment(position, NUM_TABS, mContext);
    }

    @Override
    public int getItemCount() {
        return NUM_TABS;
    }
}