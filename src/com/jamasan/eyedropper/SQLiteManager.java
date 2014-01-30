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
	private SQLiteDatabase mDb;
	private OpenHelper mOpenHelper;
	
	public static final String DATABASE_NAME = "sqliteWordTwist.db"; //TODO Change db name
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
		
		mDb.execSQL(CREATE_TABLE_COLORS);
		mDb.setVersion(1);
		mDb.setLocale(Locale.getDefault());
	}
	
	public void reset() {
		mDb = mOpenHelper.getWritableDatabase();
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
		
		return mDb.replace(TABLE_COLORS, null, values);
	}
	
	public ArrayList<ColorSample> getColors() {
		return getColors(null, null, null);
	}
	
	
	public ArrayList<ColorSample> getColors(String having, String selection, String groupby) {
		
		ArrayList<ColorSample> colors = new ArrayList<ColorSample>();
		mDb.beginTransaction();
		Cursor cur = null;
		
		try {
			String orderby = COL_COLOR_DATE_CREATED + " DESC";
			
			cur = mDb.query(
					TABLE_COLORS,
					new String[] {COL_COLOR_ID, COL_COLOR_ARGB, COL_COLOR_DATE_CREATED, COL_COLOR_SOURCE},
					selection,
					null,
					groupby,
					having,
					orderby);
			
			cur.moveToPosition(0);
			cur.moveToFirst();
			
			int id = -1;
			int argb = -1;
			Date dateCreated = null;
			String source = null;
			
			while (!cur.isAfterLast()) {
				SimpleDateFormat formatter = new SimpleDateFormat(DATE_FORMAT, Locale.ENGLISH);
				
				id = cur.getInt(0);
				argb = cur.getInt(1);
				dateCreated = (Date)formatter.parse(cur.getString(2));
				source = cur.getString(3);
				
				ColorSample color = new ColorSample(argb);
				color.setId(id);
				color.setDate(dateCreated);
				color.setSource(source);
				
				colors.add(color);
				cur.moveToNext();
			}
			
			mDb.setTransactionSuccessful();
			
		} catch (Exception e) {
			DebugLog.e("Error in transaction", e.toString());
		} finally {
			mDb.endTransaction();

			if(cur != null)
				cur.close();
		}
		
		return colors;
	}
	
	public boolean isColorSaved(int argb) {
		String whereClause = COL_COLOR_ARGB + "=?";
		
		Cursor cur = mDb.query(TABLE_COLORS,
						 new String[] {"COUNT(" + COL_COLOR_ID+")"},
						 whereClause,
						 new String[] {String.valueOf(argb)},
						 COL_COLOR_ARGB,
						 null,
						 null);
		cur.moveToFirst();
		int rowCount = cur.getInt(0);
		return rowCount > 0;
	}
	
	public int deleteColor(int colorId) {
		String whereClause = COL_COLOR_ID + "=?";
		String[] whereArgs = {String.valueOf(colorId)};
		return mDb.delete(TABLE_COLORS, whereClause, whereArgs);
	}
	
	public void clearColors() {
		mDb.execSQL(DROP_TABLE_COLORS);
	}
	
	public static List<String> GetColumns(SQLiteDatabase db, String tableName) {
	    
		List<String> ar = null;
	    Cursor c = null;
	    try {
	        c = db.rawQuery("SELECT * FROM " + tableName + " LIMIT 1", null);
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