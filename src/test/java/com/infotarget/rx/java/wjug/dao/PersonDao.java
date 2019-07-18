package com.infotarget.rx.java.wjug.dao;

import com.infotarget.rx.java.sleeper.Sleeper;
import io.reactivex.Observable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;

public class PersonDao {
  private static final Logger log = LoggerFactory.getLogger(PersonDao.class);

  public Person findById(int id) {
    //SQL, SQL, SQL
    log.info("Loading {}", id);
    Sleeper.sleep(Duration.ofMillis(1000));
    return new Person();
  }

  public Observable<Person> rxFindById(int id) {
//		return Observable.just(findById(id));
    return Observable.fromCallable(() ->
        findById(id)
    );
  }
}
