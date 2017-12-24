package org.smoothbuild.task.base;

import static java.util.Arrays.asList;
import static org.smoothbuild.task.base.Evaluator.arrayEvaluator;
import static org.smoothbuild.task.base.Evaluator.callEvaluator;
import static org.smoothbuild.task.base.Evaluator.nativeCallEvaluator;
import static org.smoothbuild.task.base.Evaluator.valueEvaluator;
import static org.smoothbuild.task.exec.TaskExecutor.taskHash;
import static org.testory.Testory.given;
import static org.testory.Testory.mock;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.when;
import static org.testory.Testory.willReturn;

import java.nio.file.Paths;

import org.junit.Before;
import org.junit.Test;
import org.smoothbuild.db.hashed.HashedDb;
import org.smoothbuild.db.values.ValuesDb;
import org.smoothbuild.lang.function.base.Name;
import org.smoothbuild.lang.function.def.DefinedFunction;
import org.smoothbuild.lang.function.nativ.NativeFunction;
import org.smoothbuild.lang.message.Location;
import org.smoothbuild.lang.type.TypeSystem;
import org.smoothbuild.lang.type.TypesDb;

import com.google.common.hash.HashCode;

public class TaskHashTest {
  private final Location location = Location.location(Paths.get("script.smooth"), 2);
  private TypeSystem typeSystem;
  private ValuesDb valuesDb;
  private Evaluator evaluator;
  private Task task;
  private Task task2;
  private Input input;
  private NativeFunction nativeFunction;
  private DefinedFunction definedFunction;

  @Before
  public void before() {
    HashedDb hashedDb = new HashedDb();
    typeSystem = new TypeSystem(new TypesDb(hashedDb));
    valuesDb = new ValuesDb(hashedDb, typeSystem);
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
    given(input = Input.fromValues(asList()));
    when(() -> taskHash(task, input));
    thenReturned(HashCode.fromString("812921907a645ce26fa53019db69f9827621070b"));
  }

  @Test
  public void hash_of_task_with_string_value_evaluator_is_stable() throws Exception {
    given(task = new Task(valueEvaluator(valuesDb.string("value"), location)));
    given(input = Input.fromValues(asList()));
    when(() -> taskHash(task, input));
    thenReturned(HashCode.fromString("7507bd5f417d485cd255203dddebda388b45183a"));
  }

  @Test
  public void hash_of_task_with_array_evaluator_and_empty_input_is_stable() throws Exception {
    given(task = new Task(arrayEvaluator(typeSystem.array(typeSystem.string()), location)));
    given(input = Input.fromValues(asList()));
    when(() -> taskHash(task, input));
    thenReturned(HashCode.fromString("3e790fa50b6a9b6a4c0c4ac933de2461b321a3f0"));
  }

  @Test
  public void hash_of_task_with_array_evaluator_and_non_empty_input_is_stable() throws Exception {
    given(task = new Task(arrayEvaluator(typeSystem.array(typeSystem.string()), location)));
    given(input = Input.fromValues(asList(valuesDb.string("abc"), valuesDb.string("def"))));
    when(() -> taskHash(task, input));
    thenReturned(HashCode.fromString("4fc92bc42737d06c9f0864a811e510322f4096f2"));
  }

  @Test
  public void hash_of_task_with_native_call_evaluator_and_empty_input_is_stable() throws Exception {
    given(nativeFunction = mock(NativeFunction.class));
    given(willReturn(HashCode.fromInt(33)), nativeFunction).hash();
    given(willReturn(new Name("name")), nativeFunction).name();
    given(task = new Task(nativeCallEvaluator(nativeFunction, false, location)));
    given(input = Input.fromValues(asList()));
    when(() -> taskHash(task, input));
    thenReturned(HashCode.fromString("d356fa190ba44e1805f5a67ce86352f3fa2834e1"));
  }

  @Test
  public void hash_of_task_with_native_call_evaluator_and_non_empty_input_is_stable()
      throws Exception {
    given(nativeFunction = mock(NativeFunction.class));
    given(willReturn(HashCode.fromInt(33)), nativeFunction).hash();
    given(willReturn(new Name("name")), nativeFunction).name();
    given(task = new Task(nativeCallEvaluator(nativeFunction, false, location)));
    given(input = Input.fromValues(asList(valuesDb.string("abc"), valuesDb.string("def"))));
    when(() -> taskHash(task, input));
    thenReturned(HashCode.fromString("931f19ada9b33792b779f484188f2f1a2aee3a4f"));
  }

  @Test
  public void hash_of_task_with_call_evaluator_and_one_element_input_is_stable() throws Exception {
    given(definedFunction = mock(DefinedFunction.class));
    given(willReturn(typeSystem.string()), definedFunction).type();
    given(willReturn(new Name("name")), definedFunction).name();
    given(task = new Task(callEvaluator(definedFunction, location)));
    given(input = Input.fromValues(asList(valuesDb.string("abc"))));
    when(() -> taskHash(task, input));
    thenReturned(HashCode.fromString("2670248a1ce9d25fb5c92ea85c153ad28f57cb4b"));
  }
}
