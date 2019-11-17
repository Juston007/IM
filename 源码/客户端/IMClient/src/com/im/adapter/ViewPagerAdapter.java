package com.im.adapter;

import java.util.List;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

//ViewPagerÊÊÅäÆ÷
public class ViewPagerAdapter extends FragmentPagerAdapter {

	private List<Fragment> list = null;

	public ViewPagerAdapter(List<Fragment> list, FragmentManager fm) {
		super(fm);
		this.list = list;
	}

	@Override
	public Fragment getItem(int arg0) {
		return list.get(arg0);
	}

	@Override
	public int getCount() {
		return list.size();
	}
}
