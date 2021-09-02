package org.smoothbuild.testing.common;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.jupiter.api.Assertions.fail;
import static org.smoothbuild.testing.common.AssertCall.assertCall;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

public class AssertCallTest {
  @Nested
  class expected_exception_by_class {
    @Test
    public void when_nothing_is_thrown() {
      try {
        assertCall(() -> {})
            .throwsException(IllegalStateException.class);
      } catch (AssertionError e) {
        assertThat(e.getMessage())
            .isEqualTo("expected call to throw exception");
        return;
      }
      fail("assertCall() should report failure");
    }

    @Test
    public void when_thrown_exception_with_different_type() {
      try {
        assertCall(() -> { throw new RuntimeException(); })
            .throwsException(IllegalStateException.class);
      } catch (AssertionError e) {
        assertThat(e.getMessage())
            .isEqualTo(
                "expected call to throw: java.lang.IllegalStateException\n" +
                    "but was               : java.lang.RuntimeException");
        return;
      }
      fail("assertCall() should report failure");
    }

    @Test
    public void when_thrown_exception_with_same_type() {
      try {
        assertCall(() -> { throw new IllegalStateException("message"); })
            .throwsException(IllegalStateException.class);
      } catch (AssertionError e) {
        fail("assertCall() should NOT report failure, but reported:\n" + e.getMessage());
      }
    }
  }

  @Nested
  class expected_exception_by_instance {
    @Test
    public void when_nothing_is_thrown() {
      try {
        assertCall(() -> {})
            .throwsException(new IllegalStateException("desired message"));
      } catch (AssertionError e) {
        assertThat(e.getMessage())
            .isEqualTo("expected call to throw exception");
        return;
      }
      fail("assertCall() should report failure");
    }

    @Test
    public void when_thrown_exception_with_different_type() {
      try {
        assertCall(() -> { throw new RuntimeException(); })
            .throwsException(new IllegalStateException("desired message"));
      } catch (AssertionError e) {
        assertThat(e.getMessage())
            .isEqualTo(
                "expected call to throw: java.lang.IllegalStateException\n" +
                    "but was               : java.lang.RuntimeException");
        return;
      }
      fail("assertCall() should report failure");
    }

    @Test
    public void when_thrown_exception_with_same_type_but_different_message() {
      try {
        assertCall(() -> { throw new IllegalStateException("real message"); })
            .throwsException(new IllegalStateException("desired message"));
      } catch (AssertionError e) {
        assertThat(e.getMessage())
            .isEqualTo(
                """
                    value of: thrownException.getMessage()
                    expected: desired message
                    but was : real message""");
        return;
      }
      fail("assertCall() should report failure");
    }

    @Test
    public void when_thrown_same_exception_with_same_message() {
      try {
        assertCall(() -> { throw new IllegalStateException("message"); })
            .throwsException(new IllegalStateException("message"));
      } catch (AssertionError e) {
        fail("assertCall() should NOT report failure, but reported:\n" + e.getMessage());
      }
    }
  }

  @Nested
  class expected_exception_by_class_with_cause_by_class {
    @Test
    public void when_thrown_same_exception_but_without_cause() {
      try {
        assertCall(() -> { throw new IllegalStateException("main message"); })
            .throwsException(IllegalStateException.class)
            .withCause(IllegalArgumentException.class);
      } catch (AssertionError e) {
        assertThat(e.getMessage())
            .isEqualTo(
                """
                    expected call to throw: java.lang.IllegalStateException
                    with cause            : java.lang.IllegalArgumentException
                    but was exception without cause""");
        return;
      }
      fail("assertCall() should report failure");
    }

    @Test
    public void when_thrown_same_exception_but_with_different_cause() {
      try {
        assertCall(() -> {
          throw new IllegalStateException("main message",
              new ArithmeticException("cause message"));
        })
            .throwsException(IllegalStateException.class)
            .withCause(IllegalArgumentException.class);
      } catch (AssertionError e) {
        assertThat(e.getMessage())
            .isEqualTo(
                """
                    expected call to throw: java.lang.IllegalStateException
                    with cause            : java.lang.IllegalArgumentException
                    but was cause         : java.lang.ArithmeticException""");
        return;
      }
      fail("assertCall() should report failure");
    }


    @Test
    public void when_thrown_exception_with_same_type_and_cause_with_same_type() {
      try {
        assertCall(() -> {
          throw new IllegalStateException("main message",
              new IllegalArgumentException("cause message"));
        })
            .throwsException(IllegalStateException.class)
            .withCause(IllegalArgumentException.class);
      } catch (AssertionError e) {
        fail("assertCall() should NOT report failure, but reported:\n" + e.getMessage());
      }
    }
  }

  @Nested
  class expected_exception_by_instance_with_cause_by_class {
    @Test
    public void when_thrown_same_exception_but_without_cause() {
      try {
        assertCall(() -> {
          throw new IllegalStateException("main message");
        })
            .throwsException(new IllegalStateException("main message"))
            .withCause(IllegalArgumentException.class);
      } catch (AssertionError e) {
        assertThat(e.getMessage())
            .isEqualTo(
                """
                    expected call to throw: java.lang.IllegalStateException
                    with message          : main message
                    with cause            : java.lang.IllegalArgumentException
                    but was exception without cause""");
        return;
      }
      fail("assertCall() should report failure");
    }

    @Test
    public void when_thrown_same_exception_but_with_different_cause() {
      try {
        assertCall(() -> {
          throw new IllegalStateException("main message",
              new ArithmeticException("cause message"));
        })
            .throwsException(new IllegalStateException("main message"))
            .withCause(IllegalArgumentException.class);
      } catch (AssertionError e) {
        assertThat(e.getMessage())
            .isEqualTo(
                """
                    expected call to throw: java.lang.IllegalStateException
                    with message          : main message
                    with cause            : java.lang.IllegalArgumentException
                    but was cause         : java.lang.ArithmeticException""");
        return;
      }
      fail("assertCall() should report failure");
    }

