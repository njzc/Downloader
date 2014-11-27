package cs325.congzhang.downloader;

import java.util.ArrayList;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

public class DownloadsListActivity extends Activity {

	private DownloadService downloadService;
	private ArrayList<Picture> pictureList;
	
	private static final String TAG = "DownloadsListActivity";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_downloads_list);
		

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.downloads_list, menu);
		return true;
	}
	
	@Override
	public void onResume()
	{
		super.onResume();
		Intent intent  = new Intent(this, DownloadService.class);  
        bindService(intent, connection, BIND_AUTO_CREATE); 

	}
	
	@Override
	public void onPause()
	{
		super.onPause();
		unbindService(connection);
	}
	

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		switch ( id )
		{
			case R.id.action_share:
//				Intent intent = new Intent(DownloadsListActivity.this, PictureViewActivity.class);
//				intent.putExtra("PictureUri","http://www.moibibiki.com/images/ford-f350-4.jpg");
//				startActivity(intent);
				break;
			case R.id.action_browse:
				if ( downloadService != null )
				{
					downloadService.downloadImage("http://www.moibibiki.com/images/ford-f350-4.jpg");  
				}
				//startActivity(new Intent(DownloadsListActivity.this, WebBrowserActivity.class));
				break;
			default:
				break;
		}
		return super.onOptionsItemSelected(item);
	}
	
	private ServiceConnection connection = new ServiceConnection() {  
		  
        @Override  
        public void onServiceDisconnected(ComponentName name) {  
        }  
  
        @Override  
        public void onServiceConnected(ComponentName name, IBinder service) {  
            downloadService = ((DownloadService.DownloadServiceBinder)service).getService();  
            Log.d(TAG,"Service connected");
    		if ( downloadService != null )
    		{
    			pictureList = downloadService.getPictureList();
    			Log.d(TAG,"Get Picture List:" + pictureList.size());
    		}
        }  
    };  
}
