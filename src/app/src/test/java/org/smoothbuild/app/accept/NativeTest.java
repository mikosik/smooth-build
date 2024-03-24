package org.smoothbuild.app.accept;

import static com.google.common.truth.Truth.assertThat;
import static java.lang.String.format;
import static java.util.regex.Pattern.DOTALL;
import static org.smoothbuild.common.base.Strings.q;
import static org.smoothbuild.common.log.base.Level.FATAL;
import static org.smoothbuild.common.log.base.Log.error;
import static org.smoothbuild.common.log.base.Log.fatal;

import java.util.regex.Pattern;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.smoothbuild.common.log.base.Log;
import org.smoothbuild.evaluator.testing.EvaluatorTestCase;
import org.smoothbuild.virtualmachine.testing.func.bytecode.NonPublicMethod;
import org.smoothbuild.virtualmachine.testing.func.bytecode.ReturnAbc;
import org.smoothbuild.virtualmachine.testing.func.bytecode.ReturnIdFunc;
import org.smoothbuild.virtualmachine.testing.func.nativ.AddElementOfWrongTypeToArray;
import org.smoothbuild.virtualmachine.testing.func.nativ.BrokenIdentity;
import org.smoothbuild.virtualmachine.testing.func.nativ.EmptyStringArray;
import org.smoothbuild.virtualmachine.testing.func.nativ.MissingMethod;
import org.smoothbuild.virtualmachine.testing.func.nativ.ReportErrorAndReturnNonNull;
import org.smoothbuild.virtualmachine.testing.func.nativ.ReportFixedError;
import org.smoothbuild.virtualmachine.testing.func.nativ.ReportWarningAndReturnNull;
import org.smoothbuild.virtualmachine.testing.func.nativ.ReturnNull;
import org.smoothbuild.virtualmachine.testing.func.nativ.ReturnStringStruct;
import org.smoothbuild.virtualmachine.testing.func.nativ.StringIdentity;
import org.smoothbuild.virtualmachine.testing.func.nativ.ThrowException;
import org.smoothbuild.virtualmachine.testing.func.nativ.ThrowRandomException;

public class NativeTest extends EvaluatorTestCase {
  @Nested
  class _native {
    @Nested
    class _value {
      @Test
      public void without_body_is_not_legal() throws Exception {
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
      public void with_body_is_not_legal() throws Exception {
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
      public void can_return_passed_arg() throws Exception {
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
      public void without_native_jar_file_causes_fatal() throws Exception {
        createUserModule(
            """
            @Native("MissingClass")
            String myFunc();
            result = myFunc();
            """);
        evaluate("result");
        assertThat(logs())
            .contains(
                userFatal(1, "Error persisting native jar '{module-bucket}/userModule.jar'."));
      }

      @Test
      public void exception_from_native_is_reported_as_fatal() throws Exception {
        var userModule = format(
            """
                @Native("%s")
                A throwException();
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
      public void fatal_wrapping_exception_from_native_is_not_cached_on_disk() throws Exception {
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

        restartSmoothWithSameBuckets();
        evaluate("result");
        assertLogsContainFailure();
        String timestamp2 = fetchTimestamp(logs().get(0).message());

        assertThat(timestamp1).isNotEqualTo(timestamp2);
      }

      @Test
      public void error_reported_is_logged() throws Exception {
        var userModule = format(
            """
                @Native("%s")
                A reportFixedError();
                Int result = reportFixedError();
                """,
            ReportFixedError.class.getCanonicalName());
        createUserModule(userModule, ReportFixedError.class);
        evaluate("result");
        assertThat(logs()).containsExactly(error("some error message"));
      }

      @Test
      public void func_with_illegal_impl_causes_error() throws Exception {
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
        public void null_without_logging_error() throws Exception {
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
        public void null_and_logs_only_warning() throws Exception {
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
        public void non_null_and_logs_error() throws Exception {
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
        public void object_of_wrong_type() throws Exception {
          var userModule = format(
              """
                  @Native("%s")
                  A brokenIdentity(A value);
                  Int result = brokenIdentity(7);
                  """,
              BrokenIdentity.class.getCanonicalName());
          createUserModule(userModule, BrokenIdentity.class);
          evaluate("result");
          assertThat(logs()).containsExactly(faultyTypeOfReturnedObject("Int", "String"));
        }

        @Test
        public void struct_of_wrong_type() throws Exception {
          var userModule = format(
              """
                  Person(
                    String firstName,
                    String lastName,
                  )
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
        public void array_of_wrong_type() throws Exception {
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
        public void array_with_added_elem_of_wrong_type() throws Exception {
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
  }

  @Nested
  class _bytecode {
    @Test
    public void func_call_can_be_evaluated() throws Exception {
      var userModule = format(
          """
              @Bytecode("%s")
              A myId(A a);
              result = myId(77);
              """,
          ReturnIdFunc.class.getCanonicalName());
      createUserModule(userModule, ReturnIdFunc.class);
      evaluate("result");
      assertThat(artifact()).isEqualTo(bInt(77));
    }

    @Test
    public void func_with_illegal_impl_causes_fatal() throws Exception {
      var userModule = format(
          """
              @Bytecode("%s")
              Int brokenFunc();
              result = brokenFunc();
              """,
          NonPublicMethod.class.getCanonicalName());
      createUserModule(userModule, NonPublicMethod.class);
      evaluate("result");
      assertThat(logs())
          .containsExactly(userFatal(
              1,
              "Error loading bytecode for `brokenFunc`"
                  + " using provider specified as `" + NonPublicMethod.class.getCanonicalName()
                  + "`: Providing method is not public."));
    }

    @Test
    public void value_can_be_evaluated() throws Exception {
      var userModule = format(
          """
              @Bytecode("%s")
              String result;
              """,
          ReturnAbc.class.getCanonicalName());
      createUserModule(userModule, ReturnAbc.class);
      evaluate("result");
      assertThat(artifact()).isEqualTo(bString("abc"));
    }

    @Test
    public void value_with_illegal_impl_causes_fatal() throws Exception {
      var userModule = format(
          """
              @Bytecode("%s")
              Int result;
              """,
          NonPublicMethod.class.getCanonicalName());
      createUserModule(userModule, NonPublicMethod.class);
      evaluate("result");
      assertThat(logs())
          .containsExactly(userFatal(
              1,
              "Error loading bytecode for `result` using "
                  + "provider specified as `" + NonPublicMethod.class.getCanonicalName()
                  + "`: Providing method is not public."));
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
        + " but it returned object with type == " + q(actual) + ".");
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
