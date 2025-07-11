package com.naeiut.plugins.backgroundstep;

import android.Manifest;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import com.jakewharton.threetenabp.AndroidThreeTen;

public class StepCountBackgroundService extends Service {

  private static final String TAG = "BackgroundService";

  private StepCountHelper stepCountHelper;

  public static boolean isServiceRunning;
  private String CHANNEL_ID; // 🔄 변경: 초기화는 onCreate에서 수행
  private Context context;

  public StepCountBackgroundService() {
    Log.d(TAG, "constructor called");
    isServiceRunning = false;
  }

  @Override
  public int onStartCommand(Intent intent, int flags, int startId) {

    Log.d(TAG, "onStartCommand called");
    Intent notificationIntent = new Intent(this, com.getcapacitor.BridgeActivity.class);
    PendingIntent pendingIntent = PendingIntent.getActivity(this,
      0, notificationIntent, PendingIntent.FLAG_IMMUTABLE);

    Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
      .setContentTitle(getString(R.string.notification_title))
      .setContentText(getString(R.string.notification_text))
//    .setSmallIcon(R.drawable.ic_launcher_background)
      .setSmallIcon(R.mipmap.ic_launcher_foreground)
      .setContentIntent(pendingIntent)
      .setAutoCancel(true)
// 	.setPriority(NotificationCompat.PRIORITY_LOW)
//    .setColor(getResources().getColor(R.color.colorPrimary))
      .build();

    // ✅ Foreground 시작을 Thread 시작 전에 먼저 수행해야 함
    startForeground(1, notification);

    // ✅ stepCountHelper 실행 (센서 리스너 등록만)
    if (stepCountHelper != null) {
      stepCountHelper.start(); // ✨ 센서 리스너 등록
    }

    return START_STICKY;
  }

//  public static void stopForegroundService(Context context, Activity activity) {
//    StepCountHelper stepCountHelper = new StepCountHelper(context);
//    stepCountHelper.stop();
//    StepCountBackgroundService service = new StepCountBackgroundService();
//    service.stopSelf();
//    isServiceRunning = false;
//  }
  public static void stopForegroundService(Context context) {
    Intent stopIntent = new Intent(context, StepCountBackgroundService.class);
    context.stopService(stopIntent);
  }

  @Override
  public void onCreate() {
    super.onCreate();

    this.context = this;

    // 🔄 변경: 문자열 리소스에서 실제 값 가져오기
    CHANNEL_ID = getString(R.string.notification_channel_id);

    AndroidThreeTen.init(this);

    int permission = ContextCompat.checkSelfPermission(this, Manifest.permission.ACTIVITY_RECOGNITION);
    if(permission == PackageManager.PERMISSION_GRANTED) {
//    Toast.makeText(this, "Service on create2", Toast.LENGTH_SHORT).show();
      this.stepCountHelper = new StepCountHelper(getApplicationContext());
      // ❌ stepCountHelper.start()는 onStartCommand에서 호출
      createNotificationChannel();
      isServiceRunning = true;
    }

  }

  @Override
  public IBinder onBind(Intent intent) {
      // We don't provide binding, so return null
      return null;
  }

  private void createNotificationChannel() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      String appName = getString(R.string.app_name);
      NotificationChannel serviceChannel = new NotificationChannel(
        CHANNEL_ID,
        appName,
        NotificationManager.IMPORTANCE_DEFAULT
      );
      NotificationManager manager = getSystemService(NotificationManager.class);
      manager.createNotificationChannel(serviceChannel);
    }
  }

  @Override
  public void onDestroy() {
    isServiceRunning = false;
    stopForeground(true);

    // ✅ 센서 리스너 해제
    if (stepCountHelper != null) {
      stepCountHelper.stop();
      stepCountHelper.close(); // ✅ DB 자원 해제
    }

    // ✅ 필요한 경우 서비스 재시작
    Intent restartService = new Intent(this.context, RestartService.class);
    sendBroadcast(restartService);

    super.onDestroy();
  }


}