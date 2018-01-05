package com.infotarget.rx.java;

import io.reactivex.Observable;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RxJavaSimpleTest {

    private Logger log = LoggerFactory.getLogger(RxJavaSimpleTest.class);

    @Test
    public void simpleObservable() {
        Observable<String> just = Observable.just("42");
        just.subscribe(this::print);
    }

    private <T> void print(T event) {
        log.info("Got: {}", event);
    }
}
