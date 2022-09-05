package org.smoothbuild.accept;

import static com.google.common.truth.Truth.assertThat;
import static java.lang.String.format;
import static java.util.regex.Pattern.DOTALL;
import static org.smoothbuild.out.log.Level.ERROR;
import static org.smoothbuild.out.log.Log.error;
import static org.smoothbuild.out.log.Log.fatal;
import static org.smoothbuild.util.Strings.q;

import java.util.regex.Pattern;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.smoothbuild.out.log.Log;
import org.smoothbuild.testing.accept.AcceptanceTestCase;
import org.smoothbuild.testing.func.bytecode.ReturnIdFunc;
import org.smoothbuild.testing.func.nativ.AddElementOfWrongTypeToArray;
import org.smoothbuild.testing.func.nativ.BrokenIdentity;
import org.smoothbuild.testing.func.nativ.EmptyStringArray;
import org.smoothbuild.testing.func.nativ.MissingMethod;
import org.smoothbuild.testing.func.nativ.ReportErrorAndReturnNonNull;
import org.smoothbuild.testing.func.nativ.ReportFixedError;
import org.smoothbuild.testing.func.nativ.ReportWarningAndReturnNull;
import org.smoothbuild.testing.func.nativ.ReturnAbc;
import org.smoothbuild.testing.func.nativ.ReturnNull;
import org.smoothbuild.testing.func.nativ.ReturnStringStruct;
import org.smoothbuild.testing.func.nativ.StringIdentity;
import org.smoothbuild.testing.func.nativ.ThrowException;
import org.smoothbuild.testing.func.nativ.ThrowRandomException;

public class NativeTest extends AcceptanceTestCase {
  @Nested
  class _native {
    @Nested
    class _value {
      @Test
      public void without_body_is_not_legal() throws Exception {
        createUserNativeJar(ReturnAbc.class);
        createUserModule(format("""
          @Native("%s")
          String illegalValue;
          """, ReturnAbc.class.getCanonicalName()));
        evaluate("result");
        assertThat(logs())
            .contains(userError(1, "Value cannot have @Native annotation."));
      }

      @Test
      public void with_body_is_not_legal() throws Exception {
        createUserNativeJar(ReturnAbc.class);
        createUserModule(format("""
          @Native("%s")
          String illegalValue = "abc";
          """, ReturnAbc.class.getCanonicalName()));
        evaluate("result");
        assertThat(logs())
            .contains(userError(1, "Value cannot have @Native annotation."));
      }
    }

    @Nested
    class _func {
      @Test
      public void can_return_passed_arg() throws Exception {
        createUserNativeJar(StringIdentity.class);
        createUserModule(format("""
            @Native("%s")
            String stringIdentity(String string);
            result = stringIdentity("abc");
            """, StringIdentity.class.getCanonicalName()));
        evaluate("result");
        assertThat(artifact())
            .isEqualTo(stringB("abc"));
      }

      @Test
      public void without_native_jar_file_causes_error() throws Exception {
        createUserModule("""
            @Native("MissingClass")
            String myFunc();
            result = myFunc();
            """);
        evaluate("result");
        assertThat(logs())
            .contains(
                userFatal(1, "Error loading native jar: File '{prj}/build.jar' doesn't exist."));
      }

      @Test
      public void exception_from_native_is_reported_as_error() throws Exception {
        createUserNativeJar(ThrowException.class);
        createUserModule(format("""
            @Native("%s")
            A throwException();
            Int result = throwException();
            """, ThrowException.class.getCanonicalName()));
        evaluate("result");
        assertThat(logs().size())
            .isEqualTo(1);
        var log = logs().get(0);
        assertThat(log.level())
            .isEqualTo(ERROR);
        assertThat(log.message())
            .startsWith("Execution failed with:\n"
                + "java.lang.RuntimeException: `throwException` threw java exception from its "
                + "native code.");
      }

