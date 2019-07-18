package com.infotarget.rx.java.book.chapter2;


import com.infotarget.rx.java.sleeper.Sleeper;
import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subscribers.ResourceSubscriber;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigInteger;
import java.time.Duration;
import java.util.Arrays;
import java.util.Collection;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static java.math.BigInteger.ONE;
import static java.math.BigInteger.ZERO;
import static java.util.concurrent.TimeUnit.MICROSECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;

public class Chapter2Test {

  private final static Logger log = LoggerFactory.getLogger(Chapter2Test.class);

  @Test
  public void sample_6() {
    Observable<Tweet> tweets = createTestObservable(); //...

    tweets.subscribe(this::print);
  }


  @Test
  public void sample_17() {
    Observable<Tweet> tweets = createTestObservable(); //...

    tweets.subscribe(
        this::print,
        Throwable::printStackTrace
    );
  }

  @Test
  public void sample_27() {
    Observable<Tweet> tweets = createTestObservable(); //...

    tweets.subscribe(
        this::print,
        Throwable::printStackTrace,
        this::noMore
    );
  }

  private void noMore() {
    log.info("noMore method");
  }

  @Test
  public void sample_51() {
    Observable<Tweet> tweets = createTestObservable(); //...

    Observer<Tweet> observer = new Observer<Tweet>() {
      @Override
      public void onSubscribe(Disposable d) {
        log.info("OnSubscribe = " + d.toString());
      }

      @Override
      public void onNext(Tweet tweet) {
        print(tweet);
      }

      @Override
      public void onError(Throwable e) {
        e.printStackTrace();
      }

      @Override
      public void onComplete() {
        noMore();
      }
    };

    //...

    tweets.subscribe(observer);
  }

  @Test
  public void sample_78() {
    Observable<Tweet> tweets = Observable.range(0, 1000)
        .map(i -> new Tweet(String.valueOf(i)))
        .subscribeOn(Schedulers.newThread()); //...

    Disposable subscription = tweets.subscribe(this::print);

    //...
    Sleeper.sleep(Duration.ofMillis(10));
    subscription.dispose();
  }

  @Test
  public void sample_91() {
    Flowable<Tweet> tweets = Flowable.range(0, 1000)
        .map(i -> new Tweet(i == 500 ? "Java" : String.valueOf(i)));

    ResourceSubscriber<Tweet> subscriber = new ResourceSubscriber<Tweet>() {
      @Override
      public void onNext(Tweet tweet) {
        print(tweet);
        if (tweet.getText().contains("Java")) {
          dispose();
        }
      }

      @Override
      public void onComplete() {
      }

      @Override
      public void onError(Throwable e) {
        e.printStackTrace();
      }
    };
    tweets.subscribe(subscriber);
  }

  private static void log(Object msg) {
    System.out.println(
        Thread.currentThread().getName() +
            ": " + msg);
  }

  @Test
  public void sample_117() {
    log("Before");
    Observable
        .range(5, 3)
        .subscribe(Chapter2Test::log);
    log("After");
  }

  @Test
  public void sample_135() {
    Observable<Integer> ints = Observable
//        .create(new ObservableOnSubscribe<Integer>() {})
        .create(subscriber -> {
          log("Create");
          subscriber.onNext(5);
          subscriber.onNext(6);
          subscriber.onNext(7);
          subscriber.onComplete();
          log("Completed");
        });
    log("Starting");
    ints.subscribe(i -> log("Element: " + i));
    log("Exit");
  }

  @Test
  public void sample_136() {
    Observable<Integer> just = just(5);
    log("Starting");
    just.subscribe(i -> log("Element: " + i));
    log("Exit");

    just(6).subscribe(this::print);
  }

  private static <T> Observable<T> just(T x) {
    return Observable.create(subscriber -> {
          subscriber.onNext(x);
          subscriber.onComplete();
        }
    );
  }

  @Test
  public void sample_162() {
    Observable<Integer> ints =
        Observable.create(subscriber -> {
              log("Create");
              subscriber.onNext(42);
              subscriber.onComplete();
            }
        );
    log("Starting");
    ints.subscribe(i -> log("Element A: " + i));
    ints.subscribe(i -> log("Element B: " + i));
    log("Exit");
  }

  @Test
  public void sample_177() {
    Observable<Integer> ints =
        Observable.<Integer>create(subscriber -> {
              //...
              log("Create");
              subscriber.onNext(42);
              subscriber.onComplete();
            }
        ).cache();
    log("Starting");
    ints.subscribe(i -> log("Element A: " + i));
    ints.subscribe(i -> log("Element B: " + i));
    log("Exit");
  }

