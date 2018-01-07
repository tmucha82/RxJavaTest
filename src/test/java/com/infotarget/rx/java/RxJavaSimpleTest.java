package com.infotarget.rx.java;

import io.reactivex.Observable;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
    public void simpleRangeObservable() {
        Observable
                .range(1, 10)
                .subscribe(this::print);
    }

    @Test
    public void simpleIntervalObservable() throws InterruptedException {
        Observable
                .interval(1, TimeUnit.SECONDS)
                .subscribe(this::print);

    }

    private <T> void print(T event) {
        log.info("Got: {}", event);
    }
}
