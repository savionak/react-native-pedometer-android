package com.savionak.reactnative.pedometer;

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
  public void showCurrentStepsToast() {
    StepCounterService.startActionTrigger(mReactContext, 0);
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
