package com.infotarget.rx.java.book.chapter8.rxbinding;

import android.view.View;
import com.infotarget.rx.java.book.chapter8.rxandroid.MainThreadSubscription;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;

import static com.infotarget.rx.java.book.chapter8.rxandroid.MainThreadSubscription.verifyMainThread;

final class ViewClickOnSubscribe implements ObservableOnSubscribe<Void> {
  final View view;

  ViewClickOnSubscribe(View view) {
    this.view = view;
  }

  @Override
  public void subscribe(ObservableEmitter<Void> subscriber) throws Exception {
    verifyMainThread();

    View.OnClickListener listener = new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        if (!subscriber.isDisposed()) {
          subscriber.onNext(null);
        }
      }
    };

    subscriber.setDisposable(new MainThreadSubscription() {
      @Override
      protected void onUnsubscribe() {
        view.setOnClickListener(null);
      }
    });

    view.setOnClickListener(listener);

  }
}