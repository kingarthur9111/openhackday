package com.openhackday2;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class ListTabMyListviewAdapter extends BaseAdapter{
	
	static class ViewHolder{
	    public TextView datetime; 
	    public TextView text;
	}
	
	private LayoutInflater mInflater;
	private Context mContext;
	
	public ListTabMyListviewAdapter (Context context){
		mContext = context;
		mInflater = LayoutInflater.from(mContext);
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return 20;
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public long getItemId(int id) {
		// TODO Auto-generated method stub
		return id;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
        if(convertView == null) {
            holder = new ViewHolder();
            
            convertView = mInflater.inflate(R.layout.list_tab_my_listview, null);
            holder.datetime = (TextView)convertView.findViewById(R.id.list_tab_my_listview_datetime);
            holder.text = (TextView)convertView.findViewById(R.id.list_tab_my_listview_text);
            
            convertView.setTag(holder);
        }else {
            holder = (ViewHolder)convertView.getTag();
        }
        
        holder.datetime.setText("2014-02-08");
        holder.text.setText("good"+position);
		
		return convertView;
	}

}
