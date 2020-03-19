package org.smoothbuild.testing.common;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.Assert.fail;
import static org.smoothbuild.testing.common.AssertCall.assertCall;

import org.junit.Test;

public class AssertCallTest {
  @Test
  public void not_throws_anything_when_expected_exception_class() {
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
  public void throws_wrong_exception_when_expected_exception_class() {
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
  public void throws_correct_exception_when_expected_exception_class() {
    try {
      assertCall(() -> { throw new IllegalStateException("message"); })
          .throwsException(IllegalStateException.class);
    } catch (AssertionError e) {
      fail("assertCall() should NOT report failure, but reported:\n" + e.getMessage());
    }
  }

  @Test
  public void not_throws_anything_when_expected_exception_instance() {
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
  public void throws_wrong_exception_when_expected_exception_instance() {
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
  public void throws_exception_with_wrong_message_when_expected_exception_instance() {
    try {
      assertCall(() -> { throw new IllegalStateException("real message"); })
          .throwsException(new IllegalStateException("desired message"));
    } catch (AssertionError e) {
      assertThat(e.getMessage())
          .isEqualTo(
              "value of: thrownException.getMessage()\n" +
              "expected: desired message\n" +
              "but was : real message");
      return;
    }
    fail("assertCall() should report failure");
  }

  @Test
  public void throws_correct_exception_when_expected_exception_instance() {
    try {
      assertCall(() -> { throw new IllegalStateException("message"); })
          .throwsException(new IllegalStateException("message"));
    } catch (AssertionError e) {
      fail("assertCall() should NOT report failure, but reported:\n" + e.getMessage());
    }
  }
}
