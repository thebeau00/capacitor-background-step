package com.naeiut.plugins.backgroundstep;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.time.LocalDateTime;
import java.time.ZoneId;

//import org.threeten.bp.LocalDateTime;
//import org.threeten.bp.ZoneId;

public class StepCountDatabaseHelper extends SQLiteOpenHelper {

  private static final String DB_NAME = "stepdata";
  private static final int DB_VERSION = 1;

  private static final String TB_NAME = "steps";
  private static final String TB_ID = "idx";
  private static final String TB_STEP = "step";
  private static final String TB_TIMESTAMP = "timestamp";

  StepCountDatabaseHelper(Context context) {
    super(context, DB_NAME, null, DB_VERSION);
  }

  public static void save(SQLiteDatabase db, Long step) {
    ContentValues values = new ContentValues();
    values.put(TB_STEP, step);
    values.put(TB_TIMESTAMP, System.currentTimeMillis());
    db.insertOrThrow(TB_NAME, null, values);
  }

  // public static int getStep(SQLiteDatabase db,LocalDateTime s, LocalDateTime e) {

  //   int count = 0;
  //   String fname = "strftime('%Y-%m-%dT%H:%M',DATETIME(" + TB_TIMESTAMP + "/1000, 'unixepoch','localtime'))";

  //   String sql = "select (max(" + TB_STEP + ") - min(" + TB_STEP + ")) as step from " + TB_NAME + " where " + fname + " >= '" + s + "' and " + fname + " < '" + e + "'";
  //   Cursor cursor = db.rawQuery(sql,null);

  //   while (cursor.moveToNext()) {
  //     count = cursor.getInt(0);
  //   }

  //   return count;

  // }

  public static int getStep(SQLiteDatabase db, LocalDateTime s, LocalDateTime e) {
    int count = 0;
    // Convert LocalDateTime to epoch seconds (Unix timestamp in seconds)
    long startTime = s.atZone(ZoneId.systemDefault()).toEpochSecond();
    long endTime = e.atZone(ZoneId.systemDefault()).toEpochSecond();
    // Use the epoch milliseconds for the timestamp comparison
    String sql = "select (max(" + TB_STEP + ") - min(" + TB_STEP + ")) as step from " + TB_NAME +
                " where " + TB_TIMESTAMP + " >= " + (startTime * 1000) +
                " and " + TB_TIMESTAMP + " < " + (endTime * 1000);
    Cursor cursor = db.rawQuery(sql, null);
    if (cursor.moveToFirst()) {
        count = cursor.getInt(0);
    }
    cursor.close(); // Don't forget to close the cursor
    return count;
  }


  @Override
  public void onCreate(SQLiteDatabase db) {
    db.execSQL("CREATE TABLE " + TB_NAME + " (" + TB_ID + " INTEGER PRIMARY KEY, " + TB_STEP + " INTEGER NOT NULL, " + TB_TIMESTAMP + " TIMESTAMP DEFAULT CURRENT_TIMESTAMP)");
    db.execSQL("CREATE INDEX stepindex ON " + TB_NAME + " (" + TB_STEP + ") ");
    db.execSQL("CREATE INDEX timeidex ON " + TB_NAME + " (" + TB_TIMESTAMP + ") ");
  }

  @Override
  public void onUpgrade(SQLiteDatabase db, int oVersion, int nVersion) {
    db.execSQL("DROP TABLE IF EXISTS " + TB_NAME);
    this.onCreate(db);
  }
}
