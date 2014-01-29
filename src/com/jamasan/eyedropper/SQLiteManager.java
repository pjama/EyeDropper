package com.jamasan.eyedropper;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.format.DateFormat;

public class SQLiteManager {

	private static final String TAG = "SQLiteManager";
	private Context mContext;
	private SQLiteDatabase db;
	private OpenHelper mOpenHelper;
	
	public static final String DATABASE_NAME = "sqliteWordTwist.db";
	public static final int DATABASE_VERSION = 2;
	
	private static final String TABLE_COLORS = "tbl_colors";
	private static final String COL_COLOR_ID = "id";
	private static final String COL_COLOR_ARGB = "argb";
	private static final String COL_COLOR_DATE_CREATED = "date_created";
	private static final String COL_COLOR_SOURCE = "source";
	
	public static final String DATE_FORMAT = "yyyy-MM-dd kk:mm:ss";
	
	// Awards Table setup
	final static String CREATE_TABLE_COLORS  =
    	"CREATE TABLE IF NOT EXISTS " + TABLE_COLORS + " (" + 
    	COL_COLOR_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
    	COL_COLOR_ARGB + " INT, " +
    	COL_COLOR_SOURCE + " TEXT, " +
    	COL_COLOR_DATE_CREATED + " TEXT);";
		
	final static String DROP_TABLE_COLORS = 
		"DROP TABLE IF EXISTS " + TABLE_COLORS + ";";
	
	public SQLiteManager(Context context) {

		mContext = context;
		mOpenHelper = new OpenHelper(mContext);
		reset();
		
		db.execSQL(CREATE_TABLE_COLORS);
		db.setVersion(1);
		db.setLocale(Locale.getDefault());
	}
	
	public void reset() {
		db = mOpenHelper.getWritableDatabase();
	}
	
	public long saveColor(ColorPoint color) {
		
		if(color == null)
			return -1;
		reset();
		ContentValues values = new ContentValues();
		
		String strDate;
		Calendar c = Calendar.getInstance();
		strDate = String.valueOf(DateFormat.format(DATE_FORMAT, c.getTime()));
		values.put(COL_COLOR_DATE_CREATED, strDate);
		values.put(COL_COLOR_ARGB, color.getARGB());
		
		return db.replace(TABLE_COLORS, null, values);
	}
	
	public ArrayList<ColorSample> getColors() {
		return getColors(null, null, null);
	}
	
	
	public ArrayList<ColorSample> getColors(String having, String selection, String groupby) {
		
		ArrayList<ColorSample> colors = new ArrayList<ColorSample>();
		db.beginTransaction();
		Cursor cur = null;
		
		try {
			String orderby = COL_COLOR_DATE_CREATED + " DESC";
			
			cur = db.query(
					TABLE_COLORS,
					new String[] {COL_COLOR_ID, COL_COLOR_ARGB, COL_COLOR_DATE_CREATED, COL_COLOR_SOURCE},
					selection,
					null,
					groupby,
					having,
					orderby);
			
			cur.moveToPosition(0);
			cur.moveToFirst();
			
			int argb = -1;
			Date dateCreated = null;
			String source = null;
			
			while (!cur.isAfterLast()) {
				SimpleDateFormat formatter = new SimpleDateFormat(DATE_FORMAT, Locale.ENGLISH);
				
				argb = cur.getInt(1);
				ColorSample color = new ColorSample(argb);
				
				dateCreated = (Date)formatter.parse(cur.getString(2));
				color.setDate(dateCreated);
				
				source = cur.getString(3);
				color.setSource(source);
				
				colors.add(color);
				cur.moveToNext();
			}
			
			db.setTransactionSuccessful();
			
		} catch (Exception e) {
			DebugLog.e("Error in transaction", e.toString());
		} finally {
			db.endTransaction();

			if(cur != null)
				cur.close();
		}
		
		return colors;
	}
	
	public void clearColors() {
		db.execSQL(DROP_TABLE_COLORS);
	}
	
	public static List<String> GetColumns(SQLiteDatabase db, String tableName) {
	    
		List<String> ar = null;
	    Cursor c = null;
	    try {
	        c = db.rawQuery("select * from " + tableName + " limit 1", null);
	        if (c != null) {
	            ar = new ArrayList<String>(Arrays.asList(c.getColumnNames()));
	        }
	    } catch (Exception e) {
	        DebugLog.e(TAG, e.getMessage(), e);
	        e.printStackTrace();
	    } finally {
	        if (c != null)
	            c.close();
	    }
	    
	    return ar;
	}
	
	private static class OpenHelper extends SQLiteOpenHelper {

		OpenHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			
		}
	}
}