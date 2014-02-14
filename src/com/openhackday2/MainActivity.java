package com.openhackday2;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;
import android.support.v4.util.LruCache;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

import com.zxing.activity.CaptureActivity;

public class MainActivity extends FragmentActivity implements OnClickListener {
	private TextView mResultTextView;
	private BookAdapter mAdapter;
	private Handler mHandler;
	private GridView mBookGridView;
	private LruCache<String, Bitmap> mLruCache;
	
	private int maxSize = 10 * 1024 * 1024;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.main);
		
		mLruCache = new LruCache<String, Bitmap>(maxSize) {
		    @Override
		    protected int sizeOf(String key, Bitmap value) {
		        return value.getRowBytes() * value.getHeight();
		    }
		};
		
		mBookGridView = (GridView) findViewById(R.id.bookGrid);
		mHandler = new Handler();
		mAdapter = new BookAdapter(this);
    	ImageItem item = new ImageItem();
        item.name = "item1";
        mAdapter.add(item);
		mBookGridView.setAdapter(mAdapter);


		mBookGridView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
	        	Intent intent = new Intent(MainActivity.this, ListTabActivity.class);
	            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
	            startActivity(intent);
			}
		});
		
		// onScrollListener の実装
		mBookGridView.setOnScrollListener(new OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (scrollState == SCROLL_STATE_IDLE) {
                    // スクロールが止まったときに読み込む
                    loadBitmap();
                }
            }
            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            }
        });
        
		mResultTextView = (TextView) this.findViewById(R.id.tv_scan_result);

		findViewById(R.id.btn_scan_barcode).setOnClickListener(this);
		
	}
	
	/**
	 * ImageLoader のコールバック.
	 */
	private LoaderCallbacks<Bitmap> callbacks = new LoaderCallbacks<Bitmap>() {
	    @Override
	    public Loader<Bitmap> onCreateLoader(int i, Bundle bundle) {
	        ImageItem item = (ImageItem) bundle.getSerializable("item");
	        ImageLoader loader = new ImageLoader(getApplicationContext(), item);
	        loader.forceLoad();
	        return loader;
	    }
	    @Override
	    public void onLoadFinished(Loader<Bitmap> loader, Bitmap bitmap) {
	        int id = loader.getId();
	        getSupportLoaderManager().destroyLoader(id);
	        // メモリキャッシュに登録する
	        ImageItem item = ((ImageLoader) loader).item;
	        Log.i("imagecache", "キャッシュに登録=" + item.name);
	        mLruCache.put(item.name, bitmap);
	        item.bitmap = bitmap;
	        setBitmap(item);
	    }
	    @Override
	    public void onLoaderReset(Loader<Bitmap> loader) {
	    }
	};
	
	/**
	 * アイテムの View に Bitmap をセットする.
	 * @param item
	 */
	private void setBitmap(ImageItem item) {
	    ImageView view = (ImageView) mBookGridView.findViewWithTag(item);
	    if (view != null) {
	        view.setImageBitmap(item.bitmap);
	        mBookGridView.invalidateViews();
	    }
	}

    public class BookAdapter extends ArrayAdapter<ImageItem> {

        public BookAdapter(Context context) {
            super(context, 0);
        }

        public long getItemId(int position) {
            return position;
        }
        
        public View getView(int position, View convertView, ViewGroup parent) {
        	ImageItem item = getItem(position);
            ImageView imageView;
            if (convertView == null) {
                imageView = new ImageView(getContext());
                imageView.setLayoutParams(new GridView.LayoutParams(200, 200));
                imageView.setTag(item);
                //imageView.setAdjustViewBounds(false);
                //imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                //imageView.setPadding(8, 8, 8, 8);
            } else {
                imageView = (ImageView) convertView;
            }
            
            imageView.setImageBitmap(item.bitmap);

            return imageView;
        }
    }
    
    private void loadBitmap() {
        int first = mBookGridView.getFirstVisiblePosition();
        int count = mBookGridView.getChildCount();
        for (int i = 0; i < count; i++) {
            ImageItem item = mAdapter.getItem(i + first);
            Bitmap bitmap = mLruCache.get(item.name);
            if (bitmap != null) {
                // キャッシュに存在
                Log.i("imageCache", "キャッシュあり=" + item.name);
                setBitmap(item);
                mBookGridView.invalidateViews();
            } else {
                // キャッシュになし
                Log.i("imageCache", "キャッシュなし=" + item.name);
                Bundle bundle = new Bundle();
                bundle.putSerializable("item", item);
                getSupportLoaderManager().initLoader(i, bundle, callbacks);
            }
        }
    }
    
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {
			Bundle bundle = data.getExtras();
			String scanResult = bundle.getString("result");
			// get data from amazon
			Map<String, String> keyMap = new HashMap<String, String>();
			keyMap.put("AssociateTag", "kingarthur911-22");
			keyMap.put("IdType", "ISBN");
			keyMap.put("ItemId", scanResult);
			keyMap.put("Operation", "ItemLookup"); 
			keyMap.put("ResponseGroup", "Large");
			keyMap.put("ReviewPage", "1");
			keyMap.put("SearchIndex", "Books");
			keyMap.put("Service", "AWSECommerceService");  
			SignedRequestsHelper signedRequestsHelper;
			try {
				signedRequestsHelper = new SignedRequestsHelper();
				final String urlStr = signedRequestsHelper.sign(keyMap);
				mResultTextView.setText(scanResult);
				new Thread(new Runnable() {
					@Override
					public void run() {
					       Log.v("AmazonAPI", ">url: " + urlStr);

					        String xml = null;
					        try {
					            DefaultHttpClient httpClient = new DefaultHttpClient();
					            HttpParams params = httpClient.getParams();
					            httpClient.getParams().setParameter("http.useragent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_8_5) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/31.0.1650.63 Safari/537.36");
					            params.setBooleanParameter(CoreProtocolPNames.USE_EXPECT_CONTINUE, false);
					            HttpConnectionParams.setConnectionTimeout(params, 30000);
					            HttpConnectionParams.setSoTimeout(params, 30000);
					            
					            HttpGet method   = new HttpGet(urlStr);

					            HttpResponse httpResponse = httpClient.execute(method);

					            if (httpResponse != null && httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
					                HttpEntity httpEntity = httpResponse.getEntity();
					                xml = EntityUtils.toString(httpEntity, "UTF-8");
					                httpEntity.consumeContent();
					            }
					            httpClient.getConnectionManager().shutdown();
					        }
					        catch (ClientProtocolException e) {
					            Log.v("AmazonAPI", e.toString());
					      
					        }
					        catch (ParseException e) {
					        	Log.v("AmazonAPI", e.toString());
					       
					        }
					        catch (IOException e){
					        	Log.v("AmazonAPI", e.toString());
					        
					        }
					        Log.v("AmazonAPI", "xml: " + xml);
					        
					        mHandler.post(new Runnable() {
								@Override
								public void run() {
						        	ImageItem item = new ImageItem();
						            item.name = "item2";
						            item.url = "http://ecx.images-amazon.com/images/I/41iUErzQk8L.jpg";
						            mAdapter.add(item);
						            loadBitmap();
								}
					        });
					}
				}).start();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
        if (id == R.id.btn_scan_barcode) {
        	Intent openCameraIntent = new Intent(MainActivity.this,CaptureActivity.class);
			startActivityForResult(openCameraIntent, 0);
        }
	}
}