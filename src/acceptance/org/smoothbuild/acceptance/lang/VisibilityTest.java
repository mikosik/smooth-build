package org.smoothbuild.acceptance.lang;

import static com.google.common.truth.Truth.assertThat;

import java.io.IOException;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.smoothbuild.acceptance.AcceptanceTestCase;

public class VisibilityTest extends AcceptanceTestCase {
  @Nested
  class reference {
    @Nested
    class to_local {
      @Test
      public void value_succeeds() throws IOException {
        createUserModule("""
          String myValue = "abc";
          result = myValue;
          """);
        runSmoothBuild("result");
        assertFinishedWithSuccess();
        assertThat(artifactFileContentAsString("result"))
            .isEqualTo("abc");
      }

      @Test
      public void function_causes_error() throws IOException {
        createUserModule("""
          String myFunction() = "abc";
          result = myFunction;
          """);
        runSmoothList();
        assertFinishedWithError();
        assertSysOutContainsParseError(
            2, "'myFunction' is a function and cannot be accessed as a value.");
      }
    }

    @Nested
    class to_imported {
      @Test
      public void value_succeeds() throws IOException {
        createUserModule("""
          result = true;
          """);
        runSmoothBuild("result");
        assertFinishedWithSuccess();
        assertThat(artifactAsBoolean("result"))
            .isEqualTo(true);
      }

      @Test
      public void function_causes_error() throws IOException {
        createUserModule("""
          result = and;
          """);
        runSmoothList();
        assertFinishedWithError();
        assertSysOutContainsParseError(
            1, "'and' is a function and cannot be accessed as a value.");
      }
    }
  }

  @Nested
  class call {
    @Nested
    class to_local {
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
    class to_imported {
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
    class to_undefined_function {
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
  }
}