      @Test
      public void error_wrapping_exception_from_native_is_not_cached_on_disk() throws Exception {
        createUserNativeJar(ThrowRandomException.class);
        createUserModule(format("""
            @Native("%s")
            String throwRandomException();
            result = throwRandomException();
            """, ThrowRandomException.class.getCanonicalName()));

        evaluate("result");
        assertLogsContainProblem();
        String timestamp1 = fetchTimestamp(logs().get(0).message());

        resetState();
        evaluate("result");
        assertLogsContainProblem();
        String timestamp2 = fetchTimestamp(logs().get(0).message());

        assertThat(timestamp1)
            .isNotEqualTo(timestamp2);
      }

      @Test
      public void error_reported_is_logged() throws Exception {
        createUserNativeJar(ReportFixedError.class);
        createUserModule(format("""
            @Native("%s")
            A reportFixedError();
            Int result = reportFixedError();
            """, ReportFixedError.class.getCanonicalName()));
        evaluate("result");
        assertThat(logs())
            .containsExactly(error("some error message"));
      }

      @Test
      public void func_with_illegal_impl_causes_error() throws Exception {
        var clazz = MissingMethod.class;
        createUserNativeJar(clazz);
        String className = clazz.getCanonicalName();
        createUserModule(format("""
              @Native("%s")
              String wrongMethodName();
              result = wrongMethodName();
              """, className));
        evaluate("result");
        assertThat(logs())
            .containsExactly(methodLoadingError(className, "wrongMethodName",
                "Class '" + className + "' does not have 'func' method."));
      }

      @Nested
      class _error_is_reported_when_java_method_returns {
        @Test
        public void null_without_logging_error() throws Exception {
          createUserNativeJar(ReturnNull.class);
          String className = ReturnNull.class.getCanonicalName();
          createUserModule(format("""
            @Native("%s")
            String returnNull();
            result = returnNull();
            """, className));
          evaluate("result");
          assertThat(logs())
              .containsExactly(faultyNullReturnedError("returnNull"));
        }

        @Test
        public void null_and_logs_only_warning() throws Exception {
          createUserNativeJar(ReportWarningAndReturnNull.class);
          createUserModule(format("""
            @Native("%s")
            String reportWarning();
            result = reportWarning();
            """, ReportWarningAndReturnNull.class.getCanonicalName()));
          evaluate("result");
          assertThat(logs())
              .contains(faultyNullReturnedError("reportWarning"));
        }

        @Test
        public void non_null_and_logs_error() throws Exception {
          createUserNativeJar(ReportErrorAndReturnNonNull.class);
          createUserModule(format("""
            @Native("%s")
            String reportErrorAndReturnValue();
            result = reportErrorAndReturnValue();
            """, ReportErrorAndReturnNonNull.class.getCanonicalName()));
          evaluate("result");
          assertThat(logs())
              .contains(nonNullValueAndError("reportErrorAndReturnValue"));
        }

        @Test
        public void object_of_wrong_type() throws Exception {
          createUserNativeJar(BrokenIdentity.class);
          createUserModule(format("""
            @Native("%s")
            A brokenIdentity(A value);
            Int result = brokenIdentity(7);
            """, BrokenIdentity.class.getCanonicalName()));
          evaluate("result");
          assertThat(logs())
              .containsExactly(faultyTypeOfReturnedObject("brokenIdentity", "Int", "String"));
        }

        @Test
        public void struct_of_wrong_type() throws Exception {
          createUserNativeJar(ReturnStringStruct.class);
          createUserModule(format("""
            Person {
              String firstName,
              String lastName,
            }
            @Native("%s")
            Person returnStringStruct();
            result = returnStringStruct();
            """, ReturnStringStruct.class.getCanonicalName()));
          evaluate("result");
          assertThat(logs())
              .containsExactly(
                  faultyTypeOfReturnedObject("returnStringStruct", "{String,String}", "{String}"));
        }

        @Test
        public void array_of_wrong_type() throws Exception {
          createUserNativeJar(EmptyStringArray.class);
          createUserModule(format("""
            @Native("%s")
            [Blob] emptyStringArray();
            result = emptyStringArray();
            """, EmptyStringArray.class.getCanonicalName()));
          evaluate("result");
          assertThat(logs())
              .containsExactly(
                  faultyTypeOfReturnedObject("emptyStringArray", "[Blob]", "[String]"));
        }

