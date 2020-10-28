import { NativeModules } from 'react-native'

type PedometerType = {
  isSupported(): boolean
  start(periodMs: number): boolean
  stop(): void
  single(): boolean
  cancelSingle(): void
}

const { Pedometer } = NativeModules

export default Pedometer as PedometerType
