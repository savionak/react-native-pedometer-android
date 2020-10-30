package com.savionak.reactnative.pedometer;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.hardware.TriggerEvent;
import android.hardware.TriggerEventListener;
import android.os.Handler;
import android.widget.Toast;

import androidx.annotation.Nullable;

import java.util.Locale;

public class StepCounterService extends IntentService {

  private static final String ACTION_TRIGGER = "com.savionak.reactnative.pedometer.action.TRIGGER";
  private static final String EXTRA_PARAM_PERIOD_MS = "com.savionak.reactnative.pedometer.extra.PERIOD_MS";

  private static final int DEFAULT_PERIOD_MS = 1000;

  public StepCounterService() {
    super("StepCounterService");
  }

  public static void startActionTrigger(Context context, int periodMs) {
    Intent intent = new Intent(context, StepCounterService.class);
    intent.setAction(ACTION_TRIGGER);
    intent.putExtra(EXTRA_PARAM_PERIOD_MS, periodMs);
    context.startService(intent);
  }

  @Override
  protected void onHandleIntent(Intent intent) {
    if (intent != null) {
      final String action = intent.getAction();
      if (ACTION_TRIGGER.equals(action)) {
        final int periodMs = intent.getIntExtra(EXTRA_PARAM_PERIOD_MS, DEFAULT_PERIOD_MS);
        handleActionTrigger(periodMs);
      }
    }
  }

  private void handleActionTrigger(int periodMs) {
    SensorManager sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
    Sensor stepCounter = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
    sensorManager.requestTriggerSensor(listener, stepCounter);
  }

  private final TriggerEventListener listener = new TriggerEventListener() {
    @Override
    public void onTrigger(TriggerEvent triggerEvent) {
      final float value = triggerEvent.values[0];
      final String toastText = String.format(Locale.ENGLISH, "Steps count: %.0f", value);
      showToast(toastText);
    }
  };

  private void showToast(String text) {
    new Handler().post(
      () -> Toast.makeText(
        getApplicationContext(),
        text,
        Toast.LENGTH_SHORT).show()
    );
  }
}
