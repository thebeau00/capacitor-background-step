package com.naeiut.plugins.backgroundstep;

import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

public class StepWorker extends Worker {
  private final Context context;
  private String TAG = "MyWorker";

  public StepWorker(
    @NonNull Context context,
    @NonNull WorkerParameters params) {
    super(context, params);
    this.context = context;
  }

  @NonNull
  @Override
  public Result doWork() {
//    Log.d(TAG, "doWork called for: " + this.getId());
//    Log.d(TAG, "Service Running: " + StepCountBackgroundService.isServiceRunning);
    if (!StepCountBackgroundService.isServiceRunning) {
//      Log.d(TAG, "starting service from doWork");
      Intent intent = new Intent(this.context, StepCountBackgroundService.class);
      ContextCompat.startForegroundService(context, intent);
    }
    return Result.success();
  }

  @Override
  public void onStopped() {
//    Log.d(TAG, "onStopped called for: " + this.getId());
    super.onStopped();
  }
}