    @Test
    public void when_thrown_same_exception_and_cause_with_same_type() {
      try {
        assertCall(() -> {
          throw new IllegalStateException("main message",
              new IllegalArgumentException("cause message"));
        })
            .throwsException(new IllegalStateException("main message"))
            .withCause(IllegalArgumentException.class);
      } catch (AssertionError e) {
        fail("assertCall() should NOT report failure, but reported:\n" + e.getMessage());
      }
    }
  }

  @Nested
  class expected_exception_by_class_with_cause_by_instance {
    @Test
    public void when_thrown_exception_with_same_type_but_without_cause() {
      try {
        assertCall(() -> {
          throw new IllegalStateException("main message");
        })
            .throwsException(IllegalStateException.class)
            .withCause(new IllegalArgumentException("cause message"));
      } catch (AssertionError e) {
        assertThat(e.getMessage())
            .isEqualTo(
                """
                    expected call to throw: java.lang.IllegalStateException
                    with cause            : java.lang.IllegalArgumentException
                    but was no cause""");
        return;
      }
      fail("assertCall() should report failure");
    }

    @Test
    public void when_thrown_exception_with_same_type_but_cause_with_different_type() {
      try {
        assertCall(() -> {
          throw new IllegalStateException("main message",
              new ArithmeticException("cause message"));
        })
            .throwsException(IllegalStateException.class)
            .withCause(new IllegalArgumentException("cause message"));
      } catch (AssertionError e) {
        assertThat(e.getMessage())
            .isEqualTo(
                """
                    expected call to throw: java.lang.IllegalStateException
                    with cause            : java.lang.IllegalArgumentException
                    but was cause         : java.lang.ArithmeticException""");
        return;
      }
      fail("assertCall() should report failure");
    }

    @Test
    public void when_thrown_exception_with_same_type_and_cause_with_same_type_but_different_message() {
      try {
        assertCall(() -> {
          throw new IllegalStateException("main message",
              new IllegalArgumentException("wrong message"));
        })
            .throwsException(IllegalStateException.class)
            .withCause(new IllegalArgumentException("cause message"));
      } catch (AssertionError e) {
        assertThat(e.getMessage())
            .isEqualTo(
                """
                    expected call to throw: java.lang.IllegalStateException
                    with cause            : java.lang.IllegalArgumentException
                    with message          : cause message
                    but was message       : wrong message""");
        return;
      }
      fail("assertCall() should report failure");
    }

    @Test
    public void when_thrown_exception_with_same_type_and_same_cause() {
      try {
        assertCall(() -> {
          throw new IllegalStateException("main message",
              new IllegalArgumentException("cause message"));
        })
            .throwsException(IllegalStateException.class)
            .withCause(new IllegalArgumentException("cause message"));
      } catch (AssertionError e) {
        fail("assertCall() should NOT report failure, but reported:\n" + e.getMessage());
      }
    }
  }

  @Nested
  class expected_exception_by_instance_with_cause_by_instance {
    @Test
    public void when_thrown_exception_without_cause() {
      try {
        assertCall(() -> {
          throw new IllegalStateException("main message");
        })
            .throwsException(new IllegalStateException("main message"))
            .withCause(new IllegalArgumentException("cause message"));
      } catch (AssertionError e) {
        assertThat(e.getMessage())
            .isEqualTo(
                """
                    expected call to throw: java.lang.IllegalStateException
                    with message          : main message
                    with cause            : java.lang.IllegalArgumentException
                    but was no cause""");
        return;
      }
      fail("assertCall() should report failure");
    }

    @Test
    public void when_thrown_exception_with_cause_with_different_type() {
      try {
        assertCall(() -> {
          throw new IllegalStateException("main message",
              new ArithmeticException("cause message"));
        })
            .throwsException(new IllegalStateException("main message"))
            .withCause(new IllegalArgumentException("cause message"));
      } catch (AssertionError e) {
        assertThat(e.getMessage())
            .isEqualTo(
                "expected call to throw: java.lang.IllegalStateException\n" +
                "with message          : main message\n" +
                "with cause            : java.lang.IllegalArgumentException\n" +
                "but was cause         : java.lang.ArithmeticException");
        return;
      }
      fail("assertCall() should report failure");
    }

    @Test
    public void when_thrown_exception_with_cause_with_same_type_but_different_message() {
      try {
        assertCall(() -> {
          throw new IllegalStateException("main message",
              new IllegalArgumentException("wrong message"));
        })
            .throwsException(new IllegalStateException("main message"))
            .withCause(new IllegalArgumentException("cause message"));
      } catch (AssertionError e) {
        assertThat(e.getMessage())
            .isEqualTo(
                """
                    expected call to throw: java.lang.IllegalStateException
                    with message          : main message
                    with cause            : java.lang.IllegalArgumentException
                    with message          : cause message
                    but was message       : wrong message""");
        return;
      }
      fail("assertCall() should report failure");
    }

    @Test
    public void when_thrown_same_exception_with_same_cause() {
      try {
        assertCall(() -> {
          throw new IllegalStateException("main message",
              new IllegalArgumentException("cause message"));
        })
            .throwsException(new IllegalStateException("main message"))
            .withCause(new IllegalArgumentException("cause message"));
      } catch (AssertionError e) {
        fail("assertCall() should NOT report failure, but reported:\n" + e.getMessage());
      }
    }
  }
}
