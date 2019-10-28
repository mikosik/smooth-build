package org.smoothbuild.task.base;

import static org.hamcrest.Matchers.not;
import static org.smoothbuild.task.base.Evaluator.arrayEvaluator;
import static org.smoothbuild.task.base.Evaluator.convertEvaluator;
import static org.smoothbuild.task.base.Evaluator.identityEvaluator;
import static org.smoothbuild.task.base.Evaluator.nativeCallEvaluator;
import static org.smoothbuild.task.base.Evaluator.valueEvaluator;
import static org.smoothbuild.util.Lists.list;
import static org.testory.Testory.given;
import static org.testory.Testory.mock;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.when;
import static org.testory.Testory.willReturn;

import java.nio.file.Paths;

import org.junit.Test;
import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.lang.base.DefinedFunction;
import org.smoothbuild.lang.base.Location;
import org.smoothbuild.lang.base.NativeFunction;
import org.smoothbuild.testing.TestingContext;

public class TaskHashTest extends TestingContext {
  private final Location location = Location.location(Paths.get("script.smooth"), 2);
  private Evaluator evaluator;
  private Evaluator evaluator2;
  private Task task;
  private Task task2;
  private Input input;
  private Input input2;
  private NativeFunction nativeFunction;
  private DefinedFunction definedFunction;

  @Test
  public void hashes_of_tasks_with_same_evaluator_runtime_and_input_are_equal() throws Exception {
    given(evaluator = valueEvaluator(string("work"), location));
    given(task = new Task(evaluator, list(), Hash.of(1)));
    given(task2 = new Task(evaluator, list(), Hash.of(1)));
    given(input = Input.fromObjects(list(string("input"))));
    when(task).hash(input);
    thenReturned(task2.hash(input));
  }

  @Test
  public void hashes_of_tasks_with_different_evaluator_but_same_runtime_and_input_are_not_equal()
      throws Exception {
    given(evaluator = valueEvaluator(string("string1"), location));
    given(evaluator2 = valueEvaluator(string("string2"), location));
    given(task = new Task(evaluator, list(), Hash.of(1)));
    given(task2 = new Task(evaluator2, list(), Hash.of(1)));
    given(input = Input.fromObjects(list(string("input"))));
    when(task).hash(input);
    thenReturned(not(task2.hash(input)));
  }

  @Test
  public void hashes_of_tasks_with_same_evaluator_and_input_but_different_runtime_are_not_equal()
      throws Exception {
    given(evaluator = valueEvaluator(string("string1"), location));
    given(task = new Task(evaluator, list(), Hash.of(1)));
    given(task2 = new Task(evaluator, list(), Hash.of(2)));
    given(input = Input.fromObjects(list(string("input"))));
    when(task).hash(input);
    thenReturned(not(task2.hash(input)));
  }

  @Test
  public void hashes_of_tasks_with_same_evaluator_runtime_but_different_input_are_equal() throws Exception {
    given(evaluator = valueEvaluator(string("work"), location));
    given(task = new Task(evaluator, list(), Hash.of(1)));
    given(task2 = new Task(evaluator, list(), Hash.of(1)));
    given(input = Input.fromObjects(list(string("input"))));
    given(input2 = Input.fromObjects(list(string("input2"))));
    when(task).hash(input);
    thenReturned(not(task2.hash(input2)));
  }

  @Test
  public void hash_of_task_with_empty_string_value_evaluator_is_stable() throws Exception {
    given(task = new Task(valueEvaluator(string(""), location), list(), Hash.of(13)));
    given(input = Input.fromObjects(list()));
    when(() -> task.hash(input));
    thenReturned(Hash.decodeHex("fb03996d8c7b95104ec51115c5c275fe91f0a9ee"));
  }

