package org.smoothbuild.exec.task.base;

import static com.google.common.truth.Truth.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.smoothbuild.exec.comp.Input.input;
import static org.smoothbuild.exec.task.base.Computer.computationHash;
import static org.smoothbuild.exec.task.base.TaskKind.BUILDING_NATIVE_CALL;
import static org.smoothbuild.lang.base.Location.unknownLocation;
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
import org.smoothbuild.lang.base.Signature;
import org.smoothbuild.lang.object.type.ConcreteType;
import org.smoothbuild.lang.plugin.NativeApi;
import org.smoothbuild.testing.TestingContext;

public class ComputationHashTest extends TestingContext {
  @Test
  public void hashes_of_computations_with_same_algorithm_runtime_and_input_are_equal() {
    Algorithm algorithm = computation(Hash.of(1));
    Input input = input(list(string("input")));
    assertThat(computationHash(algorithm, input, Hash.of(13)))
        .isEqualTo(computationHash(algorithm, input, Hash.of(13)));
  }

  @Test
  public void hashes_of_computations_with_different_algorithm_but_same_runtime_and_input_are_not_equal() {
    Algorithm algorithm = computation(Hash.of(1));
    Algorithm algorithm2 = computation(Hash.of(2));
    Input input = input(list(string("input")));
    assertThat(computationHash(algorithm, input, Hash.of(13)))
        .isNotEqualTo(computationHash(algorithm2, input, Hash.of(13)));
  }

  @Test
  public void hashes_of_computations_with_same_algorithm_and_input_but_different_runtime_are_not_equal() {
    Algorithm algorithm = computation(Hash.of(1));
    Input input = input(list(string("input")));
    assertThat(computationHash(algorithm, input, Hash.of(13)))
        .isNotEqualTo(computationHash(algorithm, input, Hash.of(14)));
  }

  @Test
  public void hashes_of_computations_with_same_algorithm_runtime_but_different_input_are_not_equal() {
    Algorithm algorithm = computation(Hash.of(1));
    Input input = input(list(string("input")));
    Input input2 = input(list(string("input2")));
    assertThat(computationHash(algorithm, input, Hash.of(13)))
        .isNotEqualTo(computationHash(algorithm, input2, Hash.of(13)));
  }

  @Test
  public void hash_of_computation_with_array_algorithm_and_empty_input_is_stable() {
    Algorithm algorithm = new ArrayLiteralAlgorithm(arrayType(stringType()));
    Input input = input(list());
    assertThat(computationHash(algorithm, input, Hash.of(13)))
        .isEqualTo(Hash.decode("16457f3457ec260bb4be0161933c32010b162123"));
  }

  @Test
  public void hash_of_computation_with_array_algorithm_and_non_empty_input_is_stable() {
    Algorithm algorithm = new ArrayLiteralAlgorithm(arrayType(stringType()));
    Input input = input(list(string("abc"), string("def")));
    assertThat(computationHash(algorithm, input, Hash.of(13)))
        .isEqualTo(Hash.decode("8dc297a86b455810ee2558f9e6eb8893c524f861"));
  }

  @Test
  public void hash_of_computation_with_native_call_algorithm_and_empty_input_is_stable() {
    Algorithm algorithm = new NativeCallAlgorithm(stringType(), mockNativeFunction());
    Input input = input(list());
    assertThat(computationHash(algorithm, input, Hash.of(13)))
        .isEqualTo(Hash.decode("d66663fdde3d70dda784d5b035578cbd7bd6c184"));
  }

  @Test
  public void hash_of_computation_with_native_call_algorithm_and_non_empty_input_is_stable() {
    Algorithm algorithm = new NativeCallAlgorithm(stringType(), mockNativeFunction());
    Input input = input(list(string("abc"), string("def")));
    assertThat(computationHash(algorithm, input, Hash.of(13)))
        .isEqualTo(Hash.decode("425c7c9760d1e93908cc13c04bdb4092eee8561d"));
  }

  @Test
  public void hash_of_computation_with_convert_from_nothing_algorithm_and_one_element_input_is_stable() {
    Algorithm algorithm = new ConvertAlgorithm(stringType(), stringType());
    Input input = input(list(string("abc")));
    assertThat(computationHash(algorithm, input, Hash.of(13)))
        .isEqualTo(Hash.decode("1ec66039449159837c5e5e82ce3da7bbf89ed417"));
  }

  @Test
  public void hash_of_computation_with_constructor_call_algorithm_and_empty_input_is_stable() {
    Algorithm algorithm = new ConstructorCallAlgorithm(constructor());
    Input input = input(list());
    assertThat(computationHash(algorithm, input, Hash.of(13)))
        .isEqualTo(Hash.decode("af8f490d36d092f1c660585e557ff988b1048c17"));
  }

  @Test
  public void hash_of_computation_with_constructor_call_algorithm_and_one_element_input_is_stable() {
    Algorithm algorithm = new ConstructorCallAlgorithm(constructor());
    Input input = input(list(string("abc")));
    assertThat(computationHash(algorithm, input, Hash.of(13)))
        .isEqualTo(Hash.decode("0df5916d25416379005a8e4700fca183b7ae86da"));
  }

  @Test
  public void hash_of_computation_with_constructor_call_algorithm_and_two_elements_input_is_stable() {
    Algorithm algorithm = new ConstructorCallAlgorithm(constructor());
    Input input = input(list(string("abc"), string("def")));
    assertThat(computationHash(algorithm, input, Hash.of(13)))
        .isEqualTo(Hash.decode("55076a75491bc119310d29f3d4239ebeba07360f"));
  }

  private Constructor constructor() {
    return new Constructor(
        new Signature(personType(), "ConstructorName", list()), unknownLocation());
  }

  @Test
  public void hash_of_computation_with_accessor_call_algorithm_and_one_element_input_is_stable() {
    Algorithm algorithm = new AccessorCallAlgorithm(accessor());
    Input input = input(list(string("abc")));
    assertThat(computationHash(algorithm, input, Hash.of(13)))
        .isEqualTo(Hash.decode("d4ef1a2529ad47aca82b72dd736dccc1ea4c1f01"));
  }

  private Accessor accessor() {
    return new Accessor(
        new Signature(stringType(), "accessor", list()), "fieldName", unknownLocation());
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
        return BUILDING_NATIVE_CALL;
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
