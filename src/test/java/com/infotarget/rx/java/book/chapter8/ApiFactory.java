package com.infotarget.rx.java.book.chapter8;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.jackson.JacksonConverterFactory;

class ApiFactory {

  GeoNames geoNames() {
    return new Retrofit.Builder()
        .baseUrl("http://api.geonames.org")
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        .addConverterFactory(JacksonConverterFactory.create(objectMapper()))
        .build()
        .create(GeoNames.class);
  }

  MeetupApi meetUp() {
    Retrofit retrofit = new Retrofit.Builder()
        .baseUrl("https://api.meetup.com/")
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        .addConverterFactory(
            JacksonConverterFactory.create(objectMapper()))
        .build();
    return retrofit.create(MeetupApi.class);
  }

  private ObjectMapper objectMapper() {
    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.setPropertyNamingStrategy(
        PropertyNamingStrategy.SNAKE_CASE);
    objectMapper.configure(
        DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    return objectMapper;
  }

}
