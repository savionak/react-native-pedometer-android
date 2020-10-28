package com.savionak.reactnative.pedometer;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.hardware.TriggerEvent;
import android.hardware.TriggerEventListener;
import android.util.Log;

import androidx.annotation.Nullable;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.modules.core.DeviceEventManagerModule;

public class StepCounter extends TriggerEventListener implements SensorEventListener {

    public static final String STEP_COUNTER_EVENT_NAME = "StepCounter";
    public static final String SINGLE_STEP_COUNTER_EVENT_NAME = "SingleStepCounter";

    private final ReactApplicationContext mReactContext;
    private final SensorManager mSensorManager;
    private final Sensor mStepCounter;

    public StepCounter(ReactApplicationContext reactContext) {
        mReactContext = reactContext;
        mSensorManager = (SensorManager) reactContext.getSystemService(Context.SENSOR_SERVICE);
        mStepCounter = mSensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
    }

    public boolean isSupported() {
        return (mStepCounter != null);
    }

    public boolean start(int periodMs) {
        boolean status = isSupported();
        if (status) {
            stop();
            int periodUs = periodMs * 1000;
            mSensorManager.registerListener(this, mStepCounter, periodUs);
        }
        return status;
    }

    public void stop() {
        mSensorManager.unregisterListener(this, mStepCounter);
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        handleSensorValues(sensorEvent.values, STEP_COUNTER_EVENT_NAME);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
    }

    public boolean single() {
        boolean status = isSupported();
        if (status) {
            mSensorManager.requestTriggerSensor(this, mStepCounter);
        }
        return status;
    }

    public void cancelSingle() {
        mSensorManager.cancelTriggerSensor(this, mStepCounter);
    }

    @Override
    public void onTrigger(TriggerEvent sensorEvent) {
        handleSensorValues(sensorEvent.values, SINGLE_STEP_COUNTER_EVENT_NAME);
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
