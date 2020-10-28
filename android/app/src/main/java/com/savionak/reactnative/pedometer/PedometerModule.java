package com.savionak.reactnative.pedometer;

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
    public boolean isSupported() {
        if (mStepCounter == null)
            mStepCounter = new StepCounter(mReactContext);
        return mStepCounter.isSupported();
    }

    @ReactMethod
    public boolean start(int periodMs) {
        if (mStepCounter == null)
            mStepCounter = new StepCounter(mReactContext);
        return mStepCounter.start(periodMs);
    }

    @ReactMethod
    public void stop() {
        if (mStepCounter != null)
            mStepCounter.stop();
    }

    @ReactMethod
    public boolean single() {
        if (mStepCounter == null)
            mStepCounter = new StepCounter(mReactContext);
        return mStepCounter.single();
    }

    @ReactMethod
    public void cancelSingle() {
        if (mStepCounter != null)
            mStepCounter.cancelSingle();
    }
}
