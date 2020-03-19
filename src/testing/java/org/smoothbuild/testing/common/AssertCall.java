package org.smoothbuild.testing.common;

import static com.google.common.truth.Fact.fact;
import static com.google.common.truth.Fact.simpleFact;
import static com.google.common.truth.Truth.assertAbout;

import com.google.common.truth.FailureMetadata;
import com.google.common.truth.Subject;
import com.google.common.truth.Subject.Factory;

public class AssertCall {
  public static ThrownExceptionSubject assertCall(ThrowingRunnable throwingRunnable) {
    return assertAbout(thrownExceptions()).that(runRunnable(throwingRunnable));
  }

  private static Throwable runRunnable(ThrowingRunnable throwingRunnable) {
    try {
      throwingRunnable.run();
    } catch (Throwable actual) {
      return actual;
    }
    return null;
  }

  private static Factory<ThrownExceptionSubject, Throwable> thrownExceptions() {
    return ThrownExceptionSubject::new;
  }

  public static class ThrownExceptionSubject extends Subject {
    private final Object actual;

    public ThrownExceptionSubject(FailureMetadata metadata, Object actual) {
      super(metadata, actual);
      this.actual = actual;
    }

    public void throwsException(Throwable expected) {
      if (actual == null) {
        failWithoutActual(simpleFact("expected call to throw exception"));
      }
      String expectedClassName = expected.getClass().getCanonicalName();
      if (actual.getClass() != expected.getClass()) {
        failWithActual(fact("expected call to throw", expectedClassName));
      }
      Throwable actualThrowable = (Throwable) this.actual;
      check("getMessage()").that(actualThrowable.getMessage()).isEqualTo(expected.getMessage());
    }

    public void throwsException(Class<? extends Throwable> expected) {
      if (actual == null) {
        failWithoutActual(simpleFact("expected call to throw exception"));
      }
      if (actual.getClass() != expected) {
        failWithActual(fact("expected call to throw", expected.getCanonicalName()));
      }
    }
  }

  public interface ThrowingRunnable {
    public void run() throws Throwable;
  }
}


