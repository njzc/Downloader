package cs325.congzhang.downloader;

import java.util.ArrayList;
import java.util.Date;

import com.facebook.FacebookRequestError;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.model.GraphObject;
import com.facebook.model.GraphUser;
import com.facebook.widget.FacebookDialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.facebook.*;

public class DownloadsListActivity extends Activity {

	private DownloadService downloadService;
	private ArrayList<Picture> pictureList;
	private ListView lvPictures;

	private static final String TAG = "DownloadsListActivity";

	private boolean canPresentShareDialog;
	private UiLifecycleHelper uiHelper;
	private GraphUser user;
	private static final String PERMISSION = "publish_actions";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_downloads_list);

		lvPictures = (ListView) findViewById(R.id.lvPictures);

		// To maintain FB Login session
		uiHelper = new UiLifecycleHelper(this, callback);
		uiHelper.onCreate(savedInstanceState);

	}

	// Called when session changes
	private Session.StatusCallback callback = new Session.StatusCallback() {
		@Override
		public void call(Session session, SessionState state,
				Exception exception) {
			onSessionStateChange(session, state, exception);
		}
	};

	// When session is changed, this method is called from callback method
	private void onSessionStateChange(Session session, SessionState state,
			Exception exception) {
		// When Session is successfully opened (User logged-in)
		if (state.isOpened()) {
			Log.i(TAG, "Logged in...");
			// make request to the /me API to get Graph user
			Request.newMeRequest(session, new Request.GraphUserCallback() {

				// callback after Graph API response with user
				// object
				@Override
				public void onCompleted(GraphUser user, Response response) {
					if (user != null) {

					}
				}
			}).executeAsync();
		} else if (state.isClosed()) {
			Log.i(TAG, "Logged out...");
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		uiHelper.onResume();

		Intent intent = new Intent(this, DownloadService.class);
		bindService(intent, connection, BIND_AUTO_CREATE);
	}

	@Override
	public void onPause() {
		super.onPause();
		uiHelper.onPause();
		unbindService(connection);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		uiHelper.onDestroy();
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		uiHelper.onSaveInstanceState(outState);
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		MenuItem item = menu.findItem(R.id.action_share);

		Session session = Session.getActiveSession();
		boolean enableButtons = (session != null && session.isOpened());
		canPresentShareDialog = FacebookDialog.canPresentShareDialog(this,
				FacebookDialog.ShareDialogFeature.SHARE_DIALOG);
		item.setEnabled(enableButtons || canPresentShareDialog);
		if ( item.isEnabled())
		{
			item.setIcon(R.drawable.ic_action_share);
		}
		else
		{
			item.setIcon(R.drawable.ic_action_share_disable);
		}
		super.onPrepareOptionsMenu(menu);
		return true;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.downloads_list, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		switch (id) {
		case R.id.action_share:
			performPublish(canPresentShareDialog);
			break;
		case R.id.action_browse:
			
			 Intent intent = new Intent(DownloadsListActivity.this,WebBrowserActivity.class);
			 startActivity(intent);
			
			break;
		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	private ServiceConnection connection = new ServiceConnection() {

		@Override
		public void onServiceDisconnected(ComponentName name) {
			downloadService = null;
		}

		@Override
		public void onServiceConnected(ComponentName name, IBinder binder) {
			downloadService = ((DownloadService.DownloadServiceBinder) binder)
					.getService();
			Log.d(TAG, "Service connected");
			if (downloadService != null) {
				
				pictureList = downloadService.getPictureList(false);
				Log.d(TAG, "Get Picture List:" + pictureList.size());
				if (pictureList != null) {
					PictureAdapter adapter = new PictureAdapter(
							DownloadsListActivity.this, pictureList);

					lvPictures
							.setOnItemClickListener(new AdapterView.OnItemClickListener() {

								@Override
								public void onItemClick(AdapterView<?> parent,
										View view, int position, long id) {
									Picture picture = pictureList.get(position);
									if (picture.State.equals(Picture.STATE_DOWNLOADED)) {
										Intent pictureViewIntent = new Intent(
												DownloadsListActivity.this,
												PictureViewActivity.class);
										pictureViewIntent.putExtra("fileName",
												picture.FileName);
										startActivity(pictureViewIntent);
									} 
									else if (picture.State.equals(Picture.STATE_DOWNLOADING)) {
										Intent downloadProgressIntent = new Intent(
												DownloadsListActivity.this,
												DownloadProgressActivity.class);
										startActivity(downloadProgressIntent);

									} else if (picture.State == "Queued") {
									}
								}
							});

					lvPictures.setAdapter(adapter);
				}
			}
		}
	};

	private boolean hasPublishPermission() {
		Session session = Session.getActiveSession();
		return session != null
				&& session.getPermissions().contains("publish_actions");
	}

	private void performPublish(boolean allowNoSession) {
		Session session = Session.getActiveSession();
		if (session != null) {
			if (hasPublishPermission()) {
				// We can do the action right away.
				postStatusUpdate();
				return;
			} else if (session.isOpened()) {
				// We need to get new permissions, then complete the action when
				// we get called back.
				session.requestNewPublishPermissions(new Session.NewPermissionsRequest(
						this, PERMISSION));
				return;
			}
		}

		if (allowNoSession) {
			postStatusUpdate();
		}
	}

	private FacebookDialog.ShareDialogBuilder createShareDialogBuilderForLink() {
		return new FacebookDialog.ShareDialogBuilder(this).setName(
				"Post Status Update").setDescription("Status Update");
	}

	private void postStatusUpdate() {
		if (canPresentShareDialog) {
			FacebookDialog shareDialog = createShareDialogBuilderForLink()
					.build();
			uiHelper.trackPendingDialogCall(shareDialog.present());
		}
		else if ( hasPublishPermission()) {
			if ( downloadService != null )
			{	
	            final String message = downloadService.getStats();
	            Request request = Request
	                    .newStatusUpdateRequest(Session.getActiveSession(), message, null, null, new Request.Callback() {
	                        @Override
	                        public void onCompleted(Response response) {
	                            showPublishResult(message, response.getGraphObject(), response.getError());
	                        }
	                    });
	            request.executeAsync();
			}
        }
		else {
			Toast.makeText(this,
					"You don't have permission to post status update",
					Toast.LENGTH_SHORT).show();
		}
	}
	
    private void showPublishResult(String message, GraphObject result, FacebookRequestError error) {
        String title = null;
        String alertMessage = null;
        if (error == null) {
            Toast.makeText(this, "Facebook status updated successfully", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Facebook status updated failed", Toast.LENGTH_SHORT).show();
        }
    }
}
