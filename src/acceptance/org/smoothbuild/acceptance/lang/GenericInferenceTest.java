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
    givenScript(
        "  testIdentity(A value) = value;       ",
        "  result = testIdentity(value='abc');  ");
    whenSmoothBuild("result");
    thenFinishedWithSuccess();
    then(artifact("result"), hasContent("abc"));
  }

  @Test
  public void actual_result_type_can_be_inferred_from_arguments_and_converted() throws Exception {
    givenScript(
        "  A myfunc(A res, A forcedType) = res;                    ",
        "  result = myfunc(res = [], forcedType = [ [ 'abc' ] ]);  ");
    whenSmoothBuild("result");
    thenFinishedWithSuccess();
    then(artifactArray("result"), equalTo(list()));
  }

  @Test
  public void actual_array_type_can_be_inferred_from_arguments_and_elements_are_converted()
      throws Exception {
    givenScript(
        "  pair(A first, A second) = [ first, second ];        ",
        "  result = pair(first = [], second = [ [ 'aaa' ] ]);  ");
    whenSmoothBuild("result");
    thenFinishedWithSuccess();
    then(artifactArray("result"), equalTo(list(list(), list(list("aaa")))));
  }

  // testConcat([A] first, [A] second)

  @Test
  public void infer_actual_type_of_parameters_in_concat_function_0() throws Exception {
    givenNativeJar(Concat.class);
    givenScript(
        "  [A] testConcat([A] first, [A] second);         ",
        "  result = testConcat(first = [], second = []);  ");
    whenSmoothBuild("result");
    thenFinishedWithSuccess();
    then(artifactArray("result"), equalTo(list()));
  }

  @Test
  public void infer_actual_type_of_parameters_in_concat_function_1() throws Exception {
    givenNativeJar(Concat.class);
    givenScript(
        "  [A] testConcat([A] first, [A] second);                       ",
        "  result = testConcat(first = [ 'aaa' ], second = [ 'bbb' ]);  ");
    whenSmoothBuild("result");
    thenFinishedWithSuccess();
    then(artifactArray("result"), equalTo(list("aaa", "bbb")));
  }

  @Test
  public void infer_actual_type_of_parameters_in_concat_function_2() throws Exception {
    givenNativeJar(Concat.class);
    givenScript(
        "  [A] testConcat([A] first, [A] second);                ",
        "  result = testConcat(first = [ 'aaa' ], second = []);  ");
    whenSmoothBuild("result");
    thenFinishedWithSuccess();
    then(artifactArray("result"), equalTo(list("aaa")));
  }

  @Test
  public void infer_actual_type_of_parameters_in_concat_function_3() throws Exception {
    givenNativeJar(Concat.class);
    givenScript(
        "  [A] testConcat([A] first, [A] second);                ",
        "  result = testConcat(first = [], second = [ 'bbb' ]);  ");
    whenSmoothBuild("result");
    thenFinishedWithSuccess();
    then(artifactArray("result"), equalTo(list("bbb")));
  }

  @Test
  public void infer_actual_type_of_parameters_in_concat_function_4() throws Exception {
    givenNativeJar(Concat.class);
    givenScript(
        "  [A] testConcat([A] first, [A] second);                    ",
        "  result = testConcat(first = [ [] ], second = [ 'bbb' ]);  ");
    whenSmoothBuild("result");
    thenFinishedWithError();
    thenOutputContainsError(2,
        "Cannot infer actual type(s) for generic parameter(s) in call to 'testConcat'.");
  }

  @Test
  public void infer_actual_type_of_parameters_in_concat_function_5() throws Exception {
    givenNativeJar(Concat.class);
    givenScript(
        "  [A] testConcat([A] first, [A] second);                              ",
        "  wrapper([Nothing] f, [[A]] s) = testConcat(first = f, second = s);  ",
        "  result = wrapper(f = [], s = [ [ 'aaa' ] ]);                        ");
    whenSmoothBuild("result");
    thenFinishedWithSuccess();
    then(artifactArray("result"), equalTo(list(list("aaa"))));
  }

  @Test
  public void infer_actual_type_of_parameters_in_concat_function_6() throws Exception {
    givenNativeJar(Concat.class);
    givenScript(
        "  [A] testConcat([A] first, [A] second);                              ",
        "  [A] testConcatW([A] f, [A] s) = testConcat(first = f, second = s);  ",
        "  wrapper([Nothing] f, [[A]] s) = testConcatW(f = f, s = s);          ",
        "  result = wrapper(f = [], s = [ [ 'aaa' ] ]);                        ");
    whenSmoothBuild("result");
    thenFinishedWithSuccess();
    then(artifactArray("result"), equalTo(list(list("aaa"))));
  }

  // testAppend([A] array, a element)

  @Test
  public void infer_actual_type_of_parameters_in_append_function_0() throws Exception {
    givenNativeJar(Append.class);
    givenScript(
        "  [A] testAppend([A] array, A element);              ",
        "  result = testAppend(array = [], element = 'bbb');  ");
    whenSmoothBuild("result");
    thenFinishedWithSuccess();
    then(artifactArray("result"), equalTo(list("bbb")));
  }

  @Test
  public void infer_actual_type_of_parameters_in_append_function_1() throws Exception {
    givenNativeJar(Append.class);
    givenScript(
        "  [A] testAppend([A] array, A element);                     ",
        "  result = testAppend(array = [ 'aaa' ], element = 'bbb');  ");
    whenSmoothBuild("result");
    thenFinishedWithSuccess();
    then(artifactArray("result"), equalTo(list("aaa", "bbb")));
  }

  @Test
  public void infer_actual_type_of_parameters_in_append_function_2() throws Exception {
    givenNativeJar(Append.class);
    givenScript(
        "  [A] testAppend([A] array, A element);                                   ",
        "  StringStruct {                                                          ",
        "    String value                                                          ",
        "  }                                                                       ",
        "  result = testAppend(array = [ 'aaa' ], element = stringStruct('bbb'));  ");
    whenSmoothBuild("result");
    thenFinishedWithSuccess();
    then(artifactArray("result"), equalTo(list("aaa", "bbb")));
  }

  @Test
  public void infer_actual_type_of_parameters_in_append_function_3() throws Exception {
    givenNativeJar(Append.class);
    givenScript(
        "  [A] testAppend([A] array, A element);                                          ",
        "  StringStruct {                                                                 ",
        "    String value                                                                 ",
        "  }                                                                              ",
        "  [String] emptyStringArray = [];                                                ",
        "  result = testAppend(array = emptyStringArray, element = stringStruct('bbb'));  ");
    whenSmoothBuild("result");
    thenFinishedWithSuccess();
    then(artifactArray("result"), equalTo(list("bbb")));
  }

  @Test
  public void infer_actual_type_of_parameters_in_append_function_4() throws Exception {
    givenNativeJar(Append.class);
    givenScript(
        "  [A] testAppend([A] array, A element);                                ",
        "  StringStruct {                                                       ",
        "    String value                                                       ",
        "  }                                                                    ",
        "  result = testAppend(array = [ [] ], element = stringStruct('bbb'));  ");
    whenSmoothBuild("result");
    thenFinishedWithError();
    thenOutputContainsError(5,
        "Cannot infer actual type(s) for generic parameter(s) in call to 'testAppend'.");
  }
}
