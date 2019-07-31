package com.infotarget.rx.java.book.chapter8;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Query;


public interface MeetupApi {

  @GET("/2/cities")
  Observable<Cities> listCities(
      @Query("lat") double lat,
      @Query("lon") double lon
  );

}
