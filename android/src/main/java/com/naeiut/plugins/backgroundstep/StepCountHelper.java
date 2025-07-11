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

  private long lastSavedSteps = -1;

  @Override
  public void onSensorChanged(SensorEvent sensorEvent) {
    long currentSteps = (long) sensorEvent.values[0];
    if (currentSteps != lastSavedSteps) {
      save(currentSteps);
      lastSavedSteps = currentSteps;
    }
  }

  @Override
  public void onAccuracyChanged(Sensor sensor, int i) {
  }

  public void start() {
    // ✅ 센서 리스너 등록 (이게 없으면 onSensorChanged는 호출되지 않음)
    if (sensorManager != null && sensor != null) {
      sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL);
    }
  }

  public void stop() {
    if (sensorManager != null) {
      sensorManager.unregisterListener(this);
    }
  }

  public void save(long step) {
    StepCountDatabaseHelper.save(this.db, step);
  }

  public void close() {
    this.dbHelper.close();
  }


}