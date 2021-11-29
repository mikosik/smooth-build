package org.smoothbuild.acceptance.lang;

import static com.google.common.truth.Truth.assertThat;
import static java.lang.String.format;

import java.io.IOException;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.smoothbuild.acceptance.AcceptanceTestCase;
import org.smoothbuild.acceptance.testing.StringIdentity;
import org.smoothbuild.acceptance.testing.ThrowException;

public class FuncTest extends AcceptanceTestCase {
  @Nested
  class param_default_argument {
    @Nested
    class _in_def_func {
      @Test
      public void is_used_when_param_has_no_value_assigned_in_call() throws Exception {
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
      public void is_ignored_when_param_is_assigned_in_a_call() throws Exception {
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
        createUserModule(format("""
          @Native("%s.func")
          Nothing throwException();
          func(String withDefault = throwException()) = withDefault;
          result = func("def");
          """, ThrowException.class.getCanonicalName()));
        runSmoothBuild("result");
        assertFinishedWithSuccess();
        assertThat(artifactFileContentAsString("result"))
            .isEqualTo("def");
      }
    }

    @Nested
    class _in_nat_func {
      @Test
      public void is_used_when_param_has_no_value_assigned_in_call() throws Exception {
        createNativeJar(StringIdentity.class);
        createUserModule(format("""
            @Native("%s")
            String stringIdentity(String value = "abc");
            result = stringIdentity();
            """, StringIdentity.class.getCanonicalName()));
        runSmoothBuild("result");
        assertFinishedWithSuccess();
        assertThat(artifactFileContentAsString("result"))
            .isEqualTo("abc");
      }

      @Test
      public void is_ignored_when_param_is_assigned_in_a_call() throws Exception {
        createNativeJar(StringIdentity.class);
        createUserModule(format("""
            @Native("%s")
            String stringIdentity(String value = "abc");
            result = stringIdentity("def");
            """, StringIdentity.class.getCanonicalName()));
        runSmoothBuild("result");
        assertFinishedWithSuccess();
        assertThat(artifactFileContentAsString("result"))
            .isEqualTo("def");
      }

      @Test
      public void is_not_evaluated_when_not_needed() throws Exception {
        createNativeJar(StringIdentity.class, ThrowException.class);
        createUserModule(format("""
            @Native("%s")
            String stringIdentity(String value = throwException());
            @Native("%s")
            Nothing throwException();
            result = stringIdentity("def");
            """, StringIdentity.class.getCanonicalName(), ThrowException.class.getCanonicalName()));
        runSmoothBuild("result");
        assertFinishedWithSuccess();
        assertThat(artifactFileContentAsString("result"))
            .isEqualTo("def");
      }
    }
  }

  @Nested
  class param_that_shadows {
    @Nested
    class imported {
      @Test
      public void value_makes_it_inaccessible() throws IOException {
        createUserModule("""
              String myFunc(String true) = true;
              result = myFunc("abc");
              """);
        runSmoothBuild("result");
        assertFinishedWithSuccess();
        assertThat(artifactFileContentAsString("result"))
            .isEqualTo("abc");
      }

      @Test
      public void func_makes_it_inaccessible() throws IOException {
        createUserModule("""
              String myFunc(String and) = and;
              result = myFunc("abc");
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
              String myFunc(String localValue) = localValue;
              result = myFunc("abc");
              """);
        runSmoothBuild("result");
        assertFinishedWithSuccess();
        assertThat(artifactFileContentAsString("result"))
            .isEqualTo("abc");
      }

      @Test
      public void func_makes_it_inaccessible() throws IOException {
        createUserModule("""
              localFunc() = true;
              String myFunc(String localFunc) = localFunc;
              result = myFunc("abc");
              """);
        runSmoothBuild("result");
        assertFinishedWithSuccess();
        assertThat(artifactFileContentAsString("result"))
            .isEqualTo("abc");
      }
    }
  }

  @Test
  public void calling_def_func_with_one_param() throws Exception {
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
  public void calling_def_func_that_returns_param() throws Exception {
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
  public void argument_is_not_evaluated_when_assigned_to_not_used_param() throws Exception {
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
  public void func_can_be_argument_to_other_func() throws Exception {
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
  public void func_can_be_result_of_other_func() throws Exception {
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
