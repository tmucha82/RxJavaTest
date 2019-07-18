package com.infotarget.rx.java.book.chapter2;

import io.reactivex.Observable;
import io.reactivex.observables.ConnectableObservable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;
import twitter4j.Status;

@Configuration
class Config implements ApplicationListener<ContextRefreshedEvent> {

  private static final Logger log = LoggerFactory.getLogger(Config.class);

  private final ConnectableObservable<Status> observable =
      Observable.<Status>create(subscriber -> {
        log.info("Starting");
        //...
      }).publish();

  @Bean
  public Observable<Status> observable() {
    return observable;
  }

  @Override
  public void onApplicationEvent(ContextRefreshedEvent event) {
    log.info("Connecting");
    observable.connect();
  }
}

@Component
class Foo {

  private static final Logger log = LoggerFactory.getLogger(Foo.class);

  @Autowired
  public Foo(Observable<Status> tweets) {
    tweets.subscribe(status -> {
      log.info(status.getText());
    });
    log.info("Subscribed");
  }
}

@Component
class Bar {

  private static final Logger log = LoggerFactory.getLogger(Bar.class);

  @Autowired
  public Bar(Observable<Status> tweets) {
    tweets.subscribe(status -> {
      log.info(status.getText());
    });
    log.info("Subscribed");
  }
}