package org.smoothbuild.util;

import static org.testory.Testory.given;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.when;

import org.junit.Test;

import com.google.common.collect.Sets;

public class DuplicatesDetectorTest {
  String string1 = "string1";
  String string2 = "string2";
  String string3 = "string3";
  String string4 = "string4";

  DuplicatesDetector<String> duplicatesDetector;

  @Test
  public void initially_has_no_duplicates() throws Exception {
    given(duplicatesDetector = new DuplicatesDetector<String>());
    when(duplicatesDetector.hasDuplicates());
    thenReturned(false);
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
    given(duplicatesDetector).add(string1);
    when(duplicatesDetector.hasDuplicates());
    thenReturned(false);
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
    given(duplicatesDetector).add(string1);
    when(duplicatesDetector.hasDuplicates());
    thenReturned(true);
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
    given(duplicatesDetector).add(string2);
    when(duplicatesDetector.hasDuplicates());
    thenReturned(false);
  }

  // getDuplicates()

  @Test
  public void get_duplicates_returns_empty_set_initially() throws Exception {
    given(duplicatesDetector = new DuplicatesDetector<String>());
    when(duplicatesDetector.getDuplicates());
    thenReturned(Sets.newHashSet());
  }

  @Test
  public void get_duplicates_returns_empty_set_when_one_element_has_been_added() throws Exception {
    given(duplicatesDetector = new DuplicatesDetector<String>());
    given(duplicatesDetector).add(string1);
    when(duplicatesDetector.getDuplicates());
    thenReturned(Sets.newHashSet());
  }

  @Test
  public void get_duplicates_returns_empty_set_when_two_different_elements_have_been_added()
      throws Exception {
    given(duplicatesDetector = new DuplicatesDetector<String>());
    given(duplicatesDetector).add(string1);
    given(duplicatesDetector).add(string2);
    when(duplicatesDetector.getDuplicates());
    thenReturned(Sets.newHashSet());
  }

  @Test
  public void get_duplicates_returns_set_with_element_that_has_been_added_twice() throws Exception {
    given(duplicatesDetector = new DuplicatesDetector<String>());
    given(duplicatesDetector).add(string1);
    given(duplicatesDetector).add(string1);
    when(duplicatesDetector.getDuplicates());
    thenReturned(Sets.newHashSet(string1));
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

    when(duplicatesDetector.getDuplicates());
    thenReturned(Sets.newHashSet(string1, string3));
  }
}
