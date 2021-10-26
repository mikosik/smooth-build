package org.smoothbuild.util.collect;

import static com.google.common.truth.Truth.assertThat;

import org.junit.jupiter.api.Test;

public class CountersMapTest {
  @Test
  public void count_never_incremented_is_zero() {
    var countersMap = new CountersMap<>();
    assertThat(countersMap.count("A"))
        .isEqualTo(0);
  }

  @Test
  public void count_incremented_once_is_one() {
    var countersMap = new CountersMap<>();
    countersMap.increment("A");
    assertThat(countersMap.count("A"))
        .isEqualTo(1);
  }

  @Test
  public void count_incremented_twice_is_two() {
    var countersMap = new CountersMap<>();
    countersMap.increment("A");
    assertThat(countersMap.count("A"))
        .isEqualTo(1);
  }

  @Test
  public void count_never_incremented_is_zero_even_when_others_are_non_zero() {
    var countersMap = new CountersMap<>();
    countersMap.increment("A");
    assertThat(countersMap.count("B"))
        .isEqualTo(0);
  }
}
