package com.infotarget.rx.java.book.chapter7;

import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.observers.TestObserver;
import io.reactivex.schedulers.Timed;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigInteger;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.util.concurrent.TimeoutException;

import static java.time.Month.*;
import static java.util.concurrent.TimeUnit.MILLISECONDS;

@Ignore
public class Chapter7 {

  private static final Logger log = LoggerFactory.getLogger(Chapter7.class);

  @Test
  public void sample_9() throws Exception {
    Observable
        .create(subscriber -> {
          try {
            subscriber.onNext(1 / 0);
          } catch (Exception e) {
            subscriber.onError(e);
          }
        })
        //BROKEN, missing onError() callback
        .subscribe(System.out::println);
    //.subscribe(System.out::println, System.err::println); //this should be like that

  }

  @Test
  public void sample_29() throws Exception {
    Observable
        .create(subscriber -> {
          try {
            subscriber.onNext(1 / 0);
          } catch (Exception e) {
            subscriber.onError(e);
          }
        })
        .subscribe(
            System.out::println,
            throwable -> log.error("That escalated quickly", throwable));

  }

  @Test
  public void sample_45() throws Exception {
    Observable.create(subscriber -> {
      try {
        subscriber.onNext(1 / 0);
      } catch (Exception e) {
        subscriber.onError(e);
      }
    })
        .subscribe(
            System.out::println,
            throwable -> log.error("That escalated quickly", throwable));
    Observable.create(subscriber -> subscriber.onNext(1 / 0))
        .subscribe(
            System.out::println,
            throwable -> log.error("That escalated quickly", throwable));

    Observable.fromCallable(() -> 1 / 0)
        .subscribe(
            System.out::println,
            throwable -> log.error("That escalated quickly", throwable));
  }

  @Test
  public void sample_60() throws Exception {
    Observable
        .just(1, 0)
        .map(x -> 10 / x)
        .subscribe(
            System.out::println,
            throwable -> log.error("That escalated quickly", throwable));

    Observable
        .just("Lorem", null, "ipsum")
        .filter(String::isEmpty)
        .subscribe(
            System.out::println,
            throwable -> log.error("That escalated quickly", throwable));
  }

  @Test
  public void sample_71() throws Exception {
    Observable
        .just(1, 0)
        .flatMap(x -> (x == 0) ?
            Observable.error(new ArithmeticException("Zero :-(")) :
            Observable.just(10 / x)
        )
        .subscribe(
            System.out::println,
            throwable -> log.error("That escalated quickly", throwable));
  }


  private final PrintHouse printHouse = new PrintHouse();

  @Test
  public void sample_81() throws Exception {
    Observable<Person> person = Observable.just(new Person());
    Observable<InsuranceContract> insurance = Observable.just(new InsuranceContract());
    Observable<Health> health = person.flatMap(this::checkHealth);
    Observable<Income> income = person.flatMap(this::determineIncome);
    Observable<Score> score = Observable
        .zip(health, income, (h, i) -> asses(h, i))
        .map(this::translate);
    Observable<Agreement> agreement = Observable.zip(
        insurance,
        score.filter(Score::isHigh),
        this::prepare);
    agreement
        .filter(Agreement::postalMailRequired)
        .flatMap(this::print)
        .flatMap(printHouse::deliver)
        .subscribe(
            System.out::println,
            throwable -> log.error("That escalated quickly", throwable));
  }

  private Observable<Agreement> print(Agreement agreement) {
    return Observable.just(agreement);
  }

  private Agreement prepare(InsuranceContract contract, Score score) {
    return new Agreement();
  }

  private Score translate(BigInteger score) {
    return new Score();
  }

  private BigInteger asses(Health h, Income i) {
    return BigInteger.ONE;
  }

  private Observable<Income> determineIncome(Person person) {
    return Observable.error(new RuntimeException("Foo"));
  }

  private Observable<Health> checkHealth(Person person) {
    return Observable.just(new Health());
  }

