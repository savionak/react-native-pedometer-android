package com.savionak.reactnative.pedometer;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.ResultReceiver;
import android.widget.Toast;

import com.facebook.react.bridge.Promise;

public class StepCounterService extends IntentService implements SensorEventListener {

  public static final String ACTION_START = "com.savionak.reactnative.pedometer.action.START";
  public static final String ACTION_STOP = "com.savionak.reactnative.pedometer.action.STOP";
  public static final String ACTION_GET_CURRENT = "com.savionak.reactnative.pedometer.action.GET_CURRENT";

  public static final String EXTRA_DELAY = "com.savionak.reactnative.pedometer.extra.DELAY";
  public static final String EXTRA_RECEIVER = "com.savionak.reactnative.pedometer.extra.RECEIVER";

  public static final String EXTRA_STEPS = "com.savionak.reactnative.pedometer.extra.STEPS";

  public static final int STATUS_STOPPED = 0;
  public static final int STATUS_RUNNING = 1;

  public static final int DEFAULT_SENSOR_DELAY_US = 15 * 1000 * 1000;

  private SensorManager mSensorManager;
  private Sensor mStepCounter;
  private int mSteps = 0;

  public StepCounterService() {
    super("StepCounterService");
  }

  public static void start(Context context, int sensorDelayUs) {
    Intent intent = new Intent(context, StepCounterService.class);
    intent.setAction(ACTION_START);
    intent.putExtra(EXTRA_DELAY, sensorDelayUs);
    context.startService(intent);
  }

  public static void stop(Context context) {
    Intent intent = new Intent(context, StepCounterService.class);
    intent.setAction(ACTION_STOP);
    context.startService(intent);
  }

  public static void getCurrentSteps(Context context, ResultReceiver resultReceiver) {
    Intent intent = new Intent(context, StepCounterService.class);
    intent.setAction(ACTION_GET_CURRENT);
    intent.putExtra(EXTRA_RECEIVER, resultReceiver);
    context.startService(intent);
  }

  @Override
  protected void onHandleIntent(Intent intent) {
    if (intent != null) {
      final String action = intent.getAction();
      if (ACTION_START.equals(action)) {
        handleActionStart(intent);
      } else if (ACTION_STOP.equals(action)) {
        handleActionStop(intent);
      } else if (ACTION_GET_CURRENT.equals(action)) {
        handleActionGetCurrent(intent);
      }
    }
  }

  private void handleActionStart(Intent intent) {
    int sensorDelayUs = intent.getIntExtra(EXTRA_DELAY, DEFAULT_SENSOR_DELAY_US);
    mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
    mStepCounter = mSensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
    if (mStepCounter != null) {
      mSensorManager.registerListener(this, mStepCounter, sensorDelayUs);
    }
    // Test
    showToast("Service STARTED");
  }

  private void handleActionStop(Intent intent) {
    if (mSensorManager != null && mStepCounter != null) {
      mSensorManager.unregisterListener(this, mStepCounter);
      mStepCounter = null;
      mSensorManager = null;
    }
    // Test
    showToast("Service STOPPED");
  }

  private void handleActionGetCurrent(Intent intent) {
    ResultReceiver resultReceiver = intent.getParcelableExtra(EXTRA_RECEIVER);
    if (resultReceiver != null) {
      int status = (mStepCounter != null) ? STATUS_RUNNING : STATUS_STOPPED;
      Bundle b = new Bundle();
      b.putInt(EXTRA_STEPS, mSteps);
      resultReceiver.send(status, b);
    }
    // Test
    showToast(String.format("Current steps: %d", mSteps));
  }

  @Override
  public void onSensorChanged(SensorEvent sensorEvent) {
    mSteps = Math.round(sensorEvent.values[0]);
    // Test
    showToast(String.format("Steps changed: %d", mSteps));
  }

  @Override
  public void onAccuracyChanged(Sensor sensor, int i) {
    // do nothing
  }

  // Test

  Handler mHandler = new Handler(Looper.getMainLooper());

  private void showToast(String message) {
    final String msg = message;
    mHandler.post(() -> {
      Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
    });
  }
}
