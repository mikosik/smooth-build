package org.smoothbuild.task.base;

import static org.smoothbuild.task.base.Evaluator.arrayEvaluator;
import static org.smoothbuild.task.base.Evaluator.callEvaluator;
import static org.smoothbuild.task.base.Evaluator.convertEvaluator;
import static org.smoothbuild.task.base.Evaluator.nativeCallEvaluator;
import static org.smoothbuild.task.base.Evaluator.valueEvaluator;
import static org.smoothbuild.task.exec.TaskExecutor.taskHash;
import static org.smoothbuild.util.Lists.list;
import static org.testory.Testory.given;
import static org.testory.Testory.mock;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.when;
import static org.testory.Testory.willReturn;

import java.nio.file.Paths;

import org.junit.Before;
import org.junit.Test;
import org.smoothbuild.db.hashed.HashedDb;
import org.smoothbuild.db.hashed.TestingHashedDb;
import org.smoothbuild.db.values.ValuesDb;
import org.smoothbuild.lang.function.DefinedFunction;
import org.smoothbuild.lang.function.NativeFunction;
import org.smoothbuild.lang.message.Location;
import org.smoothbuild.lang.type.TypesDb;

import com.google.common.hash.HashCode;

public class TaskHashTest {
  private final Location location = Location.location(Paths.get("script.smooth"), 2);
  private TypesDb typesDb;
  private ValuesDb valuesDb;
  private Evaluator evaluator;
  private Task task;
  private Task task2;
  private Input input;
  private NativeFunction nativeFunction;
  private DefinedFunction definedFunction;

  @Before
  public void before() {
    HashedDb hashedDb = new TestingHashedDb();
    typesDb = new TypesDb(hashedDb);
    valuesDb = new ValuesDb(hashedDb, typesDb);
  }

  @Test
  public void hashes_of_tasks_with_same_evaluator_are_equal() throws Exception {
    given(evaluator = valueEvaluator(valuesDb.string("work"), location));
    given(task = new Task(evaluator));
    given(task2 = new Task(evaluator));
    when(task).hash();
    thenReturned(task2.hash());
  }

  @Test
  public void hash_of_task_with_empty_string_value_evaluator_is_stable() throws Exception {
    given(task = new Task(valueEvaluator(valuesDb.string(""), location)));
    given(input = Input.fromValues(list()));
    when(() -> taskHash(task, input));
    thenReturned(HashCode.fromString("1953ba8a385d0a5ad9196d7e8847ac591200d8cd"));
  }

  @Test
  public void hash_of_task_with_string_value_evaluator_is_stable() throws Exception {
    given(task = new Task(valueEvaluator(valuesDb.string("value"), location)));
    given(input = Input.fromValues(list()));
    when(() -> taskHash(task, input));
    thenReturned(HashCode.fromString("61800426bf62c17e363be934d09e6d8b8171a153"));
  }

  @Test
  public void hash_of_task_with_array_evaluator_and_empty_input_is_stable() throws Exception {
    given(task = new Task(arrayEvaluator(typesDb.array(typesDb.string()), location)));
    given(input = Input.fromValues(list()));
    when(() -> taskHash(task, input));
    thenReturned(HashCode.fromString("3e790fa50b6a9b6a4c0c4ac933de2461b321a3f0"));
  }

  @Test
  public void hash_of_task_with_array_evaluator_and_non_empty_input_is_stable() throws Exception {
    given(task = new Task(arrayEvaluator(typesDb.array(typesDb.string()), location)));
    given(input = Input.fromValues(list(valuesDb.string("abc"), valuesDb.string("def"))));
    when(() -> taskHash(task, input));
    thenReturned(HashCode.fromString("fb8f11ee885679573b7d199247665fd83adcc4b0"));
  }

  @Test
  public void hash_of_task_with_native_call_evaluator_and_empty_input_is_stable() throws Exception {
    given(nativeFunction = mock(NativeFunction.class));
    given(willReturn(HashCode.fromInt(33)), nativeFunction).hash();
    given(willReturn("name"), nativeFunction).name();
    given(task = new Task(nativeCallEvaluator(nativeFunction, location)));
    given(input = Input.fromValues(list()));
    when(() -> taskHash(task, input));
    thenReturned(HashCode.fromString("d356fa190ba44e1805f5a67ce86352f3fa2834e1"));
  }

  @Test
  public void hash_of_task_with_native_call_evaluator_and_non_empty_input_is_stable()
      throws Exception {
    given(nativeFunction = mock(NativeFunction.class));
    given(willReturn(HashCode.fromInt(33)), nativeFunction).hash();
    given(willReturn("name"), nativeFunction).name();
    given(task = new Task(nativeCallEvaluator(nativeFunction, location)));
    given(input = Input.fromValues(list(valuesDb.string("abc"), valuesDb.string("def"))));
    when(() -> taskHash(task, input));
    thenReturned(HashCode.fromString("004f47baf3927badbe890ac4683267bb7efcda9a"));
  }

  @Test
  public void hash_of_task_with_call_evaluator_and_one_element_input_is_stable() throws Exception {
    given(definedFunction = mock(DefinedFunction.class));
    given(willReturn(typesDb.string()), definedFunction).type();
    given(willReturn("name"), definedFunction).name();
    given(task = new Task(callEvaluator(definedFunction, location)));
    given(input = Input.fromValues(list(valuesDb.string("abc"))));
    when(() -> taskHash(task, input));
    thenReturned(HashCode.fromString("a7ab89c804b206bb6162ec2f06dea846befd5d5e"));
  }

  @Test
  public void hash_of_task_with_convert_from_nothing_evaluator_and_empty_input_is_stable()
      throws Exception {
    given(task = new Task(convertEvaluator(typesDb.string(), location)));
    given(input = Input.fromValues(list()));
    when(() -> taskHash(task, input));
    thenReturned(HashCode.fromString("26aed9a5ff23729834755b52514701d2681ada48"));
  }

  @Test
  public void hash_of_task_with_convert_from_nothing_evaluator_and_one_element_input_is_stable()
      throws Exception {
    given(task = new Task(Evaluator.convertEvaluator(typesDb.string(), location)));
    given(input = Input.fromValues(list(valuesDb.string("abc"))));
    when(() -> taskHash(task, input));
    thenReturned(HashCode.fromString("97382aab8df3c290f2b3d69a69d208fb708ff4bd"));
  }
}
