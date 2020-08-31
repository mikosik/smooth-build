package org.smoothbuild.acceptance.lang;

import static com.google.common.truth.Truth.assertThat;

import java.io.IOException;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.smoothbuild.acceptance.AcceptanceTestCase;
import org.smoothbuild.acceptance.testing.GenericResult;
import org.smoothbuild.acceptance.testing.ReportError;

public class FunctionTest extends AcceptanceTestCase {
  @Nested
  class function_name {
    @Test
    public void that_is_illegal_causes_error() throws Exception {
      createUserModule("""
              function^() = "abc";
              """);
      runSmoothList();
      assertFinishedWithError();
      assertSysOutContainsParseError(1, "token recognition error at: '^'");
    }

    @Test
    public void starting_with_large_letter_causes_error() throws Exception {
      createUserModule("""
              FunctionName() = "abc";
              """);
      runSmoothList();
      assertFinishedWithError();
      assertSysOutContainsParseError(1, "no viable alternative at input 'FunctionName('");
    }

    @Test
    public void with_one_large_letter_causes_error() throws Exception {
      createUserModule("""
              F() = "abc";
              """);
      runSmoothList();
      assertFinishedWithError();
      assertSysOutContainsParseError(1, "no viable alternative at input 'F('");
    }
  }

  @Nested
  class call {
    @Nested
    class to_value {
      @Test
      public void defined_in_local_module_causes_error() throws IOException {
        createUserModule("""
            String myValue = "abc";
            result = myValue();
            """);
        runSmoothList();
        assertFinishedWithError();
        assertSysOutContainsParseError(2, "`myValue` cannot be called as it is a value.");
      }

      @Test
      public void defined_in_imported_module_causes_error() throws IOException {
        createUserModule("""
            result = true();
            """);
        runSmoothList();
        assertFinishedWithError();
        assertSysOutContainsParseError(1, "`true` cannot be called as it is a value.");
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

    @Nested
    class without_parentheses {
      @Test
      public void outside_pipe_causes_error() throws IOException {
        createUserModule("""
            function1() = "abc";
            result    = function1;
            """);
        runSmoothList();
        assertFinishedWithError();
        assertSysOutContainsParseError(
            2, "'function1' is a function and cannot be accessed as a value.");
      }

      @Test
      public void inside_pipe_is_allowed() throws IOException {
        createUserModule("""
              myIdentity(A value) = value;
              result = "abc" | myIdentity;
              """);
        runSmoothBuild("result");
        assertFinishedWithSuccess();
        assertThat(artifactFileContentAsString("result"))
            .isEqualTo("abc");
      }
    }
  }

  @Nested
  class types {
    @Test
    public void function_expression_type_not_convertible_to_function_type_causes_error()
        throws IOException {
      createUserModule("""
              String result() = [];
              """);
      runSmoothList();
      assertFinishedWithError();
      assertSysOutContainsParseError(1, "Function 'result' has body which type is '[Nothing]' and it is " +
          "not convertible to function's declared result type 'String'.");
    }

    @Test
    public void function_with_declared_result_type() throws IOException {
      createUserModule("""
              String myFunction() = "abc";
              result = myFunction();
              """);
      runSmoothBuild("result");
      assertFinishedWithSuccess();
      assertThat(artifactFileContentAsString("result"))
          .isEqualTo("abc");
    }

    @Test
    public void function_with_result_which_type_is_undefined_causes_error() throws IOException {
      createUserModule("""
              Undefined result() = "abc";
              """);
      runSmoothList();
      assertFinishedWithError();
      assertSysOutContainsParseError(1, "Undefined type 'Undefined'.\n");
    }

    @Test
    public void function_with_generic_result_type_when_some_param_has_such_type_is_allowed()
        throws Exception {
      createUserModule("""
              A testIdentity(A value) = value;
              """);
      runSmoothList();
      assertFinishedWithSuccess();
    }

    @Test
    public void function_with_generic_result_type_when_some_param_has_such_core_type_is_allowed()
        throws Exception {
      createNativeJar(GenericResult.class);
      createUserModule("""
              A genericResult([A] array);
              """);
      runSmoothList();
      assertFinishedWithSuccess();
    }

    @Test
    public void function_with_generic_array_result_type_when_some_param_has_such_type_is_allowed()
        throws Exception {
      createUserModule("""
              [A] testArrayIdentity(A value) = [value];
              """);
      runSmoothList();
      assertFinishedWithSuccess();
    }

    @Test
    public void function_with_generic_array_result_type_when_some_param_has_such_core_type_is_allowed()
        throws Exception {
      createUserModule("""
              [A] testArrayIdentity([A] value) = value;
              """);
      runSmoothList();
      assertFinishedWithSuccess();
    }

    @Test
    public void function_with_generic_result_type_when_no_param_has_such_core_type_causes_error()
        throws Exception {
      createNativeJar(GenericResult.class);
      createUserModule("""
              A genericResult([B] array);
              """);
      runSmoothList();
      assertFinishedWithError();
      assertSysOutContainsParseError(1, "Undefined generic type 'A'. "
          + "Only generic types used in declaration of function parameters can be used here.");
    }

    @Test
    public void function_with_generic_array_result_type_when_no_param_has_such_core_type_causes_error()
        throws IOException {
      createUserModule("""
              [A] result() = [];
              """);
      runSmoothList();
      assertFinishedWithError();
      assertSysOutContainsParseError(1, "Undefined generic type 'A'. "
          + "Only generic types used in declaration of function parameters can be used here.");
    }

    @Test
    public void function_with_nothing_result_type_is_allowed()
        throws Exception {
      createNativeJar(ReportError.class);
      createUserModule("""
              Nothing reportError(String message);
              """);
      runSmoothList();
      assertFinishedWithSuccess();
    }

    @Test
    public void function_with_nothing_array_result_type_is_allowed() throws IOException {
      createUserModule("""
              [Nothing] result() = [];
              """);
      runSmoothList();
      assertFinishedWithSuccess();
    }

    @Test
    public void function_with_result_type_which_is_supertype_of_function_expression()
        throws IOException {
      createUserModule("""
              Blob myFunction() = file(toBlob("abc"), "file.txt");
              """);
      runSmoothList();
      assertFinishedWithSuccess();
    }

    @Test
    public void function_result_cannot_be_assigned_to_non_convertible_type_even_when_function_expression_is_convertible()
        throws IOException {
      createUserModule("""
              Blob func() = file(toBlob("abc"), "file.txt");
              File result = func();
              """);
      runSmoothList();
      assertFinishedWithError();
      assertSysOutContainsParseError(2, "Function 'result' has body which type is 'Blob' and it is not " +
          "convertible to function's declared result type 'File'.");
    }
  }
}
