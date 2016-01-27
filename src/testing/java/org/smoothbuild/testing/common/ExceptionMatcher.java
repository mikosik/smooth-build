package org.smoothbuild.testing.common;

import java.util.Objects;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

public class ExceptionMatcher extends TypeSafeMatcher<Throwable> {
  private final Throwable throwable;

  public static Matcher<Throwable> exception(Throwable throwable) {
    return new ExceptionMatcher(throwable);
  }

  private ExceptionMatcher(Throwable throwable) {
    this.throwable = throwable;
  }

  public void describeTo(Description description) {
    description.appendText("is instance of " + throwable.getClass().getSimpleName()
        + " with message '"
        + throwable.getMessage() + "'.");
  }

  protected boolean matchesSafely(Throwable item) {
    return throwable.getClass().isInstance(item)
        && Objects.equals(item.getMessage(), throwable.getMessage());
  }
}
