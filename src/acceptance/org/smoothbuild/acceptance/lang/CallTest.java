package org.smoothbuild.acceptance.lang;

import static com.google.common.truth.Truth.assertThat;

import java.io.IOException;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.smoothbuild.acceptance.AcceptanceTestCase;

public class CallTest extends AcceptanceTestCase {
  @Nested
  class call_to_local {
    @Test
    public void value_causes_error() throws IOException {
      createUserModule("""
          String myValue = "abc";
          result = myValue();
          """);
      runSmoothList();
      assertFinishedWithError();
      assertSysOutContainsParseError(2, "`myValue` cannot be called as it is a value.");
    }

    @Test
    public void function_succeeds() throws IOException {
      createUserModule("""
          String myFunction() = "abc";
          result = myFunction();
          """);
      runSmoothBuild("result");
      assertFinishedWithSuccess();
      assertThat(artifactFileContentAsString("result"))
          .isEqualTo("abc");
    }
  }

  @Nested
  class call_to_imported {
    @Test
    public void value_causes_error() throws IOException {
      createUserModule("""
          result = true();
          """);
      runSmoothList();
      assertFinishedWithError();
      assertSysOutContainsParseError(1, "`true` cannot be called as it is a value.");
    }

    @Test
    public void function_succeeds() throws IOException {
      createUserModule("""
          result = and(true, true);
          """);
      runSmoothBuild("result");
      assertFinishedWithSuccess();
      assertThat(artifactAsBoolean("result"))
          .isEqualTo(true);
    }
  }

  @Nested
  class call_to_undefined_function {
    @Test
    public void without_arguments_causes_error() throws IOException {
      createUserModule("""
          function1 = undefinedFunction();
          """);
      runSmoothList();
      assertFinishedWithError();
      assertSysOutContainsParseError(1, "'undefinedFunction' is undefined.");
    }

    @Test
    public void with_argument_causes_error() throws IOException {
      createUserModule("""
          function1 = undefinedFunction("a");
          """);
      runSmoothList();
      assertFinishedWithError();
      assertSysOutContainsParseError(1, "'undefinedFunction' is undefined.");
    }
  }

  @Test
  public void call_without_parentheses_inside_pipe_is_allowed() throws IOException {
    createUserModule("""
          myIdentity(A value) = value;
          result = "abc" | myIdentity;
          """);
    runSmoothBuild("result");
    assertFinishedWithSuccess();
    assertThat(artifactFileContentAsString("result"))
        .isEqualTo("abc");
  }

  @Test
  public void call_to_parameter_causes_error() throws IOException {
    createUserModule("""
          myFunction(String value) = value();
          """);
    runSmoothList();
    assertFinishedWithError();
    assertSysOutContainsParseError(
        1, "Parameter `value` cannot be called as it is not a function.");
  }
}
