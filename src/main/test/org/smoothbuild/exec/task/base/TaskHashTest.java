package org.smoothbuild.exec.task.base;

import static com.google.common.truth.Truth.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.smoothbuild.exec.comp.Input.input;
import static org.smoothbuild.lang.base.Location.unknownLocation;
import static org.smoothbuild.util.Lists.list;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.exec.comp.AccessorCallAlgorithm;
import org.smoothbuild.exec.comp.Algorithm;
import org.smoothbuild.exec.comp.ArrayLiteralAlgorithm;
import org.smoothbuild.exec.comp.ConstructorCallAlgorithm;
import org.smoothbuild.exec.comp.ConvertAlgorithm;
import org.smoothbuild.exec.comp.IdentityAlgorithm;
import org.smoothbuild.exec.comp.Input;
import org.smoothbuild.exec.comp.NativeCallAlgorithm;
import org.smoothbuild.exec.comp.Output;
import org.smoothbuild.exec.comp.ValueAlgorithm;
import org.smoothbuild.lang.base.Accessor;
import org.smoothbuild.lang.base.Constructor;
import org.smoothbuild.lang.base.NativeFunction;
import org.smoothbuild.lang.base.Signature;
import org.smoothbuild.lang.object.type.ConcreteType;
import org.smoothbuild.lang.plugin.NativeApi;
import org.smoothbuild.testing.TestingContext;

public class TaskHashTest extends TestingContext {
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
  public void hash_of_execution_with_empty_string_value_algorithm_is_stable() {
    Algorithm algorithm = new ValueAlgorithm(string(""));
    Input input = input(list());
    assertThat(computationHash(algorithm, input, Hash.of(13)))
        .isEqualTo(Hash.decode("fb03996d8c7b95104ec51115c5c275fe91f0a9ee"));
  }

  @Test
  public void hash_of_execution_with_string_value_algorithm_is_stable() {
    Algorithm algorithm = new ValueAlgorithm(string("value"));
    Input input = input(list());
    assertThat(computationHash(algorithm, input, Hash.of(13)))
        .isEqualTo(Hash.decode("db9ee08a5acecc6c6bdbecee8154489d6d5ef089"));
  }

  @Test
  public void hash_of_execution_with_array_algorithm_and_empty_input_is_stable() {
    Algorithm algorithm = new ArrayLiteralAlgorithm(arrayType(stringType()));
    Input input = input(list());
    assertThat(computationHash(algorithm, input, Hash.of(13)))
        .isEqualTo(Hash.decode("d20343333435effc353d96a8704cd929f7c39498"));
  }

  @Test
  public void hash_of_execution_with_array_algorithm_and_non_empty_input_is_stable() {
    Algorithm algorithm = new ArrayLiteralAlgorithm(arrayType(stringType()));
    Input input = input(list(string("abc"), string("def")));
    assertThat(computationHash(algorithm, input, Hash.of(13)))
        .isEqualTo(Hash.decode("ed225677d4183c156bde26a8a4b5f6184e53b2d1"));
  }

  @Test
  public void hash_of_execution_with_native_call_algorithm_and_empty_input_is_stable() {
    Algorithm algorithm = new NativeCallAlgorithm(stringType(), mockNativeFunction());
    Input input = input(list());
    assertThat(computationHash(algorithm, input, Hash.of(13)))
        .isEqualTo(Hash.decode("36cf27551327ed05f5c0122dcced61e86ce01e84"));
  }

  @Test
  public void hash_of_execution_with_native_call_algorithm_and_non_empty_input_is_stable() {
    Algorithm algorithm = new NativeCallAlgorithm(stringType(), mockNativeFunction());
    Input input = input(list(string("abc"), string("def")));
    assertThat(computationHash(algorithm, input, Hash.of(13)))
        .isEqualTo(Hash.decode("e3b8532fb64253f571926e8e3cf459a57be34aa0"));
  }

  @Test
  public void hash_of_execution_with_identity_algorithm_and_one_element_input_is_stable() {
    Algorithm algorithm = new IdentityAlgorithm("name", stringType());
    Input input = input(list(string("abc")));
    assertThat(computationHash(algorithm, input, Hash.of(13)))
        .isEqualTo(Hash.decode("68f494d78c566e029fa288f0aa36b33a2f383ba7"));
  }

  @Test
  public void hash_of_execution_with_convert_from_nothing_algorithm_and_one_element_input_is_stable() {
    Algorithm algorithm = new ConvertAlgorithm(stringType());
    Input input = input(list(string("abc")));
    assertThat(computationHash(algorithm, input, Hash.of(13)))
        .isEqualTo(Hash.decode("2e18856c213531ddc9f883907e90eda84c8e3e20"));
  }

  @Test
  public void hash_of_execution_with_constructor_call_algorithm_and_empty_input_is_stable() {
    Algorithm algorithm = new ConstructorCallAlgorithm(constructor());
    Input input = input(list());
    assertThat(computationHash(algorithm, input, Hash.of(13)))
        .isEqualTo(Hash.decode("e14ca21fccb6631fd52809c4c4b409f2f66a077c"));
  }

  @Test
  public void hash_of_execution_with_constructor_call_algorithm_and_one_element_input_is_stable() {
    Algorithm algorithm = new ConstructorCallAlgorithm(constructor());
    Input input = input(list(string("abc")));
    assertThat(computationHash(algorithm, input, Hash.of(13)))
        .isEqualTo(Hash.decode("68acb231ddf9eb8d878d1a68897aba09888b3b8e"));
  }

  @Test
  public void hash_of_execution_with_constructor_call_algorithm_and_two_elements_input_is_stable() {
    Algorithm algorithm = new ConstructorCallAlgorithm(constructor());
    Input input = input(list(string("abc"), string("def")));
    assertThat(computationHash(algorithm, input, Hash.of(13)))
        .isEqualTo(Hash.decode("94296c0f10b807bb7f46c7196035c5e787dc03b2"));
  }

  private Constructor constructor() {
    return new Constructor(
        new Signature(personType(), "ConstructorName", list()), unknownLocation());
  }

  @Test
  public void hash_of_execution_with_accessor_call_algorithm_and_one_element_input_is_stable() {
    Algorithm algorithm = new AccessorCallAlgorithm(accessor());
    Input input = input(list(string("abc")));
    assertThat(computationHash(algorithm, input, Hash.of(13)))
        .isEqualTo(Hash.decode("0d0a4fd630fd9ffe0bfe5820a8b38c65b07dcbdc"));
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
    };
  }

  private static NativeFunction mockNativeFunction() {
    NativeFunction nativeFunction = mock(NativeFunction.class);
    when(nativeFunction.hash()).thenReturn(Hash.of(33));
    when(nativeFunction.name()).thenReturn("name");
    return nativeFunction;
  }
}
