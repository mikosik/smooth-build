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

import org.junit.Before;
import org.junit.Test;
import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.hashed.HashedDb;
import org.smoothbuild.db.hashed.TestingHashedDb;
import org.smoothbuild.db.values.ValuesDb;
import org.smoothbuild.lang.base.DefinedFunction;
import org.smoothbuild.lang.base.Location;
import org.smoothbuild.lang.base.NativeFunction;
import org.smoothbuild.lang.type.TypesDb;

import com.google.common.hash.HashCode;

public class TaskHashTest {
  private final Location location = Location.location(Paths.get("script.smooth"), 2);
  private TypesDb typesDb;
  private ValuesDb valuesDb;
  private Evaluator evaluator;
  private Evaluator evaluator2;
  private Task task;
  private Task task2;
  private Input input;
  private Input input2;
  private NativeFunction nativeFunction;
  private DefinedFunction definedFunction;

  @Before
  public void before() {
    HashedDb hashedDb = new TestingHashedDb();
    typesDb = new TypesDb(hashedDb);
    valuesDb = new ValuesDb(hashedDb, typesDb);
  }

  @Test
  public void hashes_of_tasks_with_same_evaluator_runtime_and_input_are_equal() throws Exception {
    given(evaluator = valueEvaluator(valuesDb.string("work"), location));
    given(task = new Task(evaluator, list(), Hash.integer(1)));
    given(task2 = new Task(evaluator, list(), Hash.integer(1)));
    given(input = Input.fromValues(list(valuesDb.string("input"))));
    when(task).hash(input);
    thenReturned(task2.hash(input));
  }

  @Test
  public void hashes_of_tasks_with_different_evaluator_but_same_runtime_and_input_are_not_equal()
      throws Exception {
    given(evaluator = valueEvaluator(valuesDb.string("string1"), location));
    given(evaluator2 = valueEvaluator(valuesDb.string("string2"), location));
    given(task = new Task(evaluator, list(), Hash.integer(1)));
    given(task2 = new Task(evaluator2, list(), Hash.integer(1)));
    given(input = Input.fromValues(list(valuesDb.string("input"))));
    when(task).hash(input);
    thenReturned(not(task2.hash(input)));
  }

  @Test
  public void hashes_of_tasks_with_same_evaluator_and_input_but_different_runtime_are_not_equal()
      throws Exception {
    given(evaluator = valueEvaluator(valuesDb.string("string1"), location));
    given(task = new Task(evaluator, list(), Hash.integer(1)));
    given(task2 = new Task(evaluator, list(), Hash.integer(2)));
    given(input = Input.fromValues(list(valuesDb.string("input"))));
    when(task).hash(input);
    thenReturned(not(task2.hash(input)));
  }

  @Test
  public void hashes_of_tasks_with_same_evaluator_runtime_but_different_input_are_equal() throws Exception {
    given(evaluator = valueEvaluator(valuesDb.string("work"), location));
    given(task = new Task(evaluator, list(), Hash.integer(1)));
    given(task2 = new Task(evaluator, list(), Hash.integer(1)));
    given(input = Input.fromValues(list(valuesDb.string("input"))));
    given(input2 = Input.fromValues(list(valuesDb.string("input2"))));
    when(task).hash(input);
    thenReturned(not(task2.hash(input2)));
  }

  @Test
  public void hash_of_task_with_empty_string_value_evaluator_is_stable() throws Exception {
    given(task = new Task(valueEvaluator(valuesDb.string(""), location), list(), Hash.integer(13)));
    given(input = Input.fromValues(list()));
    when(() -> task.hash(input));
    thenReturned(HashCode.fromString("e156f1beee1cac13a6972555685549ea6fc457ac"));
  }

  @Test
  public void hash_of_task_with_string_value_evaluator_is_stable() throws Exception {
    given(task = new Task(valueEvaluator(valuesDb.string("value"), location), list(),
        Hash.integer(13)));
    given(input = Input.fromValues(list()));
    when(() -> task.hash(input));
    thenReturned(HashCode.fromString("deb797da8e1abd9760b6fe18c2d731c4f1d22a5c"));
  }

