package com.infotarget.rx.java;

import com.infotarget.rx.java.person.Person;
import com.infotarget.rx.java.person.PersonDao;
import com.infotarget.rx.java.sleeper.Sleeper;
import com.infotarget.rx.java.weather.Weather;
import com.infotarget.rx.java.weather.WeatherClient;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

public class RxJavaSimpleTest {

  private final static Logger log = LoggerFactory.getLogger(RxJavaSimpleTest.class);

  private final WeatherClient weatherClient = new WeatherClient();
  private final PersonDao personDao = new PersonDao();

  @Test
  public void simpleJustObservable() {
    Observable
        .just("42")
        .subscribe(this::print);
  }

  @Test
  public void simpleJustWithMoreResultsObservable() {
    Observable
        .just("42", "43", "44")
        .subscribe(this::print);
  }

  @Test
  public void simpleRangeObservable() {
    Observable
        .range(1, 10)
        .subscribe(this::print);
  }

  @Test
  public void simpleIntervalObservable() {
    Observable
        .interval(1, TimeUnit.SECONDS)
        .subscribe(this::print);
  }

  @Test
  public void fetchTimeConsumingMethodWithoutTimeout() {
    Observable
        .fromCallable(() -> weatherClient.fetch("Warsaw"))
        .timeout(1, TimeUnit.SECONDS)
        .subscribe(this::print);
  }

  @Test
  public void fetchTimeConsumingMethodWithTimeout() {
    Observable
        .fromCallable(() -> weatherClient.fetch("Warsaw"))
        .timeout(700, TimeUnit.MILLISECONDS)
        .subscribe(this::print);
  }

  @Test
  public void mergeWithBothWeathers() {
    Observable<Weather> warsaw = Observable.fromCallable(() -> weatherClient.fetch("Warsaw"));
    Observable<Weather> radom = Observable.fromCallable(() -> weatherClient.fetch("Radom"));

    warsaw.mergeWith(radom).subscribe(this::print);
  }

  @Test
  public void parallelInvocation() {
    Observable<Person> john = Observable
        .fromCallable(() -> personDao.findById(42))
        .subscribeOn(Schedulers.io());
    Observable<Weather> warsaw = Observable
        .fromCallable(() -> weatherClient.fetch("Warsaw"))
        .subscribeOn(Schedulers.io());

    warsaw
        .zipWith(john, (weather, person) -> weather + ":" + person)
        .subscribe(this::print);

    Sleeper.sleep(Duration.ofSeconds(2));
  }

  @Test
  public void testObserverInterface() {
    Observable.range(3, 10)
        .subscribe(new Observer<Integer>() {
          @Override
          public void onSubscribe(Disposable d) {
            print("onSubscribe");
          }

          @Override
          public void onNext(Integer integer) {
            print("onNext = " + integer);
          }

          @Override
          public void onError(Throwable e) {
            print("onError = " + e);
          }

          @Override
          public void onComplete() {
            print("onComplete");
          }
        });
  }

  private <T> void print(T event) {
    log.info("Got: {}", event);
  }
}
