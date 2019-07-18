package com.infotarget.rx.java.wjug.cache;

import com.infotarget.rx.java.sleeper.Sleeper;
import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;

public class CacheServer {
  private static final Logger log = LoggerFactory.getLogger(CacheServer.class);

  public String findBy(long key) {
    log.info("Loading from Memcached: {}", key);
    Sleeper.sleep(Duration.ofMillis(100));
    return "<data>" + key + "</data>";
  }

  public Observable<String> rxFindBy(long key) {
    return Observable
        .fromCallable(() -> findBy(key))
        .subscribeOn(Schedulers.io());
  }
}
