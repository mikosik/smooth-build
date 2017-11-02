package org.smoothbuild.acceptance.lang;

import static org.hamcrest.Matchers.containsString;
import static org.smoothbuild.acceptance.FileContentMatcher.hasContent;
import static org.testory.Testory.then;
import static org.testory.Testory.thenEqual;

import org.junit.Test;
import org.smoothbuild.acceptance.AcceptanceTestCase;
import org.smoothbuild.acceptance.lang.nativ.ThrowException;

public class DefaultValueTest extends AcceptanceTestCase {

  @Test
  public void default_value_with_type_not_assignable_to_parameter_type_causes_error()
      throws Exception {
    givenScript("func([String] withDefault = 'abc') = withDefault;"
        + "      result = func;");
    whenSmoothBuild("result");
    thenFinishedWithError();
    then(output(), containsString(
        "build.smooth:1: error: Parameter 'withDefault' is of type [String] so"
            + " it cannot have default value of type String."));
  }

  @Test
  public void default_value_can_have_type_convertible_to_parameter_type()
      throws Exception {
    givenFile("file.txt", "abc");
    givenScript("func(Blob param = file('//file.txt')) = param;"
        + "      result = func;");
    whenSmoothBuild("result");
    thenFinishedWithSuccess();
    then(artifact("result"), hasContent("abc"));
  }

  @Test
  public void default_value_expression_can_be_a_call()
      throws Exception {
    givenScript("value = 'abc';"
        + "      func(String withDefault = value) = withDefault;"
        + "      result = func;");
    whenSmoothBuild("result");
    thenFinishedWithSuccess();
    thenEqual(artifactContent("result"), "abc");
  }

  @Test
  public void parameter_is_equal_to_its_default_value_when_not_assigned_in_call() throws Exception {
    givenScript("func(String withDefault = 'abc') = withDefault;"
        + "      result = func;");
    whenSmoothBuild("result");
    thenFinishedWithSuccess();
    thenEqual(artifactContent("result"), "abc");
  }

  @Test
  public void default_value_is_ignored_when_parameter_is_assigned_in_a_call() throws Exception {
    givenScript("func(String withDefault = 'abc') = withDefault;"
        + "      result = func('def');");
    whenSmoothBuild("result");
    thenFinishedWithSuccess();
    thenEqual(artifactContent("result"), "def");
  }

  @Test
  public void default_value_is_not_evaluated_when_not_needed() throws Exception {
    givenNativeJar(ThrowException.class);
    givenScript("String throwException();"
        + "      func(String withDefault = throwException()) = withDefault;"
        + "      result = func('def');");
    whenSmoothBuild("result");
    thenFinishedWithSuccess();
    thenEqual(artifactContent("result"), "def");
  }

  @Test
  public void default_value_of_one_parameter_cannot_reference_other_parameter() throws Exception {
    givenScript("func(String param, String withDefault = param) = param;"
        + "      result = 'abc';");
    whenSmoothBuild("result");
    thenFinishedWithError();
    then(output(), containsString(
        "build.smooth:1: error: "));
  }
}
