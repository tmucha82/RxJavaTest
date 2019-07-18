package com.infotarget.rx.java.wjug;

import com.infotarget.rx.java.sleeper.Sleeper;
import com.infotarget.rx.java.wjug.cache.CacheServer;
import com.infotarget.rx.java.wjug.dao.Person;
import com.infotarget.rx.java.wjug.dao.PersonDao;
import com.infotarget.rx.java.wjug.weather.Weather;
import com.infotarget.rx.java.wjug.weather.WeatherClient;
import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.math.BigDecimal;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.*;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

public class WjugTest {

  private static final Logger log = LoggerFactory.getLogger(WjugTest.class);
  public static final BigDecimal FALLBACK = BigDecimal.ONE.negate();

  @Test
  public void wjug_9() throws Exception {
    //.just()
    final CompletableFuture<String> fut = CompletableFuture.completedFuture("42");

    final String s42 = fut.get();  //42

    final CompletableFuture<Integer> futInt = fut
        .thenApply((String s) -> s.length() + 1);

    final CompletableFuture<Double> futDbl = futInt
        .thenApply((Integer x) -> x * 2.0);

    final CompletableFuture<Double> fd = fut
        .thenApply(s -> s.length() + 1)
        .thenApply(x -> x * 2.0);
  }

  @Test
  public void wjug_31() throws Exception {
    final CompletableFuture<Object> never = new CompletableFuture<>();
  }

  void print(Object obj) {
    log.info("Got: {}", obj);
  }

  @Test
  public void wjug_36() throws Exception {
    final Observable<String> obs = Observable.just("42");

    obs.subscribe(this::print);
  }

  @Test
  public void wjug_37() throws Exception {
    final Observable<String> obs = Observable.just("42", "43", "44");

    obs.subscribe(this::print);
  }

  WeatherClient client = new WeatherClient();

  @Test
  public void wjug_59() throws Exception {
    print(client.fetch("Warsaw"));
  }

  @Test
  public void wjug_67() throws Exception {
    final Observable<Weather> warsaw = client.rxFetch("Warsaw");

    warsaw.subscribe((Weather w) -> this.print(w));
  }

  @Test
  public void wjug_75() throws Exception {
    final Observable<Weather> warsaw = client.rxFetch("Warsaw");

    final Observable<Weather> withTimeout = warsaw
        .timeout(1, TimeUnit.SECONDS);

    withTimeout.subscribe(this::print);


  }

  @Test
  public void wjug_76() throws Exception {
    final Observable<Weather> warsaw = client.rxFetch("Warsaw");

    final Observable<Weather> withTimeout = warsaw
        .timeout(800, MILLISECONDS);

    withTimeout.subscribe(this::print);
  }

  @Test
  public void wjug_98() throws Exception {
    Observable<Weather> weather1 = client.rxFetch("Warsaw");
    Observable<Weather> weather2 = client.rxFetch("Radom");

    final Observable<Weather> pogody = weather1.mergeWith(weather2);
    //zwróci 2 obiekty
  }


  @Test
  public void wjug_99() throws Exception {
    Observable<Weather> weather1 = client.rxFetch("Warsaw");
    Observable<Weather> weather2 = client.rxFetch("Radom");

    weather1.subscribe(this::print);
    //900ms później...


    final Observable<Weather> pogody = weather1.mergeWith(weather2);
    //zwróci 2 obiekty
  }

  private final PersonDao dao = new PersonDao();

  //RxNetty
  @Test
  public void wjug_121() throws Exception {
    final Observable<Weather> lodz = client
        .rxFetch("Lodz")
        .subscribeOn(Schedulers.io());  //nie używaj io()
    final Observable<Person> person = dao
        .rxFindById(42)
        .subscribeOn(Schedulers.io());

    final Observable<String> str =
        lodz.zipWith(person, (Weather w, Person p) -> w + " : " + p);


    str.subscribe(this::print);

    TimeUnit.SECONDS.sleep(2);
  }

  //java.util.concurrent.Flow
  @Test
  public void wjug_144() throws Exception {
    final Observable<String> strings = Observable
        .just("A", "B", "C")
        .repeat(1000);
    final Observable<Integer> numbers = Observable
        .range(1, 10)
        .map(x -> x * 10);

//		strings.zipWith(numbers)
    final Observable<String> s2 = Observable.zip(
        strings,
        numbers,
        (s, n) -> s + n
    );

    s2.subscribe(this::print);
  }

  @Test
  public void wjug_167() throws Exception {
    Schedulers.io();
    Schedulers.computation();
    Schedulers.from(Executors.newFixedThreadPool(10));
    new ThreadPoolExecutor(10, 10,
        0L, MILLISECONDS,
        new LinkedBlockingQueue<>());
  }

