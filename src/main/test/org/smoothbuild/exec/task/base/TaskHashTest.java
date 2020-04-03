package org.smoothbuild.exec.task.base;

import static com.google.common.truth.Truth.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.smoothbuild.exec.comp.Input.input;
import static org.smoothbuild.exec.task.base.TaskExecutor.executionHash;
import static org.smoothbuild.lang.base.Location.unknownLocation;
import static org.smoothbuild.util.Lists.list;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.exec.comp.AccessorCallComputation;
import org.smoothbuild.exec.comp.ArrayLiteralComputation;
import org.smoothbuild.exec.comp.Computation;
import org.smoothbuild.exec.comp.ConstructorCallComputation;
import org.smoothbuild.exec.comp.ConvertComputation;
import org.smoothbuild.exec.comp.IdentityComputation;
import org.smoothbuild.exec.comp.Input;
import org.smoothbuild.exec.comp.NativeCallComputation;
import org.smoothbuild.exec.comp.Output;
import org.smoothbuild.exec.comp.ValueComputation;
import org.smoothbuild.lang.base.Accessor;
import org.smoothbuild.lang.base.Constructor;
import org.smoothbuild.lang.base.NativeFunction;
import org.smoothbuild.lang.base.Signature;
import org.smoothbuild.lang.object.type.ConcreteType;
import org.smoothbuild.lang.plugin.NativeApi;
import org.smoothbuild.testing.TestingContext;

public class TaskHashTest extends TestingContext {
  @Test
  public void hashes_of_executions_with_same_computation_runtime_and_input_are_equal() {
    Computation computation = computation(Hash.of(1));
    Task task = task(computation, list());
    Task task2 = task(computation, list());
    Input input = input(list(string("input")));
    assertThat(executionHash(task, input, Hash.of(13)))
        .isEqualTo(executionHash(task2, input, Hash.of(13)));
  }

  @Test
  public void hashes_of_executions_with_different_computation_but_same_runtime_and_input_are_not_equal() {
    Computation computation = computation(Hash.of(1));
    Computation computation2 = computation(Hash.of(2));
    Task task = task(computation, list());
    Task task2 = task(computation2, list());
    Input input = input(list(string("input")));
    assertThat(executionHash(task, input, Hash.of(13)))
        .isNotEqualTo(executionHash(task2, input, Hash.of(13)));
  }

  @Test
  public void hashes_of_executions_with_same_computation_and_input_but_different_runtime_are_not_equal() {
    Computation computation = computation(Hash.of(1));
    Task task = task(computation, list());
    Input input = input(list(string("input")));
    assertThat(executionHash(task, input, Hash.of(13)))
        .isNotEqualTo(executionHash(task, input, Hash.of(14)));
  }

  @Test
  public void hashes_of_executions_with_same_computation_runtime_but_different_input_are_not_equal() {
    Computation computation = computation(Hash.of(1));
    Task task = task(computation, list());
    Input input = input(list(string("input")));
    Input input2 = input(list(string("input2")));
    assertThat(executionHash(task, input, Hash.of(13)))
        .isNotEqualTo(executionHash(task, input2, Hash.of(13)));
  }

  @Test
  public void hash_of_execution_with_empty_string_value_computation_is_stable() {
    Task task = task(new ValueComputation(string("")), list());
    Input input = input(list());
    assertThat(executionHash(task, input, Hash.of(13)))
        .isEqualTo(Hash.decode("fb03996d8c7b95104ec51115c5c275fe91f0a9ee"));
  }

  @Test
  public void hash_of_execution_with_string_value_computation_is_stable() {
    Task task = task(new ValueComputation(string("value")), list());
    Input input = input(list());
    assertThat(executionHash(task, input, Hash.of(13)))
        .isEqualTo(Hash.decode("db9ee08a5acecc6c6bdbecee8154489d6d5ef089"));
  }

  @Test
  public void hash_of_execution_with_array_computation_and_empty_input_is_stable() {
    Task task = task(new ArrayLiteralComputation(arrayType(stringType())), list());
    Input input = input(list());
    assertThat(executionHash(task, input, Hash.of(13)))
        .isEqualTo(Hash.decode("d20343333435effc353d96a8704cd929f7c39498"));
  }

