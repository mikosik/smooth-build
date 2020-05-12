package org.smoothbuild.slib.lang;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.util.Lists.list;

import org.junit.jupiter.api.Test;
import org.smoothbuild.slib.AcceptanceTestCase;
import org.smoothbuild.slib.testing.Append;
import org.smoothbuild.slib.testing.Concat;

public class GenericInferenceTest extends AcceptanceTestCase {
  @Test
  public void actual_result_type_can_be_inferred_from_arguments() throws Exception {
    givenScript(
        "  testIdentity(A value) = value;       ",
        "  result = testIdentity(value='abc');  ");
    whenSmoothBuild("result");
    thenFinishedWithSuccess();
    assertThat(artifactContent("result"))
        .isEqualTo("abc");
  }

  @Test
  public void actual_result_type_can_be_inferred_from_arguments_and_converted() throws Exception {
    givenScript(
        "  A myfunc(A res, A forcedType) = res;                    ",
        "  result = myfunc(res = [], forcedType = [ [ 'abc' ] ]);  ");
    whenSmoothBuild("result");
    thenFinishedWithSuccess();
    assertThat(artifactArray("result"))
        .isEqualTo(list());
  }

  @Test
  public void actual_array_type_can_be_inferred_from_arguments_and_elements_are_converted()
      throws Exception {
    givenScript(
        "  pair(A first, A second) = [ first, second ];        ",
        "  result = pair(first = [], second = [ [ 'aaa' ] ]);  ");
    whenSmoothBuild("result");
    thenFinishedWithSuccess();
    assertThat(artifactArray("result"))
        .isEqualTo(list(list(), list(list("aaa"))));
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
    assertThat(artifactArray("result"))
        .isEqualTo(list());
  }

  @Test
  public void infer_actual_type_of_parameters_in_concat_function_1() throws Exception {
    givenNativeJar(Concat.class);
    givenScript(
        "  [A] testConcat([A] first, [A] second);                       ",
        "  result = testConcat(first = [ 'aaa' ], second = [ 'bbb' ]);  ");
    whenSmoothBuild("result");
    thenFinishedWithSuccess();
    assertThat(artifactArray("result"))
        .isEqualTo(list("aaa", "bbb"));
  }

  @Test
  public void infer_actual_type_of_parameters_in_concat_function_2() throws Exception {
    givenNativeJar(Concat.class);
    givenScript(
        "  [A] testConcat([A] first, [A] second);                ",
        "  result = testConcat(first = [ 'aaa' ], second = []);  ");
    whenSmoothBuild("result");
    thenFinishedWithSuccess();
    assertThat(artifactArray("result"))
        .isEqualTo(list("aaa"));
  }

  @Test
  public void infer_actual_type_of_parameters_in_concat_function_3() throws Exception {
    givenNativeJar(Concat.class);
    givenScript(
        "  [A] testConcat([A] first, [A] second);                ",
        "  result = testConcat(first = [], second = [ 'bbb' ]);  ");
    whenSmoothBuild("result");
    thenFinishedWithSuccess();
    assertThat(artifactArray("result"))
        .isEqualTo(list("bbb"));
  }

  @Test
  public void infer_actual_type_of_parameters_in_concat_function_4() throws Exception {
    givenNativeJar(Concat.class);
    givenScript(
        "  [A] testConcat([A] first, [A] second);                    ",
        "  result = testConcat(first = [ [] ], second = [ 'bbb' ]);  ");
    whenSmoothBuild("result");
    thenFinishedWithError();
    thenSysOutContainsParseError(2,
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
    assertThat(artifactArray("result"))
        .isEqualTo(list(list("aaa")));
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
    assertThat(artifactArray("result"))
        .isEqualTo(list(list("aaa")));
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
    assertThat(artifactArray("result"))
        .isEqualTo(list("bbb"));
  }

  @Test
  public void infer_actual_type_of_parameters_in_append_function_1() throws Exception {
    givenNativeJar(Append.class);
    givenScript(
        "  [A] testAppend([A] array, A element);                     ",
        "  result = testAppend(array = [ 'aaa' ], element = 'bbb');  ");
    whenSmoothBuild("result");
    thenFinishedWithSuccess();
    assertThat(artifactArray("result"))
        .isEqualTo(list("aaa", "bbb"));
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
    assertThat(artifactArray("result"))
        .isEqualTo(list("aaa", "bbb"));
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
    assertThat(artifactArray("result"))
        .isEqualTo(list("bbb"));
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
    thenSysOutContainsParseError(5,
        "Cannot infer actual type(s) for generic parameter(s) in call to 'testAppend'.");
  }
}