  @Ignore
  @Test
  public void sample_187() {
    //BROKEN! Don't do this
    Observable<BigInteger> naturalNumbers = Observable.create(
        subscriber -> {
          BigInteger i = ZERO;
          while (true) {  //don't do this!
            subscriber.onNext(i);
            i = i.add(ONE);
          }
        });
    naturalNumbers.subscribe(Chapter2Test::log);
  }

  private Observable<BigInteger> naturalNumbers() {
    return Observable.create(
        subscriber -> {
          Runnable r = () -> {
            BigInteger i = ZERO;
            while (!subscriber.isDisposed()) {
              subscriber.onNext(i);
              i = i.add(ONE);
            }
          };
          new Thread(r).start();
        });
  }

  @Test
  public void sample_221() {
    final Observable<BigInteger> naturalNumbers = naturalNumbers();
    Disposable subscription = naturalNumbers.subscribe(this::print);

    //after some time...
    Sleeper.sleep(Duration.ofMillis(10));

    subscription.dispose();
  }

  private static <T> Observable<T> delayed(T x) {
    return Observable.create(
        subscriber -> {
          Runnable r = () -> {
            sleep(10, SECONDS);
            if (!subscriber.isDisposed()) {
              subscriber.onNext(x);
              subscriber.onComplete();
            }
          };
          new Thread(r).start();
        });
  }

  private static void sleep(int timeout, TimeUnit unit) {
    try {
      unit.sleep(timeout);
    } catch (InterruptedException ignored) {
      //intentionally ignored
      log.warn("InterruptedException during sleeping time");
    }
  }


  @Test
  public void sample_222() {
    Observable<Tweet> delayed = delayed(new Tweet("Hi"));
    Disposable subscription = delayed.subscribe(this::print);

    //after some time...
    Sleeper.sleep(Duration.ofSeconds(2));

    subscription.dispose();
  }

  private static <T> Observable<T> delayed2(T x) {
    return Observable.create(
        subscriber -> {
          Runnable r = () -> {
            sleep(9, SECONDS);
            if (!subscriber.isDisposed()) {
              subscriber.onNext(x);
              subscriber.onComplete();
            }
          };
          final Thread thread = new Thread(r);
          thread.start();
          subscriber.setCancellable(thread::interrupt);
        });
  }

  @Test
  public void sample_223() {
    Observable<Tweet> delayed = delayed2(new Tweet("Hi2"));
    Disposable subscription = delayed.subscribe(this::print);

    //after some time...
    Sleeper.sleep(Duration.ofSeconds(2));

    subscription.dispose();
  }

  private Observable<Data> loadAll(Collection<Integer> ids) {
    return Observable.create(subscriber -> {
      ExecutorService pool = Executors.newFixedThreadPool(10);
      AtomicInteger countDown = new AtomicInteger(ids.size());
      //DANGER, violates Rx contract. Don't do this!
      ids.forEach(id -> pool.submit(() -> {
        final Data data = load(id);
        subscriber.onNext(data);
        if (countDown.decrementAndGet() == 0) {
          pool.shutdownNow();
          subscriber.onComplete();
        }
      }));
    });
  }

  private Data load(Integer id) {
    return new Data();
  }

  private Observable<Data> rxLoad(int id) {
    return Observable.create(subscriber -> {
      try {
        subscriber.onNext(load(id));
        subscriber.onComplete();
      } catch (Exception e) {
        subscriber.onError(e);
      }
    });
  }

  private Observable<Data> rxLoad2(int id) {
    return Observable.fromCallable(() ->
        load(id));
  }

  @Test
  public void sample_303() {
    Observable<Data> dataObservable = rxLoad(new Random().nextInt());
    Observable<Data> dataObservable2 = rxLoad2(new Random().nextInt());
    Observable<Data> dataObservable3 = loadAll(Arrays.asList(1, 2, 3));
    dataObservable
        .mergeWith(dataObservable2)
        .mergeWith(dataObservable3)
        .subscribe(Chapter2Test::log);
    Sleeper.sleep(Duration.ofSeconds(2));
  }

  @Test
  public void sample_304() {
    Observable
        .timer(1, SECONDS)
        .subscribe(Chapter2Test::log);
    Sleeper.sleep(Duration.ofSeconds(2));
  }

  @Test
  public void sample_311() {
    Observable
        .interval(1_000_000 / 60, MICROSECONDS)
        .subscribe(Chapter2Test::log);
    Sleeper.sleep(Duration.ofSeconds(2));
  }

  private Observable<Tweet> createTestObservable() {
//    return Observable.empty();
    return Observable.fromArray(
        new Tweet("A"),
        new Tweet("B"),
        new Tweet("C")
    );
  }

  private <T> void print(T event) {
    log.info("Got: {}", event);
  }
}

