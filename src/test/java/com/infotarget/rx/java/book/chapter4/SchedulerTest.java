package com.infotarget.rx.java.book.chapter4;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import io.reactivex.Observable;
import io.reactivex.Scheduler;
import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.Ignore;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadFactory;

import static java.util.concurrent.Executors.newFixedThreadPool;
import static java.util.concurrent.TimeUnit.SECONDS;

@Ignore
public class SchedulerTest {

  private final long start = System.currentTimeMillis();
  private final RxGroceries rxGroceries = new RxGroceries();
  private ExecutorService poolA = newFixedThreadPool(10, threadFactory("Sched-A-%d"));
  private Scheduler schedulerA = Schedulers.from(poolA);
  private ExecutorService poolB = newFixedThreadPool(10, threadFactory("Sched-B-%d"));
  private Scheduler schedulerB = Schedulers.from(poolB);
  private ExecutorService poolC = newFixedThreadPool(10, threadFactory("Sched-C-%d"));
  private Scheduler schedulerC = Schedulers.from(poolC);

  private void log(Object label) {
    System.out.println(
        System.currentTimeMillis() - start + "\t| " +
            Thread.currentThread().getName() + "\t| " +
            label);
  }

  private ThreadFactory threadFactory(String pattern) {
    return new ThreadFactoryBuilder()
        .setNameFormat(pattern)
        .build();
  }

  private Observable<String> simple() {
    return Observable.create(subscriber -> {
      log("Subscribed");
      subscriber.onNext("A");
      subscriber.onNext("B");
      subscriber.onComplete();
    });
  }

  @Test
  public void sample_215() {
    log("Starting");
    final Observable<String> obs = simple();
    log("Created");
    obs
        .subscribeOn(schedulerA)
        .subscribe(
            x -> log("Got " + x),
            Throwable::printStackTrace,
            () -> log("Completed")
        );
    log("Exiting");
  }

  @Test
  public void sample_33() {
    //Don't do this
    Observable<String> obs = Observable.create(subscriber -> {
      log("Subscribed");
      Runnable code = () -> {
        subscriber.onNext("A");
        subscriber.onNext("B");
        subscriber.onComplete();
      };
      new Thread(code, "Async").start();
    });
  }

  @Test
  public void sample_77() {
    log("Starting");
    Observable<String> obs = simple();
    log("Created");
    obs
        .subscribeOn(schedulerA)
        //many other operators
        .subscribeOn(schedulerB)
        .subscribe(
            x -> log("Got " + x),
            Throwable::printStackTrace,
            () -> log("Completed")
        );
    log("Exiting");
  }

  @Test
  public void sample_103() {
    log("Starting");
    final Observable<String> obs = simple();
    log("Created");
    obs
        .doOnNext(this::log)
        .map(x -> x + '1')
        .doOnNext(this::log)
        .map(x -> x + '2')
        .subscribeOn(schedulerA)
        .doOnNext(this::log)
        .subscribe(
            x -> log("Got " + x),
            Throwable::printStackTrace,
            () -> log("Completed")
        );
    log("Exiting");
  }

  @Test
  public void sample_122() {
    Single<BigDecimal> totalPrice = Observable
        .just("bread", "butter", "milk", "tomato", "cheese")
        .subscribeOn(schedulerA)  //BROKEN!!!
        .map(prod -> rxGroceries.doPurchase(prod, 1))
        .reduce(BigDecimal::add)
        .toSingle();
  }

  @Test
  public void sample_135() {
    final Single<BigDecimal> totalPrice = Observable
        .just("bread", "butter", "milk", "tomato", "cheese")
        .subscribeOn(schedulerA)
        .flatMap(prod -> rxGroceries.purchase(prod, 1))
        .reduce(BigDecimal::add)
        .toSingle();
  }

  @Test
  public void sample_145() {
    Single<BigDecimal> totalPrice = Observable
        .just("bread", "butter", "milk", "tomato", "cheese")
        .flatMap(prod ->
            rxGroceries
                .purchase(prod, 1)
                .subscribeOn(schedulerA))
        .reduce(BigDecimal::add)
        .toSingle();
  }

  @Test
  public void sample_157() {
    Single<BigDecimal> totalPrice = Observable
        .just("bread", "butter", "egg", "milk", "tomato",
            "cheese", "tomato", "egg", "egg")
        .groupBy(prod -> prod)
        .flatMap(grouped -> grouped
            .count()
            .toObservable()
            .map(quantity -> {
              String productName = grouped.getKey();
              return Pair.of(productName, quantity);
            }))
        .flatMap(order -> rxGroceries
            .purchase(order.getKey(), order.getValue())
            .subscribeOn(schedulerA))
        .reduce(BigDecimal::add)
        .toSingle();
  }

  @Test
  public void sample_177() {
    log("Starting");
    final Observable<String> obs = simple();
    log("Created");
    obs
        .doOnNext(x -> log("Found 1: " + x))
        .observeOn(schedulerA)
        .doOnNext(x -> log("Found 2: " + x))
        .subscribe(
            x -> log("Got 1: " + x),
            Throwable::printStackTrace,
            () -> log("Completed")
        );
    log("Exiting");
  }

  @Test
  public void sample_194() {
    log("Starting");
    final Observable<String> obs = simple();
    log("Created");
    obs
        .doOnNext(x -> log("Found 1: " + x))
        .observeOn(schedulerB)
        .doOnNext(x -> log("Found 2: " + x))
        .observeOn(schedulerC)
        .doOnNext(x -> log("Found 3: " + x))
        .subscribeOn(schedulerA)
        .subscribe(
            x -> log("Got 1: " + x),
            Throwable::printStackTrace,
            () -> log("Completed")
        );
    log("Exiting");
  }

  @Test
  public void sample_214() {
    log("Starting");
    Observable<String> obs = Observable.create(subscriber -> {
      log("Subscribed");
      subscriber.onNext("A");
      subscriber.onNext("B");
      subscriber.onNext("C");
      subscriber.onNext("D");
      subscriber.onComplete();
    });
    log("Created");
    obs
        .subscribeOn(schedulerA)
        .flatMap(record -> store(record).subscribeOn(schedulerB))
        .observeOn(schedulerC)
        .subscribe(
            x -> log("Got: " + x),
            Throwable::printStackTrace,
            () -> log("Completed")
        );
    log("Exiting");
  }

  private Observable<UUID> store(String s) {
    return Observable.create(subscriber -> {
      log("Storing " + s);
      //hard work
      subscriber.onNext(UUID.randomUUID());
      subscriber.onComplete();
    });
  }

  @Test
  public void sample_248() {
    Observable
        .just('A', 'B')
        .delay(1, SECONDS, schedulerA)
        .subscribe(this::log);
  }
}
