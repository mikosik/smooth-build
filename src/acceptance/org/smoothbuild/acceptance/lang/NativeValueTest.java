package org.smoothbuild.acceptance.lang;

import static com.google.common.truth.Truth.assertThat;
import static java.util.regex.Pattern.DOTALL;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.smoothbuild.acceptance.AcceptanceTestCase;
import org.smoothbuild.acceptance.testing.AddElementOfWrongTypeToArray;
import org.smoothbuild.acceptance.testing.DifferentJavaName;
import org.smoothbuild.acceptance.testing.EmptyStringArray;
import org.smoothbuild.acceptance.testing.OneStringParameter;
import org.smoothbuild.acceptance.testing.ReportFixedError;
import org.smoothbuild.acceptance.testing.ReportTwoErrors;
import org.smoothbuild.acceptance.testing.ReportWarningAndReturnNull;
import org.smoothbuild.acceptance.testing.ReturnAbc;
import org.smoothbuild.acceptance.testing.ReturnNull;
import org.smoothbuild.acceptance.testing.ReturnStringTuple;
import org.smoothbuild.acceptance.testing.ThrowException;
import org.smoothbuild.acceptance.testing.ThrowRandomException;
import org.smoothbuild.db.object.base.Blob;
import org.smoothbuild.db.object.base.Str;

public class NativeValueTest extends AcceptanceTestCase {
  @Test
  public void native_can_returns_value() throws Exception {
    createNativeJar(ReturnAbc.class);
    createUserModule("""
            String returnAbc;
            result = returnAbc;
            """);
    runSmoothBuild("result");
    assertFinishedWithSuccess();
    assertThat(artifactFileContentAsString("result"))
        .isEqualTo("abc");
  }

  @Test
  public void native_declaration_without_native_implementation_causes_error()
      throws Exception {
    createNativeJar(ReturnAbc.class);
    createUserModule("""
            String myValue;
            """);
    runSmoothBuild("myValue");
    assertFinishedWithError();
    assertSysOutContains("Error loading native implementation for `myValue`. Jar '"
        + projectDirOption().resolve("build.jar").normalize()
        + "' does not contain implementation for `myValue`. It contains {returnAbc}.");
  }

  @Test
  public void native_declaration_without_native_jar_file_causes_error()
      throws Exception {
    createUserModule("""
            String myValue;
            """);
    runSmoothBuild("myValue");
    assertFinishedWithError();
    assertSysOutContains("Error loading native implementation for `myValue`. Cannot find '"
        + projectDirOption().resolve("build.jar").normalize() + "'.");
  }

  @Test
  public void native_name_is_taken_from_annotation_not_java_method_name() throws Exception {
    createNativeJar(DifferentJavaName.class);
    createUserModule("""
            String annotationName;
            result = annotationName;
            """);
    runSmoothBuild("result");
    assertFinishedWithSuccess();
    assertThat(artifactFileContentAsString("result"))
        .isEqualTo("abc");
  }

  @Test
  public void native_without_declared_type_causes_error() throws Exception {
    createNativeJar(OneStringParameter.class);
    createUserModule("""
            oneStringParameter;
            result = oneStringParameter;
            """);
    runSmoothBuild("result");
    assertFinishedWithError();
    assertSysOutContains("`oneStringParameter` is native so it should have type declaration.\n");
  }

  @Test
  public void native_with_different_type_causes_error() throws Exception {
    createNativeJar(ReturnAbc.class);
    createUserModule("""
            Blob returnAbc;
            result = returnAbc;
            """);
    runSmoothBuild("result");
    assertFinishedWithError();
    assertSysOutContains("`returnAbc` declares type `Blob` "
        + "so its native implementation result type must be " + Blob.class.getCanonicalName() +
        " but it is " + Str.class.getCanonicalName() + ".\n");
  }

  @Test
  public void native_with_too_many_parameters_causes_error() throws Exception {
    createNativeJar(OneStringParameter.class);
    createUserModule("""
            String oneStringParameter;
            result = oneStringParameter;
            """);
    runSmoothBuild("result");
    assertFinishedWithError();
    assertSysOutContains(
        "`oneStringParameter` has native implementation that has too many parameter(s) = 2");
  }

