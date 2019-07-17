package com.infotarget.rx.java.chapter6;

import com.infotarget.rx.java.sleeper.Sleeper;
import io.reactivex.Observable;
import io.reactivex.observables.ConnectableObservable;
import org.junit.Test;

import java.math.BigDecimal;
import java.time.Duration;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;


public class Debounce {


    private final TradingPlatform tradingPlatform = new TradingPlatform();

    @Test
    public void sample_225() throws Exception {
        Observable<BigDecimal> prices = tradingPlatform.pricesOf("NFLX");
        Observable<BigDecimal> debounced = prices.debounce(100, MILLISECONDS);

        prices
                .debounce(x -> {
                    boolean goodPrice = x.compareTo(BigDecimal.valueOf(150)) > 0;
                    return Observable
                            .empty()
                            .delay(goodPrice ? 10 : 100, MILLISECONDS);
                })
                .subscribe(System.out::println);
        Sleeper.sleep(Duration.ofSeconds(4));
    }

    @Test
    public void sample_242() throws Exception {
        Observable
                .interval(99, MILLISECONDS)
                .debounce(100, MILLISECONDS)
                .subscribe(System.out::println);
        Sleeper.sleep(Duration.ofSeconds(4));
    }

    @Test
    public void sample_249() throws Exception {
        Observable
                .interval(99, MILLISECONDS)
                .debounce(100, MILLISECONDS)
                .timeout(1, SECONDS)
                .subscribe(System.out::println);
        Sleeper.sleep(Duration.ofSeconds(4));
    }

    @Test
    public void sample_48() throws Exception {
        ConnectableObservable<Long> upstream = Observable
                .interval(99, MILLISECONDS)
                .publish();
        upstream
                .debounce(100, MILLISECONDS)
                .timeout(1, SECONDS, upstream.take(1));
        upstream.connect();
    }

    @Test
    public void sample_60() throws Exception {
        final Observable<Long> upstream = Observable.interval(99, MILLISECONDS);

        upstream
                .debounce(100, MILLISECONDS)
                .timeout(1, SECONDS, upstream
                        .take(1)
                        .concatWith(
                                upstream.debounce(100, MILLISECONDS)));
    }

    @Test
    public void sample_72() throws Exception {
        final Observable<Long> upstream = Observable.interval(99, MILLISECONDS);

        upstream
                .debounce(100, MILLISECONDS)
                .timeout(1, SECONDS, upstream
                        .take(1)
                        .concatWith(
                                upstream
                                        .debounce(100, MILLISECONDS)
                                        .timeout(1, SECONDS, upstream)));
    }

    Observable<Long> timedDebounce(Observable<Long> upstream) {
        Observable<Long> onTimeout = upstream
                .take(1)
                .concatWith(Observable.defer(() -> timedDebounce(upstream)));
        return upstream
                .debounce(100, MILLISECONDS)
                .timeout(1, SECONDS, onTimeout);
    }

}