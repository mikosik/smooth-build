package org.smoothbuild.vm.compute;

import static org.smoothbuild.db.bytecode.type.TestingCatsB.INT;
import static org.smoothbuild.db.bytecode.type.TestingCatsB.PERSON;
import static org.smoothbuild.util.collect.Lists.list;
import static org.smoothbuild.vm.job.algorithm.Input.input;

import org.junit.jupiter.api.Test;
import org.smoothbuild.db.bytecode.type.TestingCatsB;
import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.plugin.NativeApi;
import org.smoothbuild.testing.TestingContext;
import org.smoothbuild.vm.job.algorithm.Algorithm;
import org.smoothbuild.vm.job.algorithm.CombineAlgorithm;
import org.smoothbuild.vm.job.algorithm.Input;
import org.smoothbuild.vm.job.algorithm.InvokeAlgorithm;
import org.smoothbuild.vm.job.algorithm.OrderAlgorithm;
import org.smoothbuild.vm.job.algorithm.Output;
import org.smoothbuild.vm.job.algorithm.SelectAlgorithm;

import com.google.common.truth.Truth;

public class ComputationHashTest extends TestingContext {
  @Test
  public void hashes_of_computations_with_same_algorithm_runtime_and_input_are_equal() {
    var algorithm = algorithm(Hash.of(1));
    var input = input(list(stringB("input")));
    Truth.assertThat(Computer.computationHash(Hash.of(13), algorithm, input))
        .isEqualTo(Computer.computationHash(Hash.of(13), algorithm, input));
  }

  @Test
  public void hashes_of_computations_with_different_algorithm_but_same_runtime_and_input_are_not_equal() {
    var algorithm1 = algorithm(Hash.of(1));
    var algorithm2 = algorithm(Hash.of(2));
    var input = input(list(stringB("input")));
    Truth.assertThat(Computer.computationHash(Hash.of(13), algorithm1, input))
        .isNotEqualTo(Computer.computationHash(Hash.of(13), algorithm2, input));
  }

  @Test
  public void hashes_of_computations_with_same_algorithm_and_input_but_different_runtime_are_not_equal() {
    var algorithm = algorithm(Hash.of(1));
    var input = input(list(stringB("input")));
    Truth.assertThat(Computer.computationHash(Hash.of(13), algorithm, input))
        .isNotEqualTo(Computer.computationHash(Hash.of(14), algorithm, input));
  }

  @Test
  public void hashes_of_computations_with_same_algorithm_runtime_but_different_input_are_not_equal() {
    var algorithm = algorithm(Hash.of(1));
    var input1 = input(list(stringB("input")));
    var input2 = input(list(stringB("input2")));
    Truth.assertThat(Computer.computationHash(Hash.of(13), algorithm, input1))
        .isNotEqualTo(Computer.computationHash(Hash.of(13), algorithm, input2));
  }

  @Test
  public void hash_of_computation_with_order_algorithm_and_empty_input_is_stable() {
    var algorithm = new OrderAlgorithm(arrayTB(stringTB()));
    var input = input(list());
    Truth.assertThat(Computer.computationHash(Hash.of(13), algorithm, input))
        .isEqualTo(Hash.decode("41ebaae34bb9d4112df469d1099b48425d6d08a8"));
  }

  @Test
  public void hash_of_computation_with_order_algorithm_and_non_empty_input_is_stable() {
    var algorithm = new OrderAlgorithm(arrayTB(stringTB()));
    var input = input(list(stringB("abc"), stringB("def")));
    Truth.assertThat(Computer.computationHash(Hash.of(13), algorithm, input))
        .isEqualTo(Hash.decode("d8de4a0428a84c9e51f8a98bbf89df0a5c126861"));
  }

  @Test
  public void hash_of_computation_with_invoke_algorithm_and_empty_input_is_stable() {
    var method = methodB(methodTB(intTB(), list()), blobB(1), stringB("1"), boolB(true));
    var algorithm = new InvokeAlgorithm(stringTB(), "name", method, null);
    var input = input(list());
    Truth.assertThat(Computer.computationHash(Hash.of(13), algorithm, input))
        .isEqualTo(Hash.decode("07de1e56ffb6edbbd8e474691631991ab52ce067"));
  }

  @Test
  public void hash_of_computation_with_invoke_algorithm_and_non_empty_input_is_stable() {
    var method = methodB(methodTB(intTB(), list()), blobB(1), stringB("1"), boolB(true));
    var algorithm = new InvokeAlgorithm(stringTB(), "name", method, null);
    var input = input(list(stringB("abc"), stringB("def")));
    Truth.assertThat(Computer.computationHash(Hash.of(13), algorithm, input))
        .isEqualTo(Hash.decode("978f490dfaf1be635689956bf7d0aa6a120f7f2f"));
  }

  @Test
  public void hash_of_computation_with_combine_algorithm_and_empty_input_is_stable() {
    var algorithm = new CombineAlgorithm(PERSON);
    var input = input(list());
    Truth.assertThat(Computer.computationHash(Hash.of(13), algorithm, input))
        .isEqualTo(Hash.decode("6f10f67d3aae226d273a3b334a75b04885a94a58"));
  }

  @Test
  public void hash_of_computation_with_combine_algorithm_and_one_elem_input_is_stable() {
    var algorithm = new CombineAlgorithm(PERSON);
    var input = input(list(stringB("abc")));
    Truth.assertThat(Computer.computationHash(Hash.of(13), algorithm, input))
        .isEqualTo(Hash.decode("7f71c36dc7c56b6415492174e8fab4c70a000778"));
  }

  @Test
  public void hash_of_computation_with_combine_algorithm_and_two_elems_input_is_stable() {
    var algorithm = new CombineAlgorithm(PERSON);
    var input = input(list(stringB("abc"), stringB("def")));
    Truth.assertThat(Computer.computationHash(Hash.of(13), algorithm, input))
        .isEqualTo(Hash.decode("cd2121b413875e2b612119751ef9fec7dbd4834f"));
  }

  @Test
  public void hash_of_computation_with_select_algorithm_and_one_elem_input_is_stable() {
    var algorithm = new SelectAlgorithm(TestingCatsB.STRING);
    var input = input(list(stringB("abc")));
    Truth.assertThat(Computer.computationHash(Hash.of(13), algorithm, input))
        .isEqualTo(Hash.decode("930d713aa3678bf1fab120b8925aadd428b79ce9"));
  }

  private static Algorithm algorithm(Hash hash) {
    return new Algorithm(INT) {
      @Override
      public Hash hash() {
        return hash;
      }

      @Override
      public Output run(Input input, NativeApi nativeApi) {
        return null;
      }
    };
  }
}
