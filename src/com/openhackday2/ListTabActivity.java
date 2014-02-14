package com.openhackday2;

import java.util.ArrayList;
import java.util.List;

import com.zxing.activity.CaptureActivity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class ListTabActivity extends Activity implements OnClickListener, OnPageChangeListener {
	private ViewPager mViewPager;
	private ImageView mImageView;
	private TextView mTextViewMy, mTextViewFriend;
	private View mViewMy, mViewFriend;
	private List<View> mViews;
	private ListView mMyListView,mFriendListView;
	private int mOffset = 0;
	private int mCurrIndex = 0;
	private int mImageWidth = 0;

	public static Bitmap mBitmap;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.list_tab);

		mViewPager = (ViewPager) findViewById(R.id.list_tab_viewpager);
		mImageView = (ImageView) findViewById(R.id.list_tab_cursor);
		mTextViewMy = (TextView) findViewById(R.id.list_tab_textview_my);
		mTextViewFriend = (TextView) findViewById(R.id.list_tab_textview_friend);

		LayoutInflater inflater = getLayoutInflater();
		mViewMy = inflater.inflate(R.layout.list_tab_my, null);
		mViewFriend = inflater.inflate(R.layout.list_tab_friend, null);

		mViews = new ArrayList<View>();
		mViews.add(mViewMy);
		mViews.add(mViewFriend);

		mViewPager.setAdapter(new ListTabViewPagerAdapter(mViews));
		mViewPager.setCurrentItem(0);
		mViewPager.setOnPageChangeListener(this);

		mTextViewMy.setOnClickListener(this);
		mTextViewFriend.setOnClickListener(this);
	
		// My View
		mMyListView =(ListView) mViewMy.findViewById(R.id.list_tab_my_listview);
		
		ListTabMyListviewAdapter myListViewAdapter = new ListTabMyListviewAdapter(this);
		mMyListView.setAdapter(myListViewAdapter);
		
		initImageView();

		mViewMy.findViewById(R.id.list_tab_my_add).setOnClickListener(this);
//		findViewById(R.id.list_tab_save_button).setOnClickListener(this);

	}
	
	private void initImageView() {
        mImageWidth = BitmapFactory.decodeResource(getResources(), R.drawable.cursor).getWidth();
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int screenW = dm.widthPixels;
        mOffset = (screenW / 2 - mImageWidth) / 2;
        Matrix matrix = new Matrix();
        matrix.postTranslate(mOffset, 0);
        mImageView.setImageMatrix(matrix);
    }

	@Override
	public void onClick(View v) {
		int id = v.getId();
        if (id == R.id.list_tab_my_add) {
        	Intent intent = new Intent(this, DetailActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }
	}

	@Override
	public void onPageScrollStateChanged(int arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onPageSelected(int arg0) {
		int one = mOffset * 2 + mImageWidth;

        Animation animation = new TranslateAnimation(one * mCurrIndex, one * arg0, 0, 0);
        mCurrIndex = arg0;
        animation.setFillAfter(true);
        animation.setDuration(300);
        mImageView.startAnimation(animation);
	}

}
