package com.infotarget.rx.java.person;

import com.infotarget.rx.java.sleeper.Sleeper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;

final public class PersonDao {

  private final Logger log = LoggerFactory.getLogger(PersonDao.class);

  public Person findById(final int id) {
    log.info("Getting person with if {}", id);
    Sleeper.sleep(Duration.ofSeconds(1));
    return new Person(id);
  }

}
