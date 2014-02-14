package com.openhackday2;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import android.os.AsyncTask;

public class GetDataAsyncTask extends AsyncTask <Void,Void,HashMap<String,String>> {

    private Context mContext;

    public GetDataAsyncTask(Context context) {
        this.mContext = context;
    }

    @Override
    protected HashMap<String,String> doInBackground(Void... params) {

        String url = "http://i.yimg.jp/dl/yshopping/android/message.json";

        HashMap<String,String> hash = new HashMap<String,String>();
        DefaultHttpClient client = new DefaultHttpClient();
        try {
            HttpGet request = new HttpGet( url );
            HttpResponse response = client.execute(request);
            if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
                return null;
        	}
            HttpEntity entity = null; 
            try {
                entity = response.getEntity();
                String message = EntityUtils.toString(entity);

                JSONObject jsonRoot = new JSONObject(message);
                JSONArray jsonEntry = jsonRoot.getJSONArray("entry");
                int total = jsonRoot.getInt("totalResults");

                if(total == 0){
                    return null;
                }else{
                    String strEnd = jsonEntry.getJSONObject(0).getString("end_date");
                    String strStart = jsonEntry.getJSONObject(0).getString("start_date");
                    String status = jsonEntry.getJSONObject(0).getString("status");
                    String text = jsonEntry.getJSONObject(0).getString("text");

                    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ",Locale.JAPAN);
                    String strNow = df.format(new Date());

                    Date dtEnd = df.parse(strEnd);
                    Date dtStart = df.parse(strStart);
                    Date dtNow = df.parse(strNow);

                    if(dtNow.getTime() >= dtStart.getTime() && dtNow.getTime() <= dtEnd.getTime() ){
                        if(text != null && text.length() != 0){
                            hash.put( "text" , text );
                        }

                        if(status != null && status.length() != 0){
                            hash.put( "status" , status );
                        }
                    }
                }
            } catch (Exception e){
            } finally {
                try {
                    if (entity != null)
                        entity.consumeContent();
                } catch (Exception e){
                }
            }

        } catch (Exception e) {
        } finally {
            client.getConnectionManager().shutdown();
        }

        return hash;
    }

    @Override
    protected void onPostExecute(HashMap<String,String> hash) {
    	/*
        LinearLayout layout = (LinearLayout) ((Activity) mContext).findViewById(R.id.noticeLayout);
        if( hash != null && hash.containsKey("text") && hash.get("text") !=null && hash.get("text").length() != 0){
            TextView textview = (TextView) ((Activity) mContext).findViewById(R.id.noticeTextView);
            textview.setText(hash.get("text"));

            if(hash.containsKey("status") && hash.get("status").equals("emergency")){
                textview.setTextColor(Color.RED);
            }else{
                textview.setTextColor(Color.BLACK);
            }

            layout.setVisibility(View.VISIBLE);
        }else{
            layout.setVisibility(View.GONE);
        }
        */
    }
}
