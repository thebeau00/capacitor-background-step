# capacitor-background-step

capacitor plugin for counting step works in background.


## Platform caution

This plugin works differently on Android and iPhone devices.

Android devices bring information from the native sensor directly and store it in SQLight DB, but ios devices bring information from the 'Health' app and work.


## Install

```bash
npm install capacitor-background-step
ionic cap sync
ionic cap build android
```

## IOS - Health app authoriztion & add framework & use permissions

```
Go to the Apple Developer site and register your app.
Ensure you enable the HealthKit capability for your app. This is crucial as the capacitor-background-step plugin relies on HealthKit to access step count data.
```

```
Open your project in Xcode.
Navigate to your app target's settings.
Go to the "Signing & Capabilities" tab.
Click the "+" button to add a new capability.
Select "HealthKit" from the list. This will configure your app to request access to HealthKit data.
```

```
Still in Xcode, navigate to your app target's settings.
Go to the "Build Phases" tab.
Expand the "Link Binary With Libraries" section.
Click the "+" button and search for HealthKit.framework.
Add HealthKit.framework to ensure your app can use HealthKit's APIs.
```

```
Open the Info.plist file in your project.
Add the following keys and values to request the necessary permissions for accessing and updating health data:

<key>NSHealthShareUsageDescription</key>
<string>Access to health data is needed to track step count.</string>
<key>NSHealthUpdateUsageDescription</key>
<string>Access to health data is needed to update step count.</string>
```


## Android - Modify main AndroidManifest.xml in Android studio

```
		<!-- Add service & receiver tags in application tag -->
      <service
        android:name="com.naeiut.plugins.backgroundstep.StepCountBackgroundService"
        android:enabled="true"
        android:exported="true"
        android:foregroundServiceType="dataSync" />

      <receiver
        android:name="com.naeiut.plugins.backgroundstep.RestartService"
        android:enabled="true"
        android:exported="false"
        android:label="RestartServiceWhenStopped"
        android:permission="android.permission.RECEIVE_BOOT_COMPLETED">
        <intent-filter>
          <action android:name="RestartService" />
        </intent-filter>
      </receiver>
```
```
    <!-- Add permissions for capacitor-background-step -->
    <uses-permission android:name="android.permission.POST_NOTIFICATIONS" />
    <uses-permission android:name="android.permission.ACTIVITY_RECOGNITION" />
    <uses-permission android:name="android.permission.FOREGROUND_SERVICE" />
    <uses-permission android:name="android.permission.FOREGROUND_SERVICE_DATA_SYNC" />
```

## Modify MainActivity.java in Android studio
```
You do not need to add any more code from this version.
```


## Configure notification

You can set some values in android/src/main/res directory.

## Usage
```
import { Backgroundstep } from 'capacitor-background-step';

...

// start service
const data = await Backgroundstep.checkAndRequestPermission();
const permissionGranted = data.granted;

if (permissionGranted) {
	await Backgroundstep.serviceStart();
} else {
	console.log('Permission denied');
}

// get Today's step
Backgroundstep.getToday().then((data:any) => {
  console.log('Today step total:',data);
});

// get Some term's step
Backgroundstep.getStepData({ sDateTime: '2024-07-19 06:00:00', eDateTime: '2024-07-19 09:00:00'}).then((data:any) => {
  console.log('3 Hours total:',data);
});

```

## API
Method 'echo' is not concern to this plugin.

<docgen-index>

* [`echo(...)`](#echo)
* [`serviceStart()`](#servicestart)
* [`serviceStop()`](#servicestop)
* [`getToday()`](#gettoday)
* [`getStepData(...)`](#getstepdata)
* [`checkAndRequestPermission()`](#checkandrequestpermission)
* [Interfaces](#interfaces)

</docgen-index>

<docgen-api>
<!--Update the source file JSDoc comments and rerun docgen to update the docs below-->

### echo(...)

```typescript
echo(options: { value: string; }) => Promise<{ value: string; }>
```

| Param         | Type                            |
| ------------- | ------------------------------- |
| **`options`** | <code>{ value: string; }</code> |

**Returns:** <code>Promise&lt;{ value: string; }&gt;</code>

--------------------


### serviceStart()

```typescript
serviceStart() => Promise<resultInterface>
```

**Returns:** <code>Promise&lt;<a href="#resultinterface">resultInterface</a>&gt;</code>

--------------------


### serviceStop()

```typescript
serviceStop() => Promise<resultInterface>
```

**Returns:** <code>Promise&lt;<a href="#resultinterface">resultInterface</a>&gt;</code>

--------------------


### getToday()

```typescript
getToday() => Promise<StepDataInterface>
```

**Returns:** <code>Promise&lt;<a href="#stepdatainterface">StepDataInterface</a>&gt;</code>

--------------------


### getStepData(...)

```typescript
getStepData(term: { sDateTime: string; eDateTime: string; }) => Promise<StepDataInterface>
```

| Param      | Type                                                   |
| ---------- | ------------------------------------------------------ |
| **`term`** | <code>{ sDateTime: string; eDateTime: string; }</code> |

**Returns:** <code>Promise&lt;<a href="#stepdatainterface">StepDataInterface</a>&gt;</code>

--------------------


### checkAndRequestPermission()

```typescript
checkAndRequestPermission() => Promise<PermissionResultInterface>
```

**Returns:** <code>Promise&lt;<a href="#permissionresultinterface">PermissionResultInterface</a>&gt;</code>

--------------------


### Interfaces


#### resultInterface

| Prop      | Type                 |
| --------- | -------------------- |
| **`res`** | <code>boolean</code> |


#### StepDataInterface

| Prop        | Type                |
| ----------- | ------------------- |
| **`count`** | <code>number</code> |


#### PermissionResultInterface

| Prop          | Type                 |
| ------------- | -------------------- |
| **`granted`** | <code>boolean</code> |

</docgen-api>