  @Test
  public void hash_of_execution_with_array_computation_and_non_empty_input_is_stable() {
    Task task = task(new ArrayLiteralComputation(arrayType(stringType())), list());
    Input input = input(list(string("abc"), string("def")));
    assertThat(executionHash(task, input, Hash.of(13)))
        .isEqualTo(Hash.decode("ed225677d4183c156bde26a8a4b5f6184e53b2d1"));
  }

  @Test
  public void hash_of_execution_with_native_call_computation_and_empty_input_is_stable() {
    Task task = task(new NativeCallComputation(stringType(), mockNativeFunction()), list());
    Input input = input(list());
    assertThat(executionHash(task, input, Hash.of(13)))
        .isEqualTo(Hash.decode("36cf27551327ed05f5c0122dcced61e86ce01e84"));
  }

  @Test
  public void hash_of_execution_with_native_call_computation_and_non_empty_input_is_stable() {
    Task task = task(new NativeCallComputation(stringType(), mockNativeFunction()), list());
    Input input = input(list(string("abc"), string("def")));
    assertThat(executionHash(task, input, Hash.of(13)))
        .isEqualTo(Hash.decode("e3b8532fb64253f571926e8e3cf459a57be34aa0"));
  }

  @Test
  public void hash_of_execution_with_identity_computation_and_one_element_input_is_stable() {
    Task task = task(new IdentityComputation("name", stringType()), list());
    Input input = input(list(string("abc")));
    assertThat(executionHash(task, input, Hash.of(13)))
        .isEqualTo(Hash.decode("68f494d78c566e029fa288f0aa36b33a2f383ba7"));
  }

  @Test
  public void hash_of_execution_with_convert_from_nothing_computation_and_one_element_input_is_stable() {
    Task task = task(new ConvertComputation(stringType()), list());
    Input input = input(list(string("abc")));
    assertThat(executionHash(task, input, Hash.of(13)))
        .isEqualTo(Hash.decode("2e18856c213531ddc9f883907e90eda84c8e3e20"));
  }

  @Test
  public void hash_of_execution_with_constructor_call_computation_and_empty_input_is_stable() {
    Task task = task(new ConstructorCallComputation(constructor()), list());
    Input input = input(list());
    assertThat(executionHash(task, input, Hash.of(13)))
        .isEqualTo(Hash.decode("e14ca21fccb6631fd52809c4c4b409f2f66a077c"));
  }

  @Test
  public void hash_of_execution_with_constructor_call_computation_and_one_element_input_is_stable() {
    Task task = task(new ConstructorCallComputation(constructor()), list());
    Input input = input(list(string("abc")));
    assertThat(executionHash(task, input, Hash.of(13)))
        .isEqualTo(Hash.decode("68acb231ddf9eb8d878d1a68897aba09888b3b8e"));
  }

  @Test
  public void hash_of_execution_with_constructor_call_computation_and_two_elements_input_is_stable() {
    Task task = task(new ConstructorCallComputation(constructor()), list());
    Input input = input(list(string("abc"), string("def")));
    assertThat(executionHash(task, input, Hash.of(13)))
        .isEqualTo(Hash.decode("94296c0f10b807bb7f46c7196035c5e787dc03b2"));
  }

  private Constructor constructor() {
    return new Constructor(
        new Signature(personType(), "ConstructorName", list()), unknownLocation());
  }

  @Test
  public void hash_of_execution_with_accessor_call_computation_and_one_element_input_is_stable() {
    Task task = task(new AccessorCallComputation(accessor()), list());
    Input input = input(list(string("abc")));
    assertThat(executionHash(task, input, Hash.of(13)))
        .isEqualTo(Hash.decode("0d0a4fd630fd9ffe0bfe5820a8b38c65b07dcbdc"));
  }

  private Accessor accessor() {
    return new Accessor(
        new Signature(stringType(), "accessor", list()), "fieldName", unknownLocation());
  }

  private static Computation computation(Hash hash) {
    return new Computation() {
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
      public Output execute(Input input, NativeApi nativeApi) {
        return null;
      }
    };
  }

  private static Task task(Computation computation, List<? extends Task> dependencies) {
    return new Task(computation, dependencies, unknownLocation(), true);
  }

  private static NativeFunction mockNativeFunction() {
    NativeFunction nativeFunction = mock(NativeFunction.class);
    when(nativeFunction.hash()).thenReturn(Hash.of(33));
    when(nativeFunction.name()).thenReturn("name");
    return nativeFunction;
  }
}
