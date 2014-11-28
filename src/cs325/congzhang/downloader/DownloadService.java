package cs325.congzhang.downloader;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Random;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

public class DownloadService extends Service {

	public static final String TAG = "DownloadService";

	private SQLController dbcon;
	
	private DownloadServiceBinder downloadBinder = new DownloadServiceBinder();
	
	private ArrayList<Picture> pictureList;
	
	private boolean isDownloading = false;
	  
    @Override  
    public void onCreate() {  
        super.onCreate();  
        Log.d(TAG, "onCreate() executed");  
        dbcon = new SQLController(this.getApplicationContext());
    }  
  
    @Override  
    public int onStartCommand(Intent intent, int flags, int startId) {  
        Log.d(TAG, "onStartCommand() executed");  
        return super.onStartCommand(intent, flags, startId);  
    }  
      
    @Override  
    public void onDestroy() {  
        super.onDestroy();  
        Log.d(TAG, "onDestroy() executed");  
    }  
    
	@Override
	public IBinder onBind(Intent intent) {
		return downloadBinder;
	}
	
	public class DownloadServiceBinder extends Binder
	{
		public DownloadService getService() {
			return DownloadService.this;
		}	
	}
	
	public void downloadImage(String url)
	{
		dbcon.open();
		  
		Random random = new Random();
		dbcon.insertData("http://www.moibibiki.com/images/ford-f350-4.jpg", String.valueOf(random.nextInt(100000)) + ".jpg", "Queued");
		Log.d(TAG, "Download Image");
		dbcon.close();
		
		if ( !isDownloading )
		{
			DownloadTask dt = new DownloadTask();
			dt.execute();			
		}
	}
	
	
	
	public ArrayList<Picture> getPictureList(boolean notFinished)
	{
		dbcon.open();
		Cursor cursor = dbcon.readData(notFinished);
		pictureList = new ArrayList<Picture>();
		while ( cursor.moveToNext())
		{
			long id = cursor.getLong(cursor.getColumnIndex(DBHelper.PICTURE_ID));
			String url = cursor.getString(cursor.getColumnIndex(DBHelper.PICTURE_URL));
			String fileName = cursor.getString(cursor.getColumnIndex(DBHelper.PICTURE_FILENAME));
			String state = cursor.getString(cursor.getColumnIndex(DBHelper.PICTURE_STATE));
			pictureList.add(new Picture(id,url,fileName,state));
		}
		cursor.close();
		dbcon.close();
		return pictureList;
	}
		
	public String getStats()
	{
		ArrayList<Picture> pictures = getPictureList(false);
		int queuedCount = 0;
		int downloadedCount = 0;
		for (Picture picture : pictures)
		{
			if ( picture.State.equals(Picture.STATE_QUEUED))
			{
				queuedCount++;
			}
			else if ( picture.State.equals(Picture.STATE_DOWNLOADED))
			{
				downloadedCount++;
			}
		}
		return "In my mobile app, I have " + queuedCount + " item(s) in queue and " + downloadedCount + " already downloaded.";
	}
	
	private class DownloadTask extends AsyncTask<Void, String, Void> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			isDownloading = true;
		}

		@Override
		protected void onProgressUpdate(String... values) {
			Log.d("Progress Update", String.valueOf(values[0]));
			
			// send broadcast
			Intent intent = new Intent();
			intent.putExtra("url",values[0]);
			intent.putExtra("fileName",values[1]);
			intent.putExtra("fileLength",Integer.parseInt(values[2]));
			intent.putExtra("currentLength",Integer.parseInt(values[3]));
			intent.setAction(getResources().getString(R.string.broadcast_download_progress));
			sendBroadcast(intent); 
			
			super.onProgressUpdate(values);
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);

			isDownloading = false;
		}
		
		@Override
		protected Void doInBackground(Void... result) {
			
			ArrayList<Picture> pictures = getPictureList(true);
			while ( pictures.size() > 0 )
			{
				for (Picture picture : pictures)
				{
					if ( picture.State.equals(Picture.STATE_QUEUED) || picture.State.equals(Picture.STATE_DOWNLOADING))
					{
						dbcon.open();
						dbcon.updateData(picture.Id, Picture.STATE_DOWNLOADING);
						Log.d(TAG, "Updated state downloading");
						dbcon.close();
						
						URL url;
						try {
							url = new URL(picture.Url);
							String fileName = downloadPicture(url);

							dbcon.open();
							dbcon.updateData(picture.Id, Picture.STATE_DOWNLOADED);
							Log.d(TAG, "Updated state downloaded");
							dbcon.close();
							
						} catch (MalformedURLException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

					}
				}
				pictures = getPictureList(true);
			}
			
			return null;
		}

		private String downloadPicture(URL url) {
			InputStream input = null;
			FileOutputStream output = null;
			HttpURLConnection connection = null;
			try {
				connection = (HttpURLConnection) url.openConnection();
				connection.connect();

				if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
					Log.d("download", "response code error: " + url.toString());
					return "";
				}

				String outputFileName = url.getFile();
				outputFileName = outputFileName.substring(outputFileName.lastIndexOf("/") + 1);
				int fileSize = connection.getContentLength();
				int total = 0;
				// download the file
				input = connection.getInputStream();
				output = openFileOutput(outputFileName, Context.MODE_PRIVATE);

				byte data[] = new byte[1024];
				int count;
				while ((count = input.read(data)) != -1) {
					// allow canceling
					if (isCancelled()) {
						input.close();
						break;
					}
					total += count;
					output.write(data, 0, count);
					publishProgress(url.toString(),outputFileName,String.valueOf(fileSize),String.valueOf(total));
					Thread.sleep(200);
				}
				if ( total == fileSize )
				{
					Log.d("download", "download succeed: " + url.toString());
					return outputFileName;
				}
				else
				{
					Log.d("download", "download failed: " + url.toString());
					return "";
				}

			} catch (Exception e) {
				Log.d("download", "download error: " + e.toString());
				return "";
			} finally {
				try {
					if (output != null)
						output.close();
					if (input != null)
						input.close();
				} catch (IOException ioe) {
				}

				if (connection != null)
					connection.disconnect();
			}

		}
	}
	
}
