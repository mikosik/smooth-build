package org.smoothbuild.task.base;

import static org.hamcrest.Matchers.not;
import static org.smoothbuild.lang.base.Location.unknownLocation;
import static org.smoothbuild.util.Lists.list;
import static org.testory.Testory.given;
import static org.testory.Testory.mock;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.when;
import static org.testory.Testory.willReturn;

import java.util.List;

import org.junit.Test;
import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.lang.base.Accessor;
import org.smoothbuild.lang.base.Constructor;
import org.smoothbuild.lang.base.NativeFunction;
import org.smoothbuild.lang.base.Signature;
import org.smoothbuild.lang.object.type.ConcreteType;
import org.smoothbuild.task.exec.Container;
import org.smoothbuild.task.exec.TaskExecutor;
import org.smoothbuild.testing.TestingContext;

public class TaskHashTest extends TestingContext {
  private Computation computation;
  private Computation computation2;
  private Task task;
  private Task task2;
  private Input input;
  private Input input2;
  private NativeFunction nativeFunction;

  @Test
  public void hashes_of_tasks_with_same_computation_runtime_and_input_are_equal() {
    given(computation = computation(Hash.of(1)));
    given(task = task(computation, list()));
    given(task2 = task(computation, list()));
    given(input = Input.fromObjects(list(string("input"))));
    when(() -> TaskExecutor.taskHash(task, input, Hash.of(13)));
    thenReturned(TaskExecutor.taskHash(task, input, Hash.of(13)));
  }

  @Test
  public void hashes_of_tasks_with_different_computation_but_same_runtime_and_input_are_not_equal() {
    given(computation = computation(Hash.of(1)));
    given(computation2 = computation(Hash.of(2)));
    given(task = task(computation, list()));
    given(task2 = task(computation2, list()));
    given(input = Input.fromObjects(list(string("input"))));
    when(() -> TaskExecutor.taskHash(task, input, Hash.of(13)));
    thenReturned(not(TaskExecutor.taskHash(task2, input, Hash.of(13))));
  }

  @Test
  public void hashes_of_tasks_with_same_computation_and_input_but_different_runtime_are_not_equal() {
    given(computation = computation(Hash.of(1)));
    given(task = task(computation, list()));
    given(input = Input.fromObjects(list(string("input"))));
    when(() -> TaskExecutor.taskHash(task, input, Hash.of(13)));
    thenReturned(not(TaskExecutor.taskHash(task, input, Hash.of(14))));
  }

  @Test
  public void hashes_of_tasks_with_same_computation_runtime_but_different_input_are_not_equal() {
    given(computation = computation(Hash.of(1)));
    given(task = task(computation, list()));
    given(input = Input.fromObjects(list(string("input"))));
    given(input2 = Input.fromObjects(list(string("input2"))));
    when(() -> TaskExecutor.taskHash(task, input, Hash.of(13)));
    thenReturned(not(TaskExecutor.taskHash(task, input2, Hash.of(13))));
  }

  @Test
  public void hash_of_task_with_empty_string_value_computation_is_stable() {
    given(task = task(new ValueComputation(string("")), list()));
    given(input = Input.fromObjects(list()));
    when(() -> TaskExecutor.taskHash(task, input, Hash.of(13)));
    thenReturned(Hash.decode("fb03996d8c7b95104ec51115c5c275fe91f0a9ee"));
  }

  @Test
  public void hash_of_task_with_string_value_computation_is_stable() {
    given(task = task(new ValueComputation(string("value")), list()));
    given(input = Input.fromObjects(list()));
    when(() -> TaskExecutor.taskHash(task, input, Hash.of(13)));
    thenReturned(Hash.decode("db9ee08a5acecc6c6bdbecee8154489d6d5ef089"));
  }

  @Test
  public void hash_of_task_with_array_computation_and_empty_input_is_stable() {
    given(task = task(new ArrayComputation(arrayType(stringType())), list()));
    given(input = Input.fromObjects(list()));
    when(() -> TaskExecutor.taskHash(task, input, Hash.of(13)));
    thenReturned(Hash.decode("d20343333435effc353d96a8704cd929f7c39498"));
  }

  @Test
  public void hash_of_task_with_array_computation_and_non_empty_input_is_stable() {
    given(task = task(new ArrayComputation(arrayType(stringType())), list()));
    given(input = Input.fromObjects(list(string("abc"), string("def"))));
    when(() -> TaskExecutor.taskHash(task, input, Hash.of(13)));
    thenReturned(Hash.decode("ed225677d4183c156bde26a8a4b5f6184e53b2d1"));
  }

