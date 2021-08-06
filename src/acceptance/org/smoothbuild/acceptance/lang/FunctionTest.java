package org.smoothbuild.acceptance.lang;

import static com.google.common.truth.Truth.assertThat;

import java.io.IOException;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.smoothbuild.acceptance.AcceptanceTestCase;
import org.smoothbuild.acceptance.testing.ThrowException;

public class FunctionTest extends AcceptanceTestCase {
  @Nested
  class parameter_default_value {
    @Test
    public void is_used_when_parameter_has_no_value_assigned_in_call() throws Exception {
      createUserModule("""
          func(String withDefault = "abc") = withDefault;
          result = func();
          """);
      runSmoothBuild("result");
      assertFinishedWithSuccess();
      assertThat(artifactFileContentAsString("result"))
          .isEqualTo("abc");
    }

    @Test
    public void is_ignored_when_parameter_is_assigned_in_a_call() throws Exception {
      createUserModule("""
              func(String withDefault = "abc") = withDefault;
              result = func("def");
              """);
      runSmoothBuild("result");
      assertFinishedWithSuccess();
      assertThat(artifactFileContentAsString("result"))
          .isEqualTo("def");
    }

    @Test
    public void is_not_evaluated_when_not_needed() throws Exception {
      createNativeJar(ThrowException.class);
      createUserModule("""
          @Native("impl")
          Nothing throwException();
          func(String withDefault = throwException()) = withDefault;
          result = func("def");
          """);
      runSmoothBuild("result");
      assertFinishedWithSuccess();
      assertThat(artifactFileContentAsString("result"))
          .isEqualTo("def");
    }
  }

  @Nested
  class parameter_that_shadows {
    @Nested
    class imported {
      @Test
      public void value_makes_it_inaccessible() throws IOException {
        createUserModule("""
              String myFunction(String true) = true;
              result = myFunction("abc");
              """);
        runSmoothBuild("result");
        assertFinishedWithSuccess();
        assertThat(artifactFileContentAsString("result"))
            .isEqualTo("abc");
      }

      @Test
      public void function_makes_it_inaccessible() throws IOException {
        createUserModule("""
              String myFunction(String and) = and;
              result = myFunction("abc");
              """);
        runSmoothBuild("result");
        assertFinishedWithSuccess();
        assertThat(artifactFileContentAsString("result"))
            .isEqualTo("abc");
      }
    }

    @Nested
    class local {
      @Test
      public void value_makes_it_inaccessible() throws IOException {
        createUserModule("""
              localValue = true;
              String myFunction(String localValue) = localValue;
              result = myFunction("abc");
              """);
        runSmoothBuild("result");
        assertFinishedWithSuccess();
        assertThat(artifactFileContentAsString("result"))
            .isEqualTo("abc");
      }

      @Test
      public void function_makes_it_inaccessible() throws IOException {
        createUserModule("""
              localFunction() = true;
              String myFunction(String localFunction) = localFunction;
              result = myFunction("abc");
              """);
        runSmoothBuild("result");
        assertFinishedWithSuccess();
        assertThat(artifactFileContentAsString("result"))
            .isEqualTo("abc");
      }
    }
  }

  @Test
  public void calling_defined_function_with_one_parameter() throws Exception {
    createUserModule("""
            func(String string) = "abc";
            result = func("def");
            """);
    runSmoothBuild("result");
    assertFinishedWithSuccess();
    assertThat(artifactFileContentAsString("result"))
        .isEqualTo("abc");
  }

  @Test
  public void calling_defined_function_that_returns_parameter() throws Exception {
    createUserModule("""
            func(String string) = string;
            result = func("abc");
            """);
    runSmoothBuild("result");
    assertFinishedWithSuccess();
    assertThat(artifactFileContentAsString("result"))
        .isEqualTo("abc");
  }

  @Test
  public void argument_is_not_evaluated_when_assigned_to_not_used_parameter() throws Exception {
    createNativeJar(ThrowException.class);
    createUserModule("""
            @Native("impl")
            Nothing throwException();
            func(String notUsedParameter) = "abc";
            result = func(throwException());
            """);
    runSmoothBuild("result");
    assertFinishedWithSuccess();
    assertThat(artifactFileContentAsString("result"))
        .isEqualTo("abc");
  }

  @Test
  public void function_can_be_argument_to_other_function() throws Exception {
    createNativeJar(ThrowException.class);
    createUserModule("""
            String returnAbc() = "abc";
            A invokeProducer(A() producer) = producer();
            result = invokeProducer(returnAbc);
            """);
    runSmoothBuild("result");
    assertFinishedWithSuccess();
    assertThat(artifactFileContentAsString("result"))
        .isEqualTo("abc");
  }

  @Test
  public void function_can_be_result_of_other_function() throws Exception {
    createNativeJar(ThrowException.class);
    createUserModule("""
            String returnAbc() = "abc";
            String() createProducer() = returnAbc;
            result = createProducer()();
            """);
    runSmoothBuild("result");
    assertFinishedWithSuccess();
    assertThat(artifactFileContentAsString("result"))
        .isEqualTo("abc");
  }
}
