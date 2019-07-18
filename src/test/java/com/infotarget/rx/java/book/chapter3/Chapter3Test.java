package com.infotarget.rx.java.book.chapter3;

import com.infotarget.rx.java.sleeper.Sleeper;
import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.functions.Function;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import twitter4j.Status;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.DayOfWeek;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.LongAdder;

import static com.infotarget.rx.java.book.chapter3.Sound.DAH;
import static com.infotarget.rx.java.book.chapter3.Sound.DI;
import static io.reactivex.Observable.*;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;

public class Chapter3Test {

  private static final Logger log = LoggerFactory.getLogger(Chapter3Test.class);

  @Test
  public void sample_6() {
    Observable<String> strings = empty();
    Observable<String> filtered = strings.filter(s -> s.startsWith("#"));
  }

  @Test
  public void sample_15() {
    Observable<String> strings = empty();
    Observable<String> comments = strings.filter(s -> s.startsWith("#"));
    Observable<String> instructions = strings.filter(s -> s.startsWith(">"));
    Observable<String> empty = strings.filter(String::isEmpty);
  }

  @Test
  public void sample_26() {
    Observable<Status> tweets = empty();
    Observable<Date> dates = tweets.map(new Function<Status, Date>() {
      @Override
      public Date apply(Status status) throws Exception {
        return status.getCreatedAt();
      }
    });

    Observable<Date> dates2 =
        tweets.map((Status status) -> status.getCreatedAt());

    Observable<Date> dates3 =
        tweets.map((status) -> status.getCreatedAt());

    Observable<Date> dates4 =
        tweets.map(Status::getCreatedAt);
  }

  @Test
  public void sample_49() {
    Observable<Status> tweets = empty();

    Observable<Instant> instants = tweets
        .map(Status::getCreatedAt)
        .map(Date::toInstant);
  }

  @Test
  public void sample_57() {
    just(8, 9, 10)
        .filter(i -> i % 3 > 0)
        .map(i -> "#" + i * 10)
        .filter(s -> s.length() < 4);
  }

  @Test
  public void sample_66() {
    just(8, 9, 10)
        .doOnNext(i -> System.out.println("A: " + i))
        .filter(i -> i % 3 > 0)
        .doOnNext(i -> System.out.println("B: " + i))
        .map(i -> "#" + i * 10)
        .doOnNext(s -> System.out.println("C: " + s))
        .filter(s -> s.length() < 4)
        .subscribe(s -> System.out.println("D: " + s));
  }

  @Test
  public void sample_79() {
    Observable<Integer> numbers = just(1, 2, 3, 4);

    numbers.map(x -> x * 2);
    numbers.filter(x -> x != 10);

    //equivalent
    numbers.flatMap(x -> just(x * 2));
    numbers.flatMap(x -> (x != 10) ? just(x) : empty());
  }

  @Test
  public void sample_111() {
    Observable<Customer> customers = just(new Customer());
    Observable<Order> orders = customers
        .flatMap(customer ->
            Observable.fromIterable(customer.getOrders()));
  }

  @Test
  public void sample_119() {
    Observable<Customer> customers = just(new Customer());
    Observable<Order> orders = customers
        .map(Customer::getOrders)
        .flatMap(Observable::fromIterable);
  }

  @Test
  public void sample_127() {
    Observable<Customer> customers = just(new Customer());
    Observable<Order> orders = customers
        .flatMapIterable(Customer::getOrders);
  }

  void store(UUID id) {
    upload(id).subscribe(
        bytes -> {
        }, //ignore
        e -> log.error("Error", e),
        () -> rate(id)
    );
  }

  private Observable<Long> upload(UUID id) {
    return just(42L);
  }

  Observable<Rating> rate(UUID id) {
    return just(new Rating());
  }

  @Test
  public void sample_155() {
    UUID id = UUID.randomUUID();
    upload(id)
        .flatMap(
            bytes -> empty(),
            Observable::error,
            () -> rate(id)
        );
  }

