package org.smoothbuild.acceptance.lang;

import static org.hamcrest.Matchers.equalTo;
import static org.smoothbuild.acceptance.FileContentMatcher.hasContent;
import static org.smoothbuild.util.Lists.list;
import static org.testory.Testory.then;

import org.junit.Test;
import org.smoothbuild.acceptance.AcceptanceTestCase;
import org.smoothbuild.acceptance.testing.Append;
import org.smoothbuild.acceptance.testing.Concat;

public class GenericInferenceTest extends AcceptanceTestCase {
  @Test
  public void actual_result_type_can_be_inferred_from_arguments() throws Exception {
    givenScript("testIdentity(a value) = value;        \n"
        + "      result = testIdentity(value='abc');   \n");
    whenSmoothBuild("result");
    thenFinishedWithSuccess();
    then(artifact("result"), hasContent("abc"));
  }

  @Test
  public void actual_result_type_can_be_inferred_from_arguments_and_converted() throws Exception {
    givenScript("a myfunc(a res, a forcedType) = res;               \n" +
        "        result = myfunc(res=[], forcedType=[['abc']]);     \n");
    whenSmoothBuild("result");
    thenFinishedWithSuccess();
    then(artifactArray("result"), equalTo(list()));
  }

  @Test
  public void actual_array_type_can_be_inferred_from_arguments_and_elements_are_converted()
      throws Exception {
    givenScript("pair(a first, a second) = [first, second];           \n" +
        "        result = pair(first=[], second=[['aaa']]);           \n");
    whenSmoothBuild("result");
    thenFinishedWithSuccess();
    then(artifactArray("result"), equalTo(list(list(), list(list("aaa")))));
  }

  // testConcat([a] first, [a] second)

  @Test
  public void infer_actual_type_of_parameters_in_concat_function_0() throws Exception {
    givenNativeJar(Concat.class);
    givenScript("[a] testConcat([a] first, [a] second);      \n"
        + "      result = testConcat(first=[], second=[]);   \n");
    whenSmoothBuild("result");
    thenFinishedWithSuccess();
    then(artifactArray("result"), equalTo(list()));
  }

  @Test
  public void infer_actual_type_of_parameters_in_concat_function_1() throws Exception {
    givenNativeJar(Concat.class);
    givenScript("[a] testConcat([a] first, [a] second);                \n"
        + "      result = testConcat(first=['aaa'], second=['bbb']);   \n");
    whenSmoothBuild("result");
    thenFinishedWithSuccess();
    then(artifactArray("result"), equalTo(list("aaa", "bbb")));
  }

  @Test
  public void infer_actual_type_of_parameters_in_concat_function_2() throws Exception {
    givenNativeJar(Concat.class);
    givenScript("[a] testConcat([a] first, [a] second);           \n"
        + "      result = testConcat(first=['aaa'], second=[]);   \n");
    whenSmoothBuild("result");
    thenFinishedWithSuccess();
    then(artifactArray("result"), equalTo(list("aaa")));
  }

  @Test
  public void infer_actual_type_of_parameters_in_concat_function_3() throws Exception {
    givenNativeJar(Concat.class);
    givenScript("[a] testConcat([a] first, [a] second);           \n"
        + "      result = testConcat(first=[], second=['bbb']);   \n");
    whenSmoothBuild("result");
    thenFinishedWithSuccess();
    then(artifactArray("result"), equalTo(list("bbb")));
  }

  @Test
  public void infer_actual_type_of_parameters_in_concat_function_4() throws Exception {
    givenNativeJar(Concat.class);
    givenScript("[a] testConcat([a] first, [a] second);           \n"
        + "      result = testConcat(first=[[]], second=['bbb']);   \n");
    whenSmoothBuild("result");
    thenFinishedWithError();
    thenOutputContainsError(2,
        "Cannot infer actual type(s) for generic parameter(s) in call to 'testConcat'.");
  }

  @Test
  public void infer_actual_type_of_parameters_in_concat_function_5() throws Exception {
    givenNativeJar(Concat.class);
    givenScript("[a] testConcat([a] first, [a] second);                            \n"
        + "      wrapper([Nothing] f, [[a]] s) = testConcat(first=f, second=s);    \n"
        + "      result = wrapper(f=[], s=[['aaa']]);                              \n");
    whenSmoothBuild("result");
    thenFinishedWithSuccess();
    then(artifactArray("result"), equalTo(list(list("aaa"))));
  }

  @Test
  public void infer_actual_type_of_parameters_in_concat_function_6() throws Exception {
    givenNativeJar(Concat.class);
    givenScript("[a] testConcat([a] first, [a] second);                             \n"
        + "      [a] testConcatW([a] f, [a] s) = testConcat(first=f, second=s);     \n"
        + "      wrapper([Nothing] f, [[a]] s) = testConcatW(f=f, s=s);             \n"
        + "      result = wrapper(f=[], s=[['aaa']]);                               \n");
    whenSmoothBuild("result");
    thenFinishedWithSuccess();
    then(artifactArray("result"), equalTo(list(list("aaa"))));
  }

  // testAppend([a] array, a element)

  @Test
  public void infer_actual_type_of_parameters_in_append_function_0() throws Exception {
    givenNativeJar(Append.class);
    givenScript("[a] testAppend([a] array, a element);               \n"
        + "      result = testAppend(array=[], element='bbb');       \n");
    whenSmoothBuild("result");
    thenFinishedWithSuccess();
    then(artifactArray("result"), equalTo(list("bbb")));
  }

  @Test
  public void infer_actual_type_of_parameters_in_append_function_1() throws Exception {
    givenNativeJar(Append.class);
    givenScript("[a] testAppend([a] array, a element);                \n"
        + "      result = testAppend(array=['aaa'], element='bbb');   \n");
    whenSmoothBuild("result");
    thenFinishedWithSuccess();
    then(artifactArray("result"), equalTo(list("aaa", "bbb")));
  }

  @Test
  public void infer_actual_type_of_parameters_in_append_function_2() throws Exception {
    givenNativeJar(Append.class);
    givenScript("[a] testAppend([a] array, a element);                              \n"
        + "      StringStruct { String value }                                      \n"
        + "      result = testAppend(array=['aaa'], element=stringStruct('bbb'));   \n");
    whenSmoothBuild("result");
    thenFinishedWithSuccess();
    then(artifactArray("result"), equalTo(list("aaa", "bbb")));
  }

  @Test
  public void infer_actual_type_of_parameters_in_append_function_3() throws Exception {
    givenNativeJar(Append.class);
    givenScript("[a] testAppend([a] array, a element);                                      \n"
        + "      StringStruct { String value }                                              \n"
        + "      [String] emptyStringArray = [];                                            \n"
        + "      result = testAppend(array=emptyStringArray, element=stringStruct('bbb'));  \n");
    whenSmoothBuild("result");
    thenFinishedWithSuccess();
    then(artifactArray("result"), equalTo(list("bbb")));
  }

  @Test
  public void infer_actual_type_of_parameters_in_append_function_4() throws Exception {
    givenNativeJar(Append.class);
    givenScript("[a] testAppend([a] array, a element);                              \n"
        + "      StringStruct { String value }                                      \n"
        + "      result = testAppend(array=[[]], element=stringStruct('bbb'));      \n");
    whenSmoothBuild("result");
    thenFinishedWithError();
    thenOutputContainsError(3,
        "Cannot infer actual type(s) for generic parameter(s) in call to 'testAppend'.");
  }
}
