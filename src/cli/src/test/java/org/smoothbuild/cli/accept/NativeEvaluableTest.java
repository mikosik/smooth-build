package org.smoothbuild.cli.accept;

import static com.google.common.truth.Truth.assertThat;
import static java.lang.String.format;
import static java.util.regex.Pattern.DOTALL;
import static org.smoothbuild.common.base.Strings.q;
import static org.smoothbuild.common.collect.List.list;
import static org.smoothbuild.common.log.base.Label.label;
import static org.smoothbuild.common.log.base.Level.FATAL;
import static org.smoothbuild.common.log.base.Log.error;
import static org.smoothbuild.common.log.base.Log.fatal;
import static org.smoothbuild.common.log.report.Report.report;

import java.util.regex.Pattern;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.smoothbuild.common.log.base.Log;
import org.smoothbuild.evaluator.dagger.EvaluatorTestContext;
import org.smoothbuild.virtualmachine.testing.func.bytecode.ReturnAbc;
import org.smoothbuild.virtualmachine.testing.func.nativ.AddElementOfWrongTypeToArray;
import org.smoothbuild.virtualmachine.testing.func.nativ.BrokenIdentity;
import org.smoothbuild.virtualmachine.testing.func.nativ.EmptyStringArray;
import org.smoothbuild.virtualmachine.testing.func.nativ.MissingMethod;
import org.smoothbuild.virtualmachine.testing.func.nativ.ReportError;
import org.smoothbuild.virtualmachine.testing.func.nativ.ReportErrorAndReturnNonNull;
import org.smoothbuild.virtualmachine.testing.func.nativ.ReportWarningAndReturnNull;
import org.smoothbuild.virtualmachine.testing.func.nativ.ReturnNull;
import org.smoothbuild.virtualmachine.testing.func.nativ.ReturnStringStruct;
import org.smoothbuild.virtualmachine.testing.func.nativ.StringIdentity;
import org.smoothbuild.virtualmachine.testing.func.nativ.ThrowException;
import org.smoothbuild.virtualmachine.testing.func.nativ.ThrowRandomException;

public class NativeEvaluableTest extends EvaluatorTestContext {
  @Nested
  class _value {
    @Test
    void without_body_is_not_legal() throws Exception {
      var userModule = format(
          """
              @Native("%s")
              String illegalValue;
              """,
          ReturnAbc.class.getCanonicalName());
      createUserModule(userModule, ReturnAbc.class);
      evaluate("result");
      assertThat(logs()).contains(userError(1, "Value cannot have @Native annotation."));
    }

    @Test
    void with_body_is_not_legal() throws Exception {
      var userModule = format(
          """
              @Native("%s")
              String illegalValue = "abc";
              """,
          ReturnAbc.class.getCanonicalName());
      createUserModule(userModule, ReturnAbc.class);
      evaluate("result");
      assertThat(logs()).contains(userError(1, "Value cannot have @Native annotation."));
    }
  }

  @Nested
  class _func {
    @Test
    void can_return_passed_arg() throws Exception {
      var userModule = format(
          """
              @Native("%s")
              String stringIdentity(String string);
              result = stringIdentity("abc");
              """,
          StringIdentity.class.getCanonicalName());
      createUserModule(userModule, StringIdentity.class);
      evaluate("result");
      assertThat(artifact()).isEqualTo(bString("abc"));
    }

    @Test
    void without_native_jar_file_causes_fatal() throws Exception {
      createUserModule(
          """
          @Native("MissingClass")
          String myFunc();
          result = myFunc();
          """);
      evaluate("result");
      assertThat(logs())
          .contains(userFatal(
              1,
              "Error loading native jar '{t-project}/module.jar'.\n"
                  + "Cannot read '{t-project}/module.jar'. "
                  + "File '{t-project}/module.jar' doesn't exist."));
    }

    @Test
    void exception_from_native_is_reported_as_fatal() throws Exception {
      var userModule = format(
          """
              @Native("%s")
              A throwException<A>();
              Int result = throwException();
              """,
          ThrowException.class.getCanonicalName());
      createUserModule(userModule, ThrowException.class);
      evaluate("result");
      assertThat(logs().size()).isEqualTo(1);
      var log = logs().get(0);
      assertThat(log.level()).isEqualTo(FATAL);
      assertThat(log.message())
          .startsWith(
              "Native code thrown exception:\n" + "java.lang.UnsupportedOperationException");
    }

    @Test
    void fatal_wrapping_exception_from_native_is_not_cached_on_disk() throws Exception {
      var userModule = format(
          """
              @Native("%s")
              String throwRandomException();
              result = throwRandomException();
              """,
          ThrowRandomException.class.getCanonicalName());
      createUserModule(userModule, ThrowRandomException.class);

      evaluate("result");
      assertLogsContainFailure();
      String timestamp1 = fetchTimestamp(logs().get(0).message());

      restartSmoothWithSameFileSystem();
      evaluate("result");
      assertLogsContainFailure();
      String timestamp2 = fetchTimestamp(logs().get(0).message());

      assertThat(timestamp1).isNotEqualTo(timestamp2);
    }