  private Observable<Sound> toMorseCode(char ch) {
    switch (ch) {
      case 'a':
        return just(DI, DAH);
      case 'b':
        return just(DAH, DI, DI, DI);
      case 'c':
        return just(DAH, DI, DAH, DI);
      case 'd':
        return just(DAH, DI, DI);
      case 'e':
        return just(DI);
      case 'f':
        return just(DI, DI, DAH, DI);
      case 'g':
        return just(DAH, DAH, DI);
      case 'h':
        return just(DI, DI, DI, DI);
      case 'i':
        return just(DI, DI);
      case 'j':
        return just(DI, DAH, DAH, DAH);
      case 'k':
        return just(DAH, DI, DAH);
      case 'l':
        return just(DI, DAH, DI, DI);
      case 'm':
        return just(DAH, DAH);
      case 'n':
        return just(DAH, DI);
      case 'o':
        return just(DAH, DAH, DAH);
      case 'p':
        return just(DI, DAH, DAH, DI);
      case 'q':
        return just(DAH, DAH, DI, DAH);
      case 'r':
        return just(DI, DAH, DI);
      case 's':
        return just(DI, DI, DI);
      case 't':
        return just(DAH);
      case 'u':
        return just(DI, DI, DAH);
      case 'v':
        return just(DI, DI, DI, DAH);
      case 'w':
        return just(DI, DAH, DAH);
      case 'x':
        return just(DAH, DI, DI, DAH);
      case 'y':
        return just(DAH, DI, DAH, DAH);
      case 'z':
        return just(DAH, DAH, DI, DI);
      case '0':
        return just(DAH, DAH, DAH, DAH, DAH);
      case '1':
        return just(DI, DAH, DAH, DAH, DAH);
      case '2':
        return just(DI, DI, DAH, DAH, DAH);
      case '3':
        return just(DI, DI, DI, DAH, DAH);
      case '4':
        return just(DI, DI, DI, DI, DAH);
      case '5':
        return just(DI, DI, DI, DI, DI);
      case '6':
        return just(DAH, DI, DI, DI, DI);
      case '7':
        return just(DAH, DAH, DI, DI, DI);
      case '8':
        return just(DAH, DAH, DAH, DI, DI);
      case '9':
        return just(DAH, DAH, DAH, DAH, DI);
      default:
        return empty();
    }
  }

  @Test
  public void sample_213() {
    just('S', 'p', 'a', 'r', 't', 'a')
        .map(Character::toLowerCase)
        .flatMap(this::toMorseCode);

  }

  @Test
  public void sample_218() throws Exception {
    just("Lorem", "ipsum", "dolor", "sit", "amet",
        "consectetur", "adipiscing", "elit")
        .delay(word -> timer(word.length(), SECONDS))
        .subscribe(System.out::println);

    SECONDS.sleep(15);
  }

  private Observable<String> loadRecordsFor(DayOfWeek dow) {
    switch (dow) {
      case SUNDAY:
        return
            interval(90, MILLISECONDS)
                .take(5)
                .map(i -> "Sun-" + i);
      case MONDAY:
        return
            interval(65, MILLISECONDS)
                .take(5)
                .map(i -> "Mon-" + i);
      default:
        throw new IllegalArgumentException("Illegal: " + dow);
    }
  }

  @Test
  public void sample_249() {
    just(DayOfWeek.SUNDAY, DayOfWeek.MONDAY)
//        .concatMap(this::loadRecordsFor)
        .flatMap(this::loadRecordsFor)
        .subscribe(this::print);
    Sleeper.sleep(Duration.ofSeconds(2));
  }

  @Test
  public void sample_258() {
    List<User> veryLargeList = Arrays.asList(new User(), new User(), new User(), new User());
    Observable<Profile> profiles = Observable
        .fromIterable(veryLargeList)
        .flatMap(User::loadProfile);
  }

  @Test
  public void sample_286() {
    final WeatherStation station = new BasicWeatherStation();

    Observable<Temperature> temperatureMeasurements = station.temperature();
    Observable<Wind> windMeasurements = station.wind();

    temperatureMeasurements.zipWith(windMeasurements, Weather::new);
  }

  @Test
  public void sample_298() {
    Observable<Integer> oneToEight = Observable.range(1, 8);
    Observable<String> ranks = oneToEight
        .map(Object::toString);
    Observable<String> files = oneToEight
        .map(x -> 'a' + x - 1)
        .map(ascii -> (char) ascii.intValue())
        .map(ch -> Character.toString(ch));

    Observable<String> squares = files
        .flatMap(file -> ranks.map(rank -> file + rank));
    squares.subscribe(this::print);
  }

