package org.smoothbuild.acceptance.lang;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.smoothbuild.lang.type.Types.BLOB;
import static org.smoothbuild.lang.type.Types.BLOB_ARRAY;
import static org.smoothbuild.lang.type.Types.STRING;
import static org.smoothbuild.lang.type.Types.STRING_ARRAY;
import static org.testory.Testory.then;

import org.junit.Test;
import org.smoothbuild.acceptance.AcceptanceTestCase;
import org.smoothbuild.acceptance.lang.nativ.BlobArrayParameter;
import org.smoothbuild.acceptance.lang.nativ.FileParameter;
import org.smoothbuild.acceptance.lang.nativ.IllegalName;
import org.smoothbuild.acceptance.lang.nativ.NonPublicMethod;
import org.smoothbuild.acceptance.lang.nativ.NonStaticMethod;
import org.smoothbuild.acceptance.lang.nativ.OneStringParameter;
import org.smoothbuild.acceptance.lang.nativ.ReportError;
import org.smoothbuild.acceptance.lang.nativ.ReportWarningAndReturnNull;
import org.smoothbuild.acceptance.lang.nativ.ReturnNull;
import org.smoothbuild.acceptance.lang.nativ.SameName;
import org.smoothbuild.acceptance.lang.nativ.SameName2;
import org.smoothbuild.acceptance.lang.nativ.ThrowException;
import org.smoothbuild.acceptance.lang.nativ.WithoutContainer;
import org.smoothbuild.lang.plugin.Container;
import org.smoothbuild.lang.type.Types;

public class NativeFunctionTest extends AcceptanceTestCase {
  @Test
  public void native_can_return_passed_argument() throws Exception {
    givenNativeJar(OneStringParameter.class);
    givenScript("String oneStringParameter(String string);"
        + "      result = oneStringParameter('token');");
    whenSmoothBuild("result");
    thenFinishedWithSuccess();
    then(artifactContent("result"), equalTo("token"));
  }

  @Test
  public void native_declaration_without_native_implementation_causes_error()
      throws Exception {
    givenNativeJar(OneStringParameter.class);
    givenScript("String function;"
        + "      result = function;");
    whenSmoothBuild("result");
    thenFinishedWithError();
    then(output(), containsString(
        "Function 'function' is native but does not have native implementation.\n"));
  }

  @Test
  public void native_jar_with_two_functions_with_same_name_causes_error() throws Exception {
    givenNativeJar(SameName.class, SameName2.class);
    givenScript("result = 'abc';");
    whenSmoothBuild("result");
    thenFinishedWithError();
    then(output(), containsString(
        "Invalid native function implementation in build.jar provided by "
            + SameName2.class.getCanonicalName() + ".sameName: "
            + "Function with the same name is also provided by "
            + SameName.class.getCanonicalName() + ".sameName.\n"));
  }

  @Test
  public void native_with_illegal_name_causes_error() throws Exception {
    givenNativeJar(IllegalName.class);
    givenScript("result = 'abc';");
    whenSmoothBuild("result");
    thenFinishedWithError();
    then(output(), containsString("Invalid native function implementation in build.jar provided by "
        + IllegalName.class.getCanonicalName()
        + ".illegalName$: Name 'illegalName$' is illegal.\n"));
  }

  @Test
  public void native_provided_by_non_public_method_causes_error() throws Exception {
    givenNativeJar(NonPublicMethod.class);
    givenScript("result = 'abc';");
    whenSmoothBuild("result");
    thenFinishedWithError();
    then(output(), containsString("Invalid native function implementation in build.jar provided by "
        + NonPublicMethod.class.getCanonicalName()
        + ".function: Providing method must be public.\n"));
  }

  @Test
  public void native_provided_by_non_static_method_causes_error() throws Exception {
    givenNativeJar(NonStaticMethod.class);
    givenScript("result = 'abc';");
    whenSmoothBuild("result");
    thenFinishedWithError();
    then(output(), containsString("Invalid native function implementation in build.jar provided by "
        + NonStaticMethod.class.getCanonicalName()
        + ".function: Providing method must be static.\n"));
  }

  @Test
  public void native_without_declared_result_type_causes_error() throws Exception {
    givenNativeJar(OneStringParameter.class);
    givenScript("oneStringParameter;\n"
        + "      result = oneStringParameter;");
    whenSmoothBuild("result");
    thenFinishedWithError();
    then(output(), containsString(
        "Function 'oneStringParameter' is native so should have declared result type.\n"));
  }

  @Test
  public void native_with_different_result_type_causes_error() throws Exception {
    givenNativeJar(OneStringParameter.class);
    givenScript("File oneStringParameter(String string);\n"
        + "      result = oneStringParameter('abc');");
    whenSmoothBuild("result");
    thenFinishedWithError();
    then(output(), containsString("Function 'oneStringParameter' has result type File "
        + "so its native implementation result type must be org.smoothbuild.lang.value.SFile "
        + "but it is org.smoothbuild.lang.value.SString.\n"));
  }

