package com.naeiut.plugins.backgroundstep;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

public class RestartService extends BroadcastReceiver {

  private String TAG = "MyReceiver";

  @Override
  public void onReceive(Context context, Intent intent) {
    Log.d(TAG, "onReceive called");

//    Intent it = new Intent(context, StepCountBackgroundService.class);
//    context.startService(it);

    WorkManager workManager = WorkManager.getInstance(context);
    OneTimeWorkRequest startServiceRequest = new OneTimeWorkRequest.Builder(StepWorker.class).build();
    workManager.enqueue(startServiceRequest);

  }
}
