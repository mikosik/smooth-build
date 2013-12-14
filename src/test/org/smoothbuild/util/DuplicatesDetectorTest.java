package org.smoothbuild.util;

import static com.google.common.collect.Sets.newHashSet;
import static org.hamcrest.Matchers.empty;
import static org.testory.Testory.given;
import static org.testory.Testory.then;
import static org.testory.Testory.thenEqual;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.when;

import org.junit.Test;

public class DuplicatesDetectorTest {
  String string1 = "string1";
  String string2 = "string2";
  String string3 = "string3";
  String string4 = "string4";

  DuplicatesDetector<String> duplicatesDetector;

  @Test
  public void initially_has_no_duplicates() throws Exception {
    when(duplicatesDetector = new DuplicatesDetector<String>());
    then(!duplicatesDetector.hasDuplicates());
  }

  @Test
  public void adding_first_element_returns_false() throws Exception {
    given(duplicatesDetector = new DuplicatesDetector<String>());
    when(duplicatesDetector).add(string1);
    thenReturned(false);
  }

  @Test
  public void has_no_duplicates_after_adding_first_element() throws Exception {
    given(duplicatesDetector = new DuplicatesDetector<String>());
    when(duplicatesDetector).add(string1);
    then(!duplicatesDetector.hasDuplicates());
  }

  @Test
  public void adding_element_for_the_second_time_returns_true() throws Exception {
    given(duplicatesDetector = new DuplicatesDetector<String>());
    given(duplicatesDetector).add(string1);
    when(duplicatesDetector).add(string1);
    thenReturned(true);
  }

  @Test
  public void has_duplicates_after_adding_element_twice() throws Exception {
    given(duplicatesDetector = new DuplicatesDetector<String>());
    given(duplicatesDetector).add(string1);
    when(duplicatesDetector).add(string1);
    then(duplicatesDetector.hasDuplicates());
  }

  @Test
  public void adding_second_but_different_element_returns_false() throws Exception {
    given(duplicatesDetector = new DuplicatesDetector<String>());
    given(duplicatesDetector).add(string1);
    when(duplicatesDetector).add(string2);
    thenReturned(false);
  }

  @Test
  public void has_no_duplicates_after_adding_two_different_elements() throws Exception {
    given(duplicatesDetector = new DuplicatesDetector<String>());
    given(duplicatesDetector).add(string1);
    when(duplicatesDetector).add(string2);
    then(!duplicatesDetector.hasDuplicates());
  }

  // getDuplicates()

  @Test
  public void get_duplicates_returns_empty_set_initially() throws Exception {
    when(duplicatesDetector = new DuplicatesDetector<String>());
    then(duplicatesDetector.getDuplicates(), empty());
  }

  @Test
  public void get_duplicates_returns_empty_set_when_one_element_has_been_added() throws Exception {
    given(duplicatesDetector = new DuplicatesDetector<String>());
    when(duplicatesDetector).add(string1);
    then(duplicatesDetector.getDuplicates(), empty());
  }

  @Test
  public void get_duplicates_returns_empty_set_when_two_different_elements_have_been_added()
      throws Exception {
    given(duplicatesDetector = new DuplicatesDetector<String>());
    given(duplicatesDetector).add(string1);
    when(duplicatesDetector).add(string2);
    then(duplicatesDetector.getDuplicates(), empty());
  }

  @Test
  public void get_duplicates_returns_set_with_element_that_has_been_added_twice() throws Exception {
    given(duplicatesDetector = new DuplicatesDetector<String>());
    given(duplicatesDetector).add(string1);
    when(duplicatesDetector).add(string1);
    thenEqual(duplicatesDetector.getDuplicates(), newHashSet(string1));
  }

  @Test
  public void get_duplicates_returns_set_with_elements_that_have_been_added_twice_without_those_added_once()
      throws Exception {
    given(duplicatesDetector = new DuplicatesDetector<String>());
    given(duplicatesDetector).add(string1);
    given(duplicatesDetector).add(string1);
    given(duplicatesDetector).add(string2);
    given(duplicatesDetector).add(string3);
    given(duplicatesDetector).add(string3);
    given(duplicatesDetector).add(string4);

    thenEqual(duplicatesDetector.getDuplicates(), newHashSet(string1, string3));
  }

  // getUniqueValues()

  @Test
  public void initially_get_unique_values_is_empty() throws Exception {
    given(duplicatesDetector = new DuplicatesDetector<String>());
    when(duplicatesDetector.getUniqueValues());
    thenReturned(empty());
  }

  @Test
  public void get_unique_values_returns_element_that_has_been_added() throws Exception {
    given(duplicatesDetector = new DuplicatesDetector<String>());
    when(duplicatesDetector).add(string1);
    thenEqual(duplicatesDetector.getUniqueValues(), newHashSet(string1));
  }

  @Test
  public void get_unique_values_returns_both_added_elements() throws Exception {
    given(duplicatesDetector = new DuplicatesDetector<String>());
    given(duplicatesDetector).add(string1);
    when(duplicatesDetector).add(string2);
    thenEqual(duplicatesDetector.getUniqueValues(), newHashSet(string1, string2));
  }

  @Test
  public void get_unique_values_returns_contains_only_once_duplicated_element() throws Exception {
    given(duplicatesDetector = new DuplicatesDetector<String>());
    given(duplicatesDetector).add(string1);
    when(duplicatesDetector).add(string1);
    thenEqual(duplicatesDetector.getUniqueValues(), newHashSet(string1));
  }

  @Test
  public void get_unique_values_returns_set_with_deduplicated_values_that_has_been_added()
      throws Exception {
    given(duplicatesDetector = new DuplicatesDetector<String>());
    given(duplicatesDetector).add(string1);
    given(duplicatesDetector).add(string1);
    given(duplicatesDetector).add(string2);
    given(duplicatesDetector).add(string3);
    given(duplicatesDetector).add(string3);
    given(duplicatesDetector).add(string4);

    thenEqual(duplicatesDetector.getUniqueValues(), newHashSet(string1, string2, string3, string4));
  }
}
