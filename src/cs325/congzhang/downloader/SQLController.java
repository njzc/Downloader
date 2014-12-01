package cs325.congzhang.downloader;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class SQLController {

	DBHelper dbHelper;
	Context context;
	SQLiteDatabase database;

	public SQLController(Context context) {
		this.context = context;
	}

	public void open() {
		dbHelper = new DBHelper(context);
		database = dbHelper.getWritableDatabase();
	}
	
	public void close() {
		if (database != null) {  
            database.close();  
        }
		if ( dbHelper != null)
		{
			dbHelper.close();
		}
	}

	public void insertData(String url, String fileName, String state) {
		ContentValues cv = new ContentValues();
		cv.put(DBHelper.PICTURE_URL, url);
		cv.put(DBHelper.PICTURE_FILENAME, fileName);
		cv.put(DBHelper.PICTURE_STATE, state);
		database.insert(DBHelper.TABLE_PICTURE, null, cv);
	}

	public ArrayList<Picture> readData(boolean notFinished) {

		ArrayList<Picture> pictureList = new ArrayList<Picture>();
		
		String[] allColumns = new String[] { DBHelper.PICTURE_ID,DBHelper.PICTURE_URL, DBHelper.PICTURE_FILENAME, DBHelper.PICTURE_STATE };
		String whereClause = notFinished ? DBHelper.PICTURE_STATE + " <> ? " : ""; 
		String[] whereArgs = notFinished ? new String[] { Picture.STATE_DOWNLOADED } : new String[]{}; 
		Cursor cursor = database.query(DBHelper.TABLE_PICTURE, allColumns, whereClause,
				whereArgs, null, null, null);
		while ( cursor.moveToNext())
		{
			long id = cursor.getLong(cursor.getColumnIndex(DBHelper.PICTURE_ID));
			String url = cursor.getString(cursor.getColumnIndex(DBHelper.PICTURE_URL));
			String fileName = cursor.getString(cursor.getColumnIndex(DBHelper.PICTURE_FILENAME));
			String state = cursor.getString(cursor.getColumnIndex(DBHelper.PICTURE_STATE));
			pictureList.add(new Picture(id,url,fileName,state));
		}
		cursor.close();
		
		return pictureList;
	}

	public int updateData(long pictureId, String state) {
		ContentValues cvUpdate = new ContentValues();
		cvUpdate.put(DBHelper.PICTURE_STATE, state);
		int i = database.update(DBHelper.TABLE_PICTURE, cvUpdate,
				DBHelper.PICTURE_ID + " = " + pictureId, null);
		return i;
	}

}
