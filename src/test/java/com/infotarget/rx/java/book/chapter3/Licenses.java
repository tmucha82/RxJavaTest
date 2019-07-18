package com.infotarget.rx.java.book.chapter3;

import io.reactivex.Observable;
import org.junit.Test;

public class Licenses {

  private Observable<CarPhoto> cars() {
    return Observable.just(new CarPhoto());
  }

  private Observable<LicensePlate> recognize(CarPhoto photo) {
    return Observable.just(new LicensePlate());
  }

  @Test
  public void sample_100() {
    Observable<CarPhoto> cars = cars();

    Observable<Observable<LicensePlate>> plates =
        cars.map(this::recognize);

    Observable<LicensePlate> plates2 =
        cars.flatMap(this::recognize);
  }


  private Observable<LicensePlate> fastAlgo(CarPhoto photo) {
    //Fast but poor quality
    return Observable.just(new LicensePlate());
  }

  private Observable<LicensePlate> preciseAlgo(CarPhoto photo) {
    //Precise but can be expensive
    return Observable.just(new LicensePlate());
  }

  private Observable<LicensePlate> experimentalAlgo(CarPhoto photo) {
    //Unpredictable, running anyway
    return Observable.just(new LicensePlate());
  }

  @Test
  public void sample_317() {
    CarPhoto photo = new CarPhoto();
    Observable<LicensePlate> all = Observable.merge(
        preciseAlgo(photo),
        fastAlgo(photo),
        experimentalAlgo(photo)
    );
  }
}
