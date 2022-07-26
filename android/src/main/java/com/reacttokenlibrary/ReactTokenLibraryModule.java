package com.reacttokenlibrary;

import android.content.Intent;
import android.util.Log;
import androidx.annotation.NonNull;

import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.module.annotations.ReactModule;
import com.google.gson.JsonObject;

import mn.hipay.tokenlibrary.WebViewActivity;
import mn.hipay.tokenlibrary.callback.Worker;
import mn.hipay.tokenlibrary.callback.CardListenerCallback;
import mn.hipay.tokenlibrary.model.CardData;

@ReactModule(name = ReactTokenLibraryModule.NAME)
public class ReactTokenLibraryModule extends ReactContextBaseJavaModule {
  public Worker worker;
  public static final String NAME = "ReactTokenLibrary";
  ReactApplicationContext context = getReactApplicationContext();

  public ReactTokenLibraryModule(ReactApplicationContext reactContext) {

    super(reactContext);
    this.worker = new Worker(reactContext);
  }

  @Override
  @NonNull
  public String getName() {
    return NAME;
  }

  // Example method
  // See https://reactnative.dev/docs/native-modules-android
  @ReactMethod
  public void multiply(double a, double b, Promise promise) {
    promise.resolve(a * b);
  }

  @ReactMethod
  public void cardList(String customerId, Promise promise) {
    Log.d("cusID: ", customerId);
    this.worker.getCardListListener(
      customerId, new CardListenerCallback() {
        @Override
        public void onSuccess(String successMessage, JsonObject data) {
          Log.d("onSuccess", successMessage);
          System.out.println(data);

          promise.resolve(data.toString());
        }

        @Override
        public void onFailure(Throwable throwableError) {

          promise.resolve(throwableError.getMessage());
          Log.d("onFailure", "fail");
        }
      });
  }

  @ReactMethod
  public void cardRemove(Callback res, CardData cardData) {

    this.worker.setCardRemoveListener(cardData, new CardListenerCallback() {
      @Override
      public void onSuccess(String successMessage, com.google.gson.JsonObject data) {
        Log.d("succ", successMessage);
      }

      @Override
      public void onFailure(Throwable throwableError) {
        Log.d("err", "fail");

      }
    });
  }

  @ReactMethod
  public void cardAdd(String customerId, Callback res, Promise promise) {
    this.worker.setCardAddListener(customerId, new CardListenerCallback() {
      @Override
      public void onSuccess(String successMessage, JsonObject data) {
        // Log.d("succ", successMessage);
        System.out.println(data);
        promise.resolve(successMessage);
        String initId = data.get("initId").getAsString();
        res.invoke(successMessage);
        Intent webViewIntent = new Intent(context, WebViewActivity.class);
        webViewIntent.putExtra("initId", initId);
        if (webViewIntent.resolveActivity(context.getPackageManager()) != null) {
          webViewIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
          context.startActivity(webViewIntent);
        }

      }

      @Override
      public void onFailure(Throwable throwableError) {
        res.invoke(throwableError.getMessage());
        promise.resolve(throwableError.getMessage());
        Log.d("err", throwableError.getMessage());
      }
    });
  }

}