  @Test
  public void sample_312() {
    Observable<LocalDate> nextTenDays =
        Observable
            .range(1, 10)
            .map(i -> LocalDate.now().plusDays(i));

    Observable<Vacation> possibleVacations =
        just(City.Warsaw, City.London, City.Paris)
            .flatMap(city -> nextTenDays.map(date -> new Vacation(city, date))
                .flatMap(vacation ->
                    Observable.zip(
                        vacation.weather().filter(Weather::isSunny),
                        vacation.cheapFlightFrom(City.NewYork),
                        vacation.cheapHotel(),
                        (w, f, h) -> vacation
                    )));
    possibleVacations.subscribe(this::print);
  }

  @Test
  public void sample_332() {
    Observable<Long> red = interval(10, TimeUnit.MILLISECONDS);
    Observable<Long> green = interval(10, TimeUnit.MILLISECONDS);

    Observable.zip(
        red.timestamp(),
        green.timestamp(),
        (r, g) -> r.time() - g.time()
    ).forEach(System.out::println);
  }

  @Test
  public void sample_345() {
    Observable.combineLatest(
        interval(17, MILLISECONDS).map(x -> "S" + x),
        interval(10, MILLISECONDS).map(x -> "F" + x),
        (s, f) -> f + ":" + s
    ).forEach(System.out::println);
    Sleeper.sleep(Duration.ofSeconds(2));
  }

  @Test
  public void sample_355() {
    Observable<String> fast = interval(10, MILLISECONDS)
        .map(x -> "F" + x)
        .delay(100, MILLISECONDS)
        .startWith("FX");
    Observable<String> slow = interval(17, MILLISECONDS).map(x -> "S" + x);
    slow
        .withLatestFrom(fast, (s, f) -> s + ":" + f)
        .forEach(System.out::println);
  }

  @Test
  public void sample_367() {
    just(1, 2)
        .startWith(0)
        .subscribe(System.out::println);
  }

  Observable<String> stream(int initialDelay, int interval, String name) {
    return
        interval(initialDelay, interval, MILLISECONDS)
            .map(x -> name + x)
            .doOnSubscribe(disposable -> log.info("Subscribe to " + name))
            .doOnDispose(() -> log.info("Unsubscribe from " + name));
  }

  @Test
  public void sample_375() {
    Observable.ambArray(
        stream(100, 17, "S"),
        stream(200, 10, "F")
    ).subscribe(log::info);
  }

  @Test
  public void sample_393() {
    stream(100, 17, "S")
        .ambWith(stream(200, 10, "F"))
        .subscribe(log::info);
  }

  @Test
  public void sample_400() {
    //BROKEN!
    Observable<Long> progress = transferFile();

    LongAdder total = new LongAdder();
    progress.subscribe(total::add);
    Sleeper.sleep(Duration.ofSeconds(10));
  }

  private Observable<Long> transferFile() {
    return
        interval(500, MILLISECONDS)
            .map(x -> RandomUtils.nextLong(10, 30))
            .take(100);
  }

  @Test
  public void sample_419() {
    Observable<Long> progress = transferFile();

    Observable<Long> totalProgress = progress
        .scan((total, chunk) -> total + chunk);

    System.out.println(totalProgress.blockingLast());
  }

  @Test
  public void sample_431() {
    Observable<BigInteger> factorials = Observable
        .range(2, 100)
        .scan(BigInteger.ONE, (big, cur) ->
            big.multiply(BigInteger.valueOf(cur)));
    factorials.subscribe(this::print);
  }

  @Test
  public void sample_440() {
    Observable<CashTransfer> transfers = just(new CashTransfer());

    Single<BigDecimal> total1 = transfers
        .reduce(BigDecimal.ZERO,
            (totalSoFar, transfer) ->
                totalSoFar.add(transfer.getAmount()));
    total1.subscribe(this::print);

    Single<BigDecimal> total2 = transfers
        .map(CashTransfer::getAmount)
        .reduce(BigDecimal.ZERO, BigDecimal::add);
    total2.subscribe(this::print);
  }

  @Test
  public void sample_456() {
    Single<List<Integer>> all = Observable
        .range(10, 20)
        .reduce(new ArrayList<>(), (list, item) -> {
          list.add(item);
          return list;
        });
    all.subscribe(this::print);
  }

