package org.smoothbuild.acceptance.lang;

import static com.google.common.truth.Truth.assertThat;

import java.io.IOException;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.smoothbuild.acceptance.AcceptanceTestCase;
import org.smoothbuild.acceptance.testing.ThrowException;

import okio.ByteString;

public class ParameterTest extends AcceptanceTestCase {
  @Nested
  class parameter_of_type {
    @Test
    public void bool() throws Exception {
      createUserModule("""
            oneParameter(Bool bool) = "abc";
            """);
      runSmoothList();
      assertFinishedWithSuccess();
    }

    @Test
    public void string() throws Exception {
      createUserModule("""
            oneParameter(String string) = "abc";
            """);
      runSmoothList();
      assertFinishedWithSuccess();
    }

    @Test
    public void blob() throws Exception {
      createUserModule("""
            oneParameter(Blob blob) = "abc";
            """);
      runSmoothList();
      assertFinishedWithSuccess();
    }

    @Test
    public void file() throws Exception {
      createUserModule("""
            oneParameter(File file) = "abc";
            """);
      runSmoothList();
      assertFinishedWithSuccess();
    }

    @Test
    public void nothing() throws Exception {
      createUserModule("""
            oneParameter(Nothing nothing) = "abc";
            """);
      runSmoothList();
      assertFinishedWithSuccess();
    }

    @Test
    public void value() throws Exception {
      createUserModule("""
            oneParameter(Value value) = "abc";
            """);
      runSmoothList();
      assertFinishedWithError();
      assertSysOutContainsParseError(1, "Undefined type 'Value'.\n");
    }

    @Test
    public void generic() throws Exception {
      createUserModule("""
            oneParameter(A param) = "abc";
            """);
      runSmoothList();
      assertFinishedWithSuccess();
    }

    @Test
    public void undefined() throws Exception {
      createUserModule("""
            oneParameter(Undefined undefined) = "abc";
            """);
      runSmoothList();
      assertFinishedWithError();
      assertSysOutContainsParseError(1, "Undefined type 'Undefined'.\n");
    }

    @Test
    public void bool_array() throws Exception {
      createUserModule("""
            oneParameter([Bool] array) = "abc";
            """);
      runSmoothList();
      assertFinishedWithSuccess();
    }

    @Test
    public void string_array() throws Exception {
      createUserModule("""
            oneParameter([String] array) = "abc";
            """);
      runSmoothList();
      assertFinishedWithSuccess();
    }

    @Test
    public void blob_array() throws Exception {
      createUserModule("""
            oneParameter([Blob] array) = "abc";
            """);
      runSmoothList();
      assertFinishedWithSuccess();
    }

    @Test
    public void file_array() throws Exception {
      createUserModule("""
            oneParameter([File] array) = "abc";
            """);
      runSmoothList();
      assertFinishedWithSuccess();
    }

    @Test
    public void nothing_array() throws Exception {
      createUserModule("""
            oneParameter([Nothing] array) = "abc";
            """);
      runSmoothList();
      assertFinishedWithSuccess();
    }

    @Test
    public void generic_array() throws Exception {
      createUserModule("""
            oneParameter([A] array) = "abc";
            """);
      runSmoothList();
      assertFinishedWithSuccess();
    }

    @Test
    public void array_of_unknown_type() throws Exception {
      createUserModule("""
            oneParameter([Undefined] param) = "abc";
            """);
      runSmoothList();
      assertFinishedWithError();
      assertSysOutContainsParseError(1, "Undefined type 'Undefined'.\n");
    }

    @Test
    public void string_array2() throws Exception {
      createUserModule("""
            oneParameter([[String]] array) = "abc";
            """);
      runSmoothList();
      assertFinishedWithSuccess();
    }
  }

  @Nested
  class default_value {
    @Nested
    class expression_defined_as {
      @Test
      public void string_literal() throws Exception {
        createUserModule("""
            oneParameter(String value = "abc") = value;
            result = oneParameter();
            """);
        runSmoothBuild("result");
        assertFinishedWithSuccess();
        assertThat(artifactFileContentAsString("result"))
            .isEqualTo("abc");
      }

