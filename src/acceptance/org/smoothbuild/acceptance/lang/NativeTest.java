package org.smoothbuild.acceptance.lang;

import static com.google.common.truth.Truth.assertThat;
import static java.lang.String.format;
import static java.util.regex.Pattern.DOTALL;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.smoothbuild.acceptance.AcceptanceTestCase;
import org.smoothbuild.acceptance.testing.AddElementOfWrongTypeToArray;
import org.smoothbuild.acceptance.testing.BrokenIdentity;
import org.smoothbuild.acceptance.testing.EmptyStringArray;
import org.smoothbuild.acceptance.testing.NonPublicMethod;
import org.smoothbuild.acceptance.testing.NonStaticMethod;
import org.smoothbuild.acceptance.testing.ReportFixedError;
import org.smoothbuild.acceptance.testing.ReportWarningAndReturnNull;
import org.smoothbuild.acceptance.testing.ReturnAbc;
import org.smoothbuild.acceptance.testing.ReturnNull;
import org.smoothbuild.acceptance.testing.ReturnStringStruct;
import org.smoothbuild.acceptance.testing.StringIdentity;
import org.smoothbuild.acceptance.testing.ThrowException;
import org.smoothbuild.acceptance.testing.ThrowRandomException;
import org.smoothbuild.acceptance.testing.WithoutContainer;
import org.smoothbuild.acceptance.testing.WrongMethodName;
import org.smoothbuild.db.object.obj.val.ArrayH;
import org.smoothbuild.db.object.obj.val.StringH;
import org.smoothbuild.db.object.obj.val.TupleH;
import org.smoothbuild.plugin.NativeApi;

public class NativeTest extends AcceptanceTestCase {
  @Nested
  class _native_value {
    @Test
    public void _native_value_without_body_is_not_legal() throws Exception {
      createNativeJar(ReturnAbc.class);
      createUserModule(format("""
          @Native("%s")
          String illegalValue;
          """, ReturnAbc.class.getCanonicalName()));
      runSmoothBuild("result");
      assertFinishedWithError();
      assertSysOutContainsParseError(1, "Value cannot have @Native annotation.");
    }

    @Test
    public void _native_value_with_body_is_not_legal() throws Exception {
      createNativeJar(ReturnAbc.class);
      createUserModule(format("""
          @Native("%s")
          String illegalValue = "abc";
          """, ReturnAbc.class.getCanonicalName()));
      runSmoothBuild("result");
      assertFinishedWithError();
      assertSysOutContainsParseError(1, "Value cannot have @Native annotation.");
    }
  }

  @Nested
  class _nat_func {
    @Test
    public void can_return_passed_arg() throws Exception {
      createNativeJar(StringIdentity.class);
      createUserModule(format("""
            @Native("%s")
            String stringIdentity(String string);
            result = stringIdentity("token");
            """, StringIdentity.class.getCanonicalName()));
      runSmoothBuild("result");
      assertFinishedWithSuccess();
      assertThat(artifactFileContentAsString("result"))
          .isEqualTo("token");
    }

    @Test
    public void without_native_jar_file_causes_error() throws Exception {
      createUserModule("""
            @Native("MissingClass")
            String myFunc();
            result = myFunc();
            """);
      runSmoothBuild("result");
      assertFinishedWithError();
      assertSysOutContains(fileNotFoundErrorMessage("myFunc"));
    }

    @Test
    public void exception_from_native_is_reported_as_error() throws Exception {
      createNativeJar(ThrowException.class);
      createUserModule(format("""
            @Native("%s")
            Nothing throwException();
            result = throwException();
            """, ThrowException.class.getCanonicalName()));
      runSmoothBuild("result");
      assertFinishedWithError();
      assertSysOutContains("`throwException` threw java exception from its native code.");
      assertSysOutContains("java.lang.UnsupportedOperationException");
    }

    @Test
    public void error_wrapping_exception_from_native_is_not_cached() throws Exception {
      createNativeJar(ThrowRandomException.class);
      createUserModule(format("""
            @Native("%s")
            String throwRandomException();
            result = throwRandomException();
            """, ThrowRandomException.class.getCanonicalName()));
      runSmoothBuild("result");
      assertFinishedWithError();
      String timestamp1 = fetchTimestamp(sysOut());
      runSmoothBuild("result");
      assertFinishedWithError();
      String timestamp2 = fetchTimestamp(sysOut());
      assertThat(timestamp1)
          .isNotEqualTo(timestamp2);
    }