  @Test
  public void exception_from_native_is_reported_as_error() throws Exception {
    createNativeJar(ThrowException.class);
    createUserModule("""
            Nothing throwException;
            result = throwException;
            """);
    runSmoothBuild("result");
    assertFinishedWithError();
    assertSysOutContains("`throwException` threw java exception from its native code.");
    assertSysOutContains("java.lang.UnsupportedOperationException");
  }

  @Test
  public void errors_reported_via_native_api_are_reported()
      throws Exception {
    createNativeJar(ReportTwoErrors.class);
    createUserModule("""
            String reportTwoErrors;
            result = reportTwoErrors;
            """);
    runSmoothBuild("result");
    assertFinishedWithError();
    assertSysOutContains("first error\n");
    assertSysOutContains("second error\n");
  }

  @Test
  public void error_wrapping_exception_from_native_is_not_cached() throws Exception {
    createNativeJar(ThrowRandomException.class);
    createUserModule("""
            String throwRandomException;
            result = throwRandomException;
            """);
    runSmoothBuild("result");
    assertFinishedWithError();
    String timestamp1 = fetchTimestamp(sysOut());
    runSmoothBuild("result");
    assertFinishedWithError();
    String timestamp2 = fetchTimestamp(sysOut());
    assertThat(timestamp1)
        .isNotEqualTo(timestamp2);
  }

  private static String fetchTimestamp(String text) {
    Pattern pattern = Pattern.compile(".*java.lang.UnsupportedOperationException: ([0-9]*).*",
        DOTALL);
    Matcher matcher = pattern.matcher(text);
    matcher.matches();
    return matcher.group(1);
  }

  @Test
  public void error_reported_is_logged() throws Exception {
    createNativeJar(ReportFixedError.class);
    createUserModule("""
            Nothing reportFixedError;
            result = reportFixedError;
            """);
    runSmoothBuild("result");
    assertFinishedWithError();
    assertSysOutContains("some error message");
  }

  @Nested
  class error_is_caused_by_native_returning {
    @Test
    public void null_without_logging_error() throws Exception {
      createNativeJar(ReturnNull.class);
      createUserModule("""
            String returnNull;
            result = returnNull;
            """);
      runSmoothBuild("result");
      assertFinishedWithError();
      assertSysOutContains("`returnNull` has faulty native implementation: "
          + "it returned `null` but logged no error.");
    }

    @Test
    public void null_and_logs_only_warning() throws Exception {
      createNativeJar(ReportWarningAndReturnNull.class);
      createUserModule("""
            String reportWarning;
            result = reportWarning;
            """);
      runSmoothBuild("result");
      assertFinishedWithError();
      assertSysOutContains("`reportWarning` has faulty native implementation: "
          + "it returned `null` but logged no error.");
    }

    @Test
    public void struct_of_wrong_type() throws Exception {
      createNativeJar(ReturnStringTuple.class);
      createUserModule("""
            Person {
              String firstName,
              String lastName,
            }
            Person returnStringTuple;
            result = returnStringTuple;
            """);
      runSmoothBuild("result");
      assertFinishedWithError();
      assertSysOutContains("`returnStringTuple` has faulty native implementation: Its declared " +
          "result spec == {STRING,STRING} but it returned object with spec == {STRING}.");
    }

    @Test
    public void array_of_wrong_type() throws Exception {
      createNativeJar(EmptyStringArray.class);
      createUserModule("""
            [Blob] emptyStringArray;
            result = emptyStringArray;
            """);
      runSmoothBuild("result");
      assertFinishedWithError();
      assertSysOutContains("`emptyStringArray` has faulty native implementation: "
          + "Its declared result spec == [BLOB] but it returned object with spec == [STRING].");
    }

    @Test
    public void array_with_added_element_of_wrong_type() throws Exception {
      createNativeJar(AddElementOfWrongTypeToArray.class);
      createUserModule("""
            [Blob] addElementOfWrongTypeToArray;
            result = addElementOfWrongTypeToArray;
            """);
      runSmoothBuild("result");
      assertFinishedWithError();
      assertSysOutContains(
          "`addElementOfWrongTypeToArray` threw java exception from its native code.");
      assertSysOutContains("Element spec must be BLOB but was STRING.");
    }
  }
}
