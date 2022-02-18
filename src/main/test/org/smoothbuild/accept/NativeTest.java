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
import org.smoothbuild.bytecode.obj.val.BlobB;
import org.smoothbuild.bytecode.obj.val.StringB;
import org.smoothbuild.bytecode.obj.val.TupleB;
import org.smoothbuild.out.log.Log;
import org.smoothbuild.plugin.NativeApi;
import org.smoothbuild.testing.accept.AcceptanceTestCase;
import org.smoothbuild.testing.func.bytecode.ReturnIdFunc;
import org.smoothbuild.testing.func.nativ.AddElementOfWrongTypeToArray;
import org.smoothbuild.testing.func.nativ.BrokenIdentity;
import org.smoothbuild.testing.func.nativ.EmptyStringArray;
import org.smoothbuild.testing.func.nativ.NonPublicMethod;
import org.smoothbuild.testing.func.nativ.NonStaticMethod;
import org.smoothbuild.testing.func.nativ.OverloadedMethod;
import org.smoothbuild.testing.func.nativ.ReportErrorAndReturnNonNull;
import org.smoothbuild.testing.func.nativ.ReportFixedError;
import org.smoothbuild.testing.func.nativ.ReportWarningAndReturnNull;
import org.smoothbuild.testing.func.nativ.ReturnAbc;
import org.smoothbuild.testing.func.nativ.ReturnNull;
import org.smoothbuild.testing.func.nativ.ReturnStringStruct;
import org.smoothbuild.testing.func.nativ.StringIdentity;
import org.smoothbuild.testing.func.nativ.ThrowException;
import org.smoothbuild.testing.func.nativ.ThrowRandomException;
import org.smoothbuild.testing.func.nativ.TooFewParameters;
import org.smoothbuild.testing.func.nativ.TooManyParameters;
import org.smoothbuild.testing.func.nativ.WrongMethodName;
import org.smoothbuild.testing.func.nativ.WrongParameterType;

public class NativeTest extends AcceptanceTestCase {
  @Nested
  class _native_value {
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
  class _native_func {
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
            Nothing throwException();
            result = throwException();
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
            Nothing reportFixedError();
            result = reportFixedError();
            """, ReportFixedError.class.getCanonicalName()));
      evaluate("result");
      assertThat(logs())
          .containsExactly(error("some error message"));
    }

    @Nested
    class _error_is_reported_when_java_method {
      @Test
      public void is_missing() throws Exception {
        createUserNativeJar(WrongMethodName.class);
        String className = WrongMethodName.class.getCanonicalName();
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

      @Test
      public void is_overloaded() throws Exception {
        createUserNativeJar(OverloadedMethod.class);
        String className = OverloadedMethod.class.getCanonicalName();
        createUserModule(format("""
              @Native("%s")
              String overloadedMethod();
              result = overloadedMethod();
              """, className));
        evaluate("result");
        assertThat(logs())
            .containsExactly(methodLoadingError(className, "overloadedMethod",
                "Class '" + className + "' has more than one 'func' method."));
      }

      @Test
      public void has_result_type_different_than_declared_in_smooth_file() throws Exception {
        createUserNativeJar(StringIdentity.class);
        String className = StringIdentity.class.getCanonicalName();
        createUserModule(format("""
            @Native("%s")
            Blob stringIdentity(String string);
            result = stringIdentity("abc");
            """, className));
        evaluate("result");
        assertThat(logs())
            .containsExactly(methodLoadingError(className, "stringIdentity",
                "`stringIdentity` declares type `Blob` so its native implementation result type must"
                + " be " + BlobB.class.getCanonicalName() + " but it is "
                + StringB.class.getCanonicalName() + "."));
      }

      @Test
      public void has_too_many_params() throws Exception {
        createUserNativeJar(TooManyParameters.class);
        String className = TooManyParameters.class.getCanonicalName();
        createUserModule(format("""
            @Native("%s")
            String tooManyParameters();
            result = tooManyParameters();
            """, className));
        evaluate("result");
        assertThat(logs())
            .containsExactly(methodSignatureError(className, "tooManyParameters"));
      }


      @Test
      public void has_too_few_params() throws Exception {
        createUserNativeJar(TooFewParameters.class);
        String className = TooFewParameters.class.getCanonicalName();
        createUserModule(format("""
            @Native("%s")
            String tooFewParameters();
            result = tooFewParameters();
            """, className));
        evaluate("result");
        assertThat(logs())
            .containsExactly(methodSignatureError(className, "tooFewParameters"));
      }

      @Test
      public void has_wrong_param_type() throws Exception {
        createUserNativeJar(WrongParameterType.class);
        String className = WrongParameterType.class.getCanonicalName();
        createUserModule(format("""
            @Native("%s")
            String wrongParameterType();
            result = wrongParameterType();
            """, className));
        evaluate("result");
        assertThat(logs())
            .containsExactly(methodSignatureError(className, "wrongParameterType"));
      }

      private Log methodSignatureError(String className, String name) {
        return methodLoadingError(className, name, "Providing method should have two parameters "
            + NativeApi.class.getCanonicalName() + " and " + TupleB.class.getCanonicalName() + ".");
      }

      @Test
      public void is_not_public() throws Exception {
        createUserNativeJar(NonPublicMethod.class, ReturnAbc.class);
        String className = NonPublicMethod.class.getCanonicalName();
        createUserModule(format("""
            @Native("%s")
            String returnAbc();
            result = returnAbc();
            """, className));
        evaluate("result");
        assertThat(logs())
            .containsExactly(
                methodLoadingError(className, "returnAbc", "Providing method is not public."));
      }

      @Test
      public void is_not_static() throws Exception {
        createUserNativeJar(NonStaticMethod.class, ReturnAbc.class);
        String className = NonStaticMethod.class.getCanonicalName();
        createUserModule(format("""
            @Native("%s")
            String returnAbc();
            result = returnAbc();
            """, className));
        evaluate("result");
        assertThat(logs())
            .containsExactly(
                methodLoadingError(className, "returnAbc", "Providing method is not static."));
      }

      @Nested
      class _returns {
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
            result = brokenIdentity(value=[]);
            """, BrokenIdentity.class.getCanonicalName()));
          evaluate("result");
          assertThat(logs())
              .containsExactly(faultyTypeOfReturnedObject("brokenIdentity", "[Nothing]", "String"));
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
