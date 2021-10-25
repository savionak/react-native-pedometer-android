# react-native-pedometer-android

React Native wrapper for Android step counter

## Installation

```
npm install git+https://github.com/savionak/react-native-pedometer-android.git
```

## Usage

```js
import { NativeModules } from 'react-native';
import Pedometer, { StepCounterEvent, SingleStepCounterEvent } from 'react-native-pedometer-android';

if (Pedometer.isSupported()) {
  const pedometerEvents = new NativeEventEmitter(NativeModules.Pedometer);

  // listen

  pedometerEvents.addListener(StepCounterEvent, (data) => {
    console.log('STEPS UPDATED: ', data.steps);
  });

  const periodMs = 1000;
  Pedometer.start(periodMs);

  // stop listening

  Pedometer.stop();
  pedometerEvents.

  // single shot

  pedometerEvents.addListener(SingleStepCounterEvent, (data) => {
    console.log('CURRENT STEPS: ', data.steps);
  });

  Pedometer.single();

} else {
  console.log('Sensor TYPE_STEP_COUNTER is not supported on this device');
}
