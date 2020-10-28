# react-native-pedometer-android
React Native wrapper for Android step counter

## Installation
```
npm install TODO
```

## Usage

### Subscribe
```js
TODO
import Pedometer from 'package-name';

if (Pedometer.isSupported()) {

  const events = new NativeEventEmitter(NativeModules.Pedometer);
  events.addListener('StepCounter', (data) => {
    console.log('STEPS', data.steps);
  });

  const periodMs = 1000;
  Pedometer.startStepCounter(periodMs);

} else {
  console.log('Sensor TYPE_STEP_COUNTER is not supported on this device');
};
```
## Single shot
```js
TODO
```