  @Test
  public void hash_of_task_with_native_call_computation_and_empty_input_is_stable() {
    given(nativeFunction = mock(NativeFunction.class));
    given(willReturn(Hash.of(33)), nativeFunction).hash();
    given(willReturn("name"), nativeFunction).name();
    given(task = task(new NativeCallComputation(stringType(), nativeFunction), list()));
    given(input = Input.fromObjects(list()));
    when(() -> TaskExecutor.taskHash(task, input, Hash.of(13)));
    thenReturned(Hash.decode("36cf27551327ed05f5c0122dcced61e86ce01e84"));
  }

  @Test
  public void hash_of_task_with_native_call_computation_and_non_empty_input_is_stable() {
    given(nativeFunction = mock(NativeFunction.class));
    given(willReturn(Hash.of(33)), nativeFunction).hash();
    given(willReturn("name"), nativeFunction).name();
    given(task = task(new NativeCallComputation(stringType(), nativeFunction), list()));
    given(input = Input.fromObjects(list(string("abc"), string("def"))));
    when(() -> TaskExecutor.taskHash(task, input, Hash.of(13)));
    thenReturned(Hash.decode("e3b8532fb64253f571926e8e3cf459a57be34aa0"));
  }

  @Test
  public void hash_of_task_with_identity_computation_and_one_element_input_is_stable() {
    given(task = task(new IdentityComputation(stringType()), list()));
    given(input = Input.fromObjects(list(string("abc"))));
    when(() -> TaskExecutor.taskHash(task, input, Hash.of(13)));
    thenReturned(Hash.decode("68f494d78c566e029fa288f0aa36b33a2f383ba7"));
  }

  @Test
  public void hash_of_task_with_convert_from_nothing_computation_and_one_element_input_is_stable() {
    given(task = task(new ConvertComputation(stringType()), list()));
    given(input = Input.fromObjects(list(string("abc"))));
    when(() -> TaskExecutor.taskHash(task, input, Hash.of(13)));
    thenReturned(Hash.decode("2e18856c213531ddc9f883907e90eda84c8e3e20"));
  }

  @Test
  public void hash_of_task_with_constructor_call_computation_and_empty_input_is_stable() {
    given(task = task(new ConstructorCallComputation(constructor()), list()));
    given(input = Input.fromObjects(list()));
    when(() -> TaskExecutor.taskHash(task, input, Hash.of(13)));
    thenReturned(Hash.decode("e14ca21fccb6631fd52809c4c4b409f2f66a077c"));
  }

  @Test
  public void hash_of_task_with_constructor_call_computation_and_one_element_input_is_stable() {
    given(task = task(new ConstructorCallComputation(constructor()), list()));
    given(input = Input.fromObjects(list(string("abc"))));
    when(() -> TaskExecutor.taskHash(task, input, Hash.of(13)));
    thenReturned(Hash.decode("68acb231ddf9eb8d878d1a68897aba09888b3b8e"));
  }

  @Test
  public void hash_of_task_with_constructor_call_computation_and_two_elements_input_is_stable() {
    given(task = task(new ConstructorCallComputation(constructor()), list()));
    given(input = Input.fromObjects(list(string("abc"), string("def"))));
    when(() -> TaskExecutor.taskHash(task, input, Hash.of(13)));
    thenReturned(Hash.decode("94296c0f10b807bb7f46c7196035c5e787dc03b2"));
  }

  private Constructor constructor() {
    return new Constructor(
        new Signature(personType(), "ConstructorName", list()), unknownLocation());
  }

  @Test
  public void hash_of_task_with_accessor_call_computation_and_one_element_input_is_stable() {
    given(task = task(new AccessorCallComputation(accessor()), list()));
    given(input = Input.fromObjects(list(string("abc"))));
    when(() -> TaskExecutor.taskHash(task, input, Hash.of(13)));
    thenReturned(Hash.decode("0d0a4fd630fd9ffe0bfe5820a8b38c65b07dcbdc"));
  }

  private Accessor accessor() {
    return new Accessor(
        new Signature(stringType(), "accessor", list()), "fieldName", unknownLocation());
  }

  private static Computation computation(Hash hash) {
    return new Computation() {
      @Override
      public Hash hash() {
        return hash;
      }

      @Override
      public ConcreteType type() {
        return null;
      }

      @Override
      public Output execute(Input input, Container container) {
        return null;
      }
    };
  }

  private static Task task(Computation computation, List<? extends Task> dependencies) {
    return new Task(computation, "task-name", true, dependencies, unknownLocation());
  }
}
