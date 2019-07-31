package com.infotarget.rx.java.book.chapter8.rxbinding;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.TextView;
import com.infotarget.rx.java.book.chapter8.rxandroid.MainThreadSubscription;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;

import static com.infotarget.rx.java.book.chapter8.rxandroid.MainThreadSubscription.verifyMainThread;

final class TextViewTextChangeEventOnSubscribe implements ObservableOnSubscribe<TextViewTextChangeEvent> {
  final TextView view;

  TextViewTextChangeEventOnSubscribe(TextView view) {
    this.view = view;
  }

  @Override
  public void subscribe(ObservableEmitter<TextViewTextChangeEvent> subscriber) throws Exception {
    verifyMainThread();

    final TextWatcher watcher = new TextWatcher() {
      @Override
      public void beforeTextChanged(CharSequence s, int start, int count, int after) {
      }

      @Override
      public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (!subscriber.isDisposed()) {
          subscriber.onNext(TextViewTextChangeEvent.create(view, s, start, before, count));
        }
      }

      @Override
      public void afterTextChanged(Editable s) {
      }
    };

    subscriber.setDisposable(new MainThreadSubscription() {
      @Override
      protected void onUnsubscribe() {
        view.removeTextChangedListener(watcher);
      }
    });

    view.addTextChangedListener(watcher);

    // Emit initial value.
    subscriber.onNext(TextViewTextChangeEvent.create(view, view.getText(), 0, 0, 0));
  }
}