        @Test
        public void array_with_added_elem_of_wrong_type() throws Exception {
          createUserNativeJar(AddElementOfWrongTypeToArray.class);
          createUserModule(format("""
            @Native("%s")
            [Blob] addElementOfWrongTypeToArray();
            result = addElementOfWrongTypeToArray();
            """, AddElementOfWrongTypeToArray.class.getCanonicalName()));
          evaluate("result");
          String message = logs().get(0).message();
          assertThat(message)
              .startsWith("Execution failed with:\n"
                  + "java.lang.RuntimeException: `addElementOfWrongTypeToArray`"
                  + " threw java exception from its native code.");
          assertThat(message)
              .contains("Element type must be Blob but was String.");
        }
      }
    }
  }

  @Nested
  class _bytecode {
    @Test
    public void func_can_be_called() throws Exception {
      createUserNativeJar(ReturnIdFunc.class);
      createUserModule(format("""
            @Bytecode("%s")
            A myId(A a);
            result = myId(77);
            """, ReturnIdFunc.class.getCanonicalName()));
      evaluate("result");
      assertThat(artifact())
          .isEqualTo(intB(77));
    }

    @Test
    public void func_with_illegal_impl_causes_err() throws Exception {
      Class<?> clazz = org.smoothbuild.testing.func.bytecode.NonPublicMethod.class;
      createUserNativeJar(clazz);
      createUserModule(format("""
            @Bytecode("%s")
            Int brokenFunc();
            result = brokenFunc();
            """, clazz.getCanonicalName()));
      evaluate("result");
      assertThat(logs())
          .containsExactly(fatal("build.smooth:1: Error loading bytecode for `brokenFunc` using "
              + "provider specified as `org.smoothbuild.testing.func.bytecode.NonPublicMethod`: "
              + "Providing method is not public."));
    }

    @Test
    public void value_can_be_called() throws Exception {
      Class<?> clazz = org.smoothbuild.testing.func.bytecode.ReturnAbc.class;
      createUserNativeJar(clazz);
      createUserModule(format("""
            @Bytecode("%s")
            String result;
            """, clazz.getCanonicalName()));
      evaluate("result");
      assertThat(artifact())
          .isEqualTo(stringB("abc"));
    }

    @Test
    public void value_with_illegal_impl_causes_err() throws Exception {
      Class<?> clazz = org.smoothbuild.testing.func.bytecode.NonPublicMethod.class;
      createUserNativeJar(clazz);
      createUserModule(format("""
            @Bytecode("%s")
            Int result;
            """, clazz.getCanonicalName()));
      evaluate("result");
      assertThat(logs())
          .containsExactly(fatal("build.smooth:1: Error loading bytecode for `result` using "
              + "provider specified as `org.smoothbuild.testing.func.bytecode.NonPublicMethod`: "
              + "Providing method is not public."));
    }
  }

  private static String fetchTimestamp(String text) {
    var pattern = Pattern.compile(".*java.lang.UnsupportedOperationException: ([0-9]*).*", DOTALL);
    var matcher = pattern.matcher(text);
    matcher.matches();
    return matcher.group(1);
  }

  private static Log faultyTypeOfReturnedObject(
      String name, String declared, String actual) {
    return faultyNativeImplError(name, "Its declared result type == " + q(declared)
        + " but it returned object with type == " + q(actual) + ".");
  }

  private static Log nonNullValueAndError(String name) {
    return faultyNativeImplError(name, "It returned non-null value but logged error.");
  }

  private static Log faultyNullReturnedError(String name) {
    return faultyNativeImplError(name, "It returned `null` but logged no error.");
  }

  private static Log faultyNativeImplError(String name, String message) {
    return error(q(name) + " has faulty native implementation: " + message);
  }

  private static Log methodLoadingError(String className, String methodName, String message) {
    return error("Error loading native implementation for " + q(methodName) + " specified as "
        + q(className) + ": " + message);
  }
}