    @Test
    public void error_reported_is_logged() throws Exception {
      createNativeJar(ReportFixedError.class);
      createUserModule(format("""
            @Native("%s")
            Nothing reportFixedError();
            result = reportFixedError();
            """, ReportFixedError.class.getCanonicalName()));
      runSmoothBuild("result");
      assertFinishedWithError();
      assertSysOutContains("some error message");
    }

    @Nested
    class _error_is_reported_when_java_method {
      @Test
      public void is_missing() throws Exception {
        createNativeJar(WrongMethodName.class);
        String classPath = WrongMethodName.class.getCanonicalName();
        createUserModule(format("""
              @Native("%s")
              String wrongMethodName();
              result = wrongMethodName();
              """, classPath));
        runSmoothBuild("result");
        assertFinishedWithError();
        assertSysOutContains(errorLoadingMessage("wrongMethodName", classPath,
            "Class '" + classPath + "' does not have 'func' method."));
      }

      @Test
      public void has_result_type_different_than_declared_in_smooth_file() throws Exception {
        createNativeJar(StringIdentity.class);
        String classPath = StringIdentity.class.getCanonicalName();
        createUserModule(format("""
            @Native("%s")
            File stringIdentity(String string);
            result = stringIdentity("abc");
            """, classPath));
        runSmoothBuild("result");
        assertFinishedWithError();
        assertSysOutContains(errorLoadingMessage("stringIdentity", classPath,
            "`stringIdentity` declares type `{Blob,String}` "
                + "so its native implementation result type must be "
                + TupleH.class.getCanonicalName()
                + " but it is " + StringH.class.getCanonicalName() + ".\n"));
      }

      @Test
      public void has_too_many_params() throws Exception {
        createNativeJar(StringIdentity.class);
        String classPath = StringIdentity.class.getCanonicalName();
        createUserModule(format("""
            @Native("%s")
            String stringIdentity();
            result = stringIdentity();
            """, classPath));
        runSmoothBuild("result");
        assertFinishedWithError();
        assertSysOutContains(errorLoadingMessage("stringIdentity", classPath,
            "`stringIdentity` has 0 parameter(s) but its native implementation "
                + "has 1 parameter(s)."));
      }

      @Test
      public void has_too_few_params() throws Exception {
        createNativeJar(StringIdentity.class);
        String classPath = StringIdentity.class.getCanonicalName();
        createUserModule(format("""
            @Native("%s")
            String stringIdentity(String a, String b);
            result = stringIdentity(a="abc", b="abc");
            """, classPath));
        runSmoothBuild("result");
        assertFinishedWithError();
        assertSysOutContains(errorLoadingMessage("stringIdentity", classPath,
            "`stringIdentity` has 2 parameter(s) but its native implementation "
                + "has 1 parameter(s)."));
      }

      @Test
      public void has_different_param_type() throws Exception {
        createNativeJar(StringIdentity.class);
        String classPath = StringIdentity.class.getCanonicalName();
        createUserModule(format("""
            @Native("%s")
            String stringIdentity([String] string);
            result = stringIdentity([]);
            """, classPath));
        runSmoothBuild("result");
        assertFinishedWithError();
        assertSysOutContains(errorLoadingMessage("stringIdentity", classPath,
            "`stringIdentity` parameter at index 0 has type `[String]` "
            + "so its native implementation type must be " + ArrayH.class.getCanonicalName()
            + " but it is " + StringH.class.getCanonicalName() + "."));
      }

      @Test
      public void is_not_public() throws Exception {
        createNativeJar(NonPublicMethod.class, ReturnAbc.class);
        String classPath = NonPublicMethod.class.getCanonicalName();
        createUserModule(format("""
            @Native("%s")
            String returnAbc();
            result = returnAbc();
            """, classPath));
        runSmoothBuild("result");
        assertFinishedWithError();
        assertSysOutContains(errorLoadingMessage("returnAbc", classPath,
            "Providing method is not public."));
      }

      @Test
      public void is_not_static() throws Exception {
        createNativeJar(NonStaticMethod.class, ReturnAbc.class);
        String classPath = NonStaticMethod.class.getCanonicalName();
        createUserModule(format("""
            @Native("%s")
            String returnAbc();
            result = returnAbc();
            """, classPath));
        runSmoothBuild("result");
        assertFinishedWithError();
        assertSysOutContains(errorLoadingMessage("returnAbc", classPath,
            "Providing method is not static."));
      }

