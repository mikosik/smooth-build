package org.smoothbuild.testing.common;

import static org.smoothbuild.testing.common.ExceptionMatcher.exception;
import static org.testory.Testory.given;
import static org.testory.Testory.thenEqual;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.when;

import java.io.EOFException;
import java.io.IOException;
import java.util.NoSuchElementException;

import org.hamcrest.Matcher;
import org.hamcrest.StringDescription;
import org.junit.Test;

public class ExceptionMatcherTest {
  private Matcher<Throwable> matcher;
  private StringDescription stringDescription;

  // matching without message

  @Test
  public void matches_exception_of_the_same_class() {
    given(() -> matcher = exception(new IOException()));
    when(() -> matcher.matches(new IOException()));
    thenReturned(true);
  }

  @Test
  public void matches_exception_of_subclass() {
    given(() -> matcher = exception(new IOException()));
    when(() -> matcher.matches(new EOFException()));
    thenReturned(true);
  }

  @Test
  public void doesnt_match_exception_of_superclass() {
    given(() -> matcher = exception(new EOFException()));
    when(() -> matcher.matches(new IOException()));
    thenReturned(false);
  }

  // matching with message

  @Test
  public void matches_exception_of_the_same_class_and_equal_message() {
    given(() -> matcher = exception(new IOException("message")));
    when(() -> matcher.matches(new IOException("message")));
    thenReturned(true);
  }

  @Test
  public void matches_exception_of_subclass_and_equal_message() {
    given(() -> matcher = exception(new IOException("message")));
    when(() -> matcher.matches(new EOFException("message")));
    thenReturned(true);
  }

  @Test
  public void doesnt_match_exception_of_the_same_class_but_different_message() {
    given(() -> matcher = exception(new IOException("message")));
    when(() -> matcher.matches(new IOException("different")));
    thenReturned(false);
  }

  @Test
  public void doesnt_match_exception_of_subclass_but_different_message() {
    given(() -> matcher = exception(new IOException("message")));
    when(() -> matcher.matches(new EOFException("different")));
    thenReturned(false);
  }

  // matching with cause

  @Test
  public void matches_exception_of_the_same_class_and_same_cause() {
    given(() -> matcher = exception(new IOException("m", new NoSuchElementException())));
    when(() -> matcher.matches(new IOException("m", new NoSuchElementException())));
    thenReturned(true);
  }

  @Test
  public void matches_exception_of_the_same_class_and_subclass_cause() {
    given(() -> matcher = exception(new IOException("m", new RuntimeException())));
    when(() -> matcher.matches(new IOException("m", new NoSuchElementException())));
    thenReturned(true);
  }

  @Test
  public void doesnt_match_exception_of_the_same_class_and_different_cause() {
    given(() -> matcher = exception(new IOException("m", new NoSuchElementException())));
    when(() -> matcher.matches(new IOException("m", new IndexOutOfBoundsException())));
    thenReturned(false);
  }

  @Test
  public void matches_exception_of_the_same_class_and_same_cause_with_same_message() {
    given(() -> matcher = exception(new IOException("m", new NoSuchElementException("message"))));
    when(() -> matcher.matches(new IOException("m", new NoSuchElementException("message"))));
    thenReturned(true);
  }

  @Test
  public void doesnt_match_exception_of_the_same_class_and_same_cause_but_with_different_message() {
    given(() -> matcher = exception(new IOException("m", new NoSuchElementException("message"))));
    when(() -> matcher.matches(new IOException("m", new NoSuchElementException("different"))));
    thenReturned(false);
  }

  @Test
  public void matches_exception_of_the_same_class_and_cause_when_matcher_has_null_cause() {
    given(() -> matcher = exception(new IOException((Throwable) null)));
    when(() -> matcher.matches(new IOException(null, new NoSuchElementException("message"))));
    thenReturned(true);
  }

  // description

  @Test
  public void description_contains_chained_causes() {
    given(() -> matcher = exception(new IOException("m1", new NoSuchElementException("m2"))));
    given(() -> stringDescription = new StringDescription());
    when(() -> matcher.describeTo(stringDescription));
    thenEqual(stringDescription.toString(),
        "is instance of IOException(message='m1', cause=NoSuchElementException(message='m2')).");
  }
}