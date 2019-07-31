package com.infotarget.rx.java.book.chapter8.rxbinding;

import android.view.KeyEvent;
import android.widget.TextView;
import com.infotarget.rx.java.book.chapter8.rxandroid.MainThreadSubscription;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.functions.Function;

import static com.infotarget.rx.java.book.chapter8.rxandroid.MainThreadSubscription.verifyMainThread;

final class TextViewEditorActionEventOnSubscribe implements ObservableOnSubscribe<TextViewEditorActionEvent> {
  final TextView view;
  final Function<? super TextViewEditorActionEvent, Boolean> handled;

  TextViewEditorActionEventOnSubscribe(TextView view,
                                       Function<? super TextViewEditorActionEvent, Boolean> handled) {
    this.view = view;
    this.handled = handled;
  }

  @Override
  public void subscribe(ObservableEmitter<TextViewEditorActionEvent> subscriber) {
    verifyMainThread();

    TextView.OnEditorActionListener listener = new TextView.OnEditorActionListener() {
      @Override
      public boolean onEditorAction(TextView v, int actionId, KeyEvent keyEvent) {
        TextViewEditorActionEvent event = TextViewEditorActionEvent.create(v, actionId, keyEvent);
        try {
          if (handled.apply(event)) {
            if (!subscriber.isDisposed()) {
              subscriber.onNext(event);
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