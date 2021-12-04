package org.smoothbuild.exec.compute;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.db.object.type.TestingCatsH.PERSON;
import static org.smoothbuild.exec.base.Input.input;
import static org.smoothbuild.exec.compute.Computer.computationHash;
import static org.smoothbuild.util.collect.Lists.list;

import org.junit.jupiter.api.Test;
import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.object.type.TestingCatsH;
import org.smoothbuild.db.object.type.base.TypeH;
import org.smoothbuild.exec.algorithm.Algorithm;
import org.smoothbuild.exec.algorithm.CombineAlgorithm;
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
    var algorithm = new OrderAlgorithm(arrayTH(stringTH()));
    var input = input(list());
    assertThat(computationHash(Hash.of(13), algorithm, input))
        .isEqualTo(Hash.decode("5c7516a24023883079ab5322ca2a208adf83734e"));
  }

  @Test
  public void hash_of_computation_with_order_algorithm_and_non_empty_input_is_stable() {
    var algorithm = new OrderAlgorithm(arrayTH(stringTH()));
    var input = input(list(stringH("abc"), stringH("def")));
    assertThat(computationHash(Hash.of(13), algorithm, input))
        .isEqualTo(Hash.decode("26ca327d87fe70373d6bb568b4af332331b6ea34"));
  }

  @Test
  public void hash_of_computation_with_invoke_algorithm_and_empty_input_is_stable() {
    var natFuncH = natFuncH(blobH(), stringH("class path"));
    var algorithm = new InvokeAlgorithm(stringTH(), "name", natFuncH, null);
    var input = input(list());
    assertThat(computationHash(Hash.of(13), algorithm, input))
        .isEqualTo(Hash.decode("f8891fe1c53f463d5917970ca0b51bda154ecdaa"));
  }

  @Test
  public void hash_of_computation_with_invoke_algorithm_and_non_empty_input_is_stable() {
    var natFuncH = natFuncH(blobH(), stringH("class path"));
    var algorithm = new InvokeAlgorithm(stringTH(), "name", natFuncH, null);
    var input = input(list(stringH("abc"), stringH("def")));
    assertThat(computationHash(Hash.of(13), algorithm, input))
        .isEqualTo(Hash.decode("33d8dc290cb161e148bf90770978131e423ea046"));
  }

  @Test
  public void hash_of_computation_with_combine_algorithm_and_empty_input_is_stable() {
    var algorithm = new CombineAlgorithm(PERSON);
    var input = input(list());
    assertThat(computationHash(Hash.of(13), algorithm, input))
        .isEqualTo(Hash.decode("6f10f67d3aae226d273a3b334a75b04885a94a58"));
  }

  @Test
  public void hash_of_computation_with_combine_algorithm_and_one_elem_input_is_stable() {
    var algorithm = new CombineAlgorithm(PERSON);
    var input = input(list(stringH("abc")));
    assertThat(computationHash(Hash.of(13), algorithm, input))
        .isEqualTo(Hash.decode("7f71c36dc7c56b6415492174e8fab4c70a000778"));
  }

  @Test
  public void hash_of_computation_with_combine_algorithm_and_two_elems_input_is_stable() {
    var algorithm = new CombineAlgorithm(PERSON);
    var input = input(list(stringH("abc"), stringH("def")));
    assertThat(computationHash(Hash.of(13), algorithm, input))
        .isEqualTo(Hash.decode("cd2121b413875e2b612119751ef9fec7dbd4834f"));
  }

  @Test
  public void hash_of_computation_with_select_algorithm_and_one_elem_input_is_stable() {
    var algorithm = new SelectAlgorithm(intH(0), TestingCatsH.STRING);
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
      public TypeH outputT() {
        return null;
      }

      @Override
      public Output run(Input input, NativeApi nativeApi) {
        return null;
      }
    };
  }
}
