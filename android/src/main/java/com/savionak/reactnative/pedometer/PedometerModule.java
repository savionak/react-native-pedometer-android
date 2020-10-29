package com.savionak.reactnative.pedometer;

import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;

public class PedometerModule extends ReactContextBaseJavaModule {
  private StepCounter mStepCounter;
  private ReactApplicationContext mReactContext;

  public PedometerModule(ReactApplicationContext reactContext) {
    super(reactContext);
    mReactContext = reactContext;
  }

  @Override
  public String getName() {
    return "Pedometer";
  }

  @ReactMethod
  public void isSupported(Promise promise) {
    if (mStepCounter == null)
      mStepCounter = new StepCounter(mReactContext);
    promise.resolve(mStepCounter.isSupported());
  }

  @ReactMethod
  public void start(int periodMs, Promise promise) {
    if (mStepCounter == null)
      mStepCounter = new StepCounter(mReactContext);
    boolean supported = mStepCounter.isSupported();
    if (supported)
      mStepCounter.start(periodMs);
    promise.resolve(supported);
  }

  @ReactMethod
  public void stop() {
    if (mStepCounter != null)
      mStepCounter.stop();
  }

  @ReactMethod
  public void single(Promise promise) {
    if (mStepCounter == null)
      mStepCounter = new StepCounter(mReactContext);
    boolean supported = mStepCounter.isSupported();
    if (supported)
      mStepCounter.single();
    promise.resolve(supported);
  }

  @ReactMethod
  public void cancelSingle() {
    if (mStepCounter != null)
      mStepCounter.cancelSingle();
  }
}
