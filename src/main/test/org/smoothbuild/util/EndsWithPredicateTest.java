package org.smoothbuild.util;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

public class EndsWithPredicateTest {
  @Test(expected = NullPointerException.class)
  public void nullSuffixIsForbidden() throws Exception {
    new EndsWithPredicate(null);
  }

  @Test
  public void test() {
    EndsWithPredicate predicate = new EndsWithPredicate("suffix");

    assertThat(predicate.apply("suffix")).isTrue();
    assertThat(predicate.apply("abcsuffix")).isTrue();
    assertThat(predicate.apply("   suffix")).isTrue();

    assertThat(predicate.apply("abc")).isFalse();
    assertThat(predicate.apply("")).isFalse();
    assertThat(predicate.apply("suffix ")).isFalse();
    assertThat(predicate.apply("abc")).isFalse();
  }

  @Test
  public void emptySuffixAlwaysMatches() throws Exception {
    EndsWithPredicate predicate = new EndsWithPredicate("");

    assertThat(predicate.apply("")).isTrue();
    assertThat(predicate.apply("abc")).isTrue();
    assertThat(predicate.apply("   ")).isTrue();
  }
}
