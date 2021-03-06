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
	
	public static final String DATABASE_NAME = "eyedropper.db"; //TODO Change db name
	public static final int DATABASE_VERSION = 2;
	
	private static final String TABLE_COLORS = "tbl_colors";
	private static final String COL_COLOR_ID = "id";
	private static final String COL_COLOR_ARGB = "argb";
	private static final String COL_COLOR_NAME = "name";
	private static final String COL_COLOR_DATE_CREATED = "date_created";
	private static final String COL_COLOR_SOURCE = "source";
	
	public static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
	
	// Awards Table setup
	final static String CREATE_TABLE_COLORS  =
    	"CREATE TABLE IF NOT EXISTS " + TABLE_COLORS + " (" + 
    	COL_COLOR_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
    	COL_COLOR_ARGB + " INT, " +
    	COL_COLOR_NAME + " TEXT, " +
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
	
	public long saveColor(ColorSample color) {
		
		if(color == null) return -1;
		reset();
		ContentValues values = new ContentValues();
		
		Calendar c = Calendar.getInstance();
		CharSequence date = DateFormat.format(DATE_FORMAT, c.getTime());
		values.put(COL_COLOR_DATE_CREATED, String.valueOf(date));
		values.put(COL_COLOR_NAME, color.getName());
		values.put(COL_COLOR_ARGB, color.getARGB());
		return mDb.replace(TABLE_COLORS, null, values);
	}
	
	public ArrayList<ColorSample> getColors() {
		return getColors(null, null, null, null);
	}
	
	
	public ArrayList<ColorSample> getColors(String having, String selection, String[] selectionArgs, String groupBy) {
		ArrayList<ColorSample> colors = new ArrayList<ColorSample>();
		mDb.beginTransaction();
		Cursor cur = null;
		String[] cols = new String[] {COL_COLOR_ID, COL_COLOR_ARGB, 
									  COL_COLOR_NAME, COL_COLOR_DATE_CREATED,
									  COL_COLOR_SOURCE};
		try {
			String orderBy = COL_COLOR_DATE_CREATED + " DESC";
			cur = mDb.query(TABLE_COLORS, cols,	selection, selectionArgs,
							groupBy, having, orderBy);
			cur.moveToFirst();
			
			long id = -1;
			int argb = -1;
			String name = "";
			Date dateCreated = null;
			String source = null;
			
			while (!cur.isAfterLast()) {
				SimpleDateFormat formatter;
				formatter = new SimpleDateFormat(DATE_FORMAT, Locale.ENGLISH);
				
				id = cur.getLong(0);
				argb = cur.getInt(1);
				name = cur.getString(2);
				dateCreated = (Date)formatter.parse(cur.getString(3));
				source = cur.getString(4);
				
				ColorSample color = new ColorSample(argb);
				color.setId(id);
				color.setName(name);
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
	
	public ColorSample isColorSaved(int argb) {
		ColorSample result = null;
		String selection = COL_COLOR_ARGB + "=?";
		String[] selectionArgs = { String.valueOf(argb) };
		ArrayList<ColorSample> colors = 
				getColors(null, selection, selectionArgs, null);
		if (colors != null && colors.size() > 0) { 
			result = colors.get(0);
		}
		return result;
	}
	
	public boolean isColorIdSaved(long colorId) {
		String whereClause = COL_COLOR_ID + "=?";
		Cursor cur = mDb.query(TABLE_COLORS,
						 new String[] { "COUNT(" + COL_COLOR_ID + ")" },
						 whereClause,
						 new String[] { String.valueOf(colorId) },
						 COL_COLOR_ID,
						 null,
						 null);
		boolean result = cur.moveToFirst();
		if (result) {
			int rowCount = cur.getInt(0);
			return rowCount > 0;
		} else {
			return false;
		}
	}
	
	public int deleteColor(long colorId) {
		String whereClause = COL_COLOR_ID + "=?";
		String[] whereArgs = { String.valueOf(colorId) };
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