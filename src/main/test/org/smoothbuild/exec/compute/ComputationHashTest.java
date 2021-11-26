package org.smoothbuild.exec.compute;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.db.object.type.TestingTypesH.PERSON;
import static org.smoothbuild.exec.base.Input.input;
import static org.smoothbuild.exec.compute.Computer.computationHash;
import static org.smoothbuild.util.collect.Lists.list;

import org.junit.jupiter.api.Test;
import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.object.type.TestingTypesH;
import org.smoothbuild.db.object.type.base.TypeHV;
import org.smoothbuild.exec.algorithm.Algorithm;
import org.smoothbuild.exec.algorithm.ConstructAlgorithm;
import org.smoothbuild.exec.algorithm.ConvertAlgorithm;
import org.smoothbuild.exec.algorithm.InvokeAlgorithm;
import org.smoothbuild.exec.algorithm.OrderAlgorithm;
import org.smoothbuild.exec.algorithm.SelectAlgorithm;
import org.smoothbuild.exec.base.Input;
import org.smoothbuild.exec.base.Output;
import org.smoothbuild.plugin.NativeApi;
import org.smoothbuild.testing.TestingContext;

public class ComputationHashTest extends TestingContext {
  @Test
  public void hashes_of_computations_with_same_algorithm_runtime_and_input_are_equal() {
    var algorithm = computation(Hash.of(1));
    var input = input(list(stringH("input")));
    assertThat(computationHash(Hash.of(13), algorithm, input))
        .isEqualTo(computationHash(Hash.of(13), algorithm, input));
  }

  @Test
  public void hashes_of_computations_with_different_algorithm_but_same_runtime_and_input_are_not_equal() {
    var algorithm1 = computation(Hash.of(1));
    var algorithm2 = computation(Hash.of(2));
    var input = input(list(stringH("input")));
    assertThat(computationHash(Hash.of(13), algorithm1, input))
        .isNotEqualTo(computationHash(Hash.of(13), algorithm2, input));
  }

  @Test
  public void hashes_of_computations_with_same_algorithm_and_input_but_different_runtime_are_not_equal() {
    var algorithm = computation(Hash.of(1));
    var input = input(list(stringH("input")));
    assertThat(computationHash(Hash.of(13), algorithm, input))
        .isNotEqualTo(computationHash(Hash.of(14), algorithm, input));
  }

  @Test
  public void hashes_of_computations_with_same_algorithm_runtime_but_different_input_are_not_equal() {
    var algorithm = computation(Hash.of(1));
    var input1 = input(list(stringH("input")));
    var input2 = input(list(stringH("input2")));
    assertThat(computationHash(Hash.of(13), algorithm, input1))
        .isNotEqualTo(computationHash(Hash.of(13), algorithm, input2));
  }

  @Test
  public void hash_of_computation_with_order_algorithm_and_empty_input_is_stable() {
    var algorithm = new OrderAlgorithm(arrayHT(stringHT()));
    var input = input(list());
    assertThat(computationHash(Hash.of(13), algorithm, input))
        .isEqualTo(Hash.decode("16457f3457ec260bb4be0161933c32010b162123"));
  }

  @Test
  public void hash_of_computation_with_order_algorithm_and_non_empty_input_is_stable() {
    var algorithm = new OrderAlgorithm(arrayHT(stringHT()));
    var input = input(list(stringH("abc"), stringH("def")));
    assertThat(computationHash(Hash.of(13), algorithm, input))
        .isEqualTo(Hash.decode("3afcc3677dc2662b5ae660ca82b6755dd89b9f6a"));
  }

  @Test
  public void hash_of_computation_with_invoke_algorithm_and_empty_input_is_stable() {
    var nativeFunctionH = nativeFunctionH(blobH(), stringH("class path"));
    var algorithm = new InvokeAlgorithm(stringHT(), "name", nativeFunctionH, null);
    var input = input(list());
    assertThat(computationHash(Hash.of(13), algorithm, input))
        .isEqualTo(Hash.decode("dd834024bc0b342ee029b1e4cb590be8cd50aae4"));
  }

  @Test
  public void hash_of_computation_with_invoke_algorithm_and_non_empty_input_is_stable() {
    var nativeFunctionH = nativeFunctionH(blobH(), stringH("class path"));
    var algorithm = new InvokeAlgorithm(stringHT(), "name", nativeFunctionH, null);
    var input = input(list(stringH("abc"), stringH("def")));
    assertThat(computationHash(Hash.of(13), algorithm, input))
        .isEqualTo(Hash.decode("f98da45979f7d77b4d0a9e1444454d3775b8c221"));
  }

  @Test
  public void hash_of_computation_with_convert_from_nothing_algorithm_and_one_element_input_is_stable() {
    var algorithm = new ConvertAlgorithm(stringHT());
    var input = input(list(stringH("abc")));
    assertThat(computationHash(Hash.of(13), algorithm, input))
        .isEqualTo(Hash.decode("a025bfcacf9dadbc72256e41fbc60f4f5355233b"));
  }

  @Test
  public void hash_of_computation_with_construct_algorithm_and_empty_input_is_stable() {
    var algorithm = new ConstructAlgorithm(PERSON);
    var input = input(list());
    assertThat(computationHash(Hash.of(13), algorithm, input))
        .isEqualTo(Hash.decode("ff45a42fc341a33b8835f873f94a083181e665a4"));
  }

  @Test
  public void hash_of_computation_with_construct_algorithm_and_one_element_input_is_stable() {
    var algorithm = new ConstructAlgorithm(PERSON);
    var input = input(list(stringH("abc")));
    assertThat(computationHash(Hash.of(13), algorithm, input))
        .isEqualTo(Hash.decode("b7f6067462bee16b2bf3e9ab5c54c3e4bd1eba54"));
  }

  @Test
  public void hash_of_computation_with_construct_algorithm_and_two_elements_input_is_stable() {
    var algorithm = new ConstructAlgorithm(PERSON);
    var input = input(list(stringH("abc"), stringH("def")));
    assertThat(computationHash(Hash.of(13), algorithm, input))
        .isEqualTo(Hash.decode("b598a215e92550d41b2772ff96ba134e4b544dbf"));
  }

  @Test
  public void hash_of_computation_with_select_algorithm_and_one_element_input_is_stable() {
    var algorithm = new SelectAlgorithm(intH(0), TestingTypesH.STRING);
    var input = input(list(stringH("abc")));
    assertThat(computationHash(Hash.of(13), algorithm, input))
        .isEqualTo(Hash.decode("6513f0e8ee2db27f5e59b8ac0a7cf0b8cd2b16b0"));
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
