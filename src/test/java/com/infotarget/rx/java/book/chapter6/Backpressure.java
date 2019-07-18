package com.infotarget.rx.java.book.chapter6;

import com.infotarget.rx.java.sleeper.Sleeper;
import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subscribers.ResourceSubscriber;
import org.apache.commons.dbutils.ResultSetIterator;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Duration;

@Ignore
public class Backpressure {

  private static final Logger log = LoggerFactory.getLogger(Backpressure.class);


  private Observable<Dish> dishes() {
    Observable<Dish> dishes = Observable
        .range(1, 1_000_000_000)
        .map(Dish::new);
    return dishes;
  }

  @Test
  public void sample_18() throws Exception {
    Observable
        .range(1, 1_000_000_000)
        .map(Dish::new)
        .subscribe(x -> {
          System.out.println("Washing: " + x);
          sleepMillis(50);
        });
  }

  @Test
  public void sample_32() throws Exception {
    final Observable<Dish> dishes = dishes();

    dishes
        .observeOn(Schedulers.io())
        .subscribe(x -> {
          System.out.println("Washing: " + x);
          sleepMillis(50);
        });

  }

  private void sleepMillis(int millis) {
    Sleeper.sleep(Duration.ofMillis(millis));
  }

  Flowable<Integer> myRange(int from, int count) {
    return Flowable.create(subscriber -> {
      int i = from;
      while (i < from + count) {
        if (!subscriber.isCancelled()) {
          subscriber.onNext(i++);
        } else {
          return;
        }
      }
      subscriber.onComplete();
    }, BackpressureStrategy.BUFFER);
  }

  @Test
  public void sample_65() throws Exception {
    myRange(1, 1_000_000_000)
        .map(Dish::new)
        .observeOn(Schedulers.io())
        .subscribe(x -> {
              System.out.println("Washing: " + x);
              sleepMillis(50);
            },
            Throwable::printStackTrace
        );
  }

  @Test
  public void sample_78() throws Exception {
    Flowable
        .range(1, 10)
        .subscribe(new ResourceSubscriber<Integer>() {

          @Override
          protected void onStart() {
            request(3);
          }

          @Override
          public void onNext(Integer integer) {

          }

          @Override
          public void onError(Throwable e) {

          }

          @Override
          public void onComplete() {

          }
        });
  }

  @Test
  public void sample_94() throws Exception {
    Flowable
        .range(1, 10)
        .subscribe(new ResourceSubscriber<Integer>() {

          {
            {
              request(3);
            }
          }

          @Override
          public void onNext(Integer integer) {

          }

          @Override
          public void onError(Throwable e) {

          }

          @Override
          public void onComplete() {

          }
        });
  }

  @Test
  public void sample_136() throws Exception {
    Flowable
        .range(1, 10)
        .subscribe(new ResourceSubscriber<Integer>() {

          @Override
          public void onStart() {
            request(1);
          }

          @Override
          public void onError(Throwable e) {

          }

          @Override
          public void onComplete() {

          }

          @Override
          public void onNext(Integer integer) {
            request(1);
            log.info("Next {}", integer);
          }

          //onCompleted, onError...
        });
  }

  @Test
  public void sample_173() throws Exception {
    myRange(1, 1_000_000_000)
        .map(Dish::new)
        .onBackpressureBuffer()
        //.onBackpressureBuffer(1000, () -> log.warn("Buffer full"))
        //.onBackpressureDrop(dish -> log.warn("Throw away {}", dish))
        .observeOn(Schedulers.io())
        .subscribe(x -> {
          System.out.println("Washing: " + x);
          sleepMillis(50);
        });
  }

  @Test
  public void sample_189() throws Exception {
    Connection connection = null;
    PreparedStatement statement =
        connection.prepareStatement("SELECT ...");
    statement.setFetchSize(1000);
    ResultSet rs = statement.executeQuery();
    Observable<Object[]> result =
        Observable
            .fromIterable(ResultSetIterator.iterable(rs))
            .doAfterTerminate(() -> {
              try {
                rs.close();
                statement.close();
                connection.close();
              } catch (SQLException e) {
                log.warn("Unable to close", e);
              }
            });
  }

  @Test
  public void sample_213() throws Exception {

    Flowable<Double> onSubscribe = Flowable
        .create(
            emitter -> emitter.onNext(Math.random()),
            BackpressureStrategy.BUFFER);
    Observable<Double> rand = onSubscribe.toObservable();
  }

  @Test
  public void sample_224() throws Exception {
    Flowable<Long> onSubscribe = Flowable.generate(
        () -> 0L,
        (cur, observer) -> {
          observer.onNext(cur);
          return cur + 1;
        }
    );

    Observable<Long> naturals = onSubscribe.toObservable();
  }

  @Test
  public void sample_238() throws Exception {
    Observable<Long> naturals = Observable.create(subscriber -> {
      long cur = 0;
      while (!subscriber.isDisposed()) {
        System.out.println("Produced: " + cur);
        subscriber.onNext(cur++);
      }
    });
  }

  @Test
  public void sample_249() throws Exception {
    ResultSet resultSet = null; //...

    Flowable<Object> onSubscribe = Flowable.generate(
        () -> resultSet,
        (rs, observer) -> {
          try {
            rs.next();
            observer.onNext(toArray(rs));
          } catch (SQLException e) {
            observer.onError(e);
          }
        },
        rs -> {
          try {
            //Also close Statement, Connection, etc.
            rs.close();
          } catch (SQLException e) {
            log.warn("Unable to close", e);
          }
        }
    );
  }

  private Object[] toArray(ResultSet rs) {
    //TODO
    return new Object[]{};
  }

  @Test
  public void sample_284() throws Exception {
    Observable<Integer> source = Observable.range(1, 1_000);

    source.subscribe(this::store);

    source
        .flatMap(this::store)
        .subscribe(uuid -> log.debug("Stored: {}", uuid));

    source
        .flatMap(this::store)
        .buffer(100)
        .subscribe(
            hundredUuids -> log.debug("Stored: {}", hundredUuids));
  }

  Observable<Void> store(int x) {
    return Observable.empty();
  }

}