      @Test
      public void does_not_have_container_as_first_param() throws Exception {
        createNativeJar(WithoutContainer.class, ReturnAbc.class);
        String classPath = WithoutContainer.class.getCanonicalName();
        createUserModule(format("""
            @Native("%s")
            String returnAbc();
            result = returnAbc();
            """, classPath));
        runSmoothBuild("result");
        assertFinishedWithError();
        assertSysOutContains(errorLoadingMessage("returnAbc", classPath,
            "Providing method first parameter is not of type "
            + NativeApi.class.getCanonicalName() + ".\n"));
      }

      @Nested
      class _returns {
        @Test
        public void null_without_logging_error() throws Exception {
          createNativeJar(ReturnNull.class);
          createUserModule(format("""
            @Native("%s")
            String returnNull();
            result = returnNull();
            """, ReturnNull.class.getCanonicalName()));
          runSmoothBuild("result");
          assertFinishedWithError();
          assertSysOutContains(faultyNullReturned("returnNull"));
        }

        @Test
        public void null_and_logs_only_warning() throws Exception {
          createNativeJar(ReportWarningAndReturnNull.class);
          createUserModule(format("""
            @Native("%s")
            String reportWarning();
            result = reportWarning();
            """, ReportWarningAndReturnNull.class.getCanonicalName()));
          runSmoothBuild("result");
          assertFinishedWithError();
          assertSysOutContains("`reportWarning` has faulty native implementation: "
              + "it returned `null` but logged no error.");
        }

        @Test
        public void object_of_wrong_type() throws Exception {
          createNativeJar(BrokenIdentity.class);
          createUserModule(format("""
            @Native("%s")
            A brokenIdentity(A value);
            result = brokenIdentity(value=[]);
            """, BrokenIdentity.class.getCanonicalName()));
          runSmoothBuild("result");
          assertFinishedWithError();
          assertSysOutContains(
              faultyTypeOfReturnedObject("brokenIdentity", "[Nothing]", "String"));
        }

        @Test
        public void struct_of_wrong_type() throws Exception {
          createNativeJar(ReturnStringStruct.class);
          createUserModule(format("""
            Person {
              String firstName,
              String lastName,
            }
            @Native("%s")
            Person returnStringStruct();
            result = returnStringStruct();
            """, ReturnStringStruct.class.getCanonicalName()));
          runSmoothBuild("result");
          assertFinishedWithError();
          assertSysOutContains(
              faultyTypeOfReturnedObject("returnStringStruct", "{String,String}", "{String}"));
        }

        @Test
        public void array_of_wrong_type() throws Exception {
          createNativeJar(EmptyStringArray.class);
          createUserModule(format("""
            @Native("%s")
            [Blob] emptyStringArray();
            result = emptyStringArray();
            """, EmptyStringArray.class.getCanonicalName()));
          runSmoothBuild("result");
          assertFinishedWithError();
          assertSysOutContains(
              faultyTypeOfReturnedObject("emptyStringArray", "[Blob]", "[String]"));
        }

        @Test
        public void array_with_added_elem_of_wrong_type() throws Exception {
          createNativeJar(AddElementOfWrongTypeToArray.class);
          createUserModule(format("""
            @Native("%s")
            [Blob] addElementOfWrongTypeToArray();
            result = addElementOfWrongTypeToArray();
            """, AddElementOfWrongTypeToArray.class.getCanonicalName()));
          runSmoothBuild("result");
          assertFinishedWithError();
          assertSysOutContains(
              "`addElementOfWrongTypeToArray` threw java exception from its native code.");
          assertSysOutContains("Element type must be Blob but was String.");
        }
      }
    }
  }

  private String faultyNullReturned(String name) {
    return "`" + name
        + "` has faulty native implementation: it returned `null` but logged no error.";
  }

  private static String faultyTypeOfReturnedObject(
      String name, String declared, String actual) {
    return "`" + name + "` has faulty native implementation: Its declared result type == `"
        + declared + "` but it returned object with type == `" + actual + "`.";
  }

  private String fileNotFoundErrorMessage(String memberName) {
    return
        "Error loading native jar for `" + memberName +"`: File '{prj}/build.jar' doesn't exist.";
  }

  private static String fetchTimestamp(String text) {
    Pattern pattern = Pattern.compile(".*java.lang.UnsupportedOperationException: ([0-9]*).*",
        DOTALL);
    Matcher matcher = pattern.matcher(text);
    matcher.matches();
    return matcher.group(1);
  }

  private static String errorLoadingMessage(String referencable, String path, String message) {
    return "Error loading native implementation for `" + referencable + "` specified as `" + path +
        "`: " + message;
  }
}
