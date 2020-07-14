package org.smoothbuild.exec.task.base;

import static com.google.common.truth.Truth.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.smoothbuild.exec.comp.Input.input;
import static org.smoothbuild.exec.task.base.Computer.computationHash;
import static org.smoothbuild.exec.task.base.TaskKind.CALL;
import static org.smoothbuild.lang.base.Location.internal;
import static org.smoothbuild.lang.base.Signature.signature;
import static org.smoothbuild.lang.object.type.TestingTypes.person;
import static org.smoothbuild.lang.object.type.TestingTypes.string;
import static org.smoothbuild.util.Lists.list;

import org.junit.jupiter.api.Test;
import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.exec.comp.AccessorCallAlgorithm;
import org.smoothbuild.exec.comp.Algorithm;
import org.smoothbuild.exec.comp.ArrayLiteralAlgorithm;
import org.smoothbuild.exec.comp.ConstructorCallAlgorithm;
import org.smoothbuild.exec.comp.ConvertAlgorithm;
import org.smoothbuild.exec.comp.Input;
import org.smoothbuild.exec.comp.NativeCallAlgorithm;
import org.smoothbuild.exec.comp.Output;
import org.smoothbuild.lang.base.Accessor;
import org.smoothbuild.lang.base.Constructor;
import org.smoothbuild.lang.base.NativeFunction;
import org.smoothbuild.lang.base.type.TestingTypes;
import org.smoothbuild.lang.object.type.ConcreteType;
import org.smoothbuild.lang.plugin.NativeApi;
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
    Algorithm algorithm = new ArrayLiteralAlgorithm(arrayType(stringType()));
    Input input = input(list());
    assertThat(computationHash(Hash.of(13), algorithm, input))
        .isEqualTo(Hash.decode("16457f3457ec260bb4be0161933c32010b162123"));
  }

  @Test
  public void hash_of_computation_with_array_algorithm_and_non_empty_input_is_stable() {
    Algorithm algorithm = new ArrayLiteralAlgorithm(arrayType(stringType()));
    Input input = input(list(string("abc"), string("def")));
    assertThat(computationHash(Hash.of(13), algorithm, input))
        .isEqualTo(Hash.decode("8dc297a86b455810ee2558f9e6eb8893c524f861"));
  }

  @Test
  public void hash_of_computation_with_native_call_algorithm_and_empty_input_is_stable() {
    Algorithm algorithm = new NativeCallAlgorithm(stringType(), mockNativeFunction());
    Input input = input(list());
    assertThat(computationHash(Hash.of(13), algorithm, input))
        .isEqualTo(Hash.decode("d66663fdde3d70dda784d5b035578cbd7bd6c184"));
  }

  @Test
  public void hash_of_computation_with_native_call_algorithm_and_non_empty_input_is_stable() {
    Algorithm algorithm = new NativeCallAlgorithm(stringType(), mockNativeFunction());
    Input input = input(list(string("abc"), string("def")));
    assertThat(computationHash(Hash.of(13), algorithm, input))
        .isEqualTo(Hash.decode("425c7c9760d1e93908cc13c04bdb4092eee8561d"));
  }

  @Test
  public void hash_of_computation_with_convert_from_nothing_algorithm_and_one_element_input_is_stable() {
    Algorithm algorithm = new ConvertAlgorithm(stringType(), stringType());
    Input input = input(list(string("abc")));
    assertThat(computationHash(Hash.of(13), algorithm, input))
        .isEqualTo(Hash.decode("1ec66039449159837c5e5e82ce3da7bbf89ed417"));
  }

  @Test
  public void hash_of_computation_with_constructor_call_algorithm_and_empty_input_is_stable() {
    Algorithm algorithm = new ConstructorCallAlgorithm(constructor(),
        person);
    Input input = input(list());
    assertThat(computationHash(Hash.of(13), algorithm, input))
        .isEqualTo(Hash.decode("84859bd0be7a07c5381a8bdb6e0550399befda04"));
  }

  @Test
  public void hash_of_computation_with_constructor_call_algorithm_and_one_element_input_is_stable() {
    Algorithm algorithm = new ConstructorCallAlgorithm(constructor(), person);
    Input input = input(list(string("abc")));
    assertThat(computationHash(Hash.of(13), algorithm, input))
        .isEqualTo(Hash.decode("ce4042c0d29b51e2b526ef5b095e7284215f6b4a"));
  }

  @Test
  public void hash_of_computation_with_constructor_call_algorithm_and_two_elements_input_is_stable() {
    Algorithm algorithm = new ConstructorCallAlgorithm(constructor(), person);
    Input input = input(list(string("abc"), string("def")));
    assertThat(computationHash(Hash.of(13), algorithm, input))
        .isEqualTo(Hash.decode("ff94e04a74fcf260266d8b9247ff5e9642944845"));
  }

  private Constructor constructor() {
    return new Constructor(signature(TestingTypes.person, "ConstructorName", list()), internal());
  }

  @Test
  public void hash_of_computation_with_accessor_call_algorithm_and_one_element_input_is_stable() {
    Algorithm algorithm = new AccessorCallAlgorithm(accessor(), string);
    Input input = input(list(string("abc")));
    assertThat(computationHash(Hash.of(13), algorithm, input))
        .isEqualTo(Hash.decode("8d7e433df55397d1a4c9153f5168ad7aef261644"));
  }

  private Accessor accessor() {
    return new Accessor(signature(org.smoothbuild.lang.base.type.TestingTypes.string,
        "accessor", list()), 0, internal());
  }

  private static Algorithm computation(Hash hash) {
    return new Algorithm() {
      @Override
      public String name() {
        return "computation-name";
      }

      @Override
      public Hash hash() {
        return hash;
      }

      @Override
      public ConcreteType type() {
        return null;
      }

      @Override
      public Output run(Input input, NativeApi nativeApi) {
        return null;
      }

      @Override
      public TaskKind kind() {
        return CALL;
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
