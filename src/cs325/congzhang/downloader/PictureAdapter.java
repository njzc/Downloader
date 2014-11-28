package cs325.congzhang.downloader;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;


public class PictureAdapter extends ArrayAdapter<Picture> {
	private ArrayList<Picture> pictureList;

	private final Activity context;
	
	private static class ViewHolder {
	    public TextView tvFileName;
	    public TextView tvState;
	  }

	public PictureAdapter(Activity context, ArrayList<Picture> list) {
		super(context, R.layout.picture_list_row, list);
		this.context = context;
		this.pictureList = list;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View rowView = convertView;
		if ( rowView == null ){
			LayoutInflater inflater = context.getLayoutInflater();
			rowView = inflater.inflate(R.layout.picture_list_row,null);
			ViewHolder viewHolder = new ViewHolder();
			viewHolder.tvFileName = (TextView) rowView.findViewById(R.id.tvRowFileName);
			viewHolder.tvState = (TextView) rowView.findViewById(R.id.tvRowStatus);			
			rowView.setTag(viewHolder);
		}
		
		ViewHolder holder = (ViewHolder)rowView.getTag();
		Picture picture = pictureList.get(position);
		
		holder.tvFileName.setText(picture.FileName);
		if ( picture.State == picture.STATE_DOWNLOADED )
		{
			holder.tvState.setText("View");
		}
		else 
		{
			holder.tvState.setText(picture.State);
		}
		
		return rowView;
	}
}
