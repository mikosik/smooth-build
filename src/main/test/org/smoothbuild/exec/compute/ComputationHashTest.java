package org.smoothbuild.exec.compute;

import static com.google.common.truth.Truth.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.smoothbuild.db.record.spec.TestingSpecs.PERSON;
import static org.smoothbuild.db.record.spec.TestingSpecs.STRING;
import static org.smoothbuild.exec.algorithm.Input.input;
import static org.smoothbuild.exec.compute.Computer.computationHash;
import static org.smoothbuild.lang.base.Location.internal;
import static org.smoothbuild.lang.base.Signature.signature;
import static org.smoothbuild.util.Lists.list;

import org.junit.jupiter.api.Test;
import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.record.spec.Spec;
import org.smoothbuild.exec.algorithm.Algorithm;
import org.smoothbuild.exec.algorithm.CallNativeAlgorithm;
import org.smoothbuild.exec.algorithm.ConvertAlgorithm;
import org.smoothbuild.exec.algorithm.CreateArrayAlgorithm;
import org.smoothbuild.exec.algorithm.CreateTupleAlgorithm;
import org.smoothbuild.exec.algorithm.Input;
import org.smoothbuild.exec.algorithm.Output;
import org.smoothbuild.exec.algorithm.ReadTupleElementAlgorithm;
import org.smoothbuild.lang.base.Accessor;
import org.smoothbuild.lang.base.NativeFunction;
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
    Algorithm algorithm = new CreateArrayAlgorithm(arraySpec(stringSpec()));
    Input input = input(list());
    assertThat(computationHash(Hash.of(13), algorithm, input))
        .isEqualTo(Hash.decode("16457f3457ec260bb4be0161933c32010b162123"));
  }

  @Test
  public void hash_of_computation_with_array_algorithm_and_non_empty_input_is_stable() {
    Algorithm algorithm = new CreateArrayAlgorithm(arraySpec(stringSpec()));
    Input input = input(list(string("abc"), string("def")));
    assertThat(computationHash(Hash.of(13), algorithm, input))
        .isEqualTo(Hash.decode("a45fc25de1f36f700edd0a7a6fbfbde52a52796c"));
  }

  @Test
  public void hash_of_computation_with_native_call_algorithm_and_empty_input_is_stable() {
    Algorithm algorithm = new CallNativeAlgorithm(stringSpec(), mockNativeFunction());
    Input input = input(list());
    assertThat(computationHash(Hash.of(13), algorithm, input))
        .isEqualTo(Hash.decode("d66663fdde3d70dda784d5b035578cbd7bd6c184"));
  }

  @Test
  public void hash_of_computation_with_native_call_algorithm_and_non_empty_input_is_stable() {
    Algorithm algorithm = new CallNativeAlgorithm(stringSpec(), mockNativeFunction());
    Input input = input(list(string("abc"), string("def")));
    assertThat(computationHash(Hash.of(13), algorithm, input))
        .isEqualTo(Hash.decode("556691e768974b206168183cee2224cb0b09fb90"));
  }

  @Test
  public void hash_of_computation_with_convert_from_nothing_algorithm_and_one_element_input_is_stable() {
    Algorithm algorithm = new ConvertAlgorithm(stringSpec());
    Input input = input(list(string("abc")));
    assertThat(computationHash(Hash.of(13), algorithm, input))
        .isEqualTo(Hash.decode("8f4c9864950191a3fd2fc6ef4dff00c6ffe8b21b"));
  }

  @Test
  public void hash_of_computation_with_constructor_call_algorithm_and_empty_input_is_stable() {
    Algorithm algorithm = new CreateTupleAlgorithm(PERSON);
    Input input = input(list());
    assertThat(computationHash(Hash.of(13), algorithm, input))
        .isEqualTo(Hash.decode("a3d5e4a7dac5d3e61fd9653753e30358253bf54b"));
  }

  @Test
  public void hash_of_computation_with_constructor_call_algorithm_and_one_element_input_is_stable() {
    Algorithm algorithm = new CreateTupleAlgorithm(PERSON);
    Input input = input(list(string("abc")));
    assertThat(computationHash(Hash.of(13), algorithm, input))
        .isEqualTo(Hash.decode("163642381cdf78d703f6cc2df0331a7c83e42963"));
  }

  @Test
  public void hash_of_computation_with_constructor_call_algorithm_and_two_elements_input_is_stable() {
    Algorithm algorithm = new CreateTupleAlgorithm(PERSON);
    Input input = input(list(string("abc"), string("def")));
    assertThat(computationHash(Hash.of(13), algorithm, input))
        .isEqualTo(Hash.decode("a025576b07cabb0e4a364b362a627f114dfe3505"));
  }

  @Test
  public void hash_of_computation_with_accessor_call_algorithm_and_one_element_input_is_stable() {
    Algorithm algorithm = new ReadTupleElementAlgorithm(accessor(), STRING);
    Input input = input(list(string("abc")));
    assertThat(computationHash(Hash.of(13), algorithm, input))
        .isEqualTo(Hash.decode("cb8da34f967f20f29c6216cd84ab979556b2b2ff"));
  }

  private Accessor accessor() {
    return new Accessor(signature(org.smoothbuild.lang.base.type.TestingTypes.STRING,
        "accessor", list()), 0, internal());
  }

  private static Algorithm computation(Hash hash) {
    return new Algorithm() {
      @Override
      public Hash hash() {
        return hash;
      }

      @Override
      public Spec type() {
        return null;
      }

      @Override
      public Output run(Input input, NativeApi nativeApi) {
        return null;
      }
    };
  }

  private static NativeFunction mockNativeFunction() {
    NativeFunction nativeFunction = mock(NativeFunction.class);
    when(nativeFunction.hash()).thenReturn(Hash.of(33));
    when(nativeFunction.name()).thenReturn("name");
    return nativeFunction;
  }
}
