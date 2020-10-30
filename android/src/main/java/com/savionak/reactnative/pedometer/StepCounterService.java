package com.savionak.reactnative.pedometer;

import android.app.Service;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Binder;
import android.os.IBinder;

import androidx.annotation.Nullable;

public class StepCounterService extends Service implements SensorEventListener {

  private IBinder mBinder = new StepCounterServiceBinder();
  private SensorManager mSensorManager;
  private Sensor mSensor;

  private boolean isRunning = false;
  private int steps = 0;

  public int getSteps() {
    return steps;
  }

  public void stopTracking() {
    if (isRunning) {
      mSensorManager.unregisterListener(this, mSensor);
    }
  }

  @Nullable
  @Override
  public IBinder onBind(Intent intent) {
    return this.mBinder;
  }

  public class StepCounterServiceBinder extends Binder {
    StepCounterService getService() {
      return StepCounterService.this;
    }
  }

  @Override
  public int onStartCommand(@Nullable Intent intent, int flags, int startId) {
    startTracking();
    return Service.START_REDELIVER_INTENT;
  }

  public void startTracking() {
    if (!isRunning) {
      mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
      mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
      mSensorManager.registerListener(this, mSensor, SensorManager.SENSOR_DELAY_NORMAL);
      isRunning = true;
    }
  }

  @Override
  public boolean stopService(Intent name) {
    isRunning = false;
    return super.stopService(name);
  }

  @Override
  public void onSensorChanged(SensorEvent sensorEvent) {
    if (sensorEvent.sensor.getType() == Sensor.TYPE_STEP_COUNTER) {
      steps = Math.round(sensorEvent.values[0]);
    }
  }

  @Override
  public void onAccuracyChanged(Sensor sensor, int i) {
    // do nothing
  }
}
