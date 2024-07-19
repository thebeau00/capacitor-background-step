package com.naeiut.plugins.backgroundstep;

import com.getcapacitor.JSObject;
import com.getcapacitor.PermissionState;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;
import com.getcapacitor.annotation.CapacitorPlugin;
import com.getcapacitor.annotation.Permission;
import com.getcapacitor.annotation.PermissionCallback;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.Context;

import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
import androidx.work.WorkManager;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.PeriodicWorkRequest;

import org.threeten.bp.LocalDateTime;
import org.threeten.bp.LocalDate;
import org.threeten.bp.format.DateTimeFormatter;
import java.util.concurrent.TimeUnit;

import android.content.pm.PackageManager;
import androidx.core.app.ActivityCompat;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;

@CapacitorPlugin(
        name = "Backgroundstep",
        permissions = {
                @Permission(strings = { Manifest.permission.ACTIVITY_RECOGNITION }, alias = "activityRecognition")
        }
)
@RequiresApi(api = Build.VERSION_CODES.O)
public class BackgroundstepPlugin extends Plugin {

    private static final int PERMISSION_REQUEST_ACTIVITY_RECOGNITION = 1;
    private Backgroundstep implementation = new Backgroundstep();

    private String TAG = "BackgroundstepPlugin";

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
            StepCountBackgroundService.stopForegroundService(context, activity);
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

    @PluginMethod
    public void echo(PluginCall call) {
        String value = call.getString("value");

        JSObject ret = new JSObject();
        ret.put("value", implementation.echo(value));
        call.resolve(ret);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @PluginMethod
    public void serviceStart(PluginCall call) {

        startService(this.getActivity().getApplicationContext(), this.getActivity());
        startServiceViaWorker(this.getActivity().getApplicationContext());

        JSObject response = new JSObject();
        response.put("result", true);

        call.resolve(response);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @PluginMethod
    public void serviceStop(PluginCall call) {

        stopService(this.getActivity().getApplicationContext(), this.getActivity());
        stopServiceViaWorker(this.getActivity().getApplicationContext());

        JSObject response = new JSObject();
        response.put("result", true);

        call.resolve(response);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @PluginMethod
    public void getToday(PluginCall call) {

        LocalDateTime s = LocalDate.now().atStartOfDay();
        LocalDateTime e = LocalDate.now().plusDays(1).atStartOfDay();

//        Log.d(TAG, s + "");

        StepCountDatabaseHelper dbHelper = new StepCountDatabaseHelper(this.getActivity().getApplicationContext());
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        int step = StepCountDatabaseHelper.getStep(db,s,e);

        JSObject response = new JSObject();
        response.put("count", step);

        call.resolve(response);
    }

    @PluginMethod
    public void getStepData(PluginCall call) {
        String sDateTime = call.getString("sDateTime");
        String eDateTime = call.getString("eDateTime");

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        try {
            LocalDateTime s = LocalDateTime.parse(sDateTime, formatter);
            LocalDateTime e = LocalDateTime.parse(eDateTime, formatter);

            StepCountDatabaseHelper dbHelper = new StepCountDatabaseHelper(getContext());
            SQLiteDatabase db = dbHelper.getReadableDatabase();
            int step = StepCountDatabaseHelper.getStep(db, s, e);

            JSObject response = new JSObject();
            response.put("count", step);

            call.resolve(response);
        } catch (Exception e) {
            call.reject("ParsingError", "날짜와 시간을 파싱하는 도중 에러가 발생했습니다: " + e.getMessage());
        }
    }

    @PluginMethod
    public void checkAndRequestPermission(PluginCall call) {
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACTIVITY_RECOGNITION) == PackageManager.PERMISSION_GRANTED) {
            JSObject response = new JSObject();
            response.put("granted", true);

            call.resolve(response);
        } else {
            requestPermissionForAlias("activityRecognition", call, "activityRecognitionPermissionCallback");
        }
    }

    @PermissionCallback
    private void activityRecognitionPermissionCallback(PluginCall call) {
        if (getPermissionState("activityRecognition") == PermissionState.GRANTED) {
            JSObject response = new JSObject();
            response.put("granted", true);

            call.resolve(response);
        } else {
            call.reject("Permission not granted");
        }
    }
}
