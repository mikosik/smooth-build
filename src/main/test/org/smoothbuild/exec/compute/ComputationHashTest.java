package org.smoothbuild.exec.compute;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.db.object.spec.TestingSpecs.PERSON;
import static org.smoothbuild.db.object.spec.TestingSpecs.STR;
import static org.smoothbuild.exec.base.Input.input;
import static org.smoothbuild.exec.compute.Computer.computationHash;
import static org.smoothbuild.lang.TestingLang.function;
import static org.smoothbuild.util.Lists.list;

import org.junit.jupiter.api.Test;
import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.object.spec.Spec;
import org.smoothbuild.exec.algorithm.Algorithm;
import org.smoothbuild.exec.algorithm.CallNativeAlgorithm;
import org.smoothbuild.exec.algorithm.ConvertAlgorithm;
import org.smoothbuild.exec.algorithm.CreateArrayAlgorithm;
import org.smoothbuild.exec.algorithm.CreateTupleAlgorithm;
import org.smoothbuild.exec.algorithm.ReadTupleElementAlgorithm;
import org.smoothbuild.exec.base.Input;
import org.smoothbuild.exec.base.Output;
import org.smoothbuild.lang.base.type.TestingTypes;
import org.smoothbuild.plugin.NativeApi;
import org.smoothbuild.testing.TestingContext;

public class ComputationHashTest extends TestingContext {
  @Test
  public void hashes_of_computations_with_same_algorithm_runtime_and_input_are_equal() {
    Algorithm algorithm = computation(Hash.of(1));
    Input input = input(list(strV("input")));
    assertThat(computationHash(Hash.of(13), algorithm, input))
        .isEqualTo(computationHash(Hash.of(13), algorithm, input));
  }

  @Test
  public void hashes_of_computations_with_different_algorithm_but_same_runtime_and_input_are_not_equal() {
    Algorithm algorithm = computation(Hash.of(1));
    Algorithm algorithm2 = computation(Hash.of(2));
    Input input = input(list(strV("input")));
    assertThat(computationHash(Hash.of(13), algorithm, input))
        .isNotEqualTo(computationHash(Hash.of(13), algorithm2, input));
  }

  @Test
  public void hashes_of_computations_with_same_algorithm_and_input_but_different_runtime_are_not_equal() {
    Algorithm algorithm = computation(Hash.of(1));
    Input input = input(list(strV("input")));
    assertThat(computationHash(Hash.of(13), algorithm, input))
        .isNotEqualTo(computationHash(Hash.of(14), algorithm, input));
  }

  @Test
  public void hashes_of_computations_with_same_algorithm_runtime_but_different_input_are_not_equal() {
    Algorithm algorithm = computation(Hash.of(1));
    Input input = input(list(strV("input")));
    Input input2 = input(list(strV("input2")));
    assertThat(computationHash(Hash.of(13), algorithm, input))
        .isNotEqualTo(computationHash(Hash.of(13), algorithm, input2));
  }

  @Test
  public void hash_of_computation_with_array_algorithm_and_empty_input_is_stable() {
    Algorithm algorithm = new CreateArrayAlgorithm(arrayS(strS()));
    Input input = input(list());
    assertThat(computationHash(Hash.of(13), algorithm, input))
        .isEqualTo(Hash.decode("16457f3457ec260bb4be0161933c32010b162123"));
  }

  @Test
  public void hash_of_computation_with_array_algorithm_and_non_empty_input_is_stable() {
    Algorithm algorithm = new CreateArrayAlgorithm(arrayS(strS()));
    Input input = input(list(strV("abc"), strV("def")));
    assertThat(computationHash(Hash.of(13), algorithm, input))
        .isEqualTo(Hash.decode("26014fc98929cdf79a7b7c96ecbc56d495d595fe"));
  }

  @Test
  public void hash_of_computation_with_native_call_algorithm_and_empty_input_is_stable() {
    Algorithm algorithm = new CallNativeAlgorithm(
        null, strS(), function(TestingTypes.STRING, "name"), true);
    Input input = input(list());
    assertThat(computationHash(Hash.of(13), algorithm, input))
        .isEqualTo(Hash.decode("fa404053c470625cc32d666d02acd1cc634e2bb5"));
  }

  @Test
  public void hash_of_computation_with_native_call_algorithm_and_non_empty_input_is_stable() {
    Algorithm algorithm = new CallNativeAlgorithm(
        null, strS(), function(TestingTypes.STRING, "name"), true);
    Input input = input(list(strV("abc"), strV("def")));
    assertThat(computationHash(Hash.of(13), algorithm, input))
        .isEqualTo(Hash.decode("995de0b5317252aa56dc55f49d1d2b043500e8c4"));
  }

  @Test
  public void hash_of_computation_with_convert_from_nothing_algorithm_and_one_element_input_is_stable() {
    Algorithm algorithm = new ConvertAlgorithm(strS());
    Input input = input(list(strV("abc")));
    assertThat(computationHash(Hash.of(13), algorithm, input))
        .isEqualTo(Hash.decode("f8f5cf59eec8d5ee4833d1086e21c8aa3f585234"));
  }

  @Test
  public void hash_of_computation_with_constructor_call_algorithm_and_empty_input_is_stable() {
    Algorithm algorithm = new CreateTupleAlgorithm(PERSON);
    Input input = input(list());
    assertThat(computationHash(Hash.of(13), algorithm, input))
        .isEqualTo(Hash.decode("b9f0b6c1b4fbdfb9547d1d258e01e651c387a41b"));
  }

  @Test
  public void hash_of_computation_with_constructor_call_algorithm_and_one_element_input_is_stable() {
    Algorithm algorithm = new CreateTupleAlgorithm(PERSON);
    Input input = input(list(strV("abc")));
    assertThat(computationHash(Hash.of(13), algorithm, input))
        .isEqualTo(Hash.decode("b83ff59d3701bbafe020d6ce781c6379457c1a68"));
  }

  @Test
  public void hash_of_computation_with_constructor_call_algorithm_and_two_elements_input_is_stable() {
    Algorithm algorithm = new CreateTupleAlgorithm(PERSON);
    Input input = input(list(strV("abc"), strV("def")));
    assertThat(computationHash(Hash.of(13), algorithm, input))
        .isEqualTo(Hash.decode("8e2cc0db36ad999d771099123fad07e0b48a75ae"));
  }

  @Test
  public void hash_of_computation_with_read_tuple_element_algorithm_and_one_element_input_is_stable() {
    Algorithm algorithm = new ReadTupleElementAlgorithm(0, STR);
    Input input = input(list(strV("abc")));
    assertThat(computationHash(Hash.of(13), algorithm, input))
        .isEqualTo(Hash.decode("5291ac7b7c636d511423afcd63dbe8a65f4cb3d7"));
  }

  private static Algorithm computation(Hash hash) {
    return new Algorithm(null) {
      @Override
      public Hash hash() {
        return hash;
      }

      @Override
      public Spec outputSpec() {
        return null;
      }

      @Override
      public Output run(Input input, NativeApi nativeApi) {
        return null;
      }
    };
  }
}
