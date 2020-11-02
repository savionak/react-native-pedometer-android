package com.savionak.reactnative.pedometer;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.hardware.TriggerEvent;
import android.hardware.TriggerEventListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;

import com.facebook.react.bridge.Promise;

public class StepCounterService extends IntentService {

  private static final String ACTION_TRIGGER = "com.savionak.reactnative.pedometer.action.TRIGGER";
  private static final String EXTRA_PARAM_RESULT_RECEIVER = "com.savionak.reactnative.pedometer.extra.RESULT_RECEIVER";
  public static final String STEPS_RESULT = "com.savionak.reactnative.pedometer.extra.STEPS";

  public StepCounterService() {
    super("StepCounterService");
  }

  public static void startActionTrigger(Context context, Promise promise) {
    Intent intent = new Intent(context, StepCounterService.class);
    intent.setAction(ACTION_TRIGGER);
    ResultReceiver resultReceiver = new ResultReceiver(new Handler()) {
      @Override
      protected void onReceiveResult(int resultCode, Bundle resultData) {
        super.onReceiveResult(resultCode, resultData);
        final int steps = resultData.getInt(STEPS_RESULT, 0);
        promise.resolve(steps);
      }
    };
    intent.putExtra(EXTRA_PARAM_RESULT_RECEIVER, resultReceiver);
    context.startService(intent);
  }

  @Override
  protected void onHandleIntent(Intent intent) {
    if (intent != null) {
      final String action = intent.getAction();
      final ResultReceiver resultReceiver = (ResultReceiver) intent.getParcelableExtra(EXTRA_PARAM_RESULT_RECEIVER);
      if (ACTION_TRIGGER.equals(action) && resultReceiver != null) {
        handleActionTrigger(resultReceiver);
      }
    }
  }

  private void handleActionTrigger(ResultReceiver resultReceiver) {
    SensorManager sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
    Sensor stepCounter = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
    sensorManager.requestTriggerSensor(new TriggerEventListener() {
      @Override
      public void onTrigger(TriggerEvent triggerEvent) {
        final float value = triggerEvent.values[0];
        final Bundle b = new Bundle();
        b.putInt(STEPS_RESULT, Math.round(value));
        resultReceiver.send(0, b);
      }
    }, stepCounter);
  }
}
