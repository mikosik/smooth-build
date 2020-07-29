package org.smoothbuild.acceptance.lang;

import static com.google.common.truth.Truth.assertThat;
import static java.util.regex.Pattern.DOTALL;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.jupiter.api.Test;
import org.smoothbuild.acceptance.AcceptanceTestCase;
import org.smoothbuild.acceptance.testing.AddElementOfWrongTypeToArray;
import org.smoothbuild.acceptance.testing.BrokenIdentity;
import org.smoothbuild.acceptance.testing.DifferentJavaName;
import org.smoothbuild.acceptance.testing.EmptyStringArray;
import org.smoothbuild.acceptance.testing.FileParameter;
import org.smoothbuild.acceptance.testing.IllegalName;
import org.smoothbuild.acceptance.testing.NonPublicMethod;
import org.smoothbuild.acceptance.testing.NonStaticMethod;
import org.smoothbuild.acceptance.testing.OneStringParameter;
import org.smoothbuild.acceptance.testing.ReportError;
import org.smoothbuild.acceptance.testing.ReportTwoErrors;
import org.smoothbuild.acceptance.testing.ReportWarningAndReturnNull;
import org.smoothbuild.acceptance.testing.ReturnNull;
import org.smoothbuild.acceptance.testing.SameName;
import org.smoothbuild.acceptance.testing.SameName2;
import org.smoothbuild.acceptance.testing.ThrowException;
import org.smoothbuild.acceptance.testing.ThrowRandomException;
import org.smoothbuild.acceptance.testing.WithoutContainer;
import org.smoothbuild.db.record.base.Array;
import org.smoothbuild.db.record.base.Blob;
import org.smoothbuild.db.record.base.RString;
import org.smoothbuild.db.record.base.Tuple;
import org.smoothbuild.plugin.NativeApi;

public class NativeFunctionTest extends AcceptanceTestCase {
  @Test
  public void native_can_return_passed_argument() throws Exception {
    createNativeJar(OneStringParameter.class);
    createUserModule(
        "  String oneStringParameter(String string);  ",
        "  result = oneStringParameter('token');      ");
    runSmoothBuild("result");
    assertFinishedWithSuccess();
    assertThat(artifactFileContentAsString("result"))
        .isEqualTo("token");
  }

  @Test
  public void native_declaration_without_native_implementation_causes_error()
      throws Exception {
    createNativeJar(OneStringParameter.class);
    createUserModule(
        "  String function;    ",
        "  result = function;  ");
    runSmoothBuild("result");
    assertFinishedWithError();
    assertSysOutContains("Function 'function' is native but does not have native implementation.\n");
  }

  @Test
  public void native_jar_with_two_functions_with_same_name_causes_error() throws Exception {
    createNativeJar(SameName.class, SameName2.class);
    createUserModule(
        "  result = 'abc';  ");
    runSmoothBuild("result");
    assertFinishedWithError();
    assertSysOutContains(invalidFunctionProvidedBy(SameName2.class) + ".sameName: "
        + "Function with the same name is also provided by "
        + SameName.class.getCanonicalName() + ".sameName.\n");
  }

  @Test
  public void native_with_illegal_name_causes_error() throws Exception {
    createNativeJar(IllegalName.class);
    createUserModule(
        "  result = 'abc';  ");
    runSmoothBuild("result");
    assertFinishedWithError();
    assertSysOutContains(invalidFunctionProvidedBy(IllegalName.class)
        + ".illegalName$: Name 'illegalName$' is illegal.\n");
  }

  @Test
  public void native_name_is_taken_from_annotation_not_java_method_name() throws Exception {
    createNativeJar(DifferentJavaName.class);
    createUserModule(
        "  String annotationName();    ",
        "  result = annotationName();  ");
    runSmoothBuild("result");
    assertFinishedWithSuccess();
    assertThat(artifactFileContentAsString("result"))
        .isEqualTo("abc");
  }

