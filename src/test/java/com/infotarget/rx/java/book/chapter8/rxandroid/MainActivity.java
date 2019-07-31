package com.infotarget.rx.java.book.chapter8.rxandroid;

import android.app.Activity;
import android.os.Bundle;
import io.reactivex.Observable;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

import java.util.concurrent.TimeUnit;

public class MainActivity extends Activity {

  private final byte[] blob = new byte[32 * 1024 * 1024];

  private final CompositeDisposable allSubscriptions = new CompositeDisposable();

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    //...
    Disposable subscription = Observable
        .interval(100, TimeUnit.MILLISECONDS)
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(x -> {
//                    text.setText(Long.toString(x));
        });
    allSubscriptions.add(subscription);
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    allSubscriptions.dispose();
  }

}
