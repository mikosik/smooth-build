package org.smoothbuild.acceptance.lang;

import static com.google.common.truth.Truth.assertThat;
import static java.lang.String.format;
import static java.util.regex.Pattern.DOTALL;

import java.nio.file.Path;
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
import org.smoothbuild.acceptance.testing.ReportTwoErrors;
import org.smoothbuild.acceptance.testing.ReportWarningAndReturnNull;
import org.smoothbuild.acceptance.testing.ReturnAbc;
import org.smoothbuild.acceptance.testing.ReturnNull;
import org.smoothbuild.acceptance.testing.ReturnStringTuple;
import org.smoothbuild.acceptance.testing.StringIdentity;
import org.smoothbuild.acceptance.testing.ThrowException;
import org.smoothbuild.acceptance.testing.ThrowRandomException;
import org.smoothbuild.acceptance.testing.WithoutContainer;
import org.smoothbuild.db.object.base.Array;
import org.smoothbuild.db.object.base.Blob;
import org.smoothbuild.db.object.base.Str;
import org.smoothbuild.db.object.base.Tuple;
import org.smoothbuild.plugin.NativeApi;

public class NativeTest extends AcceptanceTestCase {
  @Nested
  class _native_value {
    @Test
    public void can_return_value() throws Exception {
      createNativeJar(ReturnAbc.class);
      createUserModule(format("""
            @Native("%s.function")
            String returnAbc;
            result = returnAbc;
            """, ReturnAbc.class.getCanonicalName()));
      runSmoothBuild("result");
      assertFinishedWithSuccess();
      assertThat(artifactFileContentAsString("result"))
          .isEqualTo("abc");
    }

    @Test
    public void without_native_jar_file_causes_error() throws Exception {
      createUserModule("""
            @Native("MissingClass.method")
            String myValue;
            """);
      runSmoothBuild("myValue");
      assertFinishedWithError();
      assertSysOutContains(fileNotFoundErrorMessage("myValue", "MissingClass.method"));
    }

    @Test
    public void without_declared_type_causes_error() throws Exception {
      createNativeJar(StringIdentity.class);
      createUserModule(format("""
            @Native("%s.function")
            stringIdentity;
            result = stringIdentity;
            """, StringIdentity.class.getCanonicalName()));
      runSmoothBuild("result");
      assertFinishedWithError();
      assertSysOutContainsParseError(2,
          "`stringIdentity` is native so it should have declared result type.\n");
    }

    @Nested
    class _error_is_reported_when {
      @Test
      public void native_path_is_illegal() throws Exception {
        createNativeJar(ReturnAbc.class);
        createUserModule("""
              @Native("ending.with.dot.")
              String returnAbc;
              result = returnAbc;
              """);
        runSmoothBuild("result");
        assertFinishedWithError();
        assertSysOutContains(errorLoadingMessage("returnAbc", "ending.with.dot.",
            "Illegal path to java method. Expected <binary class name>.<method name>, " +
                "but was `ending.with.dot.`."));
      }

      @Test
      public void java_class_is_missing() throws Exception {
        createNativeJar(ReturnAbc.class);
        createUserModule("""
              @Native("org.smoothbuild.Missing.method")
              String returnAbc;
              result = returnAbc;
              """);
        runSmoothBuild("result");
        assertFinishedWithError();
        assertSysOutContains(invalidNativePathMessage("returnAbc", "org.smoothbuild.Missing.method",
            "Class 'org.smoothbuild.Missing' does not exist in jar '{prj}/build.jar'."));
      }

      @Nested
      class _java_method {
        @Test
        public void is_missing() throws Exception {
          createNativeJar(ReturnAbc.class);
          String classPath = ReturnAbc.class.getCanonicalName();
          createUserModule(format("""
              @Native("%s.NON_EXISTING")
              String returnAbc;
              result = returnAbc;
              """, classPath));
          runSmoothBuild("result");
          assertFinishedWithError();
          assertSysOutContains(invalidNativePathMessage("returnAbc", classPath + ".NON_EXISTING",
              "Class '" + classPath + "' does not have 'NON_EXISTING' method."));
        }

