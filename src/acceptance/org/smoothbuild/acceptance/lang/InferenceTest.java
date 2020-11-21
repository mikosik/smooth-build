package org.smoothbuild.acceptance.lang;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.util.Lists.list;

import org.junit.jupiter.api.Test;
import org.smoothbuild.acceptance.AcceptanceTestCase;
import org.smoothbuild.acceptance.testing.Append;
import org.smoothbuild.acceptance.testing.Concat;

public class InferenceTest extends AcceptanceTestCase {
  @Test
  public void actual_result_type_can_be_inferred_from_arguments() throws Exception {
    createUserModule("""
            testIdentity(A value) = value;
            result = testIdentity(value="abc");
            """);
    runSmoothBuild("result");
    assertFinishedWithSuccess();
    assertThat(artifactFileContentAsString("result"))
        .isEqualTo("abc");
  }

  @Test
  public void actual_result_type_can_be_inferred_from_arguments_and_converted() throws Exception {
    createUserModule("""
            A myfunc(A res, A forcedType) = res;
            result = myfunc(res = [], forcedType = [ [ "abc" ] ]);
            """);
    runSmoothBuild("result");
    assertFinishedWithSuccess();
    assertThat(stringifiedArtifact("result"))
        .isEqualTo(list());
  }

  @Test
  public void actual_array_type_can_be_inferred_from_arguments_and_elements_are_converted()
      throws Exception {
    createUserModule("""
            pair(A first, A second) = [ first, second ];
            result = pair(first = [], second = [ [ "aaa" ] ]);
            """);
    runSmoothBuild("result");
    assertFinishedWithSuccess();
    assertThat(stringifiedArtifact("result"))
        .isEqualTo(list(list(), list(list("aaa"))));
  }

  // testConcat([A] first, [A] second)

  @Test
  public void infer_actual_type_of_parameters_in_concat_function_0() throws Exception {
    createNativeJar(Concat.class);
    createUserModule("""
            [A] testConcat([A] first, [A] second);
            result = testConcat(first = [], second = []);
            """);
    runSmoothBuild("result");
    assertFinishedWithSuccess();
    assertThat(stringifiedArtifact("result"))
        .isEqualTo(list());
  }

  @Test
  public void infer_actual_type_of_parameters_in_concat_function_1() throws Exception {
    createNativeJar(Concat.class);
    createUserModule("""
            [A] testConcat([A] first, [A] second);
            result = testConcat(first = [ "aaa" ], second = [ "bbb" ]);
            """);
    runSmoothBuild("result");
    assertFinishedWithSuccess();
    assertThat(stringifiedArtifact("result"))
        .isEqualTo(list("aaa", "bbb"));
  }

  @Test
  public void infer_actual_type_of_parameters_in_concat_function_2() throws Exception {
    createNativeJar(Concat.class);
    createUserModule("""
            [A] testConcat([A] first, [A] second);
            result = testConcat(first = [ "aaa" ], second = []);
            """);
    runSmoothBuild("result");
    assertFinishedWithSuccess();
    assertThat(stringifiedArtifact("result"))
        .isEqualTo(list("aaa"));
  }

  @Test
  public void infer_actual_type_of_parameters_in_concat_function_3() throws Exception {
    createNativeJar(Concat.class);
    createUserModule("""
            [A] testConcat([A] first, [A] second);
            result = testConcat(first = [], second = [ "bbb" ]);
            """);
    runSmoothBuild("result");
    assertFinishedWithSuccess();
    assertThat(stringifiedArtifact("result"))
        .isEqualTo(list("bbb"));
  }

  @Test
  public void infer_actual_type_of_parameters_in_concat_function_4() throws Exception {
    createNativeJar(Concat.class);
    createUserModule("""
            [A] testConcat([A] first, [A] second);
            result = testConcat(first = [ [] ], second = [ "bbb" ]);
            """);
    runSmoothBuild("result");
    assertFinishedWithError();
    assertSysOutContainsParseError(2,
        "Cannot infer actual type(s) for parameter(s) in call to `testConcat`.");
  }

  @Test
  public void infer_actual_type_of_parameters_in_concat_function_5() throws Exception {
    createNativeJar(Concat.class);
    createUserModule("""
            [A] testConcat([A] first, [A] second);
            wrapper([Nothing] f, [[A]] s) = testConcat(first = f, second = s);
            result = wrapper(f = [], s = [ [ "aaa" ] ]);
            """);
    runSmoothBuild("result");
    assertFinishedWithSuccess();
    assertThat(stringifiedArtifact("result"))
        .isEqualTo(list(list("aaa")));
  }

  @Test
  public void infer_actual_type_of_parameters_in_concat_function_6() throws Exception {
    createNativeJar(Concat.class);
    createUserModule("""
            [A] testConcat([A] first, [A] second);
            [A] testConcatW([A] f, [A] s) = testConcat(first = f, second = s);
            wrapper([Nothing] f, [[A]] s) = testConcatW(f = f, s = s);
            result = wrapper(f = [], s = [ [ "aaa" ] ]);
            """);
    runSmoothBuild("result");
    assertFinishedWithSuccess();
    assertThat(stringifiedArtifact("result"))
        .isEqualTo(list(list("aaa")));
  }

  // testAppend([A] array, a element)

  @Test
  public void infer_actual_type_of_parameters_in_append_function_0() throws Exception {
    createNativeJar(Append.class);
    createUserModule("""
            [A] testAppend([A] array, A element);
            result = testAppend(array = [], element = "bbb");
            """);
    runSmoothBuild("result");
    assertFinishedWithSuccess();
    assertThat(stringifiedArtifact("result"))
        .isEqualTo(list("bbb"));
  }

  @Test
  public void infer_actual_type_of_parameters_in_append_function_1() throws Exception {
    createNativeJar(Append.class);
    createUserModule("""
            [A] testAppend([A] array, A element);
            result = testAppend(array = [ "aaa" ], element = "bbb");
            """);
    runSmoothBuild("result");
    assertFinishedWithSuccess();
    assertThat(stringifiedArtifact("result"))
        .isEqualTo(list("aaa", "bbb"));
  }


  @Test
  public void infer_actual_type_of_parameters_in_append_function_3() throws Exception {
    createNativeJar(Append.class);
    createUserModule("""
            [A] testAppend([A] array, A element);
            StringStruct {
              String value
            }
            [String] emptyStringArray = [];
            result = testAppend(emptyStringArray, "bbb");
            """);
    runSmoothBuild("result");
    assertFinishedWithSuccess();
    assertThat(stringifiedArtifact("result"))
        .isEqualTo(list("bbb"));
  }

  @Test
  public void infer_actual_type_of_parameters_in_append_function_4() throws Exception {
    createNativeJar(Append.class);
    createUserModule("""
            [A] testAppend([A] array, A element);
            StringStruct {
              String value
            }
            result = testAppend(array = [ [] ], element = stringStruct("bbb"));
            """);
    runSmoothBuild("result");
    assertFinishedWithError();
    assertSysOutContainsParseError(5,
        "Cannot infer actual type(s) for parameter(s) in call to `testAppend`.");
  }
}
