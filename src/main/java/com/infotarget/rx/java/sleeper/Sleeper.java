package com.infotarget.rx.java.sleeper;

import java.time.Duration;

final public class Sleeper {

  public static void sleep(final Duration duration) {
    try {
      Thread.sleep(duration.toMillis());
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }
  }
}