        @Test
        public void has_result_type_different_than_declared_in_smooth_file() throws Exception {
          createNativeJar(ReturnAbc.class);
          String classPath = ReturnAbc.class.getCanonicalName();
          createUserModule(format("""
              @Native("%s.function")
              Blob returnAbc;
              result = returnAbc;
              """, classPath));
          runSmoothBuild("result");
          assertFinishedWithError();
          assertSysOutContains(errorLoadingMessage("returnAbc", classPath + ".function",
              "`returnAbc` declares type `Blob` so its native implementation result type must be "
                  + Blob.class.getCanonicalName() + " but it is "
                  + Str.class.getCanonicalName() + ".\n"));
        }

        @Test
        public void is_not_public() throws Exception {
          createNativeJar(NonPublicMethod.class, ReturnAbc.class);
          String classPath = NonPublicMethod.class.getCanonicalName();
          createUserModule(format("""
              @Native("%s.function")
              String returnAbc;
              result = returnAbc;
              """, classPath));
          runSmoothBuild("result");
          assertFinishedWithError();
          assertSysOutContains(errorLoadingMessage("returnAbc", classPath + ".function",
              "Providing method is not public."));
        }

        @Test
        public void is_not_static() throws Exception {
          createNativeJar(NonStaticMethod.class, ReturnAbc.class);
          String classPath = NonStaticMethod.class.getCanonicalName();
          createUserModule(format("""
              @Native("%s.function")
              String returnAbc;
              result = returnAbc;
              """, classPath));
          runSmoothBuild("result");
          assertFinishedWithError();
          assertSysOutContains(errorLoadingMessage("returnAbc", classPath + ".function",
              "Providing method is not static."));
        }

        @Test
        public void does_not_have_container_as_first_parameter() throws Exception {
          createNativeJar(WithoutContainer.class, ReturnAbc.class);
          String classPath = WithoutContainer.class.getCanonicalName();
          createUserModule(format("""
              @Native("%s.function")
              String returnAbc;
              result = returnAbc;
              """, classPath));
          runSmoothBuild("result");
          assertFinishedWithError();
          assertSysOutContains(errorLoadingMessage("returnAbc", classPath + ".function",
              "Providing method first parameter is not of type "
              + NativeApi.class.getCanonicalName() + "."));
        }

        @Test
        public void has_too_many_parameters() throws Exception {
          createNativeJar(StringIdentity.class);
          String classPath = StringIdentity.class.getCanonicalName();
          createUserModule(format("""
              @Native("%s.function")
              String stringIdentity;
              result = stringIdentity;
              """, classPath));
          runSmoothBuild("result");
          assertFinishedWithError();
          assertSysOutContains(errorLoadingMessage("stringIdentity", classPath + ".function",
              "`stringIdentity` has native implementation that has too many parameter(s) = 2"));
        }

        @Nested
        class _returns {
          @Test
          public void null_without_logging_error() throws Exception {
            createNativeJar(ReturnNull.class);
            createUserModule(format("""
                @Native("%s.function")
                String returnNull;
                result = returnNull;
                """, ReturnNull.class.getCanonicalName()));
            runSmoothBuild("result");
            assertFinishedWithError();
            assertSysOutContains("`returnNull` has faulty native implementation: "
                + "it returned `null` but logged no error.");
          }

          @Test
          public void null_and_logs_only_warning() throws Exception {
            createNativeJar(ReportWarningAndReturnNull.class);
            createUserModule(format("""
                @Native("%s.function")
                String reportWarning;
                result = reportWarning;
                """, ReportWarningAndReturnNull.class.getCanonicalName()));
            runSmoothBuild("result");
            assertFinishedWithError();
            assertSysOutContains("`reportWarning` has faulty native implementation: "
                + "it returned `null` but logged no error.");
          }

          @Test
          public void struct_of_wrong_type() throws Exception {
            createNativeJar(ReturnStringTuple.class);
            createUserModule(format("""
                Person {
                  String firstName,
                  String lastName,
                }
                @Native("%s.function")
                Person returnStringTuple;
                result = returnStringTuple;
                """, ReturnStringTuple.class.getCanonicalName()));
            runSmoothBuild("result");
            assertFinishedWithError();
            assertSysOutContains("`returnStringTuple` has faulty native implementation: " +
                "Its declared result spec == {STRING,STRING} " +
                "but it returned object with spec == {STRING}.");
          }

