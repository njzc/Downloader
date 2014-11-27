package cs325.congzhang.downloader;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper{

	// TABLE INFORMATTION
	public static final String TABLE_PICTURE = "picture";
	public static final String PICTURE_ID = "_id";
	public static final String PICTURE_URL = "url";
	public static final String PICTURE_FILENAME = "file_name";
	public static final String PICTURE_STATE = "state";
	

	// DATABASE INFORMATION
	static final String DB_NAME = "picture.db";
	static final int DB_VERSION = 1;

	// TABLE CREATION STATEMENT
	private static final String CREATE_TABLE = "create table " + TABLE_PICTURE
			+ "(" + PICTURE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
			+ PICTURE_URL + " TEXT NOT NULL," 
			+ PICTURE_FILENAME + " TEXT NOT NULL,"
			+ PICTURE_STATE + " TEXT NOT NULL" + ");";

	public DBHelper(Context context) {
		
		super(context, DB_NAME, null, DB_VERSION);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(CREATE_TABLE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_PICTURE);
		onCreate(db);
	}

}
