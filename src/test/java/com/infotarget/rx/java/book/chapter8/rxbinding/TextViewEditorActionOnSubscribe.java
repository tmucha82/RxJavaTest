package com.infotarget.rx.java.book.chapter8.rxbinding;

import android.view.KeyEvent;
import android.widget.TextView;
import com.infotarget.rx.java.book.chapter8.rxandroid.MainThreadSubscription;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.functions.Function;

import static com.infotarget.rx.java.book.chapter8.rxandroid.MainThreadSubscription.verifyMainThread;

final class TextViewEditorActionOnSubscribe implements ObservableOnSubscribe<Integer> {
  final TextView view;
  final Function<? super Integer, Boolean> handled;

  TextViewEditorActionOnSubscribe(TextView view, Function<? super Integer, Boolean> handled) {
    this.view = view;
    this.handled = handled;
  }

  @Override
  public void subscribe(final ObservableEmitter<Integer> subscriber) throws Exception {
    verifyMainThread();


    TextView.OnEditorActionListener listener = new TextView.OnEditorActionListener() {
      @Override
      public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        try {
          if (handled.apply(actionId)) {
            if (!subscriber.isDisposed()) {
              subscriber.onNext(actionId);
            }
            return true;
          }
        } catch (Exception e) {
          return false;
        }
        return false;
      }
    };

    subscriber.setDisposable(new MainThreadSubscription() {
      @Override
      protected void onUnsubscribe() {
        view.setOnEditorActionListener(null);
      }
    });

    view.setOnEditorActionListener(listener);
  }
}