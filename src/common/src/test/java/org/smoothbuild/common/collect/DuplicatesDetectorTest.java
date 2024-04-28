package org.smoothbuild.common.collect;

import static com.google.common.truth.Truth.assertThat;

import org.junit.jupiter.api.Test;

public class DuplicatesDetectorTest {
  @Test
  void initially_has_no_duplicates() {
    assertThat(new DuplicatesDetector<>().hasDuplicates()).isFalse();
  }

  @Test
  void adding_first_elem_returns_false() {
    assertThat(new DuplicatesDetector<>().addValue("string1")).isFalse();
  }

  @Test
  void has_no_duplicates_after_adding_first_elem() {
    DuplicatesDetector<String> duplicatesDetector = new DuplicatesDetector<>();
    duplicatesDetector.addValue("string1");
    assertThat(duplicatesDetector.hasDuplicates()).isFalse();
  }

  @Test
  void adding_element_for_the_second_time_returns_true() {
    DuplicatesDetector<String> duplicatesDetector = new DuplicatesDetector<>();
    duplicatesDetector.addValue("string1");
    assertThat(duplicatesDetector.addValue("string1")).isTrue();
  }

  @Test
  void has_duplicates_after_adding_elem_twice() {
    DuplicatesDetector<String> duplicatesDetector = new DuplicatesDetector<>();
    duplicatesDetector.addValue("string1");
    duplicatesDetector.addValue("string1");
    assertThat(duplicatesDetector.hasDuplicates()).isTrue();
  }

  @Test
  void adding_second_but_different_elem_returns_false() {
    DuplicatesDetector<String> duplicatesDetector = new DuplicatesDetector<>();
    duplicatesDetector.addValue("string1");
    assertThat(duplicatesDetector.addValue("string2")).isFalse();
  }

  @Test
  void has_no_duplicates_after_adding_two_different_elements() {
    DuplicatesDetector<String> duplicatesDetector = new DuplicatesDetector<>();
    duplicatesDetector.addValue("string1");
    duplicatesDetector.addValue("string2");
    assertThat(duplicatesDetector.hasDuplicates()).isFalse();
  }

  // getDuplicateValues()

  @Test
  void get_duplicate_values_returns_empty_set_initially() {
    assertThat(new DuplicatesDetector<>().getDuplicateValues()).isEmpty();
  }

  @Test
  void get_duplicate_values_returns_empty_set_when_one_elem_has_been_added() {
    DuplicatesDetector<String> duplicatesDetector = new DuplicatesDetector<>();
    duplicatesDetector.addValue("string1");
    assertThat(duplicatesDetector.getDuplicateValues()).isEmpty();
  }

  @Test
  void get_duplicate_values_returns_empty_set_when_two_different_elements_have_been_added() {
    DuplicatesDetector<String> duplicatesDetector = new DuplicatesDetector<>();
    duplicatesDetector.addValue("string1");
    duplicatesDetector.addValue("string2");
    assertThat(duplicatesDetector.getDuplicateValues()).isEmpty();
  }

  @Test
  void get_duplicate_values_returns_set_with_elem_that_has_been_added_twice() {
    DuplicatesDetector<String> duplicatesDetector = new DuplicatesDetector<>();
    duplicatesDetector.addValue("string1");
    duplicatesDetector.addValue("string1");
    assertThat(duplicatesDetector.getDuplicateValues()).containsExactly("string1");
  }

  @Test
  void
      get_duplicate_values_returns_set_with_elements_that_have_been_added_twice_without_those_added_once() {
    DuplicatesDetector<String> duplicatesDetector = new DuplicatesDetector<>();
    duplicatesDetector.addValue("string1");
    duplicatesDetector.addValue("string1");
    duplicatesDetector.addValue("string2");
    duplicatesDetector.addValue("string3");
    duplicatesDetector.addValue("string3");
    duplicatesDetector.addValue("string4");
    assertThat(duplicatesDetector.getDuplicateValues()).containsExactly("string1", "string3");
  }

  // getUniqueValues()

  @Test
  void initially_get_unique_values_is_empty() {
    DuplicatesDetector<String> duplicatesDetector = new DuplicatesDetector<>();
    assertThat(duplicatesDetector.getUniqueValues()).isEmpty();
  }

  @Test
  void get_unique_values_returns_elem_that_has_been_added() {
    DuplicatesDetector<String> duplicatesDetector = new DuplicatesDetector<>();
    duplicatesDetector.addValue("string1");
    assertThat(duplicatesDetector.getUniqueValues()).containsExactly("string1");
  }

  @Test
  void get_unique_values_returns_both_added_elements() {
    DuplicatesDetector<String> duplicatesDetector = new DuplicatesDetector<>();
    duplicatesDetector.addValue("string1");
    duplicatesDetector.addValue("string2");
    assertThat(duplicatesDetector.getUniqueValues()).containsExactly("string1", "string2");
  }

  @Test
  void get_unique_values_returns_only_one_duplicated_elem() {
    DuplicatesDetector<String> duplicatesDetector = new DuplicatesDetector<>();
    duplicatesDetector.addValue("string1");
    duplicatesDetector.addValue("string1");
    assertThat(duplicatesDetector.getUniqueValues()).containsExactly("string1");
  }

  @Test
  void get_unique_values_returns_set_with_deduplicated_values_that_has_been_added() {
    DuplicatesDetector<String> duplicatesDetector = new DuplicatesDetector<>();
    duplicatesDetector.addValue("string1");
    duplicatesDetector.addValue("string1");
    duplicatesDetector.addValue("string2");
    duplicatesDetector.addValue("string3");
    duplicatesDetector.addValue("string3");
    duplicatesDetector.addValue("string4");
    assertThat(duplicatesDetector.getUniqueValues())
        .containsExactly("string1", "string2", "string3", "string4");
  }
}