  @Test
  public void sample_463() {
    Single<List<Integer>> all = Observable
        .range(10, 20)
        .collect(ArrayList::new, List::add);
    all.subscribe(this::print);
  }

  @Test
  public void sample_470() {
    Single<String> str = Observable
        .range(1, 10)
        .collect(
            StringBuilder::new,
            (sb, x) -> sb.append(x).append(", "))
        .map(StringBuilder::toString);
    str.subscribe(this::print);
  }


  private Observable<Integer> randomInts() {
    return Observable.create(subscriber -> {
      Random random = new Random();
      while (!subscriber.isDisposed()) {
        subscriber.onNext(random.nextInt(1000));
      }
    });
  }

  @Test
  public void sample_490() {
    final Observable<Integer> randomInts = randomInts();
    Observable<Integer> uniqueRandomInts = randomInts
        .distinct()
        .take(10);
//        .take(1001);
    uniqueRandomInts.subscribe(this::print);
  }

  @Test
  public void sample_499() {
    Observable<Status> tweets = empty();

    Observable<Long> distinctUserIds = tweets
        .map(status -> status.getUser().getId())
        .distinct();
    distinctUserIds.subscribe(this::print);
  }

  @Test
  public void sample_508() {
    Observable<Status> tweets = empty();

    Observable<Status> distinctUserIds = tweets
        .distinct(status -> status.getUser().getId());
    distinctUserIds.subscribe(this::print);
  }

  @Test
  public void sample_516() {
    Observable<Weather> measurements = empty();

    Observable<Weather> tempChanges = measurements
        .distinctUntilChanged(Weather::getTemperature);
  }

  @Test
  public void sample_524() {
    Observable.range(1, 5).take(3);  // [1, 2, 3]
    Observable.range(1, 5).skip(3);  // [4, 5]
    Observable.range(1, 5).skip(5);  // []
  }

  @Test
  public void sample_531() {
    Observable.range(1, 5).takeLast(2);  // [4, 5]
    Observable.range(1, 5).skipLast(2);  // [1, 2, 3]
  }

  @Test
  public void sample_537() {
    Observable.range(1, 5).takeUntil(x -> x == 3);  // [1, 2, 3]
    Observable.range(1, 5).takeWhile(x -> x != 3);  // [1, 2]
  }

  @Test
  public void sample_543() {
    Single<Integer> size =
        just('A', 'B', 'C', 'D')
            .reduce(0, (sizeSoFar, ch) -> sizeSoFar + 1);
  }

  @Test
  public void sample_550() {
    Observable<Integer> numbers = Observable.range(1, 5);

    numbers.all(x -> x != 4);    // [false]
    numbers.contains(4);         // [true]
  }

  @Test
  public void sample_559() {
    Observable<Data> veryLong = Observable
        .range(0, 1_000)
        .map(x -> new Data());
    final Observable<Data> ends = Observable.concat(
        veryLong.take(5),
        veryLong.takeLast(5)
    );
  }

  @Test
  public void sample_570() {
    Observable<Car> fromCache = loadFromCache();
    Observable<Car> fromDb = loadFromDb();

    Observable<Car> found = Observable
        .concat(fromCache, fromDb)
        .firstElement()
        .toObservable();
    found.forEach(this::print);
  }

  private Observable<Car> loadFromDb() {
    return just(new Car());
  }

  private Observable<Car> loadFromCache() {
    return just(new Car());
  }

  @Test
  public void sample_589() {
    Observable<Boolean> trueFalse = just(true, false).repeat();
    Observable<Integer> upstream = Observable.range(30, 8);
    Observable<Integer> downstream = upstream
        .zipWith(trueFalse, Pair::of)
        .filter(Pair::getRight)
        .map(Pair::getLeft);
    downstream.forEach(this::print);
  }

  @Test
  public void sample_600() {
    Observable<Boolean> trueFalse = just(true, false).repeat();
    Observable<Integer> upstream = Observable.range(30, 8);

    upstream.zipWith(trueFalse, (t, bool) ->
        bool ? just(t) : empty())
        .flatMap(obs -> obs);
  }

  private <T> void print(T t) {
    log.info("Got = {}", t);
  }
}