  @Test
  public void wjug_175() throws Exception {
    CacheServer eu = new CacheServer();
    CacheServer us = new CacheServer();

    Observable<String> reu = eu.rxFindBy(42);
    Observable<String> rus = us.rxFindBy(42);

    //mergeWith

    Observable
        .merge(reu.timeout(1, TimeUnit.SECONDS), rus)
        .first("")
        .subscribe(this::print);

    TimeUnit.SECONDS.sleep(2);
  }

  @Test
  public void wjug_194() throws Exception {
    Observable
        .interval(1, TimeUnit.SECONDS)
        .map(x -> x * Math.PI)
        .subscribe(this::print);
    TimeUnit.SECONDS.sleep(5);
  }

  private File dir = new File("./src/main/resources/wjug");

  @Test
  public void wjug_204() throws Exception {
    childrenOf(dir)
        .subscribe(this::print);
  }

  @Test
  public void wjug_215() throws Exception {
    Observable
        .empty()
        .single("")
        .subscribe();
  }

  @Test
  public void wjug_216() throws Exception {
    Observable
        .just(1, 2)
        .single(0)
        .subscribe();
  }

  @Test
  public void wjug_231() throws Exception {
    Observable
        .interval(100, MILLISECONDS)
        .single(0L)
        .subscribe();

    TimeUnit.SECONDS.sleep(1);
  }

  @Test
  public void wjug_214() throws Exception {
    Observable
        .interval(1, TimeUnit.SECONDS)
        .map(x -> childrenOf2(dir))
        .subscribe(this::print);

    Sleeper.sleep(Duration.ofSeconds(10));
  }

  @Test
  public void wjug_250() throws Exception {
    Observable
        .interval(1, TimeUnit.SECONDS)
        //poniższe dwie linijki równoważne
//				.concatMapIterable(x -> childrenOf2(dir))
        .flatMap(x -> childrenOf(dir))
        .distinct()  //unieść brew - wyciek pamięci
        //RxJava 2 - distinct(collectionFactory)
        //distinctUntilChanged()
        .subscribe(this::print);

    Sleeper.sleep(Duration.ofSeconds(10));
  }

  List<String> childrenOf2(File dir) {
    return childrenOf(dir)
        .toList()       //[a.txt, b.txt, c.txt]
        .blockingGet();   //blokuje wątek klienta
  }

  Observable<String> childrenOf(File dir) {
    final File[] files = dir.listFiles();
    return Observable
        .fromArray(files)
        .map(File::getName);
  }

  @Test
  public void wjug_191() throws Exception {
    final Observable<Object> empty = Observable.empty();
  }

  //MongoDB, RxAndroid, RxNetty, Azure SDK, Couchbase, Camel

  @Test
  public void wjug_312() throws Exception {
    final Observable<BigDecimal> response = verySlowSoapService()
        .timeout(1, TimeUnit.SECONDS)
//				.retry() // w nieskończoność
        .doOnError(ex -> log.warn("Opps " + ex))  //nigdy tak nie róbcie
        .retry(4)
//				.retryWhen()  //ponawiaj z rosnącymi wykładniczo opóźnieniami
        .onErrorReturn(x -> BigDecimal.ONE.negate());


    response
        .blockingSubscribe(this::print);
  }

/*
  @Test
  public void wjug_329() throws Exception {
    final TestScheduler testScheduler = Schedulers.test();
    final Observable<BigDecimal> response = verySlowSoapService()
        .timeout(1, TimeUnit.SECONDS, testScheduler)
        .doOnError(ex -> log.warn("Opps " + ex))  //nigdy tak nie róbcie
        .retry(4)
        .onErrorReturn(x -> FALLBACK);

    //Awaitility
    //java.time.Clock

    final TestSubscriber<BigDecimal> subscriber = new TestSubscriber<>();
    response.subscribe(subscriber);

    subscriber.assertNoErrors();
    subscriber.assertNoValues();

    testScheduler.advanceTimeBy(4_999, MILLISECONDS);
    subscriber.assertNoErrors();
    subscriber.assertNoValues();

    testScheduler.advanceTimeBy(1, MILLISECONDS);
    subscriber.assertNoErrors();
    subscriber.assertValue(FALLBACK);
  }
*/

  private Observable<BigDecimal> verySlowSoapService() {
    return Observable
//				.interval(1, TimeUnit.MINUTES).take(1)
        .timer(1, TimeUnit.MINUTES)
        .map(x -> BigDecimal.ZERO);
  }

}