  @Test
  public void sample_129() throws Exception {
    Observable<Person> person = Observable.just(new Person());
    person
        .flatMap(this::determineIncome)
        .onErrorReturn(error -> Income.no())
        .subscribe(
            System.out::println,
            throwable -> log.error("That escalated quickly", throwable));
  }

  public Observable<Income> sample_137() throws Exception {
    Person person = new Person();
    try {
      return determineIncome(person);
    } catch (Exception e) {
      return Observable.just(Income.no());
    }
  }

  @Test
  public void sample_147() throws Exception {
    Observable<Person> person = Observable.just(new Person());
    person
        .flatMap(this::determineIncome)
        .onErrorResumeNext(person.flatMap(this::guessIncome))
        .subscribe(
            System.out::println,
            throwable -> log.error("That escalated quickly", throwable));

  }

  private Observable<Income> guessIncome(Person person) {
    //...
    return Observable.just(new Income(1));
  }

  @Test
  public void sample_161() throws Exception {
    Observable<Person> person = Observable.just(new Person());

    person
        .flatMap(this::determineIncome)
        .flatMap(
            Observable::just,
            th -> Observable.empty(),
            Observable::empty)
        .concatWith(person.flatMap(this::guessIncome))
        .first(new Income(Integer.MIN_VALUE))
        .toObservable()
        .subscribe(
            System.out::println,
            throwable -> log.error("That escalated quickly", throwable));
  }

  @Test
  public void sample_175() throws Exception {
    Observable<Person> person = Observable.just(new Person());

    person
        .flatMap(this::determineIncome)
        .flatMap(
            Observable::just,
            th -> person.flatMap(this::guessIncome),
            Observable::empty)
        .subscribe(
            System.out::println,
            throwable -> log.error("That escalated quickly", throwable));
  }

  @Test
  public void sample_187() throws Exception {
    Observable<Person> person = Observable.just(new Person());

    person
        .flatMap(this::determineIncome)
        .onErrorResumeNext(th -> {
          if (th instanceof NullPointerException) {
            return Observable.error(th);
          } else {
            return person.flatMap(this::guessIncome);
          }
        })
        .subscribe(
            System.out::println,
            throwable -> log.error("That escalated quickly", throwable));

  }

  @Test
  public void sample_188() throws Exception {
    final Observable<Integer> integerObservable = Observable
        .fromArray(1, 2, 3, 4, 5, 6)
        .map(i -> {
              if (i % 3 == 0) {
                throw new RuntimeException("Haha");
              } else {
                return i;
              }
            }
        );

    integerObservable
        .onErrorResumeNext(Observable.empty())
        .flatMap(i -> Observable.just(i)
                .onErrorResumeNext(Observable.just(-3))
                .onExceptionResumeNext(Observable.empty())
        )
        .flatMap(
            Observable::just,
            th -> Observable.just(-3),
            Observable::empty)
        .subscribe(
            System.out::println,
            throwable -> log.error("That escalated quickly", throwable));
  }

  @Test
  public void sample_189() throws Exception {
    Observable<String> stringObservable = Observable
        .fromArray("1", "2", "3")
        .flatMap(x -> Observable.defer(() -> {
          try {
            if (x.equals("2")) {
              throw new NullPointerException();
            }
            return Observable.just(x + "-");
          } catch (Exception ex) {
            return Observable.error(ex);
          }
        })
            .map(s -> {
              if (s.equals("3-")) {
                throw new IllegalArgumentException();
              }
              return s + s;
            }).take(1)
            .zipWith(Observable.just("X"), (s, s2) -> s + s2)
            .onErrorResumeNext(Observable.empty()));

    TestObserver<String> test = stringObservable.test();
    test.assertResult("1-1-X");
  }

  @Test
  public void sample_190() throws Exception {
    Observable
        .fromArray(1, 2, 3, 4, 5, 6)
        .flatMapSingle(i -> Single.fromCallable(() -> {
          if (i % 3 == 0) {
            throw new RuntimeException("Haha");
          } else {
            return i;
          }
        })
            .onErrorReturnItem(-1))
        .filter(i -> i >= 0)
        .subscribe(
            System.out::println,
            throwable -> log.error("That escalated quickly", throwable));
  }


