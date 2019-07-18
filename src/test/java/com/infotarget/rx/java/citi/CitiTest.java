package com.infotarget.rx.java.citi;

import com.infotarget.rx.java.sleeper.Sleeper;
import org.junit.Test;

import java.time.Duration;

public class CitiTest {

  @Test
  public void should_combine_two_streams_in_certain_way() {
    new CashRiskAndWorkingDaySamzaTask();

    Sleeper.sleep(Duration.ofSeconds(30));
  }

  @Test
  public void should_buffer_trades_before_receiving_working_day() {
    new WaitForWorkingDaySamzaTask();

    Sleeper.sleep(Duration.ofSeconds(30));
  }
}
