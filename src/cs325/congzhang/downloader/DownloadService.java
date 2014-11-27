package cs325.congzhang.downloader;

import java.util.ArrayList;
import java.util.Random;

import android.app.Service;
import android.content.Intent;
import android.database.Cursor;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

public class DownloadService extends Service {

	public static final String TAG = "DownloadService";

	private SQLController dbcon;
	
	private DownloadServiceBinder downloadBinder = new DownloadServiceBinder();
	
	private ArrayList<Picture> pictureList;
	  
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
	}
	
	public ArrayList<Picture> getPictureList()
	{
		dbcon.open();
		Cursor cursor = dbcon.readData();
		pictureList = new ArrayList<Picture>();
		while ( cursor.moveToNext())
		{
			String url = cursor.getString(cursor.getColumnIndex(DBHelper.PICTURE_URL));
			String fileName = cursor.getString(cursor.getColumnIndex(DBHelper.PICTURE_FILENAME));
			String state = cursor.getString(cursor.getColumnIndex(DBHelper.PICTURE_STATE));
			pictureList.add(new Picture(url,fileName,state));
		}
		cursor.close();
		dbcon.close();
		return pictureList;
	}
	
}
