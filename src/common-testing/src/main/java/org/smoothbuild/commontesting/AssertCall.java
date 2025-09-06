package org.smoothbuild.commontesting;

import static com.google.common.truth.Fact.fact;
import static com.google.common.truth.Fact.simpleFact;
import static com.google.common.truth.Truth.assertAbout;
import static java.util.Arrays.asList;
import static java.util.Objects.requireNonNull;

import com.google.common.truth.Fact;
import com.google.common.truth.FailureMetadata;
import com.google.common.truth.Subject;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import org.jspecify.annotations.Nullable;

public class AssertCall {
  public static ThrownExceptionSubject assertCall(ThrowingRunnable runnable) {
    return assertAbout(thrownExceptions()).that(runRunnable(runnable));
  }

  @Nullable
  private static Throwable runRunnable(ThrowingRunnable runnable) {
    try {
      runnable.run();
    } catch (Throwable actual) {
      return actual;
    }
    return null;
  }

  private static Subject.Factory<ThrownExceptionSubject, Throwable> thrownExceptions() {
    return ThrownExceptionSubject::new;
  }

  public static class ThrownExceptionSubject extends Subject {
    private final FailureMetadata metadata;
    private final @Nullable Object actual;

    public ThrownExceptionSubject(FailureMetadata metadata, @Nullable Object actual) {
      super(metadata, actual);
      this.metadata = metadata;
      this.actual = actual;
    }

    public ExceptionCauseSubject throwsException(Throwable expected) {
      String expectedClassName = expected.getClass().getCanonicalName();
      if (actual == null) {
        failWithoutActual(
            fact("expected call to throw", expectedClassName), fact("but was", "nothing thrown"));
      } else if (actual.getClass() != expected.getClass()) {
        failWithoutActual(
            fact("expected call to throw", expectedClassName),
            fact("but was", actual.getClass().getCanonicalName()));
      } else {
        String actualMessage = ((Throwable) actual).getMessage();
        if (!Objects.equals(actualMessage, expected.getMessage())) {
          failWithoutActual(
              fact("expected call to throw", expectedClassName),
              fact("with message", expected.getMessage()),
              fact("but was message", actualMessage));
        }
      }
      var nonNullActual = requireNonNull(actual);
      return new ExceptionCauseSubject(
          metadata,
          nonNullActual,
          List.of(
              fact("expected call to throw", expectedClassName),
              fact("with message", expected.getMessage())));
    }

    public ExceptionCauseSubject throwsException(Class<? extends Throwable> expected) {
      if (actual == null) {
        failWithoutActual(
            fact("expected call to throw", expected.getCanonicalName()),
            fact("but was", "nothing thrown"));
      } else if (actual.getClass() != expected) {
        failWithActual(fact("expected call to throw", expected.getCanonicalName()));
      }
      var nonNullActual = requireNonNull(actual);
      return new ExceptionCauseSubject(
          metadata,
          nonNullActual,
          List.of(fact("expected call to throw", expected.getCanonicalName())));
    }
  }

  public static class ExceptionCauseSubject extends Subject {
    private final Object actual;
    private final List<Fact> facts;

    public ExceptionCauseSubject(FailureMetadata metadata, Object actual, List<Fact> facts) {
      super(metadata, actual);
      this.actual = actual;
      this.facts = facts;
    }

    public void withCause(Throwable expectedCause) {
      Throwable actualCause = ((Throwable) actual).getCause();
      String expectedCauseName = expectedCause.getClass().getCanonicalName();
      if (actualCause == null) {
        callFailWithoutActual(
            facts,
            fact("with cause", expectedCauseName),
            simpleFact("but was exception without cause"));
      } else if (!Objects.equals(actualCause.getClass(), expectedCause.getClass())) {
        callFailWithoutActual(
            facts,
            fact("with cause", expectedCauseName),
            fact("but was cause", actualCause.getClass().getCanonicalName()));
      } else if (!Objects.equals(actualCause.getMessage(), expectedCause.getMessage())) {
        callFailWithoutActual(
            facts,
            fact("with cause", expectedCauseName),
            fact("with message", expectedCause.getMessage()),
            fact("but was message", actualCause.getMessage()));
      }
    }

    public void withCause(Class<? extends Throwable> expectedCause) {
      Throwable actualCause = ((Throwable) actual).getCause();
      String expectedCauseName = expectedCause.getCanonicalName();
      if (actualCause == null) {
        callFailWithoutActual(
            facts,
            fact("with cause", expectedCauseName),
            simpleFact("but was exception without cause"));
      } else if (!actualCause.getClass().equals(expectedCause)) {
        callFailWithoutActual(
            facts,
            fact("with cause", expectedCauseName),
            fact("but was cause", actualCause.getClass().getCanonicalName()));
      }
    }

    private void callFailWithoutActual(List<Fact> facts1, Fact... facts2) {
      Fact first = facts1.get(0);
      ArrayList<Fact> allWithoutFirst = new ArrayList<>();
      allWithoutFirst.addAll(facts1.subList(1, facts1.size()));
      allWithoutFirst.addAll(asList(facts2));
      failWithoutActual(first, allWithoutFirst.toArray(Fact[]::new));
    }
  }
}
