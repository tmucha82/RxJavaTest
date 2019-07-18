package com.infotarget.rx.java.book.chapter4;

import android.os.Handler;
import android.os.Looper;
import io.reactivex.Scheduler;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

import java.util.concurrent.TimeUnit;

public final class SimplifiedHandlerScheduler extends Scheduler {

  @Override
  public Worker createWorker() {
    return new HandlerWorker();
  }

  static class HandlerWorker extends Scheduler.Worker {

    private final Handler handler = new Handler(Looper.getMainLooper());
    private final CompositeDisposable compositeSubscription = new CompositeDisposable();

    @Override
    public Disposable schedule(Runnable action, long delayTime, TimeUnit unit) {
/*
      if (compositeSubscription.isDisposed()) {
        return Disposables.disposed();
      }

      final CompositeDisposable scheduledAction = new CompositeDisposable(Disposables.fromRunnable(action));
      scheduledAction.add(compositeSubscription);
      compositeSubscription.add(scheduledAction);

      handler.postDelayed(scheduledAction, unit.toMillis(delayTime));

      scheduledAction.add(Disposables.fromAction(() ->
          handler.removeCallbacks(scheduledAction)));

      return scheduledAction;
*/
      //TODO
      return null;
    }

    @Override
    public void dispose() {
      compositeSubscription.dispose();
    }

    @Override
    public boolean isDisposed() {
      return compositeSubscription.isDisposed();
    }
  }
}
