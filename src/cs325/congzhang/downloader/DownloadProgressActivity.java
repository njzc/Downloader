package cs325.congzhang.downloader;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class DownloadProgressActivity extends Activity {

	private TextView tvUrl;
	private ProgressBar pbDownloading;
	
	private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
        	Bundle extras = intent.getExtras();
            if (extras != null) {
            	tvUrl.setText(extras.getString("url"));
            	setTitle(extras.getString("fileName"));
              	int fileSize = extras.getInt("fileLength");
              	int currentSize = extras.getInt("currentLength");
              	if ( fileSize > 0 )
              	{
              		pbDownloading.setProgress( (int)((float)currentSize / fileSize * 100));
              	}
              	else
              	{
              		Toast.makeText(DownloadProgressActivity.this, "All images have been downloaded", Toast.LENGTH_SHORT).show();
              	}
            }
        }
    };
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_download_progress);
		
		tvUrl = (TextView)findViewById(R.id.tvUrl);
		pbDownloading = (ProgressBar)findViewById(R.id.pbDownloading);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.download_progress, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	public void onResume() {
        super.onResume();

        IntentFilter filter = new IntentFilter();
        filter.addAction(getResources().getString(R.string.broadcast_download_progress));

        this.registerReceiver(this.receiver, filter);
    }

    public void onPause() {
        super.onPause();

        this.unregisterReceiver(this.receiver);
    }
}