  @Test
  public void native_without_container_parameter_causes_error() throws Exception {
    givenNativeJar(WithoutContainer.class);
    givenScript("result = 'abc';");
    whenSmoothBuild("result");
    thenFinishedWithError();
    then(output(), containsString("Invalid native function implementation in build.jar provided by "
        + WithoutContainer.class.getCanonicalName()
        + ".function: Providing method should have first parameter of type "
        + Container.class.getCanonicalName() + ".\n"));
  }

  @Test
  public void native_with_too_many_parameters_causes_error() throws Exception {
    givenNativeJar(OneStringParameter.class);
    givenScript("String oneStringParameter;\n"
        + "      result = oneStringParameter;");
    whenSmoothBuild("result");
    thenFinishedWithError();
    then(output(), containsString(
        "Function 'oneStringParameter' has 0 parameter(s) but its native implementation "
            + "has 1 parameter(s).\n"));
  }

  @Test
  public void native_with_too_few_parameters_causes_error() throws Exception {
    givenNativeJar(OneStringParameter.class);
    givenScript("String oneStringParameter(String a, String b);\n"
        + "      result = oneStringParameter(a='abc', b='abc');");
    whenSmoothBuild("result");
    thenFinishedWithError();
    then(output(), containsString(
        "Function 'oneStringParameter' has 2 parameter(s) but its native implementation "
            + "has 1 parameter(s).\n"));
  }

  @Test
  public void native_with_different_parameter_name_causes_error() throws Exception {
    givenNativeJar(OneStringParameter.class);
    givenScript("String oneStringParameter(String different);\n"
        + "      result = oneStringParameter('abc');");
    whenSmoothBuild("result");
    thenFinishedWithError();
    then(output(), containsString(
        "Function 'oneStringParameter' has parameter named 'different'"
            + " but its native implementation has parameter named 'string' at this position.\n"));
  }

  @Test
  public void native_with_different_parameter_type_causes_error() throws Exception {
    givenNativeJar(OneStringParameter.class);
    givenScript("String oneStringParameter([String] string);\n"
        + "      result = oneStringParameter([]);");
    whenSmoothBuild("result");
    thenFinishedWithError();
    then(output(), containsString(
        "Function 'oneStringParameter' parameter 'string' has type [String] "
            + "so its native implementation type must be " + STRING_ARRAY.jType()
            + " but it is " + STRING.jType() + ".\n"));
  }

  @Test
  public void native_with_different_array_parameter_type_causes_error()
      throws Exception {
    givenNativeJar(BlobArrayParameter.class);
    givenScript("[Blob] blobArrayParameter([String] array);\n"
        + "      result = blobArrayParameter([]);");
    whenSmoothBuild("result");
    thenFinishedWithError();
    then(output(), containsString(
        "Function 'blobArrayParameter' parameter 'array' has type [String] "
            + "so its native implementation type must be " + STRING_ARRAY.jType()
            + " but it is " + BLOB_ARRAY.jType() + ".\n"));
  }

  @Test
  public void native_with_parameter_type_that_is_subtype_of_declared_causes_error()
      throws Exception {
    givenNativeJar(FileParameter.class);
    givenScript("File fileParameter(Blob file);\n"
        + "      result = fileParameter(file('//file.txt'));");
    whenSmoothBuild("result");
    thenFinishedWithError();
    then(output(), containsString(
        "Function 'fileParameter' parameter 'file' has type Blob "
            + "so its native implementation type must be " + BLOB.jType()
            + " but it is " + Types.FILE.jType() + ".\n"));
  }

  @Test
  public void exception_from_native_is_reported_as_error() throws Exception {
    givenNativeJar(ThrowException.class);
    givenScript("String throwException();\n"
        + "      result = throwException;");
    whenSmoothBuild("result");
    thenFinishedWithError();
    then(output(), containsString(
        "Function throwException threw java exception from its native code:\n"));
    then(output(), containsString("java.lang.UnsupportedOperationException"));
  }

  @Test
  public void error_reported_is_logged() throws Exception {
    givenNativeJar(ReportError.class);
    givenScript("String reportError(String message);\n"
        + "      result = reportError('error_reported_is_logged');");
    whenSmoothBuild("result");
    thenFinishedWithError();
    then(output(), containsString("error_reported_is_logged"));
  }

  @Test
  public void returning_null_without_logging_error_causes_error() throws Exception {
    givenNativeJar(ReturnNull.class);
    givenScript("String returnNull();\n"
        + "      result = returnNull();");
    whenSmoothBuild("result");
    thenFinishedWithError();
    then(output(), containsString("Native function returnNull has faulty implementation: "
        + "it returned 'null' but logged no error."));
  }

  @Test
  public void returning_null_and_logs_only_warning_causes_error() throws Exception {
    givenNativeJar(ReportWarningAndReturnNull.class);
    givenScript("String reportWarning(String message);\n"
        + "      result = reportWarning('test message');");
    whenSmoothBuild("result");
    thenFinishedWithError();
    then(output(), containsString("Native function reportWarning has faulty implementation: "
        + "it returned 'null' but logged no error."));
  }
}
