package com.infotarget.rx.java.citi;

import io.reactivex.Observable;
import io.reactivex.internal.functions.Functions;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.TimeUnit;

class WaitForWorkingDaySamzaTask {

  private final static Logger log = LoggerFactory.getLogger(WaitForWorkingDaySamzaTask.class);

  private WorkingDay currentWorkingDay;

  private Subject<WorkingDay> workingDays = PublishSubject.create();
  private Subject<DerivTrade> derivTrades = PublishSubject.create();

  WaitForWorkingDaySamzaTask() {
    Observable
        .interval(3, 15, TimeUnit.SECONDS, Schedulers.io())
        .map(number -> new WorkingDay(LocalDate.now()
            .withDayOfMonth(number.intValue() + 1)
            .format(DateTimeFormatter.BASIC_ISO_DATE)))
        .subscribe(this::processWorkingDay);

    Observable
        .interval(0, 1, TimeUnit.SECONDS, Schedulers.io())
        .map(number -> new DerivTrade(String.valueOf(number), number.intValue()))
        .subscribe(this::processDerivTrade);

    Observable<DerivTrade> derivTradeObservable = Observable.concat(
        derivTrades
            .buffer(workingDays.isEmpty().toObservable())
            .flatMapIterable(Functions.identity()),
        derivTrades);

    derivTradeObservable
        .map(derivTrade -> new DerivTrade(derivTrade, currentWorkingDay))
        .subscribe(this::print);
  }

  private void processWorkingDay(final WorkingDay workingDay) {
    log.info("Received workingDay: {}", workingDay);
    currentWorkingDay = workingDay;
    workingDays.onNext(workingDay);
  }

  private void processDerivTrade(final DerivTrade derivTrade) {
    log.info("Received derivTrade: {}", derivTrade);
    derivTrades.onNext(derivTrade);
  }

  private <T> void print(final T object) {
    log.info("Got: {}", object);
  }
}
