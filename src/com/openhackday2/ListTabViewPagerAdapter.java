package com.openhackday2;

import java.util.List;

import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

public class ListTabViewPagerAdapter extends PagerAdapter {
    private List<View> mListViews;
    
    public ListTabViewPagerAdapter(List<View> views){
        mListViews = views;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object)   {
        container.removeView(mListViews.get(position));
    }


    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        container.addView(mListViews.get(position),position);
        return mListViews.get(position);
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return mListViews.size();
    }

    @Override
    public boolean isViewFromObject(View arg0, Object arg1) {
        // TODO Auto-generated method stub
        //return false;
        return arg0==arg1;
    }

}
