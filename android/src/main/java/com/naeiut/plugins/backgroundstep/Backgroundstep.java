package com.naeiut.plugins.backgroundstep;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import androidx.core.content.ContextCompat;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import java.util.concurrent.TimeUnit;

public class Backgroundstep {

    public String echo(String value) {
      //   Log.i("Echo", value);
        return value;
    }


    public static void startService(Context context, Activity activity) {
        if (!StepCountBackgroundService.isServiceRunning) {
            Intent serviceIntent = new Intent(context, StepCountBackgroundService.class);
            ContextCompat.startForegroundService(context, serviceIntent);
        }
    }

    public static void stopService(Context context, Activity activity) {
        if (StepCountBackgroundService.isServiceRunning) {
            StepCountBackgroundService stepCountBackgroundService = new StepCountBackgroundService();
            Intent serviceIntent = new Intent(context, StepCountBackgroundService.class);
            StepCountBackgroundService.stopForegroundService(context);
        }
    }

    public static void startServiceViaWorker(Context context) {
        String UNIQUE_WORK_NAME = "StartMyServiceViaWorker";
        WorkManager workManager = WorkManager.getInstance(context);

        // As per Documentation: The minimum repeat interval that can be defined is 15 minutes
        // (same as the JobScheduler API), but in practice 15 doesn't work. Using 16 here
        PeriodicWorkRequest request =
                new PeriodicWorkRequest.Builder(
                        StepWorker.class,
                        16,
                        TimeUnit.MINUTES)
                        .build();

        // to schedule a unique work, no matter how many times app is opened i.e. startServiceViaWorker gets called
        // do check for AutoStart permission
        workManager.enqueueUniquePeriodicWork(UNIQUE_WORK_NAME, ExistingPeriodicWorkPolicy.KEEP, request);
    }

    public static void stopServiceViaWorker(Context context) {
        String UNIQUE_WORK_NAME = "StartMyServiceViaWorker";
        WorkManager workManager = WorkManager.getInstance(context);
        workManager.cancelUniqueWork(UNIQUE_WORK_NAME);
    }
}