          @Test
          public void array_of_wrong_type() throws Exception {
            createNativeJar(EmptyStringArray.class);
            createUserModule(format("""
                @Native("%s.function")
                [Blob] emptyStringArray;
                result = emptyStringArray;
                """, EmptyStringArray.class.getCanonicalName()));
            runSmoothBuild("result");
            assertFinishedWithError();
            assertSysOutContains("`emptyStringArray` has faulty native implementation: " +
                "Its declared result spec == [BLOB] but it returned object with spec == [STRING].");
          }

          @Test
          public void array_with_added_element_of_wrong_type() throws Exception {
            createNativeJar(AddElementOfWrongTypeToArray.class);
            createUserModule(format("""
                @Native("%s.function")
                [Blob] addElementOfWrongTypeToArray;
                result = addElementOfWrongTypeToArray;
                """, AddElementOfWrongTypeToArray.class.getCanonicalName()));
            runSmoothBuild("result");
            assertFinishedWithError();
            assertSysOutContains(
                "`addElementOfWrongTypeToArray` threw java exception from its native code.");
            assertSysOutContains("Element spec must be BLOB but was STRING.");
          }
        }
      }
    }

    @Test
    public void exception_from_native_is_reported_as_error() throws Exception {
      createNativeJar(ThrowException.class);
      createUserModule(format("""
            @Native("%s.function")
            Nothing throwException;
            result = throwException;
            """, ThrowException.class.getCanonicalName()));
      runSmoothBuild("result");
      assertFinishedWithError();
      assertSysOutContains("`throwException` threw java exception from its native code.");
      assertSysOutContains("java.lang.UnsupportedOperationException");
    }

    @Test
    public void errors_reported_via_native_api_are_reported() throws Exception {
      createNativeJar(ReportTwoErrors.class);
      createUserModule(format("""
            @Native("%s.function")
            String reportTwoErrors;
            result = reportTwoErrors;
            """, ReportTwoErrors.class.getCanonicalName()));
      runSmoothBuild("result");
      assertFinishedWithError();
      assertSysOutContains("first error\n");
      assertSysOutContains("second error\n");
    }

    @Test
    public void error_wrapping_exception_from_native_is_not_cached() throws Exception {
      createNativeJar(ThrowRandomException.class);
      createUserModule(format("""
            @Native("%s.function")
            String throwRandomException;
            result = throwRandomException;
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
            @Native("%s.function")
            Nothing reportFixedError;
            result = reportFixedError;
            """, ReportFixedError.class.getCanonicalName()));
      runSmoothBuild("result");
      assertFinishedWithError();
      assertSysOutContains("some error message");
    }
  }

