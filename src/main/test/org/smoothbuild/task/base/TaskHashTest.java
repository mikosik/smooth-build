package org.smoothbuild.task.base;

import static org.hamcrest.Matchers.not;
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
  private NativeFunction nativeFunction;
  private DefinedFunction definedFunction;

  @Before
  public void before() {
    HashedDb hashedDb = new TestingHashedDb();
    typesDb = new TypesDb(hashedDb);
    valuesDb = new ValuesDb(hashedDb, typesDb);
  }

  @Test
  public void hashes_of_tasks_with_same_platform_and_evaluator_are_equal() throws Exception {
    given(evaluator = valueEvaluator(valuesDb.string("work"), location));
    given(task = new Task(evaluator, Hash.integer(1)));
    given(task2 = new Task(evaluator, Hash.integer(1)));
    when(task).hash();
    thenReturned(task2.hash());
  }

  @Test
  public void hashes_of_tasks_with_same_platform_and_different_evaluator_are_not_equal()
      throws Exception {
    given(evaluator = valueEvaluator(valuesDb.string("string1"), location));
    given(evaluator2 = valueEvaluator(valuesDb.string("string2"), location));
    given(task = new Task(evaluator, Hash.integer(1)));
    given(task2 = new Task(evaluator2, Hash.integer(1)));
    when(task).hash();
    thenReturned(not(task2.hash()));
  }

  @Test
  public void hashes_of_tasks_with_different_platform_and_same_evaluator_are_not_equal()
      throws Exception {
    given(evaluator = valueEvaluator(valuesDb.string("string1"), location));
    given(task = new Task(evaluator, Hash.integer(1)));
    given(task2 = new Task(evaluator, Hash.integer(2)));
    when(task).hash();
    thenReturned(not(task2.hash()));
  }

  @Test
  public void hash_of_task_with_empty_string_value_evaluator_is_stable() throws Exception {
    given(task = new Task(valueEvaluator(valuesDb.string(""), location), Hash.integer(13)));
    given(input = Input.fromValues(list()));
    when(() -> taskHash(task, input));
    thenReturned(HashCode.fromString("5470e25263c27f9d21f88cb5ae2d2515fda6a17f"));
  }

  @Test
  public void hash_of_task_with_string_value_evaluator_is_stable() throws Exception {
    given(task = new Task(valueEvaluator(valuesDb.string("value"), location), Hash.integer(13)));
    given(input = Input.fromValues(list()));
    when(() -> taskHash(task, input));
    thenReturned(HashCode.fromString("fecef06418a99dda5fdf2e9b93520d4ebc475ce9"));
  }

  @Test
  public void hash_of_task_with_array_evaluator_and_empty_input_is_stable() throws Exception {
    given(task = new Task(arrayEvaluator(typesDb.array(typesDb.string()), location),
        Hash.integer(13)));
    given(input = Input.fromValues(list()));
    when(() -> taskHash(task, input));
    thenReturned(HashCode.fromString("3d8e37feba638903b0ef1a8af65be85bf059ca9a"));
  }

  @Test
  public void hash_of_task_with_array_evaluator_and_non_empty_input_is_stable() throws Exception {
    given(task = new Task(arrayEvaluator(typesDb.array(typesDb.string()), location),
        Hash.integer(13)));
    given(input = Input.fromValues(list(valuesDb.string("abc"), valuesDb.string("def"))));
    when(() -> taskHash(task, input));
    thenReturned(HashCode.fromString("5547b7f4e9730411a9871a996f2409dfd44e05e6"));
  }

  @Test
  public void hash_of_task_with_native_call_evaluator_and_empty_input_is_stable() throws Exception {
    given(nativeFunction = mock(NativeFunction.class));
    given(willReturn(HashCode.fromInt(33)), nativeFunction).hash();
    given(willReturn("name"), nativeFunction).name();
    given(task = new Task(nativeCallEvaluator(nativeFunction, location), Hash.integer(13)));
    given(input = Input.fromValues(list()));
    when(() -> taskHash(task, input));
    thenReturned(HashCode.fromString("985856d168365ecd5bd5b78d213503c386049cb2"));
  }

  @Test
  public void hash_of_task_with_native_call_evaluator_and_non_empty_input_is_stable()
      throws Exception {
    given(nativeFunction = mock(NativeFunction.class));
    given(willReturn(HashCode.fromInt(33)), nativeFunction).hash();
    given(willReturn("name"), nativeFunction).name();
    given(task = new Task(nativeCallEvaluator(nativeFunction, location), Hash.integer(13)));
    given(input = Input.fromValues(list(valuesDb.string("abc"), valuesDb.string("def"))));
    when(() -> taskHash(task, input));
    thenReturned(HashCode.fromString("0b2528d145bb5e642439dbe13076ca9a8f4ffc58"));
  }

  @Test
  public void hash_of_task_with_call_evaluator_and_one_element_input_is_stable() throws Exception {
    given(definedFunction = mock(DefinedFunction.class));
    given(willReturn(typesDb.string()), definedFunction).type();
    given(willReturn("name"), definedFunction).name();
    given(task = new Task(callEvaluator(definedFunction, location), Hash.integer(13)));
    given(input = Input.fromValues(list(valuesDb.string("abc"))));
    when(() -> taskHash(task, input));
    thenReturned(HashCode.fromString("e7900f15ecd28f0a8088dfabd5de196d197d7939"));
  }

  @Test
  public void hash_of_task_with_convert_from_generic_evaluator_and_empty_input_is_stable()
      throws Exception {
    given(task = new Task(convertEvaluator(typesDb.string(), location), Hash.integer(13)));
    given(input = Input.fromValues(list()));
    when(() -> taskHash(task, input));
    thenReturned(HashCode.fromString("5d84b753f4490fd5ccdf8b95a0f8ef19f0626770"));
  }

  @Test
  public void hash_of_task_with_convert_from_generic_evaluator_and_one_element_input_is_stable()
      throws Exception {
    given(task = new Task(Evaluator.convertEvaluator(typesDb.string(), location),
        Hash.integer(13)));
    given(input = Input.fromValues(list(valuesDb.string("abc"))));
    when(() -> taskHash(task, input));
    thenReturned(HashCode.fromString("25d5a01ead6df86c7e4a7ef1521998eac09a9ad2"));
  }
}
