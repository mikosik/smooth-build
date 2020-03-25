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
import org.smoothbuild.lang.object.base.Array;
import org.smoothbuild.lang.object.base.Blob;
import org.smoothbuild.lang.object.base.SString;
import org.smoothbuild.lang.object.base.Struct;
import org.smoothbuild.lang.plugin.NativeApi;

public class NativeFunctionTest extends AcceptanceTestCase {
  @Test
  public void native_can_return_passed_argument() throws Exception {
    givenNativeJar(OneStringParameter.class);
    givenScript(
        "  String oneStringParameter(String string);  ",
        "  result = oneStringParameter('token');      ");
    whenSmoothBuild("result");
    thenFinishedWithSuccess();
    assertThat(artifactContent("result"))
        .isEqualTo("token");
  }

  @Test
  public void native_declaration_without_native_implementation_causes_error()
      throws Exception {
    givenNativeJar(OneStringParameter.class);
    givenScript(
        "  String function;    ",
        "  result = function;  ");
    whenSmoothBuild("result");
    thenFinishedWithError();
    thenOutputContains("Function 'function' is native but does not have native implementation.\n");
  }

  @Test
  public void native_jar_with_two_functions_with_same_name_causes_error() throws Exception {
    givenNativeJar(SameName.class, SameName2.class);
    givenScript(
        "  result = 'abc';  ");
    whenSmoothBuild("result");
    thenFinishedWithError();
    thenOutputContains(
        "Invalid function native implementation in build.jar provided by "
            + SameName2.class.getCanonicalName() + ".sameName: "
            + "Function with the same name is also provided by "
            + SameName.class.getCanonicalName() + ".sameName.\n");
  }

  @Test
  public void native_with_illegal_name_causes_error() throws Exception {
    givenNativeJar(IllegalName.class);
    givenScript(
        "  result = 'abc';  ");
    whenSmoothBuild("result");
    thenFinishedWithError();
    thenOutputContains("Invalid function native implementation in build.jar provided by "
        + IllegalName.class.getCanonicalName()
        + ".illegalName$: Name 'illegalName$' is illegal.\n");
  }

  @Test
  public void native_name_is_taken_from_annotation_not_java_method_name() throws Exception {
    givenNativeJar(DifferentJavaName.class);
    givenScript(
        "  String annotationName();    ",
        "  result = annotationName();  ");
    whenSmoothBuild("result");
    thenFinishedWithSuccess();
    assertThat(artifactContent("result"))
        .isEqualTo("abc");
  }

  @Test
  public void native_provided_by_non_public_method_causes_error() throws Exception {
    givenNativeJar(NonPublicMethod.class);
    givenScript(
        "  String oneStringParameter;    ",
        "  result = oneStringParameter;  ");
    whenSmoothBuild("result");
    thenFinishedWithError();
    thenOutputContains("Invalid function native implementation in build.jar provided by "
        + NonPublicMethod.class.getCanonicalName()
        + ".function: Providing method must be public.\n");
  }

  @Test
  public void native_provided_by_non_static_method_causes_error() throws Exception {
    givenNativeJar(NonStaticMethod.class);
    givenScript(
        "  result = 'abc';  ");
    whenSmoothBuild("result");
    thenFinishedWithError();
    thenOutputContains("Invalid function native implementation in build.jar provided by "
        + NonStaticMethod.class.getCanonicalName()
        + ".function: Providing method must be static.\n");
  }

  @Test
  public void native_without_declared_result_type_causes_error() throws Exception {
    givenNativeJar(OneStringParameter.class);
    givenScript(
        "  oneStringParameter;           ",
        "  result = oneStringParameter;  ");
    whenSmoothBuild("result");
    thenFinishedWithError();
    thenOutputContains(
        "Function 'oneStringParameter' is native so should have declared result type.\n");
  }

  @Test
  public void native_with_different_result_type_causes_error() throws Exception {
    givenNativeJar(OneStringParameter.class);
    givenScript(
        "  File oneStringParameter(String string);  ",
        "  result = oneStringParameter('abc');      ");
    whenSmoothBuild("result");
    thenFinishedWithError();
    thenOutputContains("Function 'oneStringParameter' has result type 'File' "
        + "so its native implementation result type must be " + Struct.class.getCanonicalName() +
        " but it is " + SString.class.getCanonicalName() + ".\n");
  }

  @Test
  public void native_without_container_parameter_causes_error() throws Exception {
    givenNativeJar(WithoutContainer.class);
    givenScript(
        "  result = 'abc';  ");
    whenSmoothBuild("result");
    thenFinishedWithError();
    thenOutputContains("Invalid function native implementation in build.jar provided by "
        + WithoutContainer.class.getCanonicalName()
        + ".function: Providing method should have first parameter of type "
        + NativeApi.class.getCanonicalName() + ".\n");
  }

  @Test
  public void native_with_too_many_parameters_causes_error() throws Exception {
    givenNativeJar(OneStringParameter.class);
    givenScript(
        "  String oneStringParameter;    ",
        "  result = oneStringParameter;  ");
    whenSmoothBuild("result");
    thenFinishedWithError();
    thenOutputContains(
        "Function 'oneStringParameter' has 0 parameter(s) but its native implementation "
            + "has 1 parameter(s).\n");
  }

  @Test
  public void native_with_too_few_parameters_causes_error() throws Exception {
    givenNativeJar(OneStringParameter.class);
    givenScript(
        "  String oneStringParameter(String a, String b);  ",
        "  result = oneStringParameter(a='abc', b='abc');  ");
    whenSmoothBuild("result");
    thenFinishedWithError();
    thenOutputContains(
        "Function 'oneStringParameter' has 2 parameter(s) but its native implementation "
            + "has 1 parameter(s).\n");
  }

