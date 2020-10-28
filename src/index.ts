import { NativeModules } from 'react-native'

type Pedometer = {
  isSupported(): boolean
  start(periodMs: number): boolean
  stop(): void
  single(): boolean
  cancelSingle(): void
}

const { Pedometer } = NativeModules

export default Pedometer as Pedometer