  @Test
  public void hash_of_task_with_string_value_evaluator_is_stable() throws Exception {
    given(task = new Task(valueEvaluator(string("value"), location), list(),
        Hash.of(13)));
    given(input = Input.fromObjects(list()));
    when(() -> task.hash(input));
    thenReturned(Hash.decodeHex("db9ee08a5acecc6c6bdbecee8154489d6d5ef089"));
  }

  @Test
  public void hash_of_task_with_array_evaluator_and_empty_input_is_stable() throws Exception {
    given(task = new Task(arrayEvaluator(
        arrayType(stringType()), list(), location), list(), Hash.of(13)));
    given(input = Input.fromObjects(list()));
    when(() -> task.hash(input));
    thenReturned(Hash.decodeHex("d20343333435effc353d96a8704cd929f7c39498"));
  }

  @Test
  public void hash_of_task_with_array_evaluator_and_non_empty_input_is_stable() throws Exception {
    given(task = new Task(arrayEvaluator(
        arrayType(stringType()), list(), location), list(), Hash.of(13)));
    given(input = Input.fromObjects(list(string("abc"), string("def"))));
    when(() -> task.hash(input));
    thenReturned(Hash.decodeHex("ed225677d4183c156bde26a8a4b5f6184e53b2d1"));
  }

  @Test
  public void hash_of_task_with_native_call_evaluator_and_empty_input_is_stable() throws Exception {
    given(nativeFunction = mock(NativeFunction.class));
    given(willReturn(Hash.of(33)), nativeFunction).hash();
    given(willReturn("name"), nativeFunction).name();
    given(task = new Task(nativeCallEvaluator(
        stringType(), nativeFunction, list(), location), list(), Hash.of(13)));
    given(input = Input.fromObjects(list()));
    when(() -> task.hash(input));
    thenReturned(Hash.decodeHex("36cf27551327ed05f5c0122dcced61e86ce01e84"));
  }

  @Test
  public void hash_of_task_with_native_call_evaluator_and_non_empty_input_is_stable()
      throws Exception {
    given(nativeFunction = mock(NativeFunction.class));
    given(willReturn(Hash.of(33)), nativeFunction).hash();
    given(willReturn("name"), nativeFunction).name();
    given(task = new Task(nativeCallEvaluator(
        stringType(), nativeFunction, list(), location), list(), Hash.of(13)));
    given(input = Input.fromObjects(list(string("abc"), string("def"))));
    when(() -> task.hash(input));
    thenReturned(Hash.decodeHex("e3b8532fb64253f571926e8e3cf459a57be34aa0"));
  }

  @Test
  public void hash_of_task_with_call_evaluator_and_one_element_input_is_stable() throws Exception {
    given(definedFunction = mock(DefinedFunction.class));
    given(willReturn(stringType()), definedFunction).type();
    given(willReturn("name"), definedFunction).name();
    given(task = new Task(identityEvaluator(stringType(), definedFunction.name(), false,
        mock(Evaluator.class), location), list(), Hash.of(13)));
    given(input = Input.fromObjects(list(string("abc"))));
    when(() -> task.hash(input));
    thenReturned(Hash.decodeHex("68f494d78c566e029fa288f0aa36b33a2f383ba7"));
  }

  @Test
  public void hash_of_task_with_convert_from_nothing_evaluator_and_empty_input_is_stable()
      throws Exception {
    given(task = new Task(convertEvaluator(stringType(), list(), location), list(),
        Hash.of(13)));
    given(input = Input.fromObjects(list()));
    when(() -> task.hash(input));
    thenReturned(Hash.decodeHex("03e12569eb415cff0fd2084ba233b6f9fdb63945"));
  }

  @Test
  public void hash_of_task_with_convert_from_nothing_evaluator_and_one_element_input_is_stable()
      throws Exception {
    given(task = new Task(convertEvaluator(stringType(), list(), location), list(),
        Hash.of(13)));
    given(input = Input.fromObjects(list(string("abc"))));
    when(() -> task.hash(input));
    thenReturned(Hash.decodeHex("2e18856c213531ddc9f883907e90eda84c8e3e20"));
  }
}
