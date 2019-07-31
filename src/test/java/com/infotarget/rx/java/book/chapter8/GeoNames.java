package com.infotarget.rx.java.book.chapter8;

import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;

import java.util.Objects;

public interface GeoNames {

  Logger log = LoggerFactory.getLogger(GeoNames.class);

  default Observable<Integer> populationOf(String query) {
    return search(query)
        .concatMapIterable(SearchResult::getGeonames)
        .map(Geoname::getPopulation)
        .filter(Objects::nonNull)
        .single(0)
        .doOnError(th ->
            log.warn("Falling back to 0 for {}", query, th))
        .onErrorReturn(th -> 0)
        .subscribeOn(Schedulers.io())
        .toObservable();
  }

  default Observable<SearchResult> search(String query) {
    return search(query, 1, "LONG", "some_user");
  }

  @GET("/searchJSON")
  Observable<SearchResult> search(
      @Query("q") String query,
      @Query("maxRows") int maxRows,
      @Query("style") String style,
      @Query("username") String username
  );

}