  @Test
  public void native_provided_by_non_public_method_causes_error() throws Exception {
    createNativeJar(NonPublicMethod.class);
    createUserModule(
        "  String oneStringParameter;    ",
        "  result = oneStringParameter;  ");
    runSmoothBuild("result");
    assertFinishedWithError();
    assertSysOutContains(invalidFunctionProvidedBy(NonPublicMethod.class)
        + ".function: Providing method must be public.\n");
  }

  @Test
  public void native_provided_by_non_static_method_causes_error() throws Exception {
    createNativeJar(NonStaticMethod.class);
    createUserModule(
        "  result = 'abc';  ");
    runSmoothBuild("result");
    assertFinishedWithError();
    assertSysOutContains(invalidFunctionProvidedBy(NonStaticMethod.class)
        + ".function: Providing method must be static.\n");
  }

  @Test
  public void native_without_declared_result_type_causes_error() throws Exception {
    createNativeJar(OneStringParameter.class);
    createUserModule(
        "  oneStringParameter;           ",
        "  result = oneStringParameter;  ");
    runSmoothBuild("result");
    assertFinishedWithError();
    assertSysOutContains(
        "Function 'oneStringParameter' is native so should have declared result type.\n");
  }

  @Test
  public void native_with_different_result_type_causes_error() throws Exception {
    createNativeJar(OneStringParameter.class);
    createUserModule(
        "  File oneStringParameter(String string);  ",
        "  result = oneStringParameter('abc');      ");
    runSmoothBuild("result");
    assertFinishedWithError();
    assertSysOutContains("Function 'oneStringParameter' has result type 'File' "
        + "so its native implementation result type must be " + Tuple.class.getCanonicalName() +
        " but it is " + RString.class.getCanonicalName() + ".\n");
  }

  @Test
  public void native_without_container_parameter_causes_error() throws Exception {
    createNativeJar(WithoutContainer.class);
    createUserModule(
        "  result = 'abc';  ");
    runSmoothBuild("result");
    assertFinishedWithError();
    assertSysOutContains(invalidFunctionProvidedBy(WithoutContainer.class)
        + ".function: Providing method should have first parameter of type "
        + NativeApi.class.getCanonicalName() + ".\n");
  }

  @Test
  public void native_with_too_many_parameters_causes_error() throws Exception {
    createNativeJar(OneStringParameter.class);
    createUserModule(
        "  String oneStringParameter;    ",
        "  result = oneStringParameter;  ");
    runSmoothBuild("result");
    assertFinishedWithError();
    assertSysOutContains(
        "Function 'oneStringParameter' has 0 parameter(s) but its native implementation "
            + "has 1 parameter(s).\n");
  }

  @Test
  public void native_with_too_few_parameters_causes_error() throws Exception {
    createNativeJar(OneStringParameter.class);
    createUserModule(
        "  String oneStringParameter(String a, String b);  ",
        "  result = oneStringParameter(a='abc', b='abc');  ");
    runSmoothBuild("result");
    assertFinishedWithError();
    assertSysOutContains(
        "Function 'oneStringParameter' has 2 parameter(s) but its native implementation "
            + "has 1 parameter(s).\n");
  }

  @Test
  public void native_with_different_parameter_type_causes_error() throws Exception {
    createNativeJar(OneStringParameter.class);
    createUserModule(
        "  String oneStringParameter([String] string);  ",
        "  result = oneStringParameter([]);             ");
    runSmoothBuild("result");
    assertFinishedWithError();
    assertSysOutContains("Function 'oneStringParameter' parameter 'string' has type [String] "
        + "so its native implementation type must be " + Array.class.getCanonicalName()
        + " but it is " + RString.class.getCanonicalName() + ".\n");
  }

  @Test
  public void native_with_parameter_type_that_is_subtype_of_declared_causes_error()
      throws Exception {
    createNativeJar(FileParameter.class);
    createUserModule(
        "  File fileParameter(Blob file);                            ",
        "  result = fileParameter(file(toBlob('abc'), 'file.txt'));  ");
    runSmoothBuild("result");
    assertFinishedWithError();
    assertSysOutContains("Function 'fileParameter' parameter 'file' has type Blob "
        + "so its native implementation type must be " + Blob.class.getCanonicalName()
        + " but it is " + Tuple.class.getCanonicalName() + ".\n");
  }

