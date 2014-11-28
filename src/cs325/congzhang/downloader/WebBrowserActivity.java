package cs325.congzhang.downloader;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebView.HitTestResult;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.Toast;

public class WebBrowserActivity extends Activity {

	private String TAG = "WebBrowserActivity";

	private WebView wvBrowser;

	private DownloadService downloadService;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_web_browser);

		wvBrowser = (WebView) findViewById(R.id.wvBrowser);

		wvBrowser.getSettings().setJavaScriptEnabled(true);
		wvBrowser.setLongClickable(true);
		wvBrowser.setWebViewClient(new WebViewClient() {
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				return super.shouldOverrideUrlLoading(view, url);
			}
		});

		wvBrowser.setOnLongClickListener(new View.OnLongClickListener() {

			@Override
			public boolean onLongClick(View v) {
				HitTestResult result = wvBrowser.getHitTestResult();
				switch (result.getType()) {
				case HitTestResult.IMAGE_TYPE:
				case HitTestResult.SRC_IMAGE_ANCHOR_TYPE:
				case HitTestResult.SRC_ANCHOR_TYPE:
					String url = result.getExtra();
					if (isImage(url)) {
						showDialog(url);
					}
					break;
				default:
					break;
				}
				return true;
			}
		});

		wvBrowser.loadUrl("http://www.njzc.com/downloader.html");
	}

	private boolean isImage(String url) {
		url = url.toLowerCase();
		return url.endsWith(".jpg") || url.endsWith(".png")
				|| url.endsWith(".gif") || url.endsWith(".jpeg");
	}

	private void showDialog(final String url) {
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

		// set dialog message
		alertDialogBuilder
				.setMessage("Do you want to download this picture?")
				.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						if (downloadService != null) {
							downloadService.downloadImage(url);
						}
						dialog.cancel();
					}
				})
				.setNegativeButton("Cancel",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								dialog.cancel();
							}
						});

		// create alert dialog
		AlertDialog alertDialog = alertDialogBuilder.create();

		// show it
		alertDialog.show();
	}


	@Override
	public void onResume() {
		super.onResume();

		Intent intent = new Intent(this, DownloadService.class);
		bindService(intent, connection, BIND_AUTO_CREATE);
	}

	@Override
	public void onPause() {
		super.onPause();
		unbindService(connection);
	}

	private ServiceConnection connection = new ServiceConnection() {

		@Override
		public void onServiceDisconnected(ComponentName name) {
		}

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			downloadService = ((DownloadService.DownloadServiceBinder) service)
					.getService();
			Log.d(TAG, "Service connected");

		}
	};

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.web_browser, menu);
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
}
