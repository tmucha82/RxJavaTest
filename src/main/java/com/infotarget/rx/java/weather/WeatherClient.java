package com.infotarget.rx.java.weather;

import com.infotarget.rx.java.sleeper.Sleeper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;

final public class WeatherClient {

    private final Logger log = LoggerFactory.getLogger(WeatherClient.class);

    public Weather fetch(final String city) {
        log.info("Loading for {}", city);
        Sleeper.sleep(Duration.ofMillis(900));
        return new Weather();
    }
}
