package com.infotarget.rx.java.book.chapter3;

import io.reactivex.Observable;
import io.reactivex.observables.GroupedObservable;
import org.junit.Test;

import java.util.Optional;
import java.util.UUID;

public class Reservations {

  @Test
  public void sample_9() {
    FactStore factStore = new CassandraFactStore();
    Observable<ReservationEvent> facts = factStore.observe();
    facts.subscribe(this::updateProjection);
  }

  void updateProjection(ReservationEvent event) {

  }

  private void store(UUID id, Reservation modified) {
    //...
  }

  Optional<Reservation> loadBy(UUID uuid) {
    //...
    return Optional.of(new Reservation());
  }

  @Test
  public void sample_34() {
    FactStore factStore = new CassandraFactStore();

    Observable<ReservationEvent> facts = factStore.observe();

    facts
        .flatMap(this::updateProjectionAsync)
        .subscribe();

    //...
  }

  Observable<ReservationEvent> updateProjectionAsync(ReservationEvent event) {
    //possibly asynchronous
    return Observable.just(new ReservationEvent());
  }

  @Test
  public void sample_52() {
    FactStore factStore = new CassandraFactStore();

    Observable<ReservationEvent> facts = factStore.observe();

    Observable<GroupedObservable<UUID, ReservationEvent>> grouped =
        facts.groupBy(ReservationEvent::getReservationUuid);

    grouped.subscribe(byUuid -> byUuid.subscribe(this::updateProjection));
  }

}