  @Test
  public void exception_from_native_is_reported_as_error() throws Exception {
    createNativeJar(ThrowException.class);
    createUserModule(
        "  Nothing throwException();  ",
        "  result = throwException;   ");
    runSmoothBuild("result");
    assertFinishedWithError();
    assertSysOutContains("Function throwException threw java exception from its native code.");
    assertSysOutContains("java.lang.UnsupportedOperationException");
  }

  @Test
  public void error_thrown_as_exception_from_native_is_reported_along_errors_logged_via_native_api()
      throws Exception {
    createNativeJar(ReportTwoErrors.class);
    createUserModule(
        "  String reportTwoErrors(String message1, String message2);                   ",
        "  result = reportTwoErrors(message1='first error', message2='second error');  ");
    runSmoothBuild("result");
    assertFinishedWithError();
    assertSysOutContains("first error\n");
    assertSysOutContains("second error\n");
  }

  @Test
  public void error_wrapping_exception_from_native_is_not_cached() throws Exception {
    createNativeJar(ThrowRandomException.class);
    createUserModule(
        "  String throwRandomException();  ",
        "  result = throwRandomException;  ");
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
    createNativeJar(ReportError.class);
    createUserModule(
        "  Nothing reportError(String message);               ",
        "  result = reportError('error_reported_is_logged');  ");
    runSmoothBuild("result");
    assertFinishedWithError();
    assertSysOutContains("error_reported_is_logged");
  }

  @Test
  public void returning_null_without_logging_error_causes_error() throws Exception {
    createNativeJar(ReturnNull.class);
    createUserModule(
        "  String returnNull();    ",
        "  result = returnNull();  ");
    runSmoothBuild("result");
    assertFinishedWithError();
    assertSysOutContains("Function returnNull has faulty native implementation: "
        + "it returned 'null' but logged no error.");
  }

  @Test
  public void returning_null_and_logs_only_warning_causes_error() throws Exception {
    createNativeJar(ReportWarningAndReturnNull.class);
    createUserModule(
        "  String reportWarning(String message);    ",
        "  result = reportWarning('test message');  ");
    runSmoothBuild("result");
    assertFinishedWithError();
    assertSysOutContains("Function reportWarning has faulty native implementation: "
        + "it returned 'null' but logged no error.");
  }

  @Test
  public void native_that_adds_element_of_wrong_type_to_array_causes_error() throws Exception {
    createNativeJar(AddElementOfWrongTypeToArray.class);
    createUserModule(
        "  [Blob] addElementOfWrongTypeToArray();  ",
        "  result = addElementOfWrongTypeToArray;  ");
    runSmoothBuild("result");
    assertFinishedWithError();
    assertSysOutContains(
        "Function addElementOfWrongTypeToArray threw java exception from its native code.");
    assertSysOutContains("Element spec must be BLOB but was STRING.");
  }

  @Test
  public void native_that_returns_array_of_wrong_type_causes_error() throws Exception {
    createNativeJar(EmptyStringArray.class);
    createUserModule(
        "  [Blob] emptyStringArray();  ",
        "  result = emptyStringArray;  ");
    runSmoothBuild("result");
    assertFinishedWithError();
    assertSysOutContains("Function emptyStringArray has faulty native implementation: "
        + "Its declared result spec == [BLOB] but it returned record with spec == [STRING].");
  }

  @Test
  public void native_that_returns_object_of_wrong_type_causes_error() throws Exception {
    createNativeJar(BrokenIdentity.class);
    createUserModule(
        "  A brokenIdentity(A value);          ",
        "  result = brokenIdentity(value=[]);  ");
    runSmoothBuild("result");
    assertFinishedWithError();
    assertSysOutContains("Function brokenIdentity has faulty native implementation: "
        + "Its declared result spec == [NOTHING] but it returned record with spec == STRING.");
  }

  private String invalidFunctionProvidedBy(Class<?> clazz) {
    return "Invalid function native implementation in "
        + projectDirOption().resolve("build.jar").normalize()
        + " provided by " + clazz.getCanonicalName();
  }
}
