package com.infotarget.rx.java.book.chapter8.rxbinding.internal;

import io.reactivex.functions.Function;

import java.util.function.Supplier;

public final class Functions {
  private static final Always<Boolean> ALWAYS_TRUE = new Always<>(true);
  public static final Supplier<Boolean> FUNC0_ALWAYS_TRUE = ALWAYS_TRUE;
  public static final Function<Object, Boolean> FUNC1_ALWAYS_TRUE = ALWAYS_TRUE;

  private static final class Always<T> implements Function<Object, T>, Supplier<T> {
    private final T value;

    Always(T value) {
      this.value = value;
    }

    @Override
    public T apply(Object o) {
      return value;
    }

    @Override
    public T get() {
      return value;
    }
  }

  private Functions() {
    throw new AssertionError("No instances.");
  }
}