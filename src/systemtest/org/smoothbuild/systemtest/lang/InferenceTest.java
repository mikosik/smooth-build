package org.smoothbuild.systemtest.lang;

import static com.google.common.truth.Truth.assertThat;
import static java.lang.String.format;
import static org.smoothbuild.util.collect.Lists.list;

import org.junit.jupiter.api.Test;
import org.smoothbuild.nativefunc.Append;
import org.smoothbuild.nativefunc.Concat;
import org.smoothbuild.systemtest.SystemTestCase;

public class InferenceTest extends SystemTestCase {
  @Test
  public void actual_result_type_can_be_inferred_from_args() throws Exception {
    createUserModule("""
            testIdentity(A value) = value;
            result = testIdentity(value="abc");
            """);
    runSmoothBuild("result");
    assertFinishedWithSuccess();
    assertThat(artifactAsString("result"))
        .isEqualTo("abc");
  }

  @Test
  public void actual_result_type_can_be_inferred_from_args_and_converted() throws Exception {
    createUserModule("""
            A myfunc(A res, A forcedType) = res;
            result = myfunc(res = [], forcedType = [ [ "abc" ] ]);
            """);
    runSmoothBuild("result");
    assertFinishedWithSuccess();
    assertThat(artifactStringified("result"))
        .isEqualTo(list());
  }

  @Test
  public void actual_array_type_can_be_inferred_from_args_and_elems_are_converted()
      throws Exception {
    createUserModule("""
            pair(A first, A second) = [ first, second ];
            result = pair(first = [], second = [ [ "aaa" ] ]);
            """);
    runSmoothBuild("result");
    assertFinishedWithSuccess();
    assertThat(artifactStringified("result"))
        .isEqualTo(list(list(), list(list("aaa"))));
  }

  // testConcat([A] first, [A] second)

  @Test
  public void infer_actual_type_of_params_in_concat_func_0() throws Exception {
    createNativeJar(Concat.class);
    createUserModule(format("""
            @Native("%s")
            [A] testConcat([A] first, [A] second);
            result = testConcat(first = [], second = []);
            """, Concat.class.getCanonicalName()));
    runSmoothBuild("result");
    assertFinishedWithSuccess();
    assertThat(artifactStringified("result"))
        .isEqualTo(list());
  }

  @Test
  public void infer_actual_type_of_params_in_concat_func_1() throws Exception {
    createNativeJar(Concat.class);
    createUserModule(format("""
            @Native("%s")
            [A] testConcat([A] first, [A] second);
            result = testConcat(first = [ "aaa" ], second = [ "bbb" ]);
            """, Concat.class.getCanonicalName()));
    runSmoothBuild("result");
    assertFinishedWithSuccess();
    assertThat(artifactStringified("result"))
        .isEqualTo(list("aaa", "bbb"));
  }

  @Test
  public void infer_actual_type_of_params_in_concat_func_2() throws Exception {
    createNativeJar(Concat.class);
    createUserModule(format("""
            @Native("%s")
            [A] testConcat([A] first, [A] second);
            result = testConcat(first = [ "aaa" ], second = []);
            """, Concat.class.getCanonicalName()));
    runSmoothBuild("result");
    assertFinishedWithSuccess();
    assertThat(artifactStringified("result"))
        .isEqualTo(list("aaa"));
  }

  @Test
  public void infer_actual_type_of_params_in_concat_func_3() throws Exception {
    createNativeJar(Concat.class);
    createUserModule(format("""
            @Native("%s")
            [A] testConcat([A] first, [A] second);
            result = testConcat(first = [], second = [ "bbb" ]);
            """, Concat.class.getCanonicalName()));
    runSmoothBuild("result");
    assertFinishedWithSuccess();
    assertThat(artifactStringified("result"))
        .isEqualTo(list("bbb"));
  }

  @Test
  public void infer_actual_type_of_params_in_concat_func_5() throws Exception {
    createNativeJar(Concat.class);
    createUserModule(format("""
            @Native("%s")
            [A] testConcat([A] first, [A] second);
            wrapper([Nothing] f, [[A]] s) = testConcat(first = f, second = s);
            result = wrapper(f = [], s = [ [ "aaa" ] ]);
            """, Concat.class.getCanonicalName()));
    runSmoothBuild("result");
    assertFinishedWithSuccess();
    assertThat(artifactStringified("result"))
        .isEqualTo(list(list("aaa")));
  }

  @Test
  public void infer_actual_type_of_params_in_concat_func_6() throws Exception {
    createNativeJar(Concat.class);
    createUserModule(format("""
            @Native("%s")
            [A] testConcat([A] first, [A] second);
            [A] testConcatW([A] f, [A] s) = testConcat(first = f, second = s);
            wrapper([Nothing] f, [[A]] s) = testConcatW(f = f, s = s);
            result = wrapper(f = [], s = [ [ "aaa" ] ]);
            """, Concat.class.getCanonicalName()));
    runSmoothBuild("result");
    assertFinishedWithSuccess();
    assertThat(artifactStringified("result"))
        .isEqualTo(list(list("aaa")));
  }

  // testAppend([A] array, a elem)

  @Test
  public void infer_actual_type_of_params_in_append_func_0() throws Exception {
    createNativeJar(Append.class);
    createUserModule(format("""
            @Native("%s")
            [A] testAppend([A] array, A elem);
            result = testAppend(array = [], elem = "bbb");
            """, Append.class.getCanonicalName()));
    runSmoothBuild("result");
    assertFinishedWithSuccess();
    assertThat(artifactStringified("result"))
        .isEqualTo(list("bbb"));
  }

  @Test
  public void infer_actual_type_of_params_in_append_func_1() throws Exception {
    createNativeJar(Append.class);
    createUserModule(format("""
            @Native("%s")
            [A] testAppend([A] array, A elem);
            result = testAppend(array = [ "aaa" ], elem = "bbb");
            """, Append.class.getCanonicalName()));
    runSmoothBuild("result");
    assertFinishedWithSuccess();
    assertThat(artifactStringified("result"))
        .isEqualTo(list("aaa", "bbb"));
  }


  @Test
  public void infer_actual_type_of_params_in_append_func_3() throws Exception {
    createNativeJar(Append.class);
    createUserModule(format("""
            @Native("%s")
            [A] testAppend([A] array, A elem);
            StringStruct {
              String value
            }
            [String] emptyStringArray = [];
            result = testAppend(emptyStringArray, "bbb");
            """, Append.class.getCanonicalName()));
    runSmoothBuild("result");
    assertFinishedWithSuccess();
    assertThat(artifactStringified("result"))
        .isEqualTo(list("bbb"));
  }
}
