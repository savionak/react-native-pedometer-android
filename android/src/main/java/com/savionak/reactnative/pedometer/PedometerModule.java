package com.savionak.reactnative.pedometer;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;

public class PedometerModule extends ReactContextBaseJavaModule {

  private static final String MODULE_NAME = "Pedometer";

  private StepCounter mStepCounter;
  private ReactApplicationContext mReactContext;

  private boolean isServiceRunning = false;
  private boolean isServiceBound = false;

  private StepCounterService mStepCounterService;
  private ServiceConnection mConnection = new ServiceConnection() {
    @Override
    public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
      StepCounterService.StepCounterServiceBinder binder =
        (StepCounterService.StepCounterServiceBinder) iBinder;
      mStepCounterService = binder.getService();
      isServiceBound = true;
    }

    @Override
    public void onServiceDisconnected(ComponentName componentName) {
      isServiceBound = false;
    }
  };

  public PedometerModule(ReactApplicationContext reactContext) {
    super(reactContext);
    mReactContext = reactContext;
  }

  @NonNull
  @Override
  public String getName() {
    return MODULE_NAME;
  }

  @ReactMethod
  public void isSupported(Promise promise) {
    boolean supported = acquireStepCounter().isSupported();
    promise.resolve(supported);
  }

  @ReactMethod
  public void start(int periodMs, Promise promise) {
    StepCounter stepCounter = acquireStepCounter();
    boolean supported = stepCounter.isSupported();
    if (supported)
      stepCounter.start(periodMs);
    promise.resolve(supported);
  }

  @ReactMethod
  public void stop() {
    StepCounter stepCounter = acquireStepCounter(false);
    if (stepCounter != null)
      stepCounter.stop();
  }

  @ReactMethod
  public void startService(Promise promise) {
    StepCounter stepCounter = acquireStepCounter();
    boolean supported = stepCounter.isSupported();
    if (supported) {
      Activity activity = mReactContext.getCurrentActivity();
      Intent intent = new Intent(activity, StepCounterService.class);
      if (activity != null) {
        activity.bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
        activity.startService(intent);
        isServiceRunning = true;
      }
    }
    promise.resolve(supported);
  }

  @ReactMethod
  public void stopService(Promise promise) {
    Activity activity = mReactContext.getCurrentActivity();
    Intent intent = new Intent(activity, StepCounterService.class);
    if (activity != null) {
      if (isServiceRunning && isServiceBound) {
        mStepCounterService.stopTracking();
        isServiceBound = false;
        activity.unbindService(mConnection);
      }
      isServiceRunning = false;
      activity.stopService(intent);
    }
    promise.resolve(true);
  }

  @ReactMethod
  public void getCurrentSteps(Promise promise) {
    promise.resolve(mStepCounterService.getSteps());
  }

  private StepCounter acquireStepCounter() {
    return acquireStepCounter(true);
  }

  @Nullable
  private StepCounter acquireStepCounter(boolean createIfNull) {
    if (mStepCounter == null && createIfNull)
      mStepCounter = new StepCounter(mReactContext);
    return mStepCounter;
  }
}
