package com.infotarget.rx.java.wjug.weather;

import com.infotarget.rx.java.sleeper.Sleeper;
import io.reactivex.Observable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;

public class WeatherClient {
  private static final Logger log = LoggerFactory.getLogger(WeatherClient.class);

  public Weather fetch(String city) {
    log.info("Loading for {}", city);
    Sleeper.sleep(Duration.ofMillis(900));
    //HTTP, HTTP, HTTP
    return new Weather();
  }

  public Observable<Weather> rxFetch(String city) {
    return Observable.fromCallable(
        () ->
            fetch(city));
  }
}
