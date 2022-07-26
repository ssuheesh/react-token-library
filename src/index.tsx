import { NativeModules, Platform } from 'react-native';

const LINKING_ERROR =
  `The package 'react-token-library' doesn't seem to be linked. Make sure: \n\n` +
  Platform.select({ ios: "- You have run 'pod install'\n", default: '' }) +
  '- You rebuilt the app after installing the package\n' +
  '- You are not using Expo managed workflow\n';

const ReactTokenLibrary = NativeModules.ReactTokenLibrary  ? NativeModules.ReactTokenLibrary  : new Proxy(
      {},
      {
        get() {
          throw new Error(LINKING_ERROR);
        },
      }
    );

export function multiply(a: number, b: number): Promise<number> {
  return ReactTokenLibrary.multiply(a, b);
}
export function cardList(a: string): Promise<string>{
  return ReactTokenLibrary.cardList(a)
}
export function cardAdd(a: string): Promise<string> {
  return ReactTokenLibrary.cardAdd(a, (e: any)=>{console.log(e)})
}