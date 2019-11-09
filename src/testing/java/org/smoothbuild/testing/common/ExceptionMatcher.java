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

  @Override
  public void describeTo(Description description) {
    description.appendText("is instance of " + describeExpected(throwable) + ".");
  }

  private String describeExpected(Throwable expected) {
    return expected.getClass().getSimpleName()
        + "(message='" + expected.getMessage() + "'"
        + (expected.getCause() == null ? "" : ", cause=" + describeExpected(expected.getCause()))
        + ")";
  }

  @Override
  protected boolean matchesSafely(Throwable actual) {
    return isMatching(actual, this.throwable);
  }

  private boolean isMatching(Throwable actual, Throwable expected) {
    return expected.getClass().isInstance(actual)
        && Objects.equals(actual.getMessage(), expected.getMessage())
        && (expected.getCause() == null || isMatching(actual.getCause(), expected.getCause()));
  }
}
