package com.infotarget.rx.java.book.chapter3;

import io.reactivex.ObservableOperator;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

import java.util.function.Function;

final class OperatorMap<T, R> implements ObservableOperator<R, T> {

  private final Function<T, R> transformer;

  public OperatorMap(Function<T, R> transformer) {
    this.transformer = transformer;
  }

  @Override
  public Observer<? super T> apply(Observer<? super R> child) throws Exception {
    return new MapOp(child);
  }

  final class MapOp implements Observer<T> {

    final Observer<? super R> child;

    MapOp(Observer<? super R> child) {
      this.child = child;
    }

    @Override
    public void onSubscribe(Disposable d) {
      child.onSubscribe(d);
    }

    @Override
    public void onNext(T t) {
      try {
        child.onNext(transformer.apply(t));
      } catch (Exception e) {
        onError(e);
      }
    }

    @Override
    public void onError(Throwable e) {
      child.onError(e);
    }

    @Override
    public void onComplete() {
      child.onComplete();
    }
  }
}


