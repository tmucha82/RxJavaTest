package com.infotarget.rx.java.citi;

import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.Subject;
import io.vavr.Tuple;
import io.vavr.Tuple2;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.TimeUnit;

class CashRiskAndWorkingDaySamzaTask {

  private final static Logger log = LoggerFactory.getLogger(CashRiskAndWorkingDaySamzaTask.class);

  private Subject<WorkingDay> workingDaySubject = PublishSubject.create();
  private Subject<CashRisk> cashRiskSubject = PublishSubject.create();

  CashRiskAndWorkingDaySamzaTask() {
    Observable
        .interval(1, 10, TimeUnit.SECONDS, Schedulers.io())
        .map(number -> new WorkingDay(LocalDate.now()
            .withDayOfMonth(number.intValue() + 1)
            .format(DateTimeFormatter.BASIC_ISO_DATE)))
        .subscribe(this::processWorkingDay);

    Observable
        .interval(2, 3, TimeUnit.SECONDS, Schedulers.io())
        .map(number ->
            new CashRisk(number, LocalDate.now()
                .withDayOfMonth(number > 4 ? 2 : 1)
                .format(DateTimeFormatter.BASIC_ISO_DATE)))
        .subscribe(this::processCashRisk);

    Observable
        .combineLatest(workingDaySubject, cashRiskSubject, Tuple::of)
        .filter(workingDayCashRiskTuple2 -> workingDayCashRiskTuple2._1().getLocalDate()
            .equals(workingDayCashRiskTuple2._2().getWorkingDay()))
        .subscribe(this::processCompletedData);

  }

  private void processWorkingDay(final WorkingDay workingDay) {
    log.info("Received workingDay: {}", workingDay);
    workingDaySubject.onNext(workingDay);
  }

  private void processCashRisk(final CashRisk cashRisk) {
    log.info("Received cashRisk: {}", cashRisk);
    cashRiskSubject.onNext(cashRisk);
  }

  private void processCompletedData(final Tuple2<WorkingDay, CashRisk> tuple) {
    log.info("Received completed data = {}", tuple);
  }
}
