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

    if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {

      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE /* API 34 */) {
        // Android 15 이상에서는 BOOT_COMPLETED에서 포그라운드 서비스 시작 제한 → WorkManager 등으로 예약
        Log.d(TAG, "Android 15 이상 - WorkManager로 서비스 예약");

        WorkManager workManager = WorkManager.getInstance(context);
        OneTimeWorkRequest startServiceRequest = new OneTimeWorkRequest.Builder(StepWorker.class).build();
        workManager.enqueue(startServiceRequest);

      } else {
        // Android 14 이하에서는 직접 서비스 시작 가능
        Log.d(TAG, "Android 14 이하 - 직접 서비스 시작");

        Intent serviceIntent = new Intent(context, StepCountBackgroundService.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
          context.startForegroundService(serviceIntent);
        } else {
          context.startService(serviceIntent);
        }
      }
    }
  }
}
