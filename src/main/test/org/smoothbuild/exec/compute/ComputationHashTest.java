package org.smoothbuild.exec.compute;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.db.object.type.TestingTypesH.PERSON;
import static org.smoothbuild.exec.base.Input.input;
import static org.smoothbuild.exec.compute.Computer.computationHash;
import static org.smoothbuild.lang.base.type.TestingTypesS.STRING;
import static org.smoothbuild.util.collect.Lists.list;

import org.junit.jupiter.api.Test;
import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.object.type.TestingTypesH;
import org.smoothbuild.db.object.type.base.TypeHV;
import org.smoothbuild.exec.algorithm.Algorithm;
import org.smoothbuild.exec.algorithm.CallNativeAlgorithm;
import org.smoothbuild.exec.algorithm.ConstructAlgorithm;
import org.smoothbuild.exec.algorithm.ConvertAlgorithm;
import org.smoothbuild.exec.algorithm.OrderAlgorithm;
import org.smoothbuild.exec.algorithm.SelectAlgorithm;
import org.smoothbuild.exec.base.Input;
import org.smoothbuild.exec.base.Output;
import org.smoothbuild.plugin.NativeApi;
import org.smoothbuild.testing.TestingContext;

public class ComputationHashTest extends TestingContext {
  @Test
  public void hashes_of_computations_with_same_algorithm_runtime_and_input_are_equal() {
    Algorithm algorithm = computation(Hash.of(1));
    Input input = input(list(string("input")));
    assertThat(computationHash(Hash.of(13), algorithm, input))
        .isEqualTo(computationHash(Hash.of(13), algorithm, input));
  }

  @Test
  public void hashes_of_computations_with_different_algorithm_but_same_runtime_and_input_are_not_equal() {
    Algorithm algorithm = computation(Hash.of(1));
    Algorithm algorithm2 = computation(Hash.of(2));
    Input input = input(list(string("input")));
    assertThat(computationHash(Hash.of(13), algorithm, input))
        .isNotEqualTo(computationHash(Hash.of(13), algorithm2, input));
  }

  @Test
  public void hashes_of_computations_with_same_algorithm_and_input_but_different_runtime_are_not_equal() {
    Algorithm algorithm = computation(Hash.of(1));
    Input input = input(list(string("input")));
    assertThat(computationHash(Hash.of(13), algorithm, input))
        .isNotEqualTo(computationHash(Hash.of(14), algorithm, input));
  }

  @Test
  public void hashes_of_computations_with_same_algorithm_runtime_but_different_input_are_not_equal() {
    Algorithm algorithm = computation(Hash.of(1));
    Input input = input(list(string("input")));
    Input input2 = input(list(string("input2")));
    assertThat(computationHash(Hash.of(13), algorithm, input))
        .isNotEqualTo(computationHash(Hash.of(13), algorithm, input2));
  }

  @Test
  public void hash_of_computation_with_array_algorithm_and_empty_input_is_stable() {
    Algorithm algorithm = new OrderAlgorithm(arrayOT(stringOT()));
    Input input = input(list());
    assertThat(computationHash(Hash.of(13), algorithm, input))
        .isEqualTo(Hash.decode("16457f3457ec260bb4be0161933c32010b162123"));
  }

  @Test
  public void hash_of_computation_with_array_algorithm_and_non_empty_input_is_stable() {
    Algorithm algorithm = new OrderAlgorithm(arrayOT(stringOT()));
    Input input = input(list(string("abc"), string("def")));
    assertThat(computationHash(Hash.of(13), algorithm, input))
        .isEqualTo(Hash.decode("3afcc3677dc2662b5ae660ca82b6755dd89b9f6a"));
  }

  @Test
  public void hash_of_computation_with_native_call_algorithm_and_empty_input_is_stable() {
    Algorithm algorithm = new CallNativeAlgorithm(
        null, stringOT(), functionS(STRING, "name"), true);
    Input input = input(list());
    assertThat(computationHash(Hash.of(13), algorithm, input))
        .isEqualTo(Hash.decode("fa404053c470625cc32d666d02acd1cc634e2bb5"));
  }

  @Test
  public void hash_of_computation_with_native_call_algorithm_and_non_empty_input_is_stable() {
    Algorithm algorithm = new CallNativeAlgorithm(
        null, stringOT(), functionS(STRING, "name"), true);
    Input input = input(list(string("abc"), string("def")));
    assertThat(computationHash(Hash.of(13), algorithm, input))
        .isEqualTo(Hash.decode("6add42096c8900855b21d87a95f7e2d26b054d44"));
  }

  @Test
  public void hash_of_computation_with_convert_from_nothing_algorithm_and_one_element_input_is_stable() {
    Algorithm algorithm = new ConvertAlgorithm(stringOT());
    Input input = input(list(string("abc")));
    assertThat(computationHash(Hash.of(13), algorithm, input))
        .isEqualTo(Hash.decode("a025bfcacf9dadbc72256e41fbc60f4f5355233b"));
  }

  @Test
  public void hash_of_computation_with_construct_algorithm_and_empty_input_is_stable() {
    Algorithm algorithm = new ConstructAlgorithm(PERSON);
    Input input = input(list());
    assertThat(computationHash(Hash.of(13), algorithm, input))
        .isEqualTo(Hash.decode("ff45a42fc341a33b8835f873f94a083181e665a4"));
  }

  @Test
  public void hash_of_computation_with_construct_algorithm_and_one_element_input_is_stable() {
    Algorithm algorithm = new ConstructAlgorithm(PERSON);
    Input input = input(list(string("abc")));
    assertThat(computationHash(Hash.of(13), algorithm, input))
        .isEqualTo(Hash.decode("b7f6067462bee16b2bf3e9ab5c54c3e4bd1eba54"));
  }

  @Test
  public void hash_of_computation_with_construct_algorithm_and_two_elements_input_is_stable() {
    Algorithm algorithm = new ConstructAlgorithm(PERSON);
    Input input = input(list(string("abc"), string("def")));
    assertThat(computationHash(Hash.of(13), algorithm, input))
        .isEqualTo(Hash.decode("b598a215e92550d41b2772ff96ba134e4b544dbf"));
  }

  @Test
  public void hash_of_computation_with_select_algorithm_and_one_element_input_is_stable() {
    Algorithm algorithm = new SelectAlgorithm(0, TestingTypesH.STRING);
    Input input = input(list(string("abc")));
    assertThat(computationHash(Hash.of(13), algorithm, input))
        .isEqualTo(Hash.decode("3bcff362c86471722865738fd753e0dd567b55ee"));
  }

  private static Algorithm computation(Hash hash) {
    return new Algorithm(null) {
      @Override
      public Hash hash() {
        return hash;
      }

      @Override
      public TypeHV outputType() {
        return null;
      }

      @Override
      public Output run(Input input, NativeApi nativeApi) {
        return null;
      }
    };
  }
}