  @Nested
  class _native_function {
    @Test
    public void can_return_passed_argument() throws Exception {
      createNativeJar(StringIdentity.class);
      createUserModule(format("""
            @Native("%s.function")
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
            @Native("MissingClass.method")
            String myFunction();
            result = myFunction();
            """);
      runSmoothBuild("result");
      assertFinishedWithError();
      assertSysOutContains(fileNotFoundErrorMessage("myFunction", "MissingClass.method"));
    }

    @Test
    public void exception_from_native_is_reported_as_error() throws Exception {
      createNativeJar(ThrowException.class);
      createUserModule(format("""
            @Native("%s.function")
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
            @Native("%s.function")
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
            @Native("%s.function")
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
      public void native_path_is_illegal() throws Exception {
        createNativeJar(ReturnAbc.class);
        createUserModule("""
              @Native("ending.with.dot.")
              String returnAbc();
              result = returnAbc();
              """);
        runSmoothBuild("result");
        assertFinishedWithError();
        assertSysOutContains(errorLoadingMessage("returnAbc", "ending.with.dot.",
            "Illegal path to java method. Expected <binary class name>.<method name>, " +
                "but was `ending.with.dot.`."));
      }

      @Test
      public void is_missing() throws Exception {
        createNativeJar(ReturnAbc.class);
        String classPath = ReturnAbc.class.getCanonicalName();
        createUserModule(format("""
            @Native("%s.NON_EXISTING")
            String returnAbc();
            result = returnAbc();
            """, classPath));
        runSmoothBuild("result");
        assertFinishedWithError();
        assertSysOutContains(invalidNativePathMessage("returnAbc", classPath + ".NON_EXISTING",
            "Class '" + classPath + "' does not have 'NON_EXISTING' method."));
      }

      @Test
      public void has_result_type_different_than_declared_in_smooth_file() throws Exception {
        createNativeJar(StringIdentity.class);
        String classPath = StringIdentity.class.getCanonicalName();
        createUserModule(format("""
            @Native("%s.function")
            File stringIdentity(String string);
            result = stringIdentity("abc");
            """, classPath));
        runSmoothBuild("result");
        assertFinishedWithError();
        assertSysOutContains(errorLoadingMessage("stringIdentity", classPath + ".function",
            "`stringIdentity` declares type `File` "
            + "so its native implementation result type must be " + Tuple.class.getCanonicalName() +
            " but it is " + Str.class.getCanonicalName() + ".\n"));
      }

      @Test
      public void has_too_many_parameters() throws Exception {
        createNativeJar(StringIdentity.class);
        String classPath = StringIdentity.class.getCanonicalName();
        createUserModule(format("""
            @Native("%s.function")
            String stringIdentity();
            result = stringIdentity();
            """, classPath));
        runSmoothBuild("result");
        assertFinishedWithError();
        assertSysOutContains(errorLoadingMessage("stringIdentity", classPath + ".function",
            "Function `stringIdentity` has 0 parameter(s) but its native implementation "
                + "has 1 parameter(s)."));
      }

      @Test
      public void has_too_few_parameters() throws Exception {
        createNativeJar(StringIdentity.class);
        String classPath = StringIdentity.class.getCanonicalName();
        createUserModule(format("""
            @Native("%s.function")
            String stringIdentity(String a, String b);
            result = stringIdentity(a="abc", b="abc");
            """, classPath));
        runSmoothBuild("result");
        assertFinishedWithError();
        assertSysOutContains(errorLoadingMessage("stringIdentity", classPath + ".function",
            "Function `stringIdentity` has 2 parameter(s) but its native implementation "
                + "has 1 parameter(s)."));
      }

      @Test
      public void has_different_parameter_type() throws Exception {
        createNativeJar(StringIdentity.class);
        String classPath = StringIdentity.class.getCanonicalName();
        createUserModule(format("""
            @Native("%s.function")
            String stringIdentity([String] string);
            result = stringIdentity([]);
            """, classPath));
        runSmoothBuild("result");
        assertFinishedWithError();
        assertSysOutContains(errorLoadingMessage("stringIdentity", classPath + ".function",
            "Function `stringIdentity` parameter `string` has type `[String]` "
            + "so its native implementation type must be " + Array.class.getCanonicalName()
            + " but it is " + Str.class.getCanonicalName() + "."));
      }

      @Test
      public void is_not_public() throws Exception {
        createNativeJar(NonPublicMethod.class, ReturnAbc.class);
        String classPath = NonPublicMethod.class.getCanonicalName();
        createUserModule(format("""
            @Native("%s.function")
            String returnAbc;
            result = returnAbc;
            """, classPath));
        runSmoothBuild("result");
        assertFinishedWithError();
        assertSysOutContains(errorLoadingMessage("returnAbc", classPath + ".function",
            "Providing method is not public."));
      }

      @Test
      public void is_not_static() throws Exception {
        createNativeJar(NonStaticMethod.class, ReturnAbc.class);
        String classPath = NonStaticMethod.class.getCanonicalName();
        createUserModule(format("""
            @Native("%s.function")
            String returnAbc;
            result = returnAbc;
            """, classPath));
        runSmoothBuild("result");
        assertFinishedWithError();
        assertSysOutContains(errorLoadingMessage("returnAbc", classPath + ".function",
            "Providing method is not static."));
      }

      @Test
      public void does_not_have_container_as_first_parameter() throws Exception {
        createNativeJar(WithoutContainer.class, ReturnAbc.class);
        String classPath = WithoutContainer.class.getCanonicalName();
        createUserModule(format("""
            @Native("%s.function")
            String returnAbc;
            result = returnAbc;
            """, classPath));
        runSmoothBuild("result");
        assertFinishedWithError();
        assertSysOutContains(errorLoadingMessage("returnAbc", classPath + ".function",
            "Providing method first parameter is not of type "
            + NativeApi.class.getCanonicalName() + ".\n"));
      }

      @Nested
      class _returns {
        @Test
        public void null_without_logging_error() throws Exception {
          createNativeJar(ReturnNull.class);
          createUserModule(format("""
            @Native("%s.function")
            String returnNull();
            result = returnNull();
            """, ReturnNull.class.getCanonicalName()));
          runSmoothBuild("result");
          assertFinishedWithError();
          assertSysOutContains("`returnNull` has faulty native implementation: "
              + "it returned `null` but logged no error.");
        }

        @Test
        public void null_and_logs_only_warning() throws Exception {
          createNativeJar(ReportWarningAndReturnNull.class);
          createUserModule(format("""
            @Native("%s.function")
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
            @Native("%s.function")
            A brokenIdentity(A value);
            result = brokenIdentity(value=[]);
            """, BrokenIdentity.class.getCanonicalName()));
          runSmoothBuild("result");
          assertFinishedWithError();
          assertSysOutContains("`brokenIdentity` has faulty native implementation: " +
              "Its declared result spec == [NOTHING] but it returned object with spec == STRING.");
        }

        @Test
        public void struct_of_wrong_type() throws Exception {
          createNativeJar(ReturnStringTuple.class);
          createUserModule(format("""
            Person {
              String firstName,
              String lastName,
            }
            @Native("%s.function")
            Person returnStringTuple();
            result = returnStringTuple();
            """, ReturnStringTuple.class.getCanonicalName()));
          runSmoothBuild("result");
          assertFinishedWithError();
          assertSysOutContains(
              "`returnStringTuple` has faulty native implementation: Its declared " +
                  "result spec == {STRING,STRING} but it returned object with spec == {STRING}.");
        }

        @Test
        public void array_of_wrong_type() throws Exception {
          createNativeJar(EmptyStringArray.class);
          createUserModule(format("""
            @Native("%s.function")
            [Blob] emptyStringArray();
            result = emptyStringArray();
            """, EmptyStringArray.class.getCanonicalName()));
          runSmoothBuild("result");
          assertFinishedWithError();
          assertSysOutContains("`emptyStringArray` has faulty native implementation: "
              + "Its declared result spec == [BLOB] but it returned object with spec == [STRING].");
        }

        @Test
        public void array_with_added_element_of_wrong_type() throws Exception {
          createNativeJar(AddElementOfWrongTypeToArray.class);
          createUserModule(format("""
            @Native("%s.function")
            [Blob] addElementOfWrongTypeToArray();
            result = addElementOfWrongTypeToArray();
            """, AddElementOfWrongTypeToArray.class.getCanonicalName()));
          runSmoothBuild("result");
          assertFinishedWithError();
          assertSysOutContains(
              "`addElementOfWrongTypeToArray` threw java exception from its native code.");
          assertSysOutContains("Element spec must be BLOB but was STRING.");
        }
      }
    }
  }

  private String fileNotFoundErrorMessage(String memberName, String methodPath) {
    return "Error loading native implementation for `" + memberName + "` specified as `" +
        methodPath + "`: Error reading file '{prj}/build.jar'.";
  }

  private static String fetchTimestamp(String text) {
    Pattern pattern = Pattern.compile(".*java.lang.UnsupportedOperationException: ([0-9]*).*",
        DOTALL);
    Matcher matcher = pattern.matcher(text);
    matcher.matches();
    return matcher.group(1);
  }

  private static String invalidNativePathMessage(
      String referencable, String path, String message) {
    return errorLoadingMessage(
        referencable, path, "Invalid native path `" + path + "`: " + message);
  }

  private static String errorLoadingMessage(String referencable, String path, String message) {
    return "Error loading native implementation for `" + referencable + "` specified as `" + path +
        "`: " + message;
  }

  private Path buildJarPath() {
    return projectDirOption().resolve("build.jar").normalize();
  }
}
