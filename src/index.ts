import { NativeModules } from 'react-native'

type PedometerType = {
  isSupported(): Promise<boolean>
  start(periodMs: number): Promise<boolean>
  stop(): void
  showCurrentStepsToast(): void
}

const { Pedometer } = NativeModules

export default Pedometer as PedometerType

export const StepCounterEvent = "StepCounter";