      @Test
      public void blob_literal() throws Exception {
        createUserModule("""
            oneParameter(Blob value = 0xAB) = value;
            result = oneParameter();
            """);
        runSmoothBuild("result");
        assertFinishedWithSuccess();
        assertThat(artifactFileContent("result"))
            .isEqualTo(ByteString.of((byte) 0xAB));
      }

      @Test
      public void field_read() throws Exception {
        createUserModule("""
            MyStruct {
              String field,
            }
            value = myStruct("abc");
            oneParameter(String value = value.field) = value;
            result = oneParameter();
            """);
        runSmoothBuild("result");
        assertFinishedWithSuccess();
        assertThat(artifactFileContentAsString("result"))
            .isEqualTo("abc");
      }

      @Test
      public void call() throws Exception {
        createUserModule("""
                myFunction() = "abc";
                otherFunction(String value = myFunction()) = value;
                result = otherFunction();
                """);
        runSmoothBuild("result");
        assertFinishedWithSuccess();
        assertThat(artifactFileContentAsString("result"))
            .isEqualTo("abc");
      }

      @Test
      public void pipe() throws Exception {
        createUserModule("""
            oneParameter(String value = true | if("abc", "def")) = value;
            result = oneParameter();
            """);
        runSmoothBuild("result");
        assertFinishedWithSuccess();
        assertThat(artifactFileContentAsString("result"))
            .isEqualTo("abc");
      }
    }

    @Test
    public void with_type_not_assignable_to_declared_parameter_type_causes_error() throws Exception {
      createUserModule("""
          func([String] withDefault = "abc") = withDefault;
          result = func();
          """);
      runSmoothBuild("result");
      assertFinishedWithError();
      assertSysOutContainsParseError(1, "Parameter 'withDefault' is of type '[String]' so"
          + " it cannot have default value of type 'String'.");
    }

    @Test
    public void can_have_type_convertible_to_declared_parameter_type() throws Exception {
      createUserModule("""
          func(Blob param = file(toBlob("abc"), "file.txt")) = param;
          result = func();
          """);
      runSmoothBuild("result");
      assertFinishedWithSuccess();
      assertThat(artifactFileContentAsString("result"))
          .isEqualTo("abc");
    }

    @Test
    public void with_declared_generic_type_causes_error() throws Exception {
      createUserModule("""
              A testIdentity(A value = 'aaa') = value;
              """);
      runSmoothList();
      assertFinishedWithError();
      assertSysOutContainsParseError(
          1, "Parameter 'value' is of type 'A' so it cannot have default value of type 'String'.");
    }

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
  public void no_parameters() throws Exception {
    createUserModule("""
            noParameters() = "abc";
            """);
    runSmoothList();
    assertFinishedWithSuccess();
  }

  @Test
  public void it_is_possible_to_declare_parameter_with_trailing_comma() throws Exception {
    createUserModule("""
            myFunction(String string, ) = string;
            """);
    runSmoothList();
    assertFinishedWithSuccess();
  }

  @Test
  public void two_parameters_with_same_name_causes_error() throws Exception {
    createUserModule("""
            twoParameters(
                String name1,
                String name1
            ) = "abc";
            """);
    runSmoothList();
    assertFinishedWithError();
    assertSysOutContainsParseError(3, "'name1' is already defined at build.smooth:2.\n");
  }

  @Test
  public void default_parameter_before_non_default_causes_error() throws Exception {
    createUserModule("""
            defaultBeforeNonDefault(
                String default = 'value',
                String nonDefault
            ) = "abc";
            """);
    runSmoothList();
    assertFinishedWithError();
    assertSysOutContainsParseError(3, "parameter with default value must be placed after all parameters " +
        "which don't have default value.\n");
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
  public void defined_function_that_returns_parameter() throws Exception {
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
            Nothing throwException();
            func(String notUsedParameter) = "abc";
            result = func(throwException());
            """);
    runSmoothBuild("result");
    assertFinishedWithSuccess();
    assertThat(artifactFileContentAsString("result"))
        .isEqualTo("abc");
  }
}
