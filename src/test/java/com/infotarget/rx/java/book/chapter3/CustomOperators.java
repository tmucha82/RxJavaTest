package com.infotarget.rx.java.book.chapter3;

import io.reactivex.*;
import io.reactivex.disposables.Disposable;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.Test;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import static io.reactivex.Observable.just;

public class CustomOperators {

  private static <T> Observable<T> odd(Observable<T> upstream) {
    Observable<Boolean> trueFalse = just(true, false).repeat();
    return upstream
        .zipWith(trueFalse, Pair::of)
        .filter(Pair::getRight)
        .map(Pair::getLeft);
  }

  private <T> ObservableTransformer<T, T> odd() {
    Observable<Boolean> trueFalse = just(true, false).repeat();
    return upstream -> upstream
        .zipWith(trueFalse, Pair::of)
        .filter(Pair::getRight)
        .map(Pair::getLeft);
  }

  @Test
  public void sample_617() {
    Observable<Integer> integerObservable = Observable.fromArray(1, 2, 3, 4, 5);
    //[A, B, C, D, E...]
    Observable<Character> alphabet =
        Observable
            .range(0, 'Z' - 'A' + 1)
            .map(c -> (char) ('A' + c));

    Observable<Integer> oddIntegers = odd(integerObservable);
    Observable<Character> oddAlphabet = odd(alphabet);

    oddIntegers.subscribe(System.out::println);
    oddAlphabet.subscribe(System.out::println);
  }

  @Test
  public void sample_618() {
    //[A, B, C, D, E...]
    Observable<Character> alphabet =
        Observable
            .range(0, 'Z' - 'A' + 1)
            .map(c -> (char) ('A' + c));

    //[A, C, E, G, I...]
    alphabet
        .compose(odd())
        .forEach(System.out::println);

    Observable<Integer> integerObservable = Observable.fromArray(1, 2, 3, 4, 5);
    integerObservable
        .compose(odd())
        .subscribe(System.out::println);
  }

  @Test
  public void sample_9() {
    Observable
        .range(1, 1000)
        .filter(x -> x % 3 == 0)
        .distinct()
        .reduce((a, x) -> a + x)
        .map(Integer::toHexString)
        .subscribe(System.out::println);
  }

  @Test
  public void sample_59() {
    Observable<String> odd = Observable
        .range(1, 9)
        .lift(toStringOfOdd());
    //Will emit: "1", "3", "5", "7" and "9" strings

    odd.subscribe(System.out::println);
  }

  public final class OddOperator<T> implements FlowableOperator<String, T> {

    @Override
    public Subscriber<? super T> apply(Subscriber<? super String> child) {
      return new Op(child);
    }

    final class Op implements FlowableSubscriber<T>, Subscription {

      final Subscriber<? super String> child;
      private boolean odd = true;

      Subscription s;

      Op(Subscriber<? super String> child) {
        this.child = child;
      }

      @Override
      public void onSubscribe(Subscription s) {
        this.s = s;
        child.onSubscribe(this);
      }

      @Override
      public void onNext(T v) {
        if (odd) {
          child.onNext(v.toString());
        } else {
          request(1);
        }
        odd = !odd;
      }

      @Override
      public void onError(Throwable e) {
        child.onError(e);
      }

      @Override
      public void onComplete() {
        child.onComplete();
      }

      @Override
      public void cancel() {
        s.cancel();
      }

      @Override
      public void request(long n) {
        s.request(n);
      }
    }
  }

  @Test
  public void sample_61() {
    Flowable
        .range(1, 4)
        .repeat()
        .lift(new OddOperator<>())
        .take(3)
        .subscribe(
            System.out::println,
            Throwable::printStackTrace,
            () -> System.out.println("Completed")
        );
  }


  private <T> ObservableOperator<String, T> toStringOfOdd() {
    return new ObservableOperator<String, T>() {
      @Override
      public Observer<? super T> apply(Observer<? super String> child) {
        return new Op(child);
      }

      final class Op implements Observer<T>, Disposable {

        final Observer<? super String> child;
        Disposable s;

        private boolean odd = true;

        Op(Observer<? super String> child) {
          this.child = child;
        }

        @Override
        public void onSubscribe(Disposable d) {
          child.onSubscribe(this);
          s = d;
        }

        @Override
        public void onNext(T t) {
          if (odd) {
            child.onNext(t.toString());
          }
          odd = !odd;
        }

        @Override
        public void onError(Throwable e) {
          child.onError(e);
        }

        @Override
        public void onComplete() {
          child.onComplete();
        }

        @Override
        public void dispose() {
          s.dispose();
        }

        @Override
        public boolean isDisposed() {
          return s.isDisposed();
        }
      }
    };
  }

  @Test
  public void sample_67() {
    Observable
        .range(1, 9)
        .buffer(1, 2)
        .concatMapIterable(x -> x)
        .map(Object::toString)
        .forEach(System.out::println);
  }

  @Test
  public void sample_112() {
    Observable
        .range(1, 4)
        .repeat()
        .lift(toStringOfOdd())
        .take(3)
        .subscribe(
            System.out::println,
            Throwable::printStackTrace,
            () -> System.out.println("Completed")
        );
  }
}