    @Test
    void error_reported_is_logged() throws Exception {
      var userModule = format(
          """
              @Native("%s")
              Int reportError(String message);
              result = reportError("ERROR MESSAGE");
              """,
          ReportError.class.getCanonicalName());
      createUserModule(userModule, ReportError.class);

      evaluate("result");

      var trace = trace("reportError", location(moduleFullPath(), 3));
      var label = label(":vm:evaluate:invoke");
      var errors = list(error("ERROR MESSAGE"));
      var report = report(label, trace, errors);
      assertThat(reports()).contains(report);
    }

    @Test
    void func_with_illegal_impl_causes_error() throws Exception {
      String className = MissingMethod.class.getCanonicalName();
      var userModule = format(
          """
              @Native("%s")
              String wrongMethodName();
              result = wrongMethodName();
              """,
          className);
      createUserModule(userModule, MissingMethod.class);
      evaluate("result");
      assertThat(logs())
          .containsExactly(methodLoadingFatal(
              className, "Class '" + className + "' does not have 'func' method."));
    }

    @Nested
    class _fatal_is_reported_when_java_method_returns {
      @Test
      void null_without_logging_error() throws Exception {
        String className = ReturnNull.class.getCanonicalName();
        var userModule = format(
            """
                @Native("%s")
                String returnNull();
                result = returnNull();
                """,
            className);
        createUserModule(userModule, ReturnNull.class);
        evaluate("result");
        assertThat(logs()).containsExactly(faultyNullReturnedFatal());
      }

      @Test
      void null_and_logs_only_warning() throws Exception {
        var userModule = format(
            """
                @Native("%s")
                String reportWarning();
                result = reportWarning();
                """,
            ReportWarningAndReturnNull.class.getCanonicalName());
        createUserModule(userModule, ReportWarningAndReturnNull.class);
        evaluate("result");
        assertThat(logs()).contains(faultyNullReturnedFatal());
      }

      @Test
      void non_null_and_logs_error() throws Exception {
        var userModule = format(
            """
                @Native("%s")
                String reportErrorAndReturnValue();
                result = reportErrorAndReturnValue();
                """,
            ReportErrorAndReturnNonNull.class.getCanonicalName());
        createUserModule(userModule, ReportErrorAndReturnNonNull.class);
        evaluate("result");
        assertThat(logs()).contains(nonNullValueAndError());
      }

      @Test
      void object_of_wrong_type() throws Exception {
        var userModule = format(
            """
                @Native("%s")
                A brokenIdentity<A>(A value);
                Int result = brokenIdentity(7);
                """,
            BrokenIdentity.class.getCanonicalName());
        createUserModule(userModule, BrokenIdentity.class);
        evaluate("result");
        assertThat(logs()).containsExactly(faultyTypeOfReturnedObject("Int", "String"));
      }

      @Test
      void struct_of_wrong_type() throws Exception {
        var userModule = format(
            """
                Person {
                  String firstName,
                  String lastName,
                }
                @Native("%s")
                Person returnStringStruct();
                result = returnStringStruct();
                """,
            ReturnStringStruct.class.getCanonicalName());
        createUserModule(userModule, ReturnStringStruct.class);
        evaluate("result");
        assertThat(logs())
            .containsExactly(faultyTypeOfReturnedObject("{String,String}", "{String}"));
      }

      @Test
      void array_of_wrong_type() throws Exception {
        var userModule = format(
            """
                @Native("%s")
                [Blob] emptyStringArray();
                result = emptyStringArray();
                """,
            EmptyStringArray.class.getCanonicalName());
        createUserModule(userModule, EmptyStringArray.class);
        evaluate("result");
        assertThat(logs()).containsExactly(faultyTypeOfReturnedObject("[Blob]", "[String]"));
      }

      @Test
      void array_with_added_elem_of_wrong_type() throws Exception {
        var userModule = format(
            """
                @Native("%s")
                [Blob] addElementOfWrongTypeToArray();
                result = addElementOfWrongTypeToArray();
                """,
            AddElementOfWrongTypeToArray.class.getCanonicalName());
        createUserModule(userModule, AddElementOfWrongTypeToArray.class);
        evaluate("result");
        String message = logs().get(0).message();
        assertThat(message)
            .startsWith("Native code thrown exception:\n"
                + "java.lang.IllegalArgumentException: Element type must be `Blob` but was "
                + "`String`.");
        assertThat(message).contains("Element type must be `Blob` but was `String`.");
      }
    }
  }

  private static String fetchTimestamp(String text) {
    var pattern = Pattern.compile(".*java.lang.UnsupportedOperationException: ([0-9]*).*", DOTALL);
    var matcher = pattern.matcher(text);
    matcher.matches();
    return matcher.group(1);
  }

  private static Log faultyTypeOfReturnedObject(String declared, String actual) {
    return faultyNativeImplFatal("Its declared result type == " + q(declared)
        + " but it returned expression with type == " + q(actual) + ".");
  }

  private static Log nonNullValueAndError() {
    return faultyNativeImplFatal("It returned non-null value but logged error.");
  }

  private static Log faultyNullReturnedFatal() {
    return faultyNativeImplFatal("It returned `null` but logged no error.");
  }

  private static Log faultyNativeImplFatal(String message) {
    return fatal("Faulty native implementation: " + message);
  }

  private static Log methodLoadingFatal(String className, String message) {
    return fatal(
        "Error loading native implementation specified as " + q(className) + ": " + message);
  }
}
