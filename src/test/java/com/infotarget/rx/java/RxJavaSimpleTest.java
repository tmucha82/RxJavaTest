package com.infotarget.rx.java;

import com.infotarget.rx.java.person.Person;
import com.infotarget.rx.java.person.PersonDao;
import com.infotarget.rx.java.sleeper.Sleeper;
import com.infotarget.rx.java.weather.Weather;
import com.infotarget.rx.java.weather.WeatherClient;
import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;
import java.util.Observer;
import org.junit.Test;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

public class RxJavaSimpleTest {

    private Logger log = LoggerFactory.getLogger(RxJavaSimpleTest.class);

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

    private final WeatherClient weatherClient = new WeatherClient();
    private final PersonDao personDao = new PersonDao();

    @Test
    public void fetchTimeConsumingMethod() {
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
    public void test() {
    }

    private <T> void print(T event) {
        log.info("Got: {}", event);
    }
}