  @Test
  public void sample_191() {
    Observable
        .fromArray(1, 2, 3, 4, 5, 6)
        .flatMap(ii -> Observable.just(ii)
            .map(i -> {
              if (i % 3 == 0) {
                throw new RuntimeException("Haha");
              } else {
                return i;
              }
            }).onErrorResumeNext(Observable.empty())
        )
        .subscribe(
            System.out::println,
            throwable -> log.error("That escalated quickly", throwable));
  }

  @Test
  public void sample_192() {
    Observable
        .fromArray(1, 2, 3, 4, 5, 6)
        .concatMapDelayError(i -> Observable.fromCallable(() -> {
          if (i % 3 == 0) {
            throw new RuntimeException("Haha");
          } else {
            return i;
          }
        }))
        .onErrorResumeNext(Observable.empty())
        .subscribe(
            System.out::println,
            throwable -> log.error("That escalated quickly", throwable));
  }

  Observable<Confirmation> confirmation() {
    Observable<Confirmation> delayBeforeCompletion =
        Observable
            .<Confirmation>empty()
            .delay(200, MILLISECONDS);
    return Observable
        .just(new Confirmation())
        .delay(100, MILLISECONDS)
        .concatWith(delayBeforeCompletion);
  }

  @Test
  public void sample_215() throws Exception {
    confirmation()
        .timeout(210, MILLISECONDS)
        .forEachWhile(
            t -> {
              System.out.println(t);
              return true;
            },
            th -> {
              if ((th instanceof TimeoutException)) {
                System.out.println("Too long");
              } else {
                th.printStackTrace();
              }
            }
        );
  }

  Observable<LocalDate> nextSolarEclipse(LocalDate after) {
    return Observable
        .just(
            LocalDate.of(2016, MARCH, 9),
            LocalDate.of(2016, SEPTEMBER, 1),
            LocalDate.of(2017, FEBRUARY, 26),
            LocalDate.of(2017, AUGUST, 21),
            LocalDate.of(2018, FEBRUARY, 15),
            LocalDate.of(2018, JULY, 13),
            LocalDate.of(2018, AUGUST, 11),
            LocalDate.of(2019, JANUARY, 6),
            LocalDate.of(2019, JULY, 2),
            LocalDate.of(2019, DECEMBER, 26))
        .skipWhile(date -> !date.isAfter(after))
        .zipWith(
            Observable.interval(500, 50, MILLISECONDS),
            (date, x) -> date);
  }

  @Test
  public void sample_253() throws Exception {
    nextSolarEclipse(LocalDate.of(2016, SEPTEMBER, 1))
        .timeout(
            Observable.timer(1000, MILLISECONDS),
            date -> Observable.timer(100, MILLISECONDS));
  }

  @Test
  public void sample_262() throws Exception {
    Observable<Timed<LocalDate>> intervals =
        nextSolarEclipse(LocalDate.of(2016, JANUARY, 1))
            .timeInterval();
  }

  @Test
  public void sample_271() throws Exception {
    Flowable<Instant> timestamps = Flowable
        .fromCallable(this::dbQuery)
        .doOnSubscribe(disposable -> log.info("subscribe()"))
        .doOnRequest(c -> log.info("Requested {}", c))
        .doOnNext(instant -> log.info("Got: {}", instant));

    timestamps
        .zipWith(timestamps.skip(1), Duration::between)
        .map(Object::toString)
        .subscribe(log::info);
  }

  private Instant dbQuery() {
    return Instant.now();
  }

  @Test
  public void sample_291() throws Exception {
    Observable<String> obs = Observable
        .<String>error(new RuntimeException("Swallowed"))
        .doOnError(th -> log.warn("onError", th))
        .onErrorReturn(th -> "Fallback");
  }
}