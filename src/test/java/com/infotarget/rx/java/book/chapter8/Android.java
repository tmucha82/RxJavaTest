package com.infotarget.rx.java.book.chapter8;

import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.widget.*;
import com.infotarget.rx.java.book.chapter8.rxandroid.AndroidSchedulers;
import com.infotarget.rx.java.book.chapter8.rxbinding.RxTextView;
import com.infotarget.rx.java.book.chapter8.rxbinding.RxView;
import com.infotarget.rx.java.book.chapter8.rxbinding.TextViewAfterTextChangeEvent;
import io.reactivex.Observable;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.Ignore;
import org.junit.Test;

import java.util.List;
import java.util.concurrent.TimeUnit;

import static android.content.ContentValues.TAG;

@Ignore
public class Android extends Activity {

  private final MeetupApi meetup = new ApiFactory().meetUp();

  @Test
  public void sample_9() throws Exception {
    Button button = null;  //...
    button.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        meetup
            .listCities(52.229841, 21.011736)
            .concatMapIterable(extractCities())
            .map(toCityName())
            .toList()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                putOnListView(),
                displayError());
      }

      //...

    });
  }

  //Cities::getResults
  Function<Cities, Iterable<City>> extractCities() {
    return new Function<Cities, Iterable<City>>() {
      @Override
      public Iterable<City> apply(Cities cities) {
        return cities.getResults();
      }
    };
  }

  //City::getCity
  Function<City, String> toCityName() {
    return new Function<City, String>() {
      @Override
      public String apply(City city) {
        return city.getCity();
      }
    };
  }

  //cities -> listView.setAdapter(...)
  Consumer<List<String>> putOnListView() {
    ListView listView = null; //...
    return new Consumer<List<String>>() {
      @Override
      public void accept(List<String> cities) {
        listView.setAdapter(new ArrayAdapter(Android.this, -1 /*R.layout.list*/, cities));
      }
    };
  }

  //throwable -> {...}
  Consumer<Throwable> displayError() {
    return new Consumer<Throwable>() {
      @Override
      public void accept(Throwable throwable) {
        Log.e(TAG, "Error", throwable);
        Toast.makeText(Android.this, "Unable to load cities", Toast.LENGTH_SHORT).show();
      }
    };
  }

  @Test
  public void sample_98() throws Exception {
    Button button = new Button(null);
    RxView
        .clicks(button)
        .flatMap(c -> meetup.listCities(52.229841, 21.011736))
        .delay(2, TimeUnit.SECONDS)
        .concatMapIterable(extractCities())
        .map(toCityName())
        .toList()
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(
            putOnListView(),
            displayError());
  }

  Function<Void, Observable<Cities>> listCities(final double lat, final double lon) {
    return new Function<Void, Observable<Cities>>() {
      @Override
      public Observable<Cities> apply(Void aVoid) throws Exception {
        return meetup.listCities(lat, lon);
      }
    };
  }

  @Test
  public void sample_121() throws Exception {
    EditText latText = null;//...
    EditText lonText = null;//...

    Observable<Double> latChanges = RxTextView
        .afterTextChangeEvents(latText)
        .flatMap(toDouble());
    Observable<Double> lonChanges = RxTextView
        .afterTextChangeEvents(lonText)
        .flatMap(toDouble());

    Observable<Cities> cities = Observable
        .combineLatest(latChanges, lonChanges, toPair())
        .debounce(1, TimeUnit.SECONDS)
        .flatMap(listCitiesNear());
  }

  Function<TextViewAfterTextChangeEvent, Observable<Double>> toDouble() {
    return new Function<TextViewAfterTextChangeEvent, Observable<Double>>() {
      @Override
      public Observable<Double> apply(TextViewAfterTextChangeEvent e) {
        String s = e.editable().toString();
        try {
          return Observable.just(Double.parseDouble(s));
        } catch (NumberFormatException ex) {
          return Observable.empty();
        }
      }
    };
  }

  //return Pair::new
  BiFunction<Double, Double, Pair<Double, Double>> toPair() {
    return new BiFunction<Double, Double, Pair<Double, Double>>() {
      @Override
      public Pair<Double, Double> apply(Double lat, Double lon) {
        return Pair.of(lat, lon);
      }
    };
  }

  //return latLon -> meetUp.listCities(latLon.first, latLon.second)
  Function<Pair<Double, Double>, Observable<Cities>> listCitiesNear() {
    return new Function<Pair<Double, Double>, Observable<Cities>>() {
      @Override
      public Observable<Cities> apply(Pair<Double, Double> latLon) throws Exception {
        return meetup.listCities(latLon.getLeft(), latLon.getRight());
      }
    };
  }

}