  @Test
  public void native_with_different_parameter_name_causes_error() throws Exception {
    givenNativeJar(OneStringParameter.class);
    givenScript(
        "  String oneStringParameter(String different);  ",
        "  result = oneStringParameter('abc');           ");
    whenSmoothBuild("result");
    thenFinishedWithError();
    thenOutputContains("Function 'oneStringParameter' has parameter named 'different'"
        + " but its native implementation has parameter named 'string' at this position.\n");
  }

  @Test
  public void native_with_different_parameter_type_causes_error() throws Exception {
    givenNativeJar(OneStringParameter.class);
    givenScript(
        "  String oneStringParameter([String] string);  ",
        "  result = oneStringParameter([]);             ");
    whenSmoothBuild("result");
    thenFinishedWithError();
    thenOutputContains("Function 'oneStringParameter' parameter 'string' has type [String] "
        + "so its native implementation type must be " + Array.class.getCanonicalName()
        + " but it is " + SString.class.getCanonicalName() + ".\n");
  }

  @Test
  public void native_with_parameter_type_that_is_subtype_of_declared_causes_error()
      throws Exception {
    givenNativeJar(FileParameter.class);
    givenScript(
        "  File fileParameter(Blob file);                            ",
        "  result = fileParameter(file(toBlob('abc'), 'file.txt'));  ");
    whenSmoothBuild("result");
    thenFinishedWithError();
    thenOutputContains("Function 'fileParameter' parameter 'file' has type Blob "
        + "so its native implementation type must be " + Blob.class.getCanonicalName()
        + " but it is " + Struct.class.getCanonicalName() + ".\n");
  }

  @Test
  public void exception_from_native_is_reported_as_error() throws Exception {
    givenNativeJar(ThrowException.class);
    givenScript(
        "  Nothing throwException();  ",
        "  result = throwException;   ");
    whenSmoothBuild("result");
    thenFinishedWithError();
    thenOutputContains("Function throwException threw java exception from its native code.");
    thenOutputContains("java.lang.UnsupportedOperationException");
  }

  @Test
  public void error_thrown_as_exception_from_native_is_reported_along_errors_logged_via_native_api()
      throws Exception {
    givenNativeJar(ReportTwoErrors.class);
    givenScript(
        "  String reportTwoErrors(String message1, String message2);                   ",
        "  result = reportTwoErrors(message1='first error', message2='second error');  ");
    whenSmoothBuild("result");
    thenFinishedWithError();
    thenOutputContains("first error\n");
    thenOutputContains("second error\n");
  }

  @Test
  public void error_wrapping_exception_from_native_is_not_cached() throws Exception {
    givenNativeJar(ThrowRandomException.class);
    givenScript(
        "  String throwRandomException();  ",
        "  result = throwRandomException;  ");
    whenSmoothBuild("result");
    thenFinishedWithError();
    String timestamp1 = fetchTimestamp(output());
    whenSmoothBuild("result");
    thenFinishedWithError();
    String timestamp2 = fetchTimestamp(output());
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
    givenNativeJar(ReportError.class);
    givenScript(
        "  Nothing reportError(String message);               ",
        "  result = reportError('error_reported_is_logged');  ");
    whenSmoothBuild("result");
    thenFinishedWithError();
    thenOutputContains("error_reported_is_logged");
  }

  @Test
  public void returning_null_without_logging_error_causes_error() throws Exception {
    givenNativeJar(ReturnNull.class);
    givenScript(
        "  String returnNull();    ",
        "  result = returnNull();  ");
    whenSmoothBuild("result");
    thenFinishedWithError();
    thenOutputContains("Function returnNull has faulty native implementation: "
        + "it returned 'null' but logged no error.");
  }

  @Test
  public void returning_null_and_logs_only_warning_causes_error() throws Exception {
    givenNativeJar(ReportWarningAndReturnNull.class);
    givenScript(
        "  String reportWarning(String message);    ",
        "  result = reportWarning('test message');  ");
    whenSmoothBuild("result");
    thenFinishedWithError();
    thenOutputContains("Function reportWarning has faulty native implementation: "
        + "it returned 'null' but logged no error.");
  }

  @Test
  public void native_that_adds_element_of_wrong_type_to_array_causes_error() throws Exception {
    givenNativeJar(AddElementOfWrongTypeToArray.class);
    givenScript(
        "  [Blob] addElementOfWrongTypeToArray();  ",
        "  result = addElementOfWrongTypeToArray;  ");
    whenSmoothBuild("result");
    thenFinishedWithError();
    thenOutputContains(
        "Function addElementOfWrongTypeToArray threw java exception from its native code.");
    thenOutputContains("Element type must be Blob but was String.");
  }

  @Test
  public void native_that_returns_array_of_wrong_type_causes_error() throws Exception {
    givenNativeJar(EmptyStringArray.class);
    givenScript(
        "  [Blob] emptyStringArray();  ",
        "  result = emptyStringArray;  ");
    whenSmoothBuild("result");
    thenFinishedWithError();
    thenOutputContains("Function emptyStringArray has faulty native implementation: "
        + "Its actual result type is [Blob] but it returned object of type [String].");
  }

  @Test
  public void native_that_returns_object_of_wrong_type_causes_error() throws Exception {
    givenNativeJar(BrokenIdentity.class);
    givenScript(
        "  A brokenIdentity(A value);          ",
        "  result = brokenIdentity(value=[]);  ");
    whenSmoothBuild("result");
    thenFinishedWithError();
    thenOutputContains("Function brokenIdentity has faulty native implementation: "
        + "Its actual result type is [Nothing] but it returned object of type String.");
  }
}
