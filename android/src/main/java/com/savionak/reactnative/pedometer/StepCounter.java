package com.savionak.reactnative.pedometer;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

import androidx.annotation.Nullable;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.modules.core.DeviceEventManagerModule;

enum State {
  Listening,
  Stopped
}

public class StepCounter implements SensorEventListener {

  public static final String STEP_COUNTER_EVENT_NAME = "StepCounter";

  private final ReactApplicationContext mReactContext;
  private final SensorManager mSensorManager;
  private final Sensor mStepCounter;

  private State mState;

  public StepCounter(ReactApplicationContext reactContext) {
    mReactContext = reactContext;
    mSensorManager = (SensorManager) reactContext.getSystemService(Context.SENSOR_SERVICE);
    mStepCounter = mSensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
    mState = State.Stopped;
  }

  public boolean isSupported() {
    return (mStepCounter != null);
  }

  public void start(int periodMs) {
    stop();
    int periodUs = periodMs * 1000;
    mSensorManager.registerListener(this, mStepCounter, periodUs, periodUs);
    mState = State.Listening;
  }

  public void stop() {
    if (mState != State.Stopped) {
      mSensorManager.unregisterListener(this, mStepCounter);
      mState = State.Stopped;
    }
  }

  @Override
  public void onSensorChanged(SensorEvent sensorEvent) {
    handleSensorValues(sensorEvent.values, STEP_COUNTER_EVENT_NAME);
  }

  @Override
  public void onAccuracyChanged(Sensor sensor, int i) {
    // do nothing
  }

  private void handleSensorValues(float[] sensorValues, String eventName) {
    WritableMap values = Arguments.createMap();
    values.putDouble("steps", sensorValues[0]);
    sendEvent(eventName, values);
  }

  private void sendEvent(String eventName, @Nullable WritableMap params) {
    try {
      mReactContext
        .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
        .emit(eventName, params);
    } catch (RuntimeException e) {
      Log.e("ERROR", "java.lang.RuntimeException: Trying to invoke JS before CatalystInstance has been set!");
    }
  }
}
