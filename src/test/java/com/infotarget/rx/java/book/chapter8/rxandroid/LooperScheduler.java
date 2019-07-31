/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.infotarget.rx.java.book.chapter8.rxandroid;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import io.reactivex.Scheduler;
import io.reactivex.disposables.Disposable;
import io.reactivex.disposables.Disposables;
import io.reactivex.exceptions.OnErrorNotImplementedException;
import io.reactivex.plugins.RxJavaPlugins;

import java.util.concurrent.TimeUnit;

class LooperScheduler extends Scheduler {
  private final Handler handler;

  LooperScheduler(Looper looper) {
    handler = new Handler(looper);
  }

  LooperScheduler(Handler handler) {
    this.handler = handler;
  }

  @Override
  public Worker createWorker() {
    return new HandlerWorker(handler);
  }

  static class HandlerWorker extends Worker {
    private final Handler handler;
    private final RxAndroidSchedulersHook hook;
    private volatile boolean unsubscribed;

    HandlerWorker(Handler handler) {
      this.handler = handler;
      this.hook = RxAndroidPlugins.getInstance().getSchedulersHook();
    }

    @Override
    public void dispose() {
      unsubscribed = true;
      handler.removeCallbacksAndMessages(this /* token */);
    }

    @Override
    public boolean isDisposed() {
      return unsubscribed;
    }

    @Override
    public Disposable schedule(Runnable run) {
      return schedule(run, 0, TimeUnit.MILLISECONDS);
    }

    @Override
    public Disposable schedule(Runnable run, long delay, TimeUnit unit) {
      if (unsubscribed) {
        return Disposables.disposed();
      }

      run = hook.onSchedule(run);

      ScheduledAction scheduledAction = new ScheduledAction(run, handler);

      Message message = Message.obtain(handler, scheduledAction);
      message.obj = this; // Used as token for unsubscription operation.

      handler.sendMessageDelayed(message, unit.toMillis(delay));

      if (unsubscribed) {
        handler.removeCallbacks(scheduledAction);
        return Disposables.disposed();
      }

      return scheduledAction;
    }
  }

  static final class ScheduledAction implements Runnable, Disposable {
    private final Runnable action;
    private final Handler handler;
    private volatile boolean unsubscribed;

    ScheduledAction(Runnable action, Handler handler) {
      this.action = action;
      this.handler = handler;
    }

    @Override
    public void run() {
      try {
        action.run();
      } catch (Throwable e) {
        // nothing to do but print a System error as this is fatal and there is nowhere else to throw this
        IllegalStateException ie;
        if (e instanceof OnErrorNotImplementedException) {
          ie = new IllegalStateException("Exception thrown on Scheduler.Worker thread. Add `onError` handling.", e);
        } else {
          ie = new IllegalStateException("Fatal Exception thrown on Scheduler.Worker thread.", e);
        }
        try {
          RxJavaPlugins.getErrorHandler().accept(ie);
        } catch (Exception ex) {
          Thread thread = Thread.currentThread();
          thread.getUncaughtExceptionHandler().uncaughtException(thread, ie);
        }
      }
    }

    @Override
    public void dispose() {
      unsubscribed = true;
      handler.removeCallbacks(this);
    }

    @Override
    public boolean isDisposed() {
      return unsubscribed;
    }
  }
}