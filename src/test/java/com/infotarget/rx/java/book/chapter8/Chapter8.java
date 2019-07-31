package com.infotarget.rx.java.book.chapter8;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.Single;
import org.junit.Ignore;
import org.junit.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static java.util.concurrent.TimeUnit.MICROSECONDS;
import static java.util.stream.Collectors.toList;


@Ignore
public class Chapter8 {

  @Test
  public void sample_9() throws Exception {
    final ApiFactory api = new ApiFactory();
    MeetupApi meetup = api.meetUp();
    GeoNames geoNames = api.geoNames();

    double warsawLat = 52.229841;
    double warsawLon = 21.011736;
    Observable<Cities> cities = meetup.listCities(warsawLat, warsawLon);
    Observable<City> cityObs = cities.concatMapIterable(Cities::getResults);

    Observable<String> map = cityObs
        .filter(city -> city.distanceTo(warsawLat, warsawLon) < 50)
        .map(City::getCity);

    Single<Long> totalPopulation = meetup
        .listCities(warsawLat, warsawLon)
        .concatMapIterable(Cities::getResults)
        .filter(city -> city.distanceTo(warsawLat, warsawLon) < 50)
        .map(City::getCity)
        .flatMap(geoNames::populationOf)
        .reduce(0L, (x, y) -> x + y);
  }

  private Observable<BigDecimal> dailyPrice(LocalDateTime date) {
    return Observable.just(BigDecimal.TEN);
  }

  @Test
  public void sample_148() throws Exception {
    List<Person> people = Collections.emptyList(); //...

    List<String> sorted = people
        .parallelStream()
        .filter(p -> p.getAge() >= 18)
        .map(Person::getFirstName)
        .sorted(Comparator.comparing(String::toLowerCase))
        .collect(toList());

    //DON'T DO THIS
    people
        .parallelStream()
        .forEach(this::publishOverJms);
  }

  private void publishOverJms(Person person) {
    //...
  }

  @Test
  public void sample_170() throws Exception {
    Observable
        .range(0, Integer.MAX_VALUE)
        .map(Picture::new)
        .distinct()
        .distinct(Picture::getTag)
        .sample(1, TimeUnit.SECONDS)
        .subscribe(System.out::println);
  }

  @Test
  public void sample_182() throws Exception {
    Observable
        .range(0, Integer.MAX_VALUE)
        .map(Picture::new)
        .window(1, TimeUnit.SECONDS)
        .flatMap(pictureObservable -> pictureObservable.count().toObservable())
        .subscribe(System.out::println);
  }

  @Test
  public void sample_192() throws Exception {
    Observable
        .range(0, Integer.MAX_VALUE)
        .map(Picture::new)
        .window(10, TimeUnit.SECONDS)
        .flatMap(Observable::distinct);
  }

  @Test
  public void sample_201() throws Exception {
    Observable<Incident> incidents = Observable.empty(); //...

    Observable<Boolean> danger = incidents
        .buffer(1, TimeUnit.SECONDS)
        .map((List<Incident> oneSecond) -> oneSecond
            .stream()
            .filter(Incident::isHIghPriority)
            .count() > 5);
  }

  @Test
  public void sample_213() throws Exception {
    Observable<Picture> fast = Observable
        .interval(10, MICROSECONDS)
        .map(Picture::new);
    Observable<Picture> slow = Observable
        .interval(11, MICROSECONDS)
        .map(Picture::new);

    Observable
        .zip(fast, slow, (f, s) -> f + " : " + s);

    Flowable
        .zip(
            fast.toFlowable(BackpressureStrategy.DROP),
            slow.toFlowable(BackpressureStrategy.DROP),
            (f, s) -> f + " : " + s);
  }

}
