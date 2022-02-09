package org.smoothbuild.accept;

import static com.google.common.truth.Truth.assertThat;
import static java.lang.String.format;

import org.junit.jupiter.api.Test;
import org.smoothbuild.testing.accept.AcceptanceTestCase;
import org.smoothbuild.testing.nativefunc.Append;
import org.smoothbuild.testing.nativefunc.Concat;

public class InferenceTest extends AcceptanceTestCase {
  @Test
  public void actual_result_type_can_be_inferred_from_args() throws Exception {
    createUserModule("""
            testIdentity(A value) = value;
            result = testIdentity(value="abc");
            """);
    evaluate("result");
    assertThat(artifact())
        .isEqualTo(stringB("abc"));
  }

  @Test
  public void actual_result_type_can_be_inferred_from_args_and_converted() throws Exception {
    createUserModule("""
            A myfunc(A res, A forcedType) = res;
            result = myfunc(res = [], forcedType = [ [ "abc" ] ]);
            """);
    evaluate("result");
    assertThat(artifact())
        .isEqualTo(arrayB(arrayTB(stringTB())));
  }

  @Test
  public void actual_array_type_can_be_inferred_from_args_and_elems_are_converted()
      throws Exception {
    createUserModule("""
            pair(A first, A second) = [ first, second ];
            result = pair(first = [], second = [ [ "abc" ] ]);
            """);
    evaluate("result");
    assertThat(artifact())
        .isEqualTo(arrayB(arrayB(arrayTB(stringTB())), arrayB(arrayB(stringB("abc")))));
  }

  // testConcat([A] first, [A] second)

  @Test
  public void infer_actual_type_of_params_in_concat_func_0() throws Exception {
    createUserNativeJar(Concat.class);
    createUserModule(format("""
            @Native("%s")
            [A] testConcat([A] first, [A] second);
            result = testConcat(first = [], second = []);
            """, Concat.class.getCanonicalName()));
    evaluate("result");
    assertThat(artifact())
        .isEqualTo(arrayB(nothingTB()));
  }

  @Test
  public void infer_actual_type_of_params_in_concat_func_1() throws Exception {
    createUserNativeJar(Concat.class);
    createUserModule(format("""
            @Native("%s")
            [A] testConcat([A] first, [A] second);
            result = testConcat(first = [ "aaa" ], second = [ "bbb" ]);
            """, Concat.class.getCanonicalName()));
    evaluate("result");
    assertThat(artifact())
        .isEqualTo(arrayB(stringB("aaa"), stringB("bbb")));
  }

  @Test
  public void infer_actual_type_of_params_in_concat_func_2() throws Exception {
    createUserNativeJar(Concat.class);
    createUserModule(format("""
            @Native("%s")
            [A] testConcat([A] first, [A] second);
            result = testConcat(first = [ "aaa" ], second = []);
            """, Concat.class.getCanonicalName()));
    evaluate("result");
    assertThat(artifact())
        .isEqualTo(arrayB(stringB("aaa")));
  }

  @Test
  public void infer_actual_type_of_params_in_concat_func_3() throws Exception {
    createUserNativeJar(Concat.class);
    createUserModule(format("""
            @Native("%s")
            [A] testConcat([A] first, [A] second);
            result = testConcat(first = [], second = [ "bbb" ]);
            """, Concat.class.getCanonicalName()));
    evaluate("result");
    assertThat(artifact())
        .isEqualTo(arrayB(stringB("bbb")));
  }

  @Test
  public void infer_actual_type_of_params_in_concat_func_5() throws Exception {
    createUserNativeJar(Concat.class);
    createUserModule(format("""
            @Native("%s")
            [A] testConcat([A] first, [A] second);
            wrapper([Nothing] f, [[A]] s) = testConcat(first = f, second = s);
            result = wrapper(f = [], s = [ [ "aaa" ] ]);
            """, Concat.class.getCanonicalName()));
    evaluate("result");
    assertThat(artifact())
        .isEqualTo(arrayB(arrayB(stringB("aaa"))));
  }

  @Test
  public void infer_actual_type_of_params_in_concat_func_6() throws Exception {
    createUserNativeJar(Concat.class);
    createUserModule(format("""
            @Native("%s")
            [A] testConcat([A] first, [A] second);
            [A] testConcatW([A] f, [A] s) = testConcat(first = f, second = s);
            wrapper([Nothing] f, [[A]] s) = testConcatW(f = f, s = s);
            result = wrapper(f = [], s = [ [ "aaa" ] ]);
            """, Concat.class.getCanonicalName()));
    evaluate("result");
    assertThat(artifact())
        .isEqualTo(arrayB(arrayB(stringB("aaa"))));
  }

  // testAppend([A] array, a elem)

  @Test
  public void infer_actual_type_of_params_in_append_func_0() throws Exception {
    createUserNativeJar(Append.class);
    createUserModule(format("""
            @Native("%s")
            [A] testAppend([A] array, A elem);
            result = testAppend(array = [], elem = "bbb");
            """, Append.class.getCanonicalName()));
    evaluate("result");
    assertThat(artifact())
        .isEqualTo(arrayB(stringB("bbb")));
  }

  @Test
  public void infer_actual_type_of_params_in_append_func_1() throws Exception {
    createUserNativeJar(Append.class);
    createUserModule(format("""
            @Native("%s")
            [A] testAppend([A] array, A elem);
            result = testAppend(array = [ "aaa" ], elem = "bbb");
            """, Append.class.getCanonicalName()));
    evaluate("result");
    assertThat(artifact())
        .isEqualTo(arrayB(stringB("aaa"), stringB("bbb")));
  }

  @Test
  public void infer_actual_type_of_params_in_append_func_3() throws Exception {
    createUserNativeJar(Append.class);
    createUserModule(format("""
            @Native("%s")
            [A] testAppend([A] array, A elem);
            StringStruct {
              String value
            }
            [String] emptyStringArray = [];
            result = testAppend(emptyStringArray, "bbb");
            """, Append.class.getCanonicalName()));
    evaluate("result");
    assertThat(artifact())
        .isEqualTo(arrayB(stringB("bbb")));
  }
}
