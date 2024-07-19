package com.naeiut.plugins.backgroundstep;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;


public class StepCountHelper implements SensorEventListener {

  private static final String TAG = "BackgroundService";

  private Context context;
  private StepCountDatabaseHelper dbHelper;
  private SQLiteDatabase db;
  private SensorManager sensorManager;
  private Sensor sensor;

  public StepCountHelper(Context context) {
    this.context = context;
    this.dbHelper = new StepCountDatabaseHelper(context);
    this.db = this.dbHelper.getWritableDatabase();
    this.sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
    if (this.sensorManager != null) {
      this.sensor = this.sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
    }

  }

  @Override
  public void onSensorChanged(SensorEvent sensorEvent) {
    save((long) sensorEvent.values[0]);
  }

  @Override
  public void onAccuracyChanged(Sensor sensor, int i) {
  }

  public void start() {
//    Toast.makeText(this.context, "Service on create", Toast.LENGTH_SHORT).show();
    this.sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_GAME);
  }

  public void stop() {
    this.sensorManager.unregisterListener(this, sensor);
  }

  public void save(long step) {
    StepCountDatabaseHelper.save(this.db, step);
  }

  public void close() {
    this.dbHelper.close();
  }


}
