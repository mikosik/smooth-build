package org.smoothbuild.util;

import static org.hamcrest.Matchers.empty;
import static org.smoothbuild.util.Lists.list;
import static org.testory.Testory.given;
import static org.testory.Testory.then;
import static org.testory.Testory.thenEqual;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.when;

import java.util.HashSet;

import org.junit.Test;

public class DuplicatesDetectorTest {
  private final String string1 = "string1";
  private final String string2 = "string2";
  private final String string3 = "string3";
  private final String string4 = "string4";
  private DuplicatesDetector<String> duplicatesDetector;

  @Test
  public void initially_has_no_duplicates() throws Exception {
    when(duplicatesDetector = new DuplicatesDetector<>());
    then(!duplicatesDetector.hasDuplicates());
  }

  @Test
  public void adding_first_element_returns_false() throws Exception {
    given(duplicatesDetector = new DuplicatesDetector<>());
    when(duplicatesDetector).addValue(string1);
    thenReturned(false);
  }

  @Test
  public void has_no_duplicates_after_adding_first_element() throws Exception {
    given(duplicatesDetector = new DuplicatesDetector<>());
    when(duplicatesDetector).addValue(string1);
    then(!duplicatesDetector.hasDuplicates());
  }

  @Test
  public void adding_element_for_the_second_time_returns_true() throws Exception {
    given(duplicatesDetector = new DuplicatesDetector<>());
    given(duplicatesDetector).addValue(string1);
    when(duplicatesDetector).addValue(string1);
    thenReturned(true);
  }

  @Test
  public void has_duplicates_after_adding_element_twice() throws Exception {
    given(duplicatesDetector = new DuplicatesDetector<>());
    given(duplicatesDetector).addValue(string1);
    when(duplicatesDetector).addValue(string1);
    then(duplicatesDetector.hasDuplicates());
  }

  @Test
  public void adding_second_but_different_element_returns_false() throws Exception {
    given(duplicatesDetector = new DuplicatesDetector<>());
    given(duplicatesDetector).addValue(string1);
    when(duplicatesDetector).addValue(string2);
    thenReturned(false);
  }

  @Test
  public void has_no_duplicates_after_adding_two_different_elements() throws Exception {
    given(duplicatesDetector = new DuplicatesDetector<>());
    given(duplicatesDetector).addValue(string1);
    when(duplicatesDetector).addValue(string2);
    then(!duplicatesDetector.hasDuplicates());
  }

  // getDuplicateValues()

  @Test
  public void get_duplicate_values_returns_empty_set_initially() throws Exception {
    when(duplicatesDetector = new DuplicatesDetector<>());
    then(duplicatesDetector.getDuplicateValues(), empty());
  }

  @Test
  public void get_duplicate_values_returns_empty_set_when_one_element_has_been_added()
      throws Exception {
    given(duplicatesDetector = new DuplicatesDetector<>());
    when(duplicatesDetector).addValue(string1);
    then(duplicatesDetector.getDuplicateValues(), empty());
  }

  @Test
  public void get_duplicate_values_returns_empty_set_when_two_different_elements_have_been_added()
      throws Exception {
    given(duplicatesDetector = new DuplicatesDetector<>());
    given(duplicatesDetector).addValue(string1);
    when(duplicatesDetector).addValue(string2);
    then(duplicatesDetector.getDuplicateValues(), empty());
  }

  @Test
  public void get_duplicate_values_returns_set_with_element_that_has_been_added_twice()
      throws Exception {
    given(duplicatesDetector = new DuplicatesDetector<>());
    given(duplicatesDetector).addValue(string1);
    when(duplicatesDetector).addValue(string1);
    thenEqual(duplicatesDetector.getDuplicateValues(), new HashSet<>(list(string1)));
  }

  @Test
  public void get_duplicate_values_returns_set_with_elements_that_have_been_added_twice_without_those_added_once()
      throws Exception {
    given(duplicatesDetector = new DuplicatesDetector<>());
    given(duplicatesDetector).addValue(string1);
    given(duplicatesDetector).addValue(string1);
    given(duplicatesDetector).addValue(string2);
    given(duplicatesDetector).addValue(string3);
    given(duplicatesDetector).addValue(string3);
    given(duplicatesDetector).addValue(string4);

    thenEqual(duplicatesDetector.getDuplicateValues(), new HashSet<>(list(string1, string3)));
  }

  // getUniqueValues()

  @Test
  public void initially_get_unique_values_is_empty() throws Exception {
    given(duplicatesDetector = new DuplicatesDetector<>());
    when(duplicatesDetector.getUniqueValues());
    thenReturned(empty());
  }

  @Test
  public void get_unique_values_returns_element_that_has_been_added() throws Exception {
    given(duplicatesDetector = new DuplicatesDetector<>());
    when(duplicatesDetector).addValue(string1);
    thenEqual(duplicatesDetector.getUniqueValues(), new HashSet<>(list(string1)));
  }

  @Test
  public void get_unique_values_returns_both_added_elements() throws Exception {
    given(duplicatesDetector = new DuplicatesDetector<>());
    given(duplicatesDetector).addValue(string1);
    when(duplicatesDetector).addValue(string2);
    thenEqual(duplicatesDetector.getUniqueValues(), new HashSet<>(list(string1, string2)));
  }

  @Test
  public void get_unique_values_returns_contains_only_once_duplicated_element() throws Exception {
    given(duplicatesDetector = new DuplicatesDetector<>());
    given(duplicatesDetector).addValue(string1);
    when(duplicatesDetector).addValue(string1);
    thenEqual(duplicatesDetector.getUniqueValues(), new HashSet<>(list(string1)));
  }

  @Test
  public void get_unique_values_returns_set_with_deduplicated_values_that_has_been_added()
      throws Exception {
    given(duplicatesDetector = new DuplicatesDetector<>());
    given(duplicatesDetector).addValue(string1);
    given(duplicatesDetector).addValue(string1);
    given(duplicatesDetector).addValue(string2);
    given(duplicatesDetector).addValue(string3);
    given(duplicatesDetector).addValue(string3);
    given(duplicatesDetector).addValue(string4);
    thenEqual(duplicatesDetector.getUniqueValues(), new HashSet<>(list(string1, string2, string3,
        string4)));
  }
}
