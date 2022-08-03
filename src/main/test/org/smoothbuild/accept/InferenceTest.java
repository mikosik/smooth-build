package org.smoothbuild.accept;

import static com.google.common.truth.Truth.assertThat;
import static java.lang.String.format;

import org.junit.jupiter.api.Test;
import org.smoothbuild.testing.accept.AcceptanceTestCase;
import org.smoothbuild.testing.func.nativ.Append;
import org.smoothbuild.testing.func.nativ.Concat;

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
            result = myfunc(res = [], forcedType = [["abc"]]);
            """);
    evaluate("result");
    assertThat(artifact())
        .isEqualTo(arrayB(arrayTB(stringTB())));
  }

  @Test
  public void actual_array_type_can_be_inferred_from_args_and_elems_are_converted()
      throws Exception {
    createUserModule("""
            pair(A first, A second) = [first, second];
            result = pair(first = [], second = [["abc"]]);
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
            inferred = testConcat(first = [], second = []);
            [Int] result = inferred;
            """, Concat.class.getCanonicalName()));
    evaluate("result");
    assertThat(artifact())
        .isEqualTo(arrayB(intTB()));
  }

  @Test
  public void infer_actual_type_of_params_in_concat_func_1() throws Exception {
    createUserNativeJar(Concat.class);
    createUserModule(format("""
            @Native("%s")
            [A] testConcat([A] first, [A] second);
            result = testConcat(first = ["aaa"], second = ["bbb"]);
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
            result = testConcat(first = ["aaa"], second = []);
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
            result = testConcat(first = [], second = ["bbb"]);
            """, Concat.class.getCanonicalName()));
    evaluate("result");
    assertThat(artifact())
        .isEqualTo(arrayB(stringB("bbb")));
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
            result = testAppend(array = ["aaa"], elem = "bbb");
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
