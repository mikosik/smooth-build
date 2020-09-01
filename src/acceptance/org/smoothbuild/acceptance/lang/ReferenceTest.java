package org.smoothbuild.acceptance.lang;

import static com.google.common.truth.Truth.assertThat;

import java.io.IOException;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.smoothbuild.acceptance.AcceptanceTestCase;

public class ReferenceTest extends AcceptanceTestCase {
  @Nested
  class reference_to_local {
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
  class reference_to_imported {
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