  @Test
  public void hash_of_task_with_array_evaluator_and_empty_input_is_stable() throws Exception {
    given(task = new Task(arrayEvaluator(typesDb.array(typesDb.string()), list(), location),
        list(), Hash.integer(13)));
    given(input = Input.fromValues(list()));
    when(() -> task.hash(input));
    thenReturned(HashCode.fromString("30f9b574ef2918098adab1d2d9d80b62261ee5ef"));
  }

  @Test
  public void hash_of_task_with_array_evaluator_and_non_empty_input_is_stable() throws Exception {
    given(task = new Task(arrayEvaluator(typesDb.array(typesDb.string()), list(), location),
        list(), Hash.integer(13)));
    given(input = Input.fromValues(list(valuesDb.string("abc"), valuesDb.string("def"))));
    when(() -> task.hash(input));
    thenReturned(HashCode.fromString("6d021483bdf5a594255209e8c3a22c931a904b89"));
  }

  @Test
  public void hash_of_task_with_native_call_evaluator_and_empty_input_is_stable() throws Exception {
    given(nativeFunction = mock(NativeFunction.class));
    given(willReturn(HashCode.fromInt(33)), nativeFunction).hash();
    given(willReturn("name"), nativeFunction).name();
    given(task = new Task(nativeCallEvaluator(typesDb.string(), nativeFunction, list(), location),
        list(), Hash.integer(13)));
    given(input = Input.fromValues(list()));
    when(() -> task.hash(input));
    thenReturned(HashCode.fromString("99c0c9fca631d10975dcb70ac2913218b5fd96d2"));
  }

  @Test
  public void hash_of_task_with_native_call_evaluator_and_non_empty_input_is_stable()
      throws Exception {
    given(nativeFunction = mock(NativeFunction.class));
    given(willReturn(HashCode.fromInt(33)), nativeFunction).hash();
    given(willReturn("name"), nativeFunction).name();
    given(task = new Task(nativeCallEvaluator(typesDb.string(), nativeFunction, list(), location),
        list(), Hash.integer(13)));
    given(input = Input.fromValues(list(valuesDb.string("abc"), valuesDb.string("def"))));
    when(() -> task.hash(input));
    thenReturned(HashCode.fromString("614f96e51aeae41a384437d95608dbfbf062785a"));
  }

  @Test
  public void hash_of_task_with_call_evaluator_and_one_element_input_is_stable() throws Exception {
    given(definedFunction = mock(DefinedFunction.class));
    given(willReturn(typesDb.string()), definedFunction).type();
    given(willReturn("name"), definedFunction).name();
    given(task = new Task(identityEvaluator(typesDb.string(), definedFunction.name(), false,
        mock(Evaluator.class), location), list(), Hash.integer(13)));
    given(input = Input.fromValues(list(valuesDb.string("abc"))));
    when(() -> task.hash(input));
    thenReturned(HashCode.fromString("f7b1d64c7e407a7c4dab12cd7e75610f7b23a7a4"));
  }

  @Test
  public void hash_of_task_with_convert_from_nothing_evaluator_and_empty_input_is_stable()
      throws Exception {
    given(task = new Task(convertEvaluator(typesDb.string(), list(), location), list(),
        Hash.integer(13)));
    given(input = Input.fromValues(list()));
    when(() -> task.hash(input));
    thenReturned(HashCode.fromString("4a5685212f3dcf2a0db1539191ac995de9317e47"));
  }

  @Test
  public void hash_of_task_with_convert_from_nothing_evaluator_and_one_element_input_is_stable()
      throws Exception {
    given(task = new Task(convertEvaluator(typesDb.string(), list(), location), list(),
        Hash.integer(13)));
    given(input = Input.fromValues(list(valuesDb.string("abc"))));
    when(() -> task.hash(input));
    thenReturned(HashCode.fromString("c5e3f9501409070279e43b6df4591971ec0ac94b"));
  }
}
