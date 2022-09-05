package org.smoothbuild.vm.compute;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.testing.type.TestingCatsB.INT;
import static org.smoothbuild.testing.type.TestingCatsB.PERSON;
import static org.smoothbuild.testing.type.TestingCatsB.STRING;
import static org.smoothbuild.util.collect.Lists.list;
import static org.smoothbuild.vm.compute.Computer.computationHash;

import org.junit.jupiter.api.Test;
import org.smoothbuild.bytecode.expr.val.TupleB;
import org.smoothbuild.bytecode.hashed.Hash;
import org.smoothbuild.plugin.NativeApi;
import org.smoothbuild.testing.TestContext;
import org.smoothbuild.vm.algorithm.Algorithm;
import org.smoothbuild.vm.algorithm.CombineAlgorithm;
import org.smoothbuild.vm.algorithm.InvokeAlgorithm;
import org.smoothbuild.vm.algorithm.OrderAlgorithm;
import org.smoothbuild.vm.algorithm.Output;
import org.smoothbuild.vm.algorithm.SelectAlgorithm;

public class ComputationHashTest extends TestContext {
  @Test
  public void hashes_of_computations_with_same_algorithm_runtime_and_input_are_equal() {
    var algorithm = algorithm(Hash.of(1));
    var input = tupleB(stringB("input"));
    assertThat(computationHash(Hash.of(13), algorithm, input))
        .isEqualTo(computationHash(Hash.of(13), algorithm, input));
  }

  @Test
  public void hashes_of_computations_with_different_algorithm_but_same_runtime_and_input_are_not_equal() {
    var algorithm1 = algorithm(Hash.of(1));
    var algorithm2 = algorithm(Hash.of(2));
    var input = tupleB(stringB("input"));
    assertThat(computationHash(Hash.of(13), algorithm1, input))
        .isNotEqualTo(computationHash(Hash.of(13), algorithm2, input));
  }

  @Test
  public void hashes_of_computations_with_same_algorithm_and_input_but_different_runtime_are_not_equal() {
    var algorithm = algorithm(Hash.of(1));
    var input = tupleB(stringB("input"));
    assertThat(computationHash(Hash.of(13), algorithm, input))
        .isNotEqualTo(computationHash(Hash.of(14), algorithm, input));
  }

  @Test
  public void hashes_of_computations_with_same_algorithm_runtime_but_different_input_are_not_equal() {
    var algorithm = algorithm(Hash.of(1));
    var input1 = tupleB(stringB("input"));
    var input2 = tupleB(stringB("input2"));
    assertThat(computationHash(Hash.of(13), algorithm, input1))
        .isNotEqualTo(computationHash(Hash.of(13), algorithm, input2));
  }

  @Test
  public void hash_of_computation_with_order_algorithm_and_empty_input_is_stable() {
    var algorithm = new OrderAlgorithm(arrayTB(stringTB()));
    var input = tupleB();
    assertThat(computationHash(Hash.of(13), algorithm, input))
        .isEqualTo(Hash.decode("bf12b71e9cfb738ee7ffb6e57667aea0e5478c6c"));
  }

  @Test
  public void hash_of_computation_with_order_algorithm_and_non_empty_input_is_stable() {
    var algorithm = new OrderAlgorithm(arrayTB(stringTB()));
    var input = tupleB(stringB("abc"), stringB("def"));
    assertThat(computationHash(Hash.of(13), algorithm, input))
        .isEqualTo(Hash.decode("a9116fd5e113887efe896d6e15263cc3f31dbf23"));
  }

  @Test
  public void hash_of_computation_with_invoke_algorithm_and_empty_input_is_stable() {
    var method = methodB(methodTB(intTB(), list()), blobB(1), stringB("1"), boolB(true));
    var algorithm = new InvokeAlgorithm(stringTB(), "name", method, null);
    var input = tupleB();
    assertThat(computationHash(Hash.of(13), algorithm, input))
        .isEqualTo(Hash.decode("b52d91d785a4b35449b0f667423c82527eca83bb"));
  }

  @Test
  public void hash_of_computation_with_invoke_algorithm_and_non_empty_input_is_stable() {
    var method = methodB(methodTB(intTB(), list()), blobB(1), stringB("1"), boolB(true));
    var algorithm = new InvokeAlgorithm(stringTB(), "name", method, null);
    var input = tupleB(stringB("abc"), stringB("def"));
    assertThat(computationHash(Hash.of(13), algorithm, input))
        .isEqualTo(Hash.decode("640b3acc88c7aec5ff496c5976d09eaeaeffdfa5"));
  }

  @Test
  public void hash_of_computation_with_combine_algorithm_and_empty_input_is_stable() {
    var algorithm = new CombineAlgorithm(PERSON);
    var input = tupleB();
    assertThat(computationHash(Hash.of(13), algorithm, input))
        .isEqualTo(Hash.decode("54e3ce86af9aab50564c1eb086d1b6310d347b7b"));
  }

  @Test
  public void hash_of_computation_with_combine_algorithm_and_one_elem_input_is_stable() {
    var algorithm = new CombineAlgorithm(PERSON);
    var input = tupleB(stringB("abc"));
    assertThat(computationHash(Hash.of(13), algorithm, input))
        .isEqualTo(Hash.decode("67525bc73ac50b8e7e3578fff17d7ae07bb6dabc"));
  }

  @Test
  public void hash_of_computation_with_combine_algorithm_and_two_elems_input_is_stable() {
    var algorithm = new CombineAlgorithm(PERSON);
    var input = tupleB(stringB("abc"), stringB("def"));
    assertThat(computationHash(Hash.of(13), algorithm, input))
        .isEqualTo(Hash.decode("e36cc569f40ee06740f06566d491045b02473886"));
  }

  @Test
  public void hash_of_computation_with_select_algorithm_and_one_elem_input_is_stable() {
    var algorithm = new SelectAlgorithm(STRING);
    var input = tupleB(stringB("abc"));
    assertThat(computationHash(Hash.of(13), algorithm, input))
        .isEqualTo(Hash.decode("11e4a89011aa0972ea9b785529d0320a7cdfed14"));
  }

  private static Algorithm algorithm(Hash hash) {
    return new Algorithm(INT) {
      @Override
      public Hash hash() {
        return hash;
      }

      @Override
      public Output run(TupleB input, NativeApi nativeApi) {
        return null;
      }
    };
  }
}
