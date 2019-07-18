package com.infotarget.rx.java.book.chapter4;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.infotarget.rx.java.sleeper.Sleeper;
import io.reactivex.Observable;
import io.reactivex.Scheduler;
import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;
import org.apache.commons.lang3.RandomUtils;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.*;

@Ignore
public class Chapter4 {

  private static final Logger log = LoggerFactory.getLogger(Chapter4.class);

  private final PersonDao personDao = new PersonDao();
  private final long start = System.currentTimeMillis();
  private int orderBookLength;

  @Test
  public void sample_9() {
    List<Person> people = personDao.listPeople();
    log(people);
  }

  @Test
  public void sample_20() {
    Observable<Person> peopleStream = personDao.listPeople2();
    peopleStream.subscribe(this::log);

    Single<List<Person>> peopleList = peopleStream.toList();
    List<Person> people = peopleList.blockingGet();

    log(people);
  }

  @Test
  public void sample_21() {
    Observable<Person> peopleStream = personDao.listPeople3();
    peopleStream.subscribe(this::log);

    Single<List<Person>> peopleList = peopleStream.toList();
    List<Person> people = peopleList.blockingGet();

    log(people);
  }

  @Test
  public void sample_34() {
    List<Person> people = personDao
        .listPeople2()
        .toList()
        .blockingGet();
    log(people);
  }

  void bestBookFor(Person person) {
    Book book;
    try {
      book = recommend(person);
    } catch (Exception e) {
      book = bestSeller();
    }
    display(book.getTitle());
  }

  private Book bestSeller() {
    return new Book("bestSeller");
  }

  private Book recommend(Person person) {
    return new Book("recommend");
  }

  private void display(String title) {
    log.info("Book with title = {}", title);
  }

  void bestBookFor2(Person person) {
    Observable<Book> recommended = recommend2(person);
    Observable<Book> bestSeller = bestSeller2();
    Observable<Book> book = recommended.onErrorResumeNext(bestSeller);
    Observable<String> title = book.map(Book::getTitle);
    title.subscribe(this::display);
  }

  void bestBookFor3(Person person) {
    recommend2(person)
        .onErrorResumeNext(bestSeller2())
        .map(Book::getTitle)
        .subscribe(this::display);
  }

  private Observable<Book> bestSeller2() {
    return Observable.fromCallable(() -> new Book("bestSeller2"));
  }

  private Observable<Book> recommend2(Person person) {
    return Observable.fromCallable(() -> new Book("recommend2"));
  }

  @Test
  public void sample_11() {
    bestBookFor(new Person("sample_11"));
  }

  @Test
  public void sample_89() {
    Observable
        .interval(10, TimeUnit.MILLISECONDS)
        .map(x -> getOrderBookLength())
        .distinctUntilChanged();
  }

  private int getOrderBookLength() {
    return RandomUtils.nextInt(5, 10);
  }

  Observable<Item> observeNewItems() {
    return Observable
        .interval(1, TimeUnit.SECONDS)
        .flatMapIterable(x -> query())
        .distinct();
  }

  private List<Item> query() {
    //take snapshot of file system directory
    //or database table
    return Collections.emptyList();
  }

  @Test
  public void sample_118() {
    final ThreadFactory threadFactory = new ThreadFactoryBuilder()
        .setNameFormat("MyPool-%d")
        .build();
    final Executor executor = new ThreadPoolExecutor(
        10,  //corePoolSize
        10,  //maximumPoolSize
        0L, TimeUnit.MILLISECONDS, //keepAliveTime, unit
        new LinkedBlockingQueue<>(1000),  //workQueue
        threadFactory
    );
    final Scheduler scheduler = Schedulers.from(executor);
    Observable
        .just(1, 2, 3)
        .subscribeOn(scheduler)
        .subscribe(this::log);
  }

  @Test
  public void sample_136() {
    ExecutorService executor = Executors.newFixedThreadPool(10);
    Observable
        .just(1, 2, 3)
        .subscribeOn(Schedulers.from(executor))
        .subscribe(this::log);
  }

  private void log(Object label) {
    System.out.println(
        System.currentTimeMillis() - start + "\t| " +
            Thread.currentThread().getName() + "\t| " +
            label);
  }

  @Test
  public void sample_141() {
    Scheduler scheduler = Schedulers.trampoline();
    Scheduler.Worker worker = scheduler.createWorker();

    log("Main start");
    worker.schedule(() -> {
      log(" Outer start");
      sleepOneSecond();
      worker.schedule(() -> {
        log("  Inner start");
        sleepOneSecond();
        log("  Inner end");
      });
      log(" Outer end");
    });
    log("Main end");
    worker.dispose();
  }

  @Test
  public void sample_175() {
    Scheduler scheduler = Schedulers.trampoline();
    Scheduler.Worker worker = scheduler.createWorker();

    log("Main start");
    worker.schedule(() -> {
      log(" Outer start");
      sleepOneSecond();
      worker.schedule(() -> {
        log("  Middle start");
        sleepOneSecond();
        worker.schedule(() -> {
          log("   Inner start");
          sleepOneSecond();
          log("   Inner end");
        });
        log("  Middle end");
      });
      log(" Outer end");
    });
    log("Main end");
  }

  private void sleepOneSecond() {
    Sleeper.sleep(Duration.ofSeconds(1));
  }